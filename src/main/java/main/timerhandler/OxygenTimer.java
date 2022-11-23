package main.timerhandler;

import main.gamehandler.GameHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
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
                oxygen.put(entry.getKey(), entry.getValue() - GameHandler.oxygenDecreaseForce);
                if (entry.getValue() == 50) {
                    entry.getKey().showTitle(Title.title(Component.text("§f⚠ §e산소 50 남음!"), Component.text("§6정화기 근처에서 산소를 회복하세요."), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))));
                } else if (entry.getValue() == 25) {
                    entry.getKey().showTitle(Title.title(Component.text("§e⚠ §6산소 25 남음!"), Component.text("§6정화기 근처에서 산소를 회복하세요."), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1))));
                } else if (entry.getValue() == 10) {
                    entry.getKey().showTitle(Title.title(Component.text("§6⚠ §c산소 10 남음!"), Component.text("§6정화기 근처에서 산소를 회복하세요."), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1))));
                } else if (entry.getValue() <= 0) {
                    entry.getKey().showTitle(Title.title(Component.text("§4⚠ §c§l산소 부족!"), Component.text("§6정화기 근처에서 산소를 회복하세요."), Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(500), Duration.ofMillis(250))));
                    if (Math.random() <= 0.5) entry.getKey().damage(Math.abs(entry.getValue()));
                }
            }
        }
    }
}
