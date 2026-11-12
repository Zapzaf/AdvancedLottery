package com.gmail.ianlim224.advancedlottery.utils;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CooldownTest {

    private static Player player() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        return player;
    }

    @Test
    void playerWithoutCooldownIsReady() {
        assertTrue(new Cooldown(Duration.ofMinutes(5)).isReady(player()));
    }

    @Test
    void playerIsNotReadyDuringCooldown() {
        Cooldown cooldown = new Cooldown(Duration.ofMinutes(5));
        Player player = player();
        cooldown.addCooldown(player);

        assertFalse(cooldown.isReady(player));
        Duration left = cooldown.getTimeLeft(player);
        assertTrue(left.compareTo(Duration.ofMinutes(4)) > 0 && left.compareTo(Duration.ofMinutes(5)) <= 0,
                "unexpected time left: " + left);
    }

    @Test
    void cooldownExpires() throws InterruptedException {
        Cooldown cooldown = new Cooldown(Duration.ofMillis(20));
        Player player = player();
        cooldown.addCooldown(player);
        Thread.sleep(50);
        assertTrue(cooldown.isReady(player));
    }

    @Test
    void cooldownsArePerPlayer() {
        Cooldown cooldown = new Cooldown(Duration.ofMinutes(1));
        Player first = player();
        Player second = player();
        cooldown.addCooldown(first);

        assertFalse(cooldown.isReady(first));
        assertTrue(cooldown.isReady(second));
    }
}
