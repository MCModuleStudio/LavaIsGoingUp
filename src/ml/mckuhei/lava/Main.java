package ml.mckuhei.lava;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.plugin.java.JavaPlugin;

import ml.mckuhei.lava.VoteManager.VoteStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Main extends JavaPlugin implements Listener {
	private static final int DEFAULT_DELAY = 30 * 20,
							 VOTE_DELAY    = 40 * 20;
	private static final List<Material> blackList = new ArrayList<>();
	private boolean started;
	private Location center;
	private int size = 64;
	private int delay = DEFAULT_DELAY, counter, voteCounter;
	private VoteManager voteManager;
	
	static {
		Field[] fields = Material.class.getDeclaredFields();
		for(Field field : fields) {
			if(field.getName().contains("DOOR") || field.getName().contains("FENCE") || field.getName().contains("SIGN") || field.getName().contains("PRESSURE_PLATE") || field.getName().contains("GLASS")) {
				try {
					blackList.add((Material) field.get(null));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
	private void setLava(World world, int x, int y, int z) {
		//world.getBlockAt(x, y, z).setType(Material.LAVA);
		Block block = world.getBlockAt(x, y, z);
		Material type = block.getType();
		if(shouldReplace(type)) 
			block.setType(Material.LAVA);
	}
	
	private boolean shouldReplace(Material type) {
		boolean bool = type == Material.AIR || type == Material.WATER || (!type.isSolid() && type != Material.CHEST);	
		if(!bool)
			bool = blackList.contains(type);
		return bool;
	}
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().runTaskTimer(this, this::mainloop, 0, 1);
	}
	
	private void mainloop() {
		if(!started)
			return;
		World world = center.getWorld();
		List<Player> players = world.getPlayers();
		int y = center.getBlockY();
		if(counter-- < 0) {
			int xCenter = center.getBlockX(), zCenter = center.getBlockZ();
			for(int x = xCenter - size; x < xCenter + size; x++)
				for(int z = zCenter - size; z < zCenter + size; z++)
					setLava(world, x, y, z);
			center.add(0, 1, 0);
			counter = delay;
			if(y == 63) world.setPVP(true);
			boolean win = y >= world.getMaxHeight();
			for(Player player : players) {
				boolean specatorMode = player.getGameMode() == GameMode.SPECTATOR;
				player.sendMessage(win && !specatorMode ? "恭喜！您赢了！" : String.format("岩浆高度: %d", y));
				if(y == 63) {
					player.sendMessage("PVP已启用");
				}
				if(specatorMode) continue;
				int playerY = player.getLocation().getBlockY();
				if(playerY == y + 1 && !win) {
					player.sendMessage("你感觉到地板有点烫脚...");
				}
				if(win)
					player.setGameMode(GameMode.SPECTATOR);
			}
			if(win)
				stop();
		}
		if(y % 64 == 0) {
			this.voteManager = new VoteManager(world);
			this.voteCounter = VOTE_DELAY;
		}
		if(this.voteManager != null) {
			if(this.voteCounter-- < 0) {
				if(checkVoteStatus(players)) {
					this.voteManager = null;
				} else {
					for(Player player : players) {
						sendVoteToPlayer(player);
					}
					this.voteCounter = VOTE_DELAY;
				}
			}
		}
	}
	
	private boolean checkVoteStatus(List<Player> players) {
		VoteStatus status = this.voteManager.getVoteStatus();
		switch(status) {
		case ACCEPTED:
			this.delay /= 2;
		case REJECTED:
			BaseComponent[] comp = new ComponentBuilder("投票结果：").append(status == VoteStatus.ACCEPTED ? "同意" : "拒绝").color(status == VoteStatus.ACCEPTED ? ChatColor.GREEN : ChatColor.RED).create();
			for(Player player : players) {
				player.sendMessage(comp);
			}
			return true;
		case PENDING:
		default:
			return false;
		}
	}

	private void sendVoteToPlayer(Player player) {
		player.sendMessage(String.format("是否将岩浆上升间隔设置为%d？", delay / 2));
		player.sendMessage(
				new ComponentBuilder("[同意]").color(ChatColor.GREEN).event(new ClickEvent(Action.RUN_COMMAND, "/lavaisgoingup:vote accept")).append(" ").reset()
				             .append("[拒绝]").color(ChatColor.RED  ).event(new ClickEvent(Action.RUN_COMMAND, "/lavaisgoingup:vote reject")).create()
				);
	}
	
	public void onDisable() {
		stop();
	}
	
	public void init(Location center) {
		Objects.requireNonNull(center).setY(0);
		center = new Location(center.getWorld(), center.getBlockX(), center.getBlockY(), center.getBlockZ());
		this.center = center.clone();
		World world = center.getWorld();
		world.setPVP(false);
		WorldBorder border = world.getWorldBorder();
		border.setCenter(center);
		border.setSize(size * 2);
		List<Player> players = world.getPlayers();
		Random rand = new Random();
		for(Player player : players) {
			int xOff = rand.nextInt(size) - rand.nextInt(size),
				zOff = rand.nextInt(size) - rand.nextInt(size);
			Location loc = center.clone().add(xOff + .5, 0, zOff + .5);
			loc.setY(world.getHighestBlockYAt(loc) + 1);
			player.teleport(loc);
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.setHealth(20D);
			player.setFoodLevel(20);
			player.setLevel(0);
			player.setTotalExperience(0);
		}
	}
	public void start() {
		if(this.center == null) {
			throw new RuntimeException("未初始化");
		}
		this.started = true;
		this.counter = this.delay = DEFAULT_DELAY;
	}
	
	public void pause() {
		this.started = false;
	}
	
	public void stop() {
		pause();
		if(this.center != null) {
			WorldBorder border = this.center.getWorld().getWorldBorder();
			border.setCenter(new Location(this.center.getWorld(), 0, 0, 0));
			border.setSize(Integer.MAX_VALUE);
		}
		this.center = null;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equals("lava")) {
			if(args.length==0) {
				sender.sendMessage("============岩浆上升============");
				sender.sendMessage("作者:Minecraftku_hei");
				sender.sendMessage("/lava init              - 初始化");
				sender.sendMessage("/lava start           - 启动");
				sender.sendMessage("/lava pause          - 暂停");
				sender.sendMessage("/lava stop            - 停止");
				sender.sendMessage("/lava delay <tick>   - 岩浆上升间隔");
				sender.sendMessage("/lava size <方块>    - 设置大小");
				return true;
			}
			if(!sender.isOp()) {
				sender.sendMessage(ChatColor.RED+"你需要op来执行这命令");
				return true;
			}
			switch(args[0]) {
			case "init": {
				if(!(sender instanceof Player)) {
					sender.sendMessage("你不是人");
					break;
				}
				init(((Player)sender).getLocation());
				sender.sendMessage("初始化成功");
				break;
			}
			case "start": {
				try {
					start();
				} catch(RuntimeException e) {
					sender.sendMessage(ChatColor.RED+"启动失败！原因:"+e.getMessage());
					break;
				}
				sender.sendMessage("已启动");
				break;
			}
			case "pause": {
				pause();
				sender.sendMessage("已暂停");
				break;
			}
			case "stop": {
				stop();
				sender.sendMessage("已停止");
				break;
			}
			case "delay": {
				counter = Math.min(delay = Integer.valueOf(args[1]), counter);
				sender.sendMessage("设置成功");
				break;
			}
			case "size": {
				size = Integer.valueOf(args[1]);
				sender.sendMessage("设置成功");
				break;
			}
			}
			return true;
		}
		if(cmd.getName().equals("vote")) {
			if(!(sender instanceof Player) || !this.started) {
				return true;
			}
			VoteStatus status;
			switch(args[0]) {
			case "accept":
				status = VoteStatus.ACCEPTED;
				break;
			case "reject":
				status = VoteStatus.REJECTED;
				break;
			default:
				return false;
			}
			if(this.voteManager != null) {
				this.voteManager.vote(status, (Player) sender);
				List<Player> players = center.getWorld().getPlayers();
				BaseComponent[] comp = new ComponentBuilder(sender.getName()).append("选择了").append(status == VoteStatus.ACCEPTED ? "同意" : "拒绝").color(status == VoteStatus.ACCEPTED ? ChatColor.GREEN : ChatColor.RED).create();
				for(Player player : players) {
					player.sendMessage(comp);
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
				return Arrays.asList(String.valueOf(DEFAULT_DELAY));
			if(args[0].equals("size") && (args.length == 1 || args[1].isEmpty()))
				return Arrays.asList("64");
		}
		return new ArrayList<>();
	}
	
	@EventHandler
	public void onPlayerDead(PlayerDeathEvent event) {
		onPlayerDead(event.getEntity());
	}
	
//	@EventHandler
//	public void onEntityDamanged(EntityDamageEvent event) {
//		if(event.getEntityType() == EntityType.PLAYER) {
//			CraftPlayer player = ((CraftPlayer) event.getEntity());
//		}
//	}
	
	public void onPlayerDead(Player player) {
		player.sendMessage("你死了！");
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setGameMode(GameMode.SPECTATOR);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(center != null) {
			Player player = event.getPlayer();
			Location loc = player.getLocation().subtract(center);
			int dist = Math.max(Math.abs(loc.getBlockX()), Math.abs(loc.getBlockZ()));
			if(dist <= size && loc.getBlockY() > 0) return;
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(ChatColor.RED + "游戏已开始，如果你是中途掉线或者游戏刚开始，请找管理员将你的游戏模式改回生存！");
			player.teleport(center);
		}
	}
}
