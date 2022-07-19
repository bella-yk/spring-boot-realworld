package io.spring.infrastructure.jpa.repository;

import io.spring.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    Page<History> findAllByArticleId(String articleId, Pageable pageable);

    History findHistoryById(Integer id);
}
