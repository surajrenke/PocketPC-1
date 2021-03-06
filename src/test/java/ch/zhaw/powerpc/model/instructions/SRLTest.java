package ch.zhaw.powerpc.model.instructions;

import static ch.zhaw.powerpc.model.instructions.TestUtil.binEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.zhaw.powerpc.model.ControlUnit;
import ch.zhaw.powerpc.model.MainMemory;

public class SRLTest {

	/**
	 * Schieben logisch nach rechts: der Inhalt des Akkus wird um eine Stelle nach rechts geschoben; der Inhalt vom LSb
	 * des LSB (das rechte Bit des Wortes) wird als Carry-Flag gesetzt. das MSb des MSB (das 1. Bit des Wortes) wird auf
	 * 0 gesetzt.
	 */
	
	@Test
	public void shouldShiftOneToTheLeft() {
		ControlUnit cu = new ControlUnit(new MainMemory());
		cu.getRegisters()[0].write(32767);

		SRL srl = new SRL();
		srl.run(cu);

		assertEquals(16383, cu.getRegisters()[0].read());
		assertTrue(cu.getAlu().isCarryFlag());
	}
	
	@Test
	public void shouldFillWithZeroesOnMsb() {
		ControlUnit cu = new ControlUnit(new MainMemory());
		cu.getRegisters()[0].write(-1);

		SRL srl = new SRL();
		srl.run(cu);

		assertEquals(32767, cu.getRegisters()[0].read());
	}
	
	@Test
	public void shouldSetLsbAsCarry() {
		ControlUnit cu = new ControlUnit(new MainMemory());
		cu.getRegisters()[0].write(1);

		SRL srl = new SRL();
		srl.run(cu);

		assertTrue(cu.getAlu().isCarryFlag());
	}

	@Test
	public void shouldSetLsbAsCarryFalse() {
		ControlUnit cu = new ControlUnit(new MainMemory());
		cu.getRegisters()[0].write(0);

		SRL srl = new SRL();
		srl.run(cu);

		assertFalse(cu.getAlu().isCarryFlag());
	}
	
	@Test
	public void testToString() {
		assertEquals("SRL", new SRL().toString());
	}
	
	@Test
	public void testGetBinary() {
		binEquals("0000100100000000", new SRL().getBinary());
	}
}
