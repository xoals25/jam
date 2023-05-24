package com.tang.game.room.service;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.common.type.TeamType;
import com.tang.game.participant.domain.Participant;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.room.domain.Room;
import com.tang.game.room.domain.RoomGameStatus;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.repository.RoomGameStatusRepository;
import com.tang.game.room.repository.RoomQuerydsl;
import com.tang.game.room.repository.RoomRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  private final ParticipantRepository participantRepository;

  private final RoomGameStatusRepository roomGameStatusRepository;

  private final RoomQuerydsl roomQuerydsl;

  @Transactional
  public void createRoom(RoomForm form) {
    if (roomRepository.existsByTitleAndStatus(form.getTitle(), RoomStatus.VALID)) {
      throw new JamGameException(ErrorCode.EXIST_ROOM_TITLE);
    }

    Room room = roomRepository.save(Room.from(form));

    roomGameStatusRepository.save(RoomGameStatus.from(room));

    participantRepository.save(Participant.from(room));
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
    return roomQuerydsl.findByIdAndStatus(roomId).orElseThrow(
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

    roomRepository.delete(room);
  }

  public boolean isRoomParticipant(Long roomId, Long userId) {
    return participantRepository.existsByRoomIdAndUserId(roomId, userId);
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
