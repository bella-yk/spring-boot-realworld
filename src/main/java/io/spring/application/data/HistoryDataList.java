package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class HistoryDataList {
  @JsonProperty("histories")
  private final List<HistoryData> historyDatas;

  @JsonProperty("historiesCount")
  private final int count;

  public HistoryDataList(List<HistoryData> historyDatas, int count) {

    this.historyDatas = historyDatas;
    this.count = count;
  }
}
