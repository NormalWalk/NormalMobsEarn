package ru.normalwalk.normalmobsearn.Listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import ru.normalwalk.normalmobsearn.Main;
import ru.normalwalk.normalmobsearn.Utils.Coloriser;
import java.util.List;
import java.util.Random;

public class MobKillListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player player = event.getEntity().getKiller();
        String mobType = event.getEntity().getType().toString().toLowerCase();
        if (!Main.getPlugin().getConfig().contains("mobs." + mobType)) return;
        String mobName = Main.getPlugin().getConfig().getString("mobs." + mobType + ".name");
        double baseEarn = getRandomEarn(mobType);
        double booster = getBooster(player);
        double finalEarn = baseEarn * booster;

        if (Main.getPlugin().getVault().getEconomy() != null) {
            Main.getPlugin().getVault().getEconomy().depositPlayer(player, finalEarn);
        }

        boolean roundCoins = Main.getPlugin().getConfig().getBoolean("Settings.round-money");
        String earnString;
        if (roundCoins) {
            earnString = String.valueOf((int) finalEarn);
        } else {
            earnString = String.format("%.2f", finalEarn);
        }

        if (Main.getPlugin().getConfig().getBoolean("Settings.message")) {
            sendMessage(player, earnString, mobName);
        }

        if (Main.getPlugin().getConfig().getBoolean("Settings.actionbar")) {
            String actionbar = Main.getPlugin().getConfig().getString("earn-actionbar")
                    .replace("{earn}", earnString)
                    .replace("{mob}", mobName);
            sendActionBar(player, Coloriser.coloriser(actionbar));
        }

        if (Main.getPlugin().getConfig().getBoolean("Settings.sound")) {
            playSound(player);
        }
    }

    private double getRandomEarn(String mobType) {
        String earnValue = Main.getPlugin().getConfig().getString("mobs." + mobType + ".earn");

        if (earnValue.contains("-")) {
            String[] parts = earnValue.split("-");
            if (parts.length == 2) {
                try {
                    double min = Double.parseDouble(parts[0].trim());
                    double max = Double.parseDouble(parts[1].trim());
                    return min + (max - min) * random.nextDouble();
                } catch (NumberFormatException e) {
                    Main.getPlugin().getLogger().warning("Неверный формат диапазона для моба " + mobType + ": " + earnValue);
                    return 0.0;
                }
            }
        }

        try {
            return Double.parseDouble(earnValue);
        } catch (NumberFormatException e) {
            return Main.getPlugin().getConfig().getDouble("mobs." + mobType + ".earn", 0.0);
        }
    }

    private void sendMessage(Player player, String earnString, String mobName) {
        if (Main.getPlugin().getConfig().isList("earn-message")) {
            List<String> messageList = Main.getPlugin().getConfig().getStringList("earn-message");
            for (String line : messageList) {
                String processedLine = line
                        .replace("{earn}", earnString)
                        .replace("{mob}", mobName);
                player.sendMessage(Coloriser.coloriser(processedLine));
            }
        } else {
            String message = Main.getPlugin().getConfig().getString("earn-message")
                    .replace("{earn}", earnString)
                    .replace("{mob}", mobName);
            player.sendMessage(Coloriser.coloriser(message));
        }
    }

    private double getBooster(Player player) {
        double highestBooster = 1.0;

        for (String group : Main.getPlugin().getConfig().getConfigurationSection("boosters").getKeys(false)) {
            if (Main.getPlugin().getVault().getPermission() != null &&
                    Main.getPlugin().getVault().getPermission().playerInGroup(player, group)) {
                double groupBooster = Main.getPlugin().getConfig().getDouble("boosters." + group);
                if (groupBooster > highestBooster) {
                    highestBooster = groupBooster;
                }
            }
        }

        return highestBooster;
    }

    private void sendActionBar(Player player, String message) {
        try {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
        } catch (Exception e) {
            player.sendMessage(Coloriser.coloriser(message));
        }
    }

    private void playSound(Player player) {
        try {
            String soundName = Main.getPlugin().getConfig().getString("Sound.name");
            float volume = (float) 1.0;
            float pitch = (float) 1.0;

            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            Main.getPlugin().getLogger().warning("Неизвестный звук: " + Main.getPlugin().getConfig().getString("Sound.name"));
        } catch (Exception e) {
            Main.getPlugin().getLogger().warning("Ошибка при воспроизведении звука: " + e.getMessage());
        }
    }
}