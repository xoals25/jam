package com.tang.game.participant.domain;

import static com.tang.core.type.ParticipantStatus.WAIT;

import com.tang.core.domain.BaseEntity;
import com.tang.core.type.ParticipantStatus;
import com.tang.game.room.domain.Room;
import java.time.LocalDateTime;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.envers.AuditOverride;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@SQLDelete(sql = "UPDATE participant SET deleted_at = current_timestamp WHERE id = ? AND status != 'LEAVE'")
public class Participant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Room room;

  private Long userId;

  @Enumerated(EnumType.STRING)
  private ParticipantStatus status;

  private LocalDateTime deletedAt;

  public static Participant from(Room room) {
    return Participant.builder()
        .room(room)
        .userId(room.getHostUserId())
        .status(WAIT)
        .build();
  }

  public static Participant of(Room room, Long userId) {
    return Participant.builder()
        .room(room)
        .userId(userId)
        .status(ParticipantStatus.WAIT)
        .build();
  }
}
