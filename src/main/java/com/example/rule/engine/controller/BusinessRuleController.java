package com.example.rule.engine.controller;

import com.example.rule.engine.model.BusinessRule;
import com.example.rule.engine.respository.BusinessRuleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class BusinessRuleController {

    private final BusinessRuleRepository ruleRepository;

    @GetMapping
    public List<BusinessRule> listRules() {
        return ruleRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessRule> getRule(@PathVariable Long id) {
        return ruleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public BusinessRule createRule(@RequestBody @Valid BusinessRule rule) {
        rule.setId(null);
        return ruleRepository.save(rule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessRule> updateRule(@PathVariable Long id, @RequestBody @Valid BusinessRule rule) {
        return ruleRepository.findById(id)
                .map(existing -> {
                    rule.setId(id);
                    return ResponseEntity.ok(ruleRepository.save(rule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        if (ruleRepository.existsById(id)) {
            ruleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}