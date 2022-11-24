package main.parsehandler;

import main.gamehandler.GameHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PlayerParser {
    public static @Nullable Player getNearestPlayer(@NotNull Entity center) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return null;
        Location l = center.getLocation();
        Player nearest = null;
        double distance = Double.MAX_VALUE;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (GameHandler.playerType.get(p).equals(GameHandler.PlayerType.SURVIVE) && l.distance(p.getLocation()) < distance) {
                nearest = p;
                distance = l.distance(p.getLocation());
            }
        } return nearest;
    }
}
