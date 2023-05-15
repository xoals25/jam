package com.tang.core.repository;

import com.tang.core.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
  boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
