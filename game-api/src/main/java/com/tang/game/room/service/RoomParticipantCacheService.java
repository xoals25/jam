package com.tang.game.room.service;

import static com.tang.game.room.constants.CacheKey.ROOM_PARTICIPANT_COUNT;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.common.util.ObjectAndStringParsing;
import com.tang.game.participant.domain.Participant;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.dto.RoomParticipantCount;
import com.tang.game.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomParticipantCacheService {

  private final ParticipantRepository participantRepository;

  private final RoomRepository roomRepository;

  private final ObjectAndStringParsing objectAndStringParsing;

  @Cacheable(value = ROOM_PARTICIPANT_COUNT, key = "#roomId")
  public String getRoomParticipantCountToString(Long roomId) {
    Room room = roomRepository.findById(roomId)
        .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_ROOM));

    return objectAndStringParsing.objectConvertString(
        RoomParticipantCount.of(
            room.getLimitedNumberPeople(),
            participantRepository.countAllByRoomId(roomId)
        )
    );
  }

  @CachePut(value = ROOM_PARTICIPANT_COUNT, key = "#room.id")
  public String saveRoomParticipantWithRoomHost(Room room, RoomForm form) {
    participantRepository.save(Participant.of(room, form, 1));

    return objectAndStringParsing.objectConvertString(
        RoomParticipantCount.of(room.getLimitedNumberPeople(), 1)
    );
  }

  @CachePut(value = ROOM_PARTICIPANT_COUNT, key = "#room.id")
  public RoomParticipantCount saveRoomParticipant(Room room, RoomForm form, int gameOrder) {
    participantRepository.save(Participant.of(room, form, gameOrder));

    RoomParticipantCount roomParticipantCount = objectAndStringParsing.stringConvertObject(
        getRoomParticipantCountToString(room.getId()),
        RoomParticipantCount.class
    );

    return RoomParticipantCount.of(
        room.getLimitedNumberPeople(),
        roomParticipantCount.getCurrentNumberPeople() + 1
    );
  }
}
