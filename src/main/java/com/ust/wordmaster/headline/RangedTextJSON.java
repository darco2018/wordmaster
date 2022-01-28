package com.ust.wordmaster.headline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class RangedTextJSON {

    private final String text;
    @JsonProperty("out")
    private String[] outOfRangeWords;
}
