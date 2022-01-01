package com.ust.wordmaster.service.analysing;

import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import com.ust.wordmaster.dictionary.WordData5000;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.util.Map.entry;

@Slf4j
//@Service
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
            entry("are", "be"),
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
            entry("v", "vs"),
            entry("lying", "lie")
    );

    private CorpusDictionary corpusDictionary;

    public RangeAnalyser5000(CorpusDictionary corpusDictionary) {
        this.corpusDictionary = corpusDictionary;
    }

    @Override
    public List<RangedText> findOutOfRangeWords(List<String> charSequences, int rangeStart, int rangeEnd) {
        validateRange(rangeStart, rangeEnd);

        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);
        List<RangedText> rangedTextList = new ArrayList<>();

        for (String str : charSequences) {

            String[] words = splitOnSpaces(str);
            words = cleanUp(words);
            int[] wordIndexes = isolateOutOfRangeWords(words, rangeStart, rangeEnd);
            String[] outOfRangeWords = convertIndexesToWords(wordIndexes, words);

            RangedText rangedText = new RangedText5000(str, rangeStart, rangeEnd);
            rangedText.setOutOfRangeWords(outOfRangeWords);
            rangedTextList.add(rangedText);

            log.trace(wordIndexes.length + " out of range words (" + Arrays.toString(wordIndexes) + ") in: " + str);
        }

        return rangedTextList;
    }

    private void validateRange(int rangeStart, int rangeEnd) {
        if (rangeStart < 0 || rangeStart >= rangeEnd)
            throw new IllegalArgumentException("Range start must be greater than 0 and less than range end.");
    }

    /**
     * @return indexes of words that are not in the given range
     */
    private int[] isolateOutOfRangeWords(String[] words, int rangeStart, int rangeEnd) {

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
            }

            log.trace(words[i] + " [i]=" + i + info + " in the given range.");
        }

        return outOfRangeWordIndexes.stream().mapToInt(i -> i).toArray();
    }

    private boolean isAnyEntryInRange(String headword, int rangeStart, int rangeEnd) {
        validateRange(rangeStart, rangeEnd);

        List<DictionaryEntry> entries = this.corpusDictionary.getEntriesByHeadword(headword);
        return entries.stream()
                .map(entry -> ((WordData5000) entry.getWordData()).getRank())
                .anyMatch(rank -> rank >= rangeStart && rank <= rangeEnd);

    }

    private boolean isInDictionary(final String headword, int rangeStart, int rangeEnd) {

        String lowerCaseHeadword = headword.toLowerCase();
        lowerCaseHeadword = replaceWithBaseForm(lowerCaseHeadword);

        boolean isInRange = isAnyEntryInRange(lowerCaseHeadword, rangeStart, rangeEnd);

        if (!isInRange) {
            // SIMPLIFICATION: all short forms with 'd (would/had)  & 's (has/is)  & 'm (am) & 're (are)
            // will be considered as present in range 0-100
            isInRange = searchInShortForms(lowerCaseHeadword);

            if (!isInRange)
                isInRange = searchWithout_S_suffix(lowerCaseHeadword, rangeStart, rangeEnd);

            if (!isInRange)
                isInRange = searchWithout_ED_suffix(lowerCaseHeadword, rangeStart, rangeEnd);

            if (!isInRange)
                isInRange = searchWithout_ING_suffix(lowerCaseHeadword, rangeStart, rangeEnd);

            if (!isInRange)
                isInRange = searchWithout_EST_suffix(lowerCaseHeadword, rangeStart, rangeEnd);

            if (!isInRange)
                isInRange = searchWithout_ER_suffix(lowerCaseHeadword, rangeStart, rangeEnd);

        }

        return isInRange;
    }

    private String[] convertIndexesToWords(int[] wordIndexes, String[] words) {

        return Arrays.stream(wordIndexes)
                .mapToObj(i -> words[i])
                .toArray(String[]::new);

    }

    private String[] splitOnSpaces(final String str) {
        log.trace("Splitting: " + str);

        if (str == null || str.isBlank() || str.isEmpty())
            return new String[0];
        else
            return str.split(" ");

    }

    ///////////// search optimisation methods ////////////////
    private boolean searchInShortForms(String key) {
        return key.contains("'") && SHORT_FORMS.contains(key);
    }

    private boolean searchWithout_EST_suffix(String headword, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (headword.length() >= 4 && headword.endsWith("st")) {
            String withoutST = headword.substring(0, headword.length() - 2);
            isInRange = isAnyEntryInRange(withoutST, rangeStart, rangeEnd);

            if (!isInRange && headword.length() >= 5 && headword.endsWith("est")) {
                String withoutEST = headword.substring(0, headword.length() - 3);
                isInRange = isAnyEntryInRange(withoutEST, rangeStart, rangeEnd);
            }

            if (!isInRange && headword.length() >= 7 && headword.endsWith("est")) { //big-gest
                String withoutXEST = headword.substring(0, headword.length() - 4);
                isInRange = isAnyEntryInRange(withoutXEST, rangeStart, rangeEnd);
            }


        }
        return isInRange;
    }

    private boolean searchWithout_ER_suffix(String headword, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (headword.length() >= 5 && headword.endsWith("er")) {
            String withoutER = headword.substring(0, headword.length() - 2);
            isInRange = isAnyEntryInRange(withoutER, rangeStart, rangeEnd);

            if (!isInRange) {
                String withoutR = headword.substring(0, headword.length() - 1); //  large-r,
                isInRange = isAnyEntryInRange(withoutR, rangeStart, rangeEnd);
            }

            if (!isInRange) {
                String withoutXER = headword.substring(0, headword.length() - 3); //  big-ger,
                isInRange = isAnyEntryInRange(withoutXER, rangeStart, rangeEnd);
            }

            if (!isInRange && headword.endsWith("ier")) {
                String withoutIER = headword.substring(0, headword.length() - 3) + "y"; // crazy-> craz-ier big-ger,
                isInRange = isAnyEntryInRange(withoutIER, rangeStart, rangeEnd);
            }


        }
        return isInRange;
    }

    private boolean searchWithout_ING_suffix(String headword, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (headword.length() >= 4 && headword.endsWith("ing")) {
            String withoutING = headword.substring(0, headword.length() - 3);
            isInRange = isAnyEntryInRange(withoutING, rangeStart, rangeEnd);

            if (!isInRange) {  // taking
                withoutING += "e";
                isInRange = isAnyEntryInRange(withoutING, rangeStart, rangeEnd);
            }

            if (!isInRange) {  // sitting
                withoutING = headword.substring(0, headword.length() - 4); // ting
                isInRange = isAnyEntryInRange(withoutING, rangeStart, rangeEnd);
            }

        }
        return isInRange;
    }

    private boolean searchWithout_S_suffix(String headword, int rangeStart, int rangeEnd) {

        boolean isInRange = false;
        if (headword.length() >= 3 && headword.endsWith("s")) {
            String withoutS = headword.substring(0, headword.length() - 1);
            isInRange = isAnyEntryInRange(withoutS, rangeStart, rangeEnd);

            // try if removing -ed helps
            if (!isInRange && headword.endsWith("es")) {
                String withoutES = headword.substring(0, headword.length() - 2);
                isInRange = isAnyEntryInRange(withoutES, rangeStart, rangeEnd);
            }

            // try if removing -ies helps
            if (!isInRange && headword.length() >= 4 && headword.endsWith("ies")) {
                String withoutIES = headword.substring(0, headword.length() - 3) + "y";
                isInRange = isAnyEntryInRange(withoutIES, rangeStart, rangeEnd);
            }
        }
        return isInRange;
    }

    private boolean searchWithout_ED_suffix(String headword, int rangeStart, int rangeEnd) {
        // try if removing -d helps
        boolean isInRange = false;
        if (headword.length() >= 4 && headword.endsWith("d")) {
            String withoutD = headword.substring(0, headword.length() - 1);
            isInRange = isAnyEntryInRange(withoutD, rangeStart, rangeEnd);

            // try if removing -ed helps
            if (!isInRange && headword.endsWith("ed")) {
                String withoutED = headword.substring(0, headword.length() - 2);
                isInRange = isAnyEntryInRange(withoutED, rangeStart, rangeEnd);
            }
            // try if removing -ied helps
            if (!isInRange && headword.endsWith("ied")) {
                String withoutIED = headword.substring(0, headword.length() - 3) + "y";
                isInRange = isAnyEntryInRange(withoutIED, rangeStart, rangeEnd);
            }
        }
        return isInRange;
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
                // *(&
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

    private String removeTrailingSpecialChars(final String headword) {
        String word = headword;
        boolean letterHasBeenRemoved = true;
        do {
            if (word.isEmpty()) {
                return headword; //  word-with-only-unwanted-chars is returned unchanged
            }
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

    private String removeLeadingSpecialChars(final String headword) {

        String word = headword;

        boolean charHasBeenRemoved = true;
        do {

            if (word.isEmpty()) {
                return headword; //  word-with-only-unwanted-chars is returned unchanged
            }
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
