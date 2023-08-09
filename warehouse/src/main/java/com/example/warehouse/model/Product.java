package com.example.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    private Long id;
    private int quantity;
}
