package com.db.weather_app_back.domain.service;

import com.db.weather_app_back.domain.dto.ClimaRequest;
import com.db.weather_app_back.domain.dto.ClimaResponse;
import com.db.weather_app_back.domain.entity.Clima;
import com.db.weather_app_back.domain.entity.Tempo;
import com.db.weather_app_back.domain.repository.ClimaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClimaServiceTest {
    @Mock
    private ClimaRepository climaRepository;

    @InjectMocks
    private ClimaService climaService;

    @Test
    @DisplayName("Deve salvar e retornar os dados meteorológicos com sucesso!")
    void deveSalvarDadosMeteorologicosComSucesso(){

        ClimaRequest request = new ClimaRequest(
                "Canoas",
                "2026-03-16",
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        Clima climaSalvo = new Clima(
                request.cidade(),
                LocalDate.parse(request.data()),
                request.tempoDia(),
                request.tempoNoite(),
                request.tempMinima(),
                request.tempMaxima(),
                request.precipitacao(),
                request.humidade(),
                request.velocidadeVento()
        );

        when(climaRepository.save(any(Clima.class)))
                .thenReturn(climaSalvo);

        ClimaResponse response = climaService.cadastrarDadoMeteorologico(request);

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals("Canoas", response.cidade(), "Deve retornar Canoas!");
        assertEquals("2026-03-16", response.data(), "Deve retornar a data de 2026-03-16!");

    }

    @Test
    @DisplayName("Deve lançar exceção 409 ao tentar salvar dados meteorológicos já cadastrados!")
    void deveLancarExcecaoAoSalvarDadosMeteorologicosQuandoJaCadastrados(){

        ClimaRequest request = new ClimaRequest(
                "Canoas",
                "2026-03-16",
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        when(climaRepository.existsByCidadeAndData(request.cidade(), LocalDate.parse(request.data())))
                .thenReturn(true);


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.cadastrarDadoMeteorologico(request)
                );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode(), "Deve retornar status code 409!");
        assertEquals("Já existe dado meteorológico cadastrado para esta cidade na data informada!",
                exception.getReason(), "Deve retornar que já existe dado meteorológico para a cidade e data informada!");

        verify(climaRepository, never()).save(any());

    }

    @Test
    @DisplayName("Deve lançar exceção 400 ao tentar salvar dados meteorológicos com dados inválidos!")
    void deveLancarExcecaoAoSalvarDadosMeteorologicosQuandoInvalidos(){

        ClimaRequest request = new ClimaRequest(
                "Canoas",
                "2026-03",
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.cadastrarDadoMeteorologico(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "Deve retornar status code 400!");
        assertEquals("A data está no formato inválido! Favor fornecer data no formato aaaa-mm-dd!",
                exception.getReason(), "Deve retornar que a data está no formato inválido!");

        verify(climaRepository, never()).save(any());

    }

    @Test
    @DisplayName("Deve retornar os dados meteorológicos ao buscar por id com sucesso!")
    void deveRetornarDadosMeteorologicosPorIdComSucesso(){
        Long id = 1L;

        Clima clima = new Clima(
                "Canoas",
                LocalDate.parse("2026-03-16"),
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        when(climaRepository.findById(id)).thenReturn(Optional.of(clima));

        ClimaResponse response = climaService.buscarDadoMeteorologicoPorId(id);

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals("Canoas", response.cidade(), "Deve retornar Canoas!");
        assertEquals("2026-03-16", response.data(), "Deve retornar a data de 2026-03-16!");

        verify(climaRepository).findById(id);

    }

    @Test
    @DisplayName("Deve lançar exceção 404 ao buscar dados meteorológicos inexistente por id!")
    void deveLancarExcecaoQuandoNaoEncontrarDadosMeteorologicosPorId(){
        Long id = 1L;

        when(climaRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.buscarDadoMeteorologicoPorId(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Deve retornar status code 404!");
        assertEquals("Dado Meteorológico com Id 1 não encontrado!",
                exception.getReason(), "Deve retornar mensagem de não encontrado!");

        verify(climaRepository).findById(id);

    }

    @Test
    @DisplayName("Deve retornar lista de dados meteorológicos a partir da data atual quando cidade não for informada!")
    void deveRetornarDadosSemCidadeInformadaComSucesso() {

        LocalDate dataDeHoje = LocalDate.now();

        Clima clima = new Clima(
                "Canoas",
                dataDeHoje.plusDays(1),
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        when(climaRepository.findByDataGreaterThanEqualOrderByDataAsc(dataDeHoje))
                .thenReturn(List.of(clima));

        List<ClimaResponse> response = climaService.buscarDadoMeteorologicoPorCidade(null);

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals(1, response.size(), "Deve retornar uma lista com apenas um resultado!");
        assertEquals("Canoas", response.get(0).cidade(), "Deve retornar Canoas!");

        verify(climaRepository).findByDataGreaterThanEqualOrderByDataAsc(dataDeHoje);
    }

    @Test
    @DisplayName("Deve lançar exceção 404 quando não houver dados e cidade não for informada!")
    void deveLancarExcecaoQuandoNaoHouverDadosSemCidadeInformada() {

        LocalDate hoje = LocalDate.now();

        when(climaRepository.findByDataGreaterThanEqualOrderByDataAsc(hoje))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.buscarDadoMeteorologicoPorCidade(null)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Deve retornar status code 404!");
        assertEquals("Nenhum dado meteorológico cadastrado!",
                exception.getReason(), "Deve retornar mensagem de nenhum dado meteorológico cadastrado!");

        verify(climaRepository).findByDataGreaterThanEqualOrderByDataAsc(hoje);
    }

    @Test
    @DisplayName("Deve retornar dados meteorológicos filtrando por cidade com sucesso!")
    void deveRetornarDadosComCidadeInformadaComSucesso() {

        LocalDate dataDeHoje = LocalDate.now();

        Clima clima = new Clima(
                "Canoas",
                dataDeHoje.plusDays(1),
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        when(climaRepository
                .findAllByCidadeAndDataGreaterThanEqualOrderByDataAsc("Canoas", dataDeHoje))
                .thenReturn(List.of(clima));

        List<ClimaResponse> response =
                climaService.buscarDadoMeteorologicoPorCidade("Canoas");

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals(1, response.size(), "Deve retornar uma lista com apenas um resultado!");
        assertEquals("Canoas", response.get(0).cidade(), "Deve retornar Canoas!");

        verify(climaRepository)
                .findAllByCidadeAndDataGreaterThanEqualOrderByDataAsc("Canoas", dataDeHoje);
    }

    @Test
    @DisplayName("Deve lançar exceção 404 quando não houver dados para a cidade informada!")
    void deveLancarExcecaoQuandoNaoHouverDadosParaComCidadeInformada() {

        LocalDate hoje = LocalDate.now();

        when(climaRepository
                .findAllByCidadeAndDataGreaterThanEqualOrderByDataAsc("Canoas", hoje))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.buscarDadoMeteorologicoPorCidade("Canoas")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Deve retornar status code 404!");
        assertEquals("Nenhum dado meteorológico cadastrado para a cidade: Canoas!",
                exception.getReason(), "Deve retornar mensagem de nenhum dado meteorológico cadastrado para Canoas!");

        verify(climaRepository)
                .findAllByCidadeAndDataGreaterThanEqualOrderByDataAsc("Canoas", hoje);
    }



}
