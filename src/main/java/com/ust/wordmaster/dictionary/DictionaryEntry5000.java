package com.ust.wordmaster.dictionary;

public class DictionaryEntry5000 extends DictionaryEntry {

    public DictionaryEntry5000(String word, WordData wordData) {
        super(word, wordData);
    }

    public DictionaryEntry5000(String word) {
        super(word);
    }

    @Override
    public int compareTo(DictionaryEntry entry) {

        if (this.getWordData() == null || entry.getWordData() == null)
            return this.getHeadword().compareTo(entry.getHeadword());

        if (entry instanceof DictionaryEntry5000 entry5000) {
            int byHeadword = super.compareTo(entry5000);
            return byHeadword == 0 ?
                    (this.getWordData().getPartOfSpeech().compareTo(entry5000.getWordData().getPartOfSpeech())) :
                    byHeadword;
        } else {
            return super.compareTo(entry);
        }

    }


}
