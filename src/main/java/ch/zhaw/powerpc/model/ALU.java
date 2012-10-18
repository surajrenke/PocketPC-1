package ch.zhaw.powerpc.model;

/**
 * Diese Klasse repräsentiert die arithemtisch-logische Einheit.
 * 
 * @author Max / Reto
 *
 */
public final class ALU {

	/**
	 * Das Carry Flag wird nur von der ALU gesetzt!
	 */
	private boolean carryFlag;
	
	private Register[] registers;

	public ALU(Register[] registers) {
		this.registers = registers;
		this.carryFlag = false;
	}

	public boolean isCarryFlag() {
		return carryFlag;
	}

	public void setCarryFlag(boolean newFlag) {
		this.carryFlag = newFlag;
	}

	public void addToAccu(int number) {
		int curAccu = registers[0].read();
		int newAccu = curAccu + number;
		this.carryFlag = isOverflow(newAccu);
		newAccu = mask(newAccu);
		registers[0].write(newAccu);
	}
	
	private static boolean isOverflow(int val) {
		return (val | 0xFFFF) != 0xFFFF;
	}
	
	private static int mask(int val) {
		return val & 0xFFFF;
	}

}
