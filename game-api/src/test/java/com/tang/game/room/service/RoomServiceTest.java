package com.tang.game.room.service;

import static com.tang.game.common.type.GameType.GAME_ORDER;
import static com.tang.game.common.type.RoomStatus.DELETE;
import static com.tang.game.common.type.TeamType.PERSONAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.CreateRoomForm;
import com.tang.game.room.repository.RoomRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

  @Mock
  private RoomRepository roomRepository;

  @InjectMocks
  private RoomService roomService;

  @Test
  @DisplayName("성공 - 방 생성")
  void successCreateRoom() {
    //given
    given(roomRepository.existsByTitleAndStatus(anyString(), any()))
        .willReturn(false);

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    roomService.createRoom(getCreateRoomForm());

    //then
    verify(roomRepository, times(1)).save(captor.capture());
    assertEquals(captor.getValue().getUserId(), 1L);
    assertEquals(captor.getValue().getTitle(), "게임방 제목");
    assertEquals(captor.getValue().getPassword(), "0123");
    assertEquals(captor.getValue().getLimitedNumberPeople(), 8);
    assertEquals(captor.getValue().getTeamType(), PERSONAL);
    assertEquals(captor.getValue().getGameType(), GAME_ORDER);
  }

  @Test
  @DisplayName("실패 - 방 생성(중복되는 방제목)")
  void failCreateRoom_ExistRoomTitle() {
    //given
    given(roomRepository.existsByTitleAndStatus(anyString(), any()))
        .willReturn(true);

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.createRoom(getCreateRoomForm()));

    //then
    assertEquals(ErrorCode.EXIST_ROOM_TITLE, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공 방 삭제")
  void successDeleteRoom() {
    //given
    given(roomRepository.findByIdAndStatus(anyLong(), any()))
        .willReturn(Optional.ofNullable(getRoom()));

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    roomService.deleteRoom(1L, 1L);

    //then
    verify(roomRepository, times(1)).save(captor.capture());
    assertEquals(captor.getValue().getStatus(), DELETE);
  }

  @Test
  @DisplayName("실패 방 삭제 - 존재하지 않는 방")
  void failDeleteRoom_NotFoundRoom() {
    //given
    given(roomRepository.findByIdAndStatus(anyLong(), any()))
        .willReturn(Optional.empty());

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.deleteRoom(1L, 1L));

    //then
    assertEquals(ErrorCode.NOT_FOUND_ROOM, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 방 삭제 - 호스트 불일치")
  void failDeleteRoom_RoomHostUnMatch() {
    //given
    given(roomRepository.findByIdAndStatus(anyLong(), any()))
        .willReturn(Optional.ofNullable(getRoom()));

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.deleteRoom(2L, 1L));

    //then
    assertEquals(ErrorCode.USER_ROOM_HOST_UN_MATCH, exception.getErrorCode());
  }

  private Room getRoom() {
    return Room.from(getCreateRoomForm());
  }

  private CreateRoomForm getCreateRoomForm() {
    return CreateRoomForm.builder()
        .userId(1L)
        .title("게임방 제목")
        .password("0123")
        .limitedNumberPeople(8)
        .gameType(GAME_ORDER)
        .teamType(PERSONAL)
        .build();
  }

}