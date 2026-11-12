package com.gmail.ianlim224.advancedlottery.items;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public enum MenuItems {
    // time preview
    TIME_PREVIEW_NAME("time_preview.name", false, "&d&lTIME TO DRAW"),
    TIME_PREVIEW_LORE("time_preview.lore", true, "&7Time left until next draw is &2%time%"),
    TIME_PREVIEW_MATERIAL("time_preview.material", false, "CLOCK"),

    // money pot
    MONEY_POT_NAME("money_pot.name", false, "&e&lMONEY IN POT"),
    MONEY_POT_LORE("money_pot.lore", true, "&7There are currently &b$%money% &7in the pot"),
    MONEY_POT_MATERIAL("money_pot.material", false, "BUCKET"),

    // help menu
    INSTRUCTIONS_NAME("help_menu.instructions_name", false, "&e&lInstructions"),
    INSTRUCTIONS_LORE("help_menu.instructions_lore", true, "&7This page shows instructions for lottery"),
    INSTRUCTIONS_MATERIAL("help_menu.instructions_material", false, "WRITABLE_BOOK"),
    HELP_TUTORIAL_NAME("help_menu.help_tutorial_name", false, "&e&lPrize"),
    HELP_TUTORIAL_LORE("help_menu.help_tutorial_lore", true,
            "&7Only one player will be selected as the winner",
            "&7That player would win the total amount of money in pot"),
    HELP_TUTORIAL_MATERIAL("help_menu.help_tutorial_material", false, "EMERALD"),
    COMMAND_INTRO_NAME("help_menu.command_intro_name", false, "&1&lCOMMANDS"),
    COMMAND_INTRO_LORE("help_menu.command_intro_lore", true,
            "&a/lottery -&2 main command",
            "&a/lottery help -&2 shows help menu",
            "&a/lottery menu -&2 opens the menu gui",
            "&a/lottery time -&2 shows countdown time left to draw",
            "&a/lottery buy -&2 buys a ticket",
            "&a/lottery stats <player> -&2 shows stats (Player is optional)"),
    COMMAND_INTRO_MATERIAL("help_menu.command_intro_material", false, "BOOK"),
    HELP_BACKGROUND_MATERIAL("help_menu.background_material", true, "YELLOW_STAINED_GLASS_PANE"),

    // lottery menu
    NEXT_PAGE_NAME("lottery_menu.next_page_name", false, "&d&lNext Page"),
    NEXT_PAGE_LORE("lottery_menu.next_page_lore", true, "&7View the next page of listings"),
    NEXT_PAGE_MATERIAL("lottery_menu.next_page_material", false, "ARROW"),
    PREVIOUS_PAGE_NAME("lottery_menu.previous_page_name", false, "&d&lPrevious Page"),
    PREVIOUS_PAGE_LORE("lottery_menu.previous_page_lore", true, "&7View the previous page of listings"),
    PREVIOUS_PAGE_MATERIAL("lottery_menu.previous_page_material", false, "ARROW"),
    BUY_TICKET_NAME("lottery_menu.buy_ticket_name", false, "&b&lBUY A TICKET"),
    BUY_TICKET_LORE("lottery_menu.buy_ticket_lore", true, "&7Click me to buy a ticket"),
    BUY_TICKET_MATERIAL("lottery_menu.buy_ticket_material", false, "COMPASS"),
    MENU_PANE_MATERIAL("lottery_menu.menu_pane_material", true, "GREEN_STAINED_GLASS_PANE"),
    PLAYER_HEAD_NAME("lottery_menu.player_head_name", false, "&a&l%player%"),
    PLAYER_HEAD_LORE("lottery_menu.player_head_lore", true, "&7Tickets bought: %tickets%", "&7Click for more info"),

    // confirm menu
    CONFIRM_INFO_NAME("confirm_menu.confirm_info_name", false, "&6&lCONFIRM PURCHASE"),
    CONFIRM_INFO_LORE("confirm_menu.confirm_info_lore", true, "&7Click to confirm purchase of one ticket"),
    CONFIRM_INFO_MATERIAL("confirm_menu.confirm_info_material", false, "BOOK"),

    CONFIRM_BLOCK_NAME("confirm_menu.confirm_block_name", false, "&a&lCONFIRM"),
    CONFIRM_BLOCK_MATERIAL("confirm_menu.confirm_block_material", false, "EMERALD_BLOCK"),

    CANCEL_BLOCK_NAME("confirm_menu.cancel_block_name", false, "&c&lCANCEL"),
    CANCEL_BLOCK_MATERIAL("confirm_menu.cancel_block_material", false, "REDSTONE_BLOCK"),

    TICKET_AMOUNT_NAME("confirm_menu.ticket_amount_name", false, "&e&l%ticket% &bticket(s) to be bought"),
    TICKET_AMOUNT_LORE("confirm_menu.ticket_amount_lore", true, "&7buy &e%ticket% &7ticket(s) for &e$%money%"),
    TICKET_AMOUNT_MATERIAL("confirm_menu.ticket_amount_material", false, "PAPER"),

    ADD_TICKET_NAME("confirm_menu.add_ticket_name", false, "&a&lAdd"),
    ADD_TICKET_LORE("confirm_menu.add_ticket_lore", true, "&7Add a ticket to be purchased"),
    ADD_TICKET_MATERIAL("confirm_menu.add_ticket_material", false, "ARROW"),

    MINUS_TICKET_NAME("confirm_menu.minus_ticket_name", false, "&c&lMinus"),
    MINUS_TICKET_LORE("confirm_menu.minus_ticket_lore", true, "&7Minus a ticket to be purchased"),
    MINUS_TICKET_MATERIAL("confirm_menu.minus_ticket_material", false, "ARROW"),

    // player statistics
    SEND_MESSAGE_NAME("player_stats_menu.send_message_name", false, "&6&lSend a message"),
    SEND_MESSAGE_MATERIAL("player_stats_menu.send_message_material", false, "PAPER"),
    SEND_MESSAGE_LORE("player_stats_menu.send_message_lore", false, "&7Send a message to &e%player%"),

    TICKETS_BOUGHT_NAME("player_stats_menu.tickets_bought_name", false, "&1&lTickets Bought"),
    TICKETS_BOUGHT_MATERIAL("player_stats_menu.tickets_bought_material", false, "EMERALD"),
    TICKETS_BOUGHT_LORE("player_stats_menu.tickets_bought_lore", true, "&7%player% has bought %ticket% ticket(s)");

    private static EnumMap<MenuItems, List<String>> configValue = new EnumMap<>(MenuItems.class);
    private static FileConfiguration fc;
    private String path;
    private List<String> defaultValue;
    private boolean isLore;

    private MenuItems(String path, boolean isLore, String... defaultValue) {
        this.path = path;
        this.defaultValue = Arrays.asList(defaultValue);
        this.isLore = isLore;
    }

    public static void loadConfigValues() {
        for (MenuItems items : MenuItems.values()) {
            if (items.isLore) {
                configValue.put(items, AdvancedLottery.f(fc.getStringList(items.getPath())));
            } else {
                List<String> list = new ArrayList<>();
                if (fc.get(items.getPath()) instanceof String) {
                    list.add(AdvancedLottery.f(fc.getString(items.getPath())));
                } else {
                    String s = fc.getString(items.getPath());
                    s = s.substring(1, s.length() - 1);
                    list.add(s);
                }
                configValue.put(items, list);
            }

        }
    }

    public static FileConfiguration getFc() {
        return fc;
    }

    public static void setFc(FileConfiguration newFc) {
        fc = newFc;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDefaultListValue() {
        return defaultValue;
    }

    public String getDefaultStringValue() {
        return defaultValue.get(0);
    }

    public List<String> getListValue() {
        return configValue.get(this);
    }

    public String getStringValue() {
        return AdvancedLottery.f(this.getListValue().get(0));
    }

    public boolean isLore() {
        return isLore;
    }
}
