package main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import main.cmdhandler.CMDHandler;
import main.eventhandler.EventListener;
import main.gamehandler.GameHandler;
import main.timerhandler.InvOpenCDTimer;
import main.timerhandler.OxygenTimer;
import main.timerhandler.WaveTimer;
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
    PURIFICATION_STAFF, CREATION_WAND, DESTRUCTION_AXE, ZOMBIE_BREAKER, D_SWORD, D_HELMET, D_CHESTPLATE, D_LEGGINGS, D_BOOTS, POWER_CRYSTAL,
    GOLDEN_APPLE, D_BOW, ZOMBIE_SAND, ZOMBIE_APPLE_D, ZOMBIE_WATERDROP, ZOMBIE_GOLD, ZOMBIE_APPLE, ZOMBIE_GOLDEN_APPLE, ZOMBIEGOD_FRUIT, INFINITELIFE_OF_ZOMBIE,
    SIMPLE_TABLE;
    public static final List<String> EXCEPTIONS = new ArrayList<>();
    public static final List<NamespacedKey> customRecipeKeys = new ArrayList<>();
    public static final List<ItemStack> customItems = new ArrayList<>();
    public static final List<NamespacedKey> recipeKeys = new ArrayList<>();
    private static final ConsoleCommandSender LOGGER = Bukkit.getConsoleSender();
    @Override
    public void onEnable() {
        try {
            startMainTask();
            LOGGER.sendMessage("§4[§2ZombieSurvival§4] §a플러그인이 활성화되었습니다.");

            Bukkit.getPluginManager().registerEvents(new EventListener(), this);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new InvOpenCDTimer(), 0, 1);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new OxygenTimer(), 0, 20);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new WaveTimer(), 0, 20);

            this.getDescription().getCommands().keySet().forEach(s -> { /* 커맨드 & 탭컴플리터 등록 */
                Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler()); /* 커맨드 처리 클래스 등록 */
                Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler()); /* 탭컴플리터(커맨드 제안) 등록 */
            });

            HashMap<Enchantment, Integer> sh5 = new HashMap<>();
            sh5.put(Enchantment.DAMAGE_ALL, 5);
            HashMap<Enchantment, Integer> sh10 = new HashMap<>();
            sh10.put(Enchantment.DAMAGE_ALL, 10);
            HashMap<Enchantment, Integer> pr3 = new HashMap<>();
            pr3.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
            HashMap<Enchantment, Integer> pw3 = new HashMap<>();
            pw3.put(Enchantment.ARROW_DAMAGE, 3);


            // 무기류
            D_SWORD = customItem(Material.IRON_SWORD, 1, "§e★☆☆ §f보급형 검", Arrays.asList("§7날카로움 V", "", "§7간단한 보급형 검 한 자루다.", "§7성능은 그럭저럭. 더 좋은 검을 찾아보자."), true, List.of(sh5), true);
            ZOMBIE_POWER = customItem(Material.DIAMOND_SWORD,1, "§e★★☆ §e좀비의 힘", List.of("§7날카로움 V", "", "§c좀비의 강력한 기운§7이 가득 담겨있다.", "§7다른 좀비 관련 드랍 아이템들과 조합하면", "§5아주 강력한 §7무기를 만들 수 있을 것 같다.", "§8모든 좀비에게서 §d1%§8 확률로 드랍"), true, List.of(sh5), true);
            PURIFICATION_STAFF = customItem(Material.NETHERITE_SHOVEL,1, "§e★★★ §d정화의 스태프", Arrays.asList("§7날카로움 V", "", "§a능력: §e힐링 킬링", "§c적 처치§7 시 근처 §e플레이어§7에게", "§d재생 II§7를 §a1§7초간 부여한다."), true, List.of(sh5), true);
            CREATION_WAND = customItem(Material.NETHERITE_PICKAXE,1, "§e★★★ §b창조의 지팡이", Arrays.asList("§7날카로움 V", "", "§a능력: §e아군 생성기", "§c적 처치§7 시 적의 위치에 §c체력 §a1,", "§c공격력 §a1§7의 친화적 §f눈사람§7을 §b소환§7한다."), true, List.of(sh5), true);
            DESTRUCTION_AXE = customItem(Material.NETHERITE_AXE, 1, "§e★★★ §c파괴의 도끼", Arrays.asList("§7날카로움 V", "", "§a능력: §e폭발성 날", "§6적 타격§7 시 §5낮은 확률§7로 적의 위치에", "§a5§7의 §c피해§7를 주는 강력한 §e폭발§7을 생성시킨다."), true, List.of(sh5), true);
            ZOMBIE_BREAKER = customItem(Material.NETHERITE_SWORD,1, "§c⭐ §4좀비 브레이커", Arrays.asList("§6날카로움 X", "", "§5\"§d이 전쟁을 끝내러 왔다§5\"", "", "§a능력: §d생명의 빛", "§6적 타격§7 시 §5낮은 확률§7로 §e플레이어§7는 §a2§7의 체력을", "§d회복§7하고 §2좀비§7는 §a6§7의 §c피해§7를 입는 §e폭발§7이 일어난다."), true, List.of(sh10), true);
            D_BOW = customItem(Material.BOW, 1, "§e★☆☆ §f보급형 활", Arrays.asList("§7힘 III", "", "§7간단한 보급형 활 하나다.", "§7화살 수가 제한되어 있으니 신중히 사용하자."), true, List.of(pw3), true);

            // 방어구류
            D_HELMET = customItem(Material.IRON_HELMET, 1, "§e★☆☆ §f보급형 헬멧", List.of("§7보호 III"), false, List.of(pr3), true);
            D_CHESTPLATE = customItem(Material.IRON_CHESTPLATE, 1, "§e★☆☆ §f보급형 갑옷", List.of("§7보호 III"), false, List.of(pr3), true);
            D_LEGGINGS = customItem(Material.IRON_LEGGINGS, 1, "§e★☆☆ §f보급형 바지", List.of("§7보호 III"), false, List.of(pr3), true);
            D_BOOTS = customItem(Material.IRON_BOOTS, 1, "§e★☆☆ §f보급형 부츠", List.of("§7보호 III"), false, List.of(pr3), true);

            // 기타
            ZOMBIE_POWDER = customItem(Material.GUNPOWDER,1, "§f좀비 가루", Arrays.asList("§7적게나마 온기가 느껴진다.", "", "§8모든 좀비에게서 §225%§8 확률로 1-3개 드랍"), false, null, true);
            ZOMBIE_TRACE = customItem(Material.POTION,1, "§c좀비의 흔적", Arrays.asList("§7즉시 치유 II", "§e우클릭 시 즉시 사용됨", "", "§8모든 좀비에게서 §53%§8 확률로 드랍"), true, null, true);
            ZOMBIE_PIECE = customItem(Material.GREEN_DYE, 1, "§a좀비 조각", Arrays.asList("§7좀비 가루 9개를 모아 만든 조각이다.", "§7다른 물건과 조합할 수 있을 것 같다."), true, null, true);
            POWER_CRYSTAL = customItem(Material.NETHER_STAR, 1, "§b파워 결정체", Arrays.asList("§b정화기§7의 파워를 랜덤하게 충전해준다.", "", "§8모든 좀비에게서 §910%§8 확률로 드랍"), true, null, true);
            GOLDEN_APPLE = customItem(Material.GOLDEN_APPLE, 16, "§b황금 사과", Arrays.asList("§7몸에 이로운 황금으로 이루어져있다.", "", "§7섭취 시:", "§8- §d재생 II §9(0:05) §c(❤ +2칸)", "§8- §e흡수 I §9(2:00) §c(§6❤ §e2칸§c)", "§8- §a포화 IV §e(§2허기 §a4 §e회복, §5포만감 §a8 §e회복)"), false, null, true);
            CORE_OF_PURIFICATION = customItem(Material.DIAMOND,1, "§d§l정화의 코어", Arrays.asList("§d정화의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다.", "", "§d정화의 좀비§8에게서 확정적으로 드랍"), true, null, true);
            CORE_OF_CREATION = customItem(Material.FEATHER,1, "§b§l창조의 코어", Arrays.asList("§b창조의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다.", "", "§b창조의 좀비§8에게서 확정적으로 드랍"), true, null, true);
            CORE_OF_DESTRUCTION = customItem(Material.END_CRYSTAL,1, "§c§l파괴의 코어", Arrays.asList("§c파괴의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다.", "", "§c파괴의 좀비§8에게서 확정적으로 드랍"), true, null, true);
            ZOMBIE_SAND = customItem(Material.GLOWSTONE_DUST, 1, "§e★☆☆ §6좀비 모래", Arrays.asList("§d\"모든 모래에는 약간의 금이 포함되어 있다\"", "§7이 모래는 더더욱 그런 것 같다.", "§e많이 모으면 금으로 만들 수 있을지도...?", "", "§8모든 허스크에게서 §220%§8 확률로 드랍"), true, null, true);
            ZOMBIE_APPLE_D = customItem(Material.APPLE, 1, "§e★☆☆ §2오염된 좀비 사과", Arrays.asList("§7이걸 떨어뜨린 좀비는 생전 사과를 좋아했던 것 같다.", "§7너무 오염되있어서 섭취할 순 없다.", "§e물 같은걸로 적당히 씻으면 섭취할 수 있을듯 하다.", "", "§8모든 미변형 좀비에게서 §220%§8 확률로 드랍"), false, null, true);
            ZOMBIE_WATERDROP = customItem(Material.LAPIS_LAZULI, 1, "§9좀비 물방울", Arrays.asList("§7드라운드에게 나온 순도 99% H₂O다.", "§7좀비에게 왜 이렇게 순도 높은 물이 있는진 모르겠지만,", "§e오염된 물체를 씻는데는 사용할 수 있을 것 같다.", "", "§8모든 드라운드에게서 §220%§8 확률로 드랍"), true, null, true);
            ZOMBIE_GOLD = customItem(Material.RAW_GOLD, 1, "§e★★☆ 좀비 토금", Arrays.asList("§7티끌 모아 태산을 실천했다.", "§7놀랍게도 금속이지만 먹을 수 있다.", "§e다른 음식에 합친다면 정말 별미일 것이다.", "", "§7섭취 시:", "§8- §d재생 I §9(0:05) §c(❤ +1칸)"), true, null, true);
            ZOMBIE_APPLE = customItem(Material.APPLE, 1, "§e★★☆ §c좀비 사과", Arrays.asList("§7적당히 먹을만한 사과다.", "§7좀비의 기운이 느껴져 먹으면 특별한 효과를 줄 것 같다.", "§e하지만 일반 금이랑은 합칠 수 없다. 황금 사과로 만들 수 있을까?", "", "§7섭취 시:", "§8- §a포화 II §e(§2허기 §a2 §e회복, §5포만감 §a4 §e회복)"), true, null, true);
            ZOMBIE_GOLDEN_APPLE = customItem(Material.ENCHANTED_GOLDEN_APPLE, 1, "§e★★★ §e좀비 §6황금§e 사과", Arrays.asList("§790%는 좀비의 물질으로 이루어졌다.", "§7일반 황금 사과보다 훨씬 좋다.", "§e더욱 강하게 만들 수 있을지도...?", "", "§7섭취 시:", "§8- §d재생 III §9(0:05) §c(❤ +5칸)", "§8- §e흡수 II §9(2:30) §c(§6❤ §e4칸§c)", "§8- §a포화 V §e(§2허기 §a5 §e회복, §5포만감 §a10 §e회복)"), true, null, true);
            ZOMBIEGOD_FRUIT = customItem(Material.SWEET_BERRIES, 1, "§c⭐ §4좀비신의 열매", Arrays.asList("§5\"§d하루에 하나씩 100년간 먹으면 신이 될수 있어§5\"", "", "§7섭취 시:", "§8- §d재생 V §9(0:05) §c(❤ +16.6칸)", "§8- §e흡수 V §9(3:00) §c(§6❤ §e10칸§c)", "§8- §5저항 II §9(4:00) §d(피해 -40%)", "§8- §c화염 저항 §9(7:00) §4(화염 피해 무시)", "§8- §a포화 IX §e(§2허기 §a9 §e회복, §5포만감 §a18 §e회복)"), true, null, true);
            INFINITELIFE_OF_ZOMBIE = customItem(Material.HEART_OF_THE_SEA, 1, "§c⭐ §4좀비의 영생", Arrays.asList("§5\"§d바이러스의 영생 효과만 쏙 훔쳐왔다§5\"", "", "§7섭취 시:", "§8- §c❤ §l최대 생명력 +5칸", "§8- §d재생 III §9(0:05) §c(❤ +5칸)", "§8- §e흡수 II §9(2:30) §c(§6❤ §e4칸§c)"), true, null, true);
            SIMPLE_TABLE = customItem(Material.CRAFTING_TABLE, 1, "§a휴대용 작업대", Arrays.asList("§7미래 기술으로 작업대를 압축해,", "§7어디서든 큰 작업을 할 수 있는 작업대이다.", "", "§e▶ 우클릭해서 사용하기"), false, null, true);

            ShapedRecipe r1 = new ShapedRecipe(new NamespacedKey(Main.getPlugin(Main.class), "purifiacation_staff"), PURIFICATION_STAFF);
            r1.shape("PCP", "PSP", "PPP");
            r1.setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_PURIFICATION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r1);
            customRecipeKeys.add(r1.getKey());
            ShapedRecipe r2 = new ShapedRecipe(new NamespacedKey(Main.getPlugin(Main.class), "creation_wand"), CREATION_WAND);
            r2.shape("PCP", "PSP", "PPP").setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_CREATION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r2);
            customRecipeKeys.add(r2.getKey());
            ShapedRecipe r3 = new ShapedRecipe(new NamespacedKey(Main.getPlugin(Main.class), "destruction_axe"), DESTRUCTION_AXE);
            r3.shape("PCP", "PSP", "PPP").setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_DESTRUCTION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r3);
            customRecipeKeys.add(r3.getKey());
            ShapedRecipe r4 = new ShapedRecipe(new NamespacedKey(Main.getPlugin(Main.class), "zombie_breaker"), ZOMBIE_BREAKER);
            r4.shape("PBP", "CSU", "PTP").setIngredient('P', ZOMBIE_PIECE).setIngredient('B', DESTRUCTION_AXE).setIngredient('C', CREATION_WAND).setIngredient('U', PURIFICATION_STAFF).setIngredient('T', ZOMBIE_TRACE).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r4);
            customRecipeKeys.add(r4.getKey());
            ShapedRecipe r5 = new ShapedRecipe(new NamespacedKey(Main.getPlugin(Main.class), "zombie_piece"), ZOMBIE_PIECE);
            r5.shape("PPP", "PPP", "PPP").setIngredient('P', ZOMBIE_POWDER);
            Bukkit.addRecipe(r5);
            customRecipeKeys.add(r5.getKey());

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
                    EventListener.mainBoardSet(p);
                }

            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
                @Override
                public void onPacketReceiving(PacketEvent e) {
                    try {
                        Player p = e.getPlayer();
                        if (EventListener.getNpcId().get(p).equals(e.getPacket().getIntegers().read(0))) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!InvOpenCDTimer.getInvOpenCooldoawn().containsKey(p)) {
                                            InvOpenCDTimer.getInvOpenCooldoawn().put(p, 1);
                                            Inventory gui = Bukkit.createInventory(null, 27, Component.text("§2게임 시작 메뉴"));
                                            ItemStack blank = Main.customItem(Material.WHITE_STAINED_GLASS_PANE, 1, " ", null, false, null, false);
                                            for (int i = 0; i < 27; i++)
                                                gui.setItem(i, blank);
                                            gui.setItem(10, Main.customItem(Material.IRON_SWORD, 1, "§e일반 모드", Arrays.asList("§b클래식한 일반 모드입니다.", "§e100§a웨이브까지 버티는게 목표이며, 모든 플레이어가", "§2좀비§a가 될 경우 §c패배§a하는 시스템입니다.", "", "§e▶ 클릭해서 게임 시작하기"), true, null, false));
                                            gui.setItem(12, Main.customItem(Material.GOLDEN_SWORD, 1, "§2숙주 처치 모드", Arrays.asList("§2숙주 좀비§b가 등장하는 모드입니다.", "§a플레이어 수에 따라 §e특정 웨이브§a에 아주 강력한", "§4숙주 좀비§a가 등장합니다. §4숙주 좀비§a를 처치할 시", "§a좀비가 더 이상 생성되지 않고 §e감염자§a들은 §c부활할 수 없습니다§a.", "§a이때 감염자들을 모두 §c처치§a하거나 §b치료§a할 경우 §d승리§a합니다.", "", "§e▶ 클릭해서 게임 시작하기"), true, null, false));
                                            gui.setItem(14, Main.customItem(Material.DIAMOND_SWORD, 1, "§c하드코어 모드", Arrays.asList("§b일반 모드의 어려운 버전입니다.", "§a좀비들의 §c공격력§a이 강해지고 §9속도§a가 빨라지며", "§e백신§a을 사용할 수 §c없습니다§a.", "", "§e▶ 클릭해서 게임 시작하기"), true, null, false));
                                            gui.setItem(16, Main.customItem(Material.NETHERITE_SWORD, 1, "§4불가능 모드", List.of("§8???", "", "§e▶ 클릭해서 게임 시작하기"), true, null, false));
                                            p.openInventory(gui);
                                            p.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 0.75F, 1));
                                        }
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

            for (NamespacedKey key : customRecipeKeys) Bukkit.removeRecipe(key);

            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.undiscoverRecipes(Main.recipeKeys);
                    ((CraftPlayer) p).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(EventListener.getNpcId().get(p)));
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
                    if (GameHandler.beaconAlive) gateway.setAge(100);
                    else gateway.setAge(gateway.getAge()+80);
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

    public static ItemStack customItem(@NotNull Material item, @NotNull Integer amount, @NotNull String name, @Nullable List<String> lore, @NotNull Boolean shiny, @Nullable List<HashMap<Enchantment, Integer>> enchantments, @NotNull Boolean save) {
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
            if (save) customItems.add(i);
            return i;
        } catch (Exception e) {
            printException(e);
            return null;
        }
    }
}
