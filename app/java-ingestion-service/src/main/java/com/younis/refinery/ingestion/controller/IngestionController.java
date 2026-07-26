package com.younis.refinery.ingestion.controller;

import com.younis.refinery.ingestion.dto.AcceptedTransactionResponse;
import com.younis.refinery.ingestion.dto.IngestionDetailResponse;
import com.younis.refinery.ingestion.dto.IngestionSummaryResponse;
import com.younis.refinery.ingestion.dto.RejectedTransactionResponse;
import com.younis.refinery.ingestion.service.IngestionQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ingestions")
public class IngestionController {

    private final IngestionQueryService ingestionQueryService;

    public IngestionController(IngestionQueryService ingestionQueryService) {
        this.ingestionQueryService = ingestionQueryService;
    }

    @GetMapping
    public List<IngestionSummaryResponse> getRecentIngestions(){
        return ingestionQueryService.getRecentIngestions();
    }

    @GetMapping("/{id}")
    public IngestionDetailResponse getIngestionById(@PathVariable Long id){
        return ingestionQueryService.getIngestionById(id);
    }

    @GetMapping("/{id}/accepted")
    public List<AcceptedTransactionResponse> getAcceptedTransactions(@PathVariable Long id){
        return ingestionQueryService.getAcceptedTransactions(id);
    }

    @GetMapping("/{id}/rejected")
    public List<RejectedTransactionResponse> getRejectedTransactions(@PathVariable Long id){
        return ingestionQueryService.getRejectedTransactions(id);
    }
}
