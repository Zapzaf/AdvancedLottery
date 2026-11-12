package com.gmail.ianlim224.advancedlottery;

import com.gmail.ianlim224.advancedlottery.bungee.LotteryChannelListener;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.LotteryCommand;
import com.gmail.ianlim224.advancedlottery.commands.admin.*;
import com.gmail.ianlim224.advancedlottery.commands.player.*;
import com.gmail.ianlim224.advancedlottery.config.ConfigManager;
import com.gmail.ianlim224.advancedlottery.gui.ConfirmGUI;
import com.gmail.ianlim224.advancedlottery.gui.HelpGUI;
import com.gmail.ianlim224.advancedlottery.gui.LotteryGUI;
import com.gmail.ianlim224.advancedlottery.hooks.AdvancedLotteryExpansion;
import com.gmail.ianlim224.advancedlottery.hooks.VaultEcon;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.legacy.SkullManager;
import com.gmail.ianlim224.advancedlottery.legacy.V_1_13_SkullImpl;
import com.gmail.ianlim224.advancedlottery.listeners.InventoryClick;
import com.gmail.ianlim224.advancedlottery.listeners.InventoryClose;
import com.gmail.ianlim224.advancedlottery.listeners.PlayerJoin;
import com.gmail.ianlim224.advancedlottery.listeners.PlayerQuit;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.mysql.LotterySql;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.parser.PlaceholderParser;
import com.gmail.ianlim224.advancedlottery.tasks.ItemTask;
import com.gmail.ianlim224.advancedlottery.time.LotteryTimer;
import com.gmail.ianlim224.advancedlottery.time.ReminderManager;
import com.gmail.ianlim224.advancedlottery.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import static com.gmail.ianlim224.advancedlottery.utils.UpdateChecker.UpdateReason.UNRELEASED_VERSION;
import static com.gmail.ianlim224.advancedlottery.utils.UpdateChecker.UpdateReason.UP_TO_DATE;

public class AdvancedLottery extends JavaPlugin {
    public static final String USER = "%%__USER__%%";
    static LotteryGrabber lotteryGrabber;
    private static AdvancedLottery instance;
    private final FileLogging fileLogging = new FileLogging(this);
    private final Random random = new Random();
    private final ReminderManager reminderManager = new ReminderManager(this);
    private final WinnerRegistry winnerRegistry = new WinnerRegistry(this);
    private final VaultEcon vaultEcon = new VaultEcon(this);
    private LotteryConfig lotteryConfig = new LotteryConfig(this);
    private ConfigManager messages = new ConfigManager(this, "", "messages.yml");
    private ConfigManager items = new ConfigManager(this, "", "items.yml");
    private LotteryTimer lotteryTimer;
    private SkullManager skullManager;
    private PersistenceManager persistenceManager = new PersistenceManager(this);
    private PluginMessageListener pluginMessageListener = new LotteryChannelListener();

    public static AdvancedLottery getInstance() {
        return instance;
    }

    public static LotteryGrabber getLotteryGrabber() {
        return lotteryGrabber;
    }

    public static String f(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> f(List<String> msg) {
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < msg.size(); i++) {
            newList.add(AdvancedLottery.f(msg.get(i)));
        }
        return newList;
    }

