package com.tang.game.participant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantCount {
  private int limitedNumberPeople;
  private int currentNumberPeople;

  public static ParticipantCount of(int limitCount, int currentCount) {
    return ParticipantCount.builder()
        .limitedNumberPeople(limitCount)
        .currentNumberPeople(currentCount)
        .build();
  }
}
