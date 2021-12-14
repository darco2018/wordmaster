package com.ust.wordmaster.dictionary1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class CSVParser1 {

    public static List<DictionaryEntry1> parse(String filePath) {

        List<DictionaryEntry1> entries = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                String[] words = line.split(",");
                DictionaryEntry1 entry = createDictionaryEntry(words);
                entries.add(entry);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

    private static DictionaryEntry1 createDictionaryEntry(String[] entryData) {

        DictionaryEntry1 entry = new DictionaryEntry1(new WordRoot1(entryData[1]),
                new WordData1(Integer.parseInt(entryData[0]),
                        entryData[2],
                        Integer.parseInt(entryData[3]),
                        Double.parseDouble(entryData[4])));

        if (Integer.parseInt(entryData[0]) == 1) {
            log.info("Started loading the dictionary with the first entry: \n\t" + entryData[0] + ". " + entry);
        }

        if (Integer.parseInt(entryData[0]) == 5000) {
            log.info("Finished loading the dictionary with last entry: \n\t" + entryData[0] + ". " + entry);
        }

        return entry;
    }
}
