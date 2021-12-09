package com.ust.wordmaster;

import com.ust.wordmaster.service.fetching.HttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WordMasterApp {

	public static final String BBCUrl = "https://www.bbc.com/";

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(WordMasterApp.class, args);
		HttpClient httpClient = context.getBean(HttpClient.class);
		String html = httpClient.fetchHtml(BBCUrl);
		System.out.println(html);

	}

}
