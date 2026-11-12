package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.utils.Materials;
import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.clickablechat.ClickableText;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class PlayerStatsGUI implements Listener {
    private final AdvancedLottery plugin;
    private final Inventory inv;
    private final OfflinePlayer player;

    public PlayerStatsGUI(OfflinePlayer player, AdvancedLottery plugin) {
        this.plugin = plugin;
        this.player = player;
        inv = Bukkit.createInventory(new StatsHolder(), 54, AdvancedLottery.f(AdvancedLottery.getLotteryGrabber().getPlayerMenuName()));
        Bukkit.getPluginManager().registerEvents(this, plugin);

        for (int i = 0; i != 54; i++) {
            inv.setItem(i, new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setName(" ").toItemStack());
        }
    }

    public void openGui(Player p) {
        ItemStack playerHead = new ItemBuilder(plugin.getSkullManager().getSkull(player))
                .setLore(Collections.emptyList()).setName(MenuItems.PLAYER_HEAD_NAME.getStringValue().replaceAll("%player%", player.getName())).toItemStack();
        inv.setItem(13, playerHead);

        String target = player.getName();
        LotteryTicket ticket = LotteryTicket.getInstance(AdvancedLottery.getInstance());
        ItemStack message = new ItemBuilder(Materials.resolve(MenuItems.SEND_MESSAGE_MATERIAL.getStringValue())).setName(MenuItems.SEND_MESSAGE_NAME.getStringValue()).setLore(MenuItems.SEND_MESSAGE_LORE.getStringValue().replaceAll("%player%", target)).toItemStack();
        ItemStack ticketsBought = new ItemBuilder(Materials.resolve(MenuItems.TICKETS_BOUGHT_MATERIAL.getStringValue())).setName(MenuItems.TICKETS_BOUGHT_NAME.getStringValue()).setLore(MenuItems.TICKETS_BOUGHT_LORE.getStringValue().replaceAll("%player%", target).replaceAll("%ticket%", Integer.toString(ticket.getTicketsBought(player.getUniqueId())))).toItemStack();

        inv.setItem(39, message);
        inv.setItem(41, ticketsBought);

        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getInventory().getHolder() instanceof StatsHolder) {
            event.setCancelled(true);

            if (!player.isOnline()) {
                Player p = (Player) event.getWhoClicked();
                event.getWhoClicked().sendMessage(Messages.PLAYER_NOT_ONLINE.getConfigValue(p).replaceAll("%player%", player.getName()));
                event.getWhoClicked().closeInventory();
                return;
            }

            Player target = player.getPlayer();
            ItemStack item = event.getCurrentItem();
            if (item.getType() == Material.PAPER) {
                event.getWhoClicked().closeInventory();
                ClickableText chat = new ClickableText();
                chat.sendMessageWithAction((Player) event.getWhoClicked(), Messages.CLICK_ME_TEXT.getConfigValue((Player) event.getWhoClicked()), Messages.CLICK_ME_HOVER_TEXT.getConfigValue((Player) event.getWhoClicked()).replaceAll("%player%", player.getName()), target);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inv)) {
            HandlerList.unregisterAll(this);
        }
    }
}
