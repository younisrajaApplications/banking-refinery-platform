package com.younis.refinery.ingestion.controller;

import com.younis.refinery.ingestion.dto.IngestionSummaryResponse;
import com.younis.refinery.ingestion.service.IngestionQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngestionController.class)
public class IngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngestionQueryService ingestionQueryService;

    @Test
    void shouldReturnRecentIngestions() throws Exception {
        IngestionSummaryResponse ingestionSummaryResponse = new IngestionSummaryResponse(
            1L,
            "transactions_sample.csv",
            5,
            2,
            3,
            true,
            "COMPLETED_WITH_REJECTIONS",
            LocalDateTime.of(2026,7,24,10,0)
        );

        //when(ingestionQueryService.getRecentIngestions()).thenReturn(List.of(ingestionSummaryResponse));
        when(ingestionQueryService.getRecentIngestions())
                .thenReturn(List.of(ingestionSummaryResponse));

        mockMvc.perform(get("/ingestions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ingestionFileId").value(1))
            .andExpect(jsonPath("$[0].fileName").value("transactions_sample.csv"))
            .andExpect(jsonPath("$[0].totalRows").value(5))
            .andExpect(jsonPath("$[0].acceptedRows").value(2))
            .andExpect(jsonPath("$[0].rejectedRows").value(3))
            .andExpect(jsonPath("$[0].processingStatus").value("COMPLETED_WITH_REJECTIONS"));
    }
}
