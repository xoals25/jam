package com.tang.game.room.domain;

import com.tang.core.domain.BaseEntity;
import com.tang.core.type.GameType;
import com.tang.core.type.TeamType;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.participant.domain.Participant;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.type.GameStatus;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.envers.AuditOverride;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@AuditOverride(forClass = BaseEntity.class)
@SQLDelete(sql = "UPDATE room SET deleted_at = current_timestamp, status = 'DELETE' WHERE id = ?")
public class Room extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long hostUserId;

  private String title;

  private String password;

  private int limitedNumberPeople;

  @OneToMany(mappedBy = "room", orphanRemoval = true)
  private List<Participant> participants;

  private GameStatus gameStatus;

  @Enumerated(EnumType.STRING)
  private GameType gameType;

  @Enumerated(EnumType.STRING)
  private TeamType teamType;

  @Enumerated(EnumType.STRING)
  private RoomStatus status;

  private LocalDateTime deletedAt;

  public static Room from(RoomForm form) {
    return Room.builder()
        .hostUserId(form.getUserId())
        .title(form.getTitle())
        .password(form.getPassword())
        .gameStatus(GameStatus.WAIT)
        .limitedNumberPeople(form.getLimitedNumberPeople())
        .gameType(form.getGameType())
        .teamType(form.getTeamType())
        .status(RoomStatus.VALID)
        .build();
  }

  public void update(RoomForm form) {
    setTitle(form.getTitle());
    setPassword(form.getPassword());
    setGameType(form.getGameType());
    setTeamType(form.getTeamType());
    setLimitedNumberPeople(form.getLimitedNumberPeople());
  }
}
