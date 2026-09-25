package com.example.ProductManagementSystem.product.dto;

import com.example.ProductManagementSystem.product.entity.Brand;
import com.example.ProductManagementSystem.product.entity.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for PATCH /api/products/{id}.
 *
 * Fields are nullable wrappers so the service can tell the difference
 * between "field omitted by client" (null) and "field explicitly set to 0 / blank".
 */
public class ProductPatchRequest {

    @Size(max = 250, message = "Product name must be at most 250 characters")
    private String productName;

    @Size(max = 100, message = "Serial number must be at most 100 characters")
    private String serialNumber;

    private Brand brand;

    private Category category;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;      // wrapper — allows null

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;      // BigDecimal, matches entity

    @Size(max = 500, message = "Image URL must be at most 500 characters")
    private String imageUrl;

    public ProductPatchRequest() { }

    // ---------- getters & setters ----------

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}