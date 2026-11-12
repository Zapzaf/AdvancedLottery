package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.utils.Materials;
import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.object.Purchase;
import com.gmail.ianlim224.advancedlottery.sounds.CancelSound;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmGUI implements Listener {
    private static Map<UUID, Integer> counter = new HashMap<>();
    private final Inventory inv;
    private ItemGrabber grabber;
    private CancelSound sound;
    private AdvancedLottery plugin;
    private LotteryTicket ticket;

    public ConfirmGUI(AdvancedLottery plugin) {
        inv = Bukkit.createInventory(new ConfirmHolder(), 54,
                AdvancedLottery.f(AdvancedLottery.getLotteryGrabber().getConfirmMenuName()));
        grabber = ItemGrabber.getInstance(plugin);
        sound = new CancelSound();
        this.plugin = plugin;
        ticket = LotteryTicket.getInstance(plugin);
    }

    public static int getCounter(Player player) {
        if (counter.containsKey(player.getUniqueId())) {
            return counter.get(player.getUniqueId());
        } else {
            throw new NullPointerException("Player is not added yet!");
        }
    }

    public static void removePlayer(Player player) {
        counter.remove(player.getUniqueId());
    }

    public static void addPlayerToCounter(Player player) {
        counter.put(player.getUniqueId(), 1);
    }

    private ItemStack getTicketAmountItem(final int amount) {
        return new ItemBuilder(Materials.resolve(MenuItems.TICKET_AMOUNT_MATERIAL.getStringValue())).setName(
                MenuItems.TICKET_AMOUNT_NAME.getStringValue().replaceAll("%ticket%", Integer.toString(amount)))
                .setLore(AdvancedLottery.replace(AdvancedLottery.replace(MenuItems.TICKET_AMOUNT_LORE.getListValue(), "%money%",
                        SpigotCommons.formatMoney(amount * plugin.getConfig().getDouble("buy_price"))), "%ticket%", Integer.toString(amount)))
                .toItemStack();
    }

    private void setTicketAmountItem(int amount, Inventory inv) {
        inv.setItem(22, getTicketAmountItem(amount));
    }

    public void openGui(Player player) {
        addPlayerToCounter(player);
        setTicketAmountItem(getCounter(player), inv);
        load();
        player.openInventory(inv);
    }

    public void load() {
        inv.setItem(4, grabber.getBuyNavigator());

        for (int i = 0; i < 3; i++) {
            inv.setItem(33 + i, grabber.getCancelBuy());
            inv.setItem(42 + i, grabber.getCancelBuy());
            inv.setItem(51 + i, grabber.getCancelBuy());
        }

        for (int i = 0; i < 3; i++) {
            inv.setItem(27 + i, grabber.getConfirmBuy());
            inv.setItem(36 + i, grabber.getConfirmBuy());
            inv.setItem(45 + i, grabber.getConfirmBuy());
        }

        inv.setItem(21, grabber.getAdd());
        inv.setItem(23, grabber.getMinus());
        setTicketAmountItem(1, inv);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        ItemStack eventItem = event.getCurrentItem();
        double buyPrice = plugin.getConfig().getLong("buy_price");
        Player player = (Player) event.getWhoClicked();

        if (eventItem == null || !(event.getClickedInventory().getHolder() instanceof ConfirmHolder)) {
            return;
        }

        // below if check so it will not throw npe
        int counter = getCounter(player);
        if (eventItem.equals(grabber.getConfirmBuy())) {
            if (ticket.getMaxTicketsCanBeBought(player) < counter) {
                player.sendMessage(Messages.TOO_MANY_TICKETS.getConfigValue(player));
                sound.playSound(player, plugin);
                player.closeInventory();
                return;
            }
            if (plugin.getVaultEcon().getBalance(player) < buyPrice * getCounter(player)) {
                player.sendMessage(Messages.NOT_ENOUGH_MONEY.getConfigValue(player));
                sound.playSound(player, plugin);
                player.closeInventory();
                return;
            }
            Purchase purchase = new Purchase(player, counter, plugin);
            purchase.executePurchase(true, true);
            event.setCancelled(true);
            player.closeInventory();
            return;
        }

        if (eventItem.equals(grabber.getCancelBuy())) {
            event.setCancelled(true);
            player.closeInventory();
            sound.playSound(player, plugin);
            return;
        }

        if (eventItem.equals(grabber.getAdd())) {
            event.setCancelled(true);

            if (ticket.getMaxTicketsCanBeBought(player) >= counter + 1) {
                setCounter(player, counter + 1);
                setTicketAmountItem(getCounter(player), event.getClickedInventory());
            }
        }

        if (eventItem.equals(grabber.getMinus())) {
            event.setCancelled(true);
            if (counter == 1)
                return;
            setCounter(player, counter - 1);
            setTicketAmountItem(getCounter(player), event.getClickedInventory());
        }
    }

    public void setCounter(Player player, int i) {
        if (counter.containsKey(player.getUniqueId())) {
            counter.put(player.getUniqueId(), i);
            setTicketAmountItem(i, inv);
        } else {
            throw new IllegalArgumentException("Player not found");
        }
    }
}