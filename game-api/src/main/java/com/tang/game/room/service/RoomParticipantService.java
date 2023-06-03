package com.tang.game.room.service;

import static com.tang.game.participant.type.ParticipantStatus.LEAVE;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.participant.domain.Participant;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.participant.type.ParticipantStatus;
import com.tang.game.room.domain.Room;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomParticipantService {

  private final RoomParticipantCacheService roomParticipantCacheService;

  private final ParticipantRepository participantRepository;

  public int getRoomParticipantCount(Long roomId) {
    return roomParticipantCacheService.getRoomCurrentParticipantCount(roomId);
  }

  public void enterRoomParticipantHost(Room room) {
    participantRepository.save(Participant.from(room));

    roomParticipantCacheService.plusParticipantCountWithHost(room.getId());
  }

  public void enterRoomParticipant(Room room, Long userId) {
    if (participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(room.getId(), userId,
        Collections.singletonList(LEAVE))) {
      throw new JamGameException(ErrorCode.ALREADY_ROOM_ENTER_USER);
    }

    int currentParticipantCount = roomParticipantCacheService
        .getRoomCurrentParticipantCount(room.getId());

    if (room.getLimitedNumberPeople() <= currentParticipantCount) {
      throw new JamGameException(ErrorCode.ROOM_PARTICIPANT_FULL);
    }

    participantRepository.findByRoomIdAndUserId(room.getId(), userId)
        .ifPresentOrElse(
            it -> it.setStatus(ParticipantStatus.WAIT),
            () -> participantRepository.save(Participant.of(room, userId))
        );

    roomParticipantCacheService.plusParticipantCount(room.getId(), currentParticipantCount);
  }

  public int leaveRoomAndGetParticipantCount(Long roomId, Long userId) {
    if (!participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(roomId, userId,
        Collections.singletonList(LEAVE))) {
      throw new JamGameException(ErrorCode.USER_NOT_ROOM_PARTICIPANT);
    }

    int currentParticipantCount = roomParticipantCacheService
        .getRoomCurrentParticipantCount(roomId);

    if (currentParticipantCount == 1) {
      roomParticipantCacheService.deleteRoom(roomId);
    }

    return currentParticipantCount;
  }

  public void minusParticipant(Long roomId, Long userId, int currentParticipantCount) {
    participantRepository.deleteByUserIdAndRoomId(userId, roomId, LEAVE);

    roomParticipantCacheService.minusParticipantCount(roomId, currentParticipantCount);
  }
}
