package comp5111.assignment.cut;


import soot.SootMethod;

import java.util.ArrayList;
import java.util.Hashtable;

public class Counter {
//    private static int numStaticInvocations = 0;
//    private static int numInstanceInvocations = 0;
    static ArrayList<String> methodList = new ArrayList();
    static ArrayList<String> branchList = new ArrayList();
	private static int numStatement = 0;
	private static int numBranch = 0;
	static Hashtable<String, Integer> branchTable= new Hashtable<String, Integer>();

//    public static void addStaticInvocation(int n) {
//        numStaticInvocations += n;
//    }
//
//    public static void addInstanceInvocation(int n) {
//        numInstanceInvocations += n;
//    }
//
//    public static int getNumInstanceInvocations() {
//        return numInstanceInvocations;
//    }
//
//    public static int getNumStaticInvocations() {
//        return numStaticInvocations;
//    }

	public static void addStatement(int n, int methodHashcode) {
//		System.out.println(methodHashcode);
		if(!methodList.contains(String.valueOf(methodHashcode))) {
            numStatement += n;
            methodList.add(String.valueOf(methodHashcode));
        }
	}
	public static void addBranch(int n, int branchHashcode) {
//		System.out.println(methodHashcode);
//		if(!branchList.contains(String.valueOf(branchHashcode))) {
//            numBranch += n;
//            branchList.add(String.valueOf(branchHashcode));
//        }
        if(!branchTable.containsKey(String.valueOf(branchHashcode))){
            numBranch+=n;
            branchTable.put(String.valueOf(branchHashcode),1);
        }else{
            if(branchTable.get(String.valueOf(branchHashcode))==0) {
//                numBranch += n;
            }
            branchTable.put(String.valueOf(branchHashcode), branchTable.get(String.valueOf(branchHashcode))+1);
        }
	}
	public static void addAndRemoveBranch(int branchHashcodeAdd,int branchHashCodeRemove){
        if(!branchTable.containsKey(String.valueOf(branchHashcodeAdd))){
            if(branchTable.get(String.valueOf(branchHashCodeRemove))==1){
                branchTable.remove(String.valueOf(branchHashCodeRemove));
            }
            branchTable.put(String.valueOf(branchHashcodeAdd),1);
        }else{
            if(branchTable.get(String.valueOf(branchHashcodeAdd))==0) {
                branchTable.put(String.valueOf(branchHashcodeAdd), 1);
            }
        }
        if(branchTable.containsKey(String.valueOf(branchHashCodeRemove))){
            branchTable.put(String.valueOf(branchHashCodeRemove), branchTable.get(String.valueOf(branchHashCodeRemove))-1);
        }
    }

	public static int getNumStatement() {
		return numStatement;
	}
	public static int getNumBranch() {
		return numBranch;
	}
}
