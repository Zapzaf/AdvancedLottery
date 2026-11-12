package com.gmail.ianlim224.advancedlottery.object;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LotteryPotTest {
    private AdvancedLottery plugin;
    private YamlConfiguration config;

    @BeforeEach
    void setUp() {
        config = new YamlConfiguration();
        config.set("starting_money_in_pot", 1000L);
        plugin = mock(AdvancedLottery.class);
        when(plugin.getConfig()).thenReturn(config);
        LotteryPot.reload(plugin);
    }

    @Test
    void startsWithConfiguredMoney() {
        assertEquals(1000, LotteryPot.getInstance(plugin).getMoneyInPot());
    }

    @Test
    void addingAndClearingMoney() {
        LotteryPot pot = LotteryPot.getInstance(plugin);
        pot.addMoneyInPot(250.5);
        pot.addMoneyInPot(49.5);
        assertEquals(1300, pot.getMoneyInPot());

        pot.clearMoneyInPot();
        assertEquals(1000, pot.getMoneyInPot(), "clearing resets to the starting amount");
    }

    @Test
    void getInstanceIsASingletonUntilReload() {
        LotteryPot first = LotteryPot.getInstance(plugin);
        assertSame(first, LotteryPot.getInstance(plugin));

        config.set("starting_money_in_pot", 50L);
        LotteryPot reloaded = LotteryPot.reload(plugin);
        assertSame(reloaded, LotteryPot.getInstance(plugin));
        assertEquals(50, reloaded.getMoneyInPot());
    }
}
