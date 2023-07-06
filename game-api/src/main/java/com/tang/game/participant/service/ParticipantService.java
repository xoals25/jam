package com.tang.game.participant.service;

import static com.tang.core.type.ParticipantStatus.LEAVE;

import com.tang.game.participant.dto.ParticipantDto;
import com.tang.game.common.exception.JamGameException;
import com.tang.core.type.ErrorCode;
import com.tang.game.participant.domain.Participant;
import com.tang.game.participant.repository.ParticipantQuerydsl;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.room.domain.Room;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {

  private final ParticipantCacheService participantCacheService;

  private final ParticipantRepository participantRepository;

  private final ParticipantQuerydsl participantQuerydsl;

  public int getRoomParticipantCount(Long roomId) {
    return participantCacheService.getRoomCurrentParticipantCount(roomId);
  }

  public int enterRoomParticipantHost(Room room) {
    participantRepository.save(Participant.from(room));

    return participantCacheService.plusParticipantCountWithHost(room.getId());
  }

  public long enterRoomParticipant(Room room, Long userId) {
    if (participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(room.getId(), userId,
        Collections.singletonList(LEAVE))) {
      throw new JamGameException(ErrorCode.ALREADY_ROOM_ENTER_USER);
    }

    int currentParticipantCount = participantCacheService
        .getRoomCurrentParticipantCount(room.getId());

    if (room.getLimitedNumberPeople() <= currentParticipantCount) {
      throw new JamGameException(ErrorCode.ROOM_PARTICIPANT_FULL);
    }

    Participant participant = participantRepository.save(Participant.of(room, userId));

    participantCacheService.plusParticipantCount(room.getId(), currentParticipantCount);

    return participant.getId();
  }

  public List<ParticipantDto> getParticipants(Long RoomId) {
    return participantQuerydsl.findAllByRoomIdAndStatusNot(RoomId, LEAVE);
  }

  public int leaveRoomAndGetParticipantCount(Long roomId, Long userId) {
    if (!participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(roomId, userId,
        Collections.singletonList(LEAVE))) {
      throw new JamGameException(ErrorCode.USER_NOT_ROOM_PARTICIPANT);
    }

    int currentParticipantCount = participantCacheService.minusParticipantCount(roomId);

    if (currentParticipantCount == 0) {
      participantCacheService.deleteRoom(roomId);
    }

    participantRepository.deleteByUserIdAndRoomIdInQuery(userId, roomId, LEAVE);

    return currentParticipantCount;
  }

  public Long getEnterSecondUserId(Long roomId) {
    return participantRepository
        .findTopByRoomIdAndStatusNotInOrderByModifiedAtAsc(roomId,
            Collections.singletonList(LEAVE))
        .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_PARTICIPANT))
        .getUserId();
  }

//  public void minusParticipant(Long roomId, Long userId, int currentParticipantCount) {
//    participantRepository.deleteByUserIdAndRoomIdInQuery(userId, roomId, LEAVE);
//
//    roomParticipantCacheService.minusParticipantCount(roomId, currentParticipantCount);
//  }

  public boolean isRoomParticipant(Long roomId, Long userId) {
    return participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(roomId, userId,
        Collections.singletonList(LEAVE));
  }
}
