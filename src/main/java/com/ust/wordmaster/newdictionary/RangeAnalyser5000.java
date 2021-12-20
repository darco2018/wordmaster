package com.ust.wordmaster.newdictionary;

import com.ust.wordmaster.dictionaryOLD.CorpusDictionaryOLD;
import com.ust.wordmaster.dictionaryOLD.DictionaryEntryOLD;
import com.ust.wordmaster.service.filteringOLD.ParsedTextUnitOLD;
import com.ust.wordmaster.service.filteringOLD.TextUnitOLD;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Map.entry;

@Slf4j
@Service
public class RangeAnalyser5000 implements RangeAnalyser{

    private final CorpusDictionary corpusDictionary;

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

    public RangeAnalyser5000(CorpusDictionary corpusDictionary) {
        this.corpusDictionary = corpusDictionary;
    }

    @Override
    public List<RangedText> findOutOfRange(List<String> charSequences, int rangeStart, int rangeEnd) {
        if (rangeStart < 0 || rangeStart >= rangeEnd)
            throw new IllegalArgumentException("Range start must be greater than 0 and less than range end.");
                        //CorpusDictionaryOLD.validateRange(rangeStart, rangeEnd);
        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);
        List<RangedText> rangedTextList = new ArrayList<>();
      //  NavigableSet<DictionaryEntryOLD> subsetDictionary = corpusDictionary.getDictionarySubset(rangeStart, rangeEnd);

        for (String str : charSequences) {

            String[] words = splitOnSpaces(str);
            words = cleanUpWordsToCreateValidTextUnitObjs(words);
            RangedText rangedText = new RangedText5000(str, rangeStart, rangeEnd);

            int[] wordIndexes = isolateOutOfRangeWords(words, this.corpusDictionary);
            String[] outOfRangeWords = convertIndexesToWords(wordIndexes, words);
            rangedText.setOutOfRangeWords(outOfRangeWords);

            rangedTextList.add(rangedText);

            log.info(wordIndexes.length + " out of range words (" + Arrays.toString(wordIndexes) + ") in: " + str);
        }

        return rangedTextList;
    }

    private String[] convertIndexesToWords(int[] wordIndexes, String[] words) {
        List<String> outOfRangeStrings = new ArrayList<>();
        for(int index : wordIndexes){
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

    private int[] isolateOutOfRangeWords(String[] words, CorpusDictionary  corpus) {

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

                //------ to be removed ?! ---------
               // wordsOutOfRangeStrings.add(words[i]);
            }

            log.info(words[i] + " [i]=" + i + info + " in the given range.");
        }

        return outOfRangeWordIndexes.stream().mapToInt(i -> i).toArray();
    }


    /**
     * This methods is heavily dependent on Corpus Dictionary's structure and contents.
     * these are my search optimalisations and simplifications
     */
    private boolean isInDictionary(String word) {

        /////////////// SEARCH ALGORITHM STARTS HERE ////////////////////////
        String key = word.toLowerCase();
        key = replaceWithBaseForm(key);

        // initial search
        boolean found = this.corpusDictionary.containsHeadword(key);
        // apply further rules to find the key
        if (!found) {
            // all short forms with 'd (would/had)  & 's (has/is)  & 'm (am) & 're (are) will be considered as present
            // in each range, so effectively in range 0-1
            found = searchInShortForms(key);

            if (!found)
                found = searchWithout_S_suffix(key);

            if (!found)
                found = searchWithout_ED_suffix(key);

            if (!found)
                found = searchWithout_ING_suffix(key);

            if (!found)
                found = searchWithout_EST_suffix(key);

        }

        return found;
    }

    private String replaceWithBaseForm(final String word) {
        return SEARCH_REPLACEMENTS.getOrDefault(word.toLowerCase(), word);
    }


    private boolean searchInShortForms(String key) {
        return key.contains("'") && SHORT_FORMS.contains(key);
    }

    private boolean searchWithout_EST_suffix(String key) {
        boolean found = false;
        if (key.length() >= 4 && key.endsWith("st")) {
            String withoutST = key.substring(0, key.length() - 2);
            log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + withoutST);
            found = this.corpusDictionary.containsHeadword(withoutST);

            if (!found && key.length() >= 5 && key.endsWith("est")) {
                String withoutEST = key.substring(0, key.length() - 3);
                found = this.corpusDictionary.containsHeadword(withoutEST);
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
