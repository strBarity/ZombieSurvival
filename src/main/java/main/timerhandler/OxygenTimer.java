package main.timerhandler;

import main.Main;
import main.gamehandler.GameHandler;
import main.parsehandler.PlayerParser;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class OxygenTimer implements Runnable {
    private static final HashMap<Player, Integer> oxygen = new HashMap<>();

    public static HashMap<Player, Integer> getOxygen() {
        return oxygen;
    }

    @Override
    public void run() {
        if (!oxygen.isEmpty() && GameHandler.gameStarted && GameHandler.oxygenStarted) {
            for (Map.Entry<Player, Integer> entry : oxygen.entrySet()) {
                Player p = entry.getKey();
                if (GameHandler.playerType.get(p).equals(GameHandler.PlayerType.SURVIVE)) {
                    if (PlayerParser.isNearPurifier(p)) {
                        if (oxygen.get(p) < 100) oxygen.put(p, entry.getValue() + 1);
                    }
                    else {
                        if (entry.getValue() <= 0) {
                            oxygen.put(p, 0);
                            Main.title(p, "§4⚠ §c§l산소 부족!", "§6정화기 근처에서 산소를 회복하세요.", 250.0, 500.0, 250.0);
                            if (Math.random() <= 0.5) p.damage(5);
                        } else oxygen.put(p, entry.getValue() - GameHandler.oxygenDecreaseForce);
                        if (entry.getValue() == 50) {
                            Main.title(p, "§f⚠ §e산소 50 남음!", "§6정화기 근처에서 산소를 회복하세요.", 1, 3, 1);
                        } else if (entry.getValue() == 25) {
                            Main.title(p, "§e⚠ §6산소 25 남음!", "§6정화기 근처에서 산소를 회복하세요.", 1, 5, 1);
                        } else if (entry.getValue() == 10) {
                            Main.title(p, "§6⚠ §c산소 10 남음!", "§6정화기 근처에서 산소를 회복하세요.", 1, 5, 1);
                        }
                    } String Oxygen;
                    BossBar.Color color;
                    float o = oxygen.get(p);
                    if (o == 100) {
                        Oxygen = "§9100";
                        color = BossBar.Color.BLUE;
                    } else if (o > 75) {
                        Oxygen = "§a" + Math.round(o);
                        color = BossBar.Color.GREEN;
                    } else if (o > 50) {
                        Oxygen = "§e" + Math.round(o);
                        color = BossBar.Color.GREEN;
                    } else if (o > 25) {
                        Oxygen = "§6" + Math.round(o);
                        color = BossBar.Color.YELLOW;
                    } else if (o > 10) {
                        Oxygen = "§c" + Math.round(o);
                        color = BossBar.Color.RED;
                    } else {
                        Oxygen = "§4" + Math.round(o);
                        color = BossBar.Color.RED;
                    } BossBar bar = BossBar.bossBar(Component.text("§d남은 산소: " + Oxygen), o / 100, color, BossBar.Overlay.PROGRESS);
                    if (GameHandler.bossbar.get(p) != null) p.hideBossBar(GameHandler.bossbar.get(p));
                    p.showBossBar(bar);
                    GameHandler.bossbar.put(p, bar);
                } else oxygen.put(p, 100);
            }
        }
    }
}
