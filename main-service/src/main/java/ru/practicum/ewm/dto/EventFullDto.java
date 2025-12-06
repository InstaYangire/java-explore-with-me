package ru.practicum.ewm.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private String eventDate;
    private String createdOn;
    private String publishedOn;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String state;
    private CategoryDto category;
    private UserDto initiator;
    private LocationDto location;
    private Integer confirmedRequests;
    private Long views;
}