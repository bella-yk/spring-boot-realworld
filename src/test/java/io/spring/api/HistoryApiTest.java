package io.spring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.TestHelper;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ArticleQueryService;
import io.spring.application.data.*;
import io.spring.application.history.HistoryService;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest({HistoryApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class HistoryApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private HistoryService historyService;

  @MockBean private ArticleRepository articleRepository;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_read_article_success() throws Exception {
    String slug = "test-new-article";
    DateTime time = new DateTime();
    Article article =
        new Article(
            "Test New Article",
            "Desc",
            "Body",
            Arrays.asList("java", "spring", "jpg"),
            user.getId(),
            time);

    HistoryData historyData = TestHelper.historyDataFixture(article, user);

    when(articleRepository.findBySlug(eq(Article.toSlug(slug))))
            .thenReturn(Optional.of(article));

    when(historyService.findUserHistories(article, 0, 5))
            .thenReturn(new HistoryDataList(Lists.newArrayList(historyData), 1));

    given()
            .header("Authorization", "Token " + token)
            .queryParam("slug", slug)
            .when()
            .get("/histories")
            .then()
            .statusCode(200)
            .body("histories[0].articleId", equalTo(article.getId()));

  }

  @Test
  public void should_get_401_unAuthorize_user() throws Exception {
    String slug = "test-new-article";
    DateTime time = new DateTime();
    Article article =
            new Article(
                    "Test New Article",
                    "Desc",
                    "Body",
                    Arrays.asList("java", "spring", "jpg"),
                    "aaaa",
                    time);

    when(articleRepository.findBySlug(eq(Article.toSlug(slug))))
            .thenReturn(Optional.of(article));

    given()
            .header("Authorization", "Token " + token)
            .queryParam("slug", slug)
            .when()
            .get("/histories")
            .then()
            .statusCode(403);
  }

  @Test
  public void should_get_401_without_token() throws Exception {
    String slug = "test-new-article";
    DateTime time = new DateTime();
    Article article =
            new Article(
                    "Test New Article",
                    "Desc",
                    "Body",
                    Arrays.asList("java", "spring", "jpg"),
                    user.getId(),
                    time);

    when(articleRepository.findBySlug(eq(Article.toSlug(slug))))
            .thenReturn(Optional.of(article));

    given()
            .queryParam("slug", slug)
            .when()
            .get("/histories")
            .then()
            .statusCode(401);
  }
}
