package com.example.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class InventoryApplication {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>(Map.of(
            "BOOK-1", 100, "MUG-2", 50, "SHIRT-3", 10, "OUT-OF-STOCK", 0));

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @PostMapping("/inventory/{sku}/reserve")
    ResponseEntity<Map<String, Object>> reserve(@PathVariable String sku, @RequestParam int quantity)
            throws InterruptedException {
        // Simulate a slow dependency (50-350ms) so spans have visible duration
        Thread.sleep(ThreadLocalRandom.current().nextInt(50, 350));

        // ~5% random failure to produce error traces
        if (ThreadLocalRandom.current().nextInt(100) < 5) {
            throw new IllegalStateException("inventory backend timeout");
        }

        Integer available = stock.get(sku);
        if (available == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "unknown sku"));
        }
        if (available < quantity) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "insufficient stock"));
        }
        stock.merge(sku, -quantity, Integer::sum);
        return ResponseEntity.ok(Map.of("sku", sku, "reserved", quantity));
    }
}
