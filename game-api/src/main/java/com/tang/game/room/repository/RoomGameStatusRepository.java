package com.tang.game.room.repository;

import com.tang.game.room.domain.RoomGameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomGameStatusRepository extends JpaRepository<RoomGameStatus, Long> {

}
