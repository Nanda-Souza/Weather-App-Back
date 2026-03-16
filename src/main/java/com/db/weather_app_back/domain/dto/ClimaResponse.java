package com.db.weather_app_back.domain.dto;

import java.time.LocalDate;

public record ClimaResponse(
        Long id,
        String cidade,
        LocalDate data,
        String tempoDia,
        String tempoNoite,
        int tempMinima,
        int temMaxima,
        int precipitacao,
        int humidade,
        int velocidadeVento

) {
}
