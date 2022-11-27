package main.parsehandler;

import main.gamehandler.GameHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PlayerParser {
    /**
     * 가장 가까운 플레이어를 찾는 메소드
     * @param center 중심이 될 엔티티
     * @return 중심이 될 엔티티로부터 가장 가까운 플레이어를 반환, 없을 시 null 반환
     */
    public static @Nullable Player getNearestPlayer(@NotNull Entity center) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return null;
        Location l = center.getLocation();
        double distance = Double.MAX_VALUE;
        Player nearest = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getGameMode().equals(GameMode.SPECTATOR) && GameHandler.playerType.get(p) != null && GameHandler.playerType.get(p).equals(GameHandler.PlayerType.SURVIVE) && l.distance(p.getLocation()) < distance) {
                nearest = p;
                distance = l.distance(p.getLocation());
            }
        } return nearest;
    }

    /**
     * 플레이어가 정화기 근처에 있는지 알아내는 메소드
     * @param player 정화기 근처에 있는지 알아낼 플레이어
     * @return 정화기로부터 5칸 내에 있으면 true 반환, 아니면 false 반환
     */
    public static boolean isNearPurifier(@NotNull Player player) {
        return Math.abs(252 - player.getLocation().getX()) <= 10 && Math.abs(208 - player.getLocation().getZ()) <= 10;
    }
}
