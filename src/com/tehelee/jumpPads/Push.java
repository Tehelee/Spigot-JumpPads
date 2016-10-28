package com.tehelee.jumpPads;

import org.bukkit.util.Vector;

public class Push
{
	public Vector velocity;
	public JumpPad pad;
	public int duration;
	
	public Push(Vector velocity, JumpPad pad, int duration)
	{
		this.velocity = velocity;
		this.pad = pad;
		this.duration = duration;
	}
}
