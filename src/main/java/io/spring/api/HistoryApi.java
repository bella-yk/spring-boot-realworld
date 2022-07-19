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
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class HistoryApi {
  private ArticleRepository articleRepository;
  private HistoryService historyService;

  @GetMapping(path = "histories")
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

        return ResponseEntity.ok(historyService.findUserHistories(article, page, limit));
      })
    .orElseThrow(ResourceNotFoundException::new);
  }
}
