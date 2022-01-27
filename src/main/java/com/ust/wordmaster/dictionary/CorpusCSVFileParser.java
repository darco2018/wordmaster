package com.ust.wordmaster.dictionary;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class CorpusCSVFileParser {

    public static List<DictionaryEntry> parse(final String filePath) {

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines
                    .map(line -> createDictionaryEntry(line.split(",")))
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private static DictionaryEntry createDictionaryEntry(final String[] entryData) {

        try {
            final int rank = Integer.parseInt(entryData[0]);
            final String word = entryData[1];
            final String partOfSpeech = entryData[2];
            final int frequency = Integer.parseInt(entryData[3]);
            final double dispersion = Double.parseDouble(entryData[4]);
            WordData wordData = new WordData5000(word, rank, partOfSpeech, frequency, dispersion);

            if (rank == 1)
                log.info("Loading the dictionary with the first entry: \n\t[word=%s, wordData=%s]".formatted(entryData[1], wordData));

            if (rank == 5000)
                log.info("Finished loading the dictionary with last entry: \n\t[word=%s, wordData=%s]".formatted(entryData[1], wordData));

            return new DictionaryEntry5000(word, wordData);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Rank or frequency or dispersion cannot be parsed into an int/double", e);
        }

    }

}
