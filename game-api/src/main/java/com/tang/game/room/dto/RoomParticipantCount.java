package com.tang.game.room.dto;

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
public class RoomParticipantCount {
  private int limitedNumberPeople;
  private int currentNumberPeople;

  public static RoomParticipantCount of(int limitCount, int currentCount) {
    return RoomParticipantCount.builder()
        .limitedNumberPeople(limitCount)
        .currentNumberPeople(currentCount)
        .build();
  }
}
