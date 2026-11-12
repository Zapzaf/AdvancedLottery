package com.gmail.ianlim224.advancedlottery.commands.admin;

import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.Purchase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddTicketsCommand implements Executable {
    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length != 3)
            return CommandResponse.INCORRECT_ARGS;
        Player player = Bukkit.getPlayer(args[1]);

        if (player == null) {
            sender.sendMessage(AdvancedLottery.f("&cCannot find player " + args[1]));
            return CommandResponse.SUCCESS;
        }

        if (!SpigotCommons.isNonNegativeInt(args[2])) {
            sender.sendMessage(AdvancedLottery.f("&cInvalid arguments. Please check command syntax" +
                    " and make sure that the amount of tickets is a positive integer!"));
            return CommandResponse.SUCCESS;
        }

        int tickets = Integer.parseInt(args[2]);

        player.sendMessage(Messages.FREE_TICKETS_GIVEN.getConfigValue(player).replaceAll("%ticket%", Integer.toString(tickets)));
        Purchase purchase = new Purchase(player, tickets, plugin);
        purchase.executePurchase(true, false);
        sender.sendMessage(AdvancedLottery.f("&aSuccessfully given player tickets!"));
        return CommandResponse.SUCCESS;
    }

    @Override
    public String getLabel() {
        return "addtickets";
    }

    @Override
    public Permissions getPermission() {
        return Permissions.ADMIN;
    }

    @Override
    public boolean isCmdPlayerOnly() {
        return false;
    }
}
