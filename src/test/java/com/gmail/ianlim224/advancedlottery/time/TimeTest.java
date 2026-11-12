package com.gmail.ianlim224.advancedlottery.time;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeTest {
    private AdvancedLottery plugin;

    @BeforeEach
    void setUp() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("time.days", "days");
        config.set("time.hours", "hours");
        config.set("time.minutes", "minutes");
        config.set("time.seconds", "seconds");
        config.set("time.days_short", "d");
        config.set("time.hours_short", "h");
        config.set("time.minutes_short", "m");
        config.set("time.seconds_short", "s");

        plugin = mock(AdvancedLottery.class);
        when(plugin.getConfig()).thenReturn(config);
    }

    // ---- TimeParser ----

    @Test
    void parsesAllUnits() {
        TimeParser parser = new TimeParser("1d2h3m4s", plugin);
        assertTrue(parser.isValid());

        Time time = parser.getTime();
        assertEquals(1, time.getDays());
        assertEquals(2, time.getHours());
        assertEquals(3, time.getMinutes());
        assertEquals(4, time.getSeconds());
        assertEquals(93_784, time.toSeconds());
    }

    @Test
    void ignoresCaseAndWhitespace() {
        TimeParser parser = new TimeParser(" 1D 30M ", plugin);
        assertTrue(parser.isValid());
        assertEquals(TimeUnit.MINUTES.toMillis(24 * 60 + 30), parser.getTime().toMilis());
        assertEquals("1d30m", parser.toString());
    }

    @Test
    void repeatedUnitsAreAdded() {
        Time time = new TimeParser("5m10m", plugin).getTime();
        assertEquals(15, time.getMinutes());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "10", "0s", "m"})
    void rejectsInvalidInput(String input) {
        assertFalse(new TimeParser(input, plugin).isValid());
    }

    // ---- Time ----

    @Test
    void millisConstructorSplitsIntoUnits() {
        long millis = TimeUnit.DAYS.toMillis(2) + TimeUnit.HOURS.toMillis(5)
                + TimeUnit.MINUTES.toMillis(6) + TimeUnit.SECONDS.toMillis(7) + 999;
        Time time = new Time(millis, false, plugin);

        assertEquals(2, time.getDays());
        assertEquals(5, time.getHours());
        assertEquals(6, time.getMinutes());
        assertEquals(7, time.getSeconds());
    }

    @Test
    void roundTripsThroughMillis() {
        Time original = new Time(3, 4, 5, 6, false, plugin);
        Time copy = new Time(original.toMilis(), false, plugin);
        assertEquals(original.toSeconds(), copy.toSeconds());
        assertEquals(original.toSeconds() * 20, original.toTicks());
    }

    @Test
    void longFormatJoinsUnitsWithCommas() {
        assertEquals("1 days, 2 hours, 3 minutes, 4 seconds", new Time(1, 2, 3, 4, false, plugin).toString());
    }

    @Test
    void shortFormatUsesShortLabelsAndSkipsZeroUnits() {
        // regression: a zero unit in the middle used to drop the comma ("2 h5 s")
        assertEquals("2 h, 5 s", new Time(0, 2, 0, 5, true, plugin).toString());
        assertEquals("1 d, 3 m", new Time(1, 0, 3, 0, true, plugin).toString());
        assertEquals("10 m", new Time(0, 0, 10, 0, true, plugin).toString());
        assertEquals("", new Time(0, 0, 0, 0, true, plugin).toString());
    }
}
