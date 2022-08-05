package com.yazukov.shop.mapper;

import com.yazukov.shop.dto.ProductDto;
import com.yazukov.shop.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto productToProductDto(Product product);

    Product productDtoToProduct(ProductDto productDto);
}
