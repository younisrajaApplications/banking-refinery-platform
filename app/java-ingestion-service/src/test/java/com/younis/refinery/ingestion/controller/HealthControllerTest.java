package com.younis.refinery.ingestion.controller;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @Test
    void healthShouldReturnUpStatus() {
        HealthController controller = new HealthController();

        Map<String, Object> response = controller.health();

        assertEquals("UP", response.get("status"));
        assertEquals("java-ingestion-service", response.get("service"));
    }
}