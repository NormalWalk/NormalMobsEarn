package ru.normalwalk.normalmobsearn.Utils;

import org.bukkit.ChatColor;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Coloriser {

    public static String coloriser(String text) {

        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            String hexCode = text.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            StringBuilder builder = new StringBuilder();

            replaceSharp.chars().forEach(c -> builder.append("&").append((char) c));

            text = text.replace(hexCode, builder.toString());
            matcher = pattern.matcher(text);

        }

        return ChatColor.translateAlternateColorCodes('&', text).replace("&", "");

    }

    public static List<String> coloriser(List<String> text) {
        return text.stream().map(Coloriser::coloriser).collect(Collectors.toList());
    }
}

