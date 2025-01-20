package com.project.shopapp.repository;

import com.project.shopapp.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    void deleteCommentEntitiesByIdIn(List<Long> idList);
    Page<CommentEntity> getAllByProductEntityId(Long id, Pageable pageable);
    Page<CommentEntity> getAllByUserId(Long id, Pageable pageable);
}
