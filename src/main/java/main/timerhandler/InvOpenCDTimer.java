package main.timerhandler;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class InvOpenCDTimer implements Runnable {
    private static final HashMap<Player, Integer> invOpenCooldoawn = new HashMap<>();

    public static HashMap<Player, Integer> getInvOpenCooldoawn() {
        return invOpenCooldoawn;
    }

    @Override
    public void run() {
        if (!invOpenCooldoawn.isEmpty()) {
            for (Map.Entry<Player, Integer> entry : invOpenCooldoawn.entrySet()) {
                invOpenCooldoawn.put(entry.getKey(), entry.getValue() - 1);
                if (entry.getValue() <= 0) {
                    invOpenCooldoawn.remove(entry.getKey());
                }
            }
        }
    }
}
