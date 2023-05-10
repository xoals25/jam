package com.tang.game.room.dto;

import com.tang.game.common.type.GameType;
import com.tang.game.common.type.TeamType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomForm {
  @NotNull(message = "유저 고유 번호가 빠졌습니다.")
  private Long userId;

  @NotNull(message = "방 제목을 입력해주세요.")
  private String title;

  @Length(max = 10, message = "방 비밀번호는 최대 10자리 입니다.")
  private String password;

  @Min(value = 1, message = "방 인원은 최소 1명 입니다.")
  @Max(value = 8, message = "방 인원은 최대 8명 입니다.")
  private int limitedNumberPeople = 4;

  private GameType gameType = GameType.GAME_ORDER;

  private TeamType teamType = TeamType.PERSONAL;
}
