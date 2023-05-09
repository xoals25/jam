package com.tang.game.room.repository;

import com.tang.game.common.type.RoomStatus;
import com.tang.game.room.domain.Room;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

  Optional<Room> findByIdAndStatus(Long roomId, RoomStatus status);

  boolean existsByTitleAndStatus(String title, RoomStatus status);
}
