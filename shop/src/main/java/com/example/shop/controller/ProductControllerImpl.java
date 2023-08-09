package com.example.shop.controller;

import com.example.shop.service.ProductService;
import org.openapitools.api.ProductsApi;
import org.openapitools.model.ProductReadDTO;
import org.openapitools.model.ProductWriteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ProductControllerImpl implements ProductsApi {

    private final ProductService productService;

    @Autowired
    public ProductControllerImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<ProductReadDTO> createProduct(ProductWriteDTO productWriteDTO) {
        return productService.createProduct(productWriteDTO);
    }

    @Override
    public ResponseEntity<ProductReadDTO> getProductById(Long id) {
        return productService.getProductById(id);
    }

    @Override
    public ResponseEntity<List<ProductReadDTO>> getAllProducts() {
        return productService.getAllProducts();
    }

    @Override
    public ResponseEntity<Void> deleteProductById(Long id) {
        return productService.deleteProductById(id);
    }

    @Override
    public ResponseEntity<ProductReadDTO> updateProduct(Long id, ProductWriteDTO productWriteDTO) {
        return productService.updateProduct(id, productWriteDTO);
    }
}
