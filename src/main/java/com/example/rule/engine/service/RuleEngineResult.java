package com.example.rule.engine.service;

import com.example.rule.engine.model.PaymentTransaction;
import java.util.List;

public class RuleEngineResult {
    private PaymentTransaction transformedTransaction;
    private List<String> appliedRuleNames;

    public PaymentTransaction getTransformedTransaction() {
        return transformedTransaction;
    }

    public void setTransformedTransaction(PaymentTransaction transformedTransaction) {
        this.transformedTransaction = transformedTransaction;
    }

    public List<String> getAppliedRuleNames() {
        return appliedRuleNames;
    }

    public void setAppliedRuleNames(List<String> appliedRuleNames) {
        this.appliedRuleNames = appliedRuleNames;
    }
}