package com.gmail.ianlim224.advancedlottery.time;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.fireworks.Fireworks;
import com.gmail.ianlim224.advancedlottery.gui.LotteryGUI;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.mysql.LotterySql;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.sounds.WinSound;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class LotteryTimer {
    private final AdvancedLottery plugin;
    private final long duration;
    private long start;
    private long end;
    private BukkitTask task;

    public LotteryTimer(AdvancedLottery plugin) {
        this.plugin = plugin;
        this.duration = TimeUnit.MINUTES.toMillis(plugin.getConfig().getInt("count_down_time"));
    }

    public void start() {
        start(duration);
    }

    public void start(long duration) {
        if (task != null)
            task.cancel();

        this.start = System.currentTimeMillis();

        this.end = start + duration;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                end();
            }
        }.runTaskLater(plugin, duration / 1000 * 20);

        plugin.getReminderManager().reload();
    }

    public void end() {
        plugin.getFileLogging().debug("Ending lottery...");
        if (LotteryTicket.getInstance(plugin).isEmpty()) {
            plugin.getFileLogging().debug("There were no winners!");

            if (plugin.getConfig().getBoolean("allow_broadcast")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(Messages.BROADCAST_NO_WINNER.getConfigValue(p));
                }
            }
        } else {
            LotteryPot lotteryPot = LotteryPot.getInstance(plugin);
            double tax = plugin.getConfig().getDouble("lottery_tax_in_percentage");
            if (tax > 1) {
                tax = 0;
                plugin.getLogger().warning("Lottery tax can only range from 0 - 1");
            }

            if (LotteryTicket.getInstance(plugin).getPlayers().size() == 1) {
                tax = 0;
            }

            double prize = lotteryPot.getMoneyInPot() * (1 - tax);

            OfflinePlayer winner = Bukkit.getOfflinePlayer(LotteryTicket.getInstance(plugin).selectWinner());

            processWinner(winner, prize, plugin.getConfig().getBoolean("shoot_fireworks_on_win"));

            broadcastWin(winner, prize);

            updateWinnerData(winner);
        }

        cleanUp();

        setDuration(duration);
    }

    private void processWinner(OfflinePlayer winner, double prize, boolean fireworks) {
        plugin.getFileLogging().debug(String.format("%s won the lottery with a prize of %f", winner.getName(), prize));
        plugin.getVaultEcon().payMoney(prize, winner);

        if (winner.isOnline()) {
            Player player = Bukkit.getPlayer(winner.getUniqueId());
            if (fireworks) {
                Fireworks fw = new Fireworks();
                fw.shootFireworks(player, plugin);
            }
            new WinSound().playSound(player, plugin);
        }

        LotterySql.getInstance(plugin).addWins(winner);
        LotterySql.getInstance(plugin).addMoneyWon(winner, prize);
    }

    private void broadcastWin(OfflinePlayer winner, double prize) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Messages.LOTTERY_WIN.getConfigValue(p).replaceAll("%player%", winner.getName())
                    .replaceAll("%money%", SpigotCommons.formatMoney(prize)));
        }

        if (plugin.getConfig().getBoolean("play_out_title_when_win")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(
                        Messages.WINNER_TITLE_MESSAGE.getConfigValue(p).replaceAll("%player%", winner.getName()),
                        Messages.WINNER_SUBTITLE_MESSAGE.getConfigValue(p).replaceAll("%money%",
                                SpigotCommons.formatMoney(prize)),
                        (int) (AdvancedLottery.getLotteryGrabber().getFadeInSeconds() * 20),
                        (int) (AdvancedLottery.getLotteryGrabber().getStaySeconds() * 20),
                        (int) (AdvancedLottery.getLotteryGrabber().getFadeOutSeconds() * 20));
            }
        }
    }

    private void updateWinnerData(OfflinePlayer winner) {
        plugin.getWinnerRegistry().setWinner(winner);
    }

    private void cleanUp() {
        LotteryTicket.getInstance(plugin).clear();
        LotteryGUI.getInstance().reset(plugin);
        LotteryPot.getInstance(plugin).clearMoneyInPot();
        plugin.getReminderManager().reload();
        LotterySql.getInstance(plugin).clearCache();
    }

    public long timeLeft() {
        return end - System.currentTimeMillis();
    }

    public String time(boolean isShort) {
        if (timeLeft() < 0)
            end();

        return new Time(timeLeft(), isShort, plugin).toString();
    }

    public long getDuration() {
        return end - System.currentTimeMillis();
    }

    public void setDuration(long duration) {
        start(duration);
    }
}
