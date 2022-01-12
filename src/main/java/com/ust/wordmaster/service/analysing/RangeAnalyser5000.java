package com.ust.wordmaster.service.analysing;

import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import com.ust.wordmaster.dictionary.WordData5000;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private static final Map<String, String> NEGATIONS = Map.ofEntries(
            entry("aren't", "n't"),
            entry("isn't", "n't"),
            entry("wasn't", "n't"),
            entry("weren't", "n't"),
            entry("don't", "n't"),
            entry("doesn't", "n't"),
            entry("didn't", "n't"),
            entry("haven't", "have"),
            entry("hasn't", "have"),
            entry("hadn't", "have"),
            entry("won't", "will"),
            entry("wouldn't", "would"),
            entry("can't", "can"),
            entry("couldn't", "could"),
            entry("shan't", "shall"),
            entry("shouldn't", "should")
    );


    private static final Map<String, String> BASE_FORMS = Map.ofEntries(
            entry("am", "be"),
            entry("are", "be"),
            entry("is", "be"),
            entry("was", "be"),
            entry("were", "be"),
            entry("has", "have"),
            entry("had", "have"),

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


    private final IrregularVerbsConverter irregularVerbsConverter;

    private final CorpusDictionary corpusDictionary;

    public RangeAnalyser5000(CorpusDictionary corpusDictionary) {
        this.corpusDictionary = corpusDictionary;
        this.irregularVerbsConverter = new IrregularVerbsConverter();
    }

    /*@Override
    public List<RangedText> findOutOfRangeWords(List<String> charSequences, int rangeStart, int rangeEnd) {
        validateRange(rangeStart, rangeEnd);
        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);

        List<RangedText> rangedTextList = new ArrayList<>();

        for (String sequence : charSequences) {

            // split to get tokens, possibly words
            List<String> tokensInThisSequence = Arrays.asList(splitOnSpaces(sequence));
            // can still include "" or " "
            // do first check if in dictionary; remove tokens found in dictionary
            tokensInThisSequence = removeIfNullBlankEmpty(Collections.unmodifiableList(tokensInThisSequence));
            tokensInThisSequence = removeIfFoundCasePreserved(Collections.unmodifiableList(tokensInThisSequence));
            tokensInThisSequence = removeIfFoundCaseModified(Collections.unmodifiableList(tokensInThisSequence));

            tokensInThisSequence = processToDoFurtherCheckIfPresentInDictionary(Collections.unmodifiableList(tokensInThisSequence));

            //-------------------------
            int[] wordIndexes = isolateOutOfRangeWords(tokensInThisSequence.toArray(new String[0]), rangeStart, rangeEnd);
            String[] outOfRangeWords = convertIndexesToWords(wordIndexes, tokensInThisSequence.toArray(new String[0]));
            // ----------------------
            RangedText rangedText = new RangedText5000(sequence, rangeStart, rangeEnd);
            rangedText.setOutOfRangeWords(outOfRangeWords);
            rangedTextList.add(rangedText);

            log.trace(wordIndexes.length + " out of range words (" + Arrays.toString(wordIndexes) + ") in: " + sequence);
        }

        return rangedTextList;
    }*/

    /////////////////////// NEW VERSION //////////////////////////////////////////////////////

    @Override
    public List<RangedText> findOutOfRangeWords(List<String> charSequences, int rangeStart, int rangeEnd) {
        validateRange(rangeStart, rangeEnd);
        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);

        List<RangedText> rangedTextList = new ArrayList<>();

        for (String sequence : charSequences) {

            List<String> tokens = Arrays.asList(splitOnSpaces(sequence));
            List<String> outOfRange = _getOutOfRangeStrings(tokens, rangeStart, rangeEnd);

            RangedText rangedText = _getRangedText(rangeStart, rangeEnd, sequence, outOfRange);
            rangedTextList.add(rangedText);
        }

        return rangedTextList;
    }

    private RangedText _getRangedText(int rangeStart, int rangeEnd, String sequence, List<String> outOfRange) {
        RangedText rangedText = new RangedText5000(sequence, rangeStart, rangeEnd);
        rangedText.setOutOfRangeWords(outOfRange.toArray(new String[0]));
        return rangedText;
    }


    /**
     * Tries to find if the token is in the givewn range of the Corpus Dictionary.
     * The token can be modified (eg stripped of adjacent special characters or brought to its base form
     * after the removal of suffixes 's, 'd, 'll, etc.)
     * <p>
     * The match in the dictionary will try to test for upercase, lowercase, title case.
     * <p>
     * If a word is not qualified as a word (blank, empty, trailing/leading special characters, numeric, etc.)
     * it will not be added just like regular words not found in the range.
     */
    private List<String> _getOutOfRangeStrings(List<String> tokens, int rangeStart, int rangeEnd) {
        List<String> wordsOutsideRange = new ArrayList<>();

        for (String token : tokens) {

            String originalToken = token;

            if (token == null || token.isEmpty() || token.isBlank()) {
                continue; // skips adding the word to wordsOutsideRange
            } else {
                token = token.trim();
            }

            if (_isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_UNCHANGED) ||
                    _isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_ALL))
                continue;

            if (_containsSpecialChars(token)) {

                token = _removeLeadingTrailingSpecialChars(token);

                String containsLetters= ".*[a-zA-Z]+.*";
                if(!token.matches(containsLetters))
                    continue;

                if (_isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_UNCHANGED) ||
                        _isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_ALL))
                    continue;

                if (token.contains("'")) {

                    if (_isInDictWhenNegationMappedToBaseForm(token, rangeStart, rangeEnd))
                        continue;

                    // As an axiom, if the token is in the set, it is in range 0-1000
                    if (_isShortFormInPredefinedSet(token, rangeStart))
                        continue;

                    if (_isInDictAfterRemovingSuffixes_d_s_ll(token, rangeStart, rangeEnd)) // 'd , 's . 'll
                        continue;
                }
            }

            if (_isInRangeWhenMappedToBaseForm(token, rangeStart, rangeEnd))
                continue;

            if (_isInDictWhenIrregularVerbMappedToBaseForm(token, rangeStart, rangeEnd))
                continue;

            if (_isInDictAfterRemovingSuffix_S(token, rangeStart, rangeEnd))
                continue;

            if (_isInDictAfterRemovingSuffix_ED(token, rangeStart, rangeEnd))
                continue;

            if (_isInDictAfterRemovingSuffix_ING(token, rangeStart, rangeEnd))
                continue;

            if (_isInDictAfterRemovingSuffix_ER(token, rangeStart, rangeEnd))
                continue;

            if (_isInDictAfterRemovingSuffix_EST(token, rangeStart, rangeEnd))
                continue;

            wordsOutsideRange.add(originalToken);
        }

        return wordsOutsideRange;

    }

    private boolean _isInDictWhenNegationMappedToBaseForm(String token, int rangeStart, int rangeEnd) {
        String found = NEGATIONS.get(token.toLowerCase());
        if (found != null) {
            return _isInRange(found, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }
        return false;
    }

    private boolean _containsSpecialChars(String token) {
        return token.matches("[^a-zA-Z]+.*|.+[^a-zA-Z]+.*");
    }

  /*  private boolean _isInDictAfterRemovingLeadingAndTrailingSpecialChars(String token, int rangeStart, int rangeEnd) {

        // remove it later because it's in the calling method. It will result in 2 tests falling
        token = _removeLeadingTrailingSpecialChars(token);
        return _isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_ALL);
    }*/

    private boolean _isInDictAfterRemovingSuffixes_d_s_ll(String token, int rangeStart, int rangeEnd) {

        if (token.length() >= 3 && token.endsWith("'d") || token.endsWith("'D") || token.endsWith("'s") || token.endsWith("'S")) {
            token = token.substring(0, token.length() - 2);
        }

        if (token.length() >= 4 && (token.endsWith("'ll") || token.endsWith("'LL"))) {
            token = token.substring(0, token.length() - 3);
        }

        return _isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_ALL);
    }

    // As an axiom, if the token is in the set, it is in range 0-1000
    private boolean _isShortFormInPredefinedSet(String token, int rangeStart) {
        return SHORT_FORMS.contains(token.toLowerCase()) && rangeStart <= 1000;
    }

    private boolean _isInRangeWhenMappedToBaseForm(final String token, int rangeStart, int rangeEnd) {
        String baseForm = BASE_FORMS.getOrDefault(token.toLowerCase(), null);
        if (baseForm != null) {
            return _isInRange(baseForm, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }
        return false;
    }


    private boolean _isInRange(String headword, int rangeStart, int rangeEnd, SearchOption searchOption) {

        switch (searchOption) {
            case CASE_UNCHANGED:
                return this.corpusDictionary.isHeadwordInRankRange(headword, rangeStart, rangeEnd);
            case CASE_ALL:
                String titleCase = headword.length() > 1 ? headword.substring(0, 1).toUpperCase() +
                        headword.toLowerCase().substring(1) : headword;
                return this.corpusDictionary.isHeadwordInRankRange(headword, rangeStart, rangeEnd) ||
                        this.corpusDictionary.isHeadwordInRankRange(headword.toLowerCase(), rangeStart, rangeEnd) ||
                        this.corpusDictionary.isHeadwordInRankRange(headword.toUpperCase(), rangeStart, rangeEnd) ||
                        this.corpusDictionary.isHeadwordInRankRange(titleCase, rangeStart, rangeEnd);
            default:
                return this.corpusDictionary.containsHeadword(headword);
        }

    }

   /* List<String> removeIfNullBlankEmpty(List<String> unmodifiableList) {
        return unmodifiableList.stream()
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank))
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toList());
    }

    List<String> removeIfFoundCaseModified(List<String> tokensList) {
        List<String> notFound = new ArrayList<>();
        for (String token : tokensList) {
            boolean isInDictionary = this.corpusDictionary.containsHeadword(token.toLowerCase()) ||
                    this.corpusDictionary.containsHeadword(token.toUpperCase()) ||
                    token.length() > 1 && this.corpusDictionary.containsHeadword(token.substring(0, 1).toUpperCase() + token.substring(1));

            if (!isInDictionary)
                notFound.add(token);
        }
        return notFound;
    }

    List<String> removeIfFoundCasePreserved(List<String> tokensList) {
        return tokensList.stream()
                .filter(token -> !this.corpusDictionary.containsHeadword(token))
                .collect(Collectors.toList());
    }*/

    private void validateRange(int rangeStart, int rangeEnd) {
        if (rangeStart < 0 || rangeStart >= rangeEnd)
            throw new IllegalArgumentException("Range start must be greater than 0 and less than range end.");
    }

    /*
    *//**
     * @return indexes of words that are not in the given range
     *//*
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
        lowerCaseHeadword = replacePastFormWithBaseForm(lowerCaseHeadword);

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

    }*/

    /**
     * Splits on (multiple) spaces
     */
    String[] splitOnSpaces(final String charSequence) {
        log.trace("Splitting: " + charSequence);

        if (charSequence == null || charSequence.isBlank() || charSequence.isEmpty())
            return new String[0];
        else
            return charSequence.trim().split("\\s+");

    }

    ///////////// search optimisation methods ////////////////
    /*private boolean searchInShortForms(String key) {
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
    }*/

    /**
     * Makes some cleanup operations to create valid words
     * (trims, sremove blanks, empty strings, special characters glued to the words
     * removes non-letter chars whose length is 1
     */

    /*List<String> processToDoFurtherCheckIfPresentInDictionary(final List<String> tokens) {

        List<String> cleanedUpList = new ArrayList<>();
        for (String token : tokens) {

            token = token.trim();

            // if a single char, only letters allowed; remove: - * etc.
            if ((token.length() <= 1 && !token.matches("a-zA-Z")))
                continue;


            // don't clean up short forms or single chars
            if (!SHORT_FORMS.contains(token) && token.length() > 1) {
                // THESE METHODS TRANSFORM WORDS
                // *(&
                token = removeShortFormSuffixesAndPossesive(token); // 'd , 's . 'll
                token = removeLeadingSpecialChars(token); // *word
                token = removeTrailingSpecialChars(token); // word?
            }
            cleanedUpList.add(token);
        }
        return cleanedUpList;
    }

    private String replaceWithBaseForm(final String word) {
        return BASE_FORMS.getOrDefault(word.toLowerCase(), word);
    }

    private String replacePastFormWithBaseForm(final String word) {
        return irregularVerbsConverter.convertToBaseForm(word.toLowerCase());
    }*/

    private boolean _isInDictWhenIrregularVerbMappedToBaseForm(String word, int rangeStart, int rangeEnd) {
        String found = irregularVerbsConverter.convertToBaseForm(word);
        if (found != null) {
            return _isInRange(found, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }
        return false;
    }

    /*private String removeShortFormSuffixesAndPossesive(String word) {

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
    }*/

    private String _removeLeadingTrailingSpecialChars(String token) {
        Pattern regex = null;
        if(token.endsWith("s'") || token.endsWith("S'")){ // $(books'   @#boys' => books, boys
             regex = Pattern.compile("[a-zA-Z]+");
        } else {
            if(token.matches("[^a-zA-Z']*'[a-zA-Z]+'[^a-zA-Z']*")){
                regex = Pattern.compile("[a-zA-Z]+"); // 'stop'  ('stop') 'stop'?! => stop
            } else {
                regex = Pattern.compile("[a-zA-Z]+'*[a-zA-Z]*"); // $(azaz'az)*
            }


        }

        Matcher matcher = regex.matcher(token);

        while (matcher.find()) {
            token = matcher.group();
            break;
        }

        return token;
    }

    private boolean _isInDictAfterRemovingSuffix_S(String token, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (token.length() >= 3 && (token.endsWith("s") || token.endsWith("S"))) {
            String withoutS = token.substring(0, token.length() - 1);
            isInRange = _isInRange(withoutS, rangeStart, rangeEnd, SearchOption.CASE_ALL);

            // try if removing -ed helps
            if (!isInRange && (token.endsWith("es") || token.endsWith("ES"))) {
                String withoutES = token.substring(0, token.length() - 2);
                isInRange = _isInRange(withoutES, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }

            // try if removing -ies helps
            if (!isInRange && token.length() >= 4 && (token.endsWith("ies") || token.endsWith("IES"))) {
                String withoutIES = token.substring(0, token.length() - 3) + "y";
                isInRange = _isInRange(withoutIES, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }
        }
        return isInRange;
    }

    private boolean _isInDictAfterRemovingSuffix_ED(String token, int rangeStart, int rangeEnd) {
        // try if removing -d helps
        boolean isInRange = false;
        if (token.length() >= 4 && (token.endsWith("d") || token.endsWith("D"))) {
            String withoutD = token.substring(0, token.length() - 1);
            isInRange = _isInRange(withoutD, rangeStart, rangeEnd, SearchOption.CASE_ALL);

            // try if removing -ed helps
            if (!isInRange && (token.endsWith("ed") || token.endsWith("ED"))) {
                String withoutED = token.substring(0, token.length() - 2);
                isInRange = _isInRange(withoutED, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }
            // try if removing -ied helps
            if (!isInRange && (token.endsWith("ied") || token.endsWith("IED"))) {
                String withoutIED = token.substring(0, token.length() - 3) + "y";
                isInRange = _isInRange(withoutIED, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }
        }
        return isInRange;
    }

    private boolean _isInDictAfterRemovingSuffix_ING(String token, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (token.length() >= 4 && (token.endsWith("ing") || token.endsWith("ING"))) {
            String withoutING = token.substring(0, token.length() - 3);
            isInRange = _isInRange(withoutING, rangeStart, rangeEnd, SearchOption.CASE_ALL);

            if (!isInRange) {  // taking
                withoutING += "e";
                isInRange = _isInRange(withoutING, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }

            if (!isInRange) {  // sitting
                withoutING = token.substring(0, token.length() - 4); // ting
                isInRange = _isInRange(withoutING, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }

        }
        return isInRange;
    }

    private boolean _isInDictAfterRemovingSuffix_ER(String token, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (token.length() >= 5 && (token.endsWith("er") || token.endsWith("ER"))) {
            String withoutER = token.substring(0, token.length() - 2);
            isInRange = _isInRange(withoutER, rangeStart, rangeEnd, SearchOption.CASE_ALL);

            if (!isInRange) {
                String withoutR = token.substring(0, token.length() - 1); //  large-r,
                isInRange = _isInRange(withoutR, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }

            if (!isInRange) {
                String withoutXER = token.substring(0, token.length() - 3); //  big-ger,
                isInRange = _isInRange(withoutXER, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }

            if (!isInRange && (token.endsWith("ier") || token.endsWith("IER"))) {
                String withoutIER = token.substring(0, token.length() - 3) + "y"; // crazy-> craz-ier big-ger,
                isInRange = _isInRange(withoutIER, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }
        }
        return isInRange;
    }

    private boolean _isInDictAfterRemovingSuffix_EST(String token, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (token.length() >= 4 && (token.endsWith("st") || token.endsWith("ST"))) {
            String withoutST = token.substring(0, token.length() - 2);
            isInRange = _isInRange(withoutST, rangeStart, rangeEnd, SearchOption.CASE_ALL);


            if (!isInRange && token.length() >= 5 && (token.endsWith("est") || token.endsWith("EST"))) {
                String withoutEST = token.substring(0, token.length() - 3);
                isInRange = _isInRange(withoutEST, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }

            if (!isInRange && token.length() >= 7 && (token.endsWith("est") || token.endsWith("EST"))) {  //big-gest
                String withoutIEST = token.substring(0, token.length() - 4);
                isInRange = _isInRange(withoutIEST, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }

            if (!isInRange && token.length() >= 6 && (token.endsWith("iest") || token.endsWith("IEST"))) {
                String withoutIEST = token.substring(0, token.length() - 4) + "y"; // craz-iest dr-iest
                isInRange = _isInRange(withoutIEST, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            }
        }
        return isInRange;
    }

    public enum SearchOption {
        CASE_UNCHANGED, CASE_ALL
    }


}
