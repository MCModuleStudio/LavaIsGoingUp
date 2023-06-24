package ml.mckuhei.lava;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class VoteManager {
	private final World world;

	public VoteManager(World world) {
		this.world = world;
		// Clear all votes
		for(VoteStatus status : VoteStatus.values()) status.votedPlayer.clear();
	}

	public VoteStatus getVoteStatus() {
		VoteStatus status = VoteStatus.PENDING;
		int voted = 0;
		for(VoteStatus s : VoteStatus.values()) {
			int i = s.votedPlayer.size();
			if(i > voted) {
				voted = i;
				status = s;
			}
		}
		double rate = voted / (double) world.getPlayers().size();
		return rate >= 0.5 ? status : VoteStatus.PENDING;
	}
	
	public void vote(VoteStatus status, Player player) {
		for(VoteStatus s : VoteStatus.values()) s.votedPlayer.remove(player);
		status.votedPlayer.add(player);
	}
	
	public enum VoteStatus {
		ACCEPTED,
		REJECTED,
		PENDING;
		
		HashSet<Player> votedPlayer = new HashSet<>();
	}
}
