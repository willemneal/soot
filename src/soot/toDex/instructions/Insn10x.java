package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction10x;

import soot.toDex.Register;

/**
 * The "10x" instruction format: It needs one 16-bit code unit, does not have any registers
 * and is used for general purposes (hence the "x").<br>
 * <br>
 * It is used by the opcodes "nop" and "return-void".
 */
public class Insn10x extends AbstractInsn {

	public Insn10x(Opcode opc) {
		super(opc);
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction10x(opc);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		return new BitSet(0);
	}

	public Insn10x shallowCloneWithRegs(List<Register> newRegs) {
		Insn10x shallowClone = new Insn10x(getOpcode());
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}