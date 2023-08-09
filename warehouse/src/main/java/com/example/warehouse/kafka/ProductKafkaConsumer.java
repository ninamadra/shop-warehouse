package com.example.warehouse.kafka;

import com.example.sharedmodel.ProductMessage;
import com.example.warehouse.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductKafkaConsumer.class);
    private final ProductService productService;

    public ProductKafkaConsumer(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "store_control", groupId = "product_group")
    public void consumeProductMessage(ProductMessage productMessage) {
        logger.info("Received product message: {}", productMessage);
        productService.checkQuantityAndSendStatus(productMessage.getId());
    }
}
