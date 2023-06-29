package org.mcmodule.lava.kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcmodule.lava.kit.kits.Speed;
import org.mcmodule.lava.kit.kits.SpeedMine;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class KitManager implements Listener {

    private final ConcurrentHashMap<Integer, Kit> kitMap; // 用于存储每个格子对应的Kit
    private final ConcurrentHashMap<Player,Kit> playerKits;
    private int index = 0;

    public KitManager(JavaPlugin plugin) {
        kitMap = new ConcurrentHashMap<>();
        playerKits = new ConcurrentHashMap<>();

        regKit(new SpeedMine());
        regKit(new Speed());

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void regKit(Kit k){
        this.kitMap.put(index,k);
        index++;
    }

    public void join(Player player){
        this.playerKits.put(player,kitMap.get(0));// 添加默认kit
    }

    public void apply(Player player){
        this.playerKits.get(player).process(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getWhoClicked().getOpenInventory().getTopInventory())) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        if (!kitMap.containsKey(event.getSlot())) return;
        event.setCancelled(true);
        Kit kit = kitMap.get(event.getSlot());
        player.sendMessage(ChatColor.GREEN + "你选择了加成效果：" + kit.getName() +", 将于游戏开始后生效");
        playerKits.put(player,kit);
    }

    public void openKitMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, "选择一个加成效果");
        for (int slot : kitMap.keySet()) {
            Kit kit = kitMap.get(slot);
            ItemStack icon = kit.getIcon();
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(kit.getName());
            meta.setLore(Arrays.asList(kit.getDescription()));
            icon.setItemMeta(meta);
            menu.setItem(slot, icon);
        }
        player.openInventory(menu);
    }
}
