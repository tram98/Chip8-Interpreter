package com.tram98.chip8interpreter;

public class InvalidOpcodeException extends Exception
{
	public InvalidOpcodeException(String message)
	{
		super(message);
	}
}
