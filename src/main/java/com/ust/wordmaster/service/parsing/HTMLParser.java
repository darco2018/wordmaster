package com.ust.wordmaster.service.parsing;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HTMLParser {

    List<String> parse(String html, String selector);

}
