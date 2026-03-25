package com.db.weather_app_back.domain.service;

import com.db.weather_app_back.domain.dto.ClimaRequest;
import com.db.weather_app_back.domain.dto.ClimaResponse;
import com.db.weather_app_back.domain.dto.ClimaUpdateRequest;
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

        LocalDate diaDeHoje = LocalDate.now();

        when(climaRepository.findByDataGreaterThanEqualOrderByDataAsc(diaDeHoje))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.buscarDadoMeteorologicoPorCidade(null)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Deve retornar status code 404!");
        assertEquals("Nenhum dado meteorológico cadastrado!",
                exception.getReason(), "Deve retornar mensagem de nenhum dado meteorológico cadastrado!");

        verify(climaRepository).findByDataGreaterThanEqualOrderByDataAsc(diaDeHoje);
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

    @Test
    @DisplayName("Deve retornar os dados meteorológicos do dia atual para a cidade com sucesso!")
    void deveRetornarDadosDoDiaAtualPorCidadeComSucesso() {

        String cidade = "Canoas";
        LocalDate dataDeHoje = LocalDate.now();

        Clima clima = new Clima(
                cidade,
                dataDeHoje,
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        when(climaRepository.findByCidadeAndData(cidade, dataDeHoje))
                .thenReturn(Optional.of(clima));

        ClimaResponse response =
                climaService.buscarDadoMeteorologicoDoDiaAtualPorCidade(cidade);

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals("Canoas", response.cidade(), "Deve retornar Canoas!");
        assertEquals(dataDeHoje.toString(), response.data(), "Deve retornar a data de hoje!");

        verify(climaRepository).findByCidadeAndData(cidade, dataDeHoje);
    }

    @Test
    @DisplayName("Deve lançar exceção 404 quando não houver dados meteorológicos do dia atual para a cidade!")
    void deveLancarExcecaoQuandoNaoEncontrarDadosDoDiaAtualPorCidade() {

        String cidade = "Canoas";
        LocalDate dataDeHoje = LocalDate.now();

        when(climaRepository.findByCidadeAndData(cidade, dataDeHoje))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.buscarDadoMeteorologicoDoDiaAtualPorCidade(cidade)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(),
                "Deve retornar status code 404!");

        assertEquals("Nenhum dado meteorológico do dia de hoje para a cidade: Canoas!",
                exception.getReason(),
                "Deve retornar mensagem de nenhum dado meteorológico do dia de hoje para Canoas!");

        verify(climaRepository).findByCidadeAndData(cidade, dataDeHoje);
    }

    @Test
    @DisplayName("Deve retornar os dados meteorológicos dos próximos dias para a cidade com sucesso!")
    void deveRetornarDadosDosProximosDiasPorCidadeComSucesso() {

        String cidade = "Canoas";
        int dias = 3;

        LocalDate dataDehoje = LocalDate.now();

        Clima climaUm = new Clima(
                cidade,
                dataDehoje.plusDays(1),
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        Clima climaDois = new Clima(
                cidade,
                dataDehoje.plusDays(2),
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        Clima climaTres = new Clima(
                cidade,
                dataDehoje.plusDays(3),
                Tempo.LIMPO,
                Tempo.LIMPO,
                10,
                20,
                5,
                10,
                15
        );

        when(climaRepository.findByCidadeAndDataBetween(cidade, dataDehoje, dataDehoje.plusDays(dias)))
                .thenReturn(List.of(climaUm, climaDois, climaTres));

        List<ClimaResponse> response =
                climaService.buscarDadosMeteorologicoDosProximosSeteDiasPorCidade(cidade, dias);

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals(3, response.size(), "Deve retornar 2 resultados!");
        assertEquals("Canoas", response.get(0).cidade(), "Primeiro resultado deve retornar Canoas!");
        assertEquals("Canoas", response.get(1).cidade(), "Segundo resultado deve retornar Canoas!");
        assertEquals("Canoas", response.get(2).cidade(), "Segundo resultado deve retornar Canoas!");
        assertEquals(climaUm.getData().toString(), response.get(0).data(), "Deve retornar a data correta para cidade de Canoas!");
        assertEquals(climaDois.getData().toString(), response.get(1).data(), "Deve retornar a data correta para cidade de Canoas!");
        assertEquals(climaTres.getData().toString(), response.get(2).data(), "Deve retornar a data correta para cidade de Canoas!");

        verify(climaRepository)
                .findByCidadeAndDataBetween(cidade, dataDehoje, dataDehoje.plusDays(dias));
    }

    @Test
    @DisplayName("Deve lançar exceção 422 quando a quantidade de dias for inválida!")
    void deveLancarExcecaoQuandoNumeroDeDiasForInvalido() {

        String cidade = "Canoas";
        int dias = 0;

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.buscarDadosMeteorologicoDosProximosSeteDiasPorCidade(cidade, dias)
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatusCode(),
                "Deve retornar status code 422!");

        assertEquals("A previsão do tempo deve ser até para os proximo 7 dias!",
                exception.getReason(),
                "Deve retornar que a previsão do tempo deve ser até para os proximo 7 dias!");

        verify(climaRepository, never())
                .findByCidadeAndDataBetween(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção 404 quando não houver dados para os próximos dias!")
    void deveLancarExcecaoQuandoNaoHouverDados() {

        String cidade = "Canoas";
        int dias = 3;

        LocalDate hoje = LocalDate.now();

        when(climaRepository.findByCidadeAndDataBetween(cidade, hoje, hoje.plusDays(dias)))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.buscarDadosMeteorologicoDosProximosSeteDiasPorCidade(cidade, dias)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(),
                "Deve retornar status code 404!");

        assertEquals("Nenhum dado meteorológico para os próximos sete dias para a cidade: Canoas!",
                exception.getReason(),
                "Deve retornar mensagem de não encontrado!");

        verify(climaRepository)
                .findByCidadeAndDataBetween(cidade, hoje, hoje.plusDays(dias));
    }

    @Test
    @DisplayName("Deve atualizar os dados meteorológicos com sucesso!")
    void deveAtualizarDadosMeteorologicosComSucesso() {

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

        ClimaUpdateRequest request = new ClimaUpdateRequest(
                Tempo.TEMPESTADE,
                Tempo.TEMPESTADE,
                12,
                22,
                6,
                11,
                16
        );

        when(climaRepository.findById(id)).thenReturn(Optional.of(clima));
        when(climaRepository.save(any(Clima.class))).thenReturn(clima);

        ClimaResponse response = climaService.editarDadosMeteorologicos(id, request);

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals("TEMPESTADE", response.tempoDia(), "Deve retornar TEMPESTADE para tempo dia!");
        assertEquals("TEMPESTADE", response.tempoNoite(), "Deve retornar TEMPESTADE para tempo noite!");
        assertEquals(12, response.tempMinima(), "Deve retornar 12 para temperatura mínima!");
        assertEquals(22, response.tempMaxima(),"Deve retornar 22 para temperatura máxima!");
        assertEquals(6, response.precipitacao(), "Deve retornar 6 para precipitação!");
        assertEquals(11, response.humidade(), "Deve retornar 11 para humidade!");
        assertEquals(16, response.velocidadeVento(), "Deve retornar 16 para velocidade e vento!");

        verify(climaRepository).findById(id);
        verify(climaRepository).save(clima);
    }

    @Test
    @DisplayName("Deve atualizar apenas os campos informados!")
    void deveAtualizarApenasCamposInformados() {

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

        ClimaUpdateRequest request = new ClimaUpdateRequest(
                Tempo.TEMPESTADE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(climaRepository.findById(id)).thenReturn(Optional.of(clima));
        when(climaRepository.save(any(Clima.class))).thenReturn(clima);

        ClimaResponse response = climaService.editarDadosMeteorologicos(id, request);

        assertNotNull(response, "O retorno não pode ser nulo!");
        assertEquals("TEMPESTADE", response.tempoDia(), "Deve retornar TEMPESTADE para tempo dia!");
        assertEquals("LIMPO", response.tempoNoite(), "Deve retornar LIMPO para tempo noite!");

        verify(climaRepository).findById(id);
        verify(climaRepository).save(clima);
    }

    @Test
    @DisplayName("Deve lançar exceção 404 ao tentar atualizar dado inexistente!")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {

        Long id = 99L;

        ClimaUpdateRequest request = new ClimaUpdateRequest(
                Tempo.TEMPESTADE,
                Tempo.LIMPO,
                12,
                22,
                6,
                11,
                16
        );

        when(climaRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> climaService.editarDadosMeteorologicos(id, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Deve retornar status code 404!");
        assertEquals("Dado Meteorológico com Id 99 não encontrado!",
                exception.getReason(), "Deve retornar mensagem de não encontrado!");

        verify(climaRepository).findById(id);
        verify(climaRepository, never()).save(any());
    }



}
