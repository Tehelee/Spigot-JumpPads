package com.tehelee.jumpPads;

public enum ForceDirection
{
	NONE,
	NORTH,
	NORTH_EAST,
	EAST,
	SOUTH_EAST,
	SOUTH,
	SOUTH_WEST,
	WEST,
	NORTH_WEST,
	UP;
	
	public static float getYaw(ForceDirection direction)
	{
		switch(direction)
		{
		default:
			return -1;
		case NORTH:
			return 180;
		case NORTH_EAST:
			return 225;
		case EAST:
			return 270;
		case SOUTH_EAST:
			return 315;
		case SOUTH:
			return 0;
		case SOUTH_WEST:
			return 45;
		case WEST:
			return 90;
		case NORTH_WEST:
			return 135;
		}
	}
	
	public float getYaw()
	{
		return getYaw(this);
	}
	
	public static String name(ForceDirection direction)
	{
		switch(direction)
		{
		default:
			return "NONE";
		case NORTH:
			return "NORTH";
		case NORTH_EAST:
			return "NORTH_EAST";
		case EAST:
			return "EAST";
		case SOUTH_EAST:
			return "SOUTH_EAST";
		case SOUTH:
			return "SOUTH";
		case SOUTH_WEST:
			return "SOUTH_WEST";
		case WEST:
			return "WEST";
		case NORTH_WEST:
			return "NORTH_WEST";
		case UP:
			return "UP";
		}
	}
	
	public static ForceDirection fromString(String parse)
	{
		if ((parse == null) || (parse.isEmpty()))
			return ForceDirection.NONE;
		
		String key = parse.toUpperCase();
		
		ForceDirection[] values = ForceDirection.values();
		
		for (int i = 0; i < values.length; i++)
		{
			if (values[i].toString().equals(key))
				return values[i];
		}
		
		return ForceDirection.NONE;
	}
}
