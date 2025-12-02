package com.mrrezik.irestart.utils;

import com.mrrezik.irestart.IRestartPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String color(String message) {
        if (message == null || message.isEmpty()) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 32);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + group).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static void sendMessage(CommandSender sender, String path) {
        Object val = IRestartPlugin.getInstance().getConfig().get(path);

        if (val instanceof List<?>) {
            List<?> list = (List<?>) val;
            for (Object obj : list) {
                if (obj instanceof String) {
                    sender.sendMessage(color((String) obj));
                }
            }
        } else if (val != null) {
            String prefix = IRestartPlugin.getInstance().getConfig().getString("messages.prefix", "");
            sender.sendMessage(color(prefix + val));
        }
    }

    public static void sendRaw(CommandSender sender, String msg) {
        sender.sendMessage(color(msg));
    }
}