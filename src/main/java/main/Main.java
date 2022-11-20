package main;

import cmdhandler.CMDHandler;
import com.google.common.collect.Multimap;
import eventhandler.EventListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static java.lang.String.format;

public final class Main extends JavaPlugin {
    public static ItemStack ZOMBIE_POWDER;
    public static ItemStack ZOMBIE_POWER;
    public static ItemStack ZOMBIE_TRACE;
    public static ItemStack CORE_OF_PURIFICATION;
    public static ItemStack CORE_OF_CREATION;
    public static ItemStack CORE_OF_DESTRUCTION;
    public static ItemStack PURIFICATION_STAFF;
    public static ItemStack CREATION_WAND;
    public static ItemStack DESTRUCTION_AXE;
    public static ItemStack ZOMBIE_BREAKER;
    public static final List<String> EXCEPTIONS = new ArrayList<>();
    private static final ConsoleCommandSender LOGGER = Bukkit.getConsoleSender();
    @Override
    public void onEnable() {
        try {
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
            ZOMBIE_POWDER = customItem(Material.GUNPOWDER,1, "§7좀비 가루", Arrays.asList("§e적게나마 온기가 느껴진다.", "", "§7모든 좀비에게서 §95%§7 확률로 1-3개 드랍"), false, null, null);
            ZOMBIE_POWER = customItem(Material.DIAMOND_SWORD,1, "§e★★☆ §e좀비의 힘", List.of("§7날카로움 V", "", "§7모든 좀비에게서 §d0.03%§7 확률로 드랍"), true, List.of(sh5), null);
            ZOMBIE_TRACE = customItem(Material.POTION,1, "§c좀비의 흔적", List.of("§7즉시 치유 II", "", "§7모든 좀비에게서 §50.1%§7 확률로 드랍"), true, null, null);
            CORE_OF_PURIFICATION = customItem(Material.DIAMOND,1, "§d§l정화의 코어", Arrays.asList("§d정화의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다."), true, null, null);
            CORE_OF_CREATION = customItem(Material.FEATHER,1, "§b§l창조의 코어", Arrays.asList("§b창조의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다."), true, null, null);
            CORE_OF_DESTRUCTION = customItem(Material.END_CRYSTAL,1, "§c§l파괴의 코어", Arrays.asList("§c파괴의 좀비§7에게서 떨어진 코어다.", "§7유용한 아이템으로 만들 수 있을 것 같다."), true, null, null);
            PURIFICATION_STAFF = customItem(Material.NETHERITE_SHOVEL,1, "§e★★★ §d정화의 스태프", Arrays.asList("§7날카로움 V", "", "§d적 처치 시 근처 플레이어에게", "§d재생 II를 1초간 부여한다. (중첩됨)"), true, List.of(sh5), null);
            CREATION_WAND = customItem(Material.NETHERITE_SHOVEL,1, "§e★★★ §b창조의 지팡이", Arrays.asList("§7날카로움 V", "", "§d적 처치 시 적을 체력 1,", "§d공격력 1의 눈사람 아군으로 만든다."), true, List.of(sh5), null);
            DESTRUCTION_AXE = customItem(Material.NETHERITE_AXE, 1, "§e★★★ §c파괴의 도끼", Arrays.asList("§7날카로움 V", "", "§d적 타격 시 낮은 확률로 적의 위치에", "§d10 공격력의 강력한 폭발을 생성시킨다."), true, List.of(sh5), null);
            ZOMBIE_BREAKER = customItem(Material.NETHERITE_SWORD,1, "§c⭐ §4좀비 브레이커", Arrays.asList("§6날카로움 X", "", "§d적 타격 시 낮은 확률로 플레이어는 2의 체력을", "§d회복하고 좀비는 4의 피해를 입는 폭발이 일어나며", "§d적 처치 시 적을 이 폭발로 공격하는", "§d체력 2의 눈사람 아군으로 만든다."), true, List.of(sh10), null);

            ShapedRecipe r1 = new ShapedRecipe(new NamespacedKey(this, "purifiacation_staff"), PURIFICATION_STAFF);
            r1.shape("PCP", "PSP", "PPP");
            r1.setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_PURIFICATION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r1);
            ShapedRecipe r2 = new ShapedRecipe(new NamespacedKey(this, "creation_wand"), CREATION_WAND);
            r2.shape("PCP", "PSP", "PPP");
            r2.setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_CREATION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r2);
            ShapedRecipe r3 = new ShapedRecipe(new NamespacedKey(this, "destruction_axe"), DESTRUCTION_AXE);
            r3.shape("PCP", "PSP", "PPP");
            r3.setIngredient('P', ZOMBIE_POWDER).setIngredient('C', CORE_OF_DESTRUCTION).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r3);
            ShapedRecipe r4 = new ShapedRecipe(new NamespacedKey(this, "zombie_breaker"), ZOMBIE_BREAKER);
            r4.shape("PBP", "CSU", "PTP");
            ItemStack powder1 = ZOMBIE_POWDER;
            powder1.setAmount(16);
            r4.setIngredient('P', powder1).setIngredient('B', DESTRUCTION_AXE).setIngredient('C', CREATION_WAND).setIngredient('U', PURIFICATION_STAFF).setIngredient('T', ZOMBIE_TRACE).setIngredient('S', ZOMBIE_POWER);
            Bukkit.addRecipe(r4);
        } catch (Exception e) {
            printException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            LOGGER.sendMessage("§4[§2ZombieSurvival§4] §c플러그인이 비활성화되었습니다.");

            Bukkit.removeRecipe(new NamespacedKey(this, "purifiacation_staff"));
            Bukkit.removeRecipe(new NamespacedKey(this, "creation_wand"));
            Bukkit.removeRecipe(new NamespacedKey(this, "destruction_axe"));
            Bukkit.removeRecipe(new NamespacedKey(this, "zombie_breaker"));
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

    public ItemStack customItem(@NotNull Material item, @NotNull Integer amount, @NotNull String name, @Nullable List<String> lore, @NotNull Boolean shiny, @Nullable List<HashMap<Enchantment, Integer>> enchantments, @Nullable Multimap<Attribute, AttributeModifier> modifiers) {
        try {
            ItemStack i = new ItemStack(item, amount);
            ItemMeta m = i.getItemMeta();
            m.displayName(Component.text(name));
            List<Component> l = new ArrayList<>();
            if (lore != null) for (String s : lore) l.add(Component.text(s));
            if (enchantments != null) for (HashMap<Enchantment, Integer> e : enchantments)
                for (Map.Entry<Enchantment, Integer> entry : e.entrySet())
                    m.addEnchant(entry.getKey(), entry.getValue(), true);
            if (modifiers != null) m.setAttributeModifiers(modifiers);
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
