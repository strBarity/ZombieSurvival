package eventhandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gamehandler.GameHandler;
import main.Main;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EventListener implements Listener {
    private static final HashMap<Player, Integer> taskId = new HashMap<>();
    private static final HashMap<Player, Integer> npcId = new HashMap<>();

    public static HashMap<Player, Integer> getNpcId() {
        return npcId;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        try {
            if (e.getClickedBlock() != null && !e.getClickedBlock().getType().equals(Material.CRAFTING_TABLE))
                e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            if (e.getView().title().equals(Component.text("§2게임 시작 메뉴"))) {
                e.setCancelled(true);
                if (e.getSlot() == 10 || e.getSlot() == 12 || e.getSlot() == 14 || e.getSlot() == 16) {
                    p.sendMessage("§aComing soon!");
                    p.closeInventory();
                }
            }
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try {
            Player p = e.getPlayer();
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            p.teleport(new Location(p.getWorld(), 240.5, 224.0, 208.5));
            registerTask(p);
            registerNpc(p);
            discoverRecipes(p);
            Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(Double.MAX_VALUE);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        try {
            Player p = e.getPlayer();
            Bukkit.getScheduler().cancelTask(taskId.get(p));
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        try {
            if (!GameHandler.gameStarted) e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        try {
            if (!GameHandler.gameStarted) e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        try {
            if (!GameHandler.gameStarted) {
                e.setCancelled(true);
                e.getEntity().setFoodLevel(20);
                e.getEntity().setSaturation(0);
            }
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }
    @EventHandler
    public void onRecipe(PlayerRecipeDiscoverEvent e) {
        try {
            if (e.getRecipe().getKey().contains("custom")) e.setCancelled(true);
        } catch (Exception e1) {
            Main.printException(e1);
        }
    }

    public static void registerTask(Player p) {
        try {
            int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                try {
                    if (!GameHandler.gameStarted && p.getLocation().getY() < 219 && p.getGameMode().equals(GameMode.SURVIVAL))
                        p.teleport(new Location(p.getWorld(), 240.5, 224.0, 208.5));
                } catch (Exception e1) {
                    Main.printException(e1);
                }
            }, 0, 1);
            taskId.put(p, i);
        } catch (Exception e2) {
            Main.printException(e2);
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc)), 1L);
            connection.send(new ClientboundRemoveEntitiesPacket());
            npcId.put(p, npc.getBukkitEntity().getEntityId());
        } catch (Exception e) {
            Main.printException(e);
        }
    }
    public static void discoverRecipes(Player p) {
        p.undiscoverRecipes(Main.recipeKeys);
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "custom_purifiacation_staff"));
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "custom_creation_wand"));
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "custom_destruction_axe"));
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "custom_zombie_breaker"));
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "custom_zombie_piece"));
    }
}
