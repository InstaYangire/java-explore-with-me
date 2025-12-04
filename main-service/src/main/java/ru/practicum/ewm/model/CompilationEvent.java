package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compilation_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationEvent {

    @EmbeddedId
    private CompilationEventId id;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompilationEventId {

        @Column(name = "compilation_id")
        private Long compilationId;

        @Column(name = "event_id")
        private Long eventId;
    }
}