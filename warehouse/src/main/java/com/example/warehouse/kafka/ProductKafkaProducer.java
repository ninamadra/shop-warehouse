package com.example.warehouse.kafka;

import com.example.sharedmodel.ProductMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductKafkaProducer {

    private static final String TOPIC_NAME = "store_status";
    private static final Logger logger = LoggerFactory.getLogger(ProductKafkaProducer.class);
    private final KafkaTemplate<String, ProductMessage> kafkaTemplate;

    public ProductKafkaProducer(KafkaTemplate<String, ProductMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductMessage(ProductMessage productMessage) {
        kafkaTemplate.send(TOPIC_NAME, productMessage);
        logger.info("Sent product message for product ID: {}", productMessage.getId());
    }
}
