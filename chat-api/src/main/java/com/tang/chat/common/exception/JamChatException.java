package com.tang.chat.common.exception;


import com.tang.chat.common.type.ErrorCode;
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
}
