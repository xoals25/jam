package com.tang.chat.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscribeType {
  CREATE("CREATE"), ENTER("ENTER");

  private final String description;
}