    public static List<String> replace(List<String> list, String regex, String value) {
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            newList.add(list.get(i).replaceAll(regex, value));
        }
        return newList;
    }


    @Override
    public void onEnable() {
        instance = this;

        skullManager = new V_1_13_SkullImpl();

        if (!vaultEcon.setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("Hooked into vault!");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Successfully hooked into PlaceholderAPI!");
            new AdvancedLotteryExpansion(this).register();
            Messages.setParser(new PlaceholderParser());
        }

        loadMessages();

        loadItems();

        lotteryConfig.loadDefault();
        lotteryConfig.loadGrabber();

        SpigotCommons.setMoneyFormat(lotteryGrabber.getMoneyFormat(), this);

        if (lotteryGrabber.shouldUpdate()) {
            checkForUpdates();
        }

        registerCommands();

        registerListeners();

        LotterySql sql = LotterySql.getInstance(this);
        sql.initDatabase();

        HelpGUI.getInstance(this).reload();

        LotteryGUI.getInstance().load(this);

        lotteryTimer = new LotteryTimer(this);
        lotteryTimer.start();

        reminderManager.read();
        winnerRegistry.read();

        startItemTask();

        UUIDFetcher.reload();

        if (persistenceManager.loadPreviousLotterySnapshot()) {
            Bukkit.broadcastMessage(AdvancedLottery.f("&aResuming previous lottery..."));
        }

        if (lotteryGrabber.isBungeeSync()) {
            getLogger().info("Enabling bungee sync");
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", pluginMessageListener);
        }
    }

    private void checkForUpdates() {
        UpdateChecker.init(this, 43668);
        UpdateChecker.get().requestUpdateCheck()
                .whenCompleteAsync((updateResult, throwable) -> {
                    if (updateResult.requiresUpdate()) {
                        getLogger().info("---------------------------------");
                        getLogger().info("There is a new version: v" + updateResult.getNewestVersion());
                        getLogger().info("You are running v" + getDescription().getVersion());
                        getLogger().info(
                                "Please download the new version at https://www.spigotmc.org/resources/lottery.43668/");
                        getLogger().info("---------------------------------");
                        return;
                    }

                    if (updateResult.getReason() == UP_TO_DATE) {
                        getLogger().info("You're running the latest version!");
                    } else if (updateResult.getReason() == UNRELEASED_VERSION) {
                        getLogger().info("Running unreleased version of plugin!");
                    } else {
                        getLogger().info("Error checking for updates on spigot!");
                    }
                });
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("save_lottery_progress_on_stop")) {
            getLogger().info("Saving current lottery to be loaded on next server start!");
            persistenceManager.saveCurrentLotterySnapshot();
            Bukkit.broadcastMessage(AdvancedLottery.f("&aSaving current lottery progress..."));
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(Messages.TICKET_MONEY_REFUNDED.getConfigValue(player));
            }

            for (Entry<UUID, Integer> entry : LotteryTicket.getInstance(this).getWhoBought().entrySet()) {
                vaultEcon.payMoney(entry.getValue() * (double) lotteryGrabber.getBuyPrice(),
                        Bukkit.getOfflinePlayer(entry.getKey()));
            }
        }

        LotterySql sql = LotterySql.getInstance(this);
        if (sql.isEnabled()) {
            sql.close();
        }

        winnerRegistry.save();
        reminderManager.save();
        reminderManager.clean();

        if (lotteryGrabber.isBungeeSync()) {
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        }
    }

    private void registerCommands() {
        PluginCommand mainCmd = this.getCommand("lottery");

        List<Executable> commands = new ArrayList<>();
        commands.add(new HelpCommand());
        commands.add(new TimeCommand());
        commands.add(new BuyCommand());
        commands.add(new MenuCommand());
        commands.add(new StatsCommand());
        commands.add(new EndCommand());
        commands.add(new ReloadCommand());
        commands.add(new ReminderCommand());
        commands.add(new SetTimeCommand());
        commands.add(new AddPotCommand());
        commands.add(new AddTicketsCommand());

        mainCmd.setExecutor(new LotteryCommand(this, commands));
        mainCmd.setUsage(Messages.WRONG_USAGE.getConfigValue(null));
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new InventoryClick(this), this);
        manager.registerEvents(new ConfirmGUI(this), this);
        manager.registerEvents(new UUIDFetcher(), this);
        manager.registerEvents(new PlayerQuit(), this);
        manager.registerEvents(new InventoryClose(), this);
        manager.registerEvents(new PlayerJoin(this), this);
    }

    private void loadMessages() {
        Messages.setFc(messages.getConfig());

        for (Messages msg : Messages.values()) {
            messages.getConfig().addDefault(msg.getPath(), msg.getValue());
        }
        messages.getConfig().options().copyDefaults(true);
        messages.saveConfig();
    }

    private void loadItems() {
        MenuItems.setFc(items.getConfig());

        items.getConfig().options().header("Lottery items configuration file - this file will load the items in guis"
                + "\nWarning: if any errors are detected, this file will automatically reset to default");

        for (MenuItems i : MenuItems.values()) {
            if (i.isLore()) {
                items.getConfig().addDefault(i.getPath(), i.getDefaultListValue());
            } else {
                items.getConfig().addDefault(i.getPath(), i.getDefaultStringValue());
            }
        }

        MenuItems.loadConfigValues();

        items.getConfig().options().copyDefaults(true);
        items.saveConfig();
        ItemGrabber.reload(this);
    }

    public ConfigManager getMessagesManager() {
        return messages;
    }

    public ConfigManager getItemsManager() {
        return items;
    }

    private void startItemTask() {
        getServer().getScheduler().runTaskTimer(this, new ItemTask(this), 0, 20);
    }

    public Random getRandom() {
        return random;
    }

    public ReminderManager getReminderManager() {
        return reminderManager;
    }

    public WinnerRegistry getWinnerRegistry() {
        return winnerRegistry;
    }

    public VaultEcon getVaultEcon() {
        return vaultEcon;
    }

    public LotteryTimer getLotteryTimer() {
        return lotteryTimer;
    }

    public SkullManager getSkullManager() {
        return skullManager;
    }

    public LotteryConfig getLotteryConfig() {
        return lotteryConfig;
    }

    public FileLogging getFileLogging() {
        return fileLogging;
    }
}
