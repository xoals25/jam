package com.tang.chat.common.exception;


import static com.tang.chat.common.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.tang.chat.common.type.ErrorCode.INVALID_REQUEST;

import com.tang.chat.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JamChatException.class)
  public ErrorResponse handleAccountException(JamChatException e) {
    log.error("{} is occurred.", e.getErrorCode());

    return new ErrorResponse(e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e
  ) {
    log.error("MethodArgumentNotValidException is occurred.", e);

    return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ErrorResponse handleDataIntegrityViolationException(
      DataIntegrityViolationException e
  ) {
    log.error("DataIntegrityViolationException is occurred.", e);

    return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
  }

  @ExceptionHandler(Exception.class)
  public ErrorResponse handleException(Exception e) {
    log.error("Exception is occurred.", e);

    return new ErrorResponse(
        INTERNAL_SERVER_ERROR,
        INTERNAL_SERVER_ERROR.getDescription()
    );
  }
}
