package com.example.warehouse.kafka;

import com.example.sharedmodel.ProductMessage;
import com.example.warehouse.service.ProductService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProductKafkaConsumer {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductKafkaConsumer.class);

    public ProductKafkaConsumer(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "store_control", groupId = "product_group")
    public void consumeProductMessage(ProductMessage productMessage) {
        logger.info("Received product message: {}", productMessage);
        productService.checkQuantityAndSendStatus(productMessage.getId());
    }
}
