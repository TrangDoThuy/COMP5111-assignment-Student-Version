package comp5111.assignment.cut;

import soot.*;
import soot.jimple.*;
import soot.jimple.parser.node.TBoolConstant;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class InstrumenterBranchCoverage extends BodyTransformer {

//	static HashMap<SootMethod,ArrayList>
    /* some internal fields */
    static SootClass counterClass;
//    static SootMethod addStaticInvocationMethod, addInstanceInvocationMethod;
    static SootMethod addBranch;
    public static int noBranch = 0;

    static {
        counterClass = Scene.v().loadClassAndSupport("comp5111.assignment.cut.Counter");
        addBranch = counterClass.getMethod("void addBranch(int,int)");
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
        // typical while loop for iterating over each statement
        Stmt nextStmt = (Stmt) stmtIt.next();
        Stmt stmt = null;
        noBranch++;
        Stmt preStmt = null;
//        if(stmtIt.hasNext()) {
//        	Stmt nextStmt = (Stmt) stmtIt.next();
//        	if (nextStmt instanceof IfStmt) {
//        		noBranch++;
//        	}
//        	Stmt preStmt = stmt;
//        	System.out.println(stmt);
        	while (stmtIt.hasNext()) {
	            // cast back to a statement.
	        	preStmt = stmt;
	            stmt = nextStmt;
//	            System.out.println(stmt);
	            nextStmt = (Stmt) stmtIt.next();
	        	if (nextStmt instanceof IfStmt) {
	        		noBranch++;
	        	}
	        	int chooseBranch = 0;
	            if ((!(stmt instanceof InvokeStmt)) && (nextStmt instanceof IfStmt)&&(!(preStmt instanceof InvokeStmt))) {
	                InvokeExpr incExpr = null;
	                int stmtHashcode = stmt.hashCode();
	                incExpr = Jimple.v().newStaticInvokeExpr(addBranch.makeRef(), IntConstant.v(1), IntConstant.v(stmtHashcode));
	                Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
	                units.insertAfter(incStmt, stmt);
//	                units.insertAfter(incStmt, nextStmt);
	                
	            }
	            if ((stmt instanceof IfStmt) && (!(nextStmt instanceof InvokeStmt))){
	                InvokeExpr incExpr = null;
	                int stmtHashcode = stmt.hashCode();
	                incExpr = Jimple.v().newStaticInvokeExpr(addBranch.makeRef(), IntConstant.v(0), IntConstant.v(stmtHashcode));
	                Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
	                units.insertAfter(incStmt, stmt);
//	                units.insertAfter(incStmt, nextStmt);
	                
	            }
	            
        	}
        
//        System.out.println(noBranch);
    }
}
