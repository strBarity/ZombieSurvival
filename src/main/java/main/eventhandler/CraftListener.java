package main.eventhandler;

import main.Main;
import main.gamehandler.GameHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
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
            case IRON_HOE -> vaccine.remove(p);
            case RAW_GOLD -> zombieGold.remove(p);
            case GOLDEN_APPLE -> zombieGoldenApple.remove(p);
            case SWEET_BERRIES -> zombieGodFruit.remove(p);
            case COOKED_BEEF -> premiumZombieSteak.remove(p);
            case GREEN_DYE -> zombiePiece.remove(p);
            case HEART_OF_THE_SEA -> infiniteLifeofZombie.remove(p);
            case APPLE -> zombieApple.remove(p);
            case IRON_INGOT -> repairer.remove(p);
            case COPPER_INGOT -> compressedLife.remove(p);
            case COOKED_PORKCHOP -> deadsMeal.remove(p);
        }
    }

    @EventHandler
    public void onPickup(@NotNull PlayerAttemptPickupItemEvent e) {
        try {
            Player p = e.getPlayer();
            if (GameHandler.gameStarted && playerType.get(p).equals(GameHandler.PlayerType.INFECTED))
                e.setCancelled(true);
            Main.delay(() -> {
                try {
                    if (has(p, getRecipe("creation_wand")) && !creatinonWand.containsKey(p)) {
                        alert(p, "??b????????? ?????????", "creation_wand");
                        creatinonWand.put(p, true);
                    }
                    if (has(p, getRecipe("purification_staff")) && !purificationStaff.containsKey(p)) {
                        alert(p, "??d??l????????? ?????????", "purification_staff");
                        purificationStaff.put(p, true);
                    }
                    if (has(p, getRecipe("destruction_axe")) && !destructionAxe.containsKey(p)) {
                        alert(p, "??c??l????????? ??????", "destruction_axe");
                        destructionAxe.put(p, true);
                    }
                    if (has(p, getRecipe("zombie_breaker")) && !zombieBreaker.containsKey(p)) {
                        alert(p, "??4??l?????? ????????????", "zombie_breaker");
                        zombieBreaker.put(p, true);
                    }
                    if (has(p, getRecipe("vaccine")) && !vaccine.containsKey(p)) {
                        alert(p, "??a??l?????? ??????", "vaccine");
                        vaccine.put(p, true);
                    }
                    if (has(p, getRecipe("zombie_gold")) && !zombieGold.containsKey(p)) {
                        alert(p, "??e??l?????? ??????", "zombie_gold");
                        zombieGold.put(p, true);
                    }
                    if (has(p, getRecipe("zombie_golden_apple")) && !zombieGoldenApple.containsKey(p)) {
                        alert(p, "??e??l?????? ??6??l?????? ??e??l??????", "zombie_golden_apple");
                        zombieGoldenApple.put(p, true);
                    }
                    if (has(p, getRecipe("zombiegod_fruit")) && !zombieGodFruit.containsKey(p)) {
                        alert(p, "??4??l???????????? ??????", "zombiegod_fruit");
                        zombieGodFruit.put(p, true);
                    }
                    if (has(p, getRecipe("premium_zombie_steak")) && !premiumZombieSteak.containsKey(p)) {
                        alert(p, "??6??l?????e??l?????6??l?????e??l??? ?????? ????????????", "premium_zombie_steak");
                        premiumZombieSteak.put(p, true);
                    }
                    if (has(p, getRecipe("zombie_piece")) && !zombiePiece.containsKey(p)) {
                        alert(p, "??a?????? ??????", "zombie_piece");
                        zombiePiece.put(p, true);
                    }
                    if (has(p, getRecipe("infinitelife_of_zombie")) && !infiniteLifeofZombie.containsKey(p)) {
                        alert(p, "??4????????? ??????", "infinitelife_of_zombie");
                        infiniteLifeofZombie.put(p, true);
                    }
                    if (has(p, getRecipe("zombie_apple")) && !zombieApple.containsKey(p)) {
                        alert(p, "??c?????? ??????", "zombie_apple");
                        zombieApple.put(p, true);
                    }
                    if (has(p, getRecipe("repairer")) && !repairer.containsKey(p)) {
                        alert(p, "??b????????? ?????????", "repairer");
                        repairer.put(p, true);
                    }
                    if (has(p, getRecipe("compressed_life")) && !compressedLife.containsKey(p)) {
                        alert(p, "??4????????? ????????? ?????????", "compressed_life");
                        compressedLife.put(p, true);
                    }
                    if (has(p, getRecipe("deads_meal")) && !deadsMeal.containsKey(p)) {
                        alert(p, "??4?????? ????????? ??????", "deads_meal");
                        deadsMeal.put(p, true);
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
                if (item.isSimilar(itemStack) && item.getAmount() >= itemStack.getAmount()) success = true;
            } if (!success) return false;
        }
        return true;
    }
    protected static void alert(@NotNull Player player, String itemName, String key) {
        Component message = Component.text(itemName + "??b???(???) ?????? ????????? ?????? ????????? ????????????. \n??b???????????? ?????? ??????????????? ??e??l????????b??? ???????????????!").clickEvent(ClickEvent.runCommand("/craft " + key)).hoverEvent(HoverEvent.showText(Component.text("??a???????????? ?????? ???????????? ")));
        player.sendMessage(message);
    }
    protected static @NotNull ItemStack item(@NotNull ItemStack i, int count) {
        ItemStack itemStack = i.clone();
        itemStack.setAmount(count);
        return itemStack;
    }
    protected static @NotNull List<ItemStack> getRecipe(@NotNull String key) {
        return switch (key) {
            case "deads_meal" ->
                    Arrays.asList(item(PREMIUM_ZOMBIE_STEAK, 3), item(ZOMBIE_TRACE, 2), item(ZOMBIE_PIECE, 2), item(ZOMBIE_GOLDEN_APPLE, 1), item(ZOMBIE_CHICKEN, 1));
            case "compressed_life" ->
                    List.of(item(ZOMBIE_POWER, 9));
            case "repairer" ->
                    Arrays.asList(item(ZOMBIE_PIECE, 8), item(POWER_CRYSTAL, 1));
            case "zombie_apple" ->
                    Arrays.asList(item(ZOMBIE_WATERDROP, 8), item(ZOMBIE_APPLE_D, 1));
            case "infinitelife_of_zombie" ->
                    Arrays.asList(item(ZOMBIE_PIECE, 6), item(ZOMBIE_GOLDEN_APPLE, 1), item(COMPRESSED_LIFE, 1), item(ZOMBIE_TRACE, 1));
            case "zombie_piece" ->
                    List.of(item(ZOMBIE_POWDER, 9));
            case "premium_zombie_steak" ->
                    Arrays.asList(item(ZOMBIE_GOLD, 8), item(ZOMBIE_STEAK, 1));
            case "zombiegod_fruit" ->
                    Arrays.asList(item(PREMIUM_ZOMBIE_STEAK, 5), item(ZOMBIE_GOLDEN_APPLE, 4));
            case "zombie_golden_apple" ->
                    Arrays.asList(item(ZOMBIE_GOLD, 8), item(ZOMBIE_APPLE, 1));
            case "zombie_gold" ->
                    List.of(item(ZOMBIE_SAND, 9));
            case "vaccine" ->
                    Arrays.asList(item(ZOMBIE_PIECE, 6), item(CORE_OF_PURIFICATION, 2), item(POWER_CRYSTAL, 1));
            case "creation_wand" ->
                    Arrays.asList(item(ZOMBIE_POWDER, 7), item(ZOMBIE_POWER, 1), item(CORE_OF_CREATION, 1));
            case "purification_staff" ->
                    Arrays.asList(item(ZOMBIE_POWDER, 7), item(ZOMBIE_POWER, 1), item(CORE_OF_PURIFICATION, 1));
            case "destruction_axe" ->
                    Arrays.asList(item(ZOMBIE_POWDER, 7), item(ZOMBIE_POWER, 1), item(CORE_OF_DESTRUCTION, 1));
            case "zombie_breaker" ->
                    Arrays.asList(item(ZOMBIE_PIECE, 4), item(CREATION_WAND, 1), item(PURIFICATION_STAFF, 1), item(DESTRUCTION_AXE, 1), item(ZOMBIE_TRACE, 1), item(ZOMBIE_POWER, 1));
            default -> List.of();
        };
    }
    public static void startrecipeTask() {
        Main.repeat(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (deadsMeal.containsKey(p) && !has(p, getRecipe("deads_meal"))) deadsMeal.remove(p);
                if (compressedLife.containsKey(p) && !has(p, getRecipe("compressed_life"))) compressedLife.remove(p);
                if (repairer.containsKey(p) && !has(p, getRecipe("repairer"))) repairer.remove(p);
                if (zombieApple.containsKey(p) && !has(p, getRecipe("zombie_apple"))) zombieApple.remove(p);
                if (infiniteLifeofZombie.containsKey(p) && !has(p, getRecipe("infinitelife_of_zombie"))) infiniteLifeofZombie.remove(p);
                if (zombiePiece.containsKey(p) && !has(p, getRecipe("zombie_piece"))) zombiePiece.remove(p);
                if (premiumZombieSteak.containsKey(p) && !has(p, getRecipe("premium_zombie_steak"))) premiumZombieSteak.remove(p);
                if (zombieGodFruit.containsKey(p) && !has(p, getRecipe("zombiegod_fruit"))) zombieGodFruit.remove(p);
                if (zombieGoldenApple.containsKey(p) && !has(p, getRecipe("zombie_golden_apple"))) zombieGoldenApple.remove(p);
                if (creatinonWand.containsKey(p) && !has(p, getRecipe("creation_wand"))) creatinonWand.remove(p);
                if (purificationStaff.containsKey(p) && !has(p, getRecipe("purification_staff"))) purificationStaff.remove(p);
                if (destructionAxe.containsKey(p) && !has(p, getRecipe("destruction_axe"))) destructionAxe.remove(p);
                if (zombieBreaker.containsKey(p) && !has(p, getRecipe("zombie_breaker"))) zombieBreaker.remove(p);
                if (vaccine.containsKey(p) && !has(p, getRecipe("vaccine"))) vaccine.remove(p);
                if (zombieGold.containsKey(p) && !has(p, getRecipe("zombie_gold"))) zombieGold.remove(p);
            }
        }, 1);
    }
}
