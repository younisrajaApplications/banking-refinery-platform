package com.younis.refinery.ingestion.service;

import com.younis.refinery.ingestion.dto.AcceptedTransactionResponse;
import com.younis.refinery.ingestion.dto.IngestionDetailResponse;
import com.younis.refinery.ingestion.dto.IngestionSummaryResponse;
import com.younis.refinery.ingestion.dto.RejectedTransactionResponse;
import com.younis.refinery.ingestion.repository.IngestionQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class IngestionQueryService {

    private final IngestionQueryRepository ingestionQueryRepository;

    public IngestionQueryService(IngestionQueryRepository ingestionQueryRepository) {
        this.ingestionQueryRepository = ingestionQueryRepository;
    }

    public List<IngestionSummaryResponse> getRecentIngestions(){
        return ingestionQueryRepository.findRecentIngestions();
    }

    public IngestionDetailResponse getIngestionById(Long ingestionFileId){
        return ingestionQueryRepository.findIngestionById(ingestionFileId).orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ingestion file not found: " + ingestionFileId
            )
        );
    }

    public List<AcceptedTransactionResponse> getAcceptedTransactions(Long ingestionFileId){
        validateIngestionExists(ingestionFileId);
        return ingestionQueryRepository.findAcceptedTransactionsByIngestionId(ingestionFileId);
    }

    public List<RejectedTransactionResponse> getRejectedTransactions(Long ingestionFileId){
        validateIngestionExists(ingestionFileId);
        return ingestionQueryRepository.findRejectedTransactionsByIngestionId(ingestionFileId);
    }

    public void validateIngestionExists(Long ingestionFileId){
        if (!ingestionQueryRepository.existsById(ingestionFileId)){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ingestion file not found: " + ingestionFileId
            );
        }
    }
}
