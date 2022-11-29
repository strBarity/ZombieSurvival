package main.cmdhandler;

import main.eventhandler.CraftListener;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import static main.Main.*;

public class CraftHandler extends CraftListener {
    public static void onCommand(CommandSender commandSender, String[] args) {
        Player p = (Player) commandSender;
        PlayerInventory i = p.getInventory();
        if (args.length == 0) {
            p.sendMessage("§c올바르지 않은 사용법입니다!");
        } switch (args[0]) {
            case "creation_wand" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(CREATION_WAND);
                    creatinonWand.remove(p);
                    success(p, "§b창조의 지팡이");
                }
            } case "purification_staff" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(PURIFICATION_STAFF);
                    purificationStaff.remove(p);
                    success(p, "§d정화의 스태프");
                }
            } case "destruction_axe" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(DESTRUCTION_AXE);
                    destructionAxe.remove(p);
                    success(p, "§c파괴의 도끼");
                }
            } case "zombie_breaker" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(ZOMBIE_BREAKER);
                    zombieBreaker.remove(p);
                    success(p, "§4좀비 브레이커");
                }
            } case "vaccine" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(VACCINE);
                    vaccine.remove(p);
                    success(p, "§a백신");
                }
            } case "zombie_gold" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(ZOMBIE_GOLD);
                    zombieGold.remove(p);
                    success(p, "§e좀비 토금");
                }
            } case "zombie_golden_apple" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(ZOMBIE_GOLDEN_APPLE);
                    zombieGoldenApple.remove(p);
                    success(p, "§e좀비 §6황금 §e사과");
                }
            } case "zombiegod_fruit" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(ZOMBIEGOD_FRUIT);
                    zombieGodFruit.remove(p);
                    success(p, "§4좀비신의 열매");
                }
            } case "premium_zombie_steak" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(PREMIUM_ZOMBIE_STEAK);
                    premiumZombieSteak.remove(p);
                    success(p, "§6단§e짠§6단§e짠 좀비 스테이크");
                }
            } case "zombie_piece" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(ZOMBIE_PIECE);
                    zombiePiece.remove(p);
                    success(p, "§a좀비 조각");
                }
            } case "infinitelife_of_zombie" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(INFINITELIFE_OF_ZOMBIE);
                    infiniteLifeofZombie.remove(p);
                    success(p, "§4좀비의 영생");
                }
            } case "zombie_apple" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(ZOMBIE_APPLE);
                    zombieApple.remove(p);
                    success(p, "§c좀비 사과");
                }
            } case "repairer" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(REPAIRER);
                    repairer.remove(p);
                    success(p, "§b정화기 수리기");
                }
            } case "compressed_life" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(COMPRESSED_LIFE);
                    compressedLife.remove(p);
                    success(p, "§4압축된 좀비의 라이프");
                }
            } case "deads_meal" -> {
                if (hasArg(p, args[0], i)) {
                    i.addItem(DEADS_MEAL);
                    deadsMeal.remove(p);
                    success(p, "§4죽은 자들의 식사");
                }
            }
            default -> p.sendMessage("§c알 수 없는 조합법입니다!");
        }
    }
    private static void success(@NotNull Player p, String item) {
        p.sendMessage(item + "§a을(를) 제작했습니다.");
        p.playSound(Sound.sound(Key.key("minecraft:entity.villager.work_shepherd"), Sound.Source.MASTER, 1, 1));
    }
    private static void noItem(@NotNull Player p) {
        p.sendMessage("§c충분한 재료를 가지고 있지 않습니다!");
    }
    private static boolean hasArg(Player p, String s, PlayerInventory i) {
        if (has(p, getRecipe(s))) {
            for (ItemStack itemStack : getRecipe(s)) i.removeItem(itemStack);
            return true;
        } noItem(p);
        return false;
    }
}
