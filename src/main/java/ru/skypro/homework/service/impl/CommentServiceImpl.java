package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.request.CreateOrUpdateComment;
import ru.skypro.homework.dto.response.Comment;
import ru.skypro.homework.dto.response.Comments;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Override
    public Comments getCommentsByAdId(Integer adId) {
        log.info("Получение комментариев для объявления с id: {}", adId);

        if (!adRepository.existsById(adId)) {
            throw new ResponseStatusException( // ✅ ИСПРАВИТЬ
                    HttpStatus.NOT_FOUND, "Объявление не найдено: " + adId);
        }

        List<CommentEntity> comments = commentRepository.findByAdPk(adId);
        return commentMapper.toCommentsDto(comments);
    }

    @Override
    public Comment createComment(Integer adId, CreateOrUpdateComment createComment, Authentication authentication) {
        log.info("Создание комментария к объявлению {} пользователем {}", adId, authentication.getName());

        UserEntity author = userService.getCurrentUser(authentication);

        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Объявление не найдено: " + adId));


        CommentEntity comment = commentMapper.toEntity(createComment, author, ad);
        CommentEntity savedComment = commentRepository.save(comment);

        log.info("Комментарий создан с id: {}", savedComment.getPk());
        return commentMapper.toDto(savedComment);
    }

    @Override
    public Comment updateComment(Integer commentId, CreateOrUpdateComment updateComment) {
        log.info("Обновление комментария с id: {}", commentId);

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Комментарий не найден: " + commentId));

        commentMapper.updateEntity(updateComment, comment);
        CommentEntity updatedComment = commentRepository.save(comment);

        return commentMapper.toDto(updatedComment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        log.info("Удаление комментария с id: {}", commentId);

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Комментарий не найден: " + commentId));

        commentRepository.delete(comment);

        log.info("Комментарий с id: {} удален", commentId);
    }

    @Override
    public boolean existsById(Integer id) {
        return commentRepository.existsById(id);
    }
}