package com.tang.game.room.dto;

import com.tang.game.common.type.GameType;
import com.tang.game.common.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomForm {
    private Long userId;

    private String title;

    private String password;

    private int limitedNumberPeople = 4;

    private GameType gameType = GameType.GAME_ORDER;

    private TeamType teamType = TeamType.PERSONAL;
}
