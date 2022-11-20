package eventhandler;

import main.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "purifiacation_staff"));
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "creation_wand"));
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "destruction_axe"));
        p.discoverRecipe(new NamespacedKey(Main.getPlugin(Main.class), "zombie_breaker"));
    }
}
