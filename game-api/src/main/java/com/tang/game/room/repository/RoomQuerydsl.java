package com.tang.game.room.repository;

import static com.tang.game.common.type.RoomStatus.VALID;
import static com.tang.game.room.domain.QRoom.room;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.TeamType;
import com.tang.game.room.dto.QRoomDto;
import com.tang.game.room.dto.RoomDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RoomQuerydsl {

  private final JPAQueryFactory jpaQueryFactory;

  public Page<RoomDto> findAllByTitleAndStatus(
      String keyword,
      GameType gameType,
      TeamType teamType,
      Pageable pageable
  ) {
    return new PageImpl<>(
        searchRooms(keyword, gameType, teamType, pageable),
        pageable,
        countRooms(keyword, gameType, teamType)
    );
  }

  public Optional<RoomDto> findByIdAndStatus(Long roomId) {
    return searchRoom(roomId);
  }

  private List<RoomDto> searchRooms(
      String keyword,
      GameType gameType,
      TeamType teamType,
      Pageable pageable
  ) {
    return searchRoomQuery()
        .where(
            containsTitle(keyword),
            eqGameType(gameType),
            eqTeamType(teamType),
            eqStatus(VALID)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(new OrderSpecifier<>(Order.DESC, room.createdAt))
        .fetch();
  }

  private Optional<RoomDto> searchRoom(Long roomId) {
    return Optional.ofNullable(searchRoomQuery()
        .where(room.id.eq(roomId), eqStatus(VALID))
        .fetchOne());
  }

  private JPAQuery<RoomDto> searchRoomQuery() {
    return jpaQueryFactory.select(new QRoomDto(
            room.id,
            room.hostUserId,
            room.title,
            room.password,
            room.limitedNumberPeople,
            room.gameType,
            room.teamType))
        .from(room);
  }

  private Long countRooms(
      String keyword,
      GameType gameType,
      TeamType teamType
  ) {
    return jpaQueryFactory.select(room.id.count())
        .from(room)
        .where(
            containsTitle(keyword),
            eqGameType(gameType),
            eqTeamType(teamType),
            eqStatus(VALID)
        )
        .fetchOne();
  }

  private BooleanExpression eqStatus(RoomStatus roomStatus) {
    return room.status.eq(roomStatus);
  }

  private BooleanExpression containsTitle(String keyword) {
    return keyword == null
        ? null
        : room.title.contains(keyword);
  }

  private BooleanExpression eqGameType(GameType gameType) {
    return gameType == GameType.ALL || gameType == null
        ? null
        : room.gameType.eq(gameType);
  }

  private BooleanExpression eqTeamType(TeamType teamType) {
    return teamType == TeamType.ALL || teamType == null
        ? null
        : room.teamType.eq(teamType);
  }
}
