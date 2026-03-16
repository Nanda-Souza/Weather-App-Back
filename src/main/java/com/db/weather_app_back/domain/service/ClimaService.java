package com.db.weather_app_back.domain.service;

import com.db.weather_app_back.domain.repository.ClimaRepository;
import org.springframework.stereotype.Service;

@Service
public class ClimaService {

    private final ClimaRepository climaRepository;

    public ClimaService(ClimaRepository climaRepository) {this.climaRepository = climaRepository;}




}
