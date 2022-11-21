package main;

import cmdhandler.CMDHandler;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import eventhandler.EventListener;
import gamehandler.GameHandler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static java.lang.String.format;

/**
 * 마인크래프트 좀비 서바이벌 디펜스 게임
 *
 * @author Barity_
 * @since 2022
 */

public final class Main extends JavaPlugin {
    public static ItemStack ZOMBIE_PIECE, ZOMBIE_POWDER, ZOMBIE_POWER, ZOMBIE_TRACE, CORE_OF_PURIFICATION, CORE_OF_CREATION, CORE_OF_DESTRUCTION,
            PURIFICATION_STAFF, CREATION_WAND, DESTRUCTION_AXE, ZOMBIE_BREAKER;
    public static final List<String> EXCEPTIONS = new ArrayList<>();
    public static final List<NamespacedKey> recipeKeys = new ArrayList<>();
    private static final ConsoleCommandSender LOGGER = Bukkit.getConsoleSender();
    @Override
    public void onEnable() {
        try {
            startMainTask();
            LOGGER.sendMessage("§4[§2ZombieSurvival§4] §a플러그인이 활성화되었습니다.");

            Bukkit.getPluginManager().registerEvents(new EventListener(), this);

            this.getDescription().getCommands().keySet().forEach(s -> { /* 커맨드 & 탭컴플리터 등록 */
                Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler()); /* 커맨드 처리 클래스 등록 */
                Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler()); /* 탭컴플리터(커맨드 제안) 등록 */
            });

            HashMap<Enchantment, Integer> sh5 = new HashMap<>();
            sh5.put(Enchantment.DAMAGE_ALL, 5);
            HashMap<Enchantment, Integer> sh10 = new HashMap<>();
            sh10.put(Enchantment.DAMAGE_ALL, 10);

            ZOMBIE_PIECE = customItem(Material.GREEN_DYE, 1, "§a좀비 조각", Arrays.asList("§7좀비 가루 9개를 모아 만든 조각이다.", "§7다른 물건과 조합할 수 있을 것 같다."), true, null);
            ZOMBIE_POWDER = customItem(Material.GUNPOWDER,1, "§f좀비 가루", Arrays.asList("§7적게나마 온기가 느껴진다.", "", "§8모든 좀비에게서 §95%§8 확률로 1-3개 드랍"), false, null);
            ZOMBIE_POWER = customItem(Material.DIAMOND_SWORD,1, "§e★★☆ §e좀비의 힘", List.of("§7날카로움 V", "", "§8모든 좀비에게서 §d0.03%§8 확률로 드랍"), true, List.of(sh5));
            ZOMBIE_TRACE = customItem(Material.POTION,1, "§c좀비의 흔적", List.of("§7즉시 치유 II", "§e우클릭 시 즉시 사용됨", "", "§8모든 좀비에게서 §50.1%§8 확률로 드랍"), true, null);
            CORE_OF_PURIFICATION = customItem(Material.DIAMOND,1, "§d§l정화의 코어", Arrays.asList("§d정화의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다.", "", "§d정화의 좀비§8에게서 확정적으로 드랍"), true, null);
            CORE_OF_CREATION = customItem(Material.FEATHER,1, "§b§l창조의 코어", Arrays.asList("§b창조의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다.", "", "§b창조의 좀비§8에게서 확정적으로 드랍"), true, null);
            CORE_OF_DESTRUCTION = customItem(Material.END_CRYSTAL,1, "§c§l파괴의 코어", Arrays.asList("§c파괴의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다.", "", "§c파괴의 좀비§8에게서 확정적으로 드랍"), true, null);
            PURIFICATION_STAFF = customItem(Material.NETHERITE_SHOVEL,1, "§e★★★ §d정화의 스태프", Arrays.asList("§7날카로움 V", "", "§a능력: §e힐링 킬링", "§c적 처치§7 시 근처 §e플레이어§7에게", "§d재생 II§7를 §a1§7초간 부여한다."), true, List.of(sh5));
            CREATION_WAND = customItem(Material.NETHERITE_PICKAXE,1, "§e★★★ §b창조의 지팡이", Arrays.asList("§7날카로움 V", "", "§a능력: §e아군 생성기", "§c적 처치§7 시 적의 위치에 §c체력 §a1,", "§c공격력 §a1§7의 친화적 §f눈사람§7을 §b소환§7한다."), true, List.of(sh5));
            DESTRUCTION_AXE = customItem(Material.NETHERITE_AXE, 1, "§e★★★ §c파괴의 도끼", Arrays.asList("§7날카로움 V", "", "§a능력: §e폭발성 날", "§6적 타격§7 시 §5낮은 확률§7로 적의 위치에", "§a5§7의 §c피해§7를 주는 강력한 §e폭발§7을 생성시킨다."), true, List.of(sh5));
            ZOMBIE_BREAKER = customItem(Material.NETHERITE_SWORD,1, "§c⭐ §4좀비 브레이커", Arrays.asList("§6날카로움 X", "", "§a능력: §d생명의 빛", "§6적 타격§7 시 §5낮은 확률§7로 §e플레이어§7는 §a2§7의 체력을", "§d회복§7하고 §2좀비§7는 §a6§7의 §c피해§7를 입는 §e폭발§7이 일어난다."), true, List.of(sh10));

