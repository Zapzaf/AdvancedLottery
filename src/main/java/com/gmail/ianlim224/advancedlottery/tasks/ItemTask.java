package com.gmail.ianlim224.advancedlottery.tasks;

import com.gmail.ianlim224.advancedlottery.utils.Materials;
import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.gui.HelpGUI;
import com.gmail.ianlim224.advancedlottery.gui.LotteryGUI;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemTask implements Runnable {
    private final AdvancedLottery plugin;
    private ItemStack pot;
    private ItemStack clock;
    private LotteryPot lotteryPot = LotteryPot.getInstance(AdvancedLottery.getInstance());
    private LotteryGUI lotteryInventory;

    public ItemTask(AdvancedLottery plugin) {
        lotteryInventory = LotteryGUI.getInstance();
        this.plugin = plugin;
        try {
            updatePotAndClock();
        } catch (Exception e) {
            System.out.println("---------------------------------");
            plugin.getLogger().warning("An error occurred when loading items.yml, resetting to default...");
            plugin.getLogger().warning("Please make sure that all material values are valid");
            System.out.println("---------------------------------");
            for (MenuItems i : MenuItems.values()) {
                MenuItems.getFc().set(i.getPath(), i.getDefaultListValue());
                MenuItems.loadConfigValues();
            }
            plugin.getItemsManager().saveConfig();
        }
    }

    @Override
    public void run() {
        updatePotAndClock();

        for (Inventory inv : lotteryInventory.getMenuInventory()) {
            inv.setItem(47, getPot());
            inv.setItem(51, getClock());
        }
        HelpGUI.getInstance(plugin).getInventory().setItem(39, getPot());
        HelpGUI.getInstance(plugin).getInventory().setItem(41, getClock());
    }

    public ItemStack getPot() {
        return pot;
    }

    private void setPot(ItemStack pot) {
        this.pot = pot;
    }

    public ItemStack getClock() {
        return clock;
    }

    private void setClock(ItemStack clock) {
        this.clock = clock;
    }

    private void updatePotAndClock() {
        setPot(new ItemBuilder(Materials.resolve(MenuItems.MONEY_POT_MATERIAL.getStringValue())).setName(MenuItems.MONEY_POT_NAME.getStringValue())
                .setLore(AdvancedLottery.replace(MenuItems.MONEY_POT_LORE.getListValue(), "%money%", SpigotCommons.formatMoney(lotteryPot.getMoneyInPot()))).toItemStack());
        setClock(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.TIME_PREVIEW_MATERIAL.getStringValue()))).setName(MenuItems.TIME_PREVIEW_NAME.getStringValue())
                .setLore(MenuItems.TIME_PREVIEW_LORE.getStringValue().replaceAll("%time%", plugin.getLotteryTimer().time(false))
                        .replaceAll("%time_short%", plugin.getLotteryTimer().time(true)))
                .toItemStack());
    }

}
