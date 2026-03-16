package com.db.weather_app_back.domain.validation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DataValidator {

    public static boolean dataDeCadastroValida(String data) {
        try {
            LocalDate dataDeCadastro = LocalDate.parse(data);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
