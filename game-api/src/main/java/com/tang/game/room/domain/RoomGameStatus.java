package com.tang.game.room.domain;

import static com.tang.game.room.util.Constants.GAME_CREATE_FIRST_ORDER;

import com.tang.core.domain.BaseEntity;
import com.tang.game.room.type.GameStatus;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
public class RoomGameStatus extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(mappedBy = "roomGameStatus", orphanRemoval = true)
  private Room room;

  private int gameTalkOrder;

  @Enumerated(EnumType.STRING)
  private GameStatus status;

  private LocalDateTime startedAt;

  public static RoomGameStatus from(Room room) {
    return RoomGameStatus.builder()
        .room(room)
        .gameTalkOrder(GAME_CREATE_FIRST_ORDER)
        .status(GameStatus.WAIT)
        .build();
  }
}
