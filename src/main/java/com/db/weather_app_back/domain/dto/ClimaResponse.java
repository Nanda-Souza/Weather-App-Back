package com.db.weather_app_back.domain.dto;

import java.time.LocalDate;

public record ClimaResponse(
        Long id,
        String cidade,
        String data,
        String tempoDia,
        String tempoNoite,
        int tempMinima,
        int tempMaxima,
        int precipitacao,
        int humidade,
        int velocidadeVento

) {
}
