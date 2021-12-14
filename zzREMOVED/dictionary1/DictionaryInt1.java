package com.ust.wordmaster.dictionary1;

import java.util.List;

public interface DictionaryInt1<DictionaryEntry> {

    DictionaryEntry add(DictionaryEntry entry);
    DictionaryEntry delete(DictionaryEntry entry);
    DictionaryEntry findById(long id);
    boolean exists(DictionaryEntry entry);
    List<DictionaryEntry> findByWord(String word);
    List<DictionaryEntry> findAll();

    default long getNumberOfEntries() {
        return findAll().size();
    }

}
