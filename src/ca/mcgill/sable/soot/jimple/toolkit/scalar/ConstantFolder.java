
package ca.mcgill.sable.soot.jimple.toolkit.scalar;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import java.io.*;
import java.util.*;


public class ConstantFolder {

    static boolean debug = false;
    static boolean verbose = false;

    static int numFolded;
    static HashSet visited;

    public static void foldConstants(StmtBody body) {

	StmtList stmtList = body.getStmtList();
	Iterator stmtIt = stmtList.iterator();
	Stmt stmt;
	Iterator useIt;

	verbose = ca.mcgill.sable.soot.Main.isVerbose;
	debug = ca.mcgill.sable.soot.Main.isInDebugMode;

	if (verbose)
	    System.out.println("    ... starting constant folder ...");

	numFolded = 0;
	visited = new HashSet();

	while (stmtIt.hasNext()) {
	    stmt = (Stmt)stmtIt.next();

	    useIt = stmt.getUseBoxes().iterator();
	    while (useIt.hasNext())
		foldValue((ValueBox)useIt.next(), 0);
	}

	if (verbose) {
	    if (numFolded == 0)
		System.out.println("    --- no folding opportunities ---");
	    else
		System.out.println("    ---folded " + numFolded +
				   " expressions ---");
	}
    } // foldConstants

