package com.db.weather_app_back.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "clima",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cidade_data",
                        columnNames = {"cidade", "data"}
                )
        }
)


public class Clima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cidade;
    private LocalDate data;
    private Tempo tempoDia;
    private Tempo tempoNoite;
    private int tempMinima;
    private int tempMaxima;
    private int precipitacao;
    private int humidade;
    private int velocidadeVento;


}
