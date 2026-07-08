package com.younis.refinery.ingestion.controller;

import com.younis.refinery.ingestion.service.CsvTransactionIngestionService;
import com.younis.refinery.ingestion.service.TransactionValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionValidationService transactionValidationService;

    @MockitoBean
    private CsvTransactionIngestionService csvTransactionIngestionService;

    @Test
    void shouldReturnValidResponseForValidTransaction() throws Exception {
        doNothing().when(transactionValidationService).validateBusinessRules(any());

        String requestBody = """
                {
                  "transactionId": "TXN-1001",
                  "customerId": "CUST-001",
                  "accountId": "ACC-001",
                  "transactionTimestamp": "2026-06-27T10:15:30",
                  "merchantId": "MERCH-001",
                  "merchantCategory": "GROCERY",
                  "amount": 42.50,
                  "currency": "GBP",
                  "country": "GB",
                  "status": "APPROVED",
                  "riskScore": 20
                }
                """;
        mockMvc.perform(post("/transactions/validate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.transactionId").value("TXN-1001"))
                .andExpect(jsonPath("$.message").value("Transaction is valid"));
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        String requestBody = """
                {
                  "transactionId": "",
                  "customerId": "",
                  "accountId": "ACC-001",
                  "transactionTimestamp": "2026-06-27T10:15:30",
                  "merchantId": "MERCH-001",
                  "merchantCategory": "GROCERY",
                  "amount": -10,
                  "currency": "GBP",
                  "country": "GB",
                  "status": "APPROVED",
                  "riskScore": 200
                }
                """;

        mockMvc.perform(post("/transactions/validate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }


}
