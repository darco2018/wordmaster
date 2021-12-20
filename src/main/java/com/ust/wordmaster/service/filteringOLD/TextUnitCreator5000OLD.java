package com.ust.wordmaster.service.filteringOLD;

import com.ust.wordmaster.dictionaryOLD.CorpusDictionaryOLD;
import com.ust.wordmaster.dictionaryOLD.CorpusDictionaryIntOLD;
import com.ust.wordmaster.dictionaryOLD.DictionaryEntryOLD;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Map.entry;

@Slf4j
@Service
public class TextUnitCreator5000OLD implements TextUnitsCreatorOLD {

    private static final Set<String> SHORT_FORMS = Set.of("i'd", "he'd", "she'd", "we'd", "you'd", "they'd",
            "i'm", "he's", "she's", "it's", "we're", "you're", "they're",
            "I'll", "he'll", "she'll", "it'll", "we'll", "you'll", "they'll",
            "there's", "there're", "there'd", "there'll",
            "ain't", "gonna");

    private static final char[] UNWANTED_CHARS = new char[]{'/', '\\', '\'', '.', ',', ':', ';', '"', '?', '!', '@',
            '#', '$', '*', '(', ')', '{', '}', '[', ']', '…', '-'};

    private static final Map<String, String> SEARCH_REPLACEMENTS = Map.ofEntries(
            entry("am", "be"),
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
    private final CorpusDictionaryIntOLD corpusDictionary;
    //todo remove later?!
    @Getter
    private final List<String> wordsOutOfRangeStrings = new ArrayList<>();

    public TextUnitCreator5000OLD(CorpusDictionaryIntOLD dictionary) {
        this.corpusDictionary = dictionary;
    }

    /**
     *
     * This class is must cooperate with some dictionary to provide info whether words are in/out of some range
     *
     * splits headline on spaces into words, then analyzes the words using CorpusDictionary, rangestart, rangeEnd,
     * then adds some the obtained info to each headline (eg words out of range).
     * the original headline + the array of out of range words + info about range  create TextUnit
     * Other possible names for this class: SentenceSplitter IntoWordsSplitter
     * or
     * WORD ANALYSER, WORD EXAMINER   RANGE ANALYSER
     *
     * I could also send here SENTENCES from an ordinary TEXT for analysis
     *
     * A TextUnit is a SENTENCE(headline) with INFO on its WORDS
     * INFORMED SENTENCE    RANGED SENTENCE    EXAMINED TEXTLINE    EXAMINED SENTENCE   RANGED TEXTLINE
     *
     * A RANGE ANALYZER producing RANGED SENTENCE
     *
     */
    @Override
    public List<ParsedTextUnitOLD> parseIntoTextUnits(List<String> charSequences, int rangeStart, int rangeEnd) {
        CorpusDictionaryOLD.validateRange(rangeStart, rangeEnd);
        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);

        List<ParsedTextUnitOLD> parsedTextUnits = new ArrayList<>();
        NavigableSet<DictionaryEntryOLD> subsetDictionary = corpusDictionary.getDictionarySubset(rangeStart, rangeEnd);

        for (String str : charSequences) {
            //create textUnits
            String[] words = splitOnSpaces(str);
            words = cleanUpWordsToCreateValidTextUnitObjs(words);
            TextUnitOLD textUnit = new TextUnitOLD(str, words);

            // create FilteredTextUnit
            int[] wordIndexes = isolateOutOfRangeWords(words, subsetDictionary);
            ParsedTextUnitOLD filteredTextUnit = new ParsedTextUnitOLD(textUnit, wordIndexes, new int[]{rangeStart, rangeEnd});
            parsedTextUnits.add(filteredTextUnit);

            log.info(wordIndexes.length + " out of range words (" + Arrays.toString(wordIndexes) + ") in: " + str);
        }

        return parsedTextUnits;
    }

    private String[] splitOnSpaces(final String str) {
        log.info("Splitting: " + str);

        if (str == null || str.isBlank() || str.isEmpty())
            return new String[0];
        else
            return str.split(" ");

    }

    private int[] isolateOutOfRangeWords(String[] words, NavigableSet<DictionaryEntryOLD> subsetDictionary) {

        List<Integer> outOfRangeWordIndexes = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String info = " is";
            String word = words[i];
            if (word.isEmpty() || word.isBlank()) {
                throw new IllegalArgumentException("The word cannot be blank or empty.");
            }

            if (!isInDictionary(word, subsetDictionary)) {
                outOfRangeWordIndexes.add(i);
                info += " NOT";

                //------ to be removed ?! ---------
                wordsOutOfRangeStrings.add(words[i]);
            }

            log.info(words[i] + " [i]=" + i + info + " in the given range.");
        }

