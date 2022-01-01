package com.ust.wordmaster.controller;

import com.ust.wordmaster.BBCHeadlinesFacade;
import com.ust.wordmaster.dictionary.WordData5000;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
public class RangedController {

    private BBCHeadlinesFacade facade;

    public RangedController(BBCHeadlinesFacade facade){
        this.facade = facade;
    }

    @GetMapping("rangestring")
    public String getRangedTexts() {
        log.info("----------> Contoller called - this will return ERROR as content-type returned is text, " +
                "and not json as @RestController suggests");
        return "Hello again, ranged controller here";
    }

    @GetMapping("range")
    public RangedTextResponseDTO getRangedTexts2() {
        RangedTextResponseDTO reponseObj = new RangedTextResponseDTO();

        log.info("----------> Controller called with RangedTextResponseDTO");
        return reponseObj;
    }

    @GetMapping("worddata")
    public WordData5000 getWordData() {
        WordData5000 wordData5000 = new WordData5000("dance",80,"v",12000,50.0);
        WordData5000 wordData5000_2 = new WordData5000("dance",80,"v",12000,50.0);

        log.info("---------->Controller called");
        return wordData5000;
    }

    @GetMapping("headlines")    // http://localhost:8080/headlines?website=bbc
    public void getRangedHeadlines(@RequestParam String website) {

        log.info("---------> headlines method called with website=" + website);

        Objects.requireNonNullElse(website,"");
        switch (website.toLowerCase()){
            case "bbc":
                this.facade.fetchAndParseHeadlines();
            case "cnn":
                this.facade.fetchAndParseHeadlines();
            default:
                this.facade.fetchAndParseHeadlines();
        }
    }
}
