package com.ust.wordmaster.headline;

import com.ust.wordmaster.service.range.RangedText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class HeadlineController {

    private final HeadlineFacade facade;
    private final HeadlineMapper mapper;

    public HeadlineController(HeadlineFacade facade, HeadlineMapper mapper) {
        this.facade = facade;
        this.mapper = mapper;
    }

    @GetMapping("headlines")    // http://localhost:8080/headlines?website=bbc&rangeStart=1&rangeEnd=5000
    public HeadlineDTO getRangedHeadlines(@RequestParam(defaultValue = "1") int rangeStart,
                                          @RequestParam(defaultValue = "5000") int rangeEnd,
                                          @RequestParam(defaultValue = "bbc") String websiteName) {

        log.info("---------> @GetMapping(\"headlines\") with website=" + websiteName + ", rangeStart=" + rangeStart + ", rangeEnd=" + rangeEnd);
        List<RangedText> rangedTexts = this.facade.processHeadlinesFromServer(rangeStart, rangeEnd, websiteName);

        log.info("---------> Mapping headlines to JSON");
        return mapper.toHeadlineDTO(rangeStart, rangeEnd, websiteName, rangedTexts);
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
    public HeadlineDTO getRangedTexts2() {
        log.info("----------> Controller called with HeadlineDTO");
        RangedText5000 rt1 = new RangedText5000("Fauci goes to fucking prison", 0, 5000);
        rt1.setOutOfRangeWords(new String[]{"Fauci", "fucking"});
        RangedText5000 rt2 = new RangedText5000("Lombardy wins again", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Lombardy"});
        RangedText5000 rt3 = new RangedText5000("Perpedicular has been stupendous", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Perpedicular", "stupendous"});

        HeadlineDTO reponseObj = new HeadlineDTO(List.of(rt1,rt2,rt3));

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
