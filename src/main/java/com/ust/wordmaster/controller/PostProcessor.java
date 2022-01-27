package com.ust.wordmaster.controller;

import com.ust.wordmaster.service.analysing.RangedText;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@NoArgsConstructor
public class PostProcessor {

    /**
     * Removes BBC's first nonsense 'headline': "id-cta-sign-in"
     */
    private static void skipFirstHeadlineBBC(List<RangedText> list) {
        list.remove(0);
    }

    private static void removeItemsFromOutOfRangeWords(List<RangedText> list) {

        for (RangedText rangedText : list) {
            List<String> noNumericsList = new ArrayList<>();

            for (String word : rangedText.getOutOfRangeWords()) {
                if (!isNumeric(word) && !isCurrentlyCommon(word) && !isTitleCase(word)) {
                    noNumericsList.add(word);
                }
            }
            rangedText.setOutOfRangeWords(noNumericsList.toArray(new String[0]));
        }
    }

    private static boolean isTitleCase(final String word) {
        return Character.isUpperCase(word.charAt(0));
    }

    private static boolean isCurrentlyCommon(String str) {
        Set<String> common = Set.of("covid", "omicron", "delta", "covid-19");
        return common.contains(str.toLowerCase());
    }

    private static boolean isNumeric(String strNum) {

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return (strNum.contains(",")) || strNum.contains(" £") || strNum.contains("$") || pattern.matcher(strNum).matches();
    }

    public void postProcess(List<RangedText> list, String website) {

        if (website.equalsIgnoreCase("bbc")) {
            skipFirstHeadlineBBC(list);
        }

        removeItemsFromOutOfRangeWords(list);
    }

}
