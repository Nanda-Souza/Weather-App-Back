package com.db.weather_app_back.controller;

import com.db.weather_app_back.domain.dto.ClimaRequest;
import com.db.weather_app_back.domain.dto.ClimaResponse;
import com.db.weather_app_back.domain.dto.ClimaUpdateRequest;
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

    @Operation(description = "Busca todoso os dados meteorológicos disponiveis ou filtrando pela cidade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna todos os dados meteorológicos"),
            @ApiResponse(responseCode = "200", description = "Retorna todos os dados meteorológicos filtrando pela cidade informada"),
            @ApiResponse(responseCode = "404", description = "Não tem nenhum dado meteorológico cadastrado"),
            @ApiResponse(responseCode = "404", description = "Não tem nenhum dado meteorológico cadastrado filtrando pela cidade informada")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<ClimaResponse>> buscarDadoMeteorologicoPorCidade(
            @RequestParam(required = false) String cidade

    ) {
        return ResponseEntity.ok(climaService.buscarDadoMeteorologicoPorCidade(cidade));
    }

    @Operation(description = "Busca os dados meteorológicos para o dia atual da cidade informada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o dado meteorológico do dia atual da cidade pesquisada"),
            @ApiResponse(responseCode = "404", description = "Não tem nenhum dado meteorológico do dia atual da cidade pesquisada")
    })
    @GetMapping("/buscar/hoje")
    public ResponseEntity<ClimaResponse> buscarDadoMeteorologicoDoDiaAtualPorCidade(
            @RequestParam String cidade

    ) {
        return ResponseEntity.ok(climaService.buscarDadoMeteorologicoDoDiaAtualPorCidade(cidade));
    }

    @Operation(description = "Busca os dados meteorológicos para os próximos sete dias da cidade informada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna os dados meteorológicos dos próximos sete dias da cidade pesquisada"),
            @ApiResponse(responseCode = "404", description = "Não tem nenhum dado meteorológico para os próximos sete dias da cidade pesquisada"),
            @ApiResponse(responseCode = "409", description = "Não tem nenhum dado meteorológico pois o a quantidade de dias informada é inválida")
    })
    @GetMapping("/buscar/proximos/{dias}/dias")
    public ResponseEntity<List<ClimaResponse>> buscarDadosMeteorologicoDosProximosSeteDiasPorCidade(
            @RequestParam String cidade,
            @PathVariable int dias

    ) {
        return ResponseEntity.ok(climaService.buscarDadosMeteorologicoDosProximosSeteDiasPorCidade(cidade, dias));
    }

    @Operation(description = "Edita dados meteorológicos por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna os dados meteorológicos com os dados atualizados"),
            @ApiResponse(responseCode = "404", description = "Não encontrou o meteorológico pelo id informado")
    })
    @PatchMapping("/editar/{id}")
    public ResponseEntity<ClimaResponse> editarDadosMeteorologicos(
            @PathVariable Long id,
            @RequestBody @Valid ClimaUpdateRequest climaRequest
            ){
        return ResponseEntity.ok(climaService.editarDadosMeteorologicos(id, climaRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDadosMeteorologicos(
            @PathVariable Long id
    ){
        climaService.excluirDadosMeteorologicos(id);
        return ResponseEntity.noContent().build();
    }

}
