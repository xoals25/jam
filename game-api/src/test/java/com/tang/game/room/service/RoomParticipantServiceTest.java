package com.tang.game.room.service;

import static com.tang.game.common.type.GameType.GAME_ORDER;
import static com.tang.game.common.type.RoomStatus.VALID;
import static com.tang.game.common.type.TeamType.PERSONAL;
import static com.tang.game.participant.type.ParticipantStatus.LEAVE;
import static com.tang.game.participant.type.ParticipantStatus.WAIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.participant.domain.Participant;
import com.tang.game.participant.repository.ParticipantRepository;
import com.tang.game.participant.type.ParticipantStatus;
import com.tang.game.room.domain.Room;
import com.tang.game.room.dto.RoomForm;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
public class RoomParticipantServiceTest {

  @Mock
  private RoomParticipantCacheService roomParticipantCacheService;

  @Mock
  private ParticipantRepository participantRepository;

  @InjectMocks
  private RoomParticipantService roomParticipantService;

  @Test
  @WithMockUser
  @DisplayName("성공 방 참가자 수 가져오기")
  void successGetRoomParticipantCount() {
    //given
    given(roomParticipantCacheService.getRoomCurrentParticipantCount(anyLong()))
        .willReturn(2);
    //when
    int currentParticipantCount = roomParticipantService.getRoomParticipantCount(1L);

    //then
    assertEquals(currentParticipantCount, 2);
  }

  @Test
  @WithMockUser
  @DisplayName("성공 참가자 방 입장 - 방 호스트 참여")
  void successEnterRoomParticipantHost() {
    //given
    ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);

    //when
    roomParticipantService.enterRoomParticipantHost(getRoom());

    verify(participantRepository, times(1))
        .save(captor.capture());

    verify(roomParticipantCacheService, times(1))
        .plusParticipantCountWithHost(anyLong());

    //then
    assertEquals(captor.getValue().getUserId(), getRoom().getHostUserId());
  }

  @Test
  @WithMockUser
  @DisplayName("성공 참가자 방 입장 - 참여한 전적이 있는 유저가 참여 - 기존 데이터 업데이트")
  void successEnterRoomParticipantUpdate() {
    //given
    given(participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(anyLong(), anyLong(), any()))
        .willReturn(false);

    given(roomParticipantCacheService.getRoomCurrentParticipantCount(anyLong()))
        .willReturn(1);

    Participant participant = getParticipant(LEAVE);

    given(participantRepository.findByRoomIdAndUserId(anyLong(), anyLong()))
        .willReturn(Optional.of(participant));

    //when
    roomParticipantService.enterRoomParticipant(getRoom(), 1L);

    //then
    assertEquals(participant.getStatus(), WAIT);
  }

  @Test
  @WithMockUser
  @DisplayName("성공 참가자 방 입장 - 참여한 전적이 없는 유저가 참여")
  void successEnterRoomParticipantSave() {
    //given
    given(participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(anyLong(), anyLong(), any()))
        .willReturn(false);

    given(roomParticipantCacheService.getRoomCurrentParticipantCount(anyLong()))
        .willReturn(1);

    given(participantRepository.findByRoomIdAndUserId(anyLong(), anyLong()))
        .willReturn(Optional.empty());

    ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);

    //when
    roomParticipantService.enterRoomParticipant(getRoom(), 2L);

    verify(participantRepository, times(1)).save(captor.capture());

    //then
    assertEquals(captor.getValue().getStatus(), WAIT);
    assertEquals(captor.getValue().getRoom().getId(), 1L);
    assertEquals(captor.getValue().getUserId(), 2L);
  }

  @Test
  @WithMockUser
  @DisplayName("실패 참가자 입장 - 이미 참여한 유저")
  void failEnterParticipant_ALREADY_ENTER() {
    //given
    given(participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(anyLong(), anyLong(), any()))
        .willReturn(true);
    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomParticipantService.enterRoomParticipant(getRoom(), 1L));

    //then
    assertEquals(ErrorCode.ALREADY_ROOM_ENTER_USER, exception.getErrorCode());
  }

  @Test
  @WithMockUser
  @DisplayName("실패 참가자 입장 - 인원수 제한")
  void failEnterParticipant_ROOM_PARTICIPANT_FULL() {
    //given
    given(roomParticipantCacheService.getRoomCurrentParticipantCount(anyLong()))
        .willReturn(8);
    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomParticipantService.enterRoomParticipant(getRoom(), 1L));

    //then
    assertEquals(ErrorCode.ROOM_PARTICIPANT_FULL, exception.getErrorCode());
  }

  @Test
  @WithMockUser
  @DisplayName("성공 참가자 방 나가기 - 전체 인원이 1명")
  void successLeaveRoomParticipant_OneParticipant() {
    //given
    given(participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(anyLong(), anyLong(), any()))
        .willReturn(true);

    given(roomParticipantCacheService.getRoomCurrentParticipantCount(anyLong()))
        .willReturn(1);

    //when
    int count = roomParticipantService.leaveRoomAndGetParticipantCount(1L, 2L);

    verify(roomParticipantCacheService,times(1))
        .deleteRoom(1L);

    //then
    assertEquals(count, 1);
  }

  @Test
  @WithMockUser
  @DisplayName("성공 참가자 방 나가기 - 전체 인원이 2명이상")
  void successLeaveRoomParticipant_ManyParticipant() {
    //given
    given(participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(anyLong(), anyLong(), any()))
        .willReturn(true);

    given(roomParticipantCacheService.getRoomCurrentParticipantCount(anyLong()))
        .willReturn(2);

    //when
    int count = roomParticipantService.leaveRoomAndGetParticipantCount(1L, 2L);

    //then
    assertEquals(count, 2);
  }

  @Test
  @WithMockUser
  @DisplayName("실패 참가자 입장 - 방 참가자 아님")
  void failLEANParticipant_NOT_PARTICIPANT() {
    //given
    given(participantRepository.existsByRoomIdAndUserIdAndStatusNotIn(anyLong(), anyLong(), any()))
        .willReturn(false);
    //when
    JamGameException exception = assertThrows(JamGameException.class,
        () -> roomParticipantService.leaveRoomAndGetParticipantCount(1L, 1L));

    //then
    assertEquals(ErrorCode.USER_NOT_ROOM_PARTICIPANT, exception.getErrorCode());
  }

  @Test
  @WithMockUser
  @DisplayName("성공 참가자 빼기")
  void successMinusParticipant() {
    //given
    //when
    roomParticipantService.minusParticipant(1L, 1L, 1);

    verify(participantRepository, times(1))
        .deleteByUserIdAndRoomId(1L, 1L, LEAVE);

    verify(roomParticipantCacheService,times(1))
        .minusParticipantCount(1L, 1);

    //then
  }

  private Participant getParticipant(ParticipantStatus status) {
    return Participant.builder()
        .id(1L)
        .room(getRoom())
        .status(status)
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
}
