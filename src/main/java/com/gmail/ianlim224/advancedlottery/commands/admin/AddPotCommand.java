package com.gmail.ianlim224.advancedlottery.commands.admin;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import org.bukkit.command.CommandSender;

public class AddPotCommand implements Executable {

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length != 2) {
            return CommandResponse.INCORRECT_ARGS;
        }

        if (!SpigotCommons.isNumber(args[1])) {
            return CommandResponse.INCORRECT_ARGS;
        }

        double amount = Double.parseDouble(args[1]);
        LotteryPot.getInstance(plugin).addMoneyInPot(amount);
        sender.sendMessage(AdvancedLottery.f("&aSuccessfully added &e$" +
                SpigotCommons.formatMoney(amount) + " &ato lottery pot!"));
        return CommandResponse.SUCCESS;
    }

    @Override
    public String getLabel() {
        return "addpot";
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
