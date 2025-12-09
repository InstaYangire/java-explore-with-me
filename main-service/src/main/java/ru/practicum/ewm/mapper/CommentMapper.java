package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

@Component
public class CommentMapper {

    public Comment toEntity(NewCommentDto dto, Event event, User author) {
        return Comment.builder()
                .event(event)
                .author(author)
                .text(dto.getText())
                .created(java.time.LocalDateTime.now())
                .build();
    }

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .authorId(comment.getAuthor().getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}