package ml.mckuhei.lava.kit.kits;

import ml.mckuhei.lava.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

public class Speed implements Kit {
    @Override
    public void process(Player player) {
        ItemStack speedPotion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) speedPotion.getItemMeta();
        potionMeta.setMainEffect(PotionEffectType.SPEED);
        potionMeta.addCustomEffect(PotionEffectType.SPEED.createEffect(3 * 60 * 20, 1), true);
        speedPotion.setItemMeta(potionMeta);
        ItemMeta itemMeta = speedPotion.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "黑色高级药水");
        speedPotion.setItemMeta(itemMeta);
        player.getInventory().addItem(speedPotion, speedPotion);
        player.setMaxHealth(18);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_BOOTS);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.GREEN + "游戏开始后获得2瓶三分钟的速度2药水",
                ChatColor.RED + "你的最大生命值只有18"
        };
    }

    @Override
    public String getName() {
        return "Speed";
    }
}
