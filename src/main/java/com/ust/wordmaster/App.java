package com.ust.wordmaster;

import com.ust.wordmaster.service.fetching.HttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@SpringBootApplication
public class App {

    public static final String BBCUrl = "https://www.bbc.com/";

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(App.class, args);
        /*HttpClient httpClient = context.getBean(HttpClient.class);
        String html = null;
        try {
            html = httpClient.fetchHtml(new URL(BBCUrl).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println(html);*/

    }

}
