package com.example.rule.engine.respository;

import com.example.rule.engine.model.BusinessRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRuleRepository extends JpaRepository<BusinessRule, Long> {
}