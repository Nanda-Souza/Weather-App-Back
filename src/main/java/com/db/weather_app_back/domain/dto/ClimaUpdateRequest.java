package com.db.weather_app_back.domain.dto;

import com.db.weather_app_back.domain.entity.Tempo;
import jakarta.validation.constraints.*;

public record ClimaUpdateRequest(
        Tempo tempoDia,
        Tempo tempoNoite,

        @Min(value = -80, message = "O valor da temperatura mínima não pode ser menor que -80° celsius")
        @Max(value = 80, message = "O valor da temperatura mínima não pode ser maior que 80° celsius")
        Integer tempMinima,

        @Min(value = -80, message = "O valor da temperatura máxima não pode ser menor que -80° celsius")
        @Max(value = 80, message = "O valor da temperatura máxima não pode ser maior que 80° celsius")
        Integer tempMaxima,

        @Positive(message = "O valor da precipitação deve ser maior que zero!")
        Integer precipitacao,

        @Positive(message = "O valor da humidade deve ser maior que zero!")
        Integer humidade,

        @Positive(message = "O valor da velocidade do vento deve ser maior que zero!")
        Integer velocidadeVento
) {
}
