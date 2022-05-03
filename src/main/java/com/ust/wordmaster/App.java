package com.ust.wordmaster;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
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
        System.out.println("456678899");
    }

    @Bean
    CorpusDictionary createCorpusDictionary() {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        return new CorpusDictionary5000("Corpus Dictionary from file", entriesFromFile);
    }

    /*
    If uncommented, @WebMvcTest will start failing with this message:
    Field userRepository in com.ust.wordmaster.user.UserCommandLineRunner required a bean of type
    'com.ust.wordmaster.user.UserRepository' that could not be found.
    I added @Component to UserCommandLineRunner to compensate for commenting off the bean creation here

    @Bean
    UserCommandLineRunner userCommandLineRunner(){
        return new UserCommandLineRunner();
    }
    */


}
