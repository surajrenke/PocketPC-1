package ch.zhaw.powerpc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import ch.zhaw.powerpc.model.ControlUnit;
import ch.zhaw.powerpc.model.MainMemory;
import ch.zhaw.powerpc.model.instructions.InvalidInstructionException;
import ch.zhaw.powerpc.view.Printer;
import ch.zhaw.powerpc.view.impl.ConsolePrinter;
import ch.zhaw.powerpc.view.impl.EvilGUI;

/**
 * Einstiegspunkt für das komplette Programm.
 * 
 * @author Max / Reto / Des
 * 
 */
public class ProgramStarter {

	static enum Mode {
		STEP, FAST, SLOW
	}

	public static void main(String[] args) throws IOException {
		if (Boolean.parseBoolean(System.getProperty("nogui"))) {
			new ProgramStarter().runAsConsole();
		} else {
			new ProgramStarter().runAsGUI();
		}
	}

	private void runAsGUI() {
		new EvilGUI(this);
	}

	private void runAsConsole() {
		ControlUnit cu = null;
		try {
			cu = generateControlUnitFromInput(getFilename());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}

		ConsolePrinter p = new ConsolePrinter();

		Mode mode = Mode.valueOf(System.getProperty("mode", "STEP"));
		System.out.println("Mode: " + mode);
		switch (mode) {
		case STEP:
			runStep(cu, p);
			break;
		case FAST:
			speedishRun(cu, p, Integer.parseInt(System.getProperty("fasttimeout", "0")));
			break;
		case SLOW:
			speedishRun(cu, p, Integer.parseInt(System.getProperty("slowtimeout", "2000")));
			break;

		}
	}

	private void speedishRun(ControlUnit cu, Printer p, long timeout) {
		int steps = 0;
		p.print(cu, steps++);
		while (cu.runCycle()) {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException ie) {
				// something went drastically wrong
				Thread.currentThread().interrupt();
			}
			p.print(cu, steps++);
		}
	}

	private void runStep(ControlUnit cu, Printer cp) {
		Scanner sc = new Scanner(System.in);
		int steps = 0;

		cp.print(cu, steps++);
		sc.nextLine();
		while (cu.runCycle()) {
			cp.print(cu, steps++);
			sc.nextLine();
		}
	}

	public ControlUnit generateControlUnitFromInput(String filename) throws IOException {
		InputReader reader = new InputReader(filename);
		String[] mnemonics = reader.readContents();
		MainMemory mainMemory = createMainMemory(mnemonics);
		return new ControlUnit(mainMemory);
	}

	public static MainMemory createMainMemory(String[] mnemonics) {
		Assembler asm = new Assembler();
		MainMemory mainMemory = new MainMemory();
		int cnt = 100;
		for (String mnemonic : mnemonics) {
			if (mnemonic.contains("=")) {
				String[] parts = mnemonic.split("=");
				mainMemory.writeData(Short.parseShort(parts[0]), Short.parseShort(parts[1]));
			} else {
				try {
					mainMemory.setInstruction(cnt, asm.assemble(mnemonic));
					cnt += 2;
				} catch (InvalidInstructionException iie) {
					System.err.println("Instruktion Nr. " + (cnt - 100) + " kann nicht assembliert werden: "
							+ iie.getMessage());
					System.exit(-1);
				}
			}
		}
		return mainMemory;
	}

	private static String getFilename() throws IOException {
		String asmFile = System.getProperty("asm");
		if (asmFile != null && new File(asmFile).canRead()) {
			return asmFile;
		} else {
			System.err.println("Etwas stimmt mit der Eingabe nicht:");
			if (asmFile == null) {
				System.err.println("- Es wurde kein File angegeben.");
			} else {
				System.err.println("- Das File ist nicht lesbar/existiert nicht: " + asmFile);
			}
			System.err.println("\n Das Programm muss folgendermassen gestartet werden:");
			System.err.println("\tjava -Dasm=/home/usr/input.asm " + ProgramStarter.class.getCanonicalName());
			System.exit(-1);
			return null; // never happens
		}
	}
}
