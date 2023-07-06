package com.tang.game.participant.controller;

import com.tang.game.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/participants")
@RequiredArgsConstructor
public class ParticipantController {

  private final ParticipantService participantService;

  @GetMapping("/{participantUserId}/rooms/{roomId}")
  public ResponseEntity<Boolean> isRoomParticipant(
      @PathVariable Long participantUserId,
      @PathVariable Long roomId
  ) {
    return ResponseEntity.ok(participantService.isRoomParticipant(roomId, participantUserId));
  }
}
