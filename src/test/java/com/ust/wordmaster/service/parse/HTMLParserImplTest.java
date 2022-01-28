package com.ust.wordmaster.service.parse;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class HTMLParserImplTest {

    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";
    private static final String BBC_PATHSTRING = "bbc.html";
    private static String BBC_HOMEPAGE;

    private final HTMLParser htmlParser = new HTMLParserImpl();

    @BeforeAll
    private static void readFileIntoString() throws Exception {
        BBC_HOMEPAGE = Files.readString(Paths.get(BBC_PATHSTRING) );
    }

    @DisplayName("Parses BBC homepage into headlines")
    @Test
    void givenBBChtml_parse_parsesCorrectlyIntoHeadlines() {

        // act
        List<String> headlineStrings = htmlParser.parseHTML(BBC_HOMEPAGE, BBC_HEADLINES_ATTRIBUTE);
        assertTrue(headlineStrings.size() > 10);
        assertEquals(67, headlineStrings.size());

        org.assertj.core.api.Assertions.assertThat(headlineStrings)
                .isNotNull()
                .doesNotContainNull()
                .isInstanceOfAny(List.class)
                .hasSize(67)
                .asString().contains("Coronavirus: Airline asks staff");
    }

    @DisplayName("Unparseble text returns empty list")
    @Test
    void givenUnparsableContent_returnsEmptyList() {

        List<String> headlines = this.htmlParser.parseHTML("Some unparsable text here that is not html", BBC_HEADLINES_ATTRIBUTE);

        Assertions.assertThat(headlines)
                .isNotNull()
                .isInstanceOfAny(List.class)
                .hasSize(0);
    }
}