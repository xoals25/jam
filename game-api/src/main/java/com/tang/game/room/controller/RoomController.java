package com.tang.game.room.controller;

import com.tang.game.room.dto.EnterRoomDto;
import com.tang.core.dto.LeaveRoomDto;
import com.tang.core.type.GameType;
import com.tang.core.type.TeamType;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.service.RoomService;
import com.tang.game.user.domain.User;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;

  @PostMapping()
  public ResponseEntity<Long> createRoom(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid RoomForm form) {
    return ResponseEntity.ok(roomService.createRoom(user, form));
  }

  @PostMapping("/{roomId}/enter")
  public ResponseEntity<EnterRoomDto> enterRoom(
      @AuthenticationPrincipal User user,
      @PathVariable Long roomId
  ) {
    return ResponseEntity.ok(roomService.enterRoom(roomId, user));
  }

  @PostMapping("/{roomId}/leave")
  public ResponseEntity<LeaveRoomDto> leaveRoom(
      @AuthenticationPrincipal User user,
      @PathVariable Long roomId
  ) {
    return ResponseEntity.ok(roomService.leaveRoom(roomId, user));
  }

  @GetMapping
  public ResponseEntity<Page<RoomDto>> searchRooms(
      @RequestParam @Nullable String keyword,
      @RequestParam @Nullable GameType gameType,
      @RequestParam @Nullable TeamType teamType,
      @PageableDefault(size = 5) Pageable pageable
  ) {
    return ResponseEntity.ok(roomService.searchRooms(keyword, gameType, teamType, pageable));
  }

  @GetMapping("/{roomId}")
  public ResponseEntity<?> searchRoom(
      @PathVariable Long roomId
  ) {
    return ResponseEntity.ok(roomService.searchRoom(roomId));
  }

  @PutMapping("/{roomId}")
  public ResponseEntity<Long> updateRoom(
      @AuthenticationPrincipal User user,
      @PathVariable Long roomId,
      @RequestBody @Valid RoomForm form
  ) {
    return ResponseEntity.ok(roomService.updateRoom(user, roomId, form));
  }

  @DeleteMapping("/{roomId}")
  public void deleteRoom(
      @AuthenticationPrincipal User user,
      @PathVariable Long roomId) {
    roomService.deleteRoom(user, roomId);
  }
}
