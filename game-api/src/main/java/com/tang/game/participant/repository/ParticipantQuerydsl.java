package com.tang.game.participant.repository;

import static com.tang.game.participant.domain.QParticipant.participant;
import static com.tang.game.user.domain.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tang.game.participant.dto.ParticipantDto;
import com.tang.core.type.ParticipantStatus;
import com.tang.game.participant.dto.QParticipantDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ParticipantQuerydsl {

  private final JPAQueryFactory jpaQueryFactory;

  public List<ParticipantDto> findAllByRoomIdAndStatusNot(
      Long roomId,
      ParticipantStatus participantStatus
  ) {
    return jpaQueryFactory.select(new QParticipantDto(
            participant.id,
            participant.userId,
            user.nickname,
            participant.status
        )).from(participant)
        .leftJoin(user)
        .on(participant.userId.eq(user.id))
        .where(participant.room.id.eq(roomId), participant.status.ne(participantStatus))
        .fetch();
  }
}
