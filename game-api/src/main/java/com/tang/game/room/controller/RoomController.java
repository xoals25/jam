package com.tang.game.room.controller;

import com.tang.game.room.dto.CreateRoomForm;
import com.tang.game.room.service.RoomService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;

  @PostMapping()
  public void createRoom(@RequestBody @Valid CreateRoomForm form) {
    roomService.createRoom(form);
  }

  @GetMapping
  public ResponseEntity<?> searchRooms() {
    return ResponseEntity.ok(roomService.searchRooms());
  }

  @GetMapping("/{roomId}")
  ResponseEntity<?> searchRoom(
      @PathVariable Long roomId
  ) {
    return ResponseEntity.ok(roomService.searchRoom());
  }

  @PutMapping("/{roomId}")
  ResponseEntity<?> updateRoom(
      @PathVariable Long roomId
  ) {
    return ResponseEntity.ok(roomService.updateRoom());
  }

  @DeleteMapping("/{roomId}")
  void deleteRoom(@PathVariable Long roomId) {
    // 유저 기능 추가 되면 삭제 예정
    Long userId = 1L;

    roomService.deleteRoom(userId, roomId);
  }
}
