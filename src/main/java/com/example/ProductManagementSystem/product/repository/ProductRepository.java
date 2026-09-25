package com.example.ProductManagementSystem.product.repository;

import com.example.ProductManagementSystem.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Unique Serial Number
    boolean existsBySerialNumber(String serialNumber);
    Optional<Product> findBySerialNumber(String serialNumber);

    // Unique Product name
    boolean existsByProductNameIgnoreCase(String productName);
    Optional<Product> findByProductNameIgnoreCase(String productName);

    // Searches a single selected field (id, name, serialNumber, brand, category)
    @Query(value = "SELECT * FROM products p WHERE " +
            ":value = '' OR " +
            "(:field = 'id' AND CAST(p.product_id AS CHAR) LIKE CONCAT('%', :value, '%')) OR " +
            "(:field = 'name' AND LOWER(p.product_name) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'serialNumber' AND LOWER(p.serial_number) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'brand' AND LOWER(p.brand) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'category' AND LOWER(p.category) LIKE LOWER(CONCAT('%', :value, '%')))",
            countQuery = "SELECT COUNT(*) FROM products p WHERE " +
                    ":value = '' OR " +
                    "(:field = 'id' AND CAST(p.product_id AS CHAR) LIKE CONCAT('%', :value, '%')) OR " +
                    "(:field = 'name' AND LOWER(p.product_name) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'serialNumber' AND LOWER(p.serial_number) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'brand' AND LOWER(p.brand) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'category' AND LOWER(p.category) LIKE LOWER(CONCAT('%', :value, '%')))",
            nativeQuery = true)
    Page<Product> searchByField(@Param("field") String field, @Param("value") String value, Pageable pageable);
}