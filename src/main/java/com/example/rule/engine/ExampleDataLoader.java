package com.example.rule.engine;

import com.example.rule.engine.model.*;
import com.example.rule.engine.respository.BusinessRuleRepository;
import com.example.rule.engine.respository.PaymentTransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ExampleDataLoader {

    @Bean
    public CommandLineRunner loadExampleData(BusinessRuleRepository businessRuleRepository,
                                             PaymentTransactionRepository paymentTransactionRepository) {
        return args -> {
            // Example PaymentTransactions
            PaymentTransaction tx1 = PaymentTransaction.builder()
                    .direction("inbound")
                    .amount(1000.00)
                    .currency("USD")
                    .status("PENDING")
                    .reference("123")
                    .build();

            PaymentTransaction tx2 = PaymentTransaction.builder()
                    .direction("outbound")
                    .amount(800.00)
                    .currency("EUR")
                    .status("PENDING")
                    .reference("456")
                    .build();
            if (paymentTransactionRepository.count() == 0){
                paymentTransactionRepository.saveAll(Arrays.asList(tx1, tx2));
            }
            // Example BusinessRules
            BusinessRule enrichmentRule = BusinessRule.builder()
                    .name("Auto-Approve Inbound USD")
                    .ruleType(RuleType.ENRICHMENT)
                    .condition("input.getDirection().equals(\"inbound\") && input.getCurrency().equals(\"USD\") && input.getAmount() >= 1000.00")
                    .action("output.setStatus(\"APPROVED\")")
                    .priority(1)
                    .enabled(true)
                    .build();

            BusinessRule routingRule = BusinessRule.builder()
                    .name("Route Large Outbound")
                    .ruleType(RuleType.ROUTING)
                    .condition("input.getDirection().equals(\"outbound\") && input.getAmount() >= 500.00")
                    .action("output.setReference(\"ROUTED-HIGH-VALUE\")")
                    .priority(2)
                    .enabled(true)
                    .build();
            if(businessRuleRepository.count() == 0){
                businessRuleRepository.saveAll(Arrays.asList(enrichmentRule, routingRule));
            }
        };
    }
}