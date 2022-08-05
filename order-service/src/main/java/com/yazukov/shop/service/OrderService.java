package com.yazukov.shop.service;

import com.yazukov.shop.client.CheckClient;
import com.yazukov.shop.dto.OrderDto;
import com.yazukov.shop.dto.ProductDto;
import com.yazukov.shop.entity.Order;
import com.yazukov.shop.entity.Product;
import com.yazukov.shop.exception.OrderNotFoundException;
import com.yazukov.shop.exception.OutOfStockException;
import com.yazukov.shop.exception.ServiceNotAvailableException;
import com.yazukov.shop.mapper.OrderMapper;
import com.yazukov.shop.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductService service;
    private final CheckClient checkClient;

    public Set<OrderDto> getOrdersByUserId(Long userId){
        return orderRepository.findOrderByUserId(userId).stream()
                .map(order -> {
                    order.setProducts(service.getProductByOrderId(order.getId()));
                    return order;
                })
                .map(orderMapper::orderToOrderDto)
                .collect(Collectors.toSet());
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "informAboutProductServiceNotAvailable")
    public OrderDto addProductIntoOrder(Long orderId, ProductDto productDto) throws OrderNotFoundException, OutOfStockException {
        Order orderById = orderRepository.findById(orderId).orElseThrow(()->new OrderNotFoundException(
                String.format("Order with id %d not found", orderId)));
        Product product = service.saveProduct(productDto);
        Boolean check = checkClient.isAvailableProduct(product.getId(), product.getQty());
        Set<Product> products = orderById.getProducts();
        if (products==null) products=new HashSet<>();
        if (check) {
            products.add(product);
        } else {
            throw new OutOfStockException("Out of stock!");
        }
        orderById.setProducts(products);
        return orderMapper.orderToOrderDto(orderRepository.save(orderById));
    }

    public void informAboutProductServiceNotAvailable(Exception e) throws ServiceNotAvailableException {
        throw new ServiceNotAvailableException("Product service not available now. Try later!");
    }

    public OrderDto removeProductFromOrder(Long orderId, Long productId) throws OrderNotFoundException {
        Order orderById = orderRepository.findById(orderId).orElseThrow(()->new OrderNotFoundException(
                String.format("Order with id %d not found", orderId)));
        Set<Product> products = orderById.getProducts();
        if (products==null) throw new IllegalArgumentException("No products within order");
        Set<Product> collect = products.stream().filter(product -> product.getId() != productId)
                .collect(Collectors.toSet());
        orderById.setProducts(collect);
        return orderMapper.orderToOrderDto(orderRepository.save(orderById));
    }

    public OrderDto setPaidOrder(Long orderId) throws OrderNotFoundException {
        Order orderById = orderRepository.findById(orderId).orElseThrow(()->new OrderNotFoundException(
                String.format("Order with id %d not found", orderId)));
        orderById.setPayment(true);
        return orderMapper.orderToOrderDto(orderRepository.save(orderById));
    }

    public OrderDto createOrder(OrderDto orderDto){
        orderDto.getProducts().forEach(service::saveProduct);
        return orderMapper.orderToOrderDto(orderRepository.save(orderMapper.orderDtoToOrder(orderDto)));
    }

    public String removeOrder(Long orderId){
        orderRepository.deleteById(orderId);
        return String.format("Order with id %d has been deleted", orderId);
    }
}
