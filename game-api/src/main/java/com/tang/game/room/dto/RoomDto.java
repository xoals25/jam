package com.tang.game.room.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
  private Long id;

  private Long hostUserId;

  private String title;

  private String password;

  private int limitedNumberPeople;

  private GameType gameType;

  private TeamType teamType;

  private int currentNumberPeople;

  @QueryProjection
  public RoomDto(Long id, Long hostUserId, String title, String password, int limitedNumberPeople,
      GameType gameType, TeamType teamType) {
    this.id = id;
    this.hostUserId = hostUserId;
    this.title = title;
    this.password = password;
    this.limitedNumberPeople = limitedNumberPeople;
    this.gameType = gameType;
    this.teamType = teamType;
  }
}
