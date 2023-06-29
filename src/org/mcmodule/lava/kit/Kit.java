package org.mcmodule.lava.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Kit {
    void process(Player player);
    ItemStack getIcon();

    String[] getDescription();

    String getName();
}