            ShapedRecipe r1 = new ShapedRecipe(new NamespacedKey(this, "custom_purifiacation_staff"), PURIFICATION_STAFF);
            r1.shape("PCP", "PSP", "PPP");
            r1.setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_PURIFICATION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r1);
            ShapedRecipe r2 = new ShapedRecipe(new NamespacedKey(this, "custom_creation_wand"), CREATION_WAND);
            r2.shape("PCP", "PSP", "PPP").setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_CREATION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r2);
            ShapedRecipe r3 = new ShapedRecipe(new NamespacedKey(this, "custom_destruction_axe"), DESTRUCTION_AXE);
            r3.shape("PCP", "PSP", "PPP").setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_DESTRUCTION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r3);
            ShapedRecipe r4 = new ShapedRecipe(new NamespacedKey(this, "custom_zombie_breaker"), ZOMBIE_BREAKER);
            r4.shape("PBP", "CSU", "PTP").setIngredient('P', ZOMBIE_PIECE).setIngredient('B', DESTRUCTION_AXE).setIngredient('C', CREATION_WAND).setIngredient('U', PURIFICATION_STAFF).setIngredient('T', ZOMBIE_TRACE).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r4);
            ShapedRecipe r5 = new ShapedRecipe(new NamespacedKey(this, "custom_zombie_piece"), ZOMBIE_PIECE);
            r5.shape("PPP", "PPP", "PPP").setIngredient('P', ZOMBIE_POWDER);
            Bukkit.addRecipe(r5);

            Bukkit.recipeIterator().forEachRemaining(recipe -> {
                if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                    recipeKeys.add(shapelessRecipe.getKey());
                } else if (recipe instanceof ShapedRecipe shapedRecipe) {
                    recipeKeys.add(shapedRecipe.getKey());
                }
            });
            if (!Bukkit.getOnlinePlayers().isEmpty())
                for (Player p : Bukkit.getOnlinePlayers()) {
                    EventListener.registerNpc(p);
                    EventListener.registerTask(p);
                    EventListener.discoverRecipes(p);
                }

            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
                @Override
                public void onPacketReceiving(PacketEvent e) {
                    try {
                        Player p = e.getPlayer();
                        if (EventListener.getNpcId().get(p).equals(e.getPacket().getIntegers().read(0)) && e.getPacket().getHands().read(0).equals(EnumWrappers.Hand.MAIN_HAND)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    try {
                                        Inventory gui = Bukkit.createInventory(null, 27, Component.text("§2게임 시작 메뉴"));
                                        for (int i = 0; i < 27; i++)
                                            gui.setItem(i, Main.customItem(Material.WHITE_STAINED_GLASS_PANE, 1, " ", null, false, null));
                                        gui.setItem(10, Main.customItem(Material.IRON_SWORD, 1, "§e일반 모드", Arrays.asList("§b클래식한 일반 모드입니다.", "§e100§a웨이브까지 버티는게 목표이며, 모든 플레이어가", "§2좀비§a가 될 경우 §c패배§a하는 시스템입니다."), true, null));
                                        gui.setItem(12, Main.customItem(Material.GOLDEN_SWORD, 1, "§2숙주 처치 모드", Arrays.asList("§2숙주 좀비§b가 등장하는 모드입니다.", "§a플레이어 수에 따라 §e특정 웨이브§a에 아주 강력한", "§4숙주 좀비§a가 등장합니다. §4숙주 좀비§a를 처치할 시", "§a좀비가 더 이상 생성되지 않고 §e감염자§a들은 §c부활할 수 없습니다§a.", "§a이때 감염자들을 모두 §c처치§a하거나 §b치료§a할 경우 §d승리§a합니다."), true, null));
                                        gui.setItem(14, Main.customItem(Material.DIAMOND_SWORD, 1, "§c하드코어 모드", Arrays.asList("§b일반 모드의 어려운 버전입니다.", "§a좀비들의 §c공격력§a이 강해지고 §9속도§a가 빨라지며", "§e백신§a을 사용할 수 §c없습니다§a."), true, null));
                                        gui.setItem(16, Main.customItem(Material.NETHERITE_SWORD, 1, "§4불가능 모드", List.of("§8???"), true, null));
                                        p.openInventory(gui);
                                        p.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 0.75F, 1));
                                    } catch (Exception e1) {
                                        Main.printException(e1);
                                    }
                                }
                            }.runTask(Main.getPlugin(Main.class));
                        }
                    } catch (Exception e1) {
                        Main.printException(e1);
                    }
                }
            });
        } catch (Exception e) {
            printException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            LOGGER.sendMessage("§4[§2ZombieSurvival§4] §c플러그인이 비활성화되었습니다.");

            Bukkit.removeRecipe(new NamespacedKey(this, "custom_purifiacation_staff"));
            Bukkit.removeRecipe(new NamespacedKey(this, "custom_creation_wand"));
            Bukkit.removeRecipe(new NamespacedKey(this, "custom_destruction_axe"));
            Bukkit.removeRecipe(new NamespacedKey(this, "custom_zombie_breaker"));
            Bukkit.removeRecipe(new NamespacedKey(this, "custom_zombie_piece"));

            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) p).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(EventListener.getNpcId().get(p)));
                    p.undiscoverRecipes(Main.recipeKeys);
                }
            }
        } catch (Exception e) {
            printException(e);
        }
    }
    public static void startMainTask() {
        try {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                try {
                    Block gate = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(252, 72, 208);
                    EndGateway gateway = (EndGateway) gate.getState();
                    if (GameHandler.beaconAlive) gateway.setAge(120);
                    else gateway.setAge(gateway.getAge()-30);
                    gateway.update();
                } catch (Exception e) {
                    printException(e);
                }
            }, 0, 20L);
        } catch (Exception e) {
            printException(e);
        }
    }
    public static void printException(@NotNull Exception e) {
        try {
            e.printStackTrace();
            String className = Thread.currentThread().getStackTrace()[2].getClassName();
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            String errorName = e.getClass().getName();
            String errorMessage = e.getMessage();
            Bukkit.broadcast(Component.text(format("§6%s.%s()§c에서 오류가 발생했습니다.", className, methodName)));
            if (e.getMessage() != null) {
                EXCEPTIONS.add(format("§4%s: §c%s\n§c> §6%s.%s() §4(§c%tT§4)", errorName, errorMessage, className, methodName, new Date()));
                Bukkit.broadcast(Component.text(format("§4%s: §c%s", errorName, errorMessage)));
            } else {
                EXCEPTIONS.add(format("§4%s: §c알 수 없는 오류 §7(오류 메시지 없음)\n§c> §6%s.%s() §4(§c%tT§4)", errorName, className, methodName, new Date()));
                Bukkit.broadcast(Component.text(format("§4%s: §c알 수 없는 오류", errorName)));
            }
        } catch (Exception e2) {
            Bukkit.broadcast(Component.text("§4§l오류 출력 도중 또 다른 오류가 발생하였습니다. §c(콘솔 확인바람)"));
            e2.printStackTrace();
        }
    }

    public static ItemStack customItem(@NotNull Material item, @NotNull Integer amount, @NotNull String name, @Nullable List<String> lore, @NotNull Boolean shiny, @Nullable List<HashMap<Enchantment, Integer>> enchantments) {
        try {
            ItemStack i = new ItemStack(item, amount);
            ItemMeta m = i.getItemMeta();
            m.displayName(Component.text(name));
            List<Component> l = new ArrayList<>();
            if (lore != null) for (String s : lore) l.add(Component.text(s));
            if (enchantments != null) for (HashMap<Enchantment, Integer> e : enchantments)
                for (Map.Entry<Enchantment, Integer> entry : e.entrySet())
                    m.addEnchant(entry.getKey(), entry.getValue(), true);
            m.lore(l);
            if (shiny) m.addEnchant(Enchantment.DURABILITY, 1, true);
            m.setUnbreakable(true);
            m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS);
            i.setItemMeta(m);
            return i;
        } catch (Exception e) {
            printException(e);
            return null;
        }
    }
}
