package com.tang.game.room.domain;

import com.tang.core.domain.BaseEntity;
import com.tang.game.common.type.GameType;
import com.tang.game.common.type.RoomStatus;
import com.tang.game.common.type.TeamType;
import com.tang.game.room.dto.CreateRoomForm;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@AuditOverride(forClass = BaseEntity.class)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    private String password;

    private int limitedNumberPeople;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public static Room from(CreateRoomForm form) {
        return Room.builder()
            .userId(form.getUserId())
            .title(form.getTitle())
            .password(form.getPassword())
            .limitedNumberPeople(form.getLimitedNumberPeople())
            .gameType(form.getGameType())
            .teamType(form.getTeamType())
            .status(RoomStatus.VALID)
            .build();
    }
}
