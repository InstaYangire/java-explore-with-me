package ru.practicum.ewm.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private String eventDate;
    private CategoryDto category;
    private UserDto initiator;
    private Boolean paid;
    private Integer confirmedRequests;
    private Integer views;
}