package com.ust.wordmaster.headline_exercise;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class HeadlineExerciseDTO {

    private Long id;

    @NotNull
    private Long userID;

    @Schema(type = "string", example = "Ex 1")
    private String title = "Exercise " + id;

    @NotNull
    private String content;

}
