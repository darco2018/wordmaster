package com.ust.wordmaster.newdictionary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Map.entry;

@Slf4j
@Service
public class RangeAnalyser5000 implements RangeAnalyser {

    private static final Set<String> SHORT_FORMS = Set.of("i'd", "he'd", "she'd", "we'd", "you'd", "they'd",
            "i'm", "he's", "she's", "it's", "we're", "you're", "they're",
            "I'll", "he'll", "she'll", "it'll", "we'll", "you'll", "they'll",
            "there's", "there're", "there'd", "there'll",
            "ain't", "gonna");
    private static final char[] UNWANTED_CHARS = new char[]{'/', '\\', '\'', '.', ',', ':', ';', '"', '?', '!', '@',
            '#', '$', '*', '(', ')', '{', '}', '[', ']', '…', '-'};
    private static final Map<String, String> BASE_FORMS = Map.ofEntries(
            entry("am", "be"),
            entry("is", "be"),
            entry("was", "be"),
            entry("were", "be"),
            entry("has", "have"),
            entry("had", "have"),

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

            entry("worse", "bad"),
            entry("worst", "bad"),

            entry("metre", "meter"),
            entry("theatre", "theater"),

            entry("an", "a"),
            entry("lying", "lie")
    );
    private final CorpusDictionary corpusDictionary;

    public RangeAnalyser5000(CorpusDictionary corpusDictionary) {
        this.corpusDictionary = corpusDictionary;
    }

    @Override
    public List<RangedText> findOutOfRangeWords(List<String> charSequences, int rangeStart, int rangeEnd) {
        if (rangeStart < 0 || rangeStart >= rangeEnd)
            throw new IllegalArgumentException("Range start must be greater than 0 and less than range end.");

        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);
        List<RangedText> rangedTextList = new ArrayList<>();

        for (String str : charSequences) {

            String[] words = splitOnSpaces(str);
            words = cleanUp(words);
            int[] wordIndexes = isolateOutOfRangeWords(words);
            String[] outOfRangeWords = convertIndexesToWords(wordIndexes, words);

            RangedText rangedText = new RangedText5000(str, rangeStart, rangeEnd);
            rangedText.setOutOfRangeWords(outOfRangeWords);
            rangedTextList.add(rangedText);

            log.info(wordIndexes.length + " out of range words (" + Arrays.toString(wordIndexes) + ") in: " + str);
        }

