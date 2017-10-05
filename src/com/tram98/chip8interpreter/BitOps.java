package com.tram98.chip8interpreter;

public class BitOps
{
	public static int left(int num, int digit)
	{
		return num<<digit*4;
	}

	public static int right(int num, int digit)
	{
		return num>>digit*4;
	}
}