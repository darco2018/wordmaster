package com.ust.wordmaster.controller;

import com.ust.wordmaster.service.analysing.RangedText;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@Data
public class RangedTextResponseDTO {

    private String version;
    private String description;
    private int size;
    private List<RangedText> rangedTextList;

    public RangedTextResponseDTO(final List<RangedText> rangedTextList) {
        Objects.requireNonNull(rangedTextList, "List of ranged texts cannot be null");
        version = "1.0";
        description = "Mock version of DTO";
        this.rangedTextList = rangedTextList;
        this.size = this.rangedTextList.size();
    }

    public RangedTextResponseDTO map() {

        skipFirstHeadlineBBC();
        skipNumbersInOutOfRangeWords();
        return this;
    }

    private void skipNumbersInOutOfRangeWords() {

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        for (RangedText rangedText : this.rangedTextList) {
            List<String> withoutNumeric = new ArrayList<>();
            for (String word : rangedText.getOutOfRangeWords()) {
                if (!isNumeric(word, pattern) &&
                        !isCommonGeographicTerm(word) &&
                        !isCurrentlyCommon(word)) {
                    withoutNumeric.add(word);
                }

            }
            rangedText.setOutOfRangeWords(withoutNumeric.toArray(new String[0]));
        }
    }

    private boolean isCommonGeographicTerm(String str) {
        Set<String> commmonGeographicalTerms = Set.of("UK", "England", "English", "Europe", "European", "Africa",
                "African", "Asia", "Asian", "Australia", "Australian", "California", "Antarctica",
                "China", "France", "India", "Hong", "Kong");
        return commmonGeographicalTerms.contains(str);
    }

    private boolean isCurrentlyCommon(String str) {
        Set<String> common = Set.of("covid", "omicron", "delta", "covid-19", "boris", "johnson",
                "joe", "biden", "messi");
        return common.contains(str.toLowerCase());
    }

    private boolean isNumeric(String strNum, Pattern pattern) {
        if (strNum == null) {
            return false;
        }
        return (strNum.contains(",")) || strNum.contains(" £") || strNum.contains("$") || pattern.matcher(strNum).matches();
    }

    // Firs for BBC is always: text: "id-cta-sign-in",
    private void skipFirstHeadlineBBC() {
        this.rangedTextList = this.rangedTextList.subList(1, this.rangedTextList.size());
    }
}
