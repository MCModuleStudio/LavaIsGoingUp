package ml.mckuhei.lava.kit.kits;

import ml.mckuhei.lava.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedMine implements Kit {
    @Override
    public void process(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 60 * 6, 1));
        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GOLD_PICKAXE);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.GREEN + "游戏开始时获得6分钟的急迫效果",
                ChatColor.GREEN + "游戏开始时获得一把铁镐（无任何特殊属性）"
        };
    }

    @Override
    public String getName() {
        return "SpeedMine";
    }
}
