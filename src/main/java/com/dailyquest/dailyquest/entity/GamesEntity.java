package com.dailyquest.dailyquest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "games",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"uid", "name"})
        }
)
public class GamesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid",nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String gameName;

}
