package com.pms.analytics.publisher;

import com.pms.analytics.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionPublisher {

    private final KafkaTemplate<String, TransactionDto> kafkaTemplate;
    private static final String TOPIC = "transactions-topic";

    public void sendTransaction(TransactionDto transaction) {
        kafkaTemplate.send(TOPIC, transaction);
        System.out.println("Message sent to Kafka: " + transaction);
    }
}
