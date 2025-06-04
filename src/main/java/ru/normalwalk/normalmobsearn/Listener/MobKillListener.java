package ru.normalwalk.normalmobsearn.Listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import ru.normalwalk.normalmobsearn.Main;
import ru.normalwalk.normalmobsearn.Utils.Colorizer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MobKillListener implements Listener {

    // Cache for boosters
    private static final Map<String, Double> BOOSTER_CACHE = new ConcurrentHashMap<>();
    private static long lastCacheUpdate = 0;
    private static final long CACHE_UPDATE_INTERVAL = 30000; // 30 seconds

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (!(player instanceof Player)) return;

        Main plugin = Main.getPlugin();
        String mobType = event.getEntity().getType().toString().toLowerCase();
        String mobPath = "mobs." + mobType;

        if (!plugin.getConfig().contains(mobPath)) return;

        // Get all values at once
        String mobName = plugin.getConfig().getString(mobPath + ".name");
        double baseEarn = plugin.getConfig().getDouble(mobPath + ".earn");
        double booster = getBooster(player);
        double finalEarn = baseEarn * booster;

        // Processing money
        if (plugin.getVault().getEconomy() != null) {
            plugin.getVault().getEconomy().depositPlayer(player, finalEarn);
        }

        // Formatting the amount
        boolean roundCoins = plugin.getConfig().getBoolean("Settings.round-money");
        String earnString = roundCoins ? String.valueOf((int) finalEarn) : String.format("%.2f", finalEarn);

        // Processing settings
        boolean showMessage = plugin.getConfig().getBoolean("Settings.message");
        boolean showActionBar = plugin.getConfig().getBoolean("Settings.actionbar");
        boolean playSound = plugin.getConfig().getBoolean("Settings.sound");

        // Sending messages
        if (showMessage) {
            sendMessage(player, earnString, mobName);
        }

        if (showActionBar) {
            String actionbar = plugin.getConfig().getString("earn-actionbar")
                    .replace("{earn}", earnString)
                    .replace("{mob}", mobName);
            sendActionBar(player, Colorizer.colorizer(actionbar));
        }

        if (playSound) {
            playSound(player);
        }
    }

    private void sendMessage(Player player, String earnString, String mobName) {
        Main plugin = Main.getPlugin();
        if (plugin.getConfig().isList("earn-message")) {
            List<String> messageList = plugin.getConfig().getStringList("earn-message");
            for (String line : messageList) {
                player.sendMessage(Colorizer.colorizer(
                    line.replace("{earn}", earnString)
                         .replace("{mob}", mobName)
                ));
            }
        } else {
            String message = plugin.getConfig().getString("earn-message")
                    .replace("{earn}", earnString)
                    .replace("{mob}", mobName);
            player.sendMessage(Colorizer.colorizer(message));
        }
    }

    private double getBooster(Player player) {
        Main plugin = Main.getPlugin();
        updateBoosterCache(plugin);

        double highestBooster = 1.0;
        for (Map.Entry<String, Double> entry : BOOSTER_CACHE.entrySet()) {
            if (plugin.getVault().getPermission() != null && 
                plugin.getVault().getPermission().playerInGroup(player, entry.getKey()) && 
                entry.getValue() > highestBooster) {
                highestBooster = entry.getValue();
            }
        }
        return highestBooster;
    }

    private void updateBoosterCache(Main plugin) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheUpdate > CACHE_UPDATE_INTERVAL) {
            BOOSTER_CACHE.clear();
            ConfigurationSection boostersSection = plugin.getConfig().getConfigurationSection("boosters");
            if (boostersSection != null) {
                for (String group : boostersSection.getKeys(false)) {
                    BOOSTER_CACHE.put(group, boostersSection.getDouble(group));
                }
            }
            lastCacheUpdate = currentTime;
        }
    }

    public static void clearBoosterCache() {
        BOOSTER_CACHE.clear();
        lastCacheUpdate = 0; // Forced cache update
    }

    private void sendActionBar(Player player, String message) {
        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(TextComponent.fromLegacyText(message)));
        } catch (Exception e) {
            player.sendMessage(Colorizer.colorizer(message));
        }
    }

    private void playSound(Player player) {
        Main plugin = Main.getPlugin();
        try {
            String soundName = plugin.getConfig().getString("Sound.name");
            if (soundName == null || soundName.isEmpty()) return;

            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Unknown sound: " + plugin.getConfig().getString("Sound.name"));
        } catch (Exception e) {
            plugin.getLogger().warning("Error playing sound: " + e.getMessage());
        }
    }
}
