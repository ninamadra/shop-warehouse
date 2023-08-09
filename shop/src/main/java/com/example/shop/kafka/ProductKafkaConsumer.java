package com.example.shop.kafka;

import com.example.sharedmodel.ProductMessage;
import com.example.shop.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductKafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger(ProductKafkaConsumer.class);
    private final ProductService productService;

    public ProductKafkaConsumer(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "store_status", groupId = "product_group")
    public void consumeProductMessage(ProductMessage productMessage) {
        logger.info("Received product message: {}", productMessage);
        productService.updateProductQuantity(productMessage.getId(), productMessage.getQuantity());
    }
}
