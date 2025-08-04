package com.dailyquest.dailyquest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Stack;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "history",indexes ={
        @Index(name = "idx_user_id",columnList = "uid"),
        @Index(name = "idx_homework_id",columnList = "homeworkId"),
        @Index(name = "idx_start_end",columnList = "startDate,endDate")
})
public class HistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid",nullable = false)
    private  UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeworkId",nullable = false)
    private  HomeworksEntity homework;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private  LocalDate endDate;

    @Column(nullable = false)
    private boolean done;
}
