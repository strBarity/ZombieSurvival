package main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static final ConsoleCommandSender LOGGER = Bukkit.getConsoleSender();
    @Override
    public void onEnable() {
        LOGGER.sendMessage("§4[§2ZombieSurvival§4] §a플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        LOGGER.sendMessage("§4[§2ZombieSurvival§4] §c플러그인이 비활성화되었습니다.");
    }
}
