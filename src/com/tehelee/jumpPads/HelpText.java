package com.tehelee.jumpPads;

public class HelpText
{
	public static String PluginName = "§b[§cJump Pads§b]§f: ";
	
	public static String PermissionPrefix = "permissions.jumpPads.";
	public static String MissingPermission = "§cYou don't have permission to use this command.";
	public static String ExistingJumpPad = "§8This pressure plate is already a jump pad.";
	public static String LogCreation = "%1$s has created a JumpPad @ %2$s";
	public static String NeedJumpPad = "§cYou must be looking at an existing jump pad.";
	public static String CreateHelp = "§8To create jump pads type:\n§f/%1$s create";
	public static String CopyHelp = "§8To copy jump pads type:\n§f/%1$s copy";
	public static String InvalidNumber = "§cYou must enter a valid number!";
	
	public static String InfoFormat = PluginName + "%1$s§f\n  §aSpeed§f: %2$.2f\n  §bHeight§f: %3$.2f\n  §cDuration§f: %4$s\n  §dSound§f: %5$s\n  §6Effect§f: %6$s\n  §9Force Direction§f: %7$s";
	
	public static String logStart = "Initialized";
	public static String logStop = "Shut Down";
	public static String logRestart = "Reloaded Config";
}
