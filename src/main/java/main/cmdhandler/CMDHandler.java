package main.cmdhandler;

import main.Main;
import main.gamehandler.GameHandler;
import main.parsehandler.NumberParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CMDHandler implements TabExecutor {
    public static final List<String> blackList = new ArrayList<>(Arrays.asList("list", "me", "msg", "tell", "w", "seed", "teammsg", "tm", "trigger", "minecraft:list", "minecraft:me", "minecraft:msg", "minecraft:tell", "minecraft:w", "minecraft:seed", "minecraft:teammsg", "minecraft:tm", "minecraft:trigger"));
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            switch (s) {
                case "items", "zombiesurvival:items" -> {
                    ItemHandler.onCommand(sender);
                    return true;
                }
                case "game", "zombiesurvival:game" -> {
                    GameTHandler.onCommand(sender, args);
                    return true;
                } case "wave", "zombiesurvival:wave" -> {
                    if (args.length == 0 || NumberParser.isNotInt(args[0])) sender.sendMessage("§c올바르지 않은 사용법입니다!");
                    else {
                        GameHandler.wave = Integer.parseInt(args[0]);
                        sender.sendMessage(String.format("§a웨이브를 §e%s§a로 설정했습니다.", args[0]));
                    } return true;
                } case "vw" -> {
                    VersionWarnHandler.onCommand(sender, args);
                    return true;
                } case "spectate", "spec", "zombiesurvival:spectate", "zombiesurvival:spec" -> {
                    SpectateHandler.onCommand(sender);
                    return true;
                } case "craft", "zombiesurvival:craft" -> {
                    CraftHandler.onCommand(sender, args);
                    return true;
                }
                default -> {
                    return false;
                }
            }
        } catch (Exception e) {
            Main.printException(e);
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            switch (s) {
                case "items", "zombiesurvival:items" -> {
                    return List.of();
                } case "game", "zombiesurvival:game" -> {
                    if (args.length == 1) return Arrays.asList("start", "stop");
                    else if (args.length == 2) {
                        if (args[0].equals("start")) return Arrays.asList("normal", "host", "hard", "impossible");
                        else if (args[0].equals("stop")) return List.of();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Main.printException(e);
            return null;
        }
    }
}
