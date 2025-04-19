package com.example.rule.engine.service;

import com.example.rule.engine.model.*;
import com.example.rule.engine.respository.BusinessRuleRepository;
import com.example.rule.engine.respository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngineService {


    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BusinessRuleRepository businessRuleRepository;

        public List<RuleEngineResult> applyRulesOnDB() {
            List<PaymentTransaction> pending= paymentTransactionRepository
                    .findAll()
                    .stream()
                    .filter(
                            p -> p
                                    .getStatus()
                                    .equalsIgnoreCase("pending")).toList();
            List<RuleEngineResult> results = new ArrayList<>();
            pending.forEach(p -> {
                RuleEngineResult result = applyRules(p);
                results.add(result);
                paymentTransactionRepository.save(result.getTransformedTransaction());
            });
            return results;
        }


        public RuleEngineResult applyRules(PaymentTransaction input) {
        List<BusinessRule> enabledRules = businessRuleRepository.findAll()
                .stream().filter(BusinessRule::getEnabled)
                .sorted(Comparator.comparing(BusinessRule::getPriority).reversed())
                .toList();

        List<BusinessRule> enrichmentRules = enabledRules.stream()
                .filter(r -> r.getRuleType() == RuleType.ENRICHMENT)
                .toList();

        List<BusinessRule> routingRules = new ArrayList<>(enabledRules.stream()
                .filter(r -> r.getRuleType() == RuleType.ROUTING)
                .toList());

        List<String> appliedRuleNames = new ArrayList<>();

        // Clone object for output
        PaymentTransaction output = clonePaymentTransaction(input);

        // Approach: We generate Drools DRL for all the rules on the fly, create a KieSession, and execute
        String drl = generateRulesDRL(enrichmentRules, routingRules);
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, "src/main/resources/rules.drl");
        KieSession kieSession = kieHelper.build().newKieSession();
        kieSession.setGlobal("appliedRuleNames", appliedRuleNames);
        kieSession.setGlobal("input", input);
        kieSession.setGlobal("output", output);

        kieSession.insert(input);
        kieSession.insert(output);

        kieSession.fireAllRules();
        kieSession.dispose();

        RuleEngineResult result = new RuleEngineResult();
        result.setTransformedTransaction(output);
        result.setAppliedRuleNames(appliedRuleNames);

        return result;
    }

    private String generateRulesDRL(List<BusinessRule> enrichmentRules, List<BusinessRule> routingRules) {
        StringBuilder drl = new StringBuilder();
        drl.append("import com.example.rule.engine.model.PaymentTransaction;\n");
        drl.append("global java.util.List appliedRuleNames;\n");
        drl.append("global com.example.rule.engine.model.PaymentTransaction input;\n");
        drl.append("global com.example.rule.engine.model.PaymentTransaction output;\n\n");



        // Enrichment rules: apply all matches
        for (BusinessRule rule : enrichmentRules) {
            drl.append("rule \"ENRICHMENT-")
                    .append(rule.getName()).append("-").append(rule.getId()).append("\"\n");
            drl.append("salience ").append(rule.getPriority()).append("\n");
            drl.append("when\n")
                    .append("    eval(").append(rule.getCondition()).append(")\n");
            drl.append("then\n")
                    .append("System.out.println(\"Rule fired: 1\");")
                    .append("    ").append(rule.getAction()).append(";\n")
                    .append("    appliedRuleNames.add(\"")
                    .append(rule.getName())
                    .append("\");\n")
                    .append("end\n\n");
        }

        // Routing rules: apply only highest-priority match
        if (!routingRules.isEmpty()) {
            // Sort routing rules by priority, pick the highest one
            routingRules.sort(Comparator.comparing(BusinessRule::getPriority).reversed());
            BusinessRule highestPriorityRule = routingRules.get(0);

            drl.append("rule \"ROUTING-")
                    .append(highestPriorityRule.getName()).append("-").append(highestPriorityRule.getId()).append("\"\n");
            drl.append("salience ").append(highestPriorityRule.getPriority()).append("\n");
            drl.append("when\n")
                    .append("    eval(").append(highestPriorityRule.getCondition()).append(")\n");
            drl.append("then\n")
                    .append("System.out.println(\"Rule fired: 2\");")
                    .append("    ").append(highestPriorityRule.getAction()).append(";\n")
                    .append("    appliedRuleNames.add(\"").append(highestPriorityRule.getName()).append("\");\n")
                    .append("end\n\n");
        }

        log.info("Enrichment Rules: {}", enrichmentRules);
        log.info("Routing Rules: {}" , routingRules);
        log.info("Generated DRL:\n{}", drl);
        return drl.toString();
    }


    private PaymentTransaction clonePaymentTransaction(PaymentTransaction original) {
        // Perform a manual deep copy to avoid shared references
        return PaymentTransaction.builder()
                .id(original.getId()) // or null if you're creating a new object
                .direction(original.getDirection())
                .amount(original.getAmount())
                .currency(original.getCurrency())
                .status(original.getStatus())
                .reference(original.getReference())
                .build();
    }
}