package com.younis.refinery.ingestion.controller;

import com.younis.refinery.ingestion.dto.TransactionRequest;
import com.younis.refinery.ingestion.dto.TransactionValidationResponse;
import com.younis.refinery.ingestion.service.TransactionValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionValidationService transactionValidationService;

    public TransactionController(TransactionValidationService transactionValidationService) {
        this.transactionValidationService = transactionValidationService;
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
}