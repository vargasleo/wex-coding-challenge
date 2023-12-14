package com.example.wexcodingchallenge.service.exchange;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ExchangeRateConfigTest {

    @Autowired
    private ExchangeRateConfig exchangeRateConfig;

    @Test
    void propertiesAreLoadedCorrectly() {
        assertNotNull(exchangeRateConfig.getHttp());
        assertEquals("https://api.fiscaldata.treasury.gov", exchangeRateConfig.getHttp().getBaseUrl());
        assertEquals("/services/api/fiscal_service/v1/accounting/od/rates_of_exchange", exchangeRateConfig.getHttp().getUri());
        assertArrayEquals(new String[]{"exchange_rate", "record_date"}, exchangeRateConfig.getHttp().getFields().toArray(new String[0]));
        assertEquals("record_date:gte:", exchangeRateConfig.getHttp().getFilter().getStart());
        assertEquals("record_date:lte:", exchangeRateConfig.getHttp().getFilter().getEnd());
        assertEquals("country_currency_desc:eq:", exchangeRateConfig.getHttp().getFilter().getCountryCurrency());
        assertEquals(6, exchangeRateConfig.getMonthsBack());
    }
}
