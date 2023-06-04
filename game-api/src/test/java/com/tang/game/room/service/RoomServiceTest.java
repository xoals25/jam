package com.tang.game.room.service;

import static com.tang.game.common.constants.ResponseConstant.SUCCESS;
import static com.tang.game.common.type.GameType.GAME_ORDER;
import static com.tang.game.common.type.RoomStatus.DELETE;
import static com.tang.game.common.type.RoomStatus.VALID;
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
import com.tang.game.participant.dto.ParticipantUserIdMapping;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.room.domain.Room;
import com.tang.game.room.domain.RoomGameStatus;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.repository.RoomGameStatusRepository;
import com.tang.game.room.repository.RoomQuerydsl;
import com.tang.game.room.repository.RoomRepository;
import com.tang.game.room.type.GameStatus;
import com.tang.game.user.domain.User;
import com.tang.game.user.type.UserStatus;
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
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

  @Mock
  private RoomRepository roomRepository;

  @Mock
  private RoomGameStatusRepository roomGameStatusRepository;

  @Mock
  private RoomParticipantService roomParticipantService;

  @Mock
  private ParticipantRepository participantRepository;

  @InjectMocks
  private RoomService roomService;

  @Mock
  private RoomQuerydsl roomQuerydsl;


  @Test
  @DisplayName("성공 - 방 생성")
  void successCreateRoom() {
    // given
    given(roomRepository.existsByTitleAndStatus(anyString(), any()))
        .willReturn(false);

    given(roomRepository.save(any()))
        .willReturn(getRoom());

//    given(roomParticipantCountService.saveRoomParticipantWithRoomHost(any(), any()))
//        .willReturn(void.class);

    ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
    ArgumentCaptor<RoomGameStatus> roomGameStatusCaptor = ArgumentCaptor.forClass(
        RoomGameStatus.class);

    //when
    Long roomId = roomService.createRoom(getUser(), getRoomForm());

    //then
    verify(roomRepository, times(1)).save(roomCaptor.capture());
    assertEquals(roomCaptor.getValue().getHostUserId(), roomId);
    assertEquals(roomCaptor.getValue().getTitle(), "게임방 제목");
    assertEquals(roomCaptor.getValue().getPassword(), "0123");
    assertEquals(roomCaptor.getValue().getLimitedNumberPeople(), 8);
    assertEquals(roomCaptor.getValue().getTeamType(), PERSONAL);
    assertEquals(roomCaptor.getValue().getGameType(), GAME_ORDER);

    verify(roomGameStatusRepository, times(1)).save(roomGameStatusCaptor.capture());
    assertEquals(roomGameStatusCaptor.getValue().getRoom().getId(), 1L);
    assertEquals(roomGameStatusCaptor.getValue().getStatus(), GameStatus.WAIT);
  }

  @Test
  @DisplayName("실패 - 방 생성(중복되는 방제목)")
  void failCreateRoom_ExistRoomTitle() {
    // given
    given(roomRepository.existsByTitleAndStatus(anyString(), any()))
        .willReturn(true);

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.createRoom(getUser(), getRoomForm()));

    //then
    assertEquals(ErrorCode.EXIST_ROOM_TITLE, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공 - 방 수정")
  @WithMockUser
  void successUpdateRoom() {
    //given
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(getRoom()));

    given(roomRepository.existsByTitleAndStatusAndIdNot(anyString(), any(), anyLong()))
        .willReturn(false);

    given(roomParticipantService.getRoomParticipantCount(anyLong()))
        .willReturn(3);

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    RoomForm roomForm = getRoomForm();
    roomForm.setTitle("게임방 제목 변경");

    Long updateRoomId = roomService.updateRoom(getUser(), 1L, roomForm);

    //then
    verify(roomRepository, times(1)).save(captor.capture());
    assertEquals(captor.getValue().getHostUserId(), 1L);
    assertEquals(captor.getValue().getTitle(), "게임방 제목 변경");
    assertEquals(captor.getValue().getPassword(), "0123");
    assertEquals(captor.getValue().getLimitedNumberPeople(), 8);
    assertEquals(captor.getValue().getTeamType(), PERSONAL);
    assertEquals(captor.getValue().getGameType(), GAME_ORDER);
    assertEquals(captor.getValue().getId(), updateRoomId);
  }

  @Test
  @DisplayName("실패 방 수정 - 존재하지 않는 방")
  @WithMockUser
  void failUpdateRoom_NotFoundRoom() {
    //given
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.updateRoom(getUser(), 1L, getRoomForm()));

    //then
    assertEquals(ErrorCode.NOT_FOUND_ROOM, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 방 수정 - 게임방 주인이 아님")
  @WithMockUser
  void failUpdateRoom_HostUnMatch() {
    //given
    Room room = Room.builder()
        .id(2L)
        .status(VALID)
        .hostUserId(2L)
        .build();

    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(room));

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.updateRoom(getUser(), 1L, getRoomForm()));

    //then
    assertEquals(ErrorCode.USER_ROOM_HOST_UN_MATCH, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 방 수정 - 중복 되는 방제목")
  @WithMockUser
  void failUpdateRoom_ExistRoomTitle() {
    //given
    given(roomRepository.existsByTitleAndStatusAndIdNot(anyString(), any(), anyLong()))
        .willReturn(true);

    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(getRoom()));

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.updateRoom(getUser(), 1L, getRoomForm()));

    //then
    assertEquals(ErrorCode.EXIST_ROOM_TITLE, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 방 수정 - 참여 인원보다 적은 제한인원 수정")
  @WithMockUser
  void failUpdateRoom_LIMIT_PARTICIPANT_COUNT_ERROR() {
    //given
    given(roomRepository.existsByTitleAndStatusAndIdNot(anyString(), any(), anyLong()))
        .willReturn(false);

    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(getRoom()));

    given(roomParticipantService.getRoomParticipantCount(anyLong()))
        .willReturn(3);

    RoomForm roomForm = getRoomForm();
    roomForm.setLimitedNumberPeople(2);

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.updateRoom(getUser(), 1L, roomForm));

    //then
    assertEquals(ErrorCode.LIMIT_PARTICIPANT_COUNT_NOT_MIN_CURRENT_PARTICIPANT_COUNT,
        exception.getErrorCode());
  }

  @Test
  @DisplayName("성공 방 삭제")
  void successDeleteRoom() {
    //given
    Room room = getRoom();
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(room));

    ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

    //when
    roomService.deleteRoom(getUser(), 1L);

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
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.deleteRoom(getUser(), 1L));

    //then
    assertEquals(ErrorCode.NOT_FOUND_ROOM, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 방 삭제 - 호스트 불일치")
  void failDeleteRoom_RoomHostUnMatch() {
    //given
    Room room = Room.builder()
        .id(1L)
        .status(VALID)
        .hostUserId(2L)
        .build();

    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(room));

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.deleteRoom(getUser(), 1L));

    //then
    assertEquals(ErrorCode.USER_ROOM_HOST_UN_MATCH, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공 방 리스트 조회")
  @WithMockUser
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

    given(roomParticipantService.getRoomParticipantCount(anyLong()))
        .willReturn(3);

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
    assertEquals(response.getContent().get(0).getCurrentNumberPeople(), 3);
  }

  @Test
  @DisplayName("성공 방 상세 조회")
  @WithMockUser
  void successSearchRoom() {
    //given
    given(roomQuerydsl.findByIdAndStatus(anyLong()))
        .willReturn(Optional.ofNullable(getRoomDto()));

    given(roomParticipantService.getRoomParticipantCount(anyLong()))
        .willReturn(3);

    //when
    RoomDto roomDto = roomService.searchRoom(1L);

    //then
    assertEquals(roomDto.getId(), 1L);
    assertEquals(roomDto.getPassword(), "123");
    assertEquals(roomDto.getTitle(), "게임 방 제목 입니다.");
    assertEquals(roomDto.getGameType(), GAME_ORDER);
    assertEquals(roomDto.getTeamType(), PERSONAL);
    assertEquals(roomDto.getLimitedNumberPeople(), 4);
    assertEquals(roomDto.getCurrentNumberPeople(), 3);
  }

  @Test
  @DisplayName("성공 참가자 확인")
  void successIsRoomParticipant() {
    //given
    given(participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(anyLong(), any(), any()))
        .willReturn(true);

    //when
    boolean isParticipant = roomService.isRoomParticipant(1L, 1L);

    //then
    assertTrue(isParticipant);
  }

  @Test
  @WithMockUser
  @DisplayName("성공 방 입장")
  void successEnterRoom() {
    //given
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.of(getRoom()));

    //when
    String result = roomService.enterRoom(1L, getUser());

    //then
    assertEquals(result, SUCCESS);
  }

  @Test
  @WithMockUser
  @DisplayName("실패 방 입장 - 존재하지 않는 방")
  void failEnterRoom_NOT_FOUND_ROOM() {
    //given
    given(roomRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomService.enterRoom(1L, getUser()));

    //then
    assertEquals(ErrorCode.NOT_FOUND_ROOM, exception.getErrorCode());
  }

  @Test
  @WithMockUser
  @DisplayName("성공 방 나가기 - 인원 수 0명 - 방 파괴")
  void successLeaveRoomAndRoomDelete() {
    //given
    given(roomParticipantService.leaveRoomAndGetParticipantCount(anyLong(), anyLong()))
        .willReturn(1);

    //when
    String result = roomService.leaveRoom(1L, getUser());

    //then
    verify(roomRepository, times(1)).deleteById(anyLong());

    assertEquals(result, SUCCESS);
  }

  @Test
  @WithMockUser
  @DisplayName("성공 방 나가기 - 인원 수 2명 이상")
  void successLeaveRoom() {
    //given
    given(roomParticipantService.leaveRoomAndGetParticipantCount(anyLong(), anyLong()))
        .willReturn(3);

    given(roomRepository.findByIdAndHostUserId(anyLong(), anyLong()))
        .willReturn(Optional.empty());

    //when
    String result = roomService.leaveRoom(1L, getUser());

    //then
    assertEquals(result, SUCCESS);
  }

  @Test
  @WithMockUser
  @DisplayName("성공 방 방장 나가기 - 방장 변경 (인원수 2명 이상)")
  void successHostLeaveRoom() {
    //given
    given(roomParticipantService.leaveRoomAndGetParticipantCount(anyLong(), anyLong()))
        .willReturn(3);

    given(roomRepository.findByIdAndHostUserId(anyLong(), anyLong()))
        .willReturn(Optional.of(getRoom()));

    ParticipantUserIdMapping participantUserIdMapping = () -> 2L;

    given(participantRepository.findTopByRoomIdAndStatusNotInOrderByModifiedAtAsc(anyLong(), any()))
        .willReturn(Optional.of(participantUserIdMapping));

    ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);

    //when
    String result = roomService.leaveRoom(1L, getUser());

    verify(roomRepository, times(1)).save(roomCaptor.capture());

    //then
    assertEquals(roomCaptor.getValue().getHostUserId(), 2L);
    assertEquals(result, SUCCESS);
  }

  private User getUser() {
    return User.builder()
        .id(1L)
        .password("123")
        .email("xoals25@naver.com")
        .status(UserStatus.VALID)
        .build();
  }

  private Room getRoom() {
    Room room = Room.from(getRoomForm());
    room.setId(1L);
    room.setStatus(VALID);
    room.setHostUserId(1L);
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