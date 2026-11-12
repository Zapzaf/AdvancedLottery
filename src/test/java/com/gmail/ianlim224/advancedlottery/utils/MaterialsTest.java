package com.gmail.ianlim224.advancedlottery.utils;

import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MaterialsTest {

    @ParameterizedTest
    @CsvSource({
            "PAPER, PAPER",
            "paper, PAPER",
            "  Emerald_Block  , EMERALD_BLOCK",
            "minecraft:compass, COMPASS",
            "green stained glass pane, GREEN_STAINED_GLASS_PANE",
            "PLAYER_HEAD, PLAYER_HEAD"
    })
    void resolvesModernNames(String input, Material expected) {
        assertEquals(expected, Materials.resolve(input));
    }

    @ParameterizedTest
    @CsvSource({
            "WATCH, CLOCK",
            "BOOK_AND_QUILL, WRITABLE_BOOK",
            "SKULL_ITEM, PLAYER_HEAD",
            "EMPTY_MAP, MAP",
            "THIN_GLASS, GLASS_PANE",
            "STAINED_GLASS_PANE, WHITE_STAINED_GLASS_PANE",
            "WORKBENCH, CRAFTING_TABLE"
    })
    void mapsLegacyNamesToModernMaterials(String legacy, Material expected) {
        assertEquals(expected, Materials.resolve(legacy));
    }

    @ParameterizedTest
    @CsvSource({
            "STAINED_GLASS_PANE:5, LIME_STAINED_GLASS_PANE",
            "STAINED_GLASS_PANE:14, RED_STAINED_GLASS_PANE",
            "WOOL:15, BLACK_WOOL",
            "STAINED_CLAY:4, YELLOW_TERRACOTTA",
            "PAPER:0, PAPER"
    })
    void handlesLegacyDataValues(String legacy, Material expected) {
        assertEquals(expected, Materials.resolve(legacy));
    }

    @Test
    void unknownMaterialFallsBackAndWarns() {
        Logger logger = mock(Logger.class);
        assertEquals(Materials.FALLBACK, Materials.resolve("NOT_A_REAL_ITEM", logger));
        verify(logger).warning(contains("NOT_A_REAL_ITEM"));
    }

    @Test
    void knownMaterialDoesNotWarn() {
        Logger logger = mock(Logger.class);
        Materials.resolve("PAPER", logger);
        verify(logger, never()).warning(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void nullAndBlankAreUnresolvable() {
        assertNull(Materials.tryResolve(null));
        assertNull(Materials.tryResolve("   "));
        assertEquals(Materials.FALLBACK, Materials.resolve(null));
    }

    @Test
    void neverReturnsLegacyMaterials() {
        Material material = Materials.tryResolve("LEGACY_PAPER");
        if (material != null) {
            assertEquals(false, material.name().startsWith("LEGACY_"));
        }
    }

    /** Every default material shipped in the config must exist on 26.3. */
    @ParameterizedTest
    @EnumSource(value = MenuItems.class, mode = EnumSource.Mode.MATCH_ALL, names = ".*_MATERIAL")
    void defaultConfigMaterialsResolve(MenuItems item) {
        Material material = Materials.tryResolve(item.getDefaultStringValue());
        assertNotNull(material, item + " default '" + item.getDefaultStringValue() + "' is not a valid material");
        assertEquals(material, Materials.resolve(item.getDefaultStringValue()));
    }
}
