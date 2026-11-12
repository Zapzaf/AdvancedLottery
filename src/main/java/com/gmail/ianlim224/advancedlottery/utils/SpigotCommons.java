package com.gmail.ianlim224.advancedlottery.utils;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.Map;

public class SpigotCommons {
    private static final long MILLION = 1000000L;
    private static final long BILLION = 1000000000L;
    private static final long TRILLION = 1000000000000L;
    private static DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###,##0.00");
    private SpigotCommons() {
    }

    public static void setMoneyFormat(String format, AdvancedLottery plugin) {
        try {
            MONEY_FORMAT = new DecimalFormat(format);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().info("Invalid money_display_format! Resetting to default format...");
        }
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static String formatMoney(double money) {
        return money < MILLION ? MONEY_FORMAT.format(money) :
                money < BILLION ? MONEY_FORMAT.format(money / MILLION) + " million" :
                        money < TRILLION ? MONEY_FORMAT.format(money / BILLION) + " billion" :
                                MONEY_FORMAT.format(money / TRILLION) + " trillion";
    }

    /**
     * Returns true if {@code str} is a finite decimal number (e.g. "10", "-2.5", "1e3").
     */
    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty() || !str.equals(str.trim())) {
            return false;
        }
        try {
            return Double.isFinite(Double.parseDouble(str));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true if {@code str} only contains digits and fits into an int,
     * so {@link Integer#parseInt(String)} is guaranteed to succeed.
     */
    public static boolean isNonNegativeInt(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses {@code str} as an int, returning {@code defaultValue} if it is not a valid int.
     */
    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Replaces every {@code %key%} in {@code message} with its value from {@code placeholders}.
     * Unknown placeholders are left untouched.
     */
    public static String replacePlaceholders(String message, Map<String, String> placeholders) {
        if (message == null) {
            return null;
        }
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("%" + entry.getKey() + "%", value);
        }
        return result;
    }

    public static String f(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
