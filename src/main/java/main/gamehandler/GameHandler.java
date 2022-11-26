package main.gamehandler;

import main.Main;
import main.cmdhandler.SpectateHandler;
import main.eventhandler.EventListener;
import main.parsehandler.PlayerParser;
import main.parsehandler.ZombieParser;
import main.timerhandler.OxygenTimer;
import main.timerhandler.WaveTimer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class GameHandler {
    public enum Gamemode { NORMAL, HOST, HARD, IMPOSSIBLE }
    public enum PlayerType { SURVIVE, INFECTED, SPECTATOR }
    public static Gamemode currentMode = null;
    public static boolean gameStarted = false;
    public static boolean beaconAlive = true;
    public static boolean subBeaconAlive = false;
    public static boolean oxygenStarted = false;
    public static boolean subBeaconRevived = false;
    public static int spwanTaskId = 0;
    public static int humanCount = 0;
    public static int infectCount = 0;
    public static int zombieToSpawn = 0;
    public static int zombieCount = 0;
    public static int remainingZombies = 0;
    public static int beaconPower = 0;
    public static int subBeaconPower = 0;
    public static int beaconDurability = 0;
    public static int subBeaconDurability = 0;
    public static int wave = 0;
    public static int finalWave = 0;
    public static double oxygenDecreaseForce = 1;
    public static double zombieDamageMult = 1;
    public static final HashMap<Player, PlayerType> playerType = new HashMap<>();
    public static final HashMap<Player, BossBar> bossbar = new HashMap<>();

    /**
     * 게임을 시작함
     * @param gamemode 설정할 게임모드
     */
    public static void startGame(Gamemode gamemode) {
        try {
            clearMobs();
            currentMode = gamemode;
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            World world = Bukkit.getWorld("world");
            if (world != null && wave < 95) world.setFullTime(14000);
            WaveTimer.firstWaveCountdown();
            startZombieSpawn();
            gameStarted = true;
            beaconAlive = true;
            subBeaconAlive = false;
            humanCount = 0;
            infectCount = 0;
            zombieCount = 0;
            beaconPower = 100;
            subBeaconPower = 0;
            beaconDurability = 50;
            subBeaconDurability = 0;
            wave = 0;
            oxygenDecreaseForce = 1;
            zombieToSpawn = 0;
            remainingZombies = 0;
            zombieDamageMult = 1;
            subBeaconRevived = false;
            if (world != null) world.getBlockAt(252, 70, 208).setType(Material.BEACON);
            if (gamemode.equals(Gamemode.NORMAL)) {
                for (Player p : players) {
                    if (SpectateHandler.spectate.get(p) == null || !SpectateHandler.spectate.get(p)) {
                        humanCount++;
                        playerType.put(p, PlayerType.SURVIVE);
                        OxygenTimer.getOxygen().put(p, 100.0);
                        EventListener.getInfiniteFull().put(p, false);
                        p.setTotalExperience(0);
                        p.sendMessage("§a§l게임이 시작되었습니다. §a(일반 모드)");
                        Main.title(p, "§c게임 시작", "§e30초 후 1 웨이브 시작", 1, 8, 1);
                        p.playSound(Sound.sound(Key.key("minecraft:entity.wither.spawn"), Sound.Source.MASTER, 1, 0.5F));
                        p.getInventory().clear();
                        p.setGameMode(GameMode.SURVIVAL);
                        AttributeInstance a = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (a != null) a.setBaseValue(20);
                        p.setHealthScale(20);
                        p.setHealth(p.getHealthScale());
                        p.setFoodLevel(20);
                        for (PotionEffect effect : p.getActivePotionEffects()) {
                            p.removePotionEffect(effect.getType());
                        } p.setGlowing(true);
                        p.setSaturation(10);
                        p.closeInventory();
                        p.setNoDamageTicks(20);
                        p.setMaximumNoDamageTicks(20);
                        PlayerInventory inv = p.getInventory();
                        inv.setItem(0, Main.D_SWORD);
                        inv.setItem(1, Main.D_BOW);
                        inv.setItem(2, Main.GOLDEN_APPLE);
                        inv.setItem(8, Main.SIMPLE_TABLE);
                        inv.setItem(27, Main.ARROW);
                        inv.setItem(39, Main.D_HELMET);
                        inv.setItem(38, Main.D_CHESTPLATE);
                        inv.setItem(37, Main.D_LEGGINGS);
                        inv.setItem(36, Main.D_BOOTS);
                        p.teleport(new Location(world, 241.5 - 1 + (Math.random() * 2), 70, 209.5 - 1 + (Math.random() * 2)));
                    } else {
                        playerType.put(p, PlayerType.SPECTATOR);
                        p.setGameMode(GameMode.SPECTATOR);
                        p.teleport(new Location(world, 241.5 - 1 + (Math.random() * 2), 80, 209.5 - 1 + (Math.random() * 2)));
                    }
                } Main.delay(() -> {
                    if (gameStarted && wave == 0) nextWave();
                }, 600);
            } else if (gamemode.equals(Gamemode.HOST)) {
                finalWave = Bukkit.getOnlinePlayers().size() * ((int) Math.round(1 + Math.random()));
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
    public static ItemStack leatherColor(ItemStack leatherarmor, Color color) {
        LeatherArmorMeta l = (LeatherArmorMeta) leatherarmor.getItemMeta();
        l.setColor(color);
        l.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
        leatherarmor.setItemMeta(l);
        return leatherarmor;
    }
    public static void setLeathers(LivingEntity z, Color c) {
        if (z.getEquipment() == null) return;
        z.getEquipment().setHelmet(leatherColor(new ItemStack(Material.LEATHER_HELMET), c));
        z.getEquipment().setChestplate(leatherColor(new ItemStack(Material.LEATHER_CHESTPLATE), c));
        z.getEquipment().setLeggings(leatherColor(new ItemStack(Material.LEATHER_LEGGINGS),c));
        z.getEquipment().setBoots(leatherColor(new ItemStack(Material.LEATHER_BOOTS), c));
    }

    /**
     * 다음 웨이브를 시작함. 좀비 누적 스폰 갯수를 초기화하고 좀비 스폰 상한선을 설정함,
     * 상한선의 식은 다음과 같이 작동. '웨이브 + (([0-1 랜덤 숫자] x 10) + 웨이브) + 10'
     */
    public static void nextWave() {
        World w = Bukkit.getWorld("world");
        if (wave < 99) {
            if (w != null && wave < 95) w.setFullTime(14000 + (wave * 75L));
            zombieCount = 0;
            WaveTimer.resetWaveCountdown();
            wave++;
            zombieToSpawn = (int) Math.round(wave + (Math.random() * 10 + wave)) + 10;
            remainingZombies = zombieToSpawn;
            if (wave % 3 == 0) {
                double random = Math.random();
                Zombie z = (Zombie) ZombieParser.spawnRandom(ZombieParser.ZombieType.ZOMBIE);
                if (random <= (1.0 / 3)) {
                    if (z != null) {
                        setLeathers(z, Color.RED);
                        z.customName(Component.text("§c파괴의 좀비"));
                        z.getEquipment().setItemInMainHand(Main.ZOMBIE_POWER);
                        z.setGlowing(true);
                        Bukkit.broadcast(Component.text("§c파괴의 좀비§4가 어딘가에 스폰되었습니다."));
                    }
                } else if (random <= (2.0 / 3)) {
                    if (z != null) {
                        setLeathers(z, Color.BLUE);
                        z.customName(Component.text("§b정화의 좀비"));
                        z.getEquipment().setItemInMainHand(Main.ZOMBIE_POWER);
                        z.setGlowing(true);
                        Bukkit.broadcast(Component.text("§b정화의 좀비§4가 어딘가에 스폰되었습니다."));
                    }
                } else {
                    if (z != null) {
                        setLeathers(z, Color.WHITE);
                        z.customName(Component.text("§d창조의 좀비"));
                        z.getEquipment().setItemInMainHand(Main.ZOMBIE_POWER);
                        z.setGlowing(true);
                        Bukkit.broadcast(Component.text("§d창조의 좀비§4가 어딘가에 스폰되었습니다."));
                    }
                }
            }
            switch (wave) {
                case 50 -> {
                    oxygenStarted = true;
                    Bukkit.broadcast(Component.text("§c...공기가 나빠지기 시작했습니다. 정화기 근처에 머물지 않을 시\n§c산소가 점점 줄어들며 산소가 0 이하가 될 시\n§c빠르게 체력이 줄어들게 됩니다."));
                }
                case 60 -> {
                    Bukkit.broadcast(Component.text("§4§o...공기가 점점 더 나빠지고 있습니다... §7(산소 감소량 1.5배)"));
                    oxygenDecreaseForce = 1.5;
                }
                case 70 -> {
                    Bukkit.broadcast(Component.text("§4§o...공기가 점점 더 나빠지고 있습니다... §7(산소 감소량 2배)"));
                    oxygenDecreaseForce = 2;
                }
                case 80 -> {
                    Bukkit.broadcast(Component.text("§4§o...공기가 점점 더 나빠지고 있습니다... §7(산소 감소량 3배)"));
                    oxygenDecreaseForce = 3;
                }
                case 95 -> {
                    Bukkit.broadcast(Component.text("§9§o...공기는 점차 좋아지고 있습니다... §7(산소 감소량 3배 -> 1배)"));
                    oxygenDecreaseForce = 1;
                }
                case 96 -> {
                    Bukkit.broadcast(Component.text("§9§o...달빛이 약해지며, 좀비들의 힘도 점차 약해지고 있습니다... §7(모든 좀비 공격력 §a-25%§7)"));
                    zombieDamageMult = 0.75;
                    if (w != null) w.setFullTime(22000);
                }
                case 97 -> {
                    Bukkit.broadcast(Component.text("§2§o...지평선 끝자락에서 빛이 보이기 시작합니다... §7(모든 좀비 공격력 §a-50%§7)"));
                    zombieDamageMult = 0.5;
                    if (w != null) w.setFullTime(22500);
                }
                case 98 -> {
                    Bukkit.broadcast(Component.text("§2§o...해가 뜨기 시작하고 있습니다... §7(모든 좀비 공격력 §a-75%§7)"));
                    zombieDamageMult = 0.25;
                    if (w != null) w.setFullTime(23000);
                }
                case 99 -> {
                    Bukkit.broadcast(Component.text("§2§o...우리들의 승리인 듯 합니다... §7(모든 좀비 공격력 §a-99%§7)"));
                    zombieDamageMult = 0.01;
                    if (w != null) w.setFullTime(23500);
                }
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                Main.title(p, "§c§l웨이브 " + wave, "§4습격 시작", 1, 4, 1);
                p.playSound(Sound.sound(Key.key("minecraft:entity.wither.spawn"), Sound.Source.MASTER, 1, 0.75F));
            }
        } else {
            wave++;
            if (w != null) {
                w.setFullTime(24000);
                for (LivingEntity e : w.getLivingEntities()) {
                    if (ZombieParser.isZombie(e)) e.setFireTicks(Integer.MAX_VALUE);
                    if (e instanceof Drowned) e.remove();
                }
            } Bukkit.broadcast(Component.text("§a...해가 완전히 떴고 좀비들이 불타 없어지기 시작했습니다."));
            Main.delay(() -> Bukkit.broadcast(Component.text("§a...이제, 이 전쟁을 끝낼 때가 왔습니다.")), 100);
            Main.delay(() -> Bukkit.broadcast(Component.text("§a...§e연구소§a를 찾아가, 태양에 의해 드러난 §4바이러스의 근원§a을 처치하세요.")), 200);
            Main.delay(() -> Bukkit.broadcast(Component.text("§a...그럼, 우리들의 승리입니다.")), 300);
        }
    }

    /**
     * 좀비 생성 작업을 시작함. 1틱(0.05초)당 20% 확률로 좀비가 생성 상한에 도달할 때까지
     * 3가지 랜덤한 좀비 타입으로 랜덤한 위치에 생성됨.
     */
    public static void startZombieSpawn() {
        spwanTaskId = Main.repeat(() -> {
            try {
                if (wave > 0 && wave < 100 && zombieCount < zombieToSpawn && Math.random() <= 0.2) {
                    zombieCount++;
                    final double zombieTypeR = Math.random();
                    if (zombieTypeR < 0.5) {
                        Main.delay(() -> {
                            if (gameStarted) ZombieParser.spawnRandom(ZombieParser.ZombieType.ZOMBIE);
                        }, 200);
                    } else if (zombieTypeR < 0.85) {
                        Main.delay(() -> {
                            if (gameStarted) ZombieParser.spawnRandom(ZombieParser.ZombieType.HUSK);
                        }, 200);
                    } else {
                        Main.delay(() -> {
                            if (gameStarted) ZombieParser.spawnRandom(ZombieParser.ZombieType.DROWNED);
                        }, 200);
                    }
                }
                World w = Bukkit.getWorld("world");
                if (w != null) {
                    for (Entity e : w.getEntities()) {
                        if (ZombieParser.isZombie(e)) {
                            switch (e.getType()) {
                                case ZOMBIE -> ((Zombie) e).setTarget(PlayerParser.getNearestPlayer(e));
                                case HUSK -> ((Husk) e).setTarget(PlayerParser.getNearestPlayer(e));
                                case DROWNED -> ((Drowned) e).setTarget(PlayerParser.getNearestPlayer(e));
                                case ZOMBIE_VILLAGER -> ((ZombieVillager) e).setTarget(PlayerParser.getNearestPlayer(e));
                                default -> throw new IllegalStateException("올바르지 않은 좀비가 좀비 타입에 대입되었습니다");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Main.printException(e);
            }
        }, 1);
    }

    /**
     * 게임을 즉시 중지하며 시작 전 상태로 되돌림
     */
    public static void stopGame() {
        try {
            gameStarted = false;
            Bukkit.getScheduler().cancelTask(spwanTaskId);
            clearMobs();
            World w = Bukkit.getWorld("world");
            if (w != null) {
                w.getEntities().stream().filter(Item.class::isInstance).forEach(Entity::remove);
                w.setFullTime(14000);
            } for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(new Location(p.getWorld(), 240.5, 224.0, 208.5));
                p.getInventory().clear();
                EventListener.getInfiniteFull().put(p, false);
                if (bossbar.get(p) != null) p.hideBossBar(bossbar.get(p));
                p.setGlowing(false);
                p.setGameMode(GameMode.SURVIVAL);
                p.setHealth(p.getHealthScale());
                p.setFoodLevel(20);
                p.setSaturation(0);
                AttributeInstance a = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (a != null) a.setBaseValue(20);
                p.closeInventory();
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                } p.setHealthScale(20);
                p.sendMessage("§c게임이 중지되었습니다.");
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    /**
     * 게임에 사용됐던 몹 제거 (좀비, 잡몹 등)
     */
    public static void clearMobs() {
        try {
            World w = Bukkit.getWorld("world");
            if (w != null) {
                for (Entity e : w.getEntities()) {
                    EntityType t = e.getType();
                    List<EntityType> toRemove = Arrays.asList(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER, EntityType.SNOWMAN, EntityType.IRON_GOLEM);
                    if (toRemove.contains(t)) e.remove();
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
