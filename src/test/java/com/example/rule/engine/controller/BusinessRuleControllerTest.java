
package com.example.rule.engine.controller;

import com.example.rule.engine.respository.BusinessRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BusinessRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessRuleRepository repo;

    @BeforeEach
    void setUp() {
        // Clear repository before each test to ensure a clean state
        repo.deleteAll();
    }

    @Test
    void testCreateAndListRule() throws Exception {
        String json = """
                {
                  "name": "Test Rule",
                  "ruleType": "ENRICHMENT",
                  "condition": "input.getAmount() > 100",
                  "action": "output.setStatus(\\"HIGH\\")",
                  "priority": 1,
                  "enabled": true
                }
                """;

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
