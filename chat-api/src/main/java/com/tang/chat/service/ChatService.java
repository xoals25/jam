package com.tang.chat.service;

import static com.tang.chat.common.constant.ChatConstants.DESTINATION_PREFIX;
import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;

import com.tang.chat.client.GameClient;
import com.tang.chat.common.dto.LeaveRoomResponse;
import com.tang.chat.common.dto.DisconnectNeedInfo;
import com.tang.chat.common.exception.JamChatException;
import com.tang.chat.dto.ChatDto;
import com.tang.chat.dto.ChatEnterDto;
import com.tang.chat.dto.ChatLeaveDto;
import com.tang.chat.token.service.TokenProvider;
import com.tang.chat.type.MessageType;
import com.tang.core.constants.TokenConstants;
import com.tang.core.dto.ParticipantResponse;
import com.tang.core.type.ErrorCode;
import com.tang.core.type.ParticipantStatus;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final SimpMessagingTemplate simpMessagingTemplate;

  private final GameClient gameClient;

  private final TokenProvider tokenProvider;

  public void sendMessage(StompHeaderAccessor accessor, ChatDto chatDto) {
    if (Boolean.FALSE.equals(gameClient.isRoomParticipant(
            getTokenAddPrefix(accessor),
            chatDto.getRoomId(),
            chatDto.getSenderId())
        .getBody())) {
      throw new JamChatException(ErrorCode.USER_NOT_ROOM_PARTICIPANT);
    }

    simpMessagingTemplate.convertAndSend(
        getDestination(chatDto.getRoomId()),
        chatDto
    );
  }

  public void sendError(String name, long roomId, Exception exception) {
    simpMessagingTemplate.convertAndSendToUser(
        name,
        DESTINATION_PREFIX + roomId,
        exception.toString(),
        createHeaders(name)
    );
  }

  public void enterRoom(
      StompHeaderAccessor accessor,
      long roomId,
      Map<String, DisconnectNeedInfo> map
  ) {

    String nickname = accessor
        .getFirstNativeHeader("nickname");

    String participantId = accessor
        .getFirstNativeHeader("participantId");

    String token = accessor
        .getFirstNativeHeader("Authorization");

    long userId = tokenProvider.getUserId(token);

    ParticipantResponse participantResponse = new ParticipantResponse(
        Long.parseLong(Objects.requireNonNull(participantId)),
        userId,
        nickname,
        ParticipantStatus.WAIT
    );

    ChatDto chatDto = new ChatDto(
        MessageType.ENTER,
        userId,
        roomId,
        nickname + "님이 입장했습니다."
    );

    map.put(accessor.getSessionId(), new DisconnectNeedInfo(token, roomId, userId, nickname));

    simpMessagingTemplate.convertAndSend(
        getDestination(roomId),
        ChatEnterDto.of(participantResponse, chatDto)
    );
  }

  public void leaveRoom(
      String sessionId,
      Map<String, DisconnectNeedInfo> map
  ) {
    DisconnectNeedInfo disconnectNeedInfo = map.get(sessionId);

    if (disconnectNeedInfo == null) {
      throw new JamChatException(ErrorCode.NOT_FOUND_SESSION_ID);
    }

    String token = TokenConstants.TOKEN_PREFIX + disconnectNeedInfo.getToken();
    long userId = disconnectNeedInfo.getUserId();
    long roomId = disconnectNeedInfo.getRoomId();
    String nickname = disconnectNeedInfo.getNickname();

    ResponseEntity<LeaveRoomResponse> responseEntity =
        gameClient.leaveRoom(token, roomId);

    LeaveRoomResponse leaveRoomResponse =
        responseEntity.getBody();

    if (leaveRoomResponse == null) {
      throw new JamChatException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    if (leaveRoomResponse.getHostUserId() == 0) {
      throw new JamChatException(
          leaveRoomResponse.getErrorCode(),
          leaveRoomResponse.getMessage()
      );
    }

    ChatDto chatDto = new ChatDto(
        MessageType.LEAVE,
        userId,
        roomId,
        nickname + "님이 나갔습니다."
    );

    map.remove(sessionId);

    simpMessagingTemplate.convertAndSend(
        getDestination(roomId),
        ChatLeaveDto.of(leaveRoomResponse, chatDto)
    );
  }

  private String getTokenAddPrefix(StompHeaderAccessor accessor) {
    return TokenConstants.TOKEN_PREFIX +
        accessor.getFirstNativeHeader("Authorization");
  }

  private String getDestination(long roomId) {
    return DESTINATION_PREFIX + roomId;
  }

  private MessageHeaders createHeaders(@Nullable String sessionId) {
    SimpMessageHeaderAccessor headerAccessor =
        SimpMessageHeaderAccessor.create(MESSAGE);

    if (sessionId != null) {
      headerAccessor.setSessionId(sessionId);
    }

    headerAccessor.setLeaveMutable(true);

    return headerAccessor.getMessageHeaders();
  }
}
