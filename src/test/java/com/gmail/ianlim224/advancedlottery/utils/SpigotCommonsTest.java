package com.gmail.ianlim224.advancedlottery.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpigotCommonsTest {
    private static Locale previousLocale;

    @BeforeAll
    static void useUsLocale() {
        previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        SpigotCommons.setMoneyFormat("#,###,##0.00", null);
    }

    @AfterAll
    static void restoreLocale() {
        Locale.setDefault(previousLocale);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "42", "-7", "2147483647"})
    void isIntegerAcceptsIntegers(String input) {
        assertTrue(SpigotCommons.isInteger(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"-", "1.5", "abc", "12a", " 1"})
    void isIntegerRejectsNonIntegers(String input) {
        assertFalse(SpigotCommons.isInteger(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"10", "-2.5", "0.01", "1e3", "100000000000"})
    void isNumberAcceptsFiniteNumbers(String input) {
        assertTrue(SpigotCommons.isNumber(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "NaN", "Infinity", "-Infinity", " 5", "1,000"})
    void isNumberRejectsInvalidNumbers(String input) {
        assertFalse(SpigotCommons.isNumber(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "250", "2147483647"})
    void isNonNegativeIntAcceptsParsableDigits(String input) {
        assertTrue(SpigotCommons.isNonNegativeInt(input));
        Integer.parseInt(input); // must never throw when the check passes
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"-1", "1.0", "+5", "ten", "2147483648", "99999999999"})
    void isNonNegativeIntRejectsInvalidOrOverflowingValues(String input) {
        assertFalse(SpigotCommons.isNonNegativeInt(input));
    }

    @Test
    void toIntParsesOrFallsBack() {
        assertEquals(12, SpigotCommons.toInt("12", 0));
        assertEquals(12, SpigotCommons.toInt(" 12 ", 0));
        assertEquals(-3, SpigotCommons.toInt("x", -3));
        assertEquals(0, SpigotCommons.toInt(null, 0));
    }

    @Test
    void replacePlaceholdersReplacesKnownKeysOnly() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", "Steve");
        placeholders.put("wins", "3");
        placeholders.put("empty", null);

        assertEquals("Steve has 3 wins (3)!", SpigotCommons.replacePlaceholders("%player% has %wins% wins (%wins%)!", placeholders));
        assertEquals("keep %unknown%", SpigotCommons.replacePlaceholders("keep %unknown%", placeholders));
        assertEquals("[]", SpigotCommons.replacePlaceholders("[%empty%]", placeholders));
        assertNull(SpigotCommons.replacePlaceholders(null, placeholders));
    }

    @Test
    void formatMoneyUsesUnitsForLargeAmounts() {
        assertEquals("0.00", SpigotCommons.formatMoney(0));
        assertEquals("1,234.50", SpigotCommons.formatMoney(1234.5));
        assertEquals("999,999.00", SpigotCommons.formatMoney(999_999));
        assertEquals("1.00 million", SpigotCommons.formatMoney(1_000_000));
        assertEquals("2.50 billion", SpigotCommons.formatMoney(2_500_000_000d));
        assertEquals("7.00 trillion", SpigotCommons.formatMoney(7_000_000_000_000d));
    }

    @Test
    void colorCodesAreTranslated() {
        assertEquals("§aHello §lWorld", SpigotCommons.f("&aHello &lWorld"));
    }
}
