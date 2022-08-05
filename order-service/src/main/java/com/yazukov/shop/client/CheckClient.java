package com.yazukov.shop.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("product-service")
public interface CheckClient {

    @GetMapping("/api/product/available/{id}")
    Boolean isAvailableProduct(@PathVariable("id") Long id,
                               @RequestParam("qty") Long qty);
}
