package com.tang.core.dto;

import com.tang.core.type.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {

  private long id;

  private long userId;

  private String nickname;

  private ParticipantStatus status;

}
