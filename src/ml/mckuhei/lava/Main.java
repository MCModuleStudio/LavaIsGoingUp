package ml.mckuhei.lava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	private boolean started;
	private Location center;
	private int size = 64;
	private int delay = 15*20,counter;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			if(!started)
				return;
			if(counter-- < 0) {
				int xCenter = center.getBlockX(), zCenter = center.getBlockZ();
				int y = center.getBlockY();
				World world = center.getWorld();
				for(int x = xCenter - size; x < xCenter + size;x++)
					for(int z = zCenter - size; z < zCenter + size;z++) {
						//world.getBlockAt(x, y, z).setType(Material.LAVA);
						Block block = world.getBlockAt(x, y, z);
						Material type = block.getType();
						if(type == Material.AIR || type == Material.WATER || (!type.isSolid() && type != Material.CHEST)) 
							block.setType(Material.LAVA);
					}
				center = center.add(0, 1, 0);
				counter = delay;
				List<Player> players = world.getPlayers();
				boolean win = y >= world.getMaxHeight();
				for(Player player : players) {
					player.sendMessage(win ? "��ϲ����Ӯ�ˣ�" : String.format("�ҽ��߶�: %d", y));
					double playerY = player.getLocation().getBlockY();
					if(playerY==y+1&&!win) {
						player.sendMessage("��о����ذ��е��̽�...");
					}
					if(win)
						player.setGameMode(GameMode.SPECTATOR);
				}
				if(win)
					stop();
			}
		},0,1);
	}
	
	public void onDisable() {
		stop();
	}
	
	public void init(Location center) {
		Objects.requireNonNull(center).setY(1);
		this.center = center.clone();
		World world = center.getWorld();
		WorldBorder border = world.getWorldBorder();
		border.setCenter(center.toBlockLocation());
		border.setSize(size * 2);
		List<Player> players = world.getPlayers();
		Random rand = new Random();
		for(Player player : players) {
			int xOff = rand.nextInt(size / 2), zOff = rand.nextInt(size / 2);
			if(rand.nextBoolean()) {
				xOff *= -1;
			}
			if(rand.nextBoolean()) {
				zOff *= -1;
			}
			Location loc = center.add(xOff + .5, 0, zOff + .5);
			loc.setY(world.getHighestBlockYAt(loc)+1);
			player.teleport(loc);
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
		}
	}
	public void start() {
		if(this.center == null) {
			throw new RuntimeException("δ��ʼ��");
		}
		this.started = true;
		this.counter = this.delay;
	}
	
	public void pause() {
		this.started = false;
	}
	
	public void stop() {
		pause();
		if(this.center != null) {
			WorldBorder border = this.center.getWorld().getWorldBorder();
			border.setCenter(new Location(this.center.getWorld(),0,0,0));
			border.setSize(Integer.MAX_VALUE);
		}
		this.center = null;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equals("lava")) {
			if(args.length==0) {
				sender.sendMessage("============�ҽ�����============");
				sender.sendMessage("����:Minecraftku_hei");
				sender.sendMessage("/lava init              - ��ʼ��");
				sender.sendMessage("/lava start           - ����");
				sender.sendMessage("/lava pause          - ��ͣ");
				sender.sendMessage("/lava stop            - ֹͣ");
				sender.sendMessage("/lava delay <tick>   - �ҽ��������");
				sender.sendMessage("/lava size <����>    - ���ô�С");
				return true;
			}
			if(!sender.isOp()) {
				sender.sendMessage(ChatColor.RED+"����Ҫop��ִ��������");
				return true;
			}
			switch(args[0]) {
			case "init": {
				if(!(sender instanceof Player)) {
					sender.sendMessage("�㲻����");
					break;
				}
				init(((Player)sender).getLocation());
				sender.sendMessage("��ʼ���ɹ�");
				break;
			}
			case "start": {
				try {
					start();
				} catch(RuntimeException e) {
					sender.sendMessage(ChatColor.RED+"����ʧ�ܣ�ԭ��:"+e.getMessage());
					break;
				}
				sender.sendMessage("������");
				break;
			}
			case "pause": {
				pause();
				sender.sendMessage("����ͣ");
				break;
			}
			case "stop": {
				stop();
				sender.sendMessage("��ֹͣ");
				break;
			}
			case "delay": {
				delay = Integer.valueOf(args[1]);
				sender.sendMessage("���óɹ�");
				break;
			}
			case "size": {
				size = Integer.valueOf(args[1]);
				sender.sendMessage("���óɹ�");
				break;
			}
			}
			return true;
		}
		return false;  // WTF???
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(command.getName().equals("lava")) {
			if(args.length <= 1) {
				List<String> ret = new ArrayList<>();
				for(String str : new String[] {"init", "start", "pause", "stop", "delay", "size"})
					if(str.startsWith(args[0]))
						ret.add(str);
				return ret;
			}
			if(args[0].equals("delay") && (args.length == 1 || args[1].isEmpty()))
				return Arrays.asList("240");
			if(args[0].equals("size") && (args.length == 1 || args[1].isEmpty()))
				return Arrays.asList("64");
		}
		return new ArrayList<>();
	}
	
	@EventHandler
	public void onPlayerDead(PlayerDeathEvent event) {
		event.getEntity().sendMessage("�����ˣ�");
		event.getEntity().setHealth(20);
		event.getEntity().setFoodLevel(20);
		event.getEntity().setGameMode(GameMode.SPECTATOR);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(center != null) {
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
			event.getPlayer().sendMessage(ChatColor.RED+"��Ϸ�ѿ�ʼ�����������;���߻�����Ϸ�տ�ʼ�����ҹ���Ա�������Ϸģʽ�Ļ����棡");
			event.getPlayer().teleport(center);
		}
	}
}
