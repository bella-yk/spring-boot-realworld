package io.spring.api;

import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.history.HistoryService;
import io.spring.core.article.ArticleRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/histories")
public class HistoryApi {

  private ArticleRepository articleRepository;
  private HistoryService historyService;


  @GetMapping(path = "/{id}")
  public ResponseEntity<?> getHistory(
      @PathVariable("id") Integer id,
      @AuthenticationPrincipal User user) {
    return historyService
        .findHistoryById(id)
        .map(historyData -> {
          if (!AuthorizationService.canWriteArticle(user, historyData.getArticleData())) {
            throw new NoAuthorizationException();
          }
          return ResponseEntity.ok(historyData);
        })
        .orElseThrow(ResourceNotFoundException::new);
  }

  @GetMapping(path = "/list")
  public ResponseEntity<?> getHistories(
      @RequestParam(value = "slug") String slug,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "limit", defaultValue = "5") int limit,
      @AuthenticationPrincipal User user) {
    return articleRepository
        .findBySlug(slug)
        .map(article -> {
          if (!AuthorizationService.canWriteArticle(user, article)) {
            throw new NoAuthorizationException();
          }

          return ResponseEntity.ok(historyService.findHistoriesByArticle(article, page, limit));
        })
        .orElseThrow(ResourceNotFoundException::new);
  }
}
