package main.eventhandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import main.Main;
import main.cmdhandler.CMDHandler;
import main.gamehandler.GameHandler;
import main.parsehandler.BlockParser;
import main.parsehandler.ZombieParser;
import main.timerhandler.InteractCDTimer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static main.Main.*;
import static main.gamehandler.GameHandler.*;
import static org.bukkit.potion.PotionEffectType.*;

public class EventListener implements Listener {
    private static final HashMap<Player, Integer> taskId = new HashMap<>();
    private static final HashMap<Player, Integer> npcId = new HashMap<>();
    private static final HashMap<Player, Boolean> infiniteFull = new HashMap<>();

    public static HashMap<Player, Integer> getNpcId() {
        return npcId;
    }

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent e) {
        try {
            Player p = e.getPlayer();
            if (playerType.get(p).equals(PlayerType.SURVIVE)) {
                e.setCancelled(true);
                GameHandler.humanCount--;
                GameHandler.infectCount++;
                for (Player player : Bukkit.getOnlinePlayers())
                    player.playSound(Sound.sound(Key.key("minecraft:entity.zombie_villager.cure"), Sound.Source.MASTER, 0.5F, 1));
                playerType.put(p, PlayerType.INFECTED);
                p.setHealth(p.getHealthScale());
                p.setNoDamageTicks(0);
                p.setMaximumNoDamageTicks(0);
                if (wave != 100) {
                    Bukkit.broadcast(Component.text("§2☠ " + p.getName() + "§c님이 §4좀비가 되셨습니다."));
                    p.addPotionEffects(Arrays.asList(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false), new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false)));
                    title(p, "§2☠ §4좀비가 되셨습니다. §2☠", "§e사망 시 10초 후 다른 장소에서 다시 부활합니다.", 1, 8, 1);
                    for (ItemStack i : p.getInventory().getContents()) {
                        if (i != null) {
                            p.getWorld().dropItem(p.getLocation(), i);
                        }
                    }
                    p.getInventory().clear();
                } else {
                    Bukkit.broadcast(Component.text("§2☠ " + p.getName() + "§c님이 §4소멸하셨습니다."));
                    title(p, "§4소멸하셨습니다.", "§e더 이상 부활할 수 없습니다.", 1, 8, 1);
                } if (humanCount == 0) {
                    failGame();
                }
            } else if (playerType.get(p).equals(PlayerType.INFECTED)) {
                e.setCancelled(true);
                p.setHealth(p.getHealthScale());
                p.getInventory().clear();
                p.setGameMode(GameMode.SPECTATOR);
                p.setGlowing(false);
                if (wave != 100) {
                    title(p, "§c사망하셨습니다.", "§b10§e초 후 부활합니다", 0, 1, 0);
                    for (int i = 1; i < 10; i++) {
                        final int sec = 10 - i;
                        delay(() -> title(p, "§c사망하셨습니다.", "§b" + sec + "§e초 후 부활합니다", 0, 2, 0), i * 20);
                    }
                    delay(() -> {
                        List<Location> spawnLocation = spawnLoc;
                        Collections.shuffle(spawnLocation);
                        p.teleport(spawnLocation.get(1));
                        p.setGameMode(GameMode.SURVIVAL);
                        p.setGlowing(true);
                    }, 200);
                } else {
                    Bukkit.broadcast(Component.text("§2☠ " + p.getName() + "§c님이 §4소멸하셨습니다."));
                    title(p, "§c소멸하셨습니다.", "§4태양이 떴기 때문에 더 이상 부활할 수 없습니다.", 0, 1, 0);
                }
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onKill(EntityDeathEvent e) {
        try {
            if (e.getEntity() instanceof Chicken) {
                e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), ZOMBIE_CHICKEN);
                Player killer = e.getEntity().getKiller();
                if (killer != null) {
                    killer.sendMessage("§5§l희귀 전리품 드랍! §8(§4좀비 치킨 바베큐§8)");
                    killer.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 10, 2));
                }
            } if (ZombieParser.isZombie(e.getEntity())) {
                GameHandler.remainingZombies--;
                if (GameHandler.remainingZombies <= 0) GameHandler.nextWave();
                e.getDrops().clear();
                Location l = e.getEntity().getLocation();
                World w = l.getWorld();
                Component name = e.getEntity().customName();
                Player killer = e.getEntity().getKiller();
                if (Math.random() <= 0.9) w.dropItem(l, ZOMBIE_FLESH);
                if (Math.random() <= 0.3) {
                    ItemStack i = ZOMBIE_POWDER;
                    i.setAmount((int) Math.round(Math.random() * 3));
                    w.dropItem(l, i);
                }
                if (Math.random() <= 0.25) w.dropItem(l, POWER_CRYSTAL);
                if (Math.random() <= 0.03) {
                    w.dropItem(l, ZOMBIE_TRACE);
                    if (killer != null) {
                        killer.sendMessage("§5§l희귀 전리품 드랍! §8(§c좀비의 흔적§8) §5(3%)");
                        killer.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 10, 2));
                    }
                } if (Math.random() <= 0.015) {
                    w.dropItem(l, ZOMBIE_POWER);
                    if (killer != null) {
                        killer.sendMessage("§5§l희귀 전리품 드랍! §8(§e좀비의 힘§8) §d(1.5%)");
                        killer.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 10, 2));
                    }
                } if (e.getEntityType().equals(EntityType.ZOMBIE) && Math.random() <= 0.05) w.dropItem(l, ZOMBIE_APPLE_D);
                if (e.getEntityType().equals(EntityType.HUSK) && Math.random() <= 0.2) {
                    ItemStack i = ZOMBIE_SAND;
                    i.setAmount((int) Math.round(Math.random() * 10));
                    w.dropItem(l, i);
                }
                if (e.getEntityType().equals(EntityType.DROWNED) && Math.random() <= 0.25) {
                    ItemStack i = ZOMBIE_WATERDROP;
                    i.setAmount((int) Math.round(Math.random() * 9));
                    w.dropItem(l, i);
                }
                if (name != null && name.equals(Component.text("§c파괴의 좀비"))) {
                    w.dropItem(l, CORE_OF_DESTRUCTION);
                    Bukkit.broadcast(Component.text("§c파괴의 좀비§e가 사망했습니다."));
                    if (killer != null) {
                        killer.sendMessage("§5§l희귀 전리품 드랍! §8(§c파괴의 코어§8)");
                        killer.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 10, 2));
                    }
                } if (name != null && name.equals(Component.text("§b정화의 좀비"))) {
                    w.dropItem(l, CORE_OF_PURIFICATION);
                    Bukkit.broadcast(Component.text("§b정화의 좀비§e가 사망했습니다."));
                    if (killer != null) {
                        killer.sendMessage("§5§l희귀 전리품 드랍! §8(§d정화의 코어§8)");
                        killer.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 10, 2));
                    }
                } if (name != null && name.equals(Component.text("§d창조의 좀비"))) {
                    w.dropItem(l, CORE_OF_CREATION);
                    Bukkit.broadcast(Component.text("§d창조의 좀비§e가 사망했습니다."));
                    if (killer != null) {
                        killer.sendMessage("§5§l희귀 전리품 드랍! §8(§b창조의 코어§8)");
                        killer.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 10, 2));
                    }
                } if (killer != null && killer.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_SHOVEL)) {
                    for (Player p : l.getNearbyPlayers(5)) p.addPotionEffect(new PotionEffect(REGENERATION, 30, 1, false, false));
                    e.getEntity().getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, l.getX(), l.getY(), l.getZ(), 50, 2.5, 1, 2.5, 1, new Particle.DustTransition(Color.fromRGB(255, 127, 255), Color.fromRGB(255, 0, 255), 1), true);
                } if (killer != null && killer.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_PICKAXE)) {
                    Snowman snowman = w.spawn(l, Snowman.class);
                    snowman.customName(Component.text("§8" + killer.getName() + "의 몬스터"));
                    snowman.setCustomNameVisible(true);
                    snowman.setHealth(1);
                    snowman.setTarget(ZombieParser.getNearestZombie(snowman));
                }
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onSnipe(ProjectileHitEvent e) {
        try {
            if (!GameHandler.gameStarted) {
                e.setCancelled(true);
                return;
            } if (e.getEntity() instanceof Arrow a && a.getShooter() instanceof Player damager && e.getHitEntity() instanceof Player victim) {
                if (e.getHitEntity() == damager || playerType.get(victim) == playerType.get(damager)) {
                    e.setCancelled(true);
                    return;
                } if (playerType.get(victim).equals(PlayerType.INFECTED)) {
                    victim.setNoDamageTicks(0);
                    victim.setMaximumNoDamageTicks(0);
                }
            } if (e.getEntity() instanceof Snowball s && s.getShooter() instanceof Snowman) {
                if (e.getHitEntity() instanceof Player) e.setCancelled(true);
                else if (e.getHitEntity() != null && ZombieParser.isZombie(e.getHitEntity())) {
                    e.setCancelled(true);
                    ((LivingEntity) e.getHitEntity()).damage(1);
                }
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        try {
            Component name = e.getDamager().customName();
            Location l = e.getEntity().getLocation();
            if (!GameHandler.gameStarted) {
                e.setCancelled(true);
                return;
            } if (e.getEntity() instanceof Player p && playerType.get(p).equals(PlayerType.INFECTED)) {
                p.setNoDamageTicks(0);
                p.setMaximumNoDamageTicks(0);
                if (e.getDamager() instanceof Player p1 && p1.getInventory().getItemInMainHand().displayName().contains(Component.text("백신"))) {
                    playerType.put(p, PlayerType.SURVIVE);
                    ItemStack i = p1.getInventory().getItemInMainHand();
                    Damageable meta = (Damageable) i.getItemMeta();
                    meta.setDamage(meta.getDamage() + 62);
                    i.setItemMeta(meta);
                    if (meta.getDamage() > 240) {
                        p1.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        p1.sendMessage("§c백신이 파괴되었습니다.");
                    } else p1.getInventory().setItemInMainHand(i);
                    Bukkit.broadcast(Component.text("§b" + p.getName() + "§9님이 인간이 되었습니다."));
                    Main.title(p, "§b인간이 되셨습니다.", "§9다시 좀비들에게서 살아남으세요!", 1, 5, 1);
                    GameHandler.setupItems(p.getInventory());
                }
            } if (e.getDamager() instanceof Player p && playerType.get(p).equals(PlayerType.INFECTED)) {
                e.setDamage(5);
            } if (GameHandler.gameStarted && e.getEntity() instanceof Player && e.getDamager().getType().equals(EntityType.PLAYER)) {
                if (playerType.get((Player) e.getDamager()).equals(playerType.get((Player) e.getEntity()))) e.setCancelled(true);
            } if (ZombieParser.isZombie(e.getEntity())) {
                if (e.getDamager() instanceof Player p && playerType.get(p).equals(PlayerType.INFECTED)) e.setCancelled(true);
                else {
                    e.getEntity().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getEntity().getLocation().add(0, 1, 0), 10, 0.25, 0.25, 0.25, 0, Material.REDSTONE_BLOCK.createBlockData(), true);
                    ((LivingEntity) e.getEntity()).setNoDamageTicks(0);
                }
            } if (ZombieParser.isZombie(e.getDamager()) && e.getEntity() instanceof Player) {
                if (!playerType.get((Player) e.getEntity()).equals(PlayerType.SURVIVE)) e.setCancelled(true);
                if (zombieDamageMult != 1) e.setDamage(e.getDamage() * zombieDamageMult);
                else if (name != null) {
                    if (name.equals(Component.text("§c파괴의 좀비"))) {
                        e.setDamage(e.getDamage() / 1.25);
                        e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
                    } else if (name.equals(Component.text("§9정화의 좀비"))) {
                        e.setDamage(e.getDamage() / 1.9);
                        if (e.getEntity() instanceof Player p) {
                            p.addPotionEffects(Arrays.asList(Main.pot(BLINDNESS, 1, 0), Main.pot(SLOW, 5, 1), Main.pot(WEAKNESS, 10, 0)));
                        }
                    } else if (name.equals(Component.text("§d창조의 좀비"))) {
                        e.setDamage(e.getDamage() / 2);
                    }
                }
            } if (e.getDamager() instanceof Player p && ZombieParser.isZombie(e.getEntity())) {
                if (p.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_AXE)) {
                    if (Math.random() <= 0.25) {
                        p.playSound(Sound.sound(Key.key("minecraft:entity.generic.explode"), Sound.Source.MASTER, 0.5F, 1));
                        l.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, l, 1, 0, 0, 0, 1, null, true);
                        l.getWorld().spawnParticle(Particle.CRIT, l.getX(), l.getY(), l.getZ(), 75, 0, 0, 0, 1, null, true);
                        for (LivingEntity entity : l.getNearbyLivingEntities(5)) {
                            if (ZombieParser.isZombie(entity)) entity.damage(5);
                        }
                    }
                } if (p.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_SWORD)) {
                    if (Math.random() <= 0.3) {
                        p.playSound(Sound.sound(Key.key("minecraft:entity.generic.explode"), Sound.Source.MASTER, 0.5F, 1));
                        l.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, l, 1, 0, 0, 0, 1, null, true);
                        l.getWorld().spawnParticle(Particle.FLAME, l.getX(), l.getY(), l.getZ(), 100, 0, 0, 0, 1.5, null, true);
                        l.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, l.getX(), l.getY(), l.getZ(), 50, 2.5, 1, 2.5, 1, new Particle.DustTransition(Color.fromRGB(255, 127, 255), Color.fromRGB(255, 0, 255), 1), true);
                        for (LivingEntity entity : l.getNearbyLivingEntities(5)) {
                            if (ZombieParser.isZombie(entity)) entity.damage(6);
                            else if (entity instanceof Player p1) {
                                p1.setHealth(Math.min(p1.getHealth() + 1, p1.getHealthScale()));
                            }
                        }
                    }
                }
            } if (e.getEntity() instanceof Snowman) {
                if (!ZombieParser.isZombie(e.getDamager())) e.setCancelled(true);
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        try {
            if (!GameHandler.gameStarted) {
                e.setCancelled(true);
                return;
            } if (ZombieParser.isZombie(e.getEntity())) {
                e.getEntity().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getEntity().getLocation(), 10, 0.25, 0.25, 0.25, 0, Material.REDSTONE_BLOCK.createBlockData(), true);
                if (e.getEntity().getType().equals(EntityType.HUSK) && e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
                    ((LivingEntity) e.getEntity()).setNoDamageTicks(20);
                    e.setCancelled(true);
                    e.getEntity().teleport(new Location(e.getEntity().getWorld(), e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY() + 1, e.getEntity().getLocation().getX()));
                    e.getEntity().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getEntity().getLocation(), 5, 0.25, 0.25, 0.25, 0, Material.SAND.createBlockData(), true);
                } else ((LivingEntity) e.getEntity()).setNoDamageTicks(0);
            } if (e.getEntity().getType().equals(EntityType.PLAYER) && playerType.get((Player) e.getEntity()).equals(PlayerType.INFECTED)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) e.setCancelled(true);
            } if (e.getEntity() instanceof Snowman) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.MELTING)) e.setCancelled(true);
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onTarget(@NotNull EntityTargetEvent e) {
        try {
            if (!e.getReason().equals(EntityTargetEvent.TargetReason.CUSTOM)) {
                e.setCancelled(true);
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onLivingTarget(@NotNull EntityTargetLivingEntityEvent e) {
        try {
            if (!e.getReason().equals(EntityTargetEvent.TargetReason.CUSTOM)) {
                e.setCancelled(true);
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onArrowPickup(@NotNull PlayerPickupArrowEvent e) {
        try {
            if (playerType.get(e.getPlayer()).equals(PlayerType.INFECTED)) e.setCancelled(true);
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        try {
            ItemStack i = e.getItem();
            Component name  = i.getItemMeta().displayName();
            Player p = e.getPlayer();
            if (e.getItem().getType().equals(Material.COOKED_CHICKEN)) {
                e.setCancelled(true);
                p.sendMessage("§c너무 따끈따끈해서 먹기 어렵다.\n§e조합하는데 쓰도록 하자.");
                return;
            } if (name != null && name.contains(Component.text("좀비 사과")) && !name.contains(Component.text("오염된"))) {
                e.setCancelled(true);
                removeOne(p, i);
                p.addPotionEffect(pot(SATURATION, 1.0, 1));
                p.sendActionBar(Component.text("§a좀비 사과§7를 섭취했습니다."));
            } if (name != null && name.contains(Component.text("§e좀비 §6황금§e 사과"))) {
                e.setCancelled(true);
                removeOne(p, i);
                p.addPotionEffects(Arrays.asList(pot(REGENERATION, 5, 2), pot(ABSORPTION, 150, 1), pot(SATURATION, 1.0, 4)));
                p.sendActionBar(Component.text("§e좀비 황금 사과§7를 섭취했습니다."));
            } if (name != null && name.contains(Component.text("황금 사과"))) {
                e.setCancelled(true);
                removeOne(p, i);
                p.addPotionEffects(Arrays.asList(pot(REGENERATION, 5, 1), pot(ABSORPTION, 150, 0), pot(SATURATION, 1.0, 3)));
                p.sendActionBar(Component.text("§e황금 사과§7를 섭취했습니다."));
            } if (name != null && name.contains(Component.text("좀비 스테이크"))) {
                e.setCancelled(true);
                if (name.contains(Component.text("단"))) {
                    p.setFoodLevel(p.getFoodLevel() + 7);
                    p.setSaturation(p.getSaturation() + 12);
                    p.sendActionBar(Component.text("§6단§e짠§6단§e짠 좀비 스테이크§7를 섭취했습니다."));
                } else {
                    p.setFoodLevel(p.getFoodLevel() + 4);
                    p.setSaturation(p.getSaturation() + 8);
                    p.sendActionBar(Component.text("§e좀비 스테이크§7를 섭취했습니다."));
                } removeOne(p, i);
            } if (name != null && name.contains(Component.text("썩은 고기"))) {
                removeOne(p, i);
                p.setFoodLevel(p.getFoodLevel() + 1);
                p.addPotionEffect(pot(HUNGER, 5, 1));
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        try {
            Player p = e.getPlayer();
            if (e.getClickedBlock() != null && BlockParser.isBlacklistedBlock(e.getClickedBlock().getType())) e.setCancelled(true);
            if (InteractCDTimer.getInteractCooldown().containsKey(p)) return;
            ItemStack i = p.getInventory().getItemInMainHand();
            switch (i.getType()) {
                case AMETHYST_SHARD -> {
                    removeOne(p, i);
                    p.setHealth(Math.min(p.getHealth() + 6, p.getHealthScale()));
                    p.playSound(Sound.sound(Key.key("minecraft:entity.generic.eat"), Sound.Source.MASTER, 10, 1));
                    p.sendActionBar(Component.text("§7좀비의 흔적을 섭취했습니다."));
                    InteractCDTimer.getInteractCooldown().put(p, 10);
                    return;
                } case SHULKER_SHELL -> {
                    p.openWorkbench(p.getLocation(), true);
                    InteractCDTimer.getInteractCooldown().put(p, 5);
                    return;
                } case RAW_GOLD -> {
                    removeOne(p, i);
                    p.playSound(Sound.sound(Key.key("minecraft:entity.generic.eat"), Sound.Source.MASTER, 10, 1));
                    p.addPotionEffect(pot(REGENERATION, 5, 0));
                    p.sendActionBar(Component.text("§7좀비 토금을 섭취했습니다."));
                    InteractCDTimer.getInteractCooldown().put(p, 5);
                    return;
                } case SWEET_BERRIES -> {
                    removeOne(p, i);
                    p.playSound(Sound.sound(Key.key("minecraft:entity.generic.eat"), Sound.Source.MASTER, 10, 1));
                    p.addPotionEffects(Arrays.asList(pot(REGENERATION, 5, 2), pot(ABSORPTION, 180, 4), pot(DAMAGE_RESISTANCE, 240, 1), pot(FIRE_RESISTANCE, 420, 0), pot(SATURATION, 1.0, 8)));
                    p.sendActionBar(Component.text("§7좀비신의 열매를 섭취했습니다."));
                    InteractCDTimer.getInteractCooldown().put(p, 5);
                    return;
                } case HEART_OF_THE_SEA -> {
                    removeOne(p, i);
                    p.setHealthScale(p.getHealthScale() + 10);
                    AttributeInstance a = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (a != null) a.setBaseValue(a.getBaseValue() + 10);
                    p.playSound(Sound.sound(Key.key("minecraft:entity.generic.eat"), Sound.Source.MASTER, 10, 1));
                    p.addPotionEffects(Arrays.asList(pot(REGENERATION, 5, 2), pot(ABSORPTION, 150, 1)));
                    p.sendMessage("§c§l체력 최대치 +5§e!");
                    title(p, " ", "§c체력 최대치 +5§e!", 0.0, 1.0, 0.5);
                    p.sendActionBar(Component.text("§7좀비의 영생을 섭취했습니다."));
                    InteractCDTimer.getInteractCooldown().put(p, 5);
                    return;
                } case COOKED_PORKCHOP -> {
                    removeOne(p, i);
                    infiniteFull.put(p, true);
                    p.playSound(Sound.sound(Key.key("minecraft:entity.generic.eat"), Sound.Source.MASTER, 10, 1));
                    p.sendMessage("§d§l배고픔, 포화 무한 활성화§e!");
                    title(p, " ", "§d§l배고픔, 포화 무한 활성화§e!", 0.0, 1.0, 0.5);
                    p.sendActionBar(Component.text("§7죽은 자들의 식사를 섭취했습니다."));
                    InteractCDTimer.getInteractCooldown().put(p, 5);
                    return;
                } case CAULDRON -> {
                    p.openInventory(Bukkit.createInventory(null, 54, Component.text("§4§l이 안의 아이템은 모두 사라집니다!!!")));
                    InteractCDTimer.getInteractCooldown().put(p, 5);
                    return;
                }
            } if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.BEACON)) {
                if (i.getType().equals(Material.NETHER_STAR)) {
                    int count = i.getAmount();
                    double random = Math.random() * 10;
                    int add = (int) Math.round(count * random);
                    p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    p.sendMessage("§b정화기 파워가§a " + add + "§b만큼 충전되었습니다.");
                    beaconPower = beaconPower + add;
                    InteractCDTimer.getInteractCooldown().put(p, 5);
                } else if (i.getType().equals(Material.ROTTEN_FLESH)) {
                    int count = i.getAmount();
                    if (beaconPower == 0) {
                        p.sendMessage("§c정화기 파워가 부족합니다.");
                        InteractCDTimer.getInteractCooldown().put(p, 5);
                    } else if (beaconPower >= count) {
                        beaconPower = beaconPower - count;
                        ItemStack steak = ZOMBIE_STEAK;
                        steak.setAmount(count);
                        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        p.getInventory().addItem(steak);
                        p.sendMessage("§a" + count + "§b개 만큼의 썩은 고기가 정화되었습니다.");
                        InteractCDTimer.getInteractCooldown().put(p, 5);
                    } else {
                        count = count - beaconPower;
                        ItemStack steak = ZOMBIE_STEAK;
                        steak.setAmount(beaconPower);
                        i.setAmount(count);
                        ItemStack origin = p.getInventory().getItemInMainHand();
                        origin.setAmount(origin.getAmount() - beaconPower);
                        p.getInventory().setItemInMainHand(i);
                        p.getInventory().setItemInMainHand(origin);
                        p.getInventory().addItem(steak);
                        beaconPower = 0;
                        p.sendMessage("§a" + count + "§b개 만큼의 썩은 고기가 정화되었습니다.");
                        InteractCDTimer.getInteractCooldown().put(p, 5);
                    } if (playerType.get(p) != null && playerType.get(p).equals(PlayerType.INFECTED)) {
                        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                            if (Math.random() <= 1.0/3.0) beaconDurability--;
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(Sound.sound(Key.key("minecraft:block.glass.hit"), Sound.Source.MASTER, 1, 2));
                                Main.title(player, " ", "§c좀비가 정화기를 부수고 있습니다!", 0.0, 0.5, 0.5);
                            } if (beaconDurability <= 0) {
                                World w = Bukkit.getWorld("world");
                                if (w != null) w.getBlockAt(252, 70, 208).setType(Material.AIR);
                                for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(Key.key("minecraft:entity.wither.death"), Sound.Source.MASTER, 1, 2));
                                Bukkit.broadcast(Component.text("§c§l정화기가 파괴되었습니다! §4산소가 더 이상 회복되지 않으며\n§4썩은 고기 또한 더 이상 정화할 수 없습니다."));
                                beaconAlive = false;
                            }

                        }
                    }
                } else if (i.getType().equals(Material.IRON_INGOT)) {
                    removeOne(p, i);
                    beaconDurability = Math.min(beaconDurability + 5, 50);
                    p.sendMessage("§b정화기를 §9" + Math.min(beaconDurability + 5, 50) + "§b만큼 수리했습니다.");
                }
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            Component t = e.getView().title();
            if (t.equals(Component.text("§2게임 시작 메뉴"))) {
                e.setCancelled(true);
                if (e.getSlot() == 10) {
                    if (!gameStarted) GameHandler.startGame(GameHandler.Gamemode.NORMAL);
                    else {
                        p.sendMessage("§c게임이 이미 진행중입니다.");
                        p.closeInventory();
                    }
                } else if (e.getSlot() == 12 || e.getSlot() == 14 || e.getSlot() == 16) {
                    p.sendMessage("§aComing soon!");
                    p.closeInventory();
                }
            } else if (t.equals(Component.text("§8커스텀 아이템들"))) {
                if (!p.isOp()) e.setCancelled(true);
            } else if (t.equals(Component.text("§4§l이 안의 아이템은 모두 사라집니다!!!"))) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.CAULDRON)) {
                    e.setCancelled(true);
                    p.sendMessage("§4쓰레기통 안에 쓰레기통을 넣을 수 없습니다...");
                }
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        try {
            e.setCancelled(true);
            if (wave == 100  && e.getBlock().getType().equals(Material.MANGROVE_ROOTS)) {
                stopGame();
                e.getBlock().getWorld().strikeLightningEffect(e.getBlock().getLocation());
                Bukkit.broadcast(Component.text("§b--------------------\n§9생존자들의 승리§b로 게임이 종료되었습니다.\n§b--------------------"));
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try {
            Player p = e.getPlayer();
            e.joinMessage(Component.text("| §a" + p.getName() + "§e님이 접속했습니다."));
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            p.teleport(new Location(p.getWorld(), 240.5, 224.0, 208.5));
            AttributeInstance a = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (a != null) a.setBaseValue(20);
            registerTask(p);
            registerNpc(p);
            discoverRecipes(p);
            Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(Double.MAX_VALUE);
            if (gameStarted) p.setGameMode(GameMode.SPECTATOR);
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        try {
            Player p = e.getPlayer();
            e.quitMessage(Component.text("| §e" + p.getName() + "§6님이 퇴장했습니다."));
            Bukkit.getScheduler().cancelTask(taskId.get(p));
            if (gameStarted) {
                if (playerType.get(p) != null && playerType.get(p).equals(PlayerType.SURVIVE)) {
                    Bukkit.broadcast(Component.text("§4생존자 한명이 세계를 떠났습니다..."));
                    for (ItemStack i : p.getInventory().getContents()) {
                        if (i != null) {
                            p.getWorld().dropItem(p.getLocation(), i);
                        }
                    } humanCount--;
                    if (humanCount <= 0) failGame();
                } if (playerType.get(p) != null && playerType.get(p).equals(PlayerType.INFECTED)) {
                    infectCount--;
                    Bukkit.broadcast(Component.text("§4좀비 한명이 세계를 떠났습니다..."));
                }
            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        try {
            if (!GameHandler.gameStarted) {
                e.setCancelled(true);
                e.getEntity().setFoodLevel(20);
                e.getEntity().setSaturation(0);
            } else if (playerType.get((Player) e.getEntity()).equals(PlayerType.INFECTED)) {
                e.setCancelled(true);
                e.getEntity().setFoodLevel(20);
                e.getEntity().setSaturation(20);
            } else if (infiniteFull.get((Player) e.getEntity())) {
                e.setCancelled(true);
                e.getEntity().setFoodLevel(20);
                e.getEntity().setSaturation(20);

            }
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler
    public void onRecipe(PlayerRecipeDiscoverEvent e) {
        try {
            String r = e.getRecipe().getKey();
            if (!customRecipeKeys.contains(new NamespacedKey(getPlugin(Main.class), r))) e.setCancelled(true);
        } catch (Exception e1) {
            printException(e1);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        try {
            for (String s : CMDHandler.blackList)
                if (e.getMessage().toLowerCase().startsWith("/" + s + " ")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§4해당 명령어는 사용이 금지되었습니다.");
                }
        } catch (Exception e1) {
            printException(e1);
        }
    }

    public static void registerTask(Player p) {
        try {
            int i = repeat(() -> {
                try {
                    Location l = p.getLocation();
                    if (!GameHandler.gameStarted && l.getY() < 219 && p.getGameMode().equals(GameMode.SURVIVAL))
                        p.teleport(new Location(p.getWorld(), 240.5, 224.0, 208.5));
                    if (p.getGameMode().equals(GameMode.SPECTATOR) && (l.getX() < 115.5 || l.getY() < 50 || l.getZ() < 83.5 || l.getX() > 365.5 || l.getY() > 275 || l.getZ() > 333.5)) {
                        p.teleport(new Location(p.getWorld(), 241.5 - 1 + (Math.random() * 2), 70, 209.5 - 1 + (Math.random() * 2)));
                        p.sendMessage("§c맵을 탈출할 수 없습니다!");
                    }
                } catch (Exception e1) {
                    printException(e1);
                }
            }, 1);
            taskId.put(p, i);
        } catch (Exception e2) {
            printException(e2);
        }
    }
    public static void removeOne(Player p, ItemStack i) {
        if (i.getAmount() == 1) p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        else {
            i.setAmount(i.getAmount() - 1);
            p.getInventory().setItemInMainHand(i);
        }
    }

    public static void registerNpc(Player p) {
        try {
            CraftPlayer player = (CraftPlayer) p;
            ServerPlayer sp = player.getHandle();
            ServerGamePacketListenerImpl connection = sp.connection;
            GameProfile profile = new GameProfile(UUID.randomUUID(), "§e§l클릭해서 게임 시작하기");
            final String value = "ewogICJ0aW1lc3RhbXAiIDogMTY0OTE5ODU0NzI0MiwKICAicHJvZmlsZUlkIiA6ICJjNTZlMjI0MmNiZWY0MWE2ODdlMzI2MGRjMGNmOTM2MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJMSlI3MzEwMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84OTRlZGQ3N2NhZmQyY2I4Mjg2ZDgwZmQ5ODdiOTRkZTA2ZjgwNDY3NmJjMjdkZDI5ZWYwNWNkOWQxMDQzYjVhIgogICAgfQogIH0KfQ==";
            final String signature = "mMX1AEpAduOrgPGeUUZWMd9BONummUyt33Vbm17Rw7l9KZjJl+sr1lBUShMUK688nCHFzUXv+8CxXxcCcn3U+Xv+cQ+WZgeAl8kfk5aQPJ6yNcGDGyZj8rmwLsvG4vJzr+BYKNYXuaIXkB3lApchLiH4xtRAaW63124v4glmlaoutYaHthvt1CsOzSdAEl25GG/dC0oAH1+wYOrG8Kln6T19qFbQe7Ox5G04SN9EzFlCzZJMHyznjse7w+kCj51OXyNHZlOw31nM/cc0/RcOGhT1kovv2wgezUdNGEChe191pMAgpXX5Rv3WV+Fj64PZsz2WQfZlKX7nV/KRxgboMJz8dvkPKliV0QUelrzDHvJ2HO8z721Cmhd1v3danXoT/FEr7AOaGsEbxQoBMJEJchPrZ6A28cGoFJWUn8nnxs+wv0KUvN37hbrmqEpQfdmJ7jfhXb/CgSVG92fbYRi/Y8QiMVPsefJtoFFIrqHfHu0EhSqm+b/HYcATcEFqBblRgJbkKeBAjV4MQWFFjge4odmw+ySGtBmfRFe50Wtu8c1/v7pRrQ8VOanmOujKAF8azETFsEzuw8nCi0pqgFtDLn8hlcSs3IkvLRjZBFQ96PeM9eYtyy86+68VNK30BIrP2ZSi+f5ymi3cAGiiJkIHvJyVyi5DLdjRDqYFPAC9rEE=";
            profile.getProperties().put("textures", new Property("textures", value, signature));
            ServerPlayer npc = new ServerPlayer(Objects.requireNonNull(sp.getServer()), sp.getLevel(), profile, null);
            npc.setPos(240.5, 224.0625, 202.5);
            npc.setGlowingTag(true);
            SynchedEntityData data = npc.getEntityData();
            data.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) (0x01 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
            connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
            connection.send(new ClientboundAddPlayerPacket(npc));
            connection.send(new ClientboundSetEntityDataPacket(npc.getId(), data, true));
            connection.send(new ClientboundRotateHeadPacket(npc, (byte) 0));
            connection.send(new ClientboundMoveEntityPacket.Rot(npc.getBukkitEntity().getEntityId(), (byte) 0, (byte) 0, true));
            delay(() -> connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc)), 1);
            connection.send(new ClientboundRemoveEntitiesPacket());
            npcId.put(p, npc.getBukkitEntity().getEntityId());
        } catch (Exception e) {
            printException(e);
        }
    }
    public static void failGame() {
        delay(() -> Bukkit.broadcast(Component.text("§c§o...결국 모든 원정대 멤버는 좀비가 되었고,")), 100);
        delay(() -> Bukkit.broadcast(Component.text("§c§o...좀비 핵심 처치 원정은 실패로 돌아가게 되었습니다.")), 200);
        delay(() -> Bukkit.broadcast(Component.text("§c§o...그렇게 남은 생존자 또한 한 자릿 수에 가까워졌고,")), 300);
        delay(() -> Bukkit.broadcast(Component.text("§4§o...인류는 바이러스에게 지배당하는 어두운 결말을 맞이하게 되었습니다...")), 400);
        delay(() -> {
            Bukkit.broadcast(Component.text("§c----------------------"));
            Bukkit.broadcast(Component.text("§c§l패배 §4(모든 플레이어 사망)"));
            Bukkit.broadcast(Component.text("§c----------------------"));
            stopGame();
        }, 500);
    }
    public static void discoverRecipes(@NotNull Player p) {
        p.undiscoverRecipes(recipeKeys);
        for (NamespacedKey key : customRecipeKeys) p.discoverRecipe(key);
    }
    public static HashMap<Player, Boolean> getInfiniteFull() {
        return infiniteFull;
    }
}
