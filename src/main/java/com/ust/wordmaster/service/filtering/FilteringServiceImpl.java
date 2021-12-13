package com.ust.wordmaster.service.filtering;

import com.ust.wordmaster.dict2.CorpusDictionary2;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilteringServiceImpl implements FilteringService {

    private final CorpusDictionary2 dictionary;

    @Getter
    private List<String> wordsOutOfRangeStrings = new ArrayList<>();

    public FilteringServiceImpl(CorpusDictionary2 dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public List<FilteredHeadline> filter(List<String> headlinesStrings, int rangeStart, int rangeEnd) {
        log.info(">>>>>>>>>>>>> Filtering " + headlinesStrings.size() + " headlines; range(" + rangeStart + ", " + rangeEnd);

        List<FilteredHeadline> filteredHeadlinesList = new ArrayList<>();

        for (String headlineStr : headlinesStrings) {
            log.info("----------------> " + headlineStr + " <-----------------");
            String[] words = headlineStr.split(" ");

            words = cleanUp(words);
            Headline headline = new Headline(headlineStr, words);

            int[] wordIndexes = getOutOfRangeWords(words, rangeStart, rangeEnd);

            /*for(int i : wordIndexes){
                System.out.print(words[i] + ", ");
            }*/

            FilteredHeadline filteredHeadline = new FilteredHeadline(headline, wordIndexes, new int[]{rangeStart, rangeEnd});
            log.info("Found " + wordIndexes.length + " out of range words: " + Arrays.toString(wordIndexes));

            filteredHeadlinesList.add(filteredHeadline);
        }

        return filteredHeadlinesList;
    }

    private int[] getOutOfRangeWords(String[] words, int rangeStart, int rangeEnd) {

        //PRECONDITION: String[] words doesn't to contain smpty or blank strings. Otherwise test will fail
        List<Integer> outOfRangeWords = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String info = " is";
            boolean contains = isInDictionary(words[i], rangeStart, rangeEnd);
            if (!contains) {
                info += " NOT";
                outOfRangeWords.add(i);
                wordsOutOfRangeStrings.add(words[i]);
            }

            log.info(words[i] + " [i]=" + i + info + " in range " + rangeStart + "-" + rangeEnd + ": ");
        }

        return outOfRangeWords.stream().mapToInt(i -> i).toArray();
    }

    private boolean isInDictionary(String word, int rangeStart, int rangeEnd) {
        //log.info("Testing: " + word);
        word = word.toLowerCase();


        // from here on CorpusDictionary will create many times the same range of subdictionaries (NavigableSet views)
        // solution: set range only once in constructor & use a view, not a whole CorpusDictionary
        boolean isIn = dictionary.containsWord(word, rangeStart, rangeEnd);

        ////////////////// -(e)d //////////////////////////////
        if (!isIn) {
            // try if removing -d helps
            if (word.length() >= 4 && word.substring(word.length() - 1).equals("d")) {
                String withoutD = word.substring(0, word.length() - 1);
                isIn = dictionary.containsWord(withoutD, rangeStart, rangeEnd);

                // try if removing -ed helps
                if (!isIn && word.length() >= 3 && word.substring(word.length() - 2).equals("ed")) {
                    String withoutED = word.substring(0, word.length() - 2);
                    isIn = dictionary.containsWord(withoutED, rangeStart, rangeEnd);
                }
                // try if removing -ied helps
                if (!isIn && word.length() >= 4 && word.substring(word.length() - 3).equals("ied")) {
                    String withoutIES = word.substring(0, word.length() - 3) + "y";
                    isIn = dictionary.containsWord(withoutIES, rangeStart, rangeEnd);
                }
            }

            //////////// -s /////////////////////////////////////////
            // try if removing -s helps
            if (!isIn && word.length() >= 3 && word.charAt(word.length() - 1) == 's') {
                String withoutS = word.substring(0, word.length() - 1);
                isIn = dictionary.containsWord(withoutS, rangeStart, rangeEnd);

                // try if removing -ed helps
                if (!isIn && word.length() >= 3 && word.substring(word.length() - 2).equals("es")) {
                    String withoutES = word.substring(0, word.length() - 2);
                    isIn = dictionary.containsWord(withoutES, rangeStart, rangeEnd);
                }

                // try if removing -ies helps
                if (!isIn && word.length() >= 4 && word.substring(word.length() - 3).equals("ies")) {
                    String withoutIES = word.substring(0, word.length() - 3) + "y";
                    isIn = dictionary.containsWord(withoutIES, rangeStart, rangeEnd);
                }
            }

            //////////// -ING ////////////////////
            // try if removing -ing helps
            if (!isIn && word.length() >= 4 && word.substring(word.length() - 3).equals("ing")) {
                String withoutING = word.substring(0, word.length() - 3);
                isIn = dictionary.containsWord(withoutING, rangeStart, rangeEnd);

                if (!isIn) {  // taking
                    withoutING += "e";
                    isIn = dictionary.containsWord(withoutING, rangeStart, rangeEnd);
                }

                if (!isIn) {  // sitting
                    withoutING = withoutING = word.substring(0, word.length() - 4); // ting
                    isIn = dictionary.containsWord(withoutING, rangeStart, rangeEnd);
                }

            }

            // try if removing -est helps
            if (!isIn && word.length() >= 4 && word.substring(word.length() - 3).equals("est")) {
                String withoutEST = word.substring(0, word.length() - 3);
                isIn = dictionary.containsWord(withoutEST, rangeStart, rangeEnd);
            }

            // all short forms with 'd (would/had)  & 's (has/is)  & 'm (am) & 're (are) will be considered as present
            // in each range, so effectively in range 0-1
            if (!isIn && word.contains("'")) {
                Set shortForms = Set.of("i'd", "he'd", "she'd", "we'd", "you'd", "they'd",
                        "i'm", "he's", "she's", "we're", "you're", "they're",
                        "there's", "there're",
                        "ain't", "gonna");
                if (shortForms.contains(word)) {
                    isIn = true;
                }
            }

        }

        return isIn;
    }

    private String[] cleanUp(final String[] words) {

        char[] unwantedChars = new char[]{'/', '\\', '\'', '.', ',', ':', ';', '"', '?', '!', '@', '#', '$', '*',
                '(', ')', '{', '}', '[', ']', '…', '-'};

        List<String> output = new ArrayList<>();
        for (String word : words) {
            word = word.trim();

            if (!word.isEmpty() && !word.isBlank()) {

                //todo
                if (word.equals("I'd"))
                    continue;

                if (word.length() == 1) {
                    if (word.equalsIgnoreCase("a") || word.equalsIgnoreCase("I"))
                        continue;
                } else {
                    word = removeShortForm(word);
                    word = removePossessive(word);
                    word = removeLeading(unwantedChars, word);
                    word = removeTrailing(unwantedChars, word);
                }

            }

            output.add(word);
        }
        return output.toArray(new String[0]);
    }

    private String removeShortForm(String word) {

        if (word.contains("'d") || word.contains("'s")) {
            return word.substring(0, word.length() - 2);
        }

        if (word.contains("'ll")) {
            return word.substring(0, word.length() - 3);
        }

        return word;
    }

    private String removePossessive(String word) {
        boolean possesive = word.toLowerCase().substring(word.length() - 2).equals("'s");
        return possesive ? word.substring(0, word.length() - 2) : word;
    }

    private String removeTrailing(char[] unwantedChars, String word) {
        boolean removedLetter = true;
        do {
            char lastChar = word.charAt(word.length() - 1);
            //test if letter equal to unwanted
            for (char unwanted : unwantedChars) {
                if (lastChar == unwanted) {
                    word = word.substring(0, word.length() - 1);
                    removedLetter = true; // continue while loop
                    break; // get out of for, set new lastChar
                } else {
                    removedLetter = false;
                }
            }
        } while (removedLetter);

        return word;
    }

    private String removeLeading(char[] unwantedChars, String word) {
        // remove leading unwanted chars
        boolean removedLetter = true;
        do {
            char firstChar = word.charAt(0);

            for (char unwanted : unwantedChars) {
                if (firstChar == unwanted) {
                    word = word.substring(1);
                    removedLetter = true;
                    break; // set new firstChar
                } else {
                    removedLetter = false;
                }
            }
        } while (removedLetter);

        return word;
    }

}
