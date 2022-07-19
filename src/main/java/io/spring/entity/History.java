package io.spring.entity;

import io.spring.common.enums.HistoryAction;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "histories")
public class History implements Serializable {

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "action")
  @Enumerated(EnumType.STRING)
  private HistoryAction historyAction;

  @Column(name = "article_id")
  private String articleId;

  @Column(name = "user_id")
  private String userId;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", nullable = false, updatable = false)
  private Date createdAt;

  @Column(name = "article_data", columnDefinition = "text")
  private String articleData;

  public History(
      HistoryAction historyAction,
      String articleId,
      String userId,
      String articleData) {
    this.historyAction = historyAction;
    this.articleId = articleId;
    this.userId = userId;
    this.articleData = articleData;
  }
}
