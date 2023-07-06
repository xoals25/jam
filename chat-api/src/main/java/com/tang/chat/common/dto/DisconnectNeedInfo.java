package com.tang.chat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisconnectNeedInfo {

  String token;

  long roomId;

  long userId;

  String nickname;
}
