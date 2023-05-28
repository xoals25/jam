package com.tang.game.participant.domain;

import static com.tang.game.participant.type.ParticipantStatus.WAIT;
import static com.tang.game.room.util.Constants.GAME_CREATE_FIRST_ORDER;

import com.tang.core.domain.BaseEntity;
import com.tang.game.participant.type.ParticipantStatus;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.RoomForm;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Participant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Room room;

  private Long userId;

  @Enumerated(EnumType.STRING)
  private ParticipantStatus status;

  private int gameOrder;

  public static Participant from(Room room) {
    return Participant.builder()
        .room(room)
        .userId(room.getHostUserId())
        .status(WAIT)
        .gameOrder(GAME_CREATE_FIRST_ORDER)
        .build();
  }

  public static Participant of(Room room, RoomForm form, int gameOrder) {
    return Participant.builder()
        .room(room)
        .userId(form.getUserId())
        .status(ParticipantStatus.WAIT)
        .gameOrder(gameOrder)
        .build();
  }
}
