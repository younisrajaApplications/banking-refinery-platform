package com.younis.refinery.ingestion.controller;

import com.younis.refinery.ingestion.dto.CsvUploadResponse;
import com.younis.refinery.ingestion.dto.TransactionRequest;
import com.younis.refinery.ingestion.dto.TransactionValidationResponse;
import com.younis.refinery.ingestion.service.CsvTransactionIngestionService;
import com.younis.refinery.ingestion.service.TransactionValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionValidationService transactionValidationService;
    private final CsvTransactionIngestionService csvTransactionIngestionService;

    public TransactionController(TransactionValidationService transactionValidationService,
                                 CsvTransactionIngestionService csvTransactionIngestionService) {
        this.transactionValidationService = transactionValidationService;
        this.csvTransactionIngestionService = csvTransactionIngestionService;
    }

    @PostMapping("/validate")
    public ResponseEntity<TransactionValidationResponse> validateTransaction(
            @Valid @RequestBody TransactionRequest request
    ) {
        transactionValidationService.validateBusinessRules(request);

        return ResponseEntity.ok(
                new TransactionValidationResponse(
                        true,
                        "Transaction is valid",
                        request.getTransactionId()
                )
        );
    }

    @PostMapping("/upload")
    public ResponseEntity<CsvUploadResponse> uploadTransactions(@RequestParam("file") MultipartFile file) {
        CsvUploadResponse response = csvTransactionIngestionService.processFile(file);
        return ResponseEntity.ok(response);
    }
}