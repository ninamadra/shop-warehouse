package com.example.shop.mapper;

import com.example.shop.model.Product;
import com.example.shop.model.ProductType;
import org.modelmapper.ModelMapper;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.model.ProductReadDTO;
import org.openapitools.model.ProductWriteDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Product toEntity(ProductWriteDTO generatedProduct) {
        return generatedProduct != null ? modelMapper.map(generatedProduct, Product.class) : null;
    }

    public ProductReadDTO toReadDTO(Product product) {
        ProductReadDTO productReadDTO = modelMapper.map(product, ProductReadDTO.class);
        productReadDTO.setProductTypeDTO(ProductReadDTO.ProductTypeDTOEnum.valueOf(product.getProductType().name()));
        return productReadDTO;
    }

    public ProductWriteDTO toWriteDTO(Product product) {
        return modelMapper.map(product, ProductWriteDTO.class);
    }

    public ProductType toProductType(ProductReadDTO.ProductTypeDTOEnum productTypeDTO) {
        return modelMapper.map(productTypeDTO, ProductType.class);
    }

    public ProductReadDTO.ProductTypeDTOEnum toProductTypeDTO(ProductType productType) {
        return ProductReadDTO.ProductTypeDTOEnum.valueOf(productType.name());
    }
}



