package com.tram98.chip8interpreter;

public class Chip8
{
    private char[] ram = new char[0x1000]; //the Chip8 has 4096 bytes of RAM, gotta use chars here, bytes are signed in Java
    private char[] rom; // this will contain the program
    private boolean[] display = new boolean[64*32];//monochrome display 64x32 pixels

    private char pc; //program counter
    private char sp; //stack pointer

    //General purpose registers
    private char[] V = new char[0x10];//0-F
    private char I;

    private char[] stack = new char[16];

    private volatile boolean running = false; //when this is false, the execution will stop

    public Chip8()
    {
        pc=0x200;
    }

    public Chip8(char[] rom)
    {
        this.rom=rom;
    }

    //load rom
    private void load(char[] rom)
    {
        //reset pc
        pc=0x200;
		this.rom = rom;
    }

    private void reset()
	{
		pc=0x200;
		sp=0;
		stack=new char[16];
		for (int i = 0x200; i < ram.length; i++)
		{
			ram[i]=0x0;
		}
	}

	//do next cycle
	private void exec()
	{
		char ins = (char) (ram[pc]<<8 + ram[pc+1]);
		//advance program counter by 2
		pc+=2;
	}

    //start or continue the emulation
    private void run()
    {
        if(rom == null)
		{
			return;
		}
		else
		{
			for (int i = 0; i < rom.length; i++)
			{
				try
				{
					ram[0x200+i] = rom[i];
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					System.err.println("Error loading rom: Rom too Big");
				}
			}
		}

        while(running)
        {
			exec();
        }
    }

    public boolean[] getDisplay()
    {
        return display;
    }
}
