package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.CompilationEvent;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, CompilationEvent.CompilationEventId> {
}