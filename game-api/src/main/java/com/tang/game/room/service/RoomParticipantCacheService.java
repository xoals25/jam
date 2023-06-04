package com.tang.game.room.service;

import static com.tang.game.room.constants.CacheKey.ROOM_PARTICIPANT_COUNT;

import com.tang.game.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomParticipantCacheService {

  private final ParticipantRepository participantRepository;

  private final ApplicationContext applicationContext;

  @Cacheable(value = ROOM_PARTICIPANT_COUNT, key = "#roomId")
  public int getRoomCurrentParticipantCount(Long roomId) {
    return participantRepository.countAllByRoomId(roomId);
  }

  @CachePut(value = ROOM_PARTICIPANT_COUNT, key = "#roomId")
  public int plusParticipantCountWithHost(Long roomId) {
    return 1;
  }

  @CachePut(value = ROOM_PARTICIPANT_COUNT, key = "#roomId")
  public int plusParticipantCount(Long roomId, int currentParticipantCount) {
    return currentParticipantCount + 1;
  }

  @CachePut(value = ROOM_PARTICIPANT_COUNT, key = "#roomId")
  public int minusParticipantCount(long roomId, int currentParticipantCount) {
    return currentParticipantCount - 1;
  }

  @CacheEvict(value = ROOM_PARTICIPANT_COUNT, key = "#roomId")
  public void deleteRoom(Long roomId) {
  }

  private int getProxyRoomCurrentParticipantCount(Long roomId) {
    return getProxy().getRoomCurrentParticipantCount(roomId);
  }

  private RoomParticipantCacheService getProxy() {
    return applicationContext.getBean(RoomParticipantCacheService.class);
  }
}
