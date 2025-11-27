package com.pms.analytics.service;

import com.pms.analytics.dao.PnlDao;
import com.pms.analytics.dao.PositionDao;
import com.pms.analytics.dao.entity.PnlEntity;
import com.pms.analytics.dao.entity.PositionEntity;
import com.pms.analytics.dao.entity.PositionEntity.PositionKey;
import com.pms.analytics.utilities.TradeSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pms.analytics.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionListenerService {

    private final ObjectMapper objectMapper;

    @Autowired
    private PnlDao pnlDao;

    @Autowired
    private PositionDao positionDao;

    @Autowired
    ExternalPriceClient externalPriceClient;

    public TransactionListenerService() {

        this.pnlDao = this.pnlDao;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "${app.kafka-topic}", groupId = "transaction-group")
    public void listen(@Payload(required = false) String message) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Received empty message, ignoring...");
            return;
        }

        System.out.println("Raw message: " + message);

        try {
            TransactionDto transactionDto = objectMapper.readValue(message, TransactionDto.class);
            Optional<PositionEntity> position = positionDao.findById(new PositionKey(transactionDto.getPortfolioId(),transactionDto.getSymbol()));
            Optional<PnlEntity> existing = pnlDao.findById(transactionDto.getTransactionId());
            if(existing.isPresent()){

            }else{
                PnlEntity newPnl = new PnlEntity();
                newPnl.setTransactionId(transactionDto.getTransactionId());
                newPnl.setPortfolioId(transactionDto.getPortfolioId());
                newPnl.setSymbol(transactionDto.getSymbol());
                newPnl.setTimestamp(transactionDto.getTimestamp());
                if(transactionDto.getSide().equals(TradeSide.BUY)) {
                    newPnl.setBuyPrice(transactionDto.getBuyPrice());
                    newPnl.setRemainingQuantity(transactionDto.getRemainingQuantity());
                    BigDecimal currentPrice = externalPriceClient.getCurrentPrice(transactionDto.getSymbol());
                    BigDecimal unrealizedPnl = (BigDecimal) (currentPrice.subtract(transactionDto.getBuyPrice())).multiply(BigDecimal.valueOf(transactionDto.getRemainingQuantity()));
                    newPnl.setUnrealizedPnl(unrealizedPnl);
                    if(position.isPresent()){
                        PositionEntity pos = position.get();
                        long holdings = pos.getHoldings();
                        pos.setHoldings(holdings+transactionDto.getRemainingQuantity());
                        positionDao.save(pos);
                    }else{
                        PositionEntity pos = new PositionEntity();
                        pos.setId(new PositionKey(transactionDto.getPortfolioId(),transactionDto.getSymbol()));
                        pos.setHoldings(transactionDto.getRemainingQuantity());
                        positionDao.saveAndFlush(pos);
                    }
                }else{
                    newPnl.setRemainingQuantity(transactionDto.getRemainingQuantity());
                    newPnl.setBuyPrice(null);
                    BigDecimal realizedPnl = (BigDecimal) (transactionDto.getSellPrice().subtract(transactionDto.getBuyPrice())).multiply(BigDecimal.valueOf(transactionDto.getQuantity()));
                    newPnl.setRealizedPnl(realizedPnl);
                    if(position.isPresent()){
                        PositionEntity pos = position.get();
                        long holdings = pos.getHoldings();
                        pos.setHoldings(holdings-transactionDto.getQuantity());
                        positionDao.save(pos);
                    }
                }
            }

            System.out.println("Converted DTO: " + transactionDto);



        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
        }
    }
}
