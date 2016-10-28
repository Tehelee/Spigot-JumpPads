package com.tehelee.jumpPads;

import java.util.Hashtable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class JumpPadsListener implements Listener
{
	private static Hashtable<Player, Push> pushing = new Hashtable<Player, Push>();
	
	private static BukkitRunnable runnablePusher = new BukkitRunnable()
	{
		@Override
		public void run()
		{
			for (Player p : pushing.keySet())
			{
				Push push = pushing.get(p);
				
				if (push.duration > 0)
				{
					p.setVelocity(push.velocity);
				}
				else if (push.duration <= -7)
				{
					pushing.remove(p);
					continue;
				}
				
				push.duration--;
				
				pushing.put(p, push);
			}
		}
	};
	
	public static void StartTasks() 
	{
		runnablePusher.runTaskTimer(Main.instance, 0, 1);
	}
	
	@EventHandler
	public void onWorldLoadEvent(WorldLoadEvent e)
	{
		JumpPad.populateRegister();
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		
		if (pushing.containsKey(p)) pushing.remove(p);
		
		CmdJumpPads.playerLeave(p);
	}
	
	@EventHandler
	public void onFall(EntityDamageEvent e)
	{
		Entity ent = e.getEntity();
		if ((e.getCause() == DamageCause.FALL) && (ent instanceof Player))
		{
			Player p = (Player)ent;
			
			if (pushing.containsKey(p))
			{
				e.setDamage(0);
				e.setCancelled(true);
				pushing.remove(p);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Block b = e.getBlock();
		Material m = b.getType();
		if (JumpPad.isPressurePlate(m))
		{
			JumpPad pad = JumpPad.getJumpPad(b.getLocation());
			if (pad != null) JumpPad.removeJumpPad(pad);
		}
	}
	
	@EventHandler
	public void onPressurePlate(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.PHYSICAL))
		{
			Block b = e.getClickedBlock();
			Material m = b.getType();
			if(JumpPad.isPressurePlate(m))
			{
				JumpPad pad = JumpPad.getJumpPad(b.getLocation());
				
				if (pad != null)
				{
					Player p = e.getPlayer();
					
					if (!pushing.containsKey(p))
					{
						Location loc = p.getLocation();
						
						double height = pad.getHeight() / 19.5;
						double length = pad.getLength() / 5;
						
						ForceDirection forceDirection = pad.getForceDirection();
						if (forceDirection != ForceDirection.NONE)
						{
							loc = pad.getLocation();
							loc.setYaw(forceDirection.getYaw());
							loc.setPitch(0);
							
							if (forceDirection == ForceDirection.UP)
							{
								Vector v = loc.getDirection();
								v.setX(0);
								v.setZ(0);
								loc.setDirection(v);
							}
						}
						
						pushing.put(p, new Push(loc.getDirection().setY(height).multiply(length), pad, pad.getDuration()));
						
						if (pad.getSound() != null)
							p.playSound(loc, pad.getSound(), 100, 100);
						
						if (pad.getEffect() != null)
						{
							CraftWorld world = (CraftWorld)loc.getWorld();
							world.playEffect(loc, pad.getEffect(), 4);
						}
					}
					
					e.setCancelled(true);
				}
			}
		}
	}
}
