package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentsController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long eventId) {
        return commentService.getComments(eventId);
    }
}