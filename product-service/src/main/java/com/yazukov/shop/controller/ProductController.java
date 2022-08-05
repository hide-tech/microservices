package com.yazukov.shop.controller;

import com.yazukov.shop.dto.ProductDto;
import com.yazukov.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService service;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {"application/json"})
    public ResponseEntity saveProducts(@RequestParam("files") MultipartFile[] files) throws Exception {
        for (MultipartFile file: files){
            service.saveProductsCsv(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(produces = {"application/json"})
    public CompletableFuture<List<ProductDto>> getAllProducts(){
        return service.findAllProducts();
    }

    @PostMapping("/one")
    public ResponseEntity saveOrUpdateProduct(@RequestBody ProductDto productDto){
        return ResponseEntity.ok(service.saveOneProduct(productDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity getProductById(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id") Long id){
        service.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("available/{id}")
    public Boolean isAvailableProduct(@PathVariable("id") Long id,
                                             @RequestParam("qty") Long qty){
        return service.isAvailableProduct(id, qty);
    }
}
