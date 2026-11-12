package com.gmail.ianlim224.advancedlottery.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateCheckerVersionTest {

    @ParameterizedTest(name = "{0} vs {1} -> {2}")
    @CsvSource({
            "12.4, 13.0, 13.0",
            "13.0, 12.4, 13.0",
            "13.0, 13.0.1, 13.0.1",
            "13.0.1, 13.0, 13.0.1",
            "1.9, 1.10, 1.10",
            "v13.0, 12.9, v13.0"
    })
    void returnsTheNewerVersion(String first, String second, String expected) {
        assertEquals(expected, UpdateChecker.VERSION_SCHEME_DECIMAL.compareVersions(first, second));
    }

    @Test
    void unsupportedVersionReturnsNull() {
        assertNull(UpdateChecker.VERSION_SCHEME_DECIMAL.compareVersions("latest", "13.0"));
    }
}
