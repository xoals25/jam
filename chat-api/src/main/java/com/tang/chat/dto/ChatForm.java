package com.tang.chat.dto;

import com.tang.chat.type.MessageType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatForm {

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {
    @NotNull
    private MessageType type;
    @NotNull
    private Long senderId;
    @NotNull
    private Long roomId;
    @NotBlank
    private String message;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {
    @NotNull
    private MessageType type;
    @NotNull
    private Long senderId;
    @NotNull
    private Long roomId;
    @NotBlank
    private String message;
  }

}
