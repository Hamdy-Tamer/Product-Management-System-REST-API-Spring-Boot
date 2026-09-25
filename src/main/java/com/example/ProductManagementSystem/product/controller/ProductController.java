package com.example.ProductManagementSystem.product.controller;

import com.example.ProductManagementSystem.product.dto.ProductPatchRequest;
import com.example.ProductManagementSystem.product.entity.Brand;
import com.example.ProductManagementSystem.product.entity.Category;
import com.example.ProductManagementSystem.product.entity.Product;
import com.example.ProductManagementSystem.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //  REST API  (JSON)   ->  /api/products
    @PostMapping("/api/products")
    @ResponseBody
    public ResponseEntity<Product> apiAdd(@Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(product));
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> apiGetById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<Product>> apiGetAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/products/search?field=&value=&page=&size= - paginated search on a single
    // selected field: id, name, serialNumber, brand, or category (image excluded).
    // Backs the AJAX-driven list page; an empty value returns everything.
    @GetMapping("/api/products/search")
    @ResponseBody
    public ResponseEntity<Page<Product>> apiSearch(
            @RequestParam(name = "field", required = false, defaultValue = "") String field,
            @RequestParam(name = "value", required = false, defaultValue = "") String value,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.searchProducts(field, value, page, size));
    }

    @DeleteMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> apiDeleteById(@PathVariable Integer id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/products")
    @ResponseBody
    public ResponseEntity<Void> apiDeleteAll() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> apiUpdate(@PathVariable Integer id,
                                             @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @PatchMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> apiPatch(@PathVariable Integer id,
                                            @RequestBody ProductPatchRequest patch) {
        return ResponseEntity.ok(productService.patchProduct(id, patch));
    }


    //  THYMELEAF UI  (HTML)  ->  /products
    //  LIST  (data itself is now loaded client-side via AJAX against /api/products/search)
    @GetMapping("/products")
    public String list() {
        return "products/list";
    }

    //  VIEW ONE
    @GetMapping("/products/{id}/view")
    public String view(@PathVariable Integer id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/view";
    }

    //  ADD FORM
    @GetMapping("/products/new")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("brands", Brand.values());
        model.addAttribute("categories", Category.values());
        model.addAttribute("mode", "add");
        return "products/form";
    }

    @PostMapping("/products")
    public String add(@Valid @ModelAttribute("product") Product product,
                      Model model,
                      RedirectAttributes ra) {
        try {
            productService.addProduct(product);
            ra.addFlashAttribute("success", "Product added successfully");
            return "redirect:/products";
        } catch (RuntimeException ex) {
            model.addAttribute("brands", Brand.values());
            model.addAttribute("categories", Category.values());
            model.addAttribute("mode", "add");
            model.addAttribute("error", ex.getMessage());
            return "products/form";
        }
    }

    //  FULL UPDATE (PUT-style) FORM
    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("brands", Brand.values());
        model.addAttribute("categories", Category.values());
        model.addAttribute("mode", "edit");
        return "products/form";
    }

    @PostMapping("/products/{id}/edit")
    public String fullUpdate(@PathVariable Integer id,
                             @Valid @ModelAttribute("product") Product product,
                             Model model,
                             RedirectAttributes ra) {
        try {
            productService.updateProduct(id, product);
            ra.addFlashAttribute("success", "Product updated successfully");
            return "redirect:/products";
        } catch (RuntimeException ex) {
            product.setProductID(id);
            model.addAttribute("brands", Brand.values());
            model.addAttribute("categories", Category.values());
            model.addAttribute("mode", "edit");
            model.addAttribute("error", ex.getMessage());
            return "products/form";
        }
    }

    //  PARTIAL UPDATE (PATCH-style)
    @PostMapping("/products/{id}/patch")
    public String patch(@PathVariable Integer id,
                        @ModelAttribute ProductPatchRequest patch,
                        RedirectAttributes ra) {
        try {
            productService.patchProduct(id, patch);
            ra.addFlashAttribute("success", "Product updated (partial)");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products";
    }

    //  DELETE
    @PostMapping("/products/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            productService.deleteProductById(id);
            ra.addFlashAttribute("success", "Product deleted");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products";
    }

    // DELETE ALL
    @PostMapping("/products/delete-all")
    public String deleteAll(RedirectAttributes ra) {
        try {
            productService.deleteAllProducts();
            ra.addFlashAttribute("success", "All products deleted");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products";
    }
}