package com.ust.wordmaster.service.fetching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HttpClientImpl implements HttpClient{

    private RestTemplate restTemplate;

    public HttpClientImpl(RestTemplateBuilder builder){
        restTemplate = builder.build();
    }

    @Override
    public String fetchHtml(String url) {
        log.info("Starting fetching from " + url);

        String html = "";
        try {
            html = restTemplate.getForObject(url, String.class);
            log.info("Fetched " + html.length() + " characters from " + url);

            return html;
        } catch (Exception e) {
            log.info("Fetching data from " + url + " unsuccessful.");
            e.printStackTrace();
        }

        return "";
    }
}
