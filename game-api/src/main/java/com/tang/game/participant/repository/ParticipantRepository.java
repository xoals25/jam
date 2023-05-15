package com.tang.game.participant.repository;

import com.tang.game.participant.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
  boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
