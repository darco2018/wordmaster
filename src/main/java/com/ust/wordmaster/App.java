package com.ust.wordmaster;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import com.ust.wordmaster.user.UserCommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class App {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(App.class, args);
    }

    @Bean
    CorpusDictionary createCorpusDictionary() {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        return new CorpusDictionary5000("Corpus Dictionary from file", entriesFromFile);
    }

    @Bean
    UserCommandLineRunner userCommandLineRunner(){
        return new UserCommandLineRunner();
    }

}
