package com.tang.game.room.service;

import com.tang.game.common.util.ObjectAndStringParsing;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.dto.RoomParticipantCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomParticipantCountService {

  private final RoomParticipantCountCacheService roomParticipantCountCacheService;

  private final ObjectAndStringParsing objectAndStringParsing;

  public RoomParticipantCount getRoomParticipantCount(Long roomId) {

    return objectAndStringParsing.stringConvertObject(
        roomParticipantCountCacheService
            .getRoomParticipantCountToString(roomId),
        RoomParticipantCount.class
    );
  }

  public void saveRoomParticipantWithRoomHost(Room room, RoomForm form) {
    roomParticipantCountCacheService.saveRoomParticipantWithRoomHost(room, form);
  }
}
