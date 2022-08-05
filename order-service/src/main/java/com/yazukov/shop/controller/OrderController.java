package com.yazukov.shop.controller;

import com.yazukov.shop.dto.OrderDto;
import com.yazukov.shop.dto.ProductDto;
import com.yazukov.shop.exception.OrderNotFoundException;
import com.yazukov.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService service;

    @PostMapping("/create")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(orderDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(service.removeOrder(orderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Set<OrderDto>> getOrdersByUserId(@PathVariable("id") Long userId){
        return ResponseEntity.ok(service.getOrdersByUserId(userId));
    }

    @PostMapping("/add-product/{id}")
    public ResponseEntity<OrderDto> addProduct(@PathVariable("id") Long orderId,
                                               @RequestBody ProductDto productDto)
            throws Exception {
        return ResponseEntity.ok(service.addProductIntoOrder(orderId, productDto));
    }

    @PostMapping("/remove-product/{id}")
    public ResponseEntity<OrderDto> removeProduct(@PathVariable("id") Long orderId,
                                               @RequestBody ProductDto productDto) throws OrderNotFoundException {
        return ResponseEntity.ok(service.removeProductFromOrder(orderId, productDto.getId()));
    }

    @PostMapping("/success/{id}")
    public ResponseEntity<OrderDto> setPaidOrder(@PathVariable("id") Long orderId) throws OrderNotFoundException {
        return ResponseEntity.ok(service.setPaidOrder(orderId));
    }
}
