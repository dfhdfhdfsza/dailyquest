package com.dailyquest.dailyquest.entity;

import com.dailyquest.dailyquest.type.HomeworkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "homeworks",indexes = {
        @Index(name = "idx_game_id",columnList = "gameId")
})
public class HomeworksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long homeworkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameId",nullable = false)
    private GamesEntity game;

    @Column(nullable = false)
    private String homeworkTitle;


    private String homeworkMemo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomeworkType homeworkType;

}
