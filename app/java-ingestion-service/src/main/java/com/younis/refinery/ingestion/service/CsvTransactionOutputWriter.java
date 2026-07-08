package com.younis.refinery.ingestion.service;

import com.younis.refinery.ingestion.dto.TransactionRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvTransactionOutputWriter {

    private static final DateTimeFormatter FILE_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final String outputBaseDirectory;

    public CsvTransactionOutputWriter(
            @Value("${refinery.output.base-directory}") String outputBaseDirectory
    ) {
        this.outputBaseDirectory = outputBaseDirectory;
    }

    public OutputFiles writeOutputs(
            List<TransactionRequest> acceptedTransactions,
            List<RejectedTransactionRecord> rejectedTransactions
    ) {
        try {
            Path acceptedDirectory = Path.of(outputBaseDirectory, "accepted");
            Path rejectedDirectory = Path.of(outputBaseDirectory, "rejected");

            Files.createDirectories(acceptedDirectory);
            Files.createDirectories(rejectedDirectory);

            String timestamp = java.time.LocalDateTime.now().format(FILE_TIMESTAMP_FORMAT);

            Path acceptedOutputPath = acceptedDirectory.resolve("accepted_transactions_" + timestamp + ".csv");
            Path rejectedOutputPath = rejectedDirectory.resolve("rejected_transactions_" + timestamp + ".csv");

            writeAcceptedTransactions(acceptedOutputPath, acceptedTransactions);
            writeRejectedTransactions(rejectedOutputPath, rejectedTransactions);

            return new OutputFiles(
                    acceptedOutputPath.toString(),
                    rejectedOutputPath.toString()
            );

        } catch (Exception e) {
            throw new IllegalStateException("Failed to write csv output files: " + e.getMessage(), e);
        }
    }

    private void writeAcceptedTransactions(
            Path outputPath,
            List<TransactionRequest> acceptedTransactions
    ) throws Exception {
        try (
            BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);
            CSVPrinter csvPrinter = new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.builder()
                    .setHeader(
                        "transaction_id",
                        "customer_id",
                        "account_id",
                        "transaction_timestamp",
                        "merchant_id",
                        "merchant_category",
                        "amount",
                        "currency",
                        "country",
                        "status",
                        "risk_score"
                    )
                    .build()

            )
        ) {
            for (TransactionRequest transaction : acceptedTransactions) {
                csvPrinter.printRecord(
                        transaction.getTransactionId(),
                        transaction.getCustomerId(),
                        transaction.getAccountId(),
                        transaction.getTransactionTimestamp(),
                        transaction.getMerchantId(),
                        transaction.getMerchantCategory(),
                        formatAmount(transaction.getAmount()),
                        transaction.getCurrency(),
                        transaction.getCountry(),
                        transaction.getStatus(),
                        transaction.getRiskScore()
                );
            }
        }
    }
}
