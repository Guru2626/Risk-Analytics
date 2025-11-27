package com.pms.analytics.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.analytics.dto.TransactionDto;

@Service
public class TransactionListenerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${app.kafka-topic}", groupId = "transaction-group")
    public void listen(String message) {
        System.out.println("Raw message: " + message);

        try {
            // Convert JSON to DTO
            TransactionDto dto = objectMapper.readValue(message, TransactionDto.class);

            System.out.println("Converted DTO: " + dto);

        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
        }
    }
}
