package com.ust.wordmaster.service.range;

import com.ust.wordmaster.dictionary.CorpusDictionary;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class) // Initializes mocks annotated with @Mock, so that explicit usage of
// MockitoAnnotations#initMocks(Object) is not necessary. Mocks are initialized before each test method.
class RangeAnalyser5000_MockitoTest {

    @Mock
    private CorpusDictionary mockedCorpus;

    /* mocks will NOT be initialized at that point. if you had a constructor that took an X
    and you would write new A(x) here, x would be null, since
    the @Mock annotation would not have been processed yet*/
    @InjectMocks
    private RangeAnalyser rangeAnalyser = new RangeAnalyser5000(this.mockedCorpus);

    @BeforeEach
    void setUp() {
        rangeAnalyser = new RangeAnalyser5000(this.mockedCorpus);
    }


    @org.junit.jupiter.api.Test
    public void givenCorpusDictData_identifiesWordsOutOfRange() {

        given(this.mockedCorpus.isHeadwordInRankRange("ccc", 1, 5000)).willReturn(true);

        List<RangedText> rangedTexts = this.rangeAnalyser.findOutOfRangeWords(List.of("ccc"), 1, 5000);

        Assertions.assertThat(rangedTexts.get(0).getOutOfRangeWords()).doesNotContain("ccc");

        //-----------------------------------

        given(this.mockedCorpus.isHeadwordInRankRange("ccc", 1, 5000)).willReturn(false);

        rangedTexts = this.rangeAnalyser.findOutOfRangeWords(List.of("ccc"), 1, 5000);

        Assertions.assertThat(rangedTexts.get(0).getOutOfRangeWords()).contains("ccc");

        //-----------------------------------
        // lenient() for calling the stubbed method multiple times
        lenient().when(this.mockedCorpus.isHeadwordInRankRange("ccc", 1, 5000)).thenReturn(false);
        lenient().when(this.mockedCorpus.isHeadwordInRankRange("happy", 1, 5000)).thenReturn(true);
        lenient().when(this.mockedCorpus.isHeadwordInRankRange("luck", 1, 5000)).thenReturn(true);

        rangedTexts = this.rangeAnalyser.findOutOfRangeWords(List.of("ccc happy luck"), 1, 5000);

        Assertions.assertThat(rangedTexts.get(0).getOutOfRangeWords()).contains("ccc");
        Assertions.assertThat(rangedTexts.get(0).getOutOfRangeWords()).doesNotContain("happy", "luck");


    }


}
