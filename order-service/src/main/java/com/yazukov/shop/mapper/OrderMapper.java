package com.yazukov.shop.mapper;

import com.yazukov.shop.dto.OrderDto;
import com.yazukov.shop.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto orderToOrderDto(Order order);

    Order orderDtoToOrder(OrderDto orderDto);
}
