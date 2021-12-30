package com.ust.wordmaster.controller;

import com.ust.wordmaster.dictionary.WordData5000;
import com.ust.wordmaster.service.analysing.RangedText5000;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class RangedController {

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
}
