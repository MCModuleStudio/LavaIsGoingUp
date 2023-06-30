package org.mcmodule.lava.kit.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mcmodule.lava.kit.Kit;

public class Tank implements Kit {
    @Override
    public void process(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 60 * 8964, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60 * 8964, 1));

        player.setMaxHealth(30);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_CHESTPLATE);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.GREEN + "你的最大生命值有30",
                ChatColor.GREEN + "游戏开始之后获得8000+秒的抗性提升2",
                ChatColor.RED + "但是获得8000+秒的缓慢1"
        };
    }

    @Override
    public String getName() {
        return "Tank";
    }
}
