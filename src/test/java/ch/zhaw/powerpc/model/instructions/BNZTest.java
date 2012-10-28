/**
 * 
 */
package ch.zhaw.powerpc.model.instructions;

import static ch.zhaw.powerpc.model.instructions.TestUtil.binEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.zhaw.powerpc.model.ControlUnit;
import ch.zhaw.powerpc.model.MainMemory;

/**
 * Testet den durch das carry bit bedingten Sprung auf die Adresse aus einem Register
 * 
 * @author Max
 * 
 */
public class BNZTest {

	@Test
	public void roundTrip() {
		MainMemory mem = new MainMemory();
		mem.setInstruction(100, new BNZ(02));
		mem.setInstruction(102, new BNZ(03));
		
		ControlUnit cu = new ControlUnit((mem)); 

		cu.getRegisters()[02].write(132);
		cu.getRegisters()[03].write(122);
		assertEquals(100, cu.getProgramCounter());

		cu.getRegisters()[0].write(0);
		cu.runCycle();
		
		assertEquals(102, cu.getProgramCounter());

		cu.getRegisters()[0].write(100);
		cu.runCycle();
		
		assertEquals(122, cu.getProgramCounter());
	}

	@Test
	public void testJumpAkku() {
		
		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[00].write(104);
		
		assertEquals(100, cu.getProgramCounter());
		
		cu.getRegisters()[0].write(100);
		assertEquals(104, new BNZ(00).run(cu));
	}
	
	@Test
	public void testNoJumpAkku() {
		
		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[00].write(104);
		
		assertEquals(100, cu.getProgramCounter());
		
		cu.getRegisters()[0].write(0);
		assertEquals(102, new BNZ(00).run(cu));
	}
	
	@Test
	public void testJumpRegisterOne() {

		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[01].write(222);

		assertEquals(100, cu.getProgramCounter());

		cu.getRegisters()[0].write(100);
		assertEquals(222, new BNZ(01).run(cu));
	}

	@Test
	public void testNoJumpRegisterOne() {

		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[01].write(222);

		assertEquals(100, cu.getProgramCounter());

		cu.getRegisters()[0].write(0);
		assertEquals(102, new BNZ(01).run(cu));
	}

	@Test
	public void testJumpRegisterTwo() {

		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[02].write(134);

		assertEquals(100, cu.getProgramCounter());

		cu.getRegisters()[0].write(100);
		assertEquals(134, new BNZ(02).run(cu));
	}

	@Test
	public void testNoJumpRegisterTwo() {
	
		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[02].write(134);
	
		assertEquals(100, cu.getProgramCounter());
	
		cu.getRegisters()[0].write(0);
		assertEquals(102, new BNZ(02).run(cu));
	}

	@Test
	public void testJumpRegisterThree() {
	
		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[03].write(144);
	
		assertEquals(100, cu.getProgramCounter());
	
		cu.getRegisters()[0].write(100);
		assertEquals(144, new BNZ(03).run(cu));
	}

	@Test
	public void testNoJumpRegisterThree() {

		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[03].write(144);

		assertEquals(100, cu.getProgramCounter());

		cu.getRegisters()[0].write(0);
		assertEquals(102, new BNZ(03).run(cu));
	}

	@Test
	public void testJumpToLastWord() {

		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[01].write(498);

		assertEquals(100, cu.getProgramCounter());

		cu.getRegisters()[0].write(100);
		
		assertEquals(498, new BNZ(01).run(cu));
	}

	@Test
	public void testJumpToFirstWord() {

		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[02].write(100);

		assertEquals(100, cu.getProgramCounter());

		cu.getRegisters()[0].write(100);
		
		assertEquals(100, new BNZ(02).run(cu));
	}
	
	@Test(expected=InvalidInstructionException.class)	
	public void badJumpToLow(){
		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[02].write(50);
		new BNZ(02).run(cu);
	}
	
	@Test(expected=InvalidInstructionException.class)	
	public void badJumpToHigh(){
		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[01].write(510);
		new BNZ(01).run(cu);
	}
	
	@Test(expected=InvalidInstructionException.class)	
	public void badJump(){
		ControlUnit cu = new ControlUnit(new MainMemory()); 
		cu.getRegisters()[03].write(135);
		new BNZ(03).run(cu);
	}
	
	@Test
	public void binary() {
		binEquals("0001000100000000", new BNZ(00).getBinary());
		binEquals("0001010100000000", new BNZ(01).getBinary());
		binEquals("0001100100000000", new BNZ(02).getBinary());
		binEquals("0001110100000000", new BNZ(03).getBinary());
	}
}