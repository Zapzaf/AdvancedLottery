package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.utils.Materials;
import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * POJO for storing location of player's head in LotteryGUI
 */
class HeadLocation {


    private final int inventoryIndex;
    private final int slotIndex;

    public HeadLocation(int inventoryIndex, int slotIndex) {
        this.inventoryIndex = inventoryIndex;
        this.slotIndex = slotIndex;
    }

    public int getInventoryIndex() {
        return inventoryIndex;
    }

    public int getSlotIndex() {
        return slotIndex;
    }
}

public class LotteryGUI {
    private static final int[] PANE_SLOTS = {
            45, 46, 47, 51, 52, 53
    };
    private static LotteryGUI instance = null;
    private ArrayList<Inventory> menuInventory = null;
    private HashMap<Player, Integer> playerPage;
    private HashMap<UUID, HeadLocation> heads;
    private ItemGrabber grabber;

    private LotteryGUI() {
    }

    public static LotteryGUI getInstance() {
        if (instance == null) {
            instance = new LotteryGUI();
        }
        return instance;
    }

    public void load(AdvancedLottery plugin) {
        menuInventory = new ArrayList<>();
        playerPage = new HashMap<>();
        grabber = ItemGrabber.getInstance(plugin);
        heads = new HashMap<>();

        addNewPage();
        loadMenu();
    }

    public void reset(AdvancedLottery plugin) {
        menuInventory = null;
        playerPage = null;
        grabber = null;
        heads = null;

        load(plugin);
    }

    public Inventory createNewInventory() {
        return Bukkit.createInventory(new LotteryHolder(), 54,
                AdvancedLottery.f(AdvancedLottery.getLotteryGrabber().getLotteryMenuName()
                        .replaceAll("%number%", Integer.toString(menuInventory.size() + 1))));
    }

    public void openFirstPage(Player paramPlayer) {
        paramPlayer.openInventory(menuInventory.get(0));
        playerPage.put(paramPlayer, 1);
    }

    public void openNextPage(Player paramPlayer) {
        if (menuInventory.size() - playerPage.get(paramPlayer) > 0) {
            paramPlayer.openInventory(menuInventory.get(playerPage.get(paramPlayer)));
            playerPage.put(paramPlayer, playerPage.get(paramPlayer) + 1);
        } else {
            return;
        }
    }

    public void openPreviousPage(Player paramPlayer) {
        if ((playerPage.get(paramPlayer) - 1) >= 0) {
            paramPlayer.openInventory(menuInventory.get(playerPage.get(paramPlayer) - 1));
            playerPage.put(paramPlayer, playerPage.get(paramPlayer) - 1);
        } else {
            return;
        }
    }

    public void addPlayer(OfflinePlayer player) {
        if (heads.containsKey(player.getUniqueId())) {
            HeadLocation location = heads.get(player.getUniqueId());
            menuInventory.get(location.getInventoryIndex()).setItem(location.getSlotIndex(), AdvancedLottery.getInstance().getSkullManager().getSkull(player));
            return;
        }

        int slotIndex;
        if (getLastPage().firstEmpty() != -1) {
            slotIndex = getLastPage().firstEmpty();
            getLastPage().setItem(getLastPage().firstEmpty(), AdvancedLottery.getInstance().getSkullManager().getSkull(player));
        } else {
            addNewPage();
            slotIndex = getLastPage().firstEmpty();
            getLastPage().addItem(AdvancedLottery.getInstance().getSkullManager().getSkull(player));
        }

        heads.put(player.getUniqueId(), new HeadLocation(menuInventory.size() - 1, slotIndex));
    }

    private Inventory getLastPage() {
        return menuInventory.get(menuInventory.size() - 1);
    }

    public List<Inventory> getMenuInventory() {
        return menuInventory;
    }

    public void addNewPage() {
        menuInventory.add(createNewInventory());
        loadMenu();
    }

    public void loadMenu() {
        for (Inventory inv : menuInventory) {

            for (int i : PANE_SLOTS) {
                //parse item allows for itemstack data to be changed
                inv.setItem(i, new ItemBuilder(Materials.resolve(MenuItems.MENU_PANE_MATERIAL.getStringValue()))
                        .setName(" ").toItemStack());
            }

            inv.setItem(48, grabber.getPreviousArrow());
            inv.setItem(50, grabber.getNextArrow());
            inv.setItem(49, grabber.getBuyButton());
        }
    }
}
