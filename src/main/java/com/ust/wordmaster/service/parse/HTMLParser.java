package com.ust.wordmaster.service.parse;

import java.util.List;


public interface HTMLParser {

    List<String> parseHTML(String html, String selector);

}
