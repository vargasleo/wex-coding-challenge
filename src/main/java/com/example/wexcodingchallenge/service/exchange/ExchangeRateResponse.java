package com.example.wexcodingchallenge.service.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ExchangeRateResponse {

    @JsonProperty("data")
    private List<ExchangeRateData> data;

    @Data
    public static class ExchangeRateData {
        @JsonProperty("exchange_rate")
        private BigDecimal exchangeRate;
        @JsonProperty("record_date")
        private LocalDate recordDate;
    }

}
