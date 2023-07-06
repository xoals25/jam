package com.tang.game.room.controller;


import static com.tang.game.common.constant.ResponseConstant.SUCCESS;
import static com.tang.core.type.GameType.GAME_ORDER;
import static com.tang.core.type.TeamType.PERSONAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.core.type.GameType;
import com.tang.core.type.TeamType;
import com.tang.game.room.dto.RoomDto;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.service.RoomService;
import com.tang.game.token.service.TokenProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(RoomController.class)
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class})
class RoomControllerTest {

  @MockBean
  private RoomService roomService;

  @MockBean
  private TokenProvider tokenProvider;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  @DisplayName("성공 - 게임 방 생성")
  void
  successCreateRoom() throws Exception {
    //given
    given(roomService.createRoom(any(), any()))
        .willReturn(1L);

    //when
    //then
    String body = objectMapper.writeValueAsString(getRoomForm());

    mockMvc.perform(post("/rooms")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(1L))
        .andDo(
            document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        );
  }

  @Test
  @DisplayName("성공 - 게임 방 수정")
  @WithMockUser
  void successUpdateRoom() throws Exception {
    //given
    given(roomService.updateRoom(any(), anyLong(), any()))
        .willReturn(1L);

    //when
    //then
    String body = objectMapper.writeValueAsString(getRoomForm());

    mockMvc.perform(put("/rooms/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(1L))
        .andDo(
            document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        );
  }

  @Test
  @DisplayName("성공 게임 방 삭제")
  @WithMockUser
  void successDeleteRoom() throws Exception {
    //given

    //when
    roomService.deleteRoom(any(), anyLong());

    //then
    mockMvc.perform(delete("/rooms/1")
            .with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        );
  }

  @Test
  @DisplayName("성공 참가자 확인")
  @WithMockUser
  void successIsRoomParticipant() throws Exception {
    //given
    given(roomService.isRoomParticipant(anyLong(), anyLong()))
        .willReturn(true);

    //when
    //then
    mockMvc.perform(get("/rooms/{roomId}/participants/{participantUserId}", 1L, 1L)
            .with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(true))
        .andDo(
            document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        );

  }

  @Test
  @DisplayName("성공 게임 방 목록 조회")
  @WithMockUser
  void successSearchRooms() throws Exception {
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
          .currentNumberPeople(2)
          .password("123")
          .build());
    }

    given(roomService.searchRooms(
        anyString(),
        any(GameType.class),
        any(TeamType.class),
        any(Pageable.class)
    )).willReturn(new PageImpl<>(roomDtos, PageRequest.of(0, 5), 3));

    //when
    //then
    mockMvc.perform(get("/rooms")
            .param("keyword", "게임")
            .param("gameType", "GAME_ORDER")
            .param("teamType", "PERSONAL")
            .param("page", "0")
            .param("size", "3")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("content[0].id").value(1L))
        .andExpect(jsonPath("content[0].title").value("게임 방 제목 입니다."))
        .andExpect(jsonPath("content[0].hostUserId").value(1L))
        .andExpect(jsonPath("content[0].gameType").value("GAME_ORDER"))
        .andExpect(jsonPath("content[0].teamType").value("PERSONAL"))
        .andExpect(jsonPath("content[0].limitedNumberPeople").value(4))
        .andExpect(jsonPath("content[0].password").value("123"))
        .andDo(
            document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                relaxedResponseFields(
                    fieldWithPath("content[].id").type(JsonFieldType.NUMBER)
                        .description("게임 방 id"),
                    fieldWithPath("content[].title").type(JsonFieldType.STRING)
                        .description("게임 방 제목"),
                    fieldWithPath("content[].hostUserId").type(JsonFieldType.NUMBER)
                        .description("게임 방 호스트 고유 번호"),
                    fieldWithPath("content[].gameType").type(JsonFieldType.STRING)
                        .description("게임 타입"),
                    fieldWithPath("content[].teamType").type(JsonFieldType.STRING)
                        .description("게임 팀 타입"),
                    fieldWithPath("content[].limitedNumberPeople").type(JsonFieldType.NUMBER)
                        .description("게임 방 인원 제한"),
                    fieldWithPath("content[].currentNumberPeople").type(JsonFieldType.NUMBER)
                        .description("게임 방 참가자 수"),
                    fieldWithPath("content[].password").type(JsonFieldType.STRING)
                        .description("게임 방 암호"),
                    fieldWithPath("first").type(JsonFieldType.BOOLEAN)
                        .description("첫번째 페이지인지 여부"),
                    fieldWithPath("last").type(JsonFieldType.BOOLEAN)
                        .description("마지막 페이지인지 여부"),
                    fieldWithPath("totalElements").type(
                            JsonFieldType.NUMBER)
                        .description("검색 데이터 전체 개수"),
                    fieldWithPath("totalElements").type(
                            JsonFieldType.NUMBER)
                        .description("검색 데이터 전체 개수"),
                    fieldWithPath("totalPages").type(JsonFieldType.NUMBER)
                        .description("검색 데이터 전체 페이지 수"),
                    fieldWithPath("size").type(JsonFieldType.NUMBER)
                        .description("요청 데이터 수"),
                    fieldWithPath("numberOfElements").type(
                            JsonFieldType.NUMBER)
                        .description("현재 페이지에서 보여지는 데이터 수")
                )
            )
        );
  }

  @Test
  @DisplayName("성공 게임 상세 조회")
  @WithMockUser
  void successSearchRoom() throws Exception {
    //given
    given(roomService.searchRoom(anyLong()))
        .willReturn(RoomDto.builder()
            .id(1L)
            .title("게임 방 제목 입니다.")
            .hostUserId(1L)
            .gameType(GAME_ORDER)
            .teamType(PERSONAL)
            .limitedNumberPeople(4)
            .currentNumberPeople(2)
            .password("123")
            .build());

    //when
    //then
    mockMvc.perform(get("/rooms/{roomId}", 1L))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.title").value("게임 방 제목 입니다."))
        .andExpect(jsonPath("$.hostUserId").value(1L))
        .andExpect(jsonPath("$.gameType").value("GAME_ORDER"))
        .andExpect(jsonPath("$.teamType").value("PERSONAL"))
        .andExpect(jsonPath("$.limitedNumberPeople").value(4))
        .andExpect(jsonPath("$.password").value("123"))
        .andDo(
            document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                relaxedResponseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER)
                        .description("게임 방 id"),
                    fieldWithPath("title").type(JsonFieldType.STRING)
                        .description("게임 방 제목"),
                    fieldWithPath("hostUserId").type(JsonFieldType.NUMBER)
                        .description("게임 방 호스트 고유 번호"),
                    fieldWithPath("gameType").type(JsonFieldType.STRING)
                        .description("게임 타입"),
                    fieldWithPath("teamType").type(JsonFieldType.STRING)
                        .description("게임 팀 타입"),
                    fieldWithPath("limitedNumberPeople").type(JsonFieldType.NUMBER)
                        .description("게임 방 인원 제한"),
                    fieldWithPath("currentNumberPeople").type(JsonFieldType.NUMBER)
                        .description("게임 방 인원 제한"),
                    fieldWithPath("password").type(JsonFieldType.STRING)
                        .description("게임 방 암호")
                )
            )
        );
  }

  @Test
  @DisplayName("성공 게임 방 참가")
  @WithMockUser
  void successEnterRoom() throws Exception {
    //given
//    given(roomService.enterRoom(anyLong(), any()))
//        .willReturn(SUCCESS);
//
//    //when
//    ResultActions resultActions =
//        mockMvc.perform(post("/rooms/{roomId}/enter", 1L)
//                .contentType(MediaType.APPLICATION_JSON)
//                .with(csrf())
//                .accept(MediaType.APPLICATION_JSON))
//            .andDo(print());
//
//    //then
//    resultActions
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$").value("success"))
//        .andDo(document(
//            "{class-name}/{method-name}",
//            preprocessRequest(prettyPrint()),
//            preprocessResponse(prettyPrint())
//        ));
  }

  @Test
  @DisplayName("성공 게임 방 나가기")
  @WithMockUser
  void successLeaveRoom() throws Exception {
    //given
    given(roomService.leaveRoom(anyLong(), any()))
        .willReturn(SUCCESS);

    //when
    ResultActions resultActions =
        mockMvc.perform(post("/rooms/{roomId}/leave", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

    //then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("success"))
        .andDo(document(
            "{class-name}/{method-name}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())
        ));
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