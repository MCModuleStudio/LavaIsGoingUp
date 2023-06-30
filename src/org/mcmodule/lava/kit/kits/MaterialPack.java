package org.mcmodule.lava.kit.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mcmodule.lava.kit.Kit;

public class MaterialPack implements Kit {
    @Override
    public void process(Player player) {
        player.getInventory().addItem(new ItemStack(Material.IRON_INGOT,10));
        player.getInventory().addItem(new ItemStack(Material.WOOD,4));
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_INGOT);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.GREEN + "游戏开始时获得10颗铁锭",
                ChatColor.GREEN + "游戏开始时获得4个木头",
        };
    }

    @Override
    public String getName() {
        return "MaterialPack";
    }
}
