package com.gmail.ianlim224.advancedlottery.listeners;

import org.bukkit.Material;
import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.gui.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {

    private final ItemGrabber grabber;
    private final LotteryGUI gui;
    private final AdvancedLottery plugin;

    public InventoryClick(AdvancedLottery plugin) {
        grabber = ItemGrabber.getInstance(plugin);
        gui = LotteryGUI.getInstance();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getView().getTopInventory();

        if (inventory.getHolder() instanceof HelpHolder || inventory.getHolder() instanceof ConfirmHolder
                || inventory.getHolder() instanceof LotteryHolder) {
            event.setCancelled(true);
        }

        if (inventory.getHolder() instanceof LotteryHolder) {
            if (item == null) {
                return;
            }

            if (item.equals(grabber.getNextArrow())) {
                gui.openNextPage(player);
                return;
            }

            if (item.getType() == Material.PLAYER_HEAD) {
                OfflinePlayer target = plugin.getSkullManager().getPlayer(item);
                PlayerStatsGUI statsGui = new PlayerStatsGUI(target, plugin);
                statsGui.openGui(player);
                return;
            }

            if (item.equals(grabber.getPreviousArrow())) {
                gui.openPreviousPage(player);
                return;
            }

            if (item.equals(grabber.getBuyButton())) {
                Bukkit.dispatchCommand(player, "lottery buy");
            }
        }
    }
}
