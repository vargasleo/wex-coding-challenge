package com.example.wexcodingchallenge.fixture;


import com.example.wexcodingchallenge.service.exchange.ExchangeRateConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ExchangeRateConfigFixture {

    public static ExchangeRateConfig createMockExchangeRateConfig() {
        final var config = new ExchangeRateConfig();
        config.setHttp(createHttpConfig());
        return config;
    }

    private static ExchangeRateConfig.HttpConfig createHttpConfig() {
        final var httpConfig = new ExchangeRateConfig.HttpConfig();
        httpConfig.setBaseUrl("https://api.fiscaldata.treasury.gov");
        httpConfig.setUri("/services/api/fiscal_service/v1/accounting/od/rates_of_exchange");
        httpConfig.setFields(List.of("exchange_rate", "record_date"));
        httpConfig.setFilter(createExchangeRateFilter());
        return httpConfig;
    }

    private static ExchangeRateConfig.ExchangeRateFilter createExchangeRateFilter() {
        final var filter = new ExchangeRateConfig.ExchangeRateFilter();
        filter.setStart("record_date:gte:");
        filter.setEnd("record_date:lte:");
        filter.setCountryCurrency("country_currency_desc:eq:");
        return filter;
    }
}