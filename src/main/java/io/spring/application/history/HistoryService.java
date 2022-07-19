package io.spring.application.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.spring.application.data.HistoryData;
import io.spring.application.data.HistoryDataList;
import io.spring.common.enums.HistoryAction;
import io.spring.core.article.Article;
import io.spring.entity.History;
import io.spring.infrastructure.jpa.repository.HistoryRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.joda.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Aspect
public class HistoryService {

  private HistoryRepository historyRepository;
  private ObjectMapper objectMapper;

  @AfterReturning(
      pointcut = "execution(* io.spring.application.article.ArticleCommandService.*(..))",
      returning = "results")
  public void saveOfCreateHistory(JoinPoint joinPoint, Article results) {
    HistoryAction action =
        joinPoint.getSignature().getName().equals("createArticle")
            ? HistoryAction.NEW
            : HistoryAction.EDIT;

    this.saveHistory(action, results);
  }

  @After(value = "execution(* io.spring.core.article.ArticleRepository.remove(..))")
  public void saveOfDeleteHistory(JoinPoint joinPoint) {
    Article article = (Article) Arrays.stream(joinPoint.getArgs()).findFirst().get();
    this.saveHistory(HistoryAction.DELETE, article);
  }

  private void saveHistory(HistoryAction action, Article article) {
    History history =
        new History(action, article.getId(), article.getUserId(), objectToJsonString(article));

    historyRepository.saveAndFlush(history);
  }

  public Optional<HistoryData> findHistoryById(Integer id) {
    return historyRepository
        .findById(id)
        .map(
            history -> {
              return new HistoryData(
                  history.getId(),
                  history.getHistoryAction(),
                  history.getArticleId(),
                  LocalDateTime.fromDateFields(history.getCreatedAt()).toDateTime(),
                  new Gson().fromJson(history.getArticleData(), Article.class));
            });
  }

  public HistoryDataList findHistoriesByArticle(Article article, int page, int limit) {
    Pageable pageable = PageRequest.of(page, limit);
    Page<History> historyPage = historyRepository.findAllByArticleId(article.getId(), pageable);
    List<HistoryData> histories =
        Optional.of(historyPage.getContent()).orElse(Collections.emptyList()).stream()
            .map(
                history ->
                    new HistoryData(
                        history.getId(),
                        history.getHistoryAction(),
                        history.getArticleId(),
                        LocalDateTime.fromDateFields(history.getCreatedAt()).toDateTime(),
                        new Gson().fromJson(history.getArticleData(), Article.class)))
            .collect(Collectors.toList());
    return new HistoryDataList(histories, Math.toIntExact(historyPage.getTotalElements()));
  }

  private String objectToJsonString(Object o) {
    if (o == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      return "";
    }
  }
}
