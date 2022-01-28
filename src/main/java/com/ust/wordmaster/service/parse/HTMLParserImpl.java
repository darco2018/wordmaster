package com.ust.wordmaster.service.parse;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HTMLParserImpl implements HTMLParser {

    @Override
    public List<String> parseHTML(final String html, final String attributeName) {

        log.info("Starting to parse the html of " + html.length() + " characters with the attribute: " + attributeName);

        Document doc = Jsoup.parse(html);
        Elements htmlElements = doc.getElementsByAttribute(attributeName); // selects HTML elements(eg <div>'s) containing this attribute
        List<String> attributeValues = new ArrayList<>();

        for (Element e : htmlElements) {
            String headline = e.attr(attributeName); // extracts the VALUES for each instance of this selector
            attributeValues.add(headline);
            log.trace(headline);
        }

        log.info("Finished parsing. Found " + attributeValues.size() + " html elements.");

        return attributeValues;
    }
}
