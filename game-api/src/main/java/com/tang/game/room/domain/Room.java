package com.tang.game.room.domain;

import com.tang.core.domain.BaseEntity;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.common.type.TeamType;
import com.tang.game.room.dto.RoomForm;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.envers.AuditOverride;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@AuditOverride(forClass = BaseEntity.class)
@SQLDelete(sql = "UPDATE room SET deleted_at = current_timestamp, status = 'DELETE' WHERE id = ?")
public class Room extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long hostUserId;

  private String title;

  private String password;

  private int limitedNumberPeople;

  @Enumerated(EnumType.STRING)
  private GameType gameType;

  @Enumerated(EnumType.STRING)
  private TeamType teamType;

  @Enumerated(EnumType.STRING)
  private RoomStatus status;

  public static Room from(RoomForm form) {
    return Room.builder()
        .hostUserId(form.getUserId())
        .title(form.getTitle())
        .password(form.getPassword())
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
