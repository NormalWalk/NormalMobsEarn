package ru.normalwalk.normalmobsearn;

import net.milkbowl.vault.Vault;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ru.normalwalk.normalmobsearn.Command.ReloadCommand;
import ru.normalwalk.normalmobsearn.Listener.MobKillListener;
import ru.normalwalk.normalmobsearn.Utils.Colorizer;
import ru.normalwalk.normalmobsearn.Utils.VaultUtils;

public class Main extends JavaPlugin {

    private static Main instance;
    private VaultUtils vaultUtils;
    public static ConsoleCommandSender console;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        console = this.getServer().getConsoleSender();
        
        // Initialize Vault integration
        vaultUtils = new VaultUtils();
        
        // Register event listener
        getServer().getPluginManager().registerEvents(new MobKillListener(), this);
        
        // Register command
        getCommand("normalmobsearn").setExecutor(new ReloadCommand());

        // Send startup message
        sendStartupBanner();
    }

    private void sendStartupBanner() {
        String version = getDescription().getVersion();
        console.sendMessage(Colorizer.colorize(""));
        console.sendMessage(Colorizer.colorize("&6╔ &fPlugin &aNormalMobsEarn &f(&e" + version + "&f)"));
        console.sendMessage(Colorizer.colorize("&6╚ &fDeveloper - &avk.com/normalwalk &f/ &at.me/normalwalk"));
        console.sendMessage(Colorizer.colorize(""));
    }

    public static Main getPlugin() {
        return instance;
    }

    public VaultUtils getVault() {
        return vaultUtils;
    }
}
