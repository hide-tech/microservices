package com.yazukov.shop.exception;

public class OutOfStockException extends Exception{
    public OutOfStockException(String message) {
        super(message);
    }
}
