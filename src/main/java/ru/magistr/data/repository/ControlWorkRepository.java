package ru.magistr.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.magistr.data.entity.ControlWork;

import java.util.Optional;

public interface ControlWorkRepository extends JpaRepository<ControlWork, Long> {
    Optional<ControlWork> findByExternalId(String externalId);
    void deleteByExternalId(String externalId);
}