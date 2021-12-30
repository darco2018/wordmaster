package com.ust.wordmaster.controller;

import com.ust.wordmaster.service.analysing.RangedText;
import com.ust.wordmaster.service.analysing.RangedText5000;
import lombok.Data;

import java.util.List;

@Data
public class RangedTextResponseDTO {

    private String version;
    private String description;
    private List<RangedText> rangedTextList;

    public RangedTextResponseDTO(){
        version = "1.0";
        description = "Mock version of DTO";
        rangedTextList = getMockList();
    }

    private List<RangedText> getMockList(){
        RangedText5000 rt1 = new RangedText5000("Fauci goes to fucking prison", 0, 5000);
        rt1.setOutOfRangeWords(new String[]{"Fauci", "fucking"});
        RangedText5000 rt2 = new RangedText5000("Lombardy wins again", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Lombardy"});
        RangedText5000 rt3 = new RangedText5000("Perpedicular has been stupendous", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Perpedicular", "stupendous"});

        return List.of(rt1,rt2,rt3);
    }
}
