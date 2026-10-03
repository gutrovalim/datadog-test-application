package com.example.orders;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class OrderController {

    record OrderRequest(String sku, int quantity) {}

    private final OrderService service;
    private final OrderRepository repository;

    public OrderController(OrderService service, OrderRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    Order create(@RequestBody OrderRequest request) {
        return service.place(request.sku(), request.quantity());
    }

    @GetMapping("/orders")
    List<Order> list() {
        return repository.findAll();
    }

    @GetMapping("/orders/{id}")
    Order get(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
