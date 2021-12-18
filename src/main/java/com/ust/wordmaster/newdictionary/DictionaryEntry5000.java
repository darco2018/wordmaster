package com.ust.wordmaster.newdictionary;

public class DictionaryEntry5000 extends DictionaryEntry {

    public DictionaryEntry5000(String word, WordData wordData) {
        super(word, wordData);
    }

    public DictionaryEntry5000(String word) {
        super(word);
    }

    @Override
    public int compareTo(DictionaryEntry o) {
        if (o instanceof DictionaryEntry5000) {
            DictionaryEntry5000 o5000 = (DictionaryEntry5000) o;
            int byHeadword = super.compareTo(o5000);
            return byHeadword == 0?
                    (this.getWordData().getPartOfSpeech().compareTo(o5000.getWordData().getPartOfSpeech())) :
                    byHeadword;
        } else {
            return super.compareTo(o);
        }

    }


}
