package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.response.Comment;
import ru.skypro.homework.dto.response.Comments;
import ru.skypro.homework.dto.request.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.entity.AdEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public Comment toDto(CommentEntity entity) {
        if (entity == null) {
            return null;
        }

        Comment dto = new Comment();
        dto.setPk(entity.getPk());
        dto.setText(entity.getText());

        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt()
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli());
        }

        UserEntity author = entity.getAuthor();
        if (author != null) {
            dto.setAuthor(author.getId());
            dto.setAuthorFirstName(author.getFirstName());

            if (author.getImagePath() != null && !author.getImagePath().isEmpty()) {
                dto.setAuthorImage("/users/" + author.getId() + "/image");
            }
        }

        return dto;
    }

    public Comments toCommentsDto(List<CommentEntity> entities) {
        Comments dto = new Comments();

        if (entities == null || entities.isEmpty()) {
            dto.setCount(0);
            dto.setResults(List.of());
            return dto;
        }

        dto.setCount(entities.size());

        List<Comment> commentDtos = entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        dto.setResults(commentDtos);
        return dto;
    }

    public CommentEntity toEntity(CreateOrUpdateComment createComment,
                                  UserEntity author,
                                  AdEntity ad) {
        if (createComment == null || author == null || ad == null) {
            return null;
        }

        CommentEntity entity = new CommentEntity();
        entity.setText(createComment.getText());
        entity.setAuthor(author);
        entity.setAd(ad);

        return entity;
    }

    public void updateEntity(CreateOrUpdateComment updateComment, CommentEntity entity) {
        if (updateComment == null || entity == null) {
            return;
        }

        if (updateComment.getText() != null) {
            entity.setText(updateComment.getText());
        }
    }
}