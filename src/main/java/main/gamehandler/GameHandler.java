package main.gamehandler;

import main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameHandler {
    public enum Gamemode { NORMAL, HOST, HARD, IMPOSSIBLE }
    public static boolean gameStarted = false;
    public static boolean beaconAlive = true;
    public static boolean subBeaconAlive = false;
    public static int humanCount = 0;
    public static int infectCount = 0;
    public static int zombieCount = 0;
    public static int beaconPower = 0;
    public static int subBeaconPower = 0;
    public static int beaconDurability = 0;
    public static int subBeaconDurability = 0;
    public static int wave = 0;
    public static int finalWave = 0;
    public static final Team human; static {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("human");
        if (team == null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("human");
            team.color(NamedTextColor.AQUA);
            team.displayName(Component.text("§b생존자"));
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        } human = team;
    }
    public static final Team zombie; static {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("zombie");
        if (team == null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("zombie");
            team.color(NamedTextColor.GREEN);
            team.displayName(Component.text("§2좀비"));
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        } zombie = team;
    }


    public static void startGame(Gamemode gamemode) {
        try {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            World world = Objects.requireNonNull(Bukkit.getWorld("world"));
            gameStarted = true;
            beaconAlive = true;
            subBeaconAlive = false;
            humanCount = Bukkit.getOnlinePlayers().size();
            infectCount = 0;
            zombieCount = 0;
            beaconPower = 0;
            subBeaconPower = 0;
            beaconDurability = 50;
            subBeaconDurability = 0;
            wave = 1;
            world.getBlockAt(252, 70, 208).setType(Material.BEACON);
            if (gamemode.equals(Gamemode.NORMAL)) {
                for (Player p : players) {
                    p.sendMessage("§a§l게임이 시작되었습니다. §a(일반 모드)");
                    human.addEntity(p);
                    p.getInventory().clear();
                    p.setGlowing(true);
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setHealth(p.getHealthScale());
                    p.setFoodLevel(20);
                    p.setSaturation(10);
                    p.closeInventory();
                    PlayerInventory inv = p.getInventory();
                    inv.setItem(0, Main.D_SWORD);
                    inv.setItem(1, Main.D_BOW);
                    inv.setItem(2, Main.GOLDEN_APPLE);
                    inv.setItem(36, Main.D_HELMET);
                    inv.setItem(37, Main.D_CHESTPLATE);
                    inv.setItem(38, Main.D_LEGGINGS);
                    inv.setItem(39, Main.D_BOOTS);
                    p.teleport(new Location(world, 241.5 + (Math.random() * 1.5), 70, 209.5 + (Math.random() * 1.5)));
                }
            } else if (gamemode.equals(Gamemode.HOST)) {
                finalWave = Bukkit.getOnlinePlayers().size() * ((int) Math.round(1 + Math.random()));
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
    public static void stopGame() {
        try {
            gameStarted = false;
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(new Location(p.getWorld(), 240.5, 224.0, 208.5));
                p.getInventory().clear();
                p.setGlowing(false);
                p.setGameMode(GameMode.SURVIVAL);
                p.setHealth(p.getHealthScale());
                p.setFoodLevel(20);
                p.setSaturation(0);
                p.closeInventory();
                p.sendMessage("§c게임이 중지되었습니다.");
                human.removeEntries(human.getEntries());
                zombie.removeEntries(zombie.getEntries());
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
