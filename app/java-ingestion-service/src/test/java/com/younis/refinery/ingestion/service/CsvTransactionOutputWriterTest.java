package com.younis.refinery.ingestion.service;

import com.younis.refinery.ingestion.dto.TransactionRequest;
import com.younis.refinery.ingestion.service.CsvTransactionOutputWriter.OutputFiles;
import com.younis.refinery.ingestion.service.CsvTransactionOutputWriter.RejectedTransactionRecord;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvTransactionOutputWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldWriteAcceptedAndRejectedOutputFiles() throws Exception {
        CsvTransactionOutputWriter writer = new CsvTransactionOutputWriter(tempDir.toString());

        TransactionRequest acceptedTransaction = getTransactionRequest();

        RejectedTransactionRecord rejectedTransaction =
                new RejectedTransactionRecord(3, "TXN-1003", "Unsupported currency: ABC");

        OutputFiles outputFiles = writer.writeOutputs(
                List.of(acceptedTransaction),
                List.of(rejectedTransaction)
        );

        Path acceptedPath = Path.of(outputFiles.acceptedOutputPath());
        Path rejectedPath = Path.of(outputFiles.rejectedOutputPath());

        assertTrue(Files.exists(acceptedPath));
        assertTrue(Files.exists(rejectedPath));

        String acceptedContent = Files.readString(acceptedPath);
        String rejectedContent = Files.readString(rejectedPath);

        assertTrue(acceptedContent.contains("TXN-1001"));
        assertTrue(rejectedContent.contains("TXN-1003"));
        assertTrue(rejectedContent.contains("Unsupported currency: ABC"));
    }

    private static @NonNull TransactionRequest getTransactionRequest() {
        TransactionRequest acceptedTransaction = new TransactionRequest();
        acceptedTransaction.setTransactionId("TXN-1001");
        acceptedTransaction.setCustomerId("CUST-001");
        acceptedTransaction.setAccountId("ACC-001");
        acceptedTransaction.setTransactionTimestamp(LocalDateTime.of(2026, 6, 27, 10, 15, 30));
        acceptedTransaction.setMerchantId("MERCH-001");
        acceptedTransaction.setMerchantCategory("GROCERY");
        acceptedTransaction.setAmount(new BigDecimal("42.50"));
        acceptedTransaction.setCurrency("GBP");
        acceptedTransaction.setCountry("GB");
        acceptedTransaction.setStatus("APPROVED");
        acceptedTransaction.setRiskScore(20);
        return acceptedTransaction;
    }

}
