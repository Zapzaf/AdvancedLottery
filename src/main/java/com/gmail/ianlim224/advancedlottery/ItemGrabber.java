package com.gmail.ianlim224.advancedlottery;

import com.gmail.ianlim224.advancedlottery.utils.Materials;
import org.bukkit.inventory.ItemStack;

import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;

public class ItemGrabber {
	
	private ItemStack previousArrow;
	private ItemStack nextArrow;
	private ItemStack bookInstructions;
	private ItemStack winNavigator;
	private ItemStack confirmBuy;
	private ItemStack cancelBuy;
	private ItemStack buyNavigator;
	private ItemStack buyButton;
	private ItemStack commandHelp;
	private ItemStack add;
	private ItemStack minus;
	
	private static ItemGrabber instance;
	
	public static ItemGrabber getInstance(AdvancedLottery plugin) {
		if(instance != null) {
			return instance;
		}
		instance = new ItemGrabber(plugin);
		return instance;
	}
	
	public static ItemGrabber reload(AdvancedLottery plugin) {
		instance = new ItemGrabber(plugin);
		return instance;
	}
	
	private ItemGrabber(AdvancedLottery plugin) {
		try {
		setPreviousArrow(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.PREVIOUS_PAGE_MATERIAL.getStringValue()))).setName(MenuItems.PREVIOUS_PAGE_NAME.getStringValue()).setLore(MenuItems.PREVIOUS_PAGE_LORE.getListValue()).toItemStack());
		setNextArrow(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.NEXT_PAGE_MATERIAL.getStringValue()))).setName(MenuItems.NEXT_PAGE_NAME.getStringValue()).setLore(MenuItems.NEXT_PAGE_LORE.getListValue()).toItemStack());
		setBookInstructions(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.INSTRUCTIONS_MATERIAL.getStringValue())))
			.setName(AdvancedLottery.f(MenuItems.INSTRUCTIONS_NAME.getStringValue())).setLore(MenuItems.INSTRUCTIONS_LORE.getListValue()).toItemStack());
		setWinNavigator(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.HELP_TUTORIAL_MATERIAL.getStringValue())))
			.setName(MenuItems.HELP_TUTORIAL_NAME.getStringValue())
			.setLore(MenuItems.HELP_TUTORIAL_LORE.getListValue())
			.toItemStack());
		setConfirmBuy(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.CONFIRM_BLOCK_MATERIAL.getStringValue()))).setName(MenuItems.CONFIRM_BLOCK_NAME.getStringValue()).toItemStack());
		setCancelBuy(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.CANCEL_BLOCK_MATERIAL.getStringValue()))).setName(MenuItems.CANCEL_BLOCK_NAME.getStringValue()).toItemStack());
		setBuyNavigator(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.CONFIRM_INFO_MATERIAL.getStringValue()))).setName(MenuItems.CONFIRM_INFO_NAME.getStringValue()).setLore(MenuItems.CONFIRM_INFO_LORE.getListValue()).toItemStack());
		setBuyButton(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.BUY_TICKET_MATERIAL.getStringValue()))).setName(MenuItems.BUY_TICKET_NAME.getStringValue()).setLore(MenuItems.BUY_TICKET_LORE.getListValue()).toItemStack());
		setCommandHelp(new ItemBuilder(new ItemStack(Materials.resolve(MenuItems.COMMAND_INTRO_MATERIAL.getStringValue()))).setName(MenuItems.COMMAND_INTRO_NAME.getStringValue()).setLore(
				MenuItems.COMMAND_INTRO_LORE.getListValue()
		).toItemStack());
		setAdd(new ItemBuilder(Materials.resolve(MenuItems.ADD_TICKET_MATERIAL.getStringValue())).setName(MenuItems.ADD_TICKET_NAME.getStringValue()).setLore(MenuItems.ADD_TICKET_LORE.getListValue()).toItemStack());
		setMinus(new ItemBuilder(Materials.resolve(MenuItems.MINUS_TICKET_MATERIAL.getStringValue())).setName(MenuItems.MINUS_TICKET_NAME.getStringValue()).setLore(MenuItems.MINUS_TICKET_LORE.getListValue()).toItemStack());
		
		} catch (Exception e) {
			System.out.println("---------------------------------");
			plugin.getLogger().warning("An error occurred while loading items.yml, resetting to default...");
			plugin.getLogger().warning("Please make sure you input a valid material type");
			System.out.println("---------------------------------");
			for(MenuItems i: MenuItems.values()) {
				MenuItems.getFc().set(i.getPath(), i.getDefaultListValue());
			}
			plugin.getItemsManager().saveConfig();
		}
	}

	public ItemStack getCommandHelp() {
		return commandHelp;
	}

	public void setCommandHelp(ItemStack commandHelp) {
		this.commandHelp = commandHelp;
	}

	public ItemStack getPreviousArrow() {
		return previousArrow;
	}

	public void setPreviousArrow(ItemStack previousArrow) {
		this.previousArrow = previousArrow;
	}

	public ItemStack getNextArrow() {
		return nextArrow;
	}

	public void setNextArrow(ItemStack nextArrow) {
		this.nextArrow = nextArrow;
	}

	public ItemStack getBookInstructions() {
		return bookInstructions;
	}

	public void setBookInstructions(ItemStack bookInstructions) {
		this.bookInstructions = bookInstructions;
	}

	public ItemStack getWinNavigator() {
		return winNavigator;
	}

	public void setWinNavigator(ItemStack winNavigator) {
		this.winNavigator = winNavigator;
	}

	public ItemStack getConfirmBuy() {
		return confirmBuy;
	}

	public void setConfirmBuy(ItemStack confirmBuy) {
		this.confirmBuy = confirmBuy;
	}

	public ItemStack getCancelBuy() {
		return cancelBuy;
	}

	public void setCancelBuy(ItemStack cancelBuy) {
		this.cancelBuy = cancelBuy;
	}

	public ItemStack getBuyNavigator() {
		return buyNavigator;
	}

	public void setBuyNavigator(ItemStack buyNavigator) {
		this.buyNavigator = buyNavigator;
	}

	public ItemStack getBuyButton() {
		return buyButton;
	}

	public void setBuyButton(ItemStack buyButton) {
		this.buyButton = buyButton;
	}

	public ItemStack getAdd() {
		return add;
	}

	public void setAdd(ItemStack add) {
		this.add = add;
	}

	public ItemStack getMinus() {
		return minus;
	}

	public void setMinus(ItemStack minus) {
		this.minus = minus;
	}
}
