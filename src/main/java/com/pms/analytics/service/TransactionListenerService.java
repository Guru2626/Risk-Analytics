package com.pms.analytics.service;

import com.pms.analytics.dto.TransactionDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionListenerService {

    @KafkaListener(topics = "transactions-topic", groupId = "demo-group")
    public void listen(TransactionDto transaction) {
        System.out.println("Received transaction from Kafka: " + transaction);

    }
}
