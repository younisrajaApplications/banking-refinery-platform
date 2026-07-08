package com.younis.refinery.ingestion.service;

import com.younis.refinery.ingestion.dto.CsvUploadResponse;
import com.younis.refinery.ingestion.dto.RejectedRecordResponse;
import com.younis.refinery.ingestion.dto.TransactionRequest;
import com.younis.refinery.ingestion.service.CsvTransactionOutputWriter.OutputFiles;
import com.younis.refinery.ingestion.service.CsvTransactionOutputWriter.RejectedTransactionRecord;
import org.springframework.stereotype.Service;
// New Imports
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvTransactionIngestionService {

    private final TransactionValidationService transactionValidationService;
    private final CsvTransactionOutputWriter csvTransactionOutputWriter;

    public CsvTransactionIngestionService(TransactionValidationService transactionValidationService, CsvTransactionOutputWriter csvTransactionOutputWriter) {
        this.transactionValidationService = transactionValidationService;
        this.csvTransactionOutputWriter = csvTransactionOutputWriter;
    }

    public CsvUploadResponse processFile(MultipartFile file) {
        validateFile(file);

        long totalRows = 0;

        List<TransactionRequest> acceptedTransactions = new ArrayList<>();
        List<CsvTransactionOutputWriter.RejectedTransactionRecord> rejectedTransactions = new ArrayList<>();
        List<RejectedRecordResponse> rejectedRecords = new ArrayList<>();

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder() //.builder allows me to set how the csv should be handled
                .setHeader() // lets CSVFormat know that the first row are headers/column names, allow me to later do something like record.get("ColumnName")
                .setSkipHeaderRecord(true) // tells CSVFormat to skip the header row
                .setTrim(true)
                .setIgnoreHeaderCase(true)
                .build(); // finishes set up

        try (
                Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser csvParser = csvFormat.parse(reader) // turns CSV rows into Java records.
        ) {
           for (CSVRecord record : csvParser) {
               totalRows++;
               try {
                   TransactionRequest transaction = mapCsvRecordToTransactionRequest(record);
                   transactionValidationService.validateBusinessRules(transaction);
                   acceptedTransactions.add(transaction);
               } catch (Exception exception) {
                   long rowNumber = record.getRecordNumber() + 1;
                   String transactionId = getTransactionIdSafely(record);
                   String reason = exception.getMessage();

                   rejectedTransactions.add(
                       new RejectedTransactionRecord(rowNumber, transactionId, reason)
                   );

                   rejectedRecords.add(
                       new RejectedRecordResponse(rowNumber, transactionId, reason)
                   );
               }
           }
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to process CSV file: " + exception.getMessage());
        }

        OutputFiles outputFiles = csvTransactionOutputWriter.writeOutputs(
            acceptedTransactions,
            rejectedTransactions
        );

        return new CsvUploadResponse(
                file.getOriginalFilename(),
                totalRows,
                acceptedTransactions.size(),
                rejectedTransactions.size(),
                outputFiles.acceptedOutputPath(),
                outputFiles.rejectedOutputPath(),
                rejectedRecords
        );
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A csv file is required.");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported!");
        }
    }

    private TransactionRequest mapCsvRecordToTransactionRequest(CSVRecord record) {
        TransactionRequest request = new TransactionRequest();

        request.setTransactionId(record.get("transaction_id"));
        request.setCustomerId(record.get("customer_id"));
        request.setAccountId(record.get("account_id"));
        request.setTransactionTimestamp(LocalDateTime.parse(record.get("transaction_timestamp")));
        request.setMerchantId(record.get("merchant_id"));
        request.setMerchantCategory(record.get("merchant_category"));
        request.setAmount(new BigDecimal(record.get("amount")));
        request.setCurrency(record.get("currency"));
        request.setCountry(record.get("country"));
        request.setStatus(record.get("status"));
        request.setRiskScore(parseRiskScore(record.get("risk_score")));

        return request;
    }

    private Integer parseRiskScore(String riskScore) {
        if (riskScore == null || riskScore.isBlank()) {
            return null;
        }

        return Integer.parseInt(riskScore);
    }

    private String getTransactionIdSafely(CSVRecord record) {
        try {
            return record.get("transaction_id");
        } catch (Exception exception) {
            return "UNKNOWN";
        }
    }
}
