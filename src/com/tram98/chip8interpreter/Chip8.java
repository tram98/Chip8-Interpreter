package com.tram98.chip8interpreter;

public class Chip8
{
	private char[] ram = new char[0x1000]; //the Chip8 has 4096 bytes of RAM, gotta use chars here, bytes are signed in Java
	private char[] rom; // this will contain the program
	private boolean[] display = new boolean[64 * 32];//monochrome display 64x32 pixels

	private char pc; //program counter
	private char sp; //stack pointer

	//General purpose registers
	private char[] V = new char[0x10];//0-F
	private char I;

	private char[] stack = new char[16];

	private volatile boolean running = false; //when this is false, the execution will stop

	public Chip8()
	{
		pc = 0x200;
	}

	public Chip8(char[] rom)
	{
		this.rom = rom;
	}

	//load rom
	private void load(char[] rom)
	{
		//reset pc
		pc = 0x200;
		this.rom = rom;
	}

	private void reset()
	{
		pc = 0x200;
		sp = 0;
		stack = new char[16];
		V = new char[0x10];
		for (int i = 0x200; i < ram.length; i++)
		{
			ram[i] = 0x0;
		}
	}

	/**
	 * Here are ALL instructions
	 */

	//Clear the display
	private void CLS()
	{
		display = new boolean[64 * 32];
	}

	//return from subroutine
	private void RETURN()
	{
		//sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
		pc = stack[sp];
		sp--;
	}

	//jump to adr
	private void GOTO(char adr)
	{
		//sets the program counter to adr
		pc = adr;
	}

	private void CALL(char adr)
	{
		//increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to adr.
		sp++;
		stack[sp] = pc;
		pc = adr;
	}

	//Skip next instruction if V[x] == kk.
	private void SE(char x, char kk)
	{
		if (V[x] == kk)
			pc += 2;
	}

	//Skip next instruction if V[x] != kk.
	private void SNE(char x, char kk)
	{
		if (V[x] != kk)
			pc += 2;
	}

	//Skip next instruction if V[x] = V[y].
	private void SEV(char x, char y)
	{
		if (V[x] == V[y])
			pc += 2;
	}

	//Load kk into V[x]
	private void LD(char x, char kk)
	{
		V[x] = kk;
	}

	//Add kk to V[x]
	private void ADD(char x, char kk)
	{
		V[x] += kk;
	}

	//Set V[x] to V[y]
	private void LDV(char x, char y)
	{
		V[x] = V[y];
	}

	//Set V[x] to V[X] OR V[y]
	private void OR(char x, char y)
	{
		V[x] = (char) (V[x] | V[y]);
	}

	//Set V[x] to V[X] AND V[y]
	private void AND(char x, char y)
	{
		V[x] = (char) (V[x] & V[y]);
	}

	//Set V[x] to V[X] XOR V[y]
	private void XOR(char x, char y)
	{
		V[x] = (char) (V[x] ^ V[y]);
	}

	//Add V[y] to V[x]. If carry, then V[0xF] = 1 else 0
	private void ADDV(char x, char y)
	{
		V[x] = (char) ((V[x]+V[y])%255);
		V[0xF]= (char) ((V[x]+V[y]>255)?1:0);//carry?
	}

	//Subtract V[y] from V[x]. If borrow, then V[0xF] = 0 else 1
	private void SUBV(char x, char y)
	{
		V[x] = (char) ((V[x]<V[y])?0:V[x]-V[y]);
		V[0xF]= (char) ((V[x]<V[y])?0:1);//borrow?
	}

	//set V[0xF] to 1 if the least significant bit of V[x] is 1
	private void SHR(char x)
	{
		V[0xF]= (char) (V[x]&1);
	}

	//Subtract V[x] from V[y] and save the value in V[x]. If borrow, then V[0xF] = 0 else 1
	private void SUBN(char x, char y)
	{
		V[x] = (char) ((V[y]<V[x])?0:V[y]-V[x]);
		V[0xF]= (char) ((V[y]<V[x])?0:1);//borrow?
	}

	//set V[0xF] to 1 if the most significant bit of V[x] is 1
	private void SHL(char x)
	{
		V[0xF]= (char) (V[x]>>7);
	}

	//execute next cycle
	private void exec() throws InvalidOpcodeException
	{
		char opcode = (char) (ram[pc] << 8 + ram[pc + 1]);            //merge 2 bytes into the next opcode
		System.out.println(Integer.toHexString(opcode).toUpperCase());
		char nnn = (char) (opcode & 0x0FFF);
		char kk = (char) (opcode & 0x00FF);
		char x = (char) (opcode & 0x0F00);
		char y = (char) (opcode & 0x00F0);

		pc += 2;                                                    //advance program counter by 2

		switch (BitOps.right(opcode, 3))
		{
			case 0:
				switch (opcode)
				{
					case 0x00E0:
						CLS();                                    //clears the display
						break;

					case 0x00EE:
						RETURN();                                //return from subroutine
						break;
				}
				break;

			case 0x1:
				GOTO(nnn);                                        //Jump to address nnn
				break;

			case 0x2:
				CALL(nnn);                                        //call subroutine at address nnn
				break;

			case 0x3:
				SE(x, kk);                                        //Skip next instruction if V[x] == kk
				break;

			case 0x4:
				SNE(x, kk);                                        //Skip next instruction if V[x] != kk
				break;

			case 0x5:
				SEV(x, y);                                        //Skip next instruction if V[x] = V[y]
				break;

			case 0x6:
				LD(x, kk);                                        //Load kk into V[x]
				break;

			case 0x7:
				ADD(x, kk);                                        //Add kk to V[x]
				break;

			case 0x8:
				switch (opcode & 0xF)
				{
					case 0x0:
						LDV(x, y);                                //Set V[x] to V[y]
						break;

					case 0x1:
						OR(x, y);                                 //Set V[x] to V[X] OR V[y]
						break;

					case 0x2:
						AND(x, y);                                //Set V[x] to V[X] AND V[y]
						break;

					case 0x3:
						XOR(x, y);                                //Set V[x] to V[X] XOR V[y]
						break;

					case 0x4:
						ADDV(x, y);								  //Add V[y] to V[x]. If carry, then V[0xF] = 1 else 0
						break;

					case 0x5:
						SUBV(x, y);								  //Subtract V[y] from V[x]. If borrow, then V[0xF] = 0 else 1
						break;

					case 0x6:
						SHR(x);									  //set V[0xF] to 1 if the least significant bit of V[x] is 1
						break;

					case 0x7:
						SUBN(x, y);								  //Subtract V[x] from V[y] and save the value in V[x]. If borrow, then V[0xF] = 0 else 1
						break;

					case 0xE:
						SHL(x);									  //set V[0xF] to 1 if the least significant bit of V[x] is 1
						break;

					default:
						throw new InvalidOpcodeException("Opcode 0x"+ Integer.toHexString(opcode)+" is invalid!");

				}
			//TODO OTHER CASES AFTER 0x8

			default:
				throw new InvalidOpcodeException("Opcode 0x"+ Integer.toHexString(opcode)+" is invalid!");
		}
	}



	//start or continue the emulation
	private void run()
	{
		if (rom == null)
		{
			return;
		} else
		{
			for (int i = 0; i < rom.length; i++)
			{
				try
				{
					ram[0x200 + i] = rom[i];
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					System.err.println("Error loading rom: Rom too Big");
				}
			}
		}

		while (running)
		{
			try
			{
				exec();
			}
			catch (InvalidOpcodeException e)
			{
				System.exit(1);
			}
		}
	}

	public boolean[] getDisplay()
	{
		return display;
	}
}
