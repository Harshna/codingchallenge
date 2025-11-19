package com.sportsbet.codingchallenge.depthchart.api;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_depth_chart_position", columnList = "position")
        }
)
public class DepthChart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long depthChartDataId;

    @Column(nullable = false)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    private Integer positionDepth;

    public DepthChart(String position, Integer positionDepth, Player player) {
        this.position = position;
        this.player = player;
        this.positionDepth = positionDepth;
    }
}
