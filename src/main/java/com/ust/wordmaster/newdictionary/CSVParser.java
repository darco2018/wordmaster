package com.ust.wordmaster.newdictionary;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class CSVParser {

    public static List<DictionaryEntry> parse(String filePath) {

        List<DictionaryEntry> entries = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                String[] items = line.split(",");
                DictionaryEntry entry = createDictionaryEntry2(items);
                entries.add(entry);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

    private static DictionaryEntry createDictionaryEntry2(String[] entryData) {

        final String partOfSpeech = entryData[2];
        final int rank;
        final int frequency;
        final double dispersion;
        try {
            rank = Integer.parseInt(entryData[0]);
            frequency = Integer.parseInt(entryData[3]);
            dispersion = Double.parseDouble(entryData[4]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Rank or frequency or dispersion cannot be parset into an int/double", e);
        }

        WordData wordData = new WordData5000(rank, partOfSpeech, frequency, dispersion);

        if (rank == 1) {
            log.info("Started loading the dictionary with the first entry: \n\t[word=" + entryData[1] + ", wordData=" + wordData);
        }

        if (rank == 5000) {
            log.info("Finished loading the dictionary with last entry: \n\t[word=" + entryData[1] + ", wordData=" + wordData);
        }

        final String word = entryData[1];
        DictionaryEntry entry = new DictionaryEntry5000(word);
        entry.setWordData(wordData);

        return entry;

        //return new DictionaryEntry5000("someword");
    }
}
