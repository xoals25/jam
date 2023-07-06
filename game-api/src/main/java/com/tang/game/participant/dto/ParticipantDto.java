package com.tang.game.participant.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tang.core.type.ParticipantStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class ParticipantDto {
  private Long id;

  private Long userId;

  private String nickname;

  private ParticipantStatus status;

  @QueryProjection
  public ParticipantDto(Long id, Long userId, String nickname, ParticipantStatus status) {
    this.id = id;
    this.userId = userId;
    this.nickname = nickname;
    this.status = status;
  }
}
