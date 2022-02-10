package com.ust.wordmaster.service.range;

import com.ust.wordmaster.dictionary.CorpusDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

@Slf4j
@Service
public class RangeAnalyser5000 implements RangeAnalyser {

    private static final Set<String> SHORT_FORMS = Set.of("i'd", "he'd", "she'd", "we'd", "you'd", "they'd",
            "i'm", "he's", "she's", "it's", "we're", "you're", "they're",
            "I'll", "he'll", "she'll", "it'll", "we'll", "you'll", "they'll",
            "there's", "there're", "there'd", "there'll",
            "ain't", "gonna");

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

    @Override
    public List<RangedText> findOutOfRangeWords(List<String> charSequences, int rangeStart, int rangeEnd) {
        _validateRange(rangeStart, rangeEnd);
        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);

        List<RangedText> rangedTextList = new ArrayList<>();

        for (String sequence : charSequences) {

            List<String> tokens = Arrays.asList(_splitOnSpaces(sequence));
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
                token = token.strip();
            }

            if (_isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_UNCHANGED) ||
                    _isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_ALL))
                continue;

            if (_isInDictAsAmericanSpelling(token, rangeStart, rangeEnd))
                continue;

            if (_containsSpecialChars(token)) {

                token = _removeLeadingTrailingSpecialChars(token);

                String containsLetters = ".*[a-zA-Z]+.*";
                if (!token.matches(containsLetters))
                    continue;

                if (_isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_UNCHANGED) ||
                        _isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_ALL))
                    continue;

                if (_isInDictAsAmericanSpelling(token, rangeStart, rangeEnd))
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

            //extract into method if more exceptions
            if (token.matches("shan") || token.matches("shan't")) {
                wordsOutsideRange.add("shan't");
            } else {
                wordsOutsideRange.add(_preserveOnlyLettersDigits(originalToken));
            }

        }

        return wordsOutsideRange;

    }

    private String _preserveOnlyLettersDigits(String token) {
        String onlyLettersDigits = "[a-zA-Z1-9]+";
        Matcher matcher = Pattern.compile(onlyLettersDigits).matcher(token);
        while (matcher.find()) {
            return matcher.group();
        }
        return token;
    }

    private boolean _isInDictAsAmericanSpelling(String token, int rangeStart, int rangeEnd) {

        if (token.length() >= 5 && token.endsWith("ise") || token.endsWith("ISE")) {
            String americanSpelling = token.substring(0, token.length() - 3) + "ize";
            return _isInRange(americanSpelling, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }

        if (token.length() >= 5 && token.endsWith("lyse") || token.endsWith("LYSE")) {
            String americanSpelling = token.substring(0, token.length() - 4) + "lyze";
            return _isInRange(americanSpelling, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }

        if (token.length() >= 5 && token.endsWith("our") || token.endsWith("OUR")) {
            String americanSpelling = token.substring(0, token.length() - 3) + "or";
            return _isInRange(americanSpelling, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }

        if (token.length() >= 5 && token.endsWith("tre") || token.endsWith("TRE")) {
            String americanSpelling = token.substring(0, token.length() - 3) + "ter";
            return _isInRange(americanSpelling, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }

        if (token.length() >= 7 && token.endsWith("logue") || token.endsWith("LOGUE")) {
            String americanSpelling = token.substring(0, token.length() - 5) + "log";
            return _isInRange(americanSpelling, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }

        if (token.length() >= 5 && token.endsWith("ence") || token.endsWith("ENCE")) {
            String americanSpelling = token.substring(0, token.length() - 4) + "ense";
            return _isInRange(americanSpelling, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }

        if (token.length() >= 4 && token.endsWith("l") || token.endsWith("L")) {
            String americanSpelling = token.substring(0, token.length() - 1) + "ll";
            return _isInRange(americanSpelling, rangeStart, rangeEnd, SearchOption.CASE_ALL);
        }

        if (token.equalsIgnoreCase("programme"))
            return _isInRange("program", rangeStart, rangeEnd, SearchOption.CASE_ALL);
        return false;
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

    private boolean _isInDictAfterRemovingSuffixes_d_s_ll(String token, int rangeStart, int rangeEnd) {

        if (token.length() >= 3 && token.endsWith("'d") || token.endsWith("'D") || token.endsWith("'s") || token.endsWith("'S")) {
            token = token.substring(0, token.length() - 2);
        }

        if (token.length() >= 4 && (token.endsWith("'ll") || token.endsWith("'LL"))) {
            token = token.substring(0, token.length() - 3);
        }

        return _isInRange(token, rangeStart, rangeEnd, SearchOption.CASE_ALL) ||
                _isInRangeWhenMappedToBaseForm(token, rangeStart, rangeEnd) ||
                _isInDictAsAmericanSpelling(token, rangeStart, rangeEnd);
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
            default:
                String titleCase = headword.length() > 1 ? headword.substring(0, 1).toUpperCase() +
                        headword.toLowerCase().substring(1) : headword;
                return this.corpusDictionary.isHeadwordInRankRange(headword, rangeStart, rangeEnd) ||
                        this.corpusDictionary.isHeadwordInRankRange(headword.toLowerCase(), rangeStart, rangeEnd) ||
                        this.corpusDictionary.isHeadwordInRankRange(headword.toUpperCase(), rangeStart, rangeEnd) ||
                        this.corpusDictionary.isHeadwordInRankRange(titleCase, rangeStart, rangeEnd);

        }

    }

    private void _validateRange(int rangeStart, int rangeEnd) {
        if (rangeStart < 0 || rangeStart >= rangeEnd)
            throw new IllegalArgumentException("Range start must be greater than 0 and less than range end.");
    }

    /**
     * Splits on (multiple) spaces
     */
    String[] _splitOnSpaces(final String charSequence) {
        log.trace("Splitting: " + charSequence);

        if (charSequence == null || charSequence.isBlank() || charSequence.isEmpty())
            return new String[0];
        else
            return charSequence.strip().split("\\s+");

    }

    private boolean _isInDictWhenIrregularVerbMappedToBaseForm(String word, int rangeStart, int rangeEnd) {
        String found = irregularVerbsConverter.convertToBaseForm(word);
        if (found != null) {
            return _isInRange(found, rangeStart, rangeEnd, SearchOption.CASE_ALL) ||
                    _isInDictAsAmericanSpelling(found, rangeStart, rangeEnd);
        }
        return false;
    }

    private String _removeLeadingTrailingSpecialChars(String token) {
        Pattern regex;
        if (token.endsWith("s'") || token.endsWith("S'")) { // $(books'   @#boys' => books, boys
            regex = Pattern.compile("[a-zA-Z]+");
        } else {
            String oneOfTwoQuotes = "[^a-zA-Z']*'*[a-zA-Z]+'*[^a-zA-Z']*"; // 'stop' stop'  ('stop') 'stop'!! => stop
            if (token.matches(oneOfTwoQuotes)) {
                regex = Pattern.compile("[a-zA-Z]+");
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
            if (!isInRange) {
                isInRange = _isInDictAsAmericanSpelling(withoutS, rangeStart, rangeEnd);
            }


            // try if removing -ed helps
            if (!isInRange && (token.endsWith("es") || token.endsWith("ES"))) {
                String withoutES = token.substring(0, token.length() - 2);
                isInRange = _isInRange(withoutES, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutES, rangeStart, rangeEnd);
                }
            }

            // try if removing -ies helps
            if (!isInRange && token.length() >= 4 && (token.endsWith("ies") || token.endsWith("IES"))) {
                String withoutIES = token.substring(0, token.length() - 3) + "y";
                isInRange = _isInRange(withoutIES, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutIES, rangeStart, rangeEnd);
                }
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
            if (!isInRange) {
                isInRange = _isInDictAsAmericanSpelling(withoutD, rangeStart, rangeEnd);
            }

            // try if removing -ed helps
            if (!isInRange && (token.endsWith("ed") || token.endsWith("ED"))) {
                String withoutED = token.substring(0, token.length() - 2);
                isInRange = _isInRange(withoutED, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutED, rangeStart, rangeEnd);
                }
                if (!isInRange) {  // ban-ned
                    String withoutXED = token.substring(0, token.length() - 3); // ban-ned
                    isInRange = _isInRange(withoutXED, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                    if (!isInRange) {
                        isInRange = _isInDictAsAmericanSpelling(withoutXED, rangeStart, rangeEnd);
                    }
                }

            }
            // try if removing -ied helps
            if (!isInRange && (token.endsWith("ied") || token.endsWith("IED"))) {
                String withoutIED = token.substring(0, token.length() - 3) + "y";
                isInRange = _isInRange(withoutIED, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutIED, rangeStart, rangeEnd);
                }
            }
        }
        return isInRange;
    }

    private boolean _isInDictAfterRemovingSuffix_ING(String token, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (token.length() >= 4 && (token.endsWith("ing") || token.endsWith("ING"))) {
            String withoutING = token.substring(0, token.length() - 3);
            isInRange = _isInRange(withoutING, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            if (!isInRange) {
                isInRange = _isInDictAsAmericanSpelling(withoutING, rangeStart, rangeEnd);
            }

            if (!isInRange) {  // taking
                withoutING += "e";
                isInRange = _isInRange(withoutING, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutING, rangeStart, rangeEnd);
                }
            }

            if (!isInRange) {  // sitting
                withoutING = token.substring(0, token.length() - 4); // ting
                isInRange = _isInRange(withoutING, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutING, rangeStart, rangeEnd);
                }
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
                isInRange = _isInDictAsAmericanSpelling(withoutER, rangeStart, rangeEnd);
            }

            if (!isInRange) {
                String withoutR = token.substring(0, token.length() - 1); //  large-r,
                isInRange = _isInRange(withoutR, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutR, rangeStart, rangeEnd);
                }
            }

            if (!isInRange) {
                String withoutXER = token.substring(0, token.length() - 3); //  big-ger,
                isInRange = _isInRange(withoutXER, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutXER, rangeStart, rangeEnd);
                }
            }

            if (!isInRange && (token.endsWith("ier") || token.endsWith("IER"))) {
                String withoutIER = token.substring(0, token.length() - 3) + "y"; // crazy-> craz-ier big-ger,
                isInRange = _isInRange(withoutIER, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutIER, rangeStart, rangeEnd);
                }
            }
        }
        return isInRange;
    }

    private boolean _isInDictAfterRemovingSuffix_EST(String token, int rangeStart, int rangeEnd) {
        boolean isInRange = false;
        if (token.length() >= 4 && (token.endsWith("st") || token.endsWith("ST"))) {
            String withoutST = token.substring(0, token.length() - 2);
            isInRange = _isInRange(withoutST, rangeStart, rangeEnd, SearchOption.CASE_ALL);
            if (!isInRange) {
                isInRange = _isInDictAsAmericanSpelling(withoutST, rangeStart, rangeEnd);
            }


            if (!isInRange && token.length() >= 5 && (token.endsWith("est") || token.endsWith("EST"))) {
                String withoutEST = token.substring(0, token.length() - 3);
                isInRange = _isInRange(withoutEST, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutEST, rangeStart, rangeEnd);
                }
            }

            if (!isInRange && token.length() >= 7 && (token.endsWith("est") || token.endsWith("EST"))) {  //big-gest
                String withoutXEST = token.substring(0, token.length() - 4);
                isInRange = _isInRange(withoutXEST, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutXEST, rangeStart, rangeEnd);
                }
            }

            if (!isInRange && token.length() >= 6 && (token.endsWith("iest") || token.endsWith("IEST"))) {
                String withoutIEST = token.substring(0, token.length() - 4) + "y"; // craz-iest dr-iest
                isInRange = _isInRange(withoutIEST, rangeStart, rangeEnd, SearchOption.CASE_ALL);
                if (!isInRange) {
                    isInRange = _isInDictAsAmericanSpelling(withoutIEST, rangeStart, rangeEnd);
                }
            }
        }
        return isInRange;
    }

    public enum SearchOption {
        CASE_UNCHANGED, CASE_ALL
    }


}
