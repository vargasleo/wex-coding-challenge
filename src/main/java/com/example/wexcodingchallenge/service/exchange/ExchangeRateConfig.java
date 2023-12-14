package com.example.wexcodingchallenge.service.exchange;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Data
@Configuration
@ConfigurationProperties(prefix = "exchange-rate")
public class ExchangeRateConfig {

    private HttpConfig http;
    private int monthsBack;

    @Data
    public static class HttpConfig {
        private String baseUrl;
        private String uri;
        private List<String> fields;
        private ExchangeRateFilter filter;

        public String buildParameters(LocalDate exchangeRateStartDate, LocalDate transactionDate, String currency) {
            final var fieldsString = fields.stream().collect(joining(",", "", ""));

            final var startFilter = filter.getStart() + exchangeRateStartDate;
            final var endFilter = filter.getEnd() + transactionDate;
            final var countryCurrencyFilter = filter.getCountryCurrency() + currency;

            final var filterString = startFilter + "," + endFilter + "," + countryCurrencyFilter;
            return "?fields=" + fieldsString + "&" + "filter=" + filterString;
        }
    }

    @Data
    public static class ExchangeRateFilter {
        private String start;
        private String end;
        private String countryCurrency;
    }
}
