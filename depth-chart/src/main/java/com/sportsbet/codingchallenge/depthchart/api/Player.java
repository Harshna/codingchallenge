package com.sportsbet.codingchallenge.depthchart.api;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String playerName;

    @Column(unique = true)
    private Integer uniqueTeamNumber;

    //go thoriugh every attribute if it is enough - dicuss orphanRemoval
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepthChart> depthChart;

    public Player(String playerName, Integer uniqueTeamNumber) {
        this.playerName = playerName;
        this.uniqueTeamNumber = uniqueTeamNumber;
    }
}
