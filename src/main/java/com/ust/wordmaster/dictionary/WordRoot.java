package com.ust.wordmaster.dictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

//@Data is a convenient shortcut annotation that bundles the features of
//@ToString, @EqualsAndHashCode, @Getter / @Setter and @RequiredArgsConstructor t
@EqualsAndHashCode
@ToString
public final class WordRoot {

    @Getter
    private final String word;

    public WordRoot(final String word) {
        Objects.requireNonNull(word, "Word cannot be null");

        if (word.isBlank() || word.isEmpty()) {
            throw new IllegalArgumentException("Word cannot be empty or blank");
        }
        this.word = word;
    }
}
