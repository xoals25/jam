package com.tang.core.dto;

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
public class LeaveRoomDto {

  private long hostUserId;

  private long leaveParticipantId;

  public static LeaveRoomDto of(long hostUserId, long leaveParticipantId) {
    return LeaveRoomDto.builder()
        .hostUserId(hostUserId)
        .leaveParticipantId(leaveParticipantId)
        .build();
  }
}