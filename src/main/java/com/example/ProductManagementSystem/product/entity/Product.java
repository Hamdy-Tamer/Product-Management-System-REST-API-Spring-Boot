package com.example.ProductManagementSystem.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_id",    columnNames = "product_id"),
                @UniqueConstraint(name = "uk_serial_number", columnNames = "serial_number"),
                @UniqueConstraint(name = "uk_product_name",  columnNames = "product_name")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false, unique = true, updatable = false)
    private Integer productID;

    @NotBlank(message = "Serial number is required")
    @Size(max = 100, message = "Serial number must be at most 100 characters")
    @Column(name = "serial_number", nullable = false, unique = true, length = 100)
    private String serialNumber;

    @NotBlank(message = "Product name is required")
    @Size(max = 250)
    @Column(name = "product_name", nullable = false, unique = true, length = 250)
    private String productName;

    @NotNull(message = "Brand is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "brand", nullable = false, length = 50)
    private Brand brand;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private Category category;

    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 500, message = "Image URL must be at most 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public Product() {
    }

    public Product(String serialNumber, String productName, Brand brand, Category category,
                   int quantity, BigDecimal price, String imageUrl) {
        this.serialNumber = serialNumber;
        this.productName = productName;
        this.brand = brand;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}