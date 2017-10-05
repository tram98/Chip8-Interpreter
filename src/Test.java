import com.tram98.chip8interpreter.BitOps;

public class Test
{
	public static void main(String[] args)
	{
		int opcode = 0B10000000;
		System.out.println(Integer.toHexString(opcode>>7));
	}
}
