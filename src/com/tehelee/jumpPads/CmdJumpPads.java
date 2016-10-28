package com.tehelee.jumpPads;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class CmdJumpPads implements CommandExecutor
{
	private static Hashtable<Player, JumpPad> clipboard = new Hashtable<Player, JumpPad>();
	
	public static void playerLeave(Player p)
	{
		if (clipboard.containsKey(p)) clipboard.remove(p);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length > 0)
		{
			Player p = null;
			if (sender instanceof Player) p = (Player)sender;
			
			Block b = null;
			
			if (p != null) b = getPlayerBlockTarget(p, false);
			
			JumpPad target = null;
			
			if ((b != null) && JumpPad.isPressurePlate(b.getType()))
				target = JumpPad.getJumpPad(b.getLocation());
			
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
			{
				commandHelp(sender, label);
			}
			else if (args[0].equalsIgnoreCase("reload"))
			{
				if ((p == null) || p.hasPermission(HelpText.PermissionPrefix + "reload"))
				{
					Reload.reload(p);
				}
				else
				{
					Main.message(p, HelpText.MissingPermission);
				}
			}
			else if (args[0].equalsIgnoreCase("create") && (p != null))
			{
				if (p.hasPermission(HelpText.PermissionPrefix + "create"))
				{
					if (b != null)
					{
						if (JumpPad.isPressurePlate(b.getType()))
						{
							Location loc = b.getLocation();
							
							JumpPad pad = JumpPad.getJumpPad(loc);
							
							if (pad == null)
							{
								target = JumpPad.createJumpPad(loc);
								
								displayInfo(sender, target, "§eCreated");
								Main.message(null, String.format(HelpText.LogCreation, p.getName(), target.getCompressedLocation()));
							}
							else
							{
								Main.message(sender, HelpText.ExistingJumpPad);
							}
						}
					}
					else
					{
						Main.message(p, String.format(HelpText.NeedJumpPad, label));
					}
				}
				else
				{
					Main.message(p, HelpText.MissingPermission);
				}
			}
			else if (args[0].equalsIgnoreCase("remove") && (p != null))
			{
				if (target != null)
				{
					JumpPad.removeJumpPad(target);
					Main.message(p, "§8§oJump Pad removed.");
				}
				else
				{
					Main.message(p, String.format(HelpText.NeedJumpPad, label));
				}
			}
			else if (args[0].equalsIgnoreCase("copy") && (p != null))
			{
				if (target != null)
				{
					clipboard.put(p, target);
					Main.message(p, "§8§oJump Pad copied.");
				}
				else
				{
					Main.message(p, String.format(HelpText.NeedJumpPad, label));
				}
			}
			else if (args[0].equalsIgnoreCase("paste") && (p != null))
			{
				if (p.hasPermission(HelpText.PermissionPrefix + "create"))
				{
					if (clipboard.containsKey(p))
					{
						if (b != null)
						{
							if (JumpPad.isPressurePlate(b.getType()))
							{
								Location loc = b.getLocation();
								
								JumpPad pad = JumpPad.getJumpPad(loc);
								
								if (pad == null)
								{
									pad = JumpPad.createJumpPad(loc);
									
									Main.message(null, String.format(HelpText.LogCreation, p.getName(), pad.getCompressedLocation()));
								}
								
								pad.clone(clipboard.get(p));
								
								displayInfo(sender, pad, "§eCloned");
							}
							else
							{
								Main.message(p, String.format(HelpText.NeedJumpPad, label));
							}
						}
						else
						{
							Main.message(p, String.format(HelpText.NeedJumpPad, label));
						}
					}
					else
					{
						Main.message(p, String.format(HelpText.CopyHelp, label));
					}
				}
				else
				{
					Main.message(p, HelpText.MissingPermission);
				}
			}
			else if (args[0].equalsIgnoreCase("info") && (p != null))
			{
				if (target != null)
				{
					displayInfo(sender, target, "§eInfo");
				}
				else
				{
					Main.message(p, String.format(HelpText.NeedJumpPad, label));
				}
			}
			else if (args[0].equalsIgnoreCase("list") && (p != null))
			{
				if (args.length > 1)
				{
					if (args[1].equalsIgnoreCase("sounds"))
					{
						Main.message(sender, "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html", true);
					}
					else if (args[1].equalsIgnoreCase("effects"))
					{
						Main.message(sender, "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Effect.html", true);
					}
					else
					{
						Main.message(p, "/" + label + " list [sounds | effects]");
					}
				}
				else
				{
					Main.message(p, "/" + label + " list [sounds | effects]");
				}
			}
			else if (args[0].equalsIgnoreCase("set") && (p != null))
			{
				if (p.hasPermission(HelpText.PermissionPrefix + "create"))
				{
					if (target == null)
					{
						Main.message(p, String.format(HelpText.NeedJumpPad, label));
						
						Main.message(p, String.format(HelpText.CreateHelp, label));
					}
					else if (args.length > 1)
					{
						if (args[1].equalsIgnoreCase("speed"))
						{
							if (args.length > 2)
							{
								double length = Double.NaN;
								try
								{
									length = Double.parseDouble(args[2]);
								}
								catch(NumberFormatException ex) { }
								
								if (!Double.isNaN(length))
								{
									target.setLength(length, true);
									
									displayInfo(sender, target, "§eUpdated §aSpeed");
								}
								else
								{
									Main.message(p, HelpText.InvalidNumber);
								}
							}
							else
							{
								Main.message(p, "/" + label + " set speed <length>"); 
							}
						}
						else if (args[1].equalsIgnoreCase("height"))
						{
							if (args.length > 2)
							{
								double length = Double.NaN;
								try
								{
									length = Double.parseDouble(args[2]);
								}
								catch(NumberFormatException ex) { }
								
								if (!Double.isNaN(length))
								{
									target.setHeight(length, true);
									
									displayInfo(sender, target, "§eUpdated §bHeight");
								}
								else
								{
									Main.message(p, HelpText.InvalidNumber);
								}
							}
							else
							{
								Main.message(p, "/" + label + " set height <length>"); 
							}
						}
						else if (args[1].equalsIgnoreCase("duration"))
						{
							if (args.length > 2)
							{
								int duration = 1;
								try
								{
									duration = Integer.parseInt(args[2]);
								}
								catch(NumberFormatException ex) { }
								
								target.setDuration(duration, true);
								
								displayInfo(sender, target, "§eUpdated §cDuration");
							}
						}
						else if (args[1].equalsIgnoreCase("sound"))
						{
							if (args.length > 2)
							{
								target.setSound(args[2], true);
								
								displayInfo(sender, target, "§eUpdated §dSound");
							}
							else
							{
								Main.message(p, "/" + label + " set sound <name>\n§cTo see a list of avalaibale sounds, type:\n§f/"  + label + " list sounds");
							}
						}
						else if (args[1].equalsIgnoreCase("effect"))
						{
							if (args.length > 2)
							{
								target.setEffect(args[2], true);
								
								displayInfo(sender, target, "§eUpdated §6Effect");
							}
							else
							{
								Main.message(p, "/" + label + " set effect <name>\n§cTo see a list of avalaibale effects, type:\n§f/"  + label + " list effects");
							}
						}
						else if (args[1].equalsIgnoreCase("direction") || args[1].equalsIgnoreCase("force") || args[1].equalsIgnoreCase("forcedirection"))
						{
							if (args.length > 2)
							{
								target.setForceDirection(args[2], true);
								
								displayInfo(sender, target, "§eUpdated §9Force Direction");
							}
							else
							{
								Main.message(p, "/" + label + " set direction [NONE | NORTH | EAST | SOUTH | WEST]"); 
							}
						}
						else
						{
							Main.message(p, "/" + label + " set [speed | height | duration | sound | effect | direction]");
						}
					}
					else
					{
						Main.message(p, "/" + label + " set [speed | height | duration | sound | effect | direction]");
					}
				}
				else
				{
					Main.message(p, HelpText.MissingPermission);
				}
			}
			else
			{
				commandHelp(sender, label);
			}
		}
		else
		{
			commandHelp(sender, label);
		}
		
		return true;
	}
	
	private static void commandHelp(CommandSender sender, String label)
	{	
		List<String> perms = new ArrayList<String>();
		
		if (sender.hasPermission(HelpText.PermissionPrefix + "reload")) perms.add("reload");
		if (sender.hasPermission(HelpText.PermissionPrefix + "create"))
		{
			perms.add("create");
			perms.add("remove");
			perms.add("set");
			perms.add("copy");
			perms.add("paste");
			perms.add("info");
			perms.add("list");
		}
		
		if (perms.size() == 0)
		{
			Main.message(sender, HelpText.MissingPermission);
			return;
		}
		
		String cmds = perms.get(0);
		
		for (int i = 1; i < perms.size(); i++)
		{
			cmds += " | " + perms.get(i);
		}
		
		if (perms.size() > 1)
		{
			cmds = "[" + cmds + "]";
		}
		
		Main.message(sender, "/" + label + " " + cmds);
	}
	
	private static void displayInfo(CommandSender sender, JumpPad target, String mode)
	{	
		String sound = "NONE", effect = "NONE";

		Sound s = target.getSound();
		Effect e = target.getEffect();
		
		if (s != null) sound = s.name();
		
		if (e != null) effect = e.name();
		
		Main.message(sender, String.format(HelpText.InfoFormat, mode, target.getLength(), target.getHeight(), target.getDuration(), sound, effect, target.getForceDirection()));
	}
	
	public Block getPlayerBlockTarget(Player player, boolean getLastAirBlock)
	{
		BlockIterator iter = new BlockIterator(player, 10);
		
		Block lastBlock = null, nextBlock = null;
		
		while (iter.hasNext())
		{
			lastBlock = nextBlock;
			nextBlock = iter.next();
			
			if (nextBlock.getType() != Material.AIR)
			{
				if (getLastAirBlock)
				{
					if (lastBlock == null)
					{
						return player.getLocation().getBlock();
					}
					else
					{
						return lastBlock;
					}
				}
				else
				{
					return nextBlock;
				}
			}
		}
		
		return null;
	}
}
