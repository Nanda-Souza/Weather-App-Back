package com.db.weather_app_back.domain.repository;

import com.db.weather_app_back.domain.entity.Clima;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClimaRepository extends JpaRepository<Clima, Long> {
}
