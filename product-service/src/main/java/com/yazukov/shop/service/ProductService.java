package com.yazukov.shop.service;

import com.yazukov.shop.dto.ProductDto;
import com.yazukov.shop.entity.Product;
import com.yazukov.shop.mapper.ProductMapper;
import com.yazukov.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper mapper;
    private final ProductRepository repository;

    @Async
    public CompletableFuture<List<ProductDto>> saveProductsCsv(MultipartFile file) throws Exception {
        return CompletableFuture.completedFuture(
                parseCSVFile(file).stream()
                .map(mapper::productDtoToProduct)
                .map(repository::save)
                .map(mapper::productToProductDto)
                .collect(Collectors.toList()));
    }

    @Async
    public CompletableFuture<List<ProductDto>> findAllProducts(){
        List<Product> products = new ArrayList<>();
        repository.findAll().forEach(products::add);
        return CompletableFuture.completedFuture(products.stream()
                .map(mapper::productToProductDto).collect(Collectors.toList()));
    }

    @CachePut(key = "#productDto.id", value = "Product")
    public ProductDto saveOneProduct(ProductDto productDto){
        return mapper.productToProductDto(repository.save(mapper.productDtoToProduct(productDto)));
    }

    @Cacheable(key = "#id", value = "Product")
    public ProductDto findById(Long id){
        return mapper.productToProductDto(repository.findById(id).orElseThrow(
                ()->new IllegalArgumentException(String.format("Product with id %d not found", id))));
    }

    @CacheEvict(key = "#id", value = "Product")
    public void deleteProduct(Long id){
        repository.deleteById(id);
    }

    public Boolean isAvailableProduct(Long id, Long qty){
        Product product = repository.findById(id).orElseThrow(
                ()->new IllegalArgumentException(String.format("Product with id %d not found", id)));
        Long productQty = product.getQty();
        if (productQty>=qty) return true;
        return false;
    }

    private List<ProductDto> parseCSVFile(MultipartFile file) throws Exception {
        final List<ProductDto> productDtos = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(";");
                    final ProductDto productDto = new ProductDto();
                    productDto.setId(Long.getLong(data[0]));
                    productDto.setName(data[1]);
                    productDto.setModel(data[2]);
                    productDto.setDescription(data[3]);
                    productDto.setPhotoUri(data[4]);
                    productDto.setPrice(new BigDecimal(data[5]));
                    productDto.setQty(Long.getLong(data[6]));
                    productDtos.add(productDto);
                }
                return productDtos;
            }
        } catch (final IOException e) {
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }
}
