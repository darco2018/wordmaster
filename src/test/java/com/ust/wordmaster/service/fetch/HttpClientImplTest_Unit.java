package com.ust.wordmaster.service.fetch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpClientImplTest_Unit {

    public static final String BBC_URL = "https://www.bbc.com/";
    HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());

    @DisplayName("Fetches BBC homepage")
    @Test
    public void givenBbcUrl_fetchesHtml() throws MalformedURLException, URISyntaxException {

        String html = fetchingService.fetchHtml(new URL(BBC_URL).toURI());

        Assertions.assertThat(html)
                .isNotNull()
                .isNotBlank()
                .isNotEmpty()
                .hasSizeGreaterThan(500)
                .containsIgnoringCase("<!DOCTYPE html>")
                .containsIgnoringCase("bbc")
                .contains("head", "body");
    }

}