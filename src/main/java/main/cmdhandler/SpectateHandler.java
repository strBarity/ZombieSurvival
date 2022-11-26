package main.cmdhandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SpectateHandler {
    public static final HashMap<Player, Boolean> spectate = new HashMap<>();
    public static void onCommand(CommandSender commandSender, String[] args) {
        Player p = (Player) commandSender;
        if (spectate.get(p) == null || !spectate.get(p)) {
            spectate.put(p, true);
            p.sendMessage("§a당신은 이제 관전자입니다.");
        } else {
            spectate.put(p, false);
            p.sendMessage("§e당신은 더 이상 관전자가 아닙니다.");
        }
    }
}
