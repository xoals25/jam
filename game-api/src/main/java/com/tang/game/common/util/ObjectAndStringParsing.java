package com.tang.game.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjectAndStringParsing {

  private final ObjectMapper objectMapper;

  public <T> String objectConvertString(T data) {
    String s;

    try {
      s = objectMapper.writeValueAsString(
          data
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return s;
  }

  public <T> T stringConvertObject(String s, Class<T> classType) {
    try {
      return objectMapper.readValue(s, classType);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
