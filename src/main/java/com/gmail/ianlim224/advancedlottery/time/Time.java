package com.gmail.ianlim224.advancedlottery.time;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Time {
    private final int days;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final boolean isShort;
    private final AdvancedLottery plugin;

    public Time(int days, int hours, int minutes, int seconds, boolean isShort, AdvancedLottery plugin) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.isShort = isShort;
        this.plugin = plugin;
    }

    public Time(long milis, boolean isShort, AdvancedLottery plugin) {
        days = (int) (milis / (1000 * 60 * 60 * 24));
        hours = (int) ((milis - TimeUnit.DAYS.toMillis(days)) / (1000 * 60 * 60));
        minutes = (int) ((milis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.DAYS.toMillis(days)) / (1000 * 60));
        seconds = (int) ((milis - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.HOURS.toMillis(hours) - TimeUnit.DAYS.toMillis(days)) / 1000);
        this.plugin = plugin;
        this.isShort = isShort;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isShort() {
        return isShort;
    }

    public long toMilis() {
        return TimeUnit.SECONDS.toMillis(seconds) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.HOURS.toMillis(hours)
                + TimeUnit.DAYS.toMillis(days);
    }

    public int toSeconds() {
        return (int) (seconds + TimeUnit.MINUTES.toSeconds(minutes) + TimeUnit.HOURS.toSeconds(hours)
                + TimeUnit.DAYS.toSeconds(days));
    }

    public int toTicks() {
        return toSeconds() * 20;
    }

    @Override
    public String toString() {
        FileConfiguration config = plugin.getConfig();

        String days, hours, minutes, seconds;
        if (!isShort) {
            days = getDays() == 0 ? "" : getDays() + " " + config.getString("time.days");
            hours = getHours() == 0 ? "" : getHours() + " " + config.getString("time.hours");
            minutes = getMinutes() == 0 ? "" : getMinutes() + " " + config.getString("time.minutes");
            seconds = getSeconds() == 0 ? "" : getSeconds() + " " + config.getString("time.seconds");
        } else {
            days = getDays() == 0 ? "" : getDays() + " " + config.getString("time.days_short");
            hours = getHours() == 0 ? "" : getHours() + " " + config.getString("time.hours_short");
            minutes = getMinutes() == 0 ? "" : getMinutes() + " " + config.getString("time.minutes_short");
            seconds = getSeconds() == 0 ? "" : getSeconds() + " " + config.getString("time.seconds_short");
        }

        // join the non-empty units with commas, e.g. "2 hours, 5 seconds"
        StringJoiner joiner = new StringJoiner(", ");
        for (String part : new String[]{days, hours, minutes, seconds}) {
            if (!part.isEmpty()) {
                joiner.add(part);
            }
        }
        return joiner.toString();
    }
}
