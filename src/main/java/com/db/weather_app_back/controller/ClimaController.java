package com.db.weather_app_back.controller;

import com.db.weather_app_back.domain.dto.ClimaRequest;
import com.db.weather_app_back.domain.dto.ClimaResponse;
import com.db.weather_app_back.domain.service.ClimaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/clima")
public class ClimaController {
    @Autowired
    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {this.climaService = climaService;}

    @PostMapping("/cadastrar")
    public ResponseEntity<ClimaResponse> cadastrarDadoMeteorologico(
            @RequestBody  @Valid ClimaRequest climaRequest
    ){
        ClimaResponse salvo = climaService.cadastrarClima(climaRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("clima/{id}")
                .buildAndExpand(salvo.id())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

}
