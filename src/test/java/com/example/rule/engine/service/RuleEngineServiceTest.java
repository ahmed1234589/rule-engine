package com.example.rule.engine.service;

import com.example.rule.engine.model.*;
import com.example.rule.engine.respository.BusinessRuleRepository;
import com.example.rule.engine.respository.PaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleEngineServiceTest {

    private BusinessRuleRepository ruleRepository;
    private PaymentTransactionRepository paymentTransactionRepository;
    private RuleEngineService ruleEngineService;

    @BeforeEach
    void setup() {
        ruleRepository = mock(BusinessRuleRepository.class);
        ruleEngineService = new RuleEngineService(paymentTransactionRepository,ruleRepository);
        }

    @Test
    void testApplyEnrichmentRule() {
        BusinessRule enrichment = BusinessRule.builder()
            .id(1L)
            .name("Approve Inbound")
            .ruleType(RuleType.ENRICHMENT)
            .condition("input.getDirection().equals(\"inbound\")")
            .action("output.setStatus('APPROVED')")
            .priority(5)
            .enabled(true)
            .build();

        when(ruleRepository.findAll()).thenReturn(Arrays.asList(enrichment));

        PaymentTransaction txn = PaymentTransaction.builder()
            .id(100L)
            .direction("inbound")
            .amount(100.0)
            .currency("USD")
            .status("NEW")
            .reference("XYZ")
            .build();

        RuleEngineResult result = ruleEngineService.applyRules(txn);

        assertEquals("APPROVED", result.getTransformedTransaction().getStatus());
        assertEquals(1, result.getAppliedRuleNames().size());
        assertTrue(result.getAppliedRuleNames().contains("Approve Inbound"));
    }

    @Test
    void testApplyRoutingRuleOnlyOnce() {
        BusinessRule routing1 = BusinessRule.builder()
            .id(2L)
            .name("High Value Outbound")
            .ruleType(RuleType.ROUTING)
            .condition("input.getAmount() > 500")
            .action("output.setReference(\"SPECIAL-ROUTE\")")
            .priority(10)
            .enabled(true)
            .build();

        BusinessRule routing2 = BusinessRule.builder()
            .id(3L)
            .name("Low Value Outbound")
            .ruleType(RuleType.ROUTING)
            .condition("input.getAmount() <= 500")
            .action("output.setReference(\"REGULAR-ROUTE\")")
            .priority(5)
            .enabled(true)
            .build();

        when(ruleRepository.findAll()).thenReturn(Arrays.asList(routing1, routing2));

        PaymentTransaction txn = PaymentTransaction.builder()
            .id(101L)
            .direction("outbound")
            .amount(1000.0)
            .currency("USD")
            .status("PENDING")
            .reference("ABC")
            .build();

        RuleEngineResult result = ruleEngineService.applyRules(txn);

        assertEquals("SPECIAL-ROUTE", result.getTransformedTransaction().getReference());
        assertEquals(1, result.getAppliedRuleNames().size());
        assertTrue(result.getAppliedRuleNames().contains("High Value Outbound"));
    }
}