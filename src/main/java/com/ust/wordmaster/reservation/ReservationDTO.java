package com.ust.wordmaster.reservation;

import io.swagger.v3.oas.annotations.media.Schema; // springdoc-openapi-ui
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationDTO {

    private Long id;

    @NotNull
    private Long userID;

    @NotNull
    private LocalDateTime reservationDate;

    @NotNull
    @Schema(type = "string", example = "14:30")
    private LocalTime reservationStart;

    @NotNull
    @Schema(type = "string", example = "14:30")
    private LocalTime reservationEnd;
}
