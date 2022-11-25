package main.cmdhandler;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class VersionWarnHandler {
    /**
     * 특정 플레이어에게 버전 경고 메시지를 보냄,
     * 오직 콘솔에서만 사용 가능, 플레이어 사용 불가
     */
    public static void onCommand(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player p) {
            p.sendMessage("§4해당 명령어는 사용이 금지되었습니다. §c(CONSOLE_ONLY)");
        } else if (commandSender instanceof ConsoleCommandSender) {
            Player p = Bukkit.getPlayer(args[0]);
            if (args.length == 2 && p != null) {
                Bukkit.getLogger().warning(String.format("플레이어 %s은(는) %s버전으로 서버에 접속했습니다.", args[0], args[1]));
                Main.title(p, "§6⚠ §l주의!!! §6⚠", "§4호환되지 않는 버전 사용 중!", 0, 10, 1);
            }
        }
    }
}
