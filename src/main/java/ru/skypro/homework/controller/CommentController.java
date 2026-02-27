package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.request.CreateOrUpdateComment;
import ru.skypro.homework.dto.response.Comment;
import ru.skypro.homework.dto.response.Comments;

import java.util.ArrayList;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentController {

    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer id) {
        log.info("Запрос на получение комментариев для объявления");
        // Код получения комментариев
        Comments comments = new Comments();
        comments.setCount(0);
        comments.setResults(new ArrayList<>());
        return ResponseEntity.ok(comments);
        //return ResponseEntity.ok(new Comments());
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Integer id, @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        log.info("Запрос на добавление комментария к объявлению");
        // Код добавления комментария
        Comment comment = new Comment();
        comment.setPk(1);
        comment.setAuthor(1);
        comment.setAuthorFirstName("Иван");
        comment.setAuthorImage("/users/me/image");
        comment.setCreatedAt(System.currentTimeMillis());
        comment.setText(createOrUpdateComment.getText());
        return ResponseEntity.ok(comment);
        //return ResponseEntity.ok(new Comment());
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer adId, @PathVariable Integer commentId) {
        log.info("Запрос на удаление комментария");
        // Код удаления комментария
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Integer adId, @PathVariable Integer commentId, @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        log.info("Запрос на обновление комментария");
        // Код обновления комментария
        Comment comment = new Comment();
        comment.setPk(commentId);
        comment.setAuthor(1);
        comment.setAuthorFirstName("Иван");
        comment.setAuthorImage("/users/me/image");
        comment.setCreatedAt(System.currentTimeMillis());
        comment.setText(createOrUpdateComment.getText());
        return ResponseEntity.ok(comment);
        //return ResponseEntity.ok(new Comment());
    }
}