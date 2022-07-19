package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.application.DateTimeCursor;
import io.spring.core.article.Article;
import io.spring.common.enums.HistoryAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryData implements io.spring.application.Node {
  private Integer id;
  private HistoryAction historyAction;
  private String articleId;
  private DateTime createdAt;
  @JsonProperty("articleData")
  private Article articleData;

  @Override
  public DateTimeCursor getCursor() {
    return new DateTimeCursor(createdAt);
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }
}
