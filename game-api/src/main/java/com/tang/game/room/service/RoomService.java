package com.tang.game.room.service;

import static com.tang.game.common.type.RoomStatus.DELETE;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.common.type.TeamType;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.repository.RoomQuerydsl;
import com.tang.game.room.repository.RoomRepository;
import com.tang.game.common.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  private final RoomQuerydsl roomQuerydsl;

  public void createRoom(RoomForm form) {
    if (roomRepository.existsByTitleAndStatus(form.getTitle(), RoomStatus.VALID)) {
      throw new JamGameException(ErrorCode.EXIST_ROOM_TITLE);
    }

    roomRepository.save(Room.from(form));
  }

  public Page<RoomDto> searchRooms(
      String keyword,
      GameType gameType,
      TeamType teamType,
      Pageable pageable
  ) {
    return roomQuerydsl.findAllByTitleAndStatus(keyword, gameType, teamType, pageable);
  }

  public RoomDto searchRoom(Long roomId) {
    return roomQuerydsl.findByTitleAndStatus(roomId).orElseThrow(
        () -> new JamGameException(ErrorCode.NOT_FOUND_ROOM));
  }

  public void updateRoom(Long userId, Long roomId, RoomForm form) {
    Room room = roomRepository.findById(roomId).orElseThrow(
        () -> new JamGameException(ErrorCode.NOT_FOUND_ROOM)
    );

    validateUpdateRoom(room, form, userId);

    room.update(form);

    roomRepository.save(room);
  }

  public void deleteRoom(Long userId, Long roomId) {
    Room room = roomRepository.findByIdAndStatus(roomId, RoomStatus.VALID)
            .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_ROOM));

    if (!Objects.equals(room.getHostUserId(), userId)) {
      throw new JamGameException(ErrorCode.USER_ROOM_HOST_UN_MATCH);
    }

    room.setStatus(RoomStatus.DELETE);
    room.setDeletedAt(LocalDateTime.now());
    roomRepository.save(room);
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
