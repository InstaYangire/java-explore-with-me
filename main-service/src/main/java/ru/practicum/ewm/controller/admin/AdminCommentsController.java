package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.CommentService;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentsController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}