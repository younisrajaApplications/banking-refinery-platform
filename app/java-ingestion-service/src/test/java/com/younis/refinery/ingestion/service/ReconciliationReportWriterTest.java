package com.younis.refinery.ingestion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.younis.refinery.ingestion.dto.ReconciliationReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReconciliationReportWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldWriteReconciliationReportAsJsonFile() throws Exception {
        ReconciliationReportWriter writer = new ReconciliationReportWriter(tempDir.toString(), new ObjectMapper());

        ReconciliationReport report = new ReconciliationReport(
            "transactions_sample.csv",
            5,
            2,
            3,
            true,
            "COMPLETED_WITH_REJECTIONS",
            "data/processed/accepted/accepted_transactions.csv",
            "data/processed/rejected/rejected_transactions.csv",
            Map.of("Unsupported currency: ABC", 1L),
            "2026-07-09T18:00:00Z"
        );

        String reportPath = writer.writeReport(report);

        Path path = Path.of(reportPath);

        assertTrue(Files.exists(path));

        String content = Files.readString(path);

        assertTrue(content.contains("transactions_sample.csv"));
        assertTrue(content.contains("COMPLETED_WITH_REJECTIONS"));
        assertTrue(content.contains("Unsupported currency: ABC"));
    }

}
