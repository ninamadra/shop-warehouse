package com.example.shop.service;

import com.example.shop.kafka.ProductKafkaProducer;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.model.Product;
import com.example.shop.model.ProductType;
import com.example.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.openapitools.model.ProductReadDTO;
import org.openapitools.model.ProductWriteDTO;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductKafkaProducer kafkaProducer;

    private final ProductMapper productMapper = new ProductMapper(new ModelMapper());

    private ProductServiceImpl productService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productRepository, productMapper, kafkaProducer);
    }


    @Test
    @DisplayName("Create product when valid ProductWriteDTO")
    void testCreateProduct_whenValidProductWriteDTO_returnsCreated() {

        // given
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.<Product>getArgument(0));
        var productWriteDTO = getProductWriteDTO();

        // when
        var response = productService.createProduct(productWriteDTO);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProductReadDTO productReadDTO = response.getBody();
        assertNotNull(productReadDTO);
        assertEquals(productWriteDTO.getName(), productReadDTO.getName());
        assertEquals(productWriteDTO.getProductTypeDTO().name(), productReadDTO.getProductTypeDTO().name());
        assertEquals(productWriteDTO.getExpirationDate(), productReadDTO.getExpirationDate());

    }

    @Test
    @DisplayName("Get product by valid Id")
    void testGetProductById_whenValidId_returnsOk() {

        // given
        var product = getProduct();
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        // when
        var response = productService.getProductById(100L);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productMapper.toReadDTO(product), response.getBody());
    }

    @Test
    @DisplayName("Product not found when invalid Id")
    void testGetProductById_whenInvalidId_returnsNotFound() {

        // given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // when
        var response = productService.getProductById(100L);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Return empty list when no product exists")
    void testGetAllProducts_whenNone_returnsEmptyList() {

        // given
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        // when
        var response = productService.getAllProducts();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("Return list of all products")
    void testGetAllProducts_returnsListOfProducts() {

        // given
        var product = getProduct();
        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));

        // when
        var response = productService.getAllProducts();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProductReadDTO> productList = response.getBody();
        assertNotNull(productList);
        assertEquals(1, productList.size());
    }

    @Test
    @DisplayName("Delete product by valid Id")
    void testDeleteProductById_whenValidId_returnsNoContent() {

        // given
        when(productRepository.existsById(any(Long.class))).thenReturn(true);
        doNothing().when(productRepository).deleteById(any());

        // when
        var response = productService.deleteProductById(100L);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    @DisplayName("Not found when delete product by invalid Id")
    void testDeleteProductById_whenInvalidId_returnsNotFound() {

        // given
        when(productRepository.existsById(any(Long.class))).thenReturn(false);

        // when
        var response = productService.deleteProductById(100L);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Update product by Id")
    void testUpdateProduct_whenValidId_returnsOk() {

        // given
        var productWriteDTO = getProductWriteDTO();
        var product = productMapper.toEntity(productWriteDTO);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.<Product>getArgument(0));

        // when
        var response = productService.updateProduct(100L, productWriteDTO);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProductReadDTO productReadDTO = response.getBody();
        assertNotNull(productReadDTO);
        assertEquals(productWriteDTO.getName(), productReadDTO.getName());
        assertEquals(productWriteDTO.getProductTypeDTO().name(), productReadDTO.getProductTypeDTO().name());
        assertEquals(productWriteDTO.getExpirationDate(), productReadDTO.getExpirationDate());

    }

    @Test
    @DisplayName("Not found when update product by invalid Id")
    void testUpdateProduct_whenInvalidId_returnsNotFound() {

        // given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // when
        var response = productService.deleteProductById(100L);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    ProductWriteDTO getProductWriteDTO() {
        var productWriteDTO = new ProductWriteDTO();
        productWriteDTO.setName("limonka");
        productWriteDTO.setProductTypeDTO(ProductWriteDTO.ProductTypeDTOEnum.FRUITS);
        productWriteDTO.setExpirationDate(LocalDate.of(2023, 11, 22));
        return productWriteDTO;
    }

    Product getProduct() {
        var product = new Product();
        product.setId(1L);
        product.setName("cukinia");
        product.setProductType(ProductType.VEGETABLES);
        product.setExpirationDate(LocalDate.of(2050,1,17));
        return product;

    }

}