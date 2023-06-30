package org.mcmodule.lava.kit.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mcmodule.lava.kit.Kit;

public class Velocity implements Kit {
    @Override
    public void process(Player player) {
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_CHESTPLATE);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.GREEN + "潜行时不会受到击退",
        };
    }

    @Override
    public String getName() {
        return "Velocity";
    }
}
