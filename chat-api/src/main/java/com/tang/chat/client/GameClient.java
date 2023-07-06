package com.tang.chat.client;

import com.tang.chat.common.dto.LeaveRoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "game-api", url = "${feign.client.url.game-api}")
public interface GameClient {

  @GetMapping("/participants/{participantsUserId}/rooms/{roomId}")
  ResponseEntity<Boolean> isRoomParticipant(
      @RequestHeader("Authorization") String token,
      @PathVariable Long roomId,
      @PathVariable Long participantsUserId
  );

  @PostMapping("/rooms/{roomId}/leave")
  ResponseEntity<LeaveRoomResponse> leaveRoom(
      @RequestHeader("Authorization") String token,
      @PathVariable Long roomId
  );
}
