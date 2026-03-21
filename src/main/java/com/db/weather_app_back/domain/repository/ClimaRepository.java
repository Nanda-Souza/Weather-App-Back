package com.db.weather_app_back.domain.repository;

import com.db.weather_app_back.domain.entity.Clima;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClimaRepository extends JpaRepository<Clima, Long> {
    Optional<Clima> findByCidadeAndData(String cidade, LocalDate data);
    boolean existsByCidadeAndData(String cidade, LocalDate data);
    List<Clima> findAllByCidadeAndDataGreaterThanEqualOrderByDataAsc(String cidade, LocalDate data);
    List<Clima> findByDataGreaterThanEqualOrderByDataAsc(LocalDate data);

}
