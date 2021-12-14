package com.ust.wordmaster.service.filtering;

import com.ust.wordmaster.dictionary.CorpusDictionary;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Map.entry;

@Slf4j
@Service
public class FilteringServiceImpl implements FilteringService {

    private static final Set<String> SHORT_FORMS = Set.of("i'd", "he'd", "she'd", "we'd", "you'd", "they'd",
            "i'm", "he's", "she's", "it's", "we're", "you're", "they're",
            "I'll", "he'll", "she'll", "it'll", "we'll", "you'll", "they'll",
            "there's", "there're", "there'd", "there'll",
            "ain't", "gonna");

    private static final char[] UNWANTED_CHARS = new char[]{'/', '\\', '\'', '.', ',', ':', ';', '"', '?', '!', '@',
            '#', '$', '*', '(', ')', '{', '}', '[', ']', '…', '-'};

    private static final Map<String, String> SEARCH_REPLACEMENTS = Map.ofEntries(
            entry("am", "be"), entry("are", "be"),
            entry("is", "be"),
            entry("was", "be"),
            entry("were", "be"),
            entry("has", "have"),
            entry("had", "have"),

            entry("an", "a"),

            entry("aren't", "n't"),
            entry("isn't", "n't"),
            entry("don't", "n't"),
            entry("doesn't", "n't"),
            entry("wasn't", "n't"),
            entry("weren't", "n't"),
            entry("haven't", "n't"),
            entry("hasn't", "n't"),
            entry("hadn't", "n't"),
            entry("won't", "n't"),
            entry("wouldn't", "n't"),
            entry("can't", "n't"),
            entry("couldn't", "n't"),
            entry("shan't", "n't"),
            entry("shouldn't", "n't"),

            entry("children", "child"),
            entry("grandchildren", "grandchild"),
            entry("mice", "mouse"),
            entry("wives", "wife"),
            entry("wolves", "wolf"),
            entry("knives", "knife"),
            entry("halves", "half"),
            entry("selves", "self"),
            entry("feet", "foot"),
            entry("teeth", "tooth"),
            entry("men", "man"),
            entry("women", "woman"),
            entry("lying", "lie"),

            entry("metre", "meter"),
            entry("theatre", "theater")
    );
    private final CorpusDictionary dictionary;
    //todo remove later?!
    @Getter
    private List<String> wordsOutOfRangeStrings = new ArrayList<>();

    public FilteringServiceImpl(CorpusDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public List<FilteredHeadline> createFilteredHeadlines(List<String> headlinesList, int rangeStart, int rangeEnd) {
        CorpusDictionary.validateRange(rangeStart, rangeEnd);
        Objects.requireNonNull(headlinesList, "List of headlines cannot be null");
        log.info("Filtering " + headlinesList.size() + " headlines; range " + rangeStart + "-" + rangeEnd);

        List<FilteredHeadline> filteredHeadlines = new ArrayList<>();

        for (String headlineAsString : headlinesList) {
            //create HEadline
            String[] words = splitHeadlineIntoWords(headlineAsString);
            words = cleanUpHeadlineWordsToCreateValidHeadlineObj(words);
            Headline headline = new Headline(headlineAsString, words);

            // create FilteredHeadline
            int[] wordIndexes = getOutOfRangeWords(words, rangeStart, rangeEnd);
            FilteredHeadline filteredHeadline = new FilteredHeadline(headline, wordIndexes, new int[]{rangeStart, rangeEnd});
            filteredHeadlines.add(filteredHeadline);

            log.info(wordIndexes.length + " out of range words (" + Arrays.toString(wordIndexes) + ") in: " + headlineAsString);
        }

        return filteredHeadlines;
    }

    private String[] splitHeadlineIntoWords(final String headlineStr) {
        log.info("Splitting headline: " + headlineStr);

        if (headlineStr == null || headlineStr.isBlank() || headlineStr.isEmpty())
            return new String[0];
        else
            return headlineStr.split(" ");

    }

    private int[] getOutOfRangeWords(String[] words, int rangeStart, int rangeEnd) {

        //PRECONDITION: String[] words doesn't to contain smpty or blank strings. Otherwise test will fail

        CorpusDictionary.validateRange(rangeStart, rangeEnd);

        List<Integer> outOfRangeWordIndexes = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String info = " is";
            String word = words[i];
            if (word.isEmpty() || word.isBlank()) {
                throw new IllegalArgumentException("The word cannot be blank or empty.");
            }

            if (!isInDictionary(word, rangeStart, rangeEnd)) {
                outOfRangeWordIndexes.add(i);
                info += " NOT";

                //------ to be removed ?! ---------
                wordsOutOfRangeStrings.add(words[i]);
            }

            log.info(words[i] + " [i]=" + i + info + " in range " + rangeStart + "-" + rangeEnd + ": ");
        }

        return outOfRangeWordIndexes.stream().mapToInt(i -> i).toArray();
    }


