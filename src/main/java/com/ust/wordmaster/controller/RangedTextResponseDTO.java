package com.ust.wordmaster.controller;

import com.ust.wordmaster.service.analysing.RangedText;
import lombok.Data;

import java.util.List;

@Data
public class RangedTextResponseDTO {

    private String version;
    private String description;
    private int size;
    private List<RangedText> rangedTextList;

    public RangedTextResponseDTO(List<RangedText> rangedTextList) {
        version = "1.0";
        description = "Mock version of DTO";
        this.rangedTextList = rangedTextList;
        this.size = this.rangedTextList.size();
    }

    public RangedTextResponseDTO map() {
        return this;
    }

    /*private List<RangedText> getMockList(){
        RangedText5000 rt1 = new RangedText5000("Fauci goes to fucking prison", 0, 5000);
        rt1.setOutOfRangeWords(new String[]{"Fauci", "fucking"});
        RangedText5000 rt2 = new RangedText5000("Lombardy wins again", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Lombardy"});
        RangedText5000 rt3 = new RangedText5000("Perpedicular has been stupendous", 0, 5000);
        rt2.setOutOfRangeWords(new String[]{"Perpedicular", "stupendous"});

        return List.of(rt1,rt2,rt3);
    }*/
}
