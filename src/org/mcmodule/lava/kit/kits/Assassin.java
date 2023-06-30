package org.mcmodule.lava.kit.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mcmodule.lava.kit.Kit;

public class Assassin implements Kit {
    @Override
    public void process(Player player) {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Assassin Sword");
        sword.setItemMeta(meta);
        player.getInventory().addItem(sword);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_SWORD);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.GREEN + "游戏开始之后获得一把铁剑，默认无附魔",
                ChatColor.GREEN + "你将在击杀玩家之后获得1分钟速度4",
                ChatColor.RED + "你将无法在击杀玩家之后获得金苹果",
        };
    }

    @Override
    public String getName() {
        return "Assassin";
    }
}
