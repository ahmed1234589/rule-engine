package com.example.rule.engine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RuleType ruleType;

    @Column(name = "`condition`",length = 2000)
    @NotBlank
    private String condition;

    @Column(length = 2000)
    @NotBlank
    private String action;

    @NotNull
    private Integer priority;

    @NotNull
    private Boolean enabled;

}