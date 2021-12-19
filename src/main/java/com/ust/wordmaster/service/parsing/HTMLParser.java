package com.ust.wordmaster.service.parsing;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HTMLParser {

    List<String> parseHTML(String html, String selector);

}
