package com.tang.game.common.exception;


import com.tang.core.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JamGameException extends RuntimeException {

  private ErrorCode errorCode;
  private String message;

  public JamGameException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.message = errorCode.getDescription();
  }
}
