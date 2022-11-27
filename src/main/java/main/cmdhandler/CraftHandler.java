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
                if (has(p, getRecipe(args[0]))) {
                    for (ItemStack itemStack : getRecipe(args[0])) i.removeItem(itemStack);
                    i.addItem(CREATION_WAND);
                    creatinonWand.remove(p);
                    success(p, "§b창조의 지팡이");
                } else noItem(p);
            } case "purification_staff" -> {
                if (has(p, getRecipe(args[0]))) {
                    for (ItemStack itemStack : getRecipe(args[0])) i.removeItem(itemStack);
                    i.addItem(PURIFICATION_STAFF);
                    purificationStaff.remove(p);
                    success(p, "§d정화의 지팡이");
                } else noItem(p);
            } case "destruction_axe" -> {
                if (has(p, getRecipe(args[0]))) {
                    for (ItemStack itemStack : getRecipe(args[0])) i.removeItem(itemStack);
                    i.addItem(DESTRUCTION_AXE);
                    destructionAxe.remove(p);
                    success(p, "§c파괴의 도끼");
                } else noItem(p);
            } case "zombie_breaker" -> {
                if (has(p, getRecipe(args[0]))) {
                    for (ItemStack itemStack : getRecipe(args[0])) i.removeItem(itemStack);
                    i.addItem(ZOMBIE_BREAKER);
                    zombieBreaker.remove(p);
                    success(p, "§4좀비 브레이커");
                } else noItem(p);
            }
            default -> p.sendMessage("§c알 수 없는 조합법입니다!");
        }
    }
    private static void success(@NotNull Player p, String item) {
        p.sendMessage(item + "§a를 제작했습니다.");
        p.playSound(Sound.sound(Key.key("minecraft:entity.villager.work_shepherd"), Sound.Source.MASTER, 1, 1));
    }
    private static void noItem(@NotNull Player p) {
        p.sendMessage("§c충분한 재료를 가지고 있지 않습니다!");
    }
}
