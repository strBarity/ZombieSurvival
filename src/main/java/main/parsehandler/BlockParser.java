package main.parsehandler;

import org.bukkit.Material;

public class BlockParser {
    public static boolean isBlacklistedBlock(Material block) {
        return block.equals(Material.FURNACE) || block.equals(Material.CHEST) || block.equals(Material.TRAPPED_CHEST) || block.equals(Material.BEACON) || block.equals(Material.CAMPFIRE) || block.equals(Material.BARREL) || block.equals(Material.CAULDRON) || block.equals(Material.BREWING_STAND) || block.equals(Material.BEEHIVE) || block.equals(Material.BIRCH_BUTTON) || block.equals(Material.REDSTONE);
    }
}
