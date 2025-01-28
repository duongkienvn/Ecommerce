package com.project.shopapp.controller;

import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.entity.ProductImageEntity;
import com.project.shopapp.model.dto.ProductDto;
import com.project.shopapp.model.dto.ProductImageDto;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.model.response.ProductImageResponse;
import com.project.shopapp.model.response.ProductResponse;
import com.project.shopapp.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @GetMapping("/search")
    public ResponseEntity<?> findProduct(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam Map<String, Object> productMap) {

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("name"));
        Page<ProductResponse> productResponsePage = productService.findProduct(productMap, pageRequest);
        List<ProductResponse> productResponseList = productResponsePage.getContent();
        int totalPages = productResponsePage.getTotalPages();

        return ResponseEntity.ok(PageResponse.builder()
                .data(productResponseList)
                .totalPages(totalPages)
                .build());
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        ProductEntity existingProduct = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);
        List<ProductResponse> productResponses = productPage.getContent();
        int totalPages = productPage.getTotalPages();

        return ResponseEntity.ok(PageResponse.builder()
                .data(productResponses)
                .totalPages(totalPages)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleting product successfully!");
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format.");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        Path uploads = Paths.get("uploads");
        if (!Files.exists(uploads)) {
            Files.createDirectories(uploads);
        }

        Path destination = Paths.get(uploads.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    @PostMapping(value = "/images/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @RequestBody List<MultipartFile> files) throws IOException {

        ProductEntity existingProduct = productService.getProductById(productId);
        files = files == null ? new ArrayList<>() : files;
        if (files.size() > ProductImageEntity.MAXIMUM_IMAGES_PER_PRODUCT) {
            return ResponseEntity.badRequest().body("You can only upload maximum 5 images!");
        }

        long maxSize = 10 * 1024 * 1024; // 10MB
        List<ProductImageResponse> productImageResponses = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue;
            }

            if (file.getSize() > maxSize) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("File is too large! Maximum size is 10MB");
            }

            if (!isImageFile(file)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image!");
            }

            String fileName = storeFile(file);
            ProductImageDto productImageDto = ProductImageDto
                    .builder()
                    .productId(productId)
                    .imageUrl(fileName)
                    .build();
            ProductImageResponse productImageResponse = productService.createProductImage(productImageDto);
            productImageResponses.add(productImageResponse);
        }
        return ResponseEntity.ok(productImageResponses);
    }
}
