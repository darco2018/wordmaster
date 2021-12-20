package com.ust.wordmaster.newdictionary;

import com.ust.wordmaster.dictionaryOLD.CorpusDictionaryOLD;
import com.ust.wordmaster.dictionaryOLD.DictionaryEntryOLD;
import com.ust.wordmaster.service.filteringOLD.ParsedTextUnitOLD;
import com.ust.wordmaster.service.filteringOLD.TextUnitOLD;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RangeAnalyser5000 implements RangeAnalyser{

    private final CorpusDictionary corpusDictionary;

    public RangeAnalyser5000(CorpusDictionary corpusDictionary) {
        this.corpusDictionary = corpusDictionary;
    }

    @Override
    public List<RangedText> analyseRange(List<String> charSequences, int rangeStart, int rangeEnd) {
        if (rangeStart < 0 || rangeStart >= rangeEnd)
            throw new IllegalArgumentException("Range start must be greater than 0 and less than range end.");
                        //CorpusDictionaryOLD.validateRange(rangeStart, rangeEnd);
        Objects.requireNonNull(charSequences, "List of charSequences cannot be null");

        log.info("Filtering " + charSequences.size() + " charSequences; range " + rangeStart + "-" + rangeEnd);
        //////////////  DONE SO FAR //////////////////////////////


        List<RangedText> rangedTextList = new ArrayList<>();
      //  NavigableSet<DictionaryEntryOLD> subsetDictionary = corpusDictionary.getDictionarySubset(rangeStart, rangeEnd);

        for (String str : charSequences) {

            String[] words = splitOnSpaces(str);
                    //words = cleanUpWordsToCreateValidTextUnitObjs(words);
            RangedText rangedText = new RangedText5000(str, rangeStart, rangeEnd);



            /////TextUnitOLD textUnit = new TextUnitOLD(str, words);

            // create FilteredTextUnit
            int[] wordIndexes = isolateOutOfRangeWords(words, this.corpusDictionary);
            String[] outOfRangeWords = convertIndexesToWords(wordIndexes, words);
            rangedText.setOutOfRangeWords(outOfRangeWords);

            //ParsedTextUnitOLD filteredTextUnit = new ParsedTextUnitOLD(textUnit, wordIndexes, new int[]{rangeStart, rangeEnd});
            //rangedTextList.add(filteredTextUnit);

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
                   //key = replaceWithBaseForm(key);

        // initial search
        boolean found = this.corpusDictionary.containsHeadword(key);
/*
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
       */

        return found;
    }





}
