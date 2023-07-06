package com.tang.game.room.dto;

import com.tang.game.participant.dto.ParticipantDto;
import com.tang.core.type.GameType;
import com.tang.core.type.TeamType;
import com.tang.game.room.domain.Room;
import java.util.List;
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
public class EnterRoomDto {
  private Long id;

  private Long hostUserId;

  private String title;

  private String password;

  private int currentNumberPeople;

  private int limitedNumberPeople;

  private GameType gameType;

  private TeamType teamType;

  private long participantId;

  private List<ParticipantDto> participants;

  public static EnterRoomDto of(Room room, long participantId, List<ParticipantDto> participants) {
    return EnterRoomDto.builder()
        .hostUserId(room.getHostUserId())
        .title(room.getTitle())
        .password(room.getPassword())
        .limitedNumberPeople(room.getLimitedNumberPeople())
        .currentNumberPeople(participants.size())
        .gameType(room.getGameType())
        .teamType(room.getTeamType())
        .participantId(participantId)
        .participants(participants)
        .build();
  }
}
