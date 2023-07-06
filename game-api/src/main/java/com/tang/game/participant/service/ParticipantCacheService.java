package com.tang.game.participant.service;

import static com.tang.game.room.constant.CacheKey.ROOM_PARTICIPANT_COUNT;

import com.tang.game.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantCacheService {

  private final ParticipantRepository participantRepository;

  private final ApplicationContext applicationContext;

  @Cacheable(value = ROOM_PARTICIPANT_COUNT, key = "#roomId")
  public int getRoomCurrentParticipantCount(Long roomId) {
    return participantRepository.countAllByRoomIdAndDeletedAtIsNull(roomId);
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
  public int minusParticipantCount(long roomId) {
    return getProxyRoomCurrentParticipantCount(roomId) - 1;
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

  private ParticipantCacheService getProxy() {
    return applicationContext.getBean(ParticipantCacheService.class);
  }
}