    private static void foldValue(ValueBox opBox, int level) {
	StringBuffer indent = new StringBuffer("      ");
	Value op = opBox.getValue();

	// indent to show recursive level
	if (debug || verbose) {
	    for (int i = 0; i < level; i++)
		indent.append("  ");
	}

	// visit each node only once
	if (visited.contains(op)) {
	    if (debug)
		System.out.println(indent + "already seen " + op);
	    return;
	}
	
	else if (op instanceof BinopExpr) {
	    BinopExpr binop = (BinopExpr)op;
	    if (debug)
		System.out.println(indent + "considering binop " + op);
	    
	    // fold operands recursively
	    foldValue(binop.getOp1Box(), level+1);
	    foldValue(binop.getOp2Box(), level+1);

	    // see if we can fold current binop
	    if (!(binop.getOp1() instanceof Constant) ||
		!(binop.getOp2() instanceof Constant))
		return;

	    Type binopType = binop.getType();
	    
	    if (binopType instanceof IntType) {
		int foldedValue;
		IntConstant op1 = (IntConstant)binop.getOp1();
		IntConstant op2 = (IntConstant)binop.getOp2();
	    
		if (binop instanceof AddExpr)
		    foldedValue = op1.value + op2.value;
		else if (binop instanceof DivExpr)
		    foldedValue = op1.value / op2.value;
		else if (binop instanceof MulExpr)
		    foldedValue = op1.value * op2.value;
		else if (binop instanceof SubExpr)
		    foldedValue = op1.value - op2.value;
		else if (binop instanceof EqExpr) {
		    if (op1.value == op2.value)
			foldedValue = 1;
		    else
			foldedValue = 0;
		}
		else if (binop instanceof NeExpr) {
		    if (op1.value != op2.value)
			foldedValue = 1;
		    else
			foldedValue = 0;
		}
		else {
		    System.out.println("  *** unknown IntBinopExpr: " + op);
		    return;
		}
		if (opBox.canContainValue(IntConstant.v(foldedValue))) {
		    opBox.setValue(IntConstant.v(foldedValue));
		    if (verbose)
			System.out.println(indent + "folded (" + op + ") to " +
					   foldedValue);
		}
		else {
		    // not folded, do not count
		    numFolded--;
		    if (verbose)
			System.out.println(indent +
					   "box cannot store value; " +
					   "not folded");
		}
	    }
	    else if (binopType instanceof LongType) {
		long foldedValue;
		LongConstant op1 = (LongConstant)binop.getOp1();
		LongConstant op2 = (LongConstant)binop.getOp2();
	    
		if (binop instanceof AddExpr)
		    foldedValue = op1.value + op2.value;
		else if (binop instanceof DivExpr)
		    foldedValue = op1.value / op2.value;
		else if (binop instanceof MulExpr)
		    foldedValue = op1.value * op2.value;
		else if (binop instanceof SubExpr)
		    foldedValue = op1.value - op2.value;
		else {
		    System.out.println("  *** unknown LongBinopExpr: " + op);
		    return;
		}
		opBox.setValue(LongConstant.v(foldedValue));
		if (verbose)
		    System.out.println(indent + "folded (" + op + ") to " +
				       foldedValue);
	    }
	    else if (binopType instanceof FloatType) {
		float foldedValue;
		FloatConstant op1 = (FloatConstant)binop.getOp1();
		FloatConstant op2 = (FloatConstant)binop.getOp2();
	    
		if (binop instanceof AddExpr)
		    foldedValue = op1.value + op2.value;
		else if (binop instanceof DivExpr)
		    foldedValue = op1.value / op2.value;
		else if (binop instanceof MulExpr)
		    foldedValue = op1.value * op2.value;
		else if (binop instanceof SubExpr)
		    foldedValue = op1.value - op2.value;
		else {
		    System.out.println("  *** unknown FloatBinopExpr: " + op);
		    return;
		}
		opBox.setValue(FloatConstant.v(foldedValue));
		if (verbose)
		    System.out.println(indent + "folded (" + op + ") to " +
				       foldedValue);
	    }
	    else if (binopType instanceof DoubleType) {
		double foldedValue;
		DoubleConstant op1 = (DoubleConstant)binop.getOp1();
		DoubleConstant op2 = (DoubleConstant)binop.getOp2();
	    
		if (binop instanceof AddExpr)
		    foldedValue = op1.value + op2.value;
		else if (binop instanceof DivExpr)
		    foldedValue = op1.value / op2.value;
		else if (binop instanceof MulExpr)
		    foldedValue = op1.value * op2.value;
		else if (binop instanceof SubExpr)
		    foldedValue = op1.value - op2.value;
		else {
		    System.out.println("  *** unknown DoubleBinopExpr: " + op);
		    return;
		}
		opBox.setValue(DoubleConstant.v(foldedValue));
		if (verbose)
		    System.out.println(indent + "folded (" + op + ") to " +
				       foldedValue);
	    }

	    numFolded++;
	}

	else if (op instanceof UnopExpr) {
	    if (debug)
		System.out.println(indent + "considering unop " + op);

	    UnopExpr unop = (UnopExpr)op;
	    foldValue(unop.getOpBox(), level+1);
	    if (!(unop.getOp() instanceof Constant))
		return;
	    
	    Type unopType = unop.getType();
	    if (unopType instanceof IntType) {
		int foldedValue;
		IntConstant op1 = (IntConstant)unop.getOp();
		
		if (unop instanceof NegExpr)
		    foldedValue = -op1.value;
		else {
		    System.out.println("  *** unknown UnopExpr: " + op);
		    return;
		}
		opBox.setValue(IntConstant.v(foldedValue));
		if (verbose)
		    System.out.println(indent + "folded (" + op + ") to " +
				       foldedValue);
	    }
	    else if (unopType instanceof LongType) {
		long foldedValue;
		LongConstant op1 = (LongConstant)unop.getOp();
		
		if (unop instanceof NegExpr)
		    foldedValue = -op1.value;
		else {
		    System.out.println("  *** unknown UnopExpr: " + op);
		    return;
		}
		opBox.setValue(LongConstant.v(foldedValue));
		if (verbose)
		    System.out.println(indent + "folded (" + op + ") to " +
				       foldedValue);
	    }
	    if (unopType instanceof FloatType) {
		float foldedValue;
		FloatConstant op1 = (FloatConstant)unop.getOp();
		
		if (unop instanceof NegExpr)
		    foldedValue = -op1.value;
		else {
		    System.out.println("  *** unknown UnopExpr: " + op);
		    return;
		}
		opBox.setValue(FloatConstant.v(foldedValue));
		if (verbose)
		    System.out.println(indent + "folded (" + op + ") to " +
				       foldedValue);
	    }
	    if (unopType instanceof DoubleType) {
		double foldedValue;
		DoubleConstant op1 = (DoubleConstant)unop.getOp();
		
		if (unop instanceof NegExpr)
		    foldedValue = -op1.value;
		else {
		    System.out.println("  *** unknown UnopExpr: " + op);
		    return;
		}
		opBox.setValue(DoubleConstant.v(foldedValue));
		if (verbose)
		    System.out.println(indent + "folded (" + op + ") to " +
				       foldedValue);
	    }
	}
	else if (debug)
	    System.out.println(indent + "ignoring " + op);

	visited.add(op);
    } // foldExpr

} // ConstantFolder
    




