package com.ust.wordmaster.service.fetch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
public class HttpClientImpl implements HttpClient {

    private final RestTemplate restTemplate;

    public HttpClientImpl(RestTemplateBuilder builder) {
        restTemplate = builder.build();
    }

    @Override
    public String fetchHtml(URI url) {
        log.info("Starting fetching from " + url);

        try {
            String html = restTemplate.getForObject(url, String.class);
            log.info("Fetched " + (html != null ? html.length() : 0) + " characters from " + url);

            return html;
        } catch (Exception e) {
            log.info("Fetching data from " + url + " unsuccessful.");
            e.printStackTrace();
        }

        return "";
    }
}
