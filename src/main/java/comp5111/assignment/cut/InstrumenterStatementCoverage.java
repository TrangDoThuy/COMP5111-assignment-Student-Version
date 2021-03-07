package comp5111.assignment.cut;

import soot.*;
import soot.jimple.*;
import soot.jimple.parser.node.TBoolConstant;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class InstrumenterStatementCoverage extends BodyTransformer {

//	static HashMap<SootMethod,ArrayList>
    /* some internal fields */
    static SootClass counterClass;
//    static SootMethod addStaticInvocationMethod, addInstanceInvocationMethod;
    static SootMethod addStatement;
    public static int noStatement = 0;

    static {
        counterClass = Scene.v().loadClassAndSupport("comp5111.assignment.cut.Counter");
        addStatement = counterClass.getMethod("void addStatement(int,int)");
//        addInstanceInvocationMethod = counterClass.getMethod("void addInstanceInvocation(int)");
    }

    /*
     * internalTransform goes through a method body and inserts counter
     * instructions before method returns
     */
    @Override
    protected synchronized void internalTransform(Body body, String phase, Map options) {
        // body's method
        SootMethod method = body.getMethod();

        // we dont instrument constructor (<init>) and static initializer (<clinit>)
        if (method.isConstructor() || method.isStaticInitializer()) {
            return;
        }

        // debugging
//        System.out.println("");
//        System.out.println("---------");
        System.out.println("instrumenting method: " + method.getSignature());

        // get body's unit as a chain
        Chain<Unit> units = body.getUnits();

        // get a snapshot iterator of the unit since we are going to
        // mutate the chain when iterating over it.
        //
        Iterator<?> stmtIt = units.snapshotIterator();
        Stmt firstStmt = (Stmt) stmtIt.next();
        if(!(firstStmt instanceof InvokeStmt)) {
        	noStatement++;
        }
        if(stmtIt.hasNext()) {
	        Stmt nextStmt = (Stmt) stmtIt.next();
	        if(!(nextStmt instanceof InvokeStmt)) {
	        	noStatement++;
	        }
	        int noParameter =1;
	        if(firstStmt.toString().contains("parameter")&& (nextStmt.toString().contains("parameter"))){
	            noParameter+=1;
	        }else {
	            if ((!(nextStmt instanceof InvokeStmt)) && (!(nextStmt.toString().contains("parameter")))) {
	            	InvokeExpr incExpr = null;
	                int stmtHashcode = firstStmt.hashCode();
	                incExpr = Jimple.v().newStaticInvokeExpr(
	                    addStatement.makeRef(), IntConstant.v(2), IntConstant.v(stmtHashcode));
	                Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
	                units.insertAfter(incStmt, firstStmt);
	            }
	        }
	        // typical while loop for iterating over each statement
	        while (stmtIt.hasNext()) {
	            // cast back to a statement.
	            Stmt stmt = nextStmt;
	            nextStmt = (Stmt) stmtIt.next();
		        if(!(nextStmt instanceof InvokeStmt)) {
		        	noStatement++;
		        }
	            if(stmt.toString().contains("parameter")){
	                noParameter+=1;
	                if((!(nextStmt instanceof InvokeStmt)) &&(!nextStmt.toString().contains("parameter"))){
	                    InvokeExpr incExpr = null;
                        int stmtHashcode = stmt.hashCode();
	                    incExpr = Jimple.v().newStaticInvokeExpr(
	                        addStatement.makeRef(), IntConstant.v(noParameter), IntConstant.v(stmtHashcode));
	                    Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
	                    units.insertAfter(incStmt, stmt);
	                }
	            }else {
	                if ((!(stmt instanceof InvokeStmt)) && (!(nextStmt instanceof InvokeStmt))) {
	                    InvokeExpr incExpr = null;
                        int stmtHashcode = stmt.hashCode();
	                    incExpr = Jimple.v().newStaticInvokeExpr(
	                        addStatement.makeRef(), IntConstant.v(1), IntConstant.v(stmtHashcode));
	                    Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
	                    units.insertAfter(incStmt, stmt);
	                }
	            }

	        }

        }else {
        	InvokeExpr incExpr = null;
            int stmtHashcode = firstStmt.hashCode();
            incExpr = Jimple.v().newStaticInvokeExpr(
                addStatement.makeRef(), IntConstant.v(1), IntConstant.v(stmtHashcode));
            Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
            units.insertAfter(incStmt, firstStmt);
        }
    }
}
