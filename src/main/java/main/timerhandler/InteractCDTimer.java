package main.timerhandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class InteractCDTimer implements Runnable {
    private static final HashMap<Player, Integer> interactCooldown = new HashMap<>();

    public static HashMap<Player, Integer> getInteractCooldown() {
        return interactCooldown;
    }

    @Override
    public void run() {
        try {
            if (!interactCooldown.isEmpty()) {
                for (Map.Entry<Player, Integer> entry : interactCooldown.entrySet()) {
                    interactCooldown.put(entry.getKey(), entry.getValue() - 1);
                    if (entry.getValue() <= 0) {
                        interactCooldown.remove(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
