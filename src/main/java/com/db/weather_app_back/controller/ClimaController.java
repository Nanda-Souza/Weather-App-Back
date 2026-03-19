package com.db.weather_app_back.controller;

import com.db.weather_app_back.domain.dto.ClimaRequest;
import com.db.weather_app_back.domain.dto.ClimaResponse;
import com.db.weather_app_back.domain.service.ClimaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clima")
public class ClimaController {
    @Autowired
    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {this.climaService = climaService;}

    @Operation(description = "Cadastra um dado meteorológico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cria dado meteorológico com base nos dados informados"),
            @ApiResponse(responseCode = "409", description = "Não cria o dado meteorológico pois já existe dado para a data/cidade informada"),
            @ApiResponse(responseCode = "400", description = "Não cria o dado meteorológico pois os dados informados são inválidos")
    })
    @PostMapping("/cadastrar")
    public ResponseEntity<ClimaResponse> cadastrarDadoMeteorologico(
            @RequestBody  @Valid ClimaRequest climaRequest
    ){
        ClimaResponse salvo = climaService.cadastrarDadoMeteorologico(climaRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("clima/{id}")
                .buildAndExpand(salvo.id())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    @Operation(description = "Busca um dado meteorológico por por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o meteorológico com base no Id informado"),
            @ApiResponse(responseCode = "404", description = "Não encontrou o meteorológico pelo id informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClimaResponse> buscarDadoMeteorologicoPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(climaService.buscarDadoMeteorologicoPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClimaResponse>> buscarDadoMeteorologicoPorCidade(
            @RequestParam(required = false) String cidade

    ) {
        return ResponseEntity.ok(climaService.buscarDadoMeteorologicoPorCidade(cidade));
    }

}
