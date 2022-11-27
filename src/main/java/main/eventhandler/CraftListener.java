package main.eventhandler;

import main.Main;
import main.gamehandler.GameHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static main.Main.*;
import static main.gamehandler.GameHandler.playerType;

public class CraftListener implements Listener {
    protected static final HashMap<Player, Boolean> creatinonWand = new HashMap<>();
    protected static final HashMap<Player, Boolean> purificationStaff = new HashMap<>();
    protected static final HashMap<Player, Boolean> vaccine = new HashMap<>();
    protected static final HashMap<Player, Boolean> destructionAxe = new HashMap<>();
    protected static final HashMap<Player, Boolean> zombieBreaker = new HashMap<>();
    protected static final HashMap<Player, Boolean> zombieGoldenApple = new HashMap<>();
    protected static final HashMap<Player, Boolean> zombieGodFruit = new HashMap<>();
    protected static final HashMap<Player, Boolean> premiumZombieSteak = new HashMap<>();
    protected static final HashMap<Player, Boolean> zombiePiece = new HashMap<>();
    protected static final HashMap<Player, Boolean> infiniteLifeofZombie = new HashMap<>();
    protected static final HashMap<Player, Boolean> zombieGold = new HashMap<>();
    protected static final HashMap<Player, Boolean> zombieApple = new HashMap<>();
    protected static final HashMap<Player, Boolean> repairer = new HashMap<>();
    protected static final HashMap<Player, Boolean> compressedLife = new HashMap<>();
    protected static final HashMap<Player, Boolean> deadsMeal = new HashMap<>();
    @EventHandler
    public void onCraft(@NotNull CraftItemEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getRecipe().getResult();
        if (!i.getItemFlags().contains(ItemFlag.HIDE_UNBREAKABLE)) e.setCancelled(true);
        switch (i.getType()) {
            case NETHERITE_PICKAXE -> creatinonWand.remove(p);
            case NETHERITE_SHOVEL -> purificationStaff.remove(p);
            case NETHERITE_AXE -> destructionAxe.remove(p);
            case NETHERITE_SWORD -> zombieBreaker.remove(p);
        }
    }

    @EventHandler
    public void onPickup(@NotNull PlayerAttemptPickupItemEvent e) {
        try {
            Main.delay(() -> {
                try {
                    Player p = e.getPlayer();
                    if (GameHandler.gameStarted && playerType.get(p).equals(GameHandler.PlayerType.INFECTED))
                        e.setCancelled(true);
                    if (has(p, getRecipe("creation_wand")) && !creatinonWand.containsKey(p)) {
                        alert(p, "§b창조의 지팡이", "creation_wand");
                        creatinonWand.put(p, true);
                    }
                    if (has(p, getRecipe("purification_staff")) && !purificationStaff.containsKey(p)) {
                        alert(p, "§d정화의 스태프", "purification_staff");
                        purificationStaff.put(p, true);
                    }
                    if (has(p, getRecipe("destruction_axe")) && !destructionAxe.containsKey(p)) {
                        alert(p, "§c파괴의 도끼", "destruction_axe");
                        destructionAxe.put(p, true);
                    }
                    if (has(p, getRecipe("zombie_breaker")) && !zombieBreaker.containsKey(p)) {
                        alert(p, "§4좀비 브레이커", "zombie_breaker");
                        zombieBreaker.put(p, true);
                    }
                } catch (Exception e1) {
                    Main.printException(e1);
                }
            }, 1);
        } catch (Exception e1) {
            printException(e1);
        }
    }
    protected static boolean has(@NotNull Player p, List<ItemStack> i) {
        for (ItemStack itemStack : i) {
            boolean success = false;
            for (ItemStack item : p.getInventory().getContents()) {
                if (item == null) continue;
                if (item.isSimilar(itemStack)) success = true;
            } if (!success) return false;
        }
        return true;
    }
    protected static void alert(@NotNull Player player, String itemName, String key) {
        Component message = Component.text(itemName + "§b를 만들 재료를 모두 가지고 있습니다. \n§b아이템을 즉시 제작하려면 §e§l여기§b를 클릭하세요!").clickEvent(ClickEvent.runCommand("/craft " + key)).hoverEvent(HoverEvent.showText(Component.text("§a클릭하여 즉시 제작하기 ")));
        player.sendMessage(message);
    }
    protected static @NotNull ItemStack item(@NotNull ItemStack i, int count) {
        ItemStack itemStack = i.clone();
        itemStack.setAmount(count);
        return itemStack;
    }
    protected static @NotNull List<ItemStack> getRecipe(String key) {
        if (key.equals("creation_wand")) return Arrays.asList(item(ZOMBIE_POWDER, 7), item(ZOMBIE_POWER, 1), item(CORE_OF_CREATION, 1));
        if (key.equals("purification_staff")) return Arrays.asList(item(ZOMBIE_POWDER, 7), item(ZOMBIE_POWER, 1), item(CORE_OF_PURIFICATION, 1));
        if (key.equals("destruction_axe")) return Arrays.asList(item(ZOMBIE_POWDER, 7), item(ZOMBIE_POWER, 1), item(CORE_OF_DESTRUCTION, 1));
        if (key.equals("zombie_breaker")) return Arrays.asList(item(ZOMBIE_PIECE, 4), item(CREATION_WAND, 1), item(PURIFICATION_STAFF, 1), item(DESTRUCTION_AXE, 1), item(ZOMBIE_TRACE, 1), item(ZOMBIE_POWER, 1));
        return List.of();
    }
}
