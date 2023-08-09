package com.example.shop.service;

import com.example.sharedmodel.ProductMessage;
import com.example.shop.kafka.ProductKafkaProducer;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.model.Product;
import com.example.shop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.openapitools.model.ProductReadDTO;
import org.openapitools.model.ProductWriteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductKafkaProducer kafkaProducer;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, ProductKafkaProducer kafkaProducer) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public ResponseEntity<ProductReadDTO> createProduct(ProductWriteDTO productWriteDTO) {
        Product product = productMapper.toEntity(productWriteDTO);
        productRepository.save(product);
        ProductMessage productMessage = new ProductMessage();
        productMessage.setId(product.getId());
        productMessage.setQuantity(product.getQuantity());
        kafkaProducer.sendProductMessage(productMessage);
        return ResponseEntity.created(URI.create("/" + product.getId())).body(productMapper.toReadDTO(product));
    }

    @Override
    public ResponseEntity<ProductReadDTO> getProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.map(product -> ResponseEntity.ok(productMapper.toReadDTO(product))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ProductReadDTO>> getAllProducts() {
        List<ProductReadDTO> products = productRepository.findAll().stream().map(productMapper::toReadDTO).toList();
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<Void> deleteProductById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<ProductReadDTO> updateProduct(Long id, ProductWriteDTO productWriteDTO) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product productToUpdate = productMapper.toEntity(productWriteDTO);
            productToUpdate.setId(id);
            Product updatedProduct = productRepository.save(productToUpdate);
            return ResponseEntity.ok(productMapper.toReadDTO(updatedProduct));
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional
    @Override
    public void updateProductQuantity(Long productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        productOptional.ifPresent(product -> {
            product.setQuantity(quantity);
            productRepository.save(product);
        });
    }

}
