package com.example.shop.kafka;

import com.example.sharedmodel.ProductMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductKafkaProducer {

    private static final String TOPIC_NAME = "store_control";
    private final Logger logger = LoggerFactory.getLogger(ProductKafkaProducer.class);
    private final KafkaTemplate<String, ProductMessage> kafkaTemplate;

    public ProductKafkaProducer(KafkaTemplate<String, ProductMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductMessage(ProductMessage productMessage) {
        logger.info("Sending product message: {}", productMessage);
        kafkaTemplate.send(TOPIC_NAME, productMessage);
    }
}
