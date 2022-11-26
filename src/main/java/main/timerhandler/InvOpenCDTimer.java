package main.timerhandler;

import main.Main;
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
        try {
            if (!invOpenCooldoawn.isEmpty()) {
                for (Map.Entry<Player, Integer> entry : invOpenCooldoawn.entrySet()) {
                    invOpenCooldoawn.put(entry.getKey(), entry.getValue() - 1);
                    if (entry.getValue() <= 0) {
                        invOpenCooldoawn.remove(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
