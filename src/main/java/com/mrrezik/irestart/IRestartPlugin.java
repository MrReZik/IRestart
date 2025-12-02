package com.mrrezik.irestart;

import com.mrrezik.irestart.commands.MainCommand;
import com.mrrezik.irestart.tasks.AutoRestartTask;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class IRestartPlugin extends JavaPlugin {

    private static IRestartPlugin instance;
    private AutoRestartTask restartTask;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Исправлен NPE Warning: проверяем, существует ли команда перед регистрацией
        PluginCommand cmd = getCommand("irestart");
        if (cmd != null) {
            MainCommand mainCommand = new MainCommand();
            cmd.setExecutor(mainCommand);
            cmd.setTabCompleter(mainCommand);
        } else {
            getLogger().severe("Команда /irestart не найдена в plugin.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        startTimer();
        getLogger().info("iRestart v" + getDescription().getVersion() + " загружен!");
    }

    @Override
    public void onDisable() {
        if (restartTask != null && !restartTask.isCancelled()) {
            restartTask.cancel();
        }
    }

    public void startTimer() {
        if (restartTask != null && !restartTask.isCancelled()) {
            restartTask.cancel();
        }
        restartTask = new AutoRestartTask();
        restartTask.runTaskTimer(this, 20L, 20L);
    }

    public static IRestartPlugin getInstance() {
        return instance;
    }
}