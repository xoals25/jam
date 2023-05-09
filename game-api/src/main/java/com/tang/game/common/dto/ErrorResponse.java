package com.tang.game.common.dto;

import com.tang.game.common.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

  private ErrorCode errorCode;
  private String message;
}
