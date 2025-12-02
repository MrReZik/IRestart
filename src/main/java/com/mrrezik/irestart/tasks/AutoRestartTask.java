package com.mrrezik.irestart.tasks;

import com.mrrezik.irestart.IRestartPlugin;
import com.mrrezik.irestart.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AutoRestartTask extends BukkitRunnable {

    private final IRestartPlugin plugin = IRestartPlugin.getInstance();

    @Override
    public void run() {
        FileConfiguration config = plugin.getConfig();
        String timeStr = config.getString("settings.time", "00:00");
        String zoneStr = config.getString("settings.timezone", "UTC");

        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(zoneStr);
        } catch (Exception e) {
            zoneId = ZoneId.systemDefault();
        }

        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // Защита от NullPointerException если timeStr придет null
        if (timeStr == null) timeStr = "00:00";
        String[] parts = timeStr.split(":");
        if (parts.length != 2) return;

        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        ZonedDateTime target = now.withHour(h).withMinute(m).withSecond(0).withNano(0);
        long secondsDiff = ChronoUnit.SECONDS.between(now, target);

        // --- ВЫПОЛНЕНИЕ РЕСТАРТА ---
        if (now.getHour() == h && now.getMinute() == m && now.getSecond() == 0) {
            executeRestart();
            return;
        }

        // --- УВЕДОМЛЕНИЯ ---
        if (secondsDiff > 0 && secondsDiff <= 600) {
            if (config.getIntegerList("warnings.times").contains((int) secondsDiff)) {
                sendAlerts((int) secondsDiff);
            }

            // Actionbar
            if (config.getBoolean("settings.actionbar-timer") && secondsDiff <= 60) {
                String msg = Utils.color("&cРестарт через: &e" + secondsDiff + " сек");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                }
            }
        }
    }

    @SuppressWarnings("deprecation") // Игнорируем устаревание sendTitle для совместимости с Spigot API 1.17
    private void sendAlerts(int seconds) {
        FileConfiguration cfg = plugin.getConfig();

        // Звуки
        String soundStr = cfg.getString("sounds.countdown", "BLOCK_NOTE_BLOCK_PLING, 1.0, 2.0");
        if (soundStr != null && !soundStr.equalsIgnoreCase("NONE")) {
            try {
                String[] sArgs = soundStr.split(",");
                if (sArgs.length >= 3) {
                    Sound sound = Sound.valueOf(sArgs[0].trim());
                    float vol = Float.parseFloat(sArgs[1].trim());
                    float pitch = Float.parseFloat(sArgs[2].trim());
                    for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), sound, vol, pitch);
                }
            } catch (Exception ignored) {}
        }

        // Title (Заголовки)
        if (cfg.getBoolean("warnings.title.enabled")) {
            String title = Utils.color(cfg.getString("warnings.title.text", "").replace("%time%", String.valueOf(seconds)));
            String sub = Utils.color(cfg.getString("warnings.title.subtext", "").replace("%time%", String.valueOf(seconds)));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(title, sub,
                        cfg.getInt("warnings.title.fade-in", 10),
                        cfg.getInt("warnings.title.stay", 40),
                        cfg.getInt("warnings.title.fade-out", 10));
            }
        }

        // Чат
        if (cfg.getBoolean("warnings.chat.enabled")) {
            List<String> lines = cfg.getStringList("warnings.chat.message");
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (String line : lines) {
                    p.sendMessage(Utils.color(line.replace("%time%", String.valueOf(seconds))));
                }
            }
        }
    }

    @SuppressWarnings("deprecation") // kickPlayer устарел в новых версиях, но в 1.17.1 это рабочий метод
    public void executeRestart() {
        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("settings.type", "RESTART");
        if (type == null) type = "RESTART";

        // Команды перед рестартом (PvP Untag и прочее)
        List<String> preCmds = cfg.getStringList("actions.pre-restart-commands");
        for (String cmd : preCmds) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }

        if (type.equalsIgnoreCase("RELOAD")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cfg.getString("actions.reload-cmd", "reload confirm"));
        } else {
            if (cfg.getBoolean("actions.kick.enabled")) {
                String kickMsg = Utils.color(cfg.getString("actions.kick.message", "Server Restarting...")
                        .replace("%time%", cfg.getString("settings.time", "now")));

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.kickPlayer(kickMsg);
                }
            }
            Bukkit.shutdown();
        }
    }
}