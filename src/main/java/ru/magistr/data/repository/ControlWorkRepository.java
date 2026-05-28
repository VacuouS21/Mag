package ru.magistr.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.magistr.data.entity.ControlWork;

import java.util.Optional;

@Repository
public interface ControlWorkRepository extends JpaRepository<ControlWork, Long> {
    Optional<ControlWork> findByExternalId(String externalId);
}