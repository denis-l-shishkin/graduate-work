package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.request.CreateOrUpdateComment;
import ru.skypro.homework.dto.response.Comment;
import ru.skypro.homework.dto.response.Comments;

public interface CommentService {

    Comments getCommentsByAdId(Integer adId);

    Comment createComment(Integer adId, CreateOrUpdateComment comment, Authentication authentication);

    Comment updateComment(Integer commentId, CreateOrUpdateComment comment);

    void deleteComment(Integer commentId);

    boolean existsById(Integer id);
}