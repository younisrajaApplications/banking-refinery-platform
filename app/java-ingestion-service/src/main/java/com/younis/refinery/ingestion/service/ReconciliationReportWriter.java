package com.younis.refinery.ingestion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.younis.refinery.ingestion.dto.ReconciliationReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReconciliationReportWriter {

    private static final DateTimeFormatter FILE_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final String outputBaseDirectory;
    private final ObjectMapper objectMapper;

    public ReconciliationReportWriter(
            @Value("${refinery.output.base-directory}") String outputBaseDirectory,
            ObjectMapper objectMapper
    ) {
        this.outputBaseDirectory = outputBaseDirectory;
        this.objectMapper = objectMapper;
    }

    public String writeReport(ReconciliationReport report) {
        try {
            Path reconciliationDirectory = Path.of(outputBaseDirectory, "reconciliation");
            Files.createDirectories(reconciliationDirectory);
            String timeStamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMAT);
            Path reportPath = reconciliationDirectory.resolve("reconciliation_report_" + timeStamp + ".json");

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(reportPath.toFile(), report);

            return reportPath.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write reconciliation report: " + exception.getMessage(), exception);
        }
    }

}


