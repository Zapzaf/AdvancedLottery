package com.gmail.ianlim224.advancedlottery.mysql;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;

public class DataPrinter extends DataHandler {

	@Override
	protected void onDataReceive(CommandSender target, PlayerData data, UUID uuid, AdvancedLottery plugin) {
		Map<String, String> placeholders = new HashMap<>();
		placeholders.put("player", Bukkit.getOfflinePlayer(uuid).getName());
		placeholders.put("wins", Integer.toString(data.getWins()));
		placeholders.put("tickets", Integer.toString(data.getTickets()));
		placeholders.put("money", SpigotCommons.formatMoney(data.getMoney()));
		placeholders.put("money_won", SpigotCommons.formatMoney(data.getMoneyWon()));
		
		for(String msg : plugin.getConfig().getStringList("mysql.stats_message")) {
			target.sendMessage(AdvancedLottery.f(SpigotCommons.replacePlaceholders(msg, placeholders)));
		}
	}

}
