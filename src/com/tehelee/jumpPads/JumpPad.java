package com.tehelee.jumpPads;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class JumpPad
{
	private static ConfigurationSection defaults;
	
	private static Hashtable<String,JumpPad> register = new Hashtable<String,JumpPad>();
	
	public static void writeDefaultConfig()
	{
		if (!Main.config.isConfigurationSection("Defaults"))
		{
			defaults = Main.config.createSection("Defaults");
			
			defaults.set("JumpHeight", 10.0);
			defaults.set("JumpLength", 7.0);
			defaults.set("JumpDuration", 1);
			defaults.set("Sound", "ENTITY_GHAST_SHOOT");
			defaults.set("Effect", "SMOKE");
		}
		else
		{
			defaults = Main.config.getConfigurationSection("Defaults");
		}
	}
	
	public static void populateRegister()
	{
		File file = new File(Main.instance.getDataFolder(), "//" + "jump_pads.yml");
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		
		for (String compressedLocation : yaml.getKeys(false))
		{
			ConfigurationSection config;
			String id = compressedLocation;
			
			if (yaml.isConfigurationSection(id))
				config = yaml.getConfigurationSection(id);
			else
				continue;
			
			if (register.containsKey(compressedLocation)) continue;
			
			Location loc = extractLocation(config.getString("Location"));
			
			if ((loc == null) || (loc.getWorld() == null)) continue;
			
			JumpPad pad = new JumpPad(loc);
			
			pad.setHeight(config.getDouble("JumpHeight"));
			pad.setLength(config.getDouble("JumpLength"));
			pad.setDuration(config.getInt("JumpDuration"));
			pad.setSound(config.getString("Sound"));
			pad.setEffect(config.getString("Effect"));
			pad.setForceDirection(config.getString("ForceDirection"));
			
			register.put(compressedLocation, pad);
		}
	}
	
	public static String compressLocation(Location loc)
	{
		return String.format("%1$s %2$.0f %3$.0f %4$.0f", loc.getWorld().getUID().toString(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public static Location extractLocation(String str)
	{
		String[] split = str.split(" ");
		
		if (split.length != 4) return null;
		
		World world = Main.server.getWorld(UUID.fromString(split[0]));
		
		if (world == null) return null;
		
		Location loc = new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])); 
		
		return loc;
	}
	
	public static Location getBlockLocation(Location original)
	{
		return new Location(original.getWorld(), original.getBlockX(), original.getBlockY(), original.getBlockZ());
	}
	
	public static JumpPad createJumpPad(Location loc)
	{
		JumpPad existing = null;
		
		Location blockLoc = getBlockLocation(loc);
		
		String compressed = compressLocation(blockLoc);
		
		if (register.containsKey(compressed))
			existing = register.get(compressed);
		
		JumpPad pad = (existing != null) ? existing : new JumpPad(blockLoc);
		
		pad.save();
		
		register.put(compressed, pad);
		
		return pad;
	}
	
	public static void removeJumpPad(JumpPad pad)
	{	
		if (register.containsKey(pad.compressedLocation))
		{
			register.remove(pad.compressedLocation);
			
			File file = new File(Main.instance.getDataFolder(), "//" + "jump_pads.yml");
			FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
			
			yaml.set(pad.compressedLocation, null);
			
			try
			{
				yaml.save(file);
			}
			catch (IOException e)
			{
				Main.message(null, "Failed to remove jump pad @: " + pad.compressedLocation);
			}
		}
	}
	
	public static JumpPad getJumpPad(Location loc)
	{	
		String compressedLocation = compressLocation(getBlockLocation(loc));
		
		return register.containsKey(compressedLocation) ? register.get(compressedLocation) : null;
	}
	
	public static boolean isPressurePlate(Material m)
	{
		switch(m)
		{
			case WOOD_PLATE:
			case STONE_PLATE:
			case IRON_PLATE:
			case GOLD_PLATE:
				return true;
			default:
				return false;
		}
	}
	
	private double height, length;
	private int duration;
	private Sound sound;
	private Effect effect;
	private ForceDirection forceDirection = ForceDirection.NONE;
	
	private Location location;
	private String compressedLocation;
	
	public JumpPad(Location location)
	{
		height = defaults.getDouble("JumpHeight");
		length = defaults.getDouble("JumpLength");
		duration = defaults.getInt("JumpDuration");
		setSound(defaults.getString("Sound"));
		setEffect(defaults.getString("Effect"));
		
		this.location = location;
		compressedLocation = compressLocation(location);
	}
	
	public JumpPad(String compressedLocation)
	{
		height = defaults.getDouble("JumpHeight");
		length = defaults.getDouble("JumpLength");
		duration = defaults.getInt("JumpDuration");
		setSound(defaults.getString("Sound"));
		setEffect(defaults.getString("Effect"));
		
		location = extractLocation(compressedLocation);
		this.compressedLocation = compressedLocation;
	}
	
	public void clone(JumpPad master)
	{
		height = master.height;
		length = master.length;
		duration = master.duration;
		sound = master.sound;
		effect = master.effect;
		forceDirection = master.forceDirection;
		
		save();
	}
	
	private void save()
	{
		File file = new File(Main.instance.getDataFolder(), "//" + "jump_pads.yml");
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection config;
		String id = compressedLocation;
		if (yaml.isConfigurationSection(id))
			config = yaml.getConfigurationSection(id);
		else
			config = yaml.createSection(id);
		
		config.set("JumpHeight", height);
		config.set("JumpLength", length);
		config.set("JumpDuration", duration);
		config.set("Sound", (sound == null ? "NONE" : sound.toString()));
		config.set("Effect", (effect == null ? "NONE" : effect.toString()));
		config.set("ForceDirection", forceDirection.name());
		config.set("Location", compressedLocation);
		
		try
		{
			yaml.save(file);
		}
		catch (IOException e)
		{
			Main.message(null, "Failed to save jump pad @: " + compressedLocation);
		}
	}
	
	public Location getLocation()
	{
		return this.location;
	}
	
	public String getCompressedLocation()
	{
		return this.compressedLocation;
	}
	
	public void setHeight(double height)
	{
		setHeight(height, false);
	}
	
	public void setHeight(double height, boolean save)
	{
		this.height = Math.min(20, Math.max(0, height));
		
		if (save) this.save();
	}
	
	public void setLength(double length)
	{
		setLength(length, false);
	}
	
	public void setLength(double length, boolean save)
	{
		this.length = Math.min(20, Math.max(0, length));
		
		if (save) this.save();
	}
	
	public void setDuration(int duration)
	{
		setDuration(duration, false);
	}
	
	public void setDuration(int duration, boolean save)
	{
		this.duration = duration;
		
		if (save) this.save();
	}
	
	public void setSound(String sound)
	{
		setSound(sound, false);
	}
	
	public void setSound(String sound, boolean save)
	{
		if ((sound == null) || sound.isEmpty() || sound.equalsIgnoreCase("NONE"))
		{
			this.sound = null;
		}
		else
		{
			try
			{
				this.sound = Sound.valueOf(sound);
			}
			catch (IllegalArgumentException ex)
			{
				this.sound = null;
			}
		}
		
		if (save) this.save();
	}
	
	public void setEffect(String effect)
	{
		setEffect(effect, false);
	}
	
	public void setEffect(String effect, boolean save)
	{
		if ((effect == null) || effect.isEmpty() || effect.equalsIgnoreCase("NONE"))
		{
			this.effect = null;
		}
		else
		{
			try
			{
				this.effect = Effect.valueOf(effect);
			}
			catch (IllegalArgumentException ex)
			{
				this.effect = null;
			}
		}
	}
	
	public void setForceDirection(String forceDirection)
	{
		setForceDirection(forceDirection, false);
	}
	
	public void setForceDirection(String forceDirection, boolean save)
	{
		this.forceDirection = ForceDirection.fromString(forceDirection);
		
		if (save) this.save();
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public double getLength()
	{
		return length;
	}
	
	public int getDuration()
	{
		return duration;
	}
	
	public Sound getSound()
	{
		return sound;
	}
	
	public Effect getEffect()
	{
		return effect;
	}
	
	public ForceDirection getForceDirection()
	{
		return forceDirection;
	}
}
