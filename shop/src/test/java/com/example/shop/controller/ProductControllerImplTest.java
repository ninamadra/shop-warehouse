package com.example.shop.controller;

import com.example.shop.mapper.ProductMapper;
import com.example.shop.model.Product;
import com.example.shop.model.ProductType;
import com.example.shop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.openapitools.model.ProductReadDTO;
import org.openapitools.model.ProductWriteDTO;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductControllerImpl.class)
class ProductControllerImplTest {

    @MockBean
    private ProductService productService;

    private MockMvc mockMvc;

    private final ProductMapper productMapper = new ProductMapper(new ModelMapper());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductControllerImpl(productService)).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Create product when valid input")
    void testCreateProduct_whenValidProductWriteDTO_returnsCreated() throws Exception {

        // given
        var product = getProduct();
        var productWriteDTO = productMapper.toWriteDTO(product);
        var productReadDTO = productMapper.toReadDTO(product);

        when(productService.createProduct(any(ProductWriteDTO.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(productReadDTO));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productWriteDTO)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(productReadDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productTypeDTO").value(productReadDTO.getProductTypeDTO().toString()));
    }

    @Test
    @DisplayName("Get product by valid Id")
    void testGetProductById_whenValidId_returnsOK() throws Exception {

        // given
        var product = getProduct();
        var productReadDTO = productMapper.toReadDTO(product);

        when(productService.getProductById(any(Long.class))).thenReturn(ResponseEntity.ok(productReadDTO));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(productReadDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productTypeDTO").value(productReadDTO.getProductTypeDTO().toString()));
    }

    @Test
    @DisplayName("Get product by invalid Id returns not found")
    void testGetProductById_whenInvalidId_returnsNotFound() throws Exception {

        // given
        when(productService.getProductById(any(Long.class))).thenReturn(ResponseEntity.notFound().build());

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Get products returns empty list when no products")
    void testGetAllProducts_returnsEmptyList_whenNoProducts() throws Exception {

        // given
        when(productService.getAllProducts()).thenReturn(ResponseEntity.ok(new ArrayList<>()));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Get products returns Ok")
    void testGetAllProducts_returnsListOfProducts() throws Exception {

        // given
        List<ProductReadDTO> products = new ArrayList<>();
        var mockProduct = productMapper.toReadDTO(getProduct());
        products.add(mockProduct);

        when(productService.getAllProducts()).thenReturn(ResponseEntity.ok(products));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(products.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(mockProduct.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productTypeDTO").value(mockProduct.getProductTypeDTO().toString()));
    }

    @Test
    @DisplayName("Update product by Id returns No content")
    void testDeleteProductById_whenValidId_returnsNoContent() throws Exception {

        // given
        when(productService.deleteProductById(any(Long.class))).thenReturn(ResponseEntity.noContent().build());

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Delete product by invalid Id returns not found")
    void testDeleteProductById_whenInvalidId_returnsNotFound() throws Exception {

        // given
        when(productService.deleteProductById(any(Long.class))).thenReturn(ResponseEntity.notFound().build());

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Update product by Id returns Ok")
    void testUpdateProduct_whenValidId_returnsOk() throws Exception {

        // given
        var product = getProduct();
        var productWriteDTO = productMapper.toWriteDTO(product);
        var productReadDTO = productMapper.toReadDTO(product);

        when(productService.updateProduct(any(Long.class), any(ProductWriteDTO.class))).thenReturn(ResponseEntity.ok(productReadDTO));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productWriteDTO)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(productReadDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productTypeDTO").value(productReadDTO.getProductTypeDTO().toString()));

    }

    @Test
    @DisplayName("Update product by invalid Id returns not found")
    void testUpdateProduct_whenInvalidId_returnsNotFound() throws Exception {

        // given
        var productWriteDTO = productMapper.toWriteDTO(getProduct());
        when(productService.updateProduct(any(Long.class), any(ProductWriteDTO.class))).thenReturn(ResponseEntity.notFound().build());

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productWriteDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
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