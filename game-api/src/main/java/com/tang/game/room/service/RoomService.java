package com.tang.game.room.service;

import static com.tang.game.common.constants.ResponseConstant.SUCCESS;
import static com.tang.game.participant.type.ParticipantStatus.LEAVE;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.common.type.TeamType;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.room.domain.Room;
import com.tang.game.room.domain.RoomGameStatus;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.repository.RoomGameStatusRepository;
import com.tang.game.room.repository.RoomQuerydsl;
import com.tang.game.room.repository.RoomRepository;
import com.tang.game.user.domain.User;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  private final RoomParticipantService roomParticipantService;

  private final ParticipantRepository participantRepository;

  private final RoomGameStatusRepository roomGameStatusRepository;

  private final RoomQuerydsl roomQuerydsl;

  @Transactional
  public Long createRoom(User user, RoomForm form) {
    if (!Objects.equals(user.getId(), form.getUserId())) {
      throw new JamGameException(ErrorCode.UN_MATCH_CREATE_ROOM_USER_ID_AND_LOGIN_USER_ID);
    }

    if (roomRepository.existsByTitleAndStatus(form.getTitle(), RoomStatus.VALID)) {
      throw new JamGameException(ErrorCode.EXIST_ROOM_TITLE);
    }

    Room room = roomRepository.save(Room.from(form));

    roomGameStatusRepository.save(RoomGameStatus.of(room, 1));

    roomParticipantService.enterRoomParticipantHost(room);

    return room.getId();
  }

  public Page<RoomDto> searchRooms(
      String keyword,
      GameType gameType,
      TeamType teamType,
      Pageable pageable
  ) {

    return roomQuerydsl.findAllByTitleAndStatus(keyword, gameType, teamType, pageable)
        .map(it -> {
          it.setCurrentNumberPeople(getRoomCurrentParticipantCount(it.getId()));

          return it;
        });
  }

  public RoomDto searchRoom(Long roomId) {
    RoomDto roomDto = roomQuerydsl.findByIdAndStatus(roomId)
        .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_ROOM));

    roomDto.setCurrentNumberPeople(getRoomCurrentParticipantCount(roomId));

    return roomDto;
  }

  public Long updateRoom(User user, Long roomId, RoomForm form) {
    Room room = getRoomFindByIdAndStatus(roomId, RoomStatus.VALID);

    validateUpdateRoom(room, form, user.getId());

    room.update(form);

    roomRepository.save(room);

    return room.getId();
  }

  @Transactional
  public String enterRoom(Long roomId, User user) {
    Room room = getRoomFindByIdAndStatus(roomId, RoomStatus.VALID);

    roomParticipantService.enterRoomParticipant(room, user.getId());

    return SUCCESS;
  }

  @Transactional
  public String leaveRoom(Long roomId, User user) {
    int countParticipant = roomParticipantService
        .leaveRoomAndGetParticipantCount(roomId, user.getId());

    if (countParticipant == 1) {
      roomRepository.deleteById(roomId);
      return SUCCESS;
    }

    Optional<Room> optionalRoom = roomRepository.findByIdAndHostUserId(roomId, user.getId());

    if (countParticipant >= 2 && optionalRoom.isPresent()) {
      Long enterSecondUserId = participantRepository
          .findTopByRoomIdAndStatusNotInOrderByModifiedAtAsc(roomId,
              Collections.singletonList(LEAVE))
          .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_PARTICIPANT))
          .getUserId();

      optionalRoom.get().setHostUserId(enterSecondUserId);
      roomRepository.save(optionalRoom.get());
    }

    roomParticipantService.minusParticipant(roomId, user.getId(), countParticipant);

    return SUCCESS;
  }

  @Transactional
  public void deleteRoom(Long userId, Long roomId) {
    Room room = getRoomFindByIdAndStatus(roomId, RoomStatus.VALID);

    if (!Objects.equals(room.getHostUserId(), userId)) {
      throw new JamGameException(ErrorCode.USER_ROOM_HOST_UN_MATCH);
    }

    roomRepository.delete(room);
  }

  public boolean isRoomParticipant(Long roomId, Long userId) {
    return participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(roomId, userId,
        Collections.singletonList(LEAVE));
  }

  private Room getRoomFindByIdAndStatus(Long roomId, RoomStatus status) {
    Room room = roomRepository.findById(roomId)
        .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_ROOM));

    if (room.getStatus() != status) {
      throw new JamGameException(ErrorCode.NOT_FOUND_ROOM);
    }

    return room;
  }

  private void validateUpdateRoom(Room room, RoomForm form, Long userId) {
    if (!Objects.equals(room.getHostUserId(), userId)) {
      throw new JamGameException(ErrorCode.USER_ROOM_HOST_UN_MATCH);
    }

    if (roomRepository.existsByTitleAndStatusAndIdNot(form.getTitle(), RoomStatus.VALID,
        room.getId())) {
      throw new JamGameException(ErrorCode.EXIST_ROOM_TITLE);
    }

    if (form.getLimitedNumberPeople() < getRoomCurrentParticipantCount(room.getId())) {
      throw new JamGameException(
          ErrorCode.LIMIT_PARTICIPANT_COUNT_NOT_MIN_CURRENT_PARTICIPANT_COUNT);
    }
  }

  private int getRoomCurrentParticipantCount(Long roomId) {
    return roomParticipantService.getRoomParticipantCount(roomId);
  }
}
