package ru.normalwalk.normalmobsearn.Utils;

import org.bukkit.ChatColor;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Colorizer {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String colorizer(String text) {
        if (text == null || text.isEmpty()) return text;
        
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hexCode = matcher.group();
            StringBuilder replacement = new StringBuilder("&x");
            
            for (char c : hexCode.substring(1).toCharArray()) {
                replacement.append('&').append(c);
            }
            
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        
        String result = ChatColor.translateAlternateColorCodes('&', buffer.toString());
        return result.replace("&", "");
    }

    public static List<String> colorizer(List<String> text) {
        return text.stream()
                .map(Colorizer::colorizer)
                .collect(Collectors.toList());
    }
}
