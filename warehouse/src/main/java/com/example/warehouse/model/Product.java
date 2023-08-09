package com.example.warehouse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    private Long id;
    private int quantity;
}
