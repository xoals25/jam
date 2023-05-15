package com.tang.game.room.service;

import static com.tang.game.common.type.GameType.GAME_ORDER;
import static com.tang.game.common.type.RoomStatus.DELETE;
import static com.tang.game.common.type.TeamType.PERSONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.TeamType;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.repository.RoomQuerydsl;
import com.tang.game.room.repository.RoomRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

  @Mock
  private RoomRepository roomRepository;

  @Mock
  private ParticipantRepository participantRepository;

  @InjectMocks
  private RoomService roomService;

  @Mock
  private RoomQuerydsl roomQuerydsl;


  @Test
  @DisplayName("성공 - 방 생성")
  void successCreateRoom() {
    //given
    given(roomRepository.existsByTitleAndStatus(anyString(), any()))
        .willReturn(false);

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    roomService.createRoom(getRoomForm());

    //then
    verify(roomRepository, times(1)).save(captor.capture());
    assertEquals(captor.getValue().getHostUserId(), 1L);
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

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.createRoom(getRoomForm()));

    //then
    assertEquals(ErrorCode.EXIST_ROOM_TITLE, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공 - 방 수정")
  void successUpdateRoom() {
    //given
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(getRoom()));

    given(roomRepository.existsByTitleAndStatusAndIdNot(anyString(), any(), anyLong()))
        .willReturn(false);

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    RoomForm roomForm = getRoomForm();
    roomForm.setTitle("게임방 제목 변경");

    roomService.updateRoom(1L, 1L, roomForm);

    //then
    verify(roomRepository, times(1)).save(captor.capture());
    assertEquals(captor.getValue().getHostUserId(), 1L);
    assertEquals(captor.getValue().getTitle(), "게임방 제목 변경");
    assertEquals(captor.getValue().getPassword(), "0123");
    assertEquals(captor.getValue().getLimitedNumberPeople(), 8);
    assertEquals(captor.getValue().getTeamType(), PERSONAL);
    assertEquals(captor.getValue().getGameType(), GAME_ORDER);
  }

  @Test
  @DisplayName("실패 방 수정 - 존재하지 않는 방")
  void failUpdateRoom_NotFoundRoom() {
    //given
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.updateRoom(1L, 1L, getRoomForm()));

    //then
    assertEquals(ErrorCode.NOT_FOUND_ROOM, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 방 수정 - 게임방 주인이 아님")
  void failUpdateRoom_HostUnMatch() {
    //given
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(getRoom()));

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.updateRoom(2L, 1L, getRoomForm()));

    //then
    assertEquals(ErrorCode.USER_ROOM_HOST_UN_MATCH, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 방 수정 - 중복 되는 방제목")
  void failUpdateRoom_ExistRoomTitle() {
    //given
    given(roomRepository.existsByTitleAndStatusAndIdNot(anyString(), any(), anyLong()))
        .willReturn(true);

    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(getRoom()));

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.updateRoom(1L, 1L, getRoomForm()));

    //then
    assertEquals(ErrorCode.EXIST_ROOM_TITLE, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공 방 삭제")
  void successDeleteRoom() {
    //given
    Room room = getRoom();
    given(roomRepository.findByIdAndStatus(anyLong(), any()))
        .willReturn(Optional.of(room));

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    roomService.deleteRoom(1L, 1L);

    //then
    verify(roomRepository, times(1)).delete(captor.capture());

    captor.getValue().setStatus(DELETE);
    captor.getValue().setDeletedAt(LocalDateTime.now());

    assertEquals(captor.getValue().getStatus(), DELETE);
    assertNotNull(captor.getValue().getDeletedAt());
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
        .willReturn(Optional.of(getRoom()));

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.deleteRoom(2L, 1L));

    //then
    assertEquals(ErrorCode.USER_ROOM_HOST_UN_MATCH, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공 방 리스트 조회")
  void successSearchRooms() {
    //given
    List<RoomDto> roomDtos = new ArrayList<>();

    for (long i = 1; i <= 3; i++) {
      roomDtos.add(RoomDto.builder()
          .id(i)
          .title("게임 방 제목 입니다.")
          .hostUserId(i)
          .gameType(GAME_ORDER)
          .teamType(PERSONAL)
          .limitedNumberPeople(4)
          .password("123")
          .build());
    }

    given(roomQuerydsl.findAllByTitleAndStatus(
        anyString(),
        any(GameType.class),
        any(TeamType.class),
        any(Pageable.class)
    )).willReturn(new PageImpl<>(roomDtos, PageRequest.of(0, 5), 3));

    //when
    Page<RoomDto> response = roomService.searchRooms(
        "게임",
        GAME_ORDER,
        PERSONAL,
        PageRequest.of(0, 3)
    );

    //then
    assertEquals(response.getContent().size(), 3);
    assertEquals(response.getContent().get(0).getId(), 1L);
    assertEquals(response.getContent().get(0).getPassword(), "123");
    assertEquals(response.getContent().get(0).getTitle(), "게임 방 제목 입니다.");
    assertEquals(response.getContent().get(0).getGameType(), GAME_ORDER);
    assertEquals(response.getContent().get(0).getTeamType(), PERSONAL);
    assertEquals(response.getContent().get(0).getLimitedNumberPeople(), 4);
  }

  @Test
  @DisplayName("성공 방 상세 조회")
  void successSearchRoom() {
    //given
    given(roomQuerydsl.findByTitleAndStatus(anyLong()))
        .willReturn(Optional.ofNullable(getRoomDto()));

    //when
    RoomDto roomDto = roomService.searchRoom(1L);

    //then
    assertEquals(roomDto.getId(), 1L);
    assertEquals(roomDto.getPassword(), "123");
    assertEquals(roomDto.getTitle(), "게임 방 제목 입니다.");
    assertEquals(roomDto.getGameType(), GAME_ORDER);
    assertEquals(roomDto.getTeamType(), PERSONAL);
    assertEquals(roomDto.getLimitedNumberPeople(), 4);
  }

  @Test
  @DisplayName("성공 참가자 확인")
  void successIsRoomParticipant() {
    //given
    given(participantRepository.existsByRoomIdAndUserId(anyLong(), any()))
        .willReturn(true);

    //when
    boolean isParticipant = roomService.isRoomParticipant(1L, 1L);

    //then
    assertTrue(isParticipant);
  }

  private Room getRoom() {
    Room room = Room.from(getRoomForm());
    room.setId(1L);
    return room;
  }

  private RoomForm getRoomForm() {
    return RoomForm.builder()
        .userId(1L)
        .title("게임방 제목")
        .password("0123")
        .limitedNumberPeople(8)
        .gameType(GAME_ORDER)
        .teamType(PERSONAL)
        .build();
  }

  private RoomDto getRoomDto() {
    return RoomDto.builder()
        .id(1L)
        .title("게임 방 제목 입니다.")
        .hostUserId(1L)
        .gameType(GAME_ORDER)
        .teamType(PERSONAL)
        .limitedNumberPeople(4)
        .password("123")
        .build();
  }
}