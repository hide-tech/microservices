package com.yazukov.shop.repository;

import com.yazukov.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Set<Order> findOrderByUserId(Long userId);
}
