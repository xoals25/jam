package com.tang.game.room.service;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.repository.RoomRepository;
import com.tang.game.common.type.ErrorCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  public void createRoom(RoomForm form) {
    if (roomRepository.existsByTitleAndStatus(form.getTitle(), RoomStatus.VALID)) {
      throw new JamGameException(ErrorCode.EXIST_ROOM_TITLE);
    }

    roomRepository.save(Room.from(form));
  }

  public Object searchRooms() {
    return null;
  }

  public Object searchRoom() {
    return null;
  }

  public void updateRoom(Long userId, Long roomId, RoomForm form) {
    Room room = roomRepository.findById(roomId).orElseThrow(
        () -> new JamGameException(ErrorCode.NOT_FOUND_ROOM)
    );

    validateUpdateRoom(room, form, userId);

    room.update(form);

    roomRepository.save(room);
  }

  public Object deleteRoom() {
    return null;
  }

  private void validateUpdateRoom(Room room, RoomForm form, Long userId) {
    if (!Objects.equals(room.getHostUserId(), userId)) {
      throw new JamGameException(ErrorCode.USER_ROOM_HOST_UN_MATCH);
    }

    if (roomRepository.existsByTitleAndStatusAndIdNot(form.getTitle(), RoomStatus.VALID, room.getId())) {
      throw new JamGameException(ErrorCode.EXIST_ROOM_TITLE);
    }
  }
}
