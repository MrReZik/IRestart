package com.mrrezik.irestart.commands;

import com.mrrezik.irestart.IRestartPlugin;
import com.mrrezik.irestart.tasks.AutoRestartTask;
import com.mrrezik.irestart.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("irestart.admin")) {
            Utils.sendMessage(sender, "messages.no-perm");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            List<String> help = IRestartPlugin.getInstance().getConfig().getStringList("messages.help");
            for (String s : help) Utils.sendRaw(sender, s);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            IRestartPlugin.getInstance().reloadConfig();
            IRestartPlugin.getInstance().startTimer();
            Utils.sendMessage(sender, "messages.reloaded");
            return true;
        }

        if (args[0].equalsIgnoreCase("now")) {
            new AutoRestartTask().executeRestart();
            return true;
        }

        FileConfiguration config = IRestartPlugin.getInstance().getConfig();

        if (args[0].equalsIgnoreCase("type")) {
            if (args.length < 2) {
                Utils.sendRaw(sender, "&cУкажите тип: RESTART или RELOAD");
                return true;
            }
            config.set("settings.type", args[1].toUpperCase());
            IRestartPlugin.getInstance().saveConfig();
            Utils.sendRaw(sender, "&aТип действия изменен на: " + args[1].toUpperCase());
            return true;
        }

        if (args[0].equalsIgnoreCase("time") && args.length >= 3 && args[1].equalsIgnoreCase("set")) {
            if (!args[2].matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                Utils.sendRaw(sender, "&cНеверный формат! Используйте HH:mm");
                return true;
            }
            config.set("settings.time", args[2]);
            IRestartPlugin.getInstance().saveConfig();
            IRestartPlugin.getInstance().startTimer();
            Utils.sendRaw(sender, "&aВремя рестарта установлено: " + args[2]);
            return true;
        }

        if (args[0].equalsIgnoreCase("timezone")) {
            if (args.length < 2) {
                Utils.sendRaw(sender, "&cУкажите часовой пояс.");
                return true;
            }
            try {
                ZoneId zone = ZoneId.of(args[1]);
                config.set("settings.timezone", zone.toString());
                IRestartPlugin.getInstance().saveConfig();
                IRestartPlugin.getInstance().startTimer();
                Utils.sendRaw(sender, "&aЧасовой пояс: " + zone);
            } catch (Exception e) {
                Utils.sendRaw(sender, "&cНеверный часовой пояс!");
            }
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("help", "reload", "type", "time", "timezone", "now");
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("type")) return Arrays.asList("RESTART", "RELOAD");
            if (args[0].equalsIgnoreCase("time")) return Collections.singletonList("set");
            if (args[0].equalsIgnoreCase("timezone")) return IRestartPlugin.getInstance().getConfig().getStringList("timezones");
        }
        return new ArrayList<>();
    }
}