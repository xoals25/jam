package com.tang.game.participant.domain;

import com.tang.core.domain.BaseEntity;
import com.tang.game.participant.type.ParticipantStatus;
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

  private Long roomId;

  private Long userId;

  @Enumerated(EnumType.STRING)
  private ParticipantStatus status;

  private int gameOrder;

  public static Participant from(Room room) {
    return Participant.builder()
        .room(room)
        .userId(room.getHostUserId())
        .status(WAIT)
        .gameOrder(1)
        .build();
  }
}
