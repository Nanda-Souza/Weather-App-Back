package com.db.weather_app_back.domain.service;

import com.db.weather_app_back.domain.dto.ClimaRequest;
import com.db.weather_app_back.domain.dto.ClimaResponse;
import com.db.weather_app_back.domain.entity.Clima;
import com.db.weather_app_back.domain.repository.ClimaRepository;
import com.db.weather_app_back.domain.validation.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class ClimaService {

    private final ClimaRepository climaRepository;

    public ClimaService(ClimaRepository climaRepository) {this.climaRepository = climaRepository;}

    public ClimaResponse cadastrarDadoMeteorologico(ClimaRequest climaRequest) {

        if (!DataValidator.dataDeCadastroValida(climaRequest.data())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data está no formato inválido! Favor fornecer data no formato aaaa-mm-dd!");
        }


        Clima clima =  new Clima(
                climaRequest.cidade(),
                LocalDate.parse(climaRequest.data()),
                climaRequest.tempoDia(),
                climaRequest.tempoNoite(),
                climaRequest.tempMinima(),
                climaRequest.tempMaxima(),
                climaRequest.precipitacao(),
                climaRequest.humidade(),
                climaRequest.velocidadeVento()
        );

        Clima climaSalvo = climaRepository.save(clima);

        return new ClimaResponse(
                climaSalvo.getId(),
                climaSalvo.getCidade(),
                climaSalvo.getData().toString(),
                climaSalvo.getTempoDia().toString(),
                climaSalvo.getTempoNoite().toString(),
                climaSalvo.getTempMinima(),
                climaSalvo.getTempMaxima(),
                climaSalvo.getPrecipitacao(),
                climaSalvo.getHumidade(),
                climaSalvo.getVelocidadeVento()
        );

    }

    public ClimaResponse buscarDadoMeteorologicoPorId(Long id){
        Clima clima = climaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Dado Meteorológico com Id " + id + " não encontrado!")
                );

        return new ClimaResponse(
                clima.getId(),
                clima.getCidade(),
                clima.getData().toString(),
                clima.getTempoDia().toString(),
                clima.getTempoNoite().toString(),
                clima.getTempMinima(),
                clima.getTempMaxima(),
                clima.getPrecipitacao(),
                clima.getHumidade(),
                clima.getVelocidadeVento()
        );
    }


}
