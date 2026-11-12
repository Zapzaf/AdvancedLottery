package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.utils.Materials;
import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HelpGUI {
    private static HelpGUI instance;
    private final AdvancedLottery plugin;
    private Inventory inventory;

    private HelpGUI(AdvancedLottery plugin) {
        this.plugin = plugin;
        reload();
    }

    public static HelpGUI getInstance(AdvancedLottery plugin) {
        if (instance == null)
            instance = new HelpGUI(plugin);

        return instance;
    }

    public void show(Player player) {
        player.openInventory(inventory);
    }

    public void reload() {
        inventory = Bukkit.createInventory(new HelpHolder(), 54,
                AdvancedLottery.f(AdvancedLottery.getLotteryGrabber().getHelpMenuName()));

        for (int i = 0; i < 54; i++) {
            //parse item allows for itemstack data to be changed
            inventory.setItem(i, new ItemBuilder(Materials.resolve(MenuItems.HELP_BACKGROUND_MATERIAL.getStringValue()))
                    .setName(" ").toItemStack());
        }
        inventory.setItem(4, ItemGrabber.getInstance(plugin).getBookInstructions());
        inventory.setItem(31, ItemGrabber.getInstance(plugin).getWinNavigator());
        inventory.setItem(49, ItemGrabber.getInstance(plugin).getCommandHelp());
    }

    public Inventory getInventory() {
        return inventory;
    }
}
