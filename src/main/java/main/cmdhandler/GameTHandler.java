package main.cmdhandler;

import main.gamehandler.GameHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameTHandler {
    /**
     * 게임의 전반적인 흐름을 설정함, OP가 있어야 사용 가능함.
     */
    public static void onCommand(CommandSender commandSender, String[] args) {
        Player p = (Player) commandSender;
        if (!p.isOp()) {
            p.sendMessage("§c이 명령어를 사용할 권한이 없습니다!");
            return;
        } String w = "§c올바르지 않은 사용법입니다!";
        if (args.length == 0) {
            p.sendMessage(w);
            return;
        } switch (args[0]) {
            case "start" -> {
                if (args.length == 1) {
                    p.sendMessage(w);
                    return;
                } switch (args[1]) {
                    case "normal" -> GameHandler.startGame(GameHandler.Gamemode.NORMAL);
                    case "host" -> GameHandler.startGame(GameHandler.Gamemode.HOST);
                    case "hard" -> GameHandler.startGame(GameHandler.Gamemode.HARD);
                    case "impossible" -> GameHandler.startGame(GameHandler.Gamemode.IMPOSSIBLE);
                    default -> p.sendMessage(w);
                }
            } case "stop" -> {
                Bukkit.broadcast(Component.text("§4관리자가 강제로 게임을 중지시켰습니다."));
                GameHandler.stopGame();
            }
        }
    }
}
