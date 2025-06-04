package ru.normalwalk.normalmobsearn.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.normalwalk.normalmobsearn.Main;
import ru.normalwalk.normalmobsearn.Utils.Coloriser;

public class ReloadCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("normalmobsearn.reload")) {
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(Coloriser.coloriser("&c&lМОБЫ: &fИспользование: &a/normalmobsearn reload"));
            return true;
        }

        try {
            Main.getPlugin().reloadConfig();
            sender.sendMessage(Coloriser.coloriser("&c&lМОБЫ: &fФайл &aconfig.yml &fуспешно перезагружен!"));
        } catch (Exception e) {
            sender.sendMessage(Coloriser.coloriser("&c&lМОБЫ: &fОшибка при перезагрузке конфигурации: " + e.getMessage()));
        }

        return true;
    }
}