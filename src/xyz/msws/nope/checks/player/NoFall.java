package xyz.msws.nope.checks.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

public class NoFall implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Map<UUID, Double> highest = new HashMap<>();

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		Location loc = player.getLocation();
		Vector vel = player.getVelocity();
		if (vel.getY() > 0) {
			if (highest.getOrDefault(player.getUniqueId(), 0d) < loc.getY())
				highest.put(player.getUniqueId(), loc.getY());
			return;
		}

		if (player.isOnGround()) {
			if (!highest.containsKey(player.getUniqueId()))
				return;
			double dist = highest.get(player.getUniqueId()) - loc.getY();
			double diff = (highest.get(player.getUniqueId()) - loc.getY()) - player.getFallDistance();
			highest.put(player.getUniqueId(), loc.getY());

			if (diff < .3)
				return;

			cp.flagHack(this, (int) Math.abs(diff * 20) + 5,
					String.format("Expected: &e%.3f&7\nReceived: &a%.3f", dist, player.getFallDistance()));
			return;
		}

	}

	@Override
	public String getCategory() {
		return "NoFall";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
