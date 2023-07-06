package com.tang.game.room.service;

import com.tang.core.dto.LeaveRoomDto;
import com.tang.core.type.GameType;
import com.tang.core.type.TeamType;
import com.tang.game.common.exception.JamGameException;
import com.tang.core.type.ErrorCode;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.participant.service.ParticipantService;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.EnterRoomDto;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.repository.RoomQuerydsl;
import com.tang.game.room.repository.RoomRepository;
import com.tang.game.user.domain.User;
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

  private final ParticipantService participantService;

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

    participantService.enterRoomParticipantHost(room);

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
  public EnterRoomDto enterRoom(Long roomId, User user) {
    Room room = getRoomFindByIdAndStatus(roomId, RoomStatus.VALID);

    long participantId = participantService.enterRoomParticipant(room, user.getId());

    return EnterRoomDto.of(room, participantId, participantService.getParticipants(roomId));
  }

  @Transactional
  public LeaveRoomDto leaveRoom(long roomId, User user) {
    int remainParticipantCount = participantService
        .leaveRoomAndGetParticipantCount(roomId, user.getId());

    if (remainParticipantCount == 0) {
      roomRepository.deleteById(roomId);
      return LeaveRoomDto.of(user.getId(), user.getId());
    }

    Room room = getRoomFindByIdAndStatus(roomId, RoomStatus.VALID);

    long hostUserId = room.getHostUserId();

    if (hostUserId == user.getId()) {
      Long enterSecondUserId = participantService.getEnterSecondUserId(roomId);

      room.setHostUserId(enterSecondUserId);

      hostUserId = enterSecondUserId;
    }

    return LeaveRoomDto.of(hostUserId, user.getId());
  }

  @Transactional
  public void deleteRoom(User user, Long roomId) {
    Room room = getRoomFindByIdAndStatus(roomId, RoomStatus.VALID);

    if (!Objects.equals(room.getHostUserId(), user.getId())) {
      throw new JamGameException(ErrorCode.USER_ROOM_HOST_UN_MATCH);
    }

    roomRepository.delete(room);
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
    return participantService.getRoomParticipantCount(roomId);
  }
}
