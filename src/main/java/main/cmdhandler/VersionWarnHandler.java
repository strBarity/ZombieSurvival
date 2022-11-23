package main.cmdhandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public class VersionWarnHandler {
    public static void onCommand(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player p) {
            p.sendMessage("§4해당 명령어는 사용이 금지되었습니다. §c(CONSOLE_ONLY)");
        } else if (commandSender instanceof ConsoleCommandSender) {
            Player p = Bukkit.getPlayer(args[0]);
            if (args.length == 2 && p != null) {
                Bukkit.getLogger().warning(String.format("플레이어 %s은(는) %s버전으로 서버에 접속했습니다.", args[0], args[1]));
                p.showTitle(Title.title(Component.text("§6⚠ §l주의!!! §6⚠"), Component.text("§4호환되지 않는 버전 사용 중!"), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(10), Duration.ofSeconds(1))));
            }
        }
    }
}
