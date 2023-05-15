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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Getter
@Setter
@Entity
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
}
