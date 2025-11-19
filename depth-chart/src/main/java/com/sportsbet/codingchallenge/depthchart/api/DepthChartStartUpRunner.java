package com.sportsbet.codingchallenge.depthchart.api;

import com.sportsbet.codingchallenge.depthchart.api.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class DepthChartStartUpRunner implements CommandLineRunner {

    private PlayerRepository playerRepository;
    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Depth Chart Service ...");
        List<Player> players= List.of(new Player("Evans Mike",13),
                new Player("Tom Brady",12),
                new Player("Scott Miller",10),
                new Player("Alex Cappa",65),
                new Player("Richard Williams",9)
                );
        playerRepository.saveAll(players);

    }
}
