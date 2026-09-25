package com.example.ProductManagementSystem.product.service;

import com.example.ProductManagementSystem.product.dto.ProductPatchRequest;
import com.example.ProductManagementSystem.product.entity.Product;
import com.example.ProductManagementSystem.product.exception.ProductAlreadyExistsException;
import com.example.ProductManagementSystem.product.exception.ProductNotFoundException;
import com.example.ProductManagementSystem.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Adding a new product
    public Product addProduct(Product product) {
        String name = product.getProductName().trim();
        String serialNumber = product.getSerialNumber().trim();

        if (productRepository.existsByProductNameIgnoreCase(name)) {
            throw new ProductAlreadyExistsException(
                    "A product named '" + name + "' already exists");
        }

        if (productRepository.existsBySerialNumber(serialNumber)) {
            throw new ProductAlreadyExistsException(
                    "A product with serial number '" + serialNumber + "' already exists");
        }

        product.setProductName(name);
        product.setSerialNumber(serialNumber);
        return productRepository.save(product);
    }

    // Deletes a single product by its ID
    public void deleteProductById(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("No product found with ID " + productId);
        }
        productRepository.deleteById(productId);
    }

    // Deletes all products in one shot.
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    // Full update (PUT)
    public Product updateProduct(Integer productId, Product updatedProduct) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "No product found with ID " + productId));

        String newName = updatedProduct.getProductName().trim();
        if (!newName.equalsIgnoreCase(existing.getProductName())
                && productRepository.existsByProductNameIgnoreCase(newName)) {
            throw new ProductAlreadyExistsException(
                    "A product named '" + newName + "' already exists");
        }

        String newSerialNumber = updatedProduct.getSerialNumber().trim();
        if (!newSerialNumber.equals(existing.getSerialNumber())
                && productRepository.existsBySerialNumber(newSerialNumber)) {
            throw new ProductAlreadyExistsException(
                    "A product with serial number '" + newSerialNumber + "' already exists");
        }

        existing.setProductName(newName);
        existing.setSerialNumber(newSerialNumber);
        existing.setBrand(updatedProduct.getBrand());
        existing.setCategory(updatedProduct.getCategory());
        existing.setQuantity(updatedProduct.getQuantity());
        existing.setPrice(updatedProduct.getPrice());
        existing.setImageUrl(updatedProduct.getImageUrl());

        return productRepository.save(existing);
    }

    // Partial update (PATCH)
    public Product patchProduct(Integer productId, ProductPatchRequest patch) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "No product found with ID " + productId));

        if (patch.getProductName() != null && !patch.getProductName().isBlank()) {
            String newName = patch.getProductName().trim();
            if (!newName.equalsIgnoreCase(existing.getProductName())
                    && productRepository.existsByProductNameIgnoreCase(newName)) {
                throw new ProductAlreadyExistsException(
                        "A product named '" + newName + "' already exists");
            }
            existing.setProductName(newName);
        }

        if (patch.getSerialNumber() != null && !patch.getSerialNumber().isBlank()) {
            String newSerialNumber = patch.getSerialNumber().trim();
            if (!newSerialNumber.equals(existing.getSerialNumber())
                    && productRepository.existsBySerialNumber(newSerialNumber)) {
                throw new ProductAlreadyExistsException(
                        "A product with serial number '" + newSerialNumber + "' already exists");
            }
            existing.setSerialNumber(newSerialNumber);
        }

        if (patch.getQuantity() != null) {
            existing.setQuantity(patch.getQuantity());
        }

        if (patch.getPrice() != null) {
            existing.setPrice(patch.getPrice());
        }

        if (patch.getImageUrl() != null && !patch.getImageUrl().isBlank()) {
            existing.setImageUrl(patch.getImageUrl());
        }

        return productRepository.save(existing);
    }

    // Convenience read helpers used by the controller layer.
    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "No product found with ID " + productId));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Searches a single selected field (id, name, serialNumber, brand, category)
    public Page<Product> searchProducts(String field, String value, int page, int size) {
        String safeField = (field == null) ? "" : field.trim();
        String safeValue = (value == null) ? "" : value.trim();
        // "product_id" (native column name), not "productID" - this method now runs
        // a native SQL query, so Sort must use the actual DB column name.
        Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by("product_id").ascending());
        return productRepository.searchByField(safeField, safeValue, pageable);
    }
}