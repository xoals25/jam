package com.tang.chat.common.exception;


import com.tang.core.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JamChatException extends RuntimeException {

  private ErrorCode errorCode;
  private String message;

  public JamChatException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.message = errorCode.getDescription();
  }

  @Override
  public String toString() {
    return String.format("{\"errorCode\":\"%s\", \"message\":\"%s\"}", this.errorCode, this.message);
  }
}
