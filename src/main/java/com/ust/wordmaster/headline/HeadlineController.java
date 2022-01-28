package com.ust.wordmaster.headline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HeadlineController {

    private final HeadlineFacade facade;

    public HeadlineController(HeadlineFacade facade) {
        this.facade = facade;
    }

    @GetMapping("headlines")    // http://localhost:8080/headlines?website=bbc cnn rangeStart=1&rangeEnd=5000
    public HeadlineResponseDTO getRangedHeadlines(@RequestParam(defaultValue = "bbc") String website,
                                                  @RequestParam(defaultValue = "1") int rangeStart,
                                                  @RequestParam(defaultValue = "5000") int rangeEnd) {

        log.info("---------> @GetMapping(\"headlines\") with website=" + website + ", rangeStart=" + rangeStart + ", rangeEnd=" + rangeEnd);

        return this.facade.processHeadlines(website, rangeStart, rangeEnd);
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
    public HeadlineResponseDTO getRangedTexts2() {
        log.info("----------> Controller called with HeadlineResponseDTO");
        RangedText5000 rt1 = new RangedText5000("Fauci goes to fucking prison", 0, 5000);
        rt1.setOutOfRangeWords(new String[]{"Fauci", "fucking"});
        RangedText5000 rt2 = new RangedText5000("Lombardy wins again", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Lombardy"});
        RangedText5000 rt3 = new RangedText5000("Perpedicular has been stupendous", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Perpedicular", "stupendous"});

        HeadlineResponseDTO reponseObj = new HeadlineResponseDTO(List.of(rt1,rt2,rt3));

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