        return rangedTextList;
    }

    /**
     * @return indexes of words that are not in the given range
     */
    private int[] isolateOutOfRangeWords(String[] words) {

        List<Integer> outOfRangeWordIndexes = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String info = " is";
            String word = words[i];
            if (word.isEmpty() || word.isBlank()) {
                throw new IllegalArgumentException("The word cannot be blank or empty.");
            }

            if (!isInDictionary(word)) {
                outOfRangeWordIndexes.add(i);
                info += " NOT";
            }

            log.info(words[i] + " [i]=" + i + info + " in the given range.");
        }

        return outOfRangeWordIndexes.stream().mapToInt(i -> i).toArray();
    }

    private boolean isInDictionary(String word) {

        String key = word.toLowerCase();
        key = replaceWithBaseForm(key);

        boolean found = this.corpusDictionary.containsHeadword(key);

        if (!found) {
            // SIMPLIFICATION: all short forms with 'd (would/had)  & 's (has/is)  & 'm (am) & 're (are)
            // will be considered as present in range 0-100
            found = searchInShortForms(key);

            if (!found)
                found = searchWithout_S_suffix(key);

            if (!found)
                found = searchWithout_ED_suffix(key);

            if (!found)
                found = searchWithout_ING_suffix(key);

            if (!found)
                found = searchWithout_EST_suffix(key);

            if (!found)
                found = searchWithout_ER_suffix(key);

        }

        return found;
    }

    private String[] convertIndexesToWords(int[] wordIndexes, String[] words) {
        List<String> outOfRangeStrings = new ArrayList<>();
        for (int index : wordIndexes) {
            outOfRangeStrings.add(words[index]);
        }

        return outOfRangeStrings.toArray(new String[0]);
    }

    private String[] splitOnSpaces(final String str) {
        log.info("Splitting: " + str);

        if (str == null || str.isBlank() || str.isEmpty())
            return new String[0];
        else
            return str.split(" ");

    }

    ///////////// search optimisation methods ////////////////
    private boolean searchInShortForms(String key) {
        return key.contains("'") && SHORT_FORMS.contains(key);
    }

    private boolean searchWithout_EST_suffix(String key) {
        boolean found = false;
        if (key.length() >= 4 && key.endsWith("st")) {
            String withoutST = key.substring(0, key.length() - 2);
            found = this.corpusDictionary.containsHeadword(withoutST);

            if (!found && key.length() >= 5 && key.endsWith("est")) {
                String withoutEST = key.substring(0, key.length() - 3);
                found = this.corpusDictionary.containsHeadword(withoutEST);
            }

            if (!found && key.length() >= 7 && key.endsWith("est")) { //big-gest
                String withoutXEST = key.substring(0, key.length() - 4);
                found = this.corpusDictionary.containsHeadword(withoutXEST);
            }


        }
        return found;
    }

    private boolean searchWithout_ER_suffix(String key) {
        boolean found = false;
        if (key.length() >= 5 && key.endsWith("er")) {
            String withoutER = key.substring(0, key.length() - 2);
            found = this.corpusDictionary.containsHeadword(withoutER);

            if (!found) {
                String withoutR = key.substring(0, key.length() - 1); //  large-r,
                found = this.corpusDictionary.containsHeadword(withoutR);
            }

            if (!found) {
                String withoutXER = key.substring(0, key.length() - 3); //  big-ger,
                found = this.corpusDictionary.containsHeadword(withoutXER);
            }

            if (!found && key.endsWith("ier")) {
                String withoutIER = key.substring(0, key.length() - 3) + "y"; // crazy-> craz-ier big-ger,
                found = this.corpusDictionary.containsHeadword(withoutIER);
            }


        }
        return found;
    }

    private boolean searchWithout_ING_suffix(String key) {
        boolean found = false;
        if (key.length() >= 4 && key.endsWith("ing")) {
            String withoutING = key.substring(0, key.length() - 3);
            found = this.corpusDictionary.containsHeadword(withoutING);

            if (!found) {  // taking
                withoutING += "e";
                found = this.corpusDictionary.containsHeadword(withoutING);
            }

            if (!found) {  // sitting
                withoutING = key.substring(0, key.length() - 4); // ting
                found = this.corpusDictionary.containsHeadword(withoutING);
            }

        }
        return found;
    }

    private boolean searchWithout_S_suffix(String key) {

        boolean found = false;
        if (key.length() >= 3 && key.endsWith("s")) {
            String withoutS = key.substring(0, key.length() - 1);
            found = this.corpusDictionary.containsHeadword(withoutS);

            // try if removing -ed helps
            if (!found && key.endsWith("es")) {
                String withoutES = key.substring(0, key.length() - 2);
                found = this.corpusDictionary.containsHeadword(withoutES);
            }

            // try if removing -ies helps
            if (!found && key.length() >= 4 && key.endsWith("ies")) {
                String withoutIES = key.substring(0, key.length() - 3) + "y";
                found = this.corpusDictionary.containsHeadword(withoutIES);
            }
        }
        return found;
    }

    private boolean searchWithout_ED_suffix(String key) {
        // try if removing -d helps
        boolean found = false;
        if (key.length() >= 4 && key.endsWith("d")) {
            String withoutD = key.substring(0, key.length() - 1);
            found = this.corpusDictionary.containsHeadword(withoutD);

            // try if removing -ed helps
            if (!found && key.endsWith("ed")) {
                String withoutED = key.substring(0, key.length() - 2);
                found = this.corpusDictionary.containsHeadword(withoutED);
            }
            // try if removing -ied helps
            if (!found && key.endsWith("ied")) {
                String withoutIED = key.substring(0, key.length() - 3) + "y";
                found = this.corpusDictionary.containsHeadword(withoutIED);
            }
        }
        return found;
    }

    ///////////// word preparation methods ////////////////

    /**
     * Makes some cleanup operations to create valid words
     * (trim & remove blanks, empty strings, special characters glued to the words
     *
     * @return indexes of
     */

    private String[] cleanUp(final String[] words) {

        List<String> cleanedUpList = new ArrayList<>();
        for (String word : words) {

            if (word == null || word.isEmpty() || word.isBlank())
                continue;
            else
                word = word.trim();

            // don't clean up short forms or single chars
            if (!SHORT_FORMS.contains(word) && word.length() != 1) {
                word = removeShortFormSuffixesAndPossesive(word);
                word = removeLeadingSpecialChars(word);
                word = removeTrailingSpecialChars(word);
            }
            cleanedUpList.add(word);
        }
        return cleanedUpList.toArray(new String[0]);
    }

    private String replaceWithBaseForm(final String word) {
        return BASE_FORMS.getOrDefault(word.toLowerCase(), word);
    }

    private String removeShortFormSuffixesAndPossesive(String word) {

        if (word.length() >= 3 && word.endsWith("'d") || word.endsWith("'s")) {
            return word.substring(0, word.length() - 2);
        }

        if (word.length() >= 4 && word.endsWith("'ll")) {
            return word.substring(0, word.length() - 3);
        }

        return word;
    }

    private String removeTrailingSpecialChars(String word) {
        boolean letterHasBeenRemoved = true;
        do {
            char lastChar = word.charAt(word.length() - 1);

            for (char unwantedChar : UNWANTED_CHARS) {
                if (lastChar == unwantedChar) {
                    word = word.substring(0, word.length() - 1);
                    letterHasBeenRemoved = true;
                    break; // get out of for, set new lastChar & repeat test
                } else {
                    letterHasBeenRemoved = false;
                }
            }
        } while (letterHasBeenRemoved);

        return word;
    }

    private String removeLeadingSpecialChars(String word) {
        // remove leading unwanted chars
        boolean charHasBeenRemoved = true;
        do {
            char firstChar = word.charAt(0);

            for (char unwanted : UNWANTED_CHARS) {
                if (firstChar == unwanted) {
                    word = word.substring(1);
                    charHasBeenRemoved = true;
                    break; // set new firstChar
                } else {
                    charHasBeenRemoved = false;
                }
            }
        } while (charHasBeenRemoved);

        return word;
    }


}
