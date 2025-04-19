package com.example.rule.engine.controller;

import com.example.rule.engine.model.PaymentTransaction;
import com.example.rule.engine.respository.PaymentTransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionRepository transactionRepository;


    @GetMapping
    public List<PaymentTransaction> listTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransaction> getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PaymentTransaction createTransaction(@RequestBody @Valid PaymentTransaction transaction) {
        transaction.setId(null);
        return transactionRepository.save(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentTransaction> updateTransaction(@PathVariable Long id, @RequestBody @Valid PaymentTransaction transaction) {
        return transactionRepository.findById(id)
                .map(existing -> {
                    transaction.setId(id);
                    return ResponseEntity.ok(transactionRepository.save(transaction));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}