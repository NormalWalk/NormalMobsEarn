package ru.normalwalk.normalmobsearn.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.normalwalk.normalmobsearn.Listener.MobKillListener;
import ru.normalwalk.normalmobsearn.Main;
import ru.normalwalk.normalmobsearn.Utils.Colorizer;

public class ReloadCommand implements CommandExecutor {

    private static final String PERMISSION = "normalmobsearn.reload";
    private static final String USAGE_MESSAGE = "&c&lMOBS: &fUsage: &a/normalmobsearn reload";
    private static final String SUCCESS_MESSAGE = "&c&lMOBS: &fFile &aconfig.yml &freloaded successfully!";
    private static final String ERROR_PREFIX = "&c&lMOBS: &fReload error: ";
    private static final String NO_PERMISSION_MESSAGE = "&cYou don't have permission to execute this command.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(Colorizer.colorize(NO_PERMISSION_MESSAGE));
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(Colorizer.colorize(USAGE_MESSAGE));
            return true;
        }

        try {
            Main plugin = Main.getPlugin();
            plugin.reloadConfig();
            
            // Reset booster cache after reload
            MobKillListener.clearBoosterCache();
            
            sender.sendMessage(Colorizer.colorize(SUCCESS_MESSAGE));
            plugin.getLogger().info("Configuration reloaded by command from " + sender.getName());
        } catch (Exception e) {
            String errorMsg = ERROR_PREFIX + e.getMessage();
            sender.sendMessage(Colorizer.colorize(errorMsg));
            Main.getPlugin().getLogger().severe("Configuration reload error: " + e.getMessage());
        }

        return true;
    }
}
