package com.example.shop.service;

import org.openapitools.model.ProductReadDTO;
import org.openapitools.model.ProductWriteDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<ProductReadDTO> createProduct(ProductWriteDTO productWriteDTO);

    ResponseEntity<ProductReadDTO> getProductById(Long id);

    ResponseEntity<List<ProductReadDTO>> getAllProducts();

    ResponseEntity<Void> deleteProductById(Long id);

    ResponseEntity<ProductReadDTO> updateProduct(Long id, ProductWriteDTO productWriteDTO);

    void updateProductQuantity(Long productId, Integer quantity);
}
