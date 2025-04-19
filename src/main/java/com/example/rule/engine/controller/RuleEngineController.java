package com.example.rule.engine.controller;

import com.example.rule.engine.model.PaymentTransaction;
import com.example.rule.engine.service.RuleEngineResult;
import com.example.rule.engine.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engine")
@RequiredArgsConstructor
public class RuleEngineController {

    private final RuleEngineService ruleEngineService;

    @PostMapping("/run")
    public ResponseEntity<RuleEngineResult> runEngine(@RequestBody PaymentTransaction transaction) {
        RuleEngineResult result = ruleEngineService.applyRules(transaction);
        return ResponseEntity.ok(result);
    }

    @PostMapping("runOnDB")
    public ResponseEntity<List<RuleEngineResult>> runEngineOnDB() {

        List<RuleEngineResult> results =  ruleEngineService.applyRulesOnDB();
        return ResponseEntity.ok(results);
    }
}