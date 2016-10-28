package com.tehelee.jumpPads;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin
{
	public static Server server;
	public static PluginManager pluginManager;
	public static ConsoleCommandSender console;
	public static FileConfiguration config;
	
	public static Main instance;
	
	public static BukkitScheduler scheduler;
	
	public Main()
	{
		Main.instance = this;
	}
	
	@Override
	public void onEnable()
	{
		initializeConfig();
		
		Main.server = getServer();
		
		Main.pluginManager = server.getPluginManager();
		
		Main.console = Main.server.getConsoleSender();		
		Main.scheduler = Main.server.getScheduler();
		
		JumpPad.populateRegister();
		
		Main.pluginManager.registerEvents(new JumpPadsListener(), this);
		
		JumpPadsListener.StartTasks();
		
		this.getCommand("JumpPads").setExecutor(new CmdJumpPads());
		
		message(null, HelpText.logStart, true);
		
		Reload.onEnable();
	}
	
	private void initializeConfig()
	{
		Main.config = this.getConfig();
		
		JumpPad.writeDefaultConfig();
		
		Main.config.options().copyDefaults(true);
		
		writeConfig();
	}
	
	public static void writeConfig()
	{
		if (null != instance)
		{
			instance.saveConfig();
			
			instance.reloadConfig();
			
			Main.config = instance.getConfig();
		}
	}
	
	@Override
	public void onDisable()
	{
		message(null, HelpText.logStop, true);
	}
	
	public static void message(Player player, String message)
	{
		message((CommandSender)player, message);
	}
	
	public static void message(CommandSender sender, String message)
	{
		message(sender, message, false);
	}
	
	public static void message(CommandSender sender, String message, boolean prefix)
	{	
		if ((null != sender) && (sender instanceof Player))
		{
			sender.sendMessage((prefix ? HelpText.PluginName : "" ) + ChatColor.WHITE + message);
		}
		else
		{
			Main.console.sendMessage((prefix ? HelpText.PluginName : "" ) + ChatColor.GRAY + ChatColor.stripColor(message));
		}
	}	
}
