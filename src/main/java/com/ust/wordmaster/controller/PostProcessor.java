package com.ust.wordmaster.controller;

import com.ust.wordmaster.service.analysing.RangedText;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@NoArgsConstructor
public class PostProcessor {

    public static List<RangedText> postprocess(List<RangedText> list, String website) {

        if (website.equalsIgnoreCase("bbc"))
            skipFirstHeadlineBBC(list);

        skipNumbersInOutOfRangeWords(list);
        removeTitleCaseWords(list);

        return list;
    }

    private static void removeTitleCaseWords(List<RangedText> list) {
        // not implemented

    }

    /**
     * Remove BBC's first nonsense 'headline': "id-cta-sign-in"
     */
    private static void skipFirstHeadlineBBC(List<RangedText> list) {
        list.remove(0);
    }

    private static void skipNumbersInOutOfRangeWords(List<RangedText> list) {

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        for (RangedText rangedText : list) {
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

    private static boolean isCommonGeographicTerm(String str) {
        Set<String> commmonGeographicalTerms = Set.of("UK", "England", "English", "Europe", "European", "Africa",
                "African", "Asia", "Asian", "Australia", "Australian", "California", "Antarctica",
                "China", "France", "India", "Hong", "Kong");
        return commmonGeographicalTerms.contains(str);
    }

    private static boolean isCurrentlyCommon(String str) {
        Set<String> common = Set.of("covid", "omicron", "delta", "covid-19", "boris", "johnson",
                "joe", "biden", "messi");
        return common.contains(str.toLowerCase());
    }

    private static boolean isNumeric(String strNum, Pattern pattern) {
        if (strNum == null) {
            return false;
        }
        return (strNum.contains(",")) || strNum.contains(" £") || strNum.contains("$") || pattern.matcher(strNum).matches();
    }

}
