package com.db.weather_app_back.domain.controller;


import com.db.weather_app_back.domain.repository.ClimaRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Transactional

public class ClimaIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ClimaRepository climaRepository;

    @Test
    @DisplayName("Deve cadastrar os dados meteorológicos com sucesso!")
    void deveSalvarDadosMeteorologicosComSucesso() throws Exception {
        String json = """
                {
                    "cidade": "Canoas",
                    "data": "2026-03-16",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
            """;

        mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cidade").value("Canoas"))
                .andExpect(jsonPath("$.data").value("2026-03-16"))
                .andExpect(jsonPath("$.tempoDia").value("LIMPO"))
                .andExpect(jsonPath("$.tempoNoite").value("LIMPO"))
                .andExpect(jsonPath("$.tempMinima").value(10))
                .andExpect(jsonPath("$.tempMaxima").value(20))
                .andExpect(jsonPath("$.precipitacao").value(5))
                .andExpect(jsonPath("$.humidade").value(10))
                .andExpect(jsonPath("$.velocidadeVento").value(15));



    }

    @Test
    @DisplayName("Deve retornar dados meteorológicos quando buscador por id!")
    void deveBuscarDadosMeteorologicosPorIdComSucesso() throws Exception {
        String json = """
                {
                    "cidade": "Canoas",
                    "data": "2026-03-16",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
                """;

        String response = mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number idNumber = JsonPath.read(response, "$.id");
        Long id = idNumber.longValue();

        mockMvc.perform(get("/clima/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cidade").value("Canoas"))
                .andExpect(jsonPath("$.data").value("2026-03-16"))
                .andExpect(jsonPath("$.tempoDia").value("LIMPO"))
                .andExpect(jsonPath("$.tempoNoite").value("LIMPO"))
                .andExpect(jsonPath("$.tempMinima").value(10))
                .andExpect(jsonPath("$.tempMaxima").value(20))
                .andExpect(jsonPath("$.precipitacao").value(5))
                .andExpect(jsonPath("$.humidade").value(10))
                .andExpect(jsonPath("$.velocidadeVento").value(15));

    }

    @Test
    @DisplayName("Deve retornar lista de dados meteorológicos sem informar cidade!")
    void deveRetornarDadosSemCidadeInformada() throws Exception {

        LocalDate diaDeHoje = LocalDate.now().plusDays(1);

        String json = """
                {
                    "cidade": "Canoas",
                    "data": "%s",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
            """.formatted(diaDeHoje);

        mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clima/buscar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cidade").value("Canoas"))
                .andExpect(jsonPath("$[0].data").value(diaDeHoje.toString()))
                .andExpect(jsonPath("$[0].tempoDia").value("LIMPO"))
                .andExpect(jsonPath("$[0].tempoNoite").value("LIMPO"))
                .andExpect(jsonPath("$[0].tempMinima").value(10))
                .andExpect(jsonPath("$[0].tempMaxima").value(20))
                .andExpect(jsonPath("$[0].precipitacao").value(5))
                .andExpect(jsonPath("$[0].humidade").value(10))
                .andExpect(jsonPath("$[0].velocidadeVento").value(15));
    }

    @Test
    @DisplayName("Deve retornar dados meteorológicos filtrando por cidade!")
    void deveRetornarDadosComCidadeInformada() throws Exception {

        LocalDate diaDeHoje = LocalDate.now().plusDays(1);

        String json = """
                {
                    "cidade": "Canoas",
                    "data": "%s",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
            """.formatted(diaDeHoje);;

        mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clima/buscar")
                        .param("cidade", "Canoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cidade").value("Canoas"))
                .andExpect(jsonPath("$[0].data").value(diaDeHoje.toString()))
                .andExpect(jsonPath("$[0].tempoDia").value("LIMPO"))
                .andExpect(jsonPath("$[0].tempoNoite").value("LIMPO"))
                .andExpect(jsonPath("$[0].tempMinima").value(10))
                .andExpect(jsonPath("$[0].tempMaxima").value(20))
                .andExpect(jsonPath("$[0].precipitacao").value(5))
                .andExpect(jsonPath("$[0].humidade").value(10))
                .andExpect(jsonPath("$[0].velocidadeVento").value(15));
    }

    @Test
    @DisplayName("Deve retornar dados meteorológicos do dia atual por cidade com sucesso!")
    void deveRetornarDadosDoDiaAtualPorCidade() throws Exception {

        LocalDate diaDeHoje = LocalDate.now();

        String json = """
                {
                    "cidade": "Canoas",
                    "data": "%s",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
            """.formatted(diaDeHoje);

        mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clima/buscar/hoje")
                        .param("cidade", "Canoas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cidade").value("Canoas"))
                .andExpect(jsonPath("$.data").value(diaDeHoje.toString()))
                .andExpect(jsonPath("$.tempoDia").value("LIMPO"))
                .andExpect(jsonPath("$.tempoNoite").value("LIMPO"))
                .andExpect(jsonPath("$.tempMinima").value(10))
                .andExpect(jsonPath("$.tempMaxima").value(20))
                .andExpect(jsonPath("$.precipitacao").value(5))
                .andExpect(jsonPath("$.humidade").value(10))
                .andExpect(jsonPath("$.velocidadeVento").value(15));
    }

    @Test
    @DisplayName("Deve retornar dados meteorológicos dos próximos dias por cidade com sucesso!")
    void deveRetornarDadosDosProximosDiasPorCidade() throws Exception {

        LocalDate hoje = LocalDate.now();
        int dias = 3;

        String jsonUm = """
                {
                    "cidade": "Canoas",
                    "data": "%s",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
            """.formatted(hoje.plusDays(1));

        mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUm))
                .andExpect(status().isCreated());

        String jsonDois = """
                {
                    "cidade": "Canoas",
                    "data": "%s",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
            """.formatted(hoje.plusDays(2));

        mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDois))
                .andExpect(status().isCreated());

        String jsonTres = """
                {
                    "cidade": "Canoas",
                    "data": "%s",
                    "tempoDia": "LIMPO",
                    "tempoNoite": "LIMPO",
                    "tempMinima": 10,
                    "tempMaxima": 20,
                    "precipitacao": 5,
                    "humidade": 10,
                    "velocidadeVento": 15
                }
            """.formatted(hoje.plusDays(5));

        mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTres))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clima/buscar/proximos/{dia}/dias", dias)
                        .param("cidade", "Canoas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cidade").value("Canoas"))
                .andExpect(jsonPath("$[0].data").value(hoje.plusDays(1).toString()))
                .andExpect(jsonPath("$[0].tempoDia").value("LIMPO"))
                .andExpect(jsonPath("$[0].tempoNoite").value("LIMPO"))
                .andExpect(jsonPath("$[0].tempMinima").value(10))
                .andExpect(jsonPath("$[0].tempMaxima").value(20))
                .andExpect(jsonPath("$[0].precipitacao").value(5))
                .andExpect(jsonPath("$[0].humidade").value(10))
                .andExpect(jsonPath("$[0].velocidadeVento").value(15))
                .andExpect(jsonPath("$[1].cidade").value("Canoas"))
                .andExpect(jsonPath("$[1].data").value(hoje.plusDays(2).toString()))
                .andExpect(jsonPath("$[1].tempoDia").value("LIMPO"))
                .andExpect(jsonPath("$[1].tempoNoite").value("LIMPO"))
                .andExpect(jsonPath("$[1].tempMinima").value(10))
                .andExpect(jsonPath("$[1].tempMaxima").value(20))
                .andExpect(jsonPath("$[1].precipitacao").value(5))
                .andExpect(jsonPath("$[1].humidade").value(10))
                .andExpect(jsonPath("$[1].velocidadeVento").value(15));
    }

    @Test
    @DisplayName("Deve editar os dados meteorológicos com sucesso!")
    void deveEditarDadosMeteorologicosComSucesso() throws Exception {

        String jsonCreate = """
        {
            "cidade": "Canoas",
            "data": "2026-03-16",
            "tempoDia": "LIMPO",
            "tempoNoite": "LIMPO",
            "tempMinima": 10,
            "tempMaxima": 20,
            "precipitacao": 5,
            "humidade": 10,
            "velocidadeVento": 15
        }
        """;

        String response = mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreate))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number idNumber = JsonPath.read(response, "$.id");
        Long id = idNumber.longValue();

        String jsonUpdate = """
        {
            "tempoDia": "TEMPESTADE",
            "tempoNoite": "TEMPESTADE",
            "tempMinima": 12,
            "tempMaxima": 22,
            "precipitacao": 6,
            "humidade": 11,
            "velocidadeVento": 16
        }
        """;

        mockMvc.perform(patch("/clima/editar/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.cidade").value("Canoas"))
                .andExpect(jsonPath("$.data").value("2026-03-16"))
                .andExpect(jsonPath("$.tempoDia").value("TEMPESTADE"))
                .andExpect(jsonPath("$.tempoNoite").value("TEMPESTADE"))
                .andExpect(jsonPath("$.tempMinima").value(12))
                .andExpect(jsonPath("$.tempMaxima").value(22))
                .andExpect(jsonPath("$.precipitacao").value(6))
                .andExpect(jsonPath("$.humidade").value(11))
                .andExpect(jsonPath("$.velocidadeVento").value(16));
    }

    @Test
    @DisplayName("Deve excluir os dados meteorológicos com sucesso!")
    void deveExcluirDadosMeteorologicosComSucesso() throws Exception {

        String json = """
        {
            "cidade": "Canoas",
            "data": "2026-03-16",
            "tempoDia": "LIMPO",
            "tempoNoite": "LIMPO",
            "tempMinima": 10,
            "tempMaxima": 20,
            "precipitacao": 5,
            "humidade": 10,
            "velocidadeVento": 15
        }
        """;

        String response = mockMvc.perform(post("/clima/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number idNumber = JsonPath.read(response, "$.id");
        Long id = idNumber.longValue();

        mockMvc.perform(delete("/clima/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clima/{id}", id))
                .andExpect(status().isNotFound());

    }


}
