package com.tang.game.room.controller;


import static com.tang.game.common.type.GameType.GAME_ORDER;
import static com.tang.game.common.type.TeamType.PERSONAL;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.game.room.dto.RoomForm;
import com.tang.game.room.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RoomController.class)
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class})
class RoomControllerTest {

  @MockBean
  private RoomService roomService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("성공 - 게임 방 생성")
  void
  successCreateRoom() throws Exception {
    //given

    RoomForm form = RoomForm.builder()
        .userId(1L)
        .title("게임방 제목")
        .password("0123")
        .limitedNumberPeople(8)
        .gameType(GAME_ORDER)
        .teamType(PERSONAL)
        .build();

    roomService.createRoom(form);
    //when
    //then
    mockMvc.perform(post("/rooms"))
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
  @DisplayName("성공 - 게임 방 수정")
  void
  successUpdateRoom() throws Exception {
    //given

    RoomForm form = RoomForm.builder()
        .userId(1L)
        .title("게임방 제목")
        .password("0123")
        .limitedNumberPeople(8)
        .gameType(GAME_ORDER)
        .teamType(PERSONAL)
        .build();

    roomService.updateRoom(1L, 1L, form);
    //when
    //then
    mockMvc.perform(post("/rooms/1"))
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
  @DisplayName("성공 게임 방 삭제")
  void
  successDeleteRoom() throws Exception {
    //given
    roomService.deleteRoom(anyLong(), anyLong());
    //when
    //then
    mockMvc.perform(delete("/rooms/1"))
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

}