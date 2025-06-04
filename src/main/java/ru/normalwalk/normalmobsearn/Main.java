package ru.normalwalk.normalmobsearn;

import net.milkbowl.vault.Vault;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ru.normalwalk.normalmobsearn.Command.ReloadCommand;
import ru.normalwalk.normalmobsearn.Listener.MobKillListener;
import ru.normalwalk.normalmobsearn.Utils.Coloriser;
import ru.normalwalk.normalmobsearn.Utils.VaultUtils;

public class Main extends JavaPlugin {

    private static Main instance;
    public static ConsoleCommandSender log;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        log = this.getServer().getConsoleSender();
        new VaultUtils();
        getServer().getPluginManager().registerEvents(new MobKillListener(), this);
        getCommand("normalmobsearn").setExecutor(new ReloadCommand());

        // а кто мешает мне лог сюда добавить?)
        log.sendMessage(Coloriser.coloriser(""));
        log.sendMessage(Coloriser.coloriser("&c╔ &fПлагин &aNormalMobsEarn &f(&e" + getDescription().getVersion() + "&f)"));
        log.sendMessage(Coloriser.coloriser("&c╚ &fРазработчик - &avk.com/normalwalk &f/ &at.me.normalwalk"));
        log.sendMessage(Coloriser.coloriser(""));
    }


    public static Main getPlugin() {
        return instance;
    }

    public VaultUtils getVault() {
        return new VaultUtils();
    }
}
