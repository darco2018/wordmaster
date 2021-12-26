package com.ust.wordmaster.service.analysing;

import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.DictionaryEntry5000;
import com.ust.wordmaster.dictionary.WordData5000;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class) // Initializes mocks annotated with @Mock, so that explicit usage of
// MockitoAnnotations#initMocks(Object) is not necessary. Mocks are initialized before each test method.
public class RangeAnalyser5000_MockitoTest {

    @Mock
    private CorpusDictionary mockedCorpus;

    /* mocks will NOT be initialized at that point. if you had a constructor that took an X
    and you would write new A(x) here, x would be null, since
    the @Mock annotation would not have been processed yet*/
    @InjectMocks
    private RangeAnalyser rangeAnalyser = new RangeAnalyser5000(this.mockedCorpus);

    /* ALTERNATIVE
    private RangeAnalyser5000 rangeAnalyser;
    @BeforeEach
    void setUp() {
        rangeAnalyser= new RangeAnalyser5000(this.mockedCorpus);
    }*/


    @org.junit.jupiter.api.Test
    public void given_someEntryFoundForHeadword_findsWordsOutOfRange(){

        WordData5000 wordData = new WordData5000();
        wordData.setRank(3000);
        given(this.mockedCorpus.getEntriesByHeadword("ccc")).willReturn(List.of(new DictionaryEntry5000("ccc", wordData)));

        List<RangedText> rangedTexts = this.rangeAnalyser.findOutOfRangeWords(List.of("ccc"), 1, 5000);
        Assertions.assertThat(rangedTexts.get(0).getOutOfRangeWords()).doesNotContain("ccc");

        //-----------------------------------

        rangedTexts = this.rangeAnalyser.findOutOfRangeWords(List.of("ccc"), 1, 2222);
        Assertions.assertThat(rangedTexts.get(0).getOutOfRangeWords()).contains("ccc");

        //-----------------------------------

        wordData.setRank(5555);
        given(this.mockedCorpus.getEntriesByHeadword("ccc")).willReturn(List.of(new DictionaryEntry5000("ccc", wordData)));

        rangedTexts = this.rangeAnalyser.findOutOfRangeWords(List.of("ccc"), 1, 5000);
        Assertions.assertThat(rangedTexts.get(0).getOutOfRangeWords()).contains("ccc");
    }

    @Test
    public void givenNoEntriesForHeadword_returnsAllWordsAsOutOfRange() {

        List<String> headlines = List.of("aaa bbb ccc", "ddd eee", "fff");
        given(this.mockedCorpus.getEntriesByHeadword(anyString())).willReturn(Collections.emptyList());

        List<RangedText> rangedTexts = this.rangeAnalyser.findOutOfRangeWords(headlines, 1, 5000);

        // assert
        String[] outOfRangeWords_1 = rangedTexts.get(0).getOutOfRangeWords();
        String[] outOfRangeWords_2 = rangedTexts.get(1).getOutOfRangeWords();
        String[] outOfRangeWords_3 = rangedTexts.get(2).getOutOfRangeWords();

        Assertions.assertThat(outOfRangeWords_1.length).isEqualTo(headlines.get(0).split(" ").length);
        Assertions.assertThat(outOfRangeWords_1).containsExactly("aaa", "bbb", "ccc");

        Assertions.assertThat(outOfRangeWords_2.length).isEqualTo(headlines.get(1).split(" ").length);
        Assertions.assertThat(outOfRangeWords_2).containsExactly("ddd", "eee");

        Assertions.assertThat(outOfRangeWords_3.length).isEqualTo(headlines.get(2).split(" ").length);
        Assertions.assertThat(outOfRangeWords_3).containsExactly("fff");
    }

    /*@Test
    public void givenRangeLimits_whenWordsOutOfRangePresentInHeadlines_findsTheirIndexes() {


    }*/

    /*@Test
    public void givenRangeLimits_whenNoWordsOutOfRangePresentInHeadline_returnsEmptyArray() {



    }*/
}
