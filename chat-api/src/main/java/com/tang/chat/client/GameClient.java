package com.tang.chat.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "game-api", url = "${feign.client.url.game-api}")
public interface GameClient {

  @GetMapping("/rooms/{roomId}/participants/{participantsUserId}")
  ResponseEntity<Boolean> isRoomParticipant(
      @PathVariable Long roomId,
      @PathVariable Long participantsUserId
  );
}
