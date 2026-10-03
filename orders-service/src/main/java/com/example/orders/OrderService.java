package com.example.orders;

import datadog.trace.api.Trace;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final RestClient inventory;
    private final OrderRepository repository;

    public OrderService(RestClient inventory, OrderRepository repository) {
        this.inventory = inventory;
        this.repository = repository;
    }

    // Auto-instrumentation already traces HTTP, JDBC and Spring MVC.
    // @Trace adds a custom span around our own business logic.
    @Trace(operationName = "order.place", resourceName = "OrderService.place")
    public Order place(String sku, int quantity) {
        reserveStock(sku, quantity);
        return save(new Order(sku, quantity, "CONFIRMED"));
    }

    @Trace(operationName = "order.reserve_stock")
    void reserveStock(String sku, int quantity) {
        try {
            inventory.post()
                    .uri("/inventory/{sku}/reserve?quantity={q}", sku, quantity)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode().value()),
                    "inventory rejected reservation", e);
        }
    }

    @Trace(operationName = "order.save")
    Order save(Order order) {
        return repository.save(order);
    }
}
