package comp5111.assignment.cut;

import org.junit.runner.JUnitCore;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.options.Options;

public class EntryPointBranchCoverage {
    public static void main(String[] args) {
        instrumentWithSoot();
        // after instrument, we run Junit tests
        runJunitTests();
        // after junit test running, we have already get the counting in the Counter class
//        System.out.println("Invocation to static methods: " + Counter.getNumStaticInvocations());
//        System.out.println("Invocation to instance methods: " + Counter.getNumInstanceInvocations());
        System.out.println("Invocation to branch: " + Counter.getNumBranch());
        System.out.println("Total branches: " + InstrumenterBranchCoverage.noBranch);
        System.out.println("Branch coverage: "+ (Counter.getNumBranch()*100/InstrumenterBranchCoverage.noBranch)+"%");
    }

 
    private static void instrumentWithSoot() {
        // the path to the compiled Subject class file
        String classUnderTestPath = "./raw-classes";
        String targetPath = "./target/classes";

        String classPathSeparator = ":";
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            classPathSeparator = ";";
        }

        /*Set the soot-classpath to include the helper class and class to analyze*/
        Options.v().set_soot_classpath(Scene.v().defaultClassPath() + classPathSeparator + targetPath + classPathSeparator + classUnderTestPath);

        // we set the soot output dir to target/classes so that the instrumented class can override the class file
        Options.v().set_output_dir(targetPath);

        // retain line numbers
        Options.v().set_keep_line_number(true);
        // retain the original variable names
        Options.v().setPhaseOption("jb", "use-original-names:true");

        /* add a phase to transformer pack by call Pack.add */
        Pack jtp = PackManager.v().getPack("jtp");
        InstrumenterBranchCoverage instrumenter = new InstrumenterBranchCoverage();
        jtp.add(new Transform("jtp.instrumenter", instrumenter));
//        comp5111.assignment.cut.ToolBox.ArrayTools
        String classUnderTestArrayTools = "comp5111.assignment.cut.ToolBox$ArrayTools";
        String classUnderTestCharSequenceTools = "comp5111.assignment.cut.ToolBox$CharSequenceTools";
        String classCharTools = "comp5111.assignment.cut.ToolBox$CharTools";
        String classLocaleTools = "comp5111.assignment.cut.ToolBox$LocaleTools";
        String classRegExTools = "comp5111.assignment.cut.ToolBox$RegExTools";
        String classStringTools = "comp5111.assignment.cut.ToolBox$StringTools";
        // pass arguments to soot
        soot.Main.main(new String[]{
        		classUnderTestArrayTools,
        		classUnderTestCharSequenceTools,
        		classCharTools,
        		classLocaleTools,
        		classRegExTools,
        		classStringTools
        		});
    }

    private static void runJunitTests() {
//    	System.out.println("Running");
        Class<?> testClass = null;
        try {
            // here we programmitically run junit tests
        	//COMP5111-assignment-Student-Version/src/test/randoop0/comp5111/assignment/cut/RegressionTest.java
            testClass = Class.forName("comp5111.assignment.cut.RegressionTest");
            JUnitCore junit = new JUnitCore();
            System.out.println("Running junit test: " + testClass.getName());
            junit.run(testClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
