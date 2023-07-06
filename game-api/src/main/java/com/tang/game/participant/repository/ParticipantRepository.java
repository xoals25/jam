package com.tang.game.participant.repository;

import com.tang.core.type.ParticipantStatus;
import com.tang.game.participant.domain.Participant;
import com.tang.game.participant.dto.ParticipantUserIdMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

  boolean existsByRoomIdAndUserIdAndStatusNotIn(
      Long roomId,
      Long userId,
      List<ParticipantStatus> statuses
  );

  int countAllByRoomIdAndDeletedAtIsNull(Long roomId);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = "UPDATE Participant " +
      "SET status=:status, deletedAt=current_timestamp " +
      "WHERE userId=:userId " +
      "AND room.id=:roomId")
  void deleteByUserIdAndRoomIdInQuery(
      Long userId,
      Long roomId,
      ParticipantStatus status
  );

  Optional<ParticipantUserIdMapping> findTopByRoomIdAndStatusNotInOrderByModifiedAtAsc(
      Long roomId,
      List<ParticipantStatus> status
  );

  Optional<Participant> findByRoomIdAndUserId(Long roomId, Long userId);
}
