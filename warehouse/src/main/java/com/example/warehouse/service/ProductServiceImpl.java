package com.example.warehouse.service;

import com.example.sharedmodel.ProductMessage;
import com.example.warehouse.kafka.ProductKafkaProducer;
import com.example.warehouse.model.Product;
import com.example.warehouse.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductKafkaProducer kafkaProducer;

    public ProductServiceImpl(ProductRepository productRepository, ProductKafkaProducer kafkaProducer) {
        this.productRepository = productRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void checkQuantityAndSendStatus(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        productOptional.ifPresent(product -> {
            ProductMessage productMessage = new ProductMessage();
            productMessage.setId(product.getId());
            productMessage.setQuantity(product.getQuantity());

            kafkaProducer.sendProductMessage(productMessage);
        });
    }

}
