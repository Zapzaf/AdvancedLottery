package com.gmail.ianlim224.advancedlottery.sounds;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;

public class WinSound implements ISound {
	public void playSound(Player player, AdvancedLottery plugin) {
		if (plugin.getConfig().getConfigurationSection("sounds").getBoolean("player_win_sound")) {
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
		}
	}
}