    /**
     * This methods is heavily dependent on Corpus Dictionary's structure and contents.
     * these are my search optimalisations and simplifications
     */
    private boolean isInDictionary(String word, int rangeStart, int rangeEnd) {

        String key = word.toLowerCase();
        // for each word, the replacement hashmap is created - not effective!
        key = replaceForSearch(key);

        // getSubset Dictionary
        boolean isIn = dictionary.containsWord(key, rangeStart, rangeEnd);

        ////////////////// -(e)d //////////////////////////////
        if (!isIn) {
            // try if removing -d helps
            if (key.length() >= 4 && key.substring(key.length() - 1).equals("d")) {
                String withoutD = key.substring(0, key.length() - 1);
                isIn = dictionary.containsWord(withoutD, rangeStart, rangeEnd);

                // try if removing -ed helps
                if (!isIn && key.length() >= 3 && key.substring(key.length() - 2).equals("ed")) {
                    String withoutED = key.substring(0, key.length() - 2);
                    isIn = dictionary.containsWord(withoutED, rangeStart, rangeEnd);
                }
                // try if removing -ied helps
                if (!isIn && key.length() >= 4 && key.substring(key.length() - 3).equals("ied")) {
                    String withoutIES = key.substring(0, key.length() - 3) + "y";
                    isIn = dictionary.containsWord(withoutIES, rangeStart, rangeEnd);
                }
            }

            //////////// -s /////////////////////////////////////////
            // try if removing -s helps
            if (!isIn && key.length() >= 3 && key.charAt(key.length() - 1) == 's') {
                String withoutS = key.substring(0, key.length() - 1);
                isIn = dictionary.containsWord(withoutS, rangeStart, rangeEnd);

                // try if removing -ed helps
                if (!isIn && key.length() >= 3 && key.substring(key.length() - 2).equals("es")) {
                    String withoutES = key.substring(0, key.length() - 2);
                    isIn = dictionary.containsWord(withoutES, rangeStart, rangeEnd);
                }

                // try if removing -ies helps
                if (!isIn && key.length() >= 4 && key.substring(key.length() - 3).equals("ies")) {
                    String withoutIES = key.substring(0, key.length() - 3) + "y";
                    isIn = dictionary.containsWord(withoutIES, rangeStart, rangeEnd);
                }
            }

            //////////// -ING ////////////////////
            // try if removing -ing helps
            if (!isIn && key.length() >= 4 && key.substring(key.length() - 3).equals("ing")) {
                String withoutING = key.substring(0, key.length() - 3);
                isIn = dictionary.containsWord(withoutING, rangeStart, rangeEnd);

                if (!isIn) {  // taking
                    withoutING += "e";
                    isIn = dictionary.containsWord(withoutING, rangeStart, rangeEnd);
                }

                if (!isIn) {  // sitting
                    withoutING = withoutING = key.substring(0, key.length() - 4); // ting
                    isIn = dictionary.containsWord(withoutING, rangeStart, rangeEnd);
                }

            }

            // try if removing -est helps
            if (!isIn && key.length() >= 4 && key.substring(key.length() - 3).equals("est")) {
                String withoutEST = key.substring(0, key.length() - 3);
                isIn = dictionary.containsWord(withoutEST, rangeStart, rangeEnd);
            }

            // all short forms with 'd (would/had)  & 's (has/is)  & 'm (am) & 're (are) will be considered as present
            // in each range, so effectively in range 0-1
            if (!isIn && key.contains("'")) {

                if (SHORT_FORMS.contains(key)) {
                    isIn = true;
                }
            }

        }

        return isIn;
    }


    private String replaceForSearch(final String word) {
        return SEARCH_REPLACEMENTS.getOrDefault(word.toLowerCase(), word);
    }


    ///////////// word preparation methods ////////////////

    /**
     * Makes some cleanup operations to create valid elements of a Haadline
     * (removes blanks, empty strings, trims, removes special characters glued to the words
     */
    private String[] cleanUpHeadlineWordsToCreateValidHeadlineObj(final String[] words) {

        // we actually create NEW List/array
        List<String> cleanedUpHeadline = new ArrayList<>();
        for (String word : words) {

            if (word == null || word.isEmpty() || word.isBlank())
                continue;

            word = word.trim();

            if (SHORT_FORMS.contains(word) || word.length() == 1) {
                cleanedUpHeadline.add(word);
                continue;
            }

            // passed for further processing

            // this will remove the 'd 's ''ll part on words not identified as typical short forms, eg
            // boy's , girl'd, dog'll etc.
            word = removeShortFormSuffixesAndPossesive(word);

            // hobbies: -> hobbies
            // you?! -> you
            // [(stuff)] -> stuff
            word = removeLeadingSpecialChars(word);
            word = removeTrailingSpecialChars(word);

            cleanedUpHeadline.add(word);
        }
        return cleanedUpHeadline.toArray(new String[0]);
    }

    private String removeShortFormSuffixesAndPossesive(String word) {

        if (word.length() >= 3 && word.contains("'d") || word.contains("'s")) {
            return word.substring(0, word.length() - 2);
        }

        if (word.length() >= 4 && word.contains("'ll")) {
            return word.substring(0, word.length() - 3);
        }

        return word;
    }

    private String removeTrailingSpecialChars(String word) {
        boolean removedLetter = true;
        do {
            char lastChar = word.charAt(word.length() - 1);
            //test if letter equal to unwanted
            for (char unwanted : UNWANTED_CHARS) {
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

    private String removeLeadingSpecialChars(String word) {
        // remove leading unwanted chars
        boolean removedLetter = true;
        do {
            char firstChar = word.charAt(0);

            for (char unwanted : UNWANTED_CHARS) {
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
