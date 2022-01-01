package com.ust.wordmaster.controller;

import com.ust.wordmaster.BBCHeadlinesFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RangedController {

    private final BBCHeadlinesFacade facade;

    public RangedController(BBCHeadlinesFacade facade) {
        this.facade = facade;
    }

    @GetMapping("headlines")    // http://localhost:8080/headlines?website=bbc cnn rangeStart=1&rangeEnd=5000
    public RangedTextResponseDTO getRangedHeadlines(@RequestParam(defaultValue = "bbc") String website,
                                                    @RequestParam(defaultValue = "1") int rangeStart,
                                                    @RequestParam(defaultValue = "5000") int rangeEnd) {

        log.info("---------> headlines method called with website=" + website + ", rangeStart=" + rangeStart + ", rangeEnd=" + rangeEnd);

        RangedTextResponseDTO dto = this.facade.fetchAndParseHeadlines(website.toLowerCase(), rangeStart, rangeEnd);

        return dto;
    }


/*

    @GetMapping("rangestring")
    public String getRangedTexts() {
        log.info("----------> Contoller called - this will return ERROR as content-type returned is text, " +
                "and not json as @RestController suggests");
        return "Hello again, ranged controller here";
    }
*/

    /*@GetMapping("range")
    public RangedTextResponseDTO getRangedTexts2() {
        log.info("----------> Controller called with RangedTextResponseDTO");
        RangedText5000 rt1 = new RangedText5000("Fauci goes to fucking prison", 0, 5000);
        rt1.setOutOfRangeWords(new String[]{"Fauci", "fucking"});
        RangedText5000 rt2 = new RangedText5000("Lombardy wins again", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Lombardy"});
        RangedText5000 rt3 = new RangedText5000("Perpedicular has been stupendous", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Perpedicular", "stupendous"});

        RangedTextResponseDTO reponseObj = new RangedTextResponseDTO(List.of(rt1,rt2,rt3));

        return reponseObj;
        return null;
    }
    */

/*
    @GetMapping("worddata")
    public WordData5000 getWordData() {
        WordData5000 wordData5000 = new WordData5000("dance", 80, "v", 12000, 50.0);
        WordData5000 wordData5000_2 = new WordData5000("dance", 80, "v", 12000, 50.0);

        log.info("---------->Controller called");
        return wordData5000;
    }*/


}
