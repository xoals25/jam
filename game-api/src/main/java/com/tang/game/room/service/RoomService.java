package com.tang.game.room.service;

import static com.tang.game.common.type.RoomStatus.DELETE;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.CreateRoomForm;
import com.tang.game.room.repository.RoomRepository;
import com.tang.game.common.type.ErrorCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  public void createRoom(CreateRoomForm form) {
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

  public Object updateRoom() {
    return null;
  }

  public void deleteRoom(Long userId, Long roomId) {
    Room room = roomRepository.findByIdAndStatus(roomId, RoomStatus.VALID)
            .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_ROOM));

    validateDeleteRoom(room.getUserId(), userId);

    room.setStatus(DELETE);

    roomRepository.save(room);
  }

  private void validateDeleteRoom(Long roomHostId, Long userId) {
    if (!Objects.equals(roomHostId, userId)) {
      throw new JamGameException(ErrorCode.USER_ROOM_HOST_UN_MATCH);
    }
  }
}
