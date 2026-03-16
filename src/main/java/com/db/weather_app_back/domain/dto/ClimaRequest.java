package com.db.weather_app_back.domain.dto;

import com.db.weather_app_back.domain.entity.Tempo;
import jakarta.validation.constraints.*;

public record ClimaRequest(
        @NotBlank(message = "O campo cidade é obrigatório!")
        String cidade,

        @NotNull(message = "O campo data é obrigatório!")
        String data,

        @NotNull(message = "O campo tempoDia é obrigatório!")
        Tempo tempoDia,

        @NotNull(message = "O campo tempoNoite é obrigatório!")
        Tempo tempoNoite,

        @NotNull(message = "O campo tempMinima é obrigatório!")
        @Min(value = -80, message = "O valor da temperatura mínima não pode ser menor que -80° celsius")
        @Max(value = 80, message = "O valor da temperatura mínima não pode ser maior que 80° celsius")
        int tempMinima,

        @NotNull(message = "O campo tempMaxima é obrigatório!")
        @Min(value = -80, message = "O valor da temperatura máxima não pode ser menor que -80° celsius")
        @Max(value = 80, message = "O valor da temperatura máxima não pode ser maior que 80° celsius")
        int tempMaxima,

        @NotNull(message = "O campo precipitacao é obrigatório!")
        @Positive(message = "O valor da precipitação deve ser maior que zero!")
        int precipitacao,

        @NotNull(message = "O campo humidade é obrigatório!")
        @Positive(message = "O valor da humidade deve ser maior que zero!")
        int humidade,

        @NotNull(message = "O campo velocidadeVento é obrigatório!")
        @Positive(message = "O valor da velocidade do vento deve ser maior que zero!")
        int velocidadeVento
) {
}
