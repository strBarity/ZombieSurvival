package main.cmdhandler;

import main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDHandler implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            switch (s) {
                case "items", "zombiesurvival:items" -> ItemHandler.onCommand(sender);
                case "game", "zombiesurvival:game" -> GameTHandler.onCommand(sender, args);
            }
            return false;
        } catch (Exception e) {
            Main.printException(e);
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            return null;
        } catch (Exception e) {
            Main.printException(e);
            return null;
        }
    }
}
