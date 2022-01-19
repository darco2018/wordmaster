package com.ust.wordmaster.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class RangedTextDTO {

    private final String text;
    @JsonProperty("out")
    private String[] outOfRangeWords;
}
