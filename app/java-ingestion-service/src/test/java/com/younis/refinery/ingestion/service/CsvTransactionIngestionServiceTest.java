package com.younis.refinery.ingestion.service;

import com.younis.refinery.ingestion.dto.CsvUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvTransactionIngestionServiceTest {

    @TempDir
    Path tempDir;

    private CsvTransactionIngestionService csvTransactionIngestionService;

    @BeforeEach
    void setUp() {
        TransactionValidationService transactionValidationService = new TransactionValidationService();
        CsvTransactionOutputWriter outputWriter = new CsvTransactionOutputWriter(tempDir.toString());
        csvTransactionIngestionService = new CsvTransactionIngestionService(transactionValidationService, outputWriter);
    }

    @Test
    void shouldProcessCsvAndReturnAcceptedAndRejectedCounts() {
        String csvContent = """
                transaction_id,customer_id,account_id,transaction_timestamp,merchant_id,merchant_category,amount,currency,country,status,risk_score
                TXN-1001,CUST-001,ACC-001,2026-06-27T10:15:30,MERCH-001,GROCERY,42.50,GBP,GB,APPROVED,20
                TXN-1002,CUST-002,ACC-002,2026-06-27T11:20:00,MERCH-002,FUEL,75.00,GBP,GB,APPROVED,35
                TXN-1003,CUST-003,ACC-003,2026-06-27T12:45:00,MERCH-003,TRAVEL,120.00,ABC,GB,APPROVED,40
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "transactions_sample.csv",
                "text/csv",
                csvContent.getBytes()
        ); // turns the contents of the string into bytes to mock actual upload over browser

        CsvUploadResponse response = csvTransactionIngestionService.processFile(file);

        assertEquals("transactions_sample.csv", response.getFileName());
        assertEquals(3, response.getTotalRows());
        assertEquals(2, response.getAcceptedRows());
        assertEquals(1, response.getRejectedRows());
        assertEquals("TXN-1003", response.getRejectedRecords().getFirst().getTransactionId());
        assertEquals("Unsupported currency: ABC", response.getRejectedRecords().getFirst().getReason());

        assertTrue(response.getAcceptedOutputPath().contains("accepted_transactions_"));
        assertTrue(response.getRejectedOutputPath().contains("rejected_transactions_"));
    }
}
