package main.gamehandler;

import main.Main;
import main.timerhandler.OxygenTimer;
import main.timerhandler.WaveTimer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameHandler {
    public enum Gamemode { NORMAL, HOST, HARD, IMPOSSIBLE }
    public static Gamemode currentMode = null;
    public static boolean gameStarted = false;
    public static boolean beaconAlive = true;
    public static boolean subBeaconAlive = false;
    public static boolean oxygenStarted = false;
    public static int humanCount = 0;
    public static int infectCount = 0;
    public static int zombieToSpawn = 0;
    public static int zombieCount = 0;
    public static int beaconPower = 0;
    public static int subBeaconPower = 0;
    public static int beaconDurability = 0;
    public static int subBeaconDurability = 0;
    public static int wave = 0;
    public static int finalWave = 0;
    public static int oxygenDecreaseForce = 1;
    public static double zombieDamageMult = 1;
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
            currentMode = gamemode;
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            World world = Objects.requireNonNull(Bukkit.getWorld("world"));
            WaveTimer.firstWaveCountdown();
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
            wave = 0;
            oxygenDecreaseForce = 1;
            zombieToSpawn = 0;
            world.getBlockAt(252, 70, 208).setType(Material.BEACON);
            if (gamemode.equals(Gamemode.NORMAL)) {
                for (Player p : players) {
                    OxygenTimer.getOxygen().put(p, 100);
                    p.sendMessage("§a§l게임이 시작되었습니다. §a(일반 모드)");
                    p.showTitle(Title.title(Component.text("§c게임 시작"), Component.text("§e30초 후 1 웨이브 시작"), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(8), Duration.ofSeconds(1))));
                    human.addEntity(p);
                    p.playSound(Sound.sound(Key.key("minecraft:entity.wither.spawn"), Sound.Source.MASTER, 1, 0.5F));
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
                    inv.setItem(39, Main.D_HELMET);
                    inv.setItem(38, Main.D_CHESTPLATE);
                    inv.setItem(37, Main.D_LEGGINGS);
                    inv.setItem(36, Main.D_BOOTS);
                    p.teleport(new Location(world, 241.5 - 1 + (Math.random() * 3), 70, 209.5 - 1 + (Math.random() * 3)));
                } Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), GameHandler::nextWave, 600L);
            } else if (gamemode.equals(Gamemode.HOST)) {
                finalWave = Bukkit.getOnlinePlayers().size() * ((int) Math.round(1 + Math.random()));
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static void nextWave() {
        WaveTimer.resetWaveCountdown();
        wave++;
        for (Player p : Bukkit.getOnlinePlayers()) {
            zombieToSpawn = (int) Math.round(wave * 2 + (Math.random() * 10 + wave)) + 10;
            switch (wave) {
                case 50 -> {
                    oxygenStarted = true;
                    Bukkit.broadcast(Component.text("§c...공기가 나빠지기 시작했습니다. 정화기 근처에 머물지 않을 시\n§c산소가 점점 줄어들며 산소가 0 이하가 될 시\n§c빠르게 체력이 줄어들게 됩니다."));
                } case 60 -> {
                    Bukkit.broadcast(Component.text("§4§o...공기가 점점 더 나빠지고 있습니다... §7(산소 감소량 2배)"));
                    oxygenDecreaseForce = 2;
                } case 70 -> {
                    Bukkit.broadcast(Component.text("§4§o...공기가 점점 더 나빠지고 있습니다... §7(산소 감소량 3배)"));
                    oxygenDecreaseForce = 3;
                } case 80 -> {
                    Bukkit.broadcast(Component.text("§4§o...공기가 점점 더 나빠지고 있습니다... §7(산소 감소량 5배)"));
                    oxygenDecreaseForce = 5;
                } case 95 -> {
                    Bukkit.broadcast(Component.text("§9§o...공기는 점차 좋아지고 있습니다... §7(산소 감소량 5배 -> 1배)"));
                    oxygenDecreaseForce = 1;
                } case 96 -> {
                    Bukkit.broadcast(Component.text("§9§o...구름이 걷히고, 좀비들의 힘이 점차 약해지고 있습니다... §7(모든 좀비 공격력 §a-25%§7)"));
                    zombieDamageMult = 0.75;
                } case 97 -> {
                    Bukkit.broadcast(Component.text("§2§o...지평선 끝자락에서 빛이 보이기 시작합니다... §7(모든 좀비 공격력 §a-50%§7)"));
                    zombieDamageMult = 0.5;
                } case 98 -> {
                    Bukkit.broadcast(Component.text("§2§o...해가 뜨기 시작하고 있습니다... §7(모든 좀비 공격력 §a-75%§7)"));
                    zombieDamageMult = 0.25;
                } case 99 -> {
                    Bukkit.broadcast(Component.text("§2§o...우리들의 승리인 듯 합니다... §7(모든 좀비 공격력 §a-99%§7)"));
                    zombieDamageMult = 0.01;
                } case 100 -> {
                    Bukkit.broadcast(Component.text("§a...해가 완전히 떴고 좀비들이 불타 없어지기 시작했습니다."));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> Bukkit.broadcast(Component.text("§a...이제, 이 전쟁을 끝낼 때가 왔습니다.")), 100);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> Bukkit.broadcast(Component.text("§a...§e연구소§a를 찾아가, 태양에 의해 드러난 §4바이러스의 근원§a을 처치하세요.")), 200);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> Bukkit.broadcast(Component.text("§a...그럼, 우리들의 승리입니다.")), 300);
                }
            } p.showTitle(Title.title(Component.text("§c§l웨이브 " + wave), Component.text("§4습격 시작"), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(1))));
            p.playSound(Sound.sound(Key.key("minecraft:entity.wither.spawn"), Sound.Source.MASTER, 1, 0.75F));
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
