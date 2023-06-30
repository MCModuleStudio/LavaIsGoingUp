package org.mcmodule.lava.kit.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mcmodule.lava.kit.Kit;

public class Phoenix implements Kit {
    @Override
    public void process(Player player) {
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.EGG);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.GREEN + "你可以在死亡后立即原地复活一次",
                ChatColor.GREEN + "复活后获得半分钟抗火效果"
        };
    }

    @Override
    public String getName() {
        return "Phoenix";
    }
}
