package com.yazukov.shop.service;

import com.yazukov.shop.dto.ProductDto;
import com.yazukov.shop.entity.Product;
import com.yazukov.shop.mapper.ProductMapper;
import com.yazukov.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Product saveProduct(ProductDto productDto){
        return repository.save(mapper.productDtoToProduct(productDto));
    }

    public Set<Product> getProductByOrderId(Long orderId){
        Set<Product> collect = repository.findAllByOrdersId(orderId);

        if (collect==null) throw new IllegalArgumentException(String.format("No products in order id-%d", orderId));

        return collect;
    }
}
