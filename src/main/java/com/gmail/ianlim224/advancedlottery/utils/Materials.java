package com.gmail.ianlim224.advancedlottery.utils;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Resolves material names from the config into modern {@link Material}s.
 * <p>
 * Replaces XSeries' XMaterial. Older configs (written for 1.8 - 1.12) may still contain
 * pre-flattening names such as {@code WATCH} or {@code STAINED_GLASS_PANE:5}; those are
 * mapped to their modern equivalents so existing configs keep working.
 */
public final class Materials {
    public static final Material FALLBACK = Material.STONE;

    private static final Map<String, String> LEGACY_ALIASES = new HashMap<>();
    private static final String[] COLORS = {
            "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY",
            "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK"
    };

    static {
        LEGACY_ALIASES.put("WATCH", "CLOCK");
        LEGACY_ALIASES.put("BOOK_AND_QUILL", "WRITABLE_BOOK");
        LEGACY_ALIASES.put("SKULL_ITEM", "PLAYER_HEAD");
        LEGACY_ALIASES.put("SKULL", "PLAYER_HEAD");
        LEGACY_ALIASES.put("EMPTY_MAP", "MAP");
        LEGACY_ALIASES.put("SIGN", "OAK_SIGN");
        LEGACY_ALIASES.put("WORKBENCH", "CRAFTING_TABLE");
        LEGACY_ALIASES.put("ENDER_PORTAL_FRAME", "END_PORTAL_FRAME");
        LEGACY_ALIASES.put("EXP_BOTTLE", "EXPERIENCE_BOTTLE");
        LEGACY_ALIASES.put("FIREWORK", "FIREWORK_ROCKET");
        LEGACY_ALIASES.put("INK_SACK", "INK_SAC");
        LEGACY_ALIASES.put("REDSTONE_TORCH_ON", "REDSTONE_TORCH");
        LEGACY_ALIASES.put("SNOW_BALL", "SNOWBALL");
        LEGACY_ALIASES.put("STORAGE_MINECART", "CHEST_MINECART");
        LEGACY_ALIASES.put("WOOD_DOOR", "OAK_DOOR");
        LEGACY_ALIASES.put("GOLD_RECORD", "MUSIC_DISC_13");
        LEGACY_ALIASES.put("GREEN_RECORD", "MUSIC_DISC_CAT");
        LEGACY_ALIASES.put("THIN_GLASS", "GLASS_PANE");
        LEGACY_ALIASES.put("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE");
        LEGACY_ALIASES.put("STAINED_GLASS", "WHITE_STAINED_GLASS");
        LEGACY_ALIASES.put("WOOL", "WHITE_WOOL");
        LEGACY_ALIASES.put("CARPET", "WHITE_CARPET");
    }

    private Materials() {
    }

    /**
     * Resolves a config material name. Unknown names fall back to {@link #FALLBACK}.
     */
    public static Material resolve(String name) {
        return resolve(name, null);
    }

    /**
     * Resolves a config material name, logging a warning through {@code logger} when the
     * name is unknown and the fallback is used.
     */
    public static Material resolve(String name, Logger logger) {
        Material material = tryResolve(name);
        if (material != null) {
            return material;
        }
        if (logger != null) {
            logger.warning("Unknown material '" + name + "' in config, using " + FALLBACK.name() + " instead.");
        }
        return FALLBACK;
    }

    /**
     * Resolves a config material name, returning {@code null} if it cannot be matched.
     */
    public static Material tryResolve(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.trim().toUpperCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
        if (normalized.startsWith("MINECRAFT:")) {
            normalized = normalized.substring("MINECRAFT:".length());
        }
        if (normalized.isEmpty()) {
            return null;
        }

        // Legacy "NAME:data" syntax, e.g. STAINED_GLASS_PANE:5
        int data = -1;
        int colon = normalized.indexOf(':');
        if (colon >= 0) {
            String dataPart = normalized.substring(colon + 1);
            normalized = normalized.substring(0, colon);
            if (SpigotCommons.isInteger(dataPart)) {
                data = Integer.parseInt(dataPart);
            }
        }

        if (data >= 0 && data < COLORS.length) {
            Material colored = colored(normalized, COLORS[data]);
            if (colored != null) {
                return colored;
            }
        }

        Material direct = Material.matchMaterial(normalized);
        if (direct != null && !direct.name().startsWith("LEGACY_")) {
            return direct;
        }

        String alias = LEGACY_ALIASES.get(normalized);
        if (alias != null) {
            return Material.matchMaterial(alias);
        }
        return null;
    }

    private static Material colored(String legacyName, String color) {
        switch (legacyName) {
            case "STAINED_GLASS_PANE":
                return Material.matchMaterial(color + "_STAINED_GLASS_PANE");
            case "STAINED_GLASS":
                return Material.matchMaterial(color + "_STAINED_GLASS");
            case "WOOL":
                return Material.matchMaterial(color + "_WOOL");
            case "CARPET":
                return Material.matchMaterial(color + "_CARPET");
            case "STAINED_CLAY":
                return Material.matchMaterial(color + "_TERRACOTTA");
            default:
                return null;
        }
    }
}
