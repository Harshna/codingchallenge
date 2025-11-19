package com.sportsbet.codingchallenge.depthchart.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlayerDTO(

        Long id,

        @Size(min = 2, max = 20, message="Player name should be between 2 and 20 characters")
        String playerName,

        @NotNull(message = "Unique Team NUmber is Required")
        Integer uniqueTeamNumber
) {
}
