package ru.skypro.homework.repository;

import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<AdEntity, Integer> {

    List<AdEntity> findByAuthor(UserEntity author);

    List<AdEntity> findByAuthorId(Integer authorId);
}