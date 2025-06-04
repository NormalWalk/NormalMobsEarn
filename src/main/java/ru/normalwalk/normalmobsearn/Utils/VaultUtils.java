package ru.normalwalk.normalmobsearn.Utils;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.normalwalk.normalmobsearn.Main;

public class VaultUtils {

    private final JavaPlugin plugin;
    private volatile Economy economy;
    private volatile Permission permission;
    private boolean economySetupAttempted = false;
    private boolean permissionSetupAttempted = false;

    public VaultUtils() {
        this.plugin = Main.getPlugin(); // Cache the plugin instance
    }

    public Economy getEconomy() {
        if (!economySetupAttempted) {
            synchronized (this) {
                if (!economySetupAttempted) {
                    setupEconomy();
                    economySetupAttempted = true;
                }
            }
        }
        return economy;
    }

    public Permission getPermission() {
        if (!permissionSetupAttempted) {
            synchronized (this) {
                if (!permissionSetupAttempted) {
                    setupPermissions();
                    permissionSetupAttempted = true;
                }
            }
        }
        return permission;
    }

    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault not found. Economy features disabled.");
            return;
        }
        
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy provider found. Economy features disabled.");
            return;
        }
        
        economy = rsp.getProvider();
        if (economy == null) {
            plugin.getLogger().warning("Failed to load economy provider.");
        }
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            plugin.getLogger().warning("No permission provider found. Permission features disabled.");
            return;
        }
        
        permission = rsp.getProvider();
        if (permission == null) {
            plugin.getLogger().warning("Failed to load permission provider.");
        }
    }
}
