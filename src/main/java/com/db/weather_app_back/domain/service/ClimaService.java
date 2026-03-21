package com.db.weather_app_back.domain.service;

import com.db.weather_app_back.domain.dto.ClimaRequest;
import com.db.weather_app_back.domain.dto.ClimaResponse;
import com.db.weather_app_back.domain.entity.Clima;
import com.db.weather_app_back.domain.repository.ClimaRepository;
import com.db.weather_app_back.domain.validation.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClimaService {

    private final ClimaRepository climaRepository;

    public ClimaService(ClimaRepository climaRepository) {this.climaRepository = climaRepository;}

    public ClimaResponse cadastrarDadoMeteorologico(ClimaRequest climaRequest) {

        if (!DataValidator.dataDeCadastroValida(climaRequest.data())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data está no formato inválido! Favor fornecer data no formato aaaa-mm-dd!");
        }


        if (climaRepository.existsByCidadeAndData(climaRequest.cidade(), LocalDate.parse(climaRequest.data()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe dado meteorológico cadastrado para esta cidade na data informada!");
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

    public List<ClimaResponse> buscarDadoMeteorologicoPorCidade(String cidade){
        LocalDate diaDehoje = LocalDate.now();
        List<Clima> climas;

        if (cidade == null || cidade.isBlank()) {

            climas = climaRepository
                    .findByDataGreaterThanEqualOrderByDataAsc(diaDehoje);

            if (climas.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nenhum dado meteorológico cadastrado!");
            }

        } else {
            climas = climaRepository
                    .findAllByCidadeAndDataGreaterThanEqualOrderByDataAsc(cidade, diaDehoje);

            if (climas.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nenhum dado meteorológico cadastrado para a cidade: " + cidade + "!");
            }
        }

            return climas.stream()
                    .map(clima -> new ClimaResponse(
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
                    ))
                    .toList();

    }

    public ClimaResponse buscarDadoMeteorologicoDoDiaAtualPorCidade(String cidade){
        LocalDate dataDeHoje = LocalDate.now();



        Clima clima = climaRepository.findByCidadeAndData(cidade, dataDeHoje)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Nenhum dado meteorológico do dia de hoje para a cidade: " + cidade + "!")
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