        return outOfRangeWordIndexes.stream().mapToInt(i -> i).toArray();
    }


    /**
     * This methods is heavily dependent on Corpus Dictionary's structure and contents.
     * these are my search optimalisations and simplifications
     */
    private boolean isInDictionary(String word, NavigableSet<DictionaryEntryOLD> subsetDictionary) {

        /////////////// SEARCH ALGORITHM STARTS HERE ////////////////////////
        String key = word.toLowerCase();
        key = replaceWithBaseForm(key);

        // initial search
        boolean found = corpusDictionary.containsWord(key, subsetDictionary);

        // apply further rules to find the key
        if (!found) {
            // all short forms with 'd (would/had)  & 's (has/is)  & 'm (am) & 're (are) will be considered as present
            // in each range, so effectively in range 0-1
            found = searchInShortForms(key);

            if (!found)
                found = searchWithout_S_suffix(subsetDictionary, key);

            if (!found)
                found = searchWithout_ED_suffix(subsetDictionary, key);

            if (!found)
                found = searchWithout_ING_suffix(subsetDictionary, key);

            if (!found)
                found = searchWithout_EST_suffix(subsetDictionary, key);

        }

        return found;
    }

    private boolean searchInShortForms(String key) {
        return key.contains("'") && SHORT_FORMS.contains(key);
    }

    private boolean searchWithout_EST_suffix(NavigableSet<DictionaryEntryOLD> subsetDictionary, String key) {
        boolean found = false;
        if (key.length() >= 4 && key.endsWith("est")) {
            String withoutEST = key.substring(0, key.length() - 3);
            found = corpusDictionary.containsWord(withoutEST, subsetDictionary);
        }
        return found;
    }

    private boolean searchWithout_ING_suffix(NavigableSet<DictionaryEntryOLD> subsetDictionary, String key) {
        boolean found = false;
        if (key.length() >= 4 && key.endsWith("ing")) {
            String withoutING = key.substring(0, key.length() - 3);
            found = corpusDictionary.containsWord(withoutING, subsetDictionary);

            if (!found) {  // taking
                withoutING += "e";
                found = corpusDictionary.containsWord(withoutING, subsetDictionary);
            }

            if (!found) {  // sitting
                withoutING = key.substring(0, key.length() - 4); // ting
                found = corpusDictionary.containsWord(withoutING, subsetDictionary);
            }

        }
        return found;
    }

    private boolean searchWithout_S_suffix(NavigableSet<DictionaryEntryOLD> subsetDictionary, String key) {

        boolean found = false;
        if (key.length() >= 3 && key.endsWith("s")) {
            String withoutS = key.substring(0, key.length() - 1);
            found = corpusDictionary.containsWord(withoutS, subsetDictionary);

            // try if removing -ed helps
            if (!found && key.endsWith("es")) {
                String withoutES = key.substring(0, key.length() - 2);
                found = corpusDictionary.containsWord(withoutES, subsetDictionary);
            }

            // try if removing -ies helps
            if (!found && key.length() >= 4 && key.endsWith("ies")) {
                String withoutIES = key.substring(0, key.length() - 3) + "y";
                found = corpusDictionary.containsWord(withoutIES, subsetDictionary);
            }
        }
        return found;
    }

    private boolean searchWithout_ED_suffix(NavigableSet<DictionaryEntryOLD> subsetDictionary, String key) {
        // try if removing -d helps
        boolean found = false;
        if (key.length() >= 4 && key.endsWith("d")) {
            String withoutD = key.substring(0, key.length() - 1);
            found = corpusDictionary.containsWord(withoutD, subsetDictionary);

            // try if removing -ed helps
            if (!found && key.endsWith("ed")) {
                String withoutED = key.substring(0, key.length() - 2);
                found = corpusDictionary.containsWord(withoutED, subsetDictionary);
            }
            // try if removing -ied helps
            if (!found && key.endsWith("ied")) {
                String withoutIES = key.substring(0, key.length() - 3) + "y";
                found = corpusDictionary.containsWord(withoutIES, subsetDictionary);
            }
        }
        return found;
    }


    private String replaceWithBaseForm(final String word) {
        return SEARCH_REPLACEMENTS.getOrDefault(word.toLowerCase(), word);
    }


    ///////////// word preparation methods ////////////////

    /**
     * Makes some cleanup operations to create valid elements of a Haadline
     * (removes blanks, empty strings, trims, removes special characters glued to the words
     */
    private String[] cleanUpWordsToCreateValidTextUnitObjs(final String[] words) {

        // we actually create NEW List/array
        List<String> cleanedUpTextUnit = new ArrayList<>();
        for (String word : words) {

            if (word == null || word.isEmpty() || word.isBlank())
                continue;

            word = word.trim();

            if (SHORT_FORMS.contains(word) || word.length() == 1) {
                cleanedUpTextUnit.add(word);
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

            cleanedUpTextUnit.add(word);
        }
        return cleanedUpTextUnit.toArray(new String[0]);
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
