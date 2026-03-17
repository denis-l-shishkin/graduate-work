package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.request.CreateOrUpdateComment;
import ru.skypro.homework.dto.response.Comment;
import ru.skypro.homework.dto.response.Comments;
import ru.skypro.homework.service.CommentService;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer id) {
        log.info("Запрос на получение комментариев для объявления с id: {}", id);
        return ResponseEntity.ok(commentService.getCommentsByAdId(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @RequestBody CreateOrUpdateComment comment,
                                              Authentication authentication) {
        log.info("Запрос на добавление комментария к объявлению с id: {} от пользователя: {}", id, authentication.getName());
        Comment createdComment = commentService.createComment(id, comment, authentication);
        return ResponseEntity.ok(createdComment);
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    @PreAuthorize("@securityUtils.isCommentOwnerOrAdmin(authentication, #commentId)")
    public ResponseEntity<?> deleteComment(@PathVariable Integer adId,
                                           @PathVariable Integer commentId) {
        log.info("Запрос на удаление комментария {} у объявления {}", commentId, adId);
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    @PreAuthorize("@securityUtils.isCommentOwnerOrAdmin(authentication, #commentId)")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @RequestBody CreateOrUpdateComment comment) {
        log.info("Запрос на обновление комментария {} у объявления {}", commentId, adId);
        Comment updatedComment = commentService.updateComment(commentId, comment);
        return ResponseEntity.ok(updatedComment);
    }
}