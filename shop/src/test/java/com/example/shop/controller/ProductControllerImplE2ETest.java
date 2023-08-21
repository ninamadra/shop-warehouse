package com.example.shop.controller;

import com.example.shop.model.Product;
import com.example.shop.model.ProductType;
import com.example.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.model.ProductReadDTO;
import org.openapitools.model.ProductWriteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductControllerImplE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository repository;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        repository.deleteAll();
    }

    @Test
    @DisplayName("Create product when valid input")
    void testHttpCreateProduct_whenValidInput_returnsCreated() {

        // given
        Product product = createProduct("Product");
        //doNothing().when(kafkaProducer).sendProductMessage(any());

        // when
        ResponseEntity<ProductReadDTO> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/products", product, ProductReadDTO.class);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals(ProductReadDTO.ProductTypeDTOEnum.OTHER, response.getBody().getProductTypeDTO());
        assertEquals(LocalDate.of(2002, 2, 18), response.getBody().getExpirationDate());

    }

    @Test
    @DisplayName("Get product by valid Id")
    void testHttpGetProductById_whenValidId_returnsOK() {

        // given
        Product product = createProduct("Product 1");
        product.setId(1L);
        repository.save(product);

        // when
        ResponseEntity<ProductReadDTO> response = restTemplate.getForEntity("http://localhost:" + port +"/products/1", ProductReadDTO.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProductReadDTO createdProduct = response.getBody();
        assertEquals(product.getName(), createdProduct.getName());
        assertEquals(product.getProductType().toString(), createdProduct.getProductTypeDTO().toString());
        assertEquals(product.getExpirationDate(), createdProduct.getExpirationDate());

    }

    @Test
    @DisplayName("Get product by invalid Id returns not found")
    void testHttpGetProductById_whenInvalidId_returnsNotFound() {

        try {
            // when
            restTemplate.getForEntity(
                    "http://localhost:" + port + "/products/999", ProductReadDTO.class);

            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException.NotFound ex) {
            // then
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }

    }

    @Test
    @DisplayName("Get products returns empty list when no products")
    void testHttpGetAllProducts_returnsEmptyList_whenNoProducts() {
        // when
        ResponseEntity<ProductReadDTO[]> response = restTemplate.getForEntity("http://localhost:" + port +"/products", ProductReadDTO[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);

    }


    @Test
    @DisplayName("Get products returns Ok")
    void testHttpGetAllProducts_returnsListOfProducts() {

        // given
        Product product1 = createProduct("Product 1");
        Product product2 = createProduct("Product 2");
        repository.saveAll(List.of(product1, product2));

        // when
        ResponseEntity<ProductReadDTO[]> response = restTemplate.getForEntity("http://localhost:" + port +"/products", ProductReadDTO[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
        assertEquals(product1.getName(), response.getBody()[0].getName());
        assertEquals(product1.getProductType().toString(), response.getBody()[0].getProductTypeDTO().toString());
        assertEquals(product1.getExpirationDate(), response.getBody()[0].getExpirationDate());
        assertEquals(product2.getName(), response.getBody()[1].getName());

    }

    @Test
    @DisplayName("Delete product by Id returns No content")
    void testHttpDeleteProductById_whenValidId_returnsNoContent() {

        // given
        Product product = createProduct("Product to delete");
        repository.save(product);

        //when
        restTemplate.delete("http://localhost:" + port + "/products/" + product.getId());

        // then
        try {
           restTemplate.getForEntity(
                    "http://localhost:" + port + "/products/" + product.getId(), Product.class);

            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException.NotFound ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }

    }

    @Test
    @DisplayName("Delete product by invalid Id returns not found")
    void testHttpDeleteProductById_whenInvalidId_returnsNotFound() {

        try {
            // when
            restTemplate.delete("http://localhost:" + port + "/products/999");

            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException.NotFound ex) {
            // then
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }

    }

    @Test
    @DisplayName("Update product by Id returns Ok")
    void testHttpUpdateProduct_whenValidId_returnsOk() {

        // given
        Product product = createProduct("Product to update");
        repository.save(product);

        ProductWriteDTO modifiedProduct = new ProductWriteDTO();
        modifiedProduct.setName("Updated Product");
        modifiedProduct.setExpirationDate(LocalDate.of(2023, 12, 31));
        modifiedProduct.setProductTypeDTO(ProductWriteDTO.ProductTypeDTOEnum.FRUITS);

        // when
        restTemplate.put("http://localhost:" + port + "/products/" + product.getId(), modifiedProduct);

        // then
        ResponseEntity<ProductReadDTO> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/products/" + product.getId(), ProductReadDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(modifiedProduct.getName(), response.getBody().getName());
        assertEquals(modifiedProduct.getExpirationDate(), response.getBody().getExpirationDate());
        assertEquals(modifiedProduct.getProductTypeDTO().toString(), response.getBody().getProductTypeDTO().toString());

    }

    @Test
    @DisplayName("Update product by invalid Id returns not found")
    void testHttpUpdateProduct_whenInvalidId_returnsNotFound() {

        try {
            // when
            restTemplate.put("http://localhost:" + port + "/products/" + 999, new ProductWriteDTO());

            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException.NotFound ex) {
            // then
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }

    }

    private Product createProduct(String name) {
        Product product = new Product();
        product.setName(name);
        product.setProductType(ProductType.OTHER);
        product.setExpirationDate(LocalDate.of(2002,2,18));
        return product;
    }
}
