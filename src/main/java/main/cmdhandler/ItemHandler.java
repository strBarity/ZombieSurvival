package main.cmdhandler;

import main.Main;
import main.timerhandler.InvOpenCDTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemHandler {
    /**
     * Main에서 save된 모든 커스텀 아이템을 표시함, OP 권한이 있다면
     * 아이템을 꺼내올 수 있고 없다면 구경만 가능
     */
    public static void onCommand(CommandSender commandSender) {
        Player p = (Player) commandSender;
        Inventory gui = Bukkit.createInventory(null, 54, Component.text("§8커스텀 아이템들"));
        int i = 0;
        for (ItemStack item : Main.customItems) {
            gui.setItem(i, item);
            i++;
        } p.openInventory(gui);
        InvOpenCDTimer.getInvOpenCooldoawn().put(p, 1);
    }
}
