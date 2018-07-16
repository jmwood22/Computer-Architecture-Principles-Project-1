/* On my honor, I have neither given nor
received unauthorized aid on this assignment */

/*
 * C_D_A 5_1_5_5: F_a_l_l 2_0_1_7
 * P_r_o_j_e_c_t 1
 * Jeffrey Wood
 * MIPS simulator
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

public class MIPSsim 
{
	static boolean breakFlag = false;
	static boolean jumpFlag = false;
	static boolean dataFlag = false;
	static int[] registers = new int[32];
	static int[] data = new int[24];
	static HashMap<Integer, String> memory = new HashMap<Integer, String>();
	static int simAddress = 256;
	static int dataStartAddress = 0;
	
	public static void main(String args[])
	{	



		//Reading the input text file
		String text = "";
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(args[0]));

			String temp;
			while((temp = br.readLine())!=null)
			{
				text += temp +"\n";
			}
			br.close();
			//System.out.println(text);
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		//writing the disassembly text file
		String disassembly = disassemble(text);
		try
		{
			PrintWriter pw = new PrintWriter("disassembly.txt");
			pw.println(disassembly);
			pw.close();

		}catch (Exception e)
		{
			e.printStackTrace();
		}


		//simulating the instructions and writing to the simulation text file

		String simulation = simulate();
		try
		{
			PrintWriter pw = new PrintWriter("simulation.txt");
			pw.println(simulation);
			pw.close();

		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}



	private static String simulate() 
	{
		int index = 0;
		int cycle = 1;
		
		String simulation = "";
		breakFlag = false;
		while(!breakFlag)
		{
			jumpFlag = false;
			String temp = memory.get(simAddress);
			String instruction = "";
			simulation += "--------------------\n"
					+ "Cycle:" + cycle + "\t" + simAddress+"\t";

			if(temp.substring(0, 2).equals("01"))
			{
				simulateCat1(temp);
				instruction += disassembleCat1(temp);
			}
			else
			{
				simulateCat2(temp);
				instruction += disassembleCat2(temp);
			}

			simulation+= instruction + "\n\n" + printRegisters() + "\n\n"
					+ printData()+"\n";




			//System.out.println(simulation);
			if(!jumpFlag)
				simAddress +=4;
			cycle++;

		}


		return simulation;
	}

	private static String printData() {
		String temp = "Data\n"
				+ "340:\t";
		for(int i = 0; i < 8; i++)
		{
			temp += data[i];
			if(i!=7)
				temp +="\t";
		}

		temp += "\n372:\t";
		for(int i = 8; i < 16; i++)
		{
			temp += data[i];
			if(i!=15)
				temp +="\t";
		}

		temp += "\n404:\t";
		for(int i = 16; i < 24; i++)
		{
			temp += data[i];
			if(i!=23)
				temp +="\t";
		}

		return temp;

	}
	

	private static String printRegisters() {
		String temp = "Registers\n"
				+ "R00:\t";
		for(int i = 0; i < 8; i++)
		{
			temp += registers[i];
			if(i!=7)
				temp +="\t";
		}

		temp += "\nR08:\t";
		for(int i = 8; i < 16; i++)
		{
			temp += registers[i];
			if(i!=15)
				temp +="\t";
		}

		temp += "\nR16:\t";
		for(int i = 16; i < 24; i++)
		{
			temp += registers[i];
			if(i!=23)
				temp +="\t";
		}

		temp += "\nR24:\t";
		for(int i = 24; i < 32; i++)
		{
			temp += registers[i];
			if(i!=31)
				temp +="\t";
		}

		return temp;
	}

	private static void simulateCat2(String temp) {
		//4 bit opcode
		String opcode = temp.substring(2,6);
		int rd, rs, rt, immediate;

		switch(opcode) {
		case "0000":
			//Instruction ADD
			//ADD rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs + rt
			//The 32-bit word value in GPR rt is added to the 32-bit value in GPR rs to produce a 32-bit result.
			//If the addition results in 32-bit 2s complement arithmetic overflow, the destination register is not modified and
			//	an Integer Overflow exception occurs.
			//If the addition does not overflow, the 32-bit result is placed into GPR rd.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			registers[rd] = registers[rs] + registers[rt];
			break;
		case "0001":
			//Instruction SUB
			//SUB rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs -rt
			//The 32-bit word value in GPR rt is subtracted from the 32-bit value in GPR rs to produce a 32-bit result. If the subtraction
			//results in 32-bit 2s complement arithmetic overflow, then the destination register is not modified and an Integer
			//Overflow exception occurs. If it does not overflow, the 32-bit result is placed into GPR rd.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			registers[rd] = registers[rs] - registers[rt];
			break;
		case "0010":
			//Instruction MUL
			//MUL rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs x rt
			//The 32-bit word value in GPR rs is multiplied by the 32-bit value in GPR rt, treating both operands as signed values,
			//to produce a 64-bit result. The least significant 32 bits of the product are written to GPR rd. The contents of HI and
			//LO are UNPREDICTABLE after the operation. No arithmetic exception occurs under any circumstances.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			registers[rd] = registers[rs] * registers[rt];
			break;
		case "0011":
			//Instruction AND
			//AND rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs AND rt
			//The contents of GPR rs are combined with the contents of GPR rt in a bitwise logical AND operation. The result is
			//placed into GPR rd.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			registers[rd] = registers[rs] & registers[rt];
			break;
		case "0100":
			//Instruction OR
			//OR rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs or rt
			//The contents of GPR rs are combined with the contents of GPR rt in a bitwise logical OR operation. The result is
			//placed into GPR rd.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			registers[rd] = registers[rs] | registers[rt];
			break;
		case "0101":
			//Instruction XOR
			//XOR rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs XOR rt
			//Combine the contents of GPR rs and GPR rt in a bitwise logical Exclusive OR operation and place the result into
			//GPR rd
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			registers[rd] = registers[rs] ^ registers[rt];
			break;
		case "0110":
			//Instruction NOR
			//NOR rd, rs, rt
			//rd <- rs NOR rt
			//The contents of GPR rs are combined with the contents of GPR rt in a bitwise logical NOR operation. The result is
			//placed into GPR rd.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			registers[rd] = ~(registers[rs] | registers[rt]);
			break;
		case "0111":
			//Instruction SLT
			//SLT rd, rs, rt
			//rd <- (rs < rt)
			//Compare the contents of GPR rs and GPR rt as signed integers and record the Boolean result of the comparison in
			//GPR rd. If GPR rs is less than GPR rt, the result is 1 (true); otherwise, it is 0 (false).
			//The arithmetic comparison does not cause an Integer Overflow exception.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16),2);
			if(registers[rs] < registers[rt])
			{
				registers[rd] = 1;
			}
			else
				registers[rd] = 0;
			break;
		case "1000":
			//Instruction ADDI
			//ADDI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs + immediate
			//The 16-bit signed immediate is added to the 32-bit value in GPR rs to produce a 32-bit result.
			//If the addition results in 32-bit 2s complement arithmetic overflow, the destination register is not modified and
			//	an Integer Overflow exception occurs.
			//If the addition does not overflow, the 32-bit result is placed into GPR rt.
			rt = Integer.parseInt(temp.substring(11,16), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			immediate = Integer.parseInt(temp.substring(16), 2);
			registers[rt] = registers[rs] + immediate;
			break;
		case "1001":
			//Instruction ANDI
			//ANDI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs AND immediate
			//The 16-bit immediate is zero-extended to the left and combined with the contents of GPR rs in a bitwise logical AND
			//operation. The result is placed into GPR rt.
			rt = Integer.parseInt(temp.substring(11,16), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			immediate = Integer.parseInt(temp.substring(16), 2);
			registers[rt] = registers[rs] & immediate;
			break;
		case "1010":
			//Instruction ORI
			//ORI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs or immediate
			//The 16-bit immediate is zero-extended to the left and combined with the contents of GPR rs in a bitwise logical OR
			//operation. The result is placed into GPR rt.
			rt = Integer.parseInt(temp.substring(11,16), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			immediate = Integer.parseInt(temp.substring(16), 2);
			registers[rt] = registers[rs] | immediate;
			break;
		case "1011":
			//Instruction XORI
			//XORI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs XOR immediate
			//Combine the contents of GPR rs and the 16-bit zero-extended immediate in a bitwise logical Exclusive OR operation
			//and place the result into GPR rt.
			rt = Integer.parseInt(temp.substring(11,16), 2);
			rs = Integer.parseInt(temp.substring(6,11), 2);
			immediate = Integer.parseInt(temp.substring(16), 2);
			registers[rt] = registers[rs] ^ immediate;
			break;
		}
	}

	private static void simulateCat1(String temp) {
		//4 bit opcode
		String opcode = temp.substring(2,6);
		int rd, rs, rt, immediate, offset, base, sa;

		switch(opcode) {
		case "0000":
			//Instruction J
			//J target
			//low 28 bits of target address are the remaining bits in the instruction shifted left 2 bits
			//the remaining upper bits are the corresponding bits of the address of the instruction in the delay slot (not the branch itself)
			simAddress = Integer.parseInt(temp.substring(7)+"00", 2);
			jumpFlag = true;
			break;
		case "0001":
			//Instruction JR
			//JR rs
			//rs = first 5 bits
			//PC <- rs
			//Jump to the effective target address in GPR rs. Execute the instruction following the jump, in the branch delay slot,
			//before jumping.
			//For processors that implement the MIPS16e ASE, set the ISA Mode bit to the value in GPR rs bit 0. Bit 0 of the target
			//address is always zero so that no Address Exceptions occur when bit 0 of the source register is one
			rs = Integer.parseInt(temp.substring(6,11), 2);
			simAddress = registers[rs];
			jumpFlag = true;
			break;
		case "0010":
			//Instruction BEQ
			//BEQ rs, rt, offset
			//rs = first 5 bits, rt = next 5 bits, offset = remaining 16 bits
			//if rs = rt then branch
			//the offset shifted left 2 bits is added to the address of the instruction following the branch (not the branch itself), in the branch delay
			//slot, to form a PC-relative effective target address
			rs = Integer.parseInt(temp.substring(6,11), 2);
			rt = Integer.parseInt(temp.substring(11,16), 2);
			offset = Integer.parseInt(temp.substring(16)+"00", 2);
			if(registers[rs] == registers[rt])
			{
				simAddress = offset + simAddress + 4;
				jumpFlag = true;
			}
			break; 
		case "0011":
			//Instruction BLTZ
			//BLTZ rs, offset
			//rs = first 5 bits, 0 = next 5 bits, offset = remaining 16 bits
			//if rs < 0 then branch
			//the offset shifted left 2 bits is added to the address of the instruction following the branch (not the branch itself), in the branch delay
			//slot, to form a PC-relative effective target address.
			//If the contents of rs are less than zero (sign bit is 1), branch to the effective target address after the instruction in the delay slot is executed.
			rs = Integer.parseInt(temp.substring(6,11), 2);
			offset = Integer.parseInt(temp.substring(16)+"00", 2);
			if(registers[rs] < 0)
			{
				simAddress = offset + simAddress + 4;
				jumpFlag = true;
			}
			break;
		case "0100":
			//Instruction BGTZ
			//BGTZ rs, offset
			//rs = first 5 bits, 0 = next 5 bits, offset = remaining 16 bits
			//if rs > 0 then branch
			//the offset shifted left 2 bits is added to the address of the instruction following the branch (not the branch itself), in the branch delay
			//slot, to form a PC-relative effective target address.
			//If the contents of rs are less than zero (sign bit is 1), branch to the effective target address after the instruction in the delay slot is executed.
			rs = Integer.parseInt(temp.substring(6,11), 2);
			offset = Integer.parseInt(temp.substring(16)+"00", 2);
			if(registers[rs] > 0)
			{
				simAddress = offset + simAddress + 4;
				jumpFlag = true;
			}
			break;
		case "0101":
			//Instruction BREAK
			breakFlag = true;
			break;
		case "0110":
			//Instruction SW
			//SW rt, offset(base)
			//base = first 5 bits, rt = next 5 bits, offset = remaining 16 bits
			//memory[base+offset] <- rt
			//the least-significant 32-bit word of register rt is stored in memory at the location specified by the aligned effective
			//address. The 16-bit signed offset is added to the contents of GPR base to form the effective address.
			rt = Integer.parseInt(temp.substring(11,16), 2);
			offset = Integer.parseInt(temp.substring(16), 2);
			base = Integer.parseInt(temp.substring(6,11), 2);
			data[(registers[base]+offset-dataStartAddress)/4] = registers[rt];
			break;
		case "0111":
			//Instruction LW
			//LW rt, offset(base)
			//base = first 5 bits, rt = next 5 bits, offset = remaining 16 bits
			//rt <- memory[base+offset]
			//The contents of the 32-bit word at the memory location specified by the aligned effective address are fetched,
			//sign-extended to the GPR register length if necessary, and placed in GPR rt. The 16-bit signed offset is added to the
			//contents of GPR base to form the effective address.
			rt = Integer.parseInt(temp.substring(11,16), 2);
			offset = Integer.parseInt(temp.substring(16), 2);
			base = Integer.parseInt(temp.substring(6,11), 2);
			registers[rt] = data[(registers[base]+offset-dataStartAddress)/4];
			break;
		case "1000":
			//Instruction SLL
			//SLL rd, rt, sa
			//0 = first 5 bits, rt = next 5 bits, rd = next 5 bits, sa = next 5 bits
			//rd <- rt << sa
			//The contents of the low-order 32-bit word of GPR rt are shifted left, inserting zeros into the emptied bits; the word
			//result is placed in GPR rd. The bit-shift amount is specified by sa.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rt = Integer.parseInt(temp.substring(11,16), 2);
			sa = Integer.parseInt(temp.substring(21,26), 2);
			registers[rd] = registers[rt] << sa;
			break;
		case "1001":
			//Instruction SRL
			//SRL rd, rt, sa
			//0 = first 5 bits, rt = next 5 bits, rd = next 5 bits, sa = next 5 bits
			//rd <- rt >> sa
			//The contents of the low-order 32-bit word of GPR rt are shifted right, inserting zeros into the emptied bits; the word
			//result is placed in GPR rd. The bit-shift amount is specified by sa.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rt = Integer.parseInt(temp.substring(11,16), 2);
			sa = Integer.parseInt(temp.substring(21,26), 2);
			registers[rd] = registers[rt] >>> sa;
			break;
		case "1010":
			//Instruction SRA
			//SRA rd, rt, sa
			//0 = first 5 bits, rt = next 5 bits, rd = next 5 bits, sa = next 5 bits
			//rd <- rt >> sa
			//The contents of the low-order 32-bit word of GPR rt are shifted right, duplicating the sign-bit (bit 31) in the emptied
			//bits; the word result is placed in GPR rd. The bit-shift amount is specified by sa.
			rd = Integer.parseInt(temp.substring(16,21), 2);
			rt = Integer.parseInt(temp.substring(11,16), 2);
			sa = Integer.parseInt(temp.substring(21,26), 2);
			registers[rd] = registers[rt] >> sa;
			break;
		case "1011":
			//Instruction NOP
			//No operation
			break;
		}
	}

	private static String disassemble(String text) 
	{
		int index = 0;
		int address = 256;
		String disassembly = "";
		int dataIndex = 0;

		while(index < text.length()-1)
		{
			String temp = text.substring(index, index+32);
			memory.put(address, temp);
			if(!breakFlag)
			{
				if(temp.substring(0, 2).equals("01"))
				{
					temp += "\t" + address + disassembleCat1(temp);
				}
				else
				{
					temp += "\t" + address + disassembleCat2(temp);
				}
			}
			else
			{
				if(!dataFlag)
				{
					dataStartAddress = address;
					dataFlag = true;
				}
				data[dataIndex] = twosComplement(temp);
				temp += "\t" + address + "\t" + data[dataIndex];
				dataIndex++;
			}

			index+=33;
			address +=4;
			disassembly += temp  + "\n";
		}
		disassembly = disassembly.substring(0,disassembly.length()-1);

		return disassembly;
	}

	private static int twosComplement(String temp) {
		String val = temp;
		int sign = 1;

		if(temp.charAt(0) == '1')
		{
			sign = -1;
			val = "";
			for(int i = 0; i < temp.length(); i++)
			{
				if(temp.charAt(i)== '1')
				{
					val+="0";
				}

				else
				{
					val+="1";
				}

			}
			return (Integer.parseInt(val.substring(1),2)+1)*sign;

		}

		else
			return Integer.parseInt(val.substring(1),2);
	}

	private static String disassembleCat2(String temp) 
	{
		//4 bit opcode
		String opcode = temp.substring(2,6);

		switch(opcode) {
		case "0000":
			//Instruction ADD
			//ADD rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs + rt
			//The 32-bit word value in GPR rt is added to the 32-bit value in GPR rs to produce a 32-bit result.
			//If the addition results in 32-bit 2s complement arithmetic overflow, the destination register is not modified and
			//	an Integer Overflow exception occurs.
			//If the addition does not overflow, the 32-bit result is placed into GPR rd.
			return "\tADD R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ; 
		case "0001":
			//Instruction SUB
			//SUB rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs -rt
			//The 32-bit word value in GPR rt is subtracted from the 32-bit value in GPR rs to produce a 32-bit result. If the subtraction
			//results in 32-bit 2s complement arithmetic overflow, then the destination register is not modified and an Integer
			//Overflow exception occurs. If it does not overflow, the 32-bit result is placed into GPR rd.
			return "\tSUB R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ;
		case "0010":
			//Instruction MUL
			//MUL rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs x rt
			//The 32-bit word value in GPR rs is multiplied by the 32-bit value in GPR rt, treating both operands as signed values,
			//to produce a 64-bit result. The least significant 32 bits of the product are written to GPR rd. The contents of HI and
			//LO are UNPREDICTABLE after the operation. No arithmetic exception occurs under any circumstances.
			return "\tMUL R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ;
		case "0011":
			//Instruction AND
			//AND rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs AND rt
			//The contents of GPR rs are combined with the contents of GPR rt in a bitwise logical AND operation. The result is
			//placed into GPR rd.
			return "\tAND R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ;
		case "0100":
			//Instruction OR
			//OR rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs or rt
			//The contents of GPR rs are combined with the contents of GPR rt in a bitwise logical OR operation. The result is
			//placed into GPR rd.
			return "\tOR R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ;
		case "0101":
			//Instruction XOR
			//XOR rd, rs, rt
			//rs = first 5 bits, rt = next 5 bits, rd = next 5 bits
			//rd <- rs XOR rt
			//Combine the contents of GPR rs and GPR rt in a bitwise logical Exclusive OR operation and place the result into
			//GPR rd
			return "\tXOR R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ;
		case "0110":
			//Instruction NOR
			//NOR rd, rs, rt
			//rd <- rs NOR rt
			//The contents of GPR rs are combined with the contents of GPR rt in a bitwise logical NOR operation. The result is
			//placed into GPR rd.
			return "\tNOR R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ;
		case "0111":
			//Instruction SLT
			//SLT rd, rs, rt
			//rd <- (rs < rt)
			//Compare the contents of GPR rs and GPR rt as signed integers and record the Boolean result of the comparison in
			//GPR rd. If GPR rs is less than GPR rt, the result is 1 (true); otherwise, it is 0 (false).
			//The arithmetic comparison does not cause an Integer Overflow exception.
			return "\tSLT R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) ;
		case "1000":
			//Instruction ADDI
			//ADDI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs + immediate
			//The 16-bit signed immediate is added to the 32-bit value in GPR rs to produce a 32-bit result.
			//If the addition results in 32-bit 2s complement arithmetic overflow, the destination register is not modified and
			//	an Integer Overflow exception occurs.
			//If the addition does not overflow, the 32-bit result is placed into GPR rt.
			return "\tADDI R" + Integer.parseInt(temp.substring(11,16), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", #" + Integer.parseInt(temp.substring(16), 2) ;
		case "1001":
			//Instruction ANDI
			//ANDI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs AND immediate
			//The 16-bit immediate is zero-extended to the left and combined with the contents of GPR rs in a bitwise logical AND
			//operation. The result is placed into GPR rt.
			return "\tANDI R" + Integer.parseInt(temp.substring(11,16), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", #" + Integer.parseInt(temp.substring(16), 2) ;
		case "1010":
			//Instruction ORI
			//ORI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs or immediate
			//The 16-bit immediate is zero-extended to the left and combined with the contents of GPR rs in a bitwise logical OR
			//operation. The result is placed into GPR rt.
			return "\tORI R" + Integer.parseInt(temp.substring(11,16), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", #" + Integer.parseInt(temp.substring(16), 2) ;
		case "1011":
			//Instruction XORI
			//XORI rt, rs, immediate
			//rs = first 5 bits, rt = next 5 bits, immediate = remaining 16 bits
			//rt <- rs XOR immediate
			//Combine the contents of GPR rs and the 16-bit zero-extended immediate in a bitwise logical Exclusive OR operation
			//and place the result into GPR rt.
			return "\tXORI R" + Integer.parseInt(temp.substring(11,16), 2) + ", R" + Integer.parseInt(temp.substring(6,11), 2) + ", #" + Integer.parseInt(temp.substring(16), 2) ;
		}
		return "";
	}

	private static String disassembleCat1(String temp) 
	{
		//4 bit opcode
		String opcode = temp.substring(2,6);

		switch(opcode) {
		case "0000":
			//Instruction J
			//J target
			//low 28 bits of target address are the remaining bits in the instruction shifted left 2 bits
			//the remaining upper bits are the corresponding bits of the address of the instruction in the delay slot (not the branch itself)
			return "\tJ #" + Integer.parseInt(temp.substring(7)+"00", 2);
		case "0001":
			//Instruction JR
			//JR rs
			//rs = first 5 bits
			//PC <- rs
			//Jump to the effective target address in GPR rs. Execute the instruction following the jump, in the branch delay slot,
			//before jumping.
			//For processors that implement the MIPS16e ASE, set the ISA Mode bit to the value in GPR rs bit 0. Bit 0 of the target
			//address is always zero so that no Address Exceptions occur when bit 0 of the source register is one
			return "\tJR R" + Integer.parseInt(temp.substring(6,11), 2);
		case "0010":
			//Instruction BEQ
			//BEQ rs, rt, offset
			//rs = first 5 bits, rt = next 5 bits, offset = remaining 16 bits
			//if rs = rt then branch
			//the offset shifted left 2 bits is added to the address of the instruction following the branch (not the branch itself), in the branch delay
			//slot, to form a PC-relative effective target address
			return "\tBEQ R" + Integer.parseInt(temp.substring(6,11), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) + ", #" + Integer.parseInt(temp.substring(16)+"00", 2); 
		case "0011":
			//Instruction BLTZ
			//BLTZ rs, offset
			//rs = first 5 bits, 0 = next 5 bits, offset = remaining 16 bits
			//if rs < 0 then branch
			//the offset shifted left 2 bits is added to the address of the instruction following the branch (not the branch itself), in the branch delay
			//slot, to form a PC-relative effective target address.
			//If the contents of rs are less than zero (sign bit is 1), branch to the effective target address after the instruction in the delay slot is executed.
			return "\tBLTZ R" + Integer.parseInt(temp.substring(6,11), 2) + ", #" + Integer.parseInt(temp.substring(16)+"00", 2);
		case "0100":
			//Instruction BGTZ
			//BGTZ rs, offset
			//rs = first 5 bits, 0 = next 5 bits, offset = remaining 16 bits
			//if rs > 0 then branch
			//the offset shifted left 2 bits is added to the address of the instruction following the branch (not the branch itself), in the branch delay
			//slot, to form a PC-relative effective target address.
			//If the contents of rs are less than zero (sign bit is 1), branch to the effective target address after the instruction in the delay slot is executed.
			return "\tBGTZ R" + Integer.parseInt(temp.substring(6,11), 2) + ", #" + Integer.parseInt(temp.substring(16)+"00", 2);
		case "0101":
			//Instruction BREAK
			breakFlag = true;
			return "\tBREAK";
		case "0110":
			//Instruction SW
			//SW rt, offset(base)
			//base = first 5 bits, rt = next 5 bits, offset = remaining 16 bits
			//memory[base+offset] <- rt
			//the least-significant 32-bit word ofregister rt is stored in memory at the location specified by the aligned effective
			//address. The 16-bit signed offset is added to the contents of GPR base to form the effective address.
			return "\tSW R" + Integer.parseInt(temp.substring(11,16), 2) + ", " + Integer.parseInt(temp.substring(16), 2) + "(R" +Integer.parseInt(temp.substring(6,11), 2) + ")";
		case "0111":
			//Instruction LW
			//LW rt, offset(base)
			//base = first 5 bits, rt = next 5 bits, offset = remaining 16 bits
			//rt <- memory[base+offset]
			//The contents of the 32-bit word at the memory location specified by the aligned effective address are fetched,
			//sign-extended to the GPR register length if necessary, and placed in GPR rt. The 16-bit signed offset is added to the
			//contents of GPR base to form the effective address.
			return "\tLW R" + Integer.parseInt(temp.substring(11,16), 2) + ", " + Integer.parseInt(temp.substring(16), 2) + "(R" +Integer.parseInt(temp.substring(6,11), 2) + ")";
		case "1000":
			//Instruction SLL
			//SLL rd, rt, sa
			//0 = first 5 bits, rt = next 5 bits, rd = next 5 bits, sa = next 5 bits
			//rd <- rt << sa
			//The contents of the low-order 32-bit word of GPR rt are shifted left, inserting zeros into the emptied bits; the word
			//result is placed in GPR rd. The bit-shift amount is specified by sa.
			return "\tSLL R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) + ", #" + Integer.parseInt(temp.substring(21,26), 2);
		case "1001":
			//Instruction SRL
			//SRL rd, rt, sa
			//0 = first 5 bits, rt = next 5 bits, rd = next 5 bits, sa = next 5 bits
			//rd <- rt >> sa
			//The contents of the low-order 32-bit word of GPR rt are shifted right, inserting zeros into the emptied bits; the word
			//result is placed in GPR rd. The bit-shift amount is specified by sa.
			return "\tSRL R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) + ", #" + Integer.parseInt(temp.substring(21,26), 2);
		case "1010":
			//Instruction SRA
			//SRA rd, rt, sa
			//0 = first 5 bits, rt = next 5 bits, rd = next 5 bits, sa = next 5 bits
			//rd <- rt >> sa
			//The contents of the low-order 32-bit word of GPR rt are shifted right, duplicating the sign-bit (bit 31) in the emptied
			//bits; the word result is placed in GPR rd. The bit-shift amount is specified by sa.
			return "\tSRA R" + Integer.parseInt(temp.substring(16,21), 2) + ", R" + Integer.parseInt(temp.substring(11,16), 2) + ", #" + Integer.parseInt(temp.substring(21,26), 2);
		case "1011":
			//Instruction NOP
			//No operation
			return "\tNOP";
		}
		return "";
	}

}
