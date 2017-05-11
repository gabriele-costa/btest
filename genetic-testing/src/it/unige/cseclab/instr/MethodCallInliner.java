package it.unige.cseclab.instr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.options.Options;

public class MethodCallInliner {
	
	public final static String TAG = "GACALL";
	
	public static String[] args = {"-v"};

	public static void instrument(String apk) {
		
		
		//prefer Android APK files// -src-prec apk
		Options.v().set_src_prec(Options.src_prec_apk);

		//output as APK, too//-f J
		Options.v().set_output_format(Options.output_format_dex);
		
		Options.v().set_android_jars("/home/gabriele/Android/Sdk/platforms/");
		
        Options.v().set_process_dir(Collections.singletonList(apk));

        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);

		// resolve the PrintStream and System soot-classes
		//Scene.v().addBasicClass("android.os.Debug",SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
		Scene.v().addBasicClass("android.util.Log",SootClass.SIGNATURES);
		
		ProcessManifest processMan = null;
		try {
			processMan = new ProcessManifest(apk);
		} catch (IOException | XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(processMan != null) {
			processMan.addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
			processMan.getActivities();
			//System.out.println(processMan.targetSdkVersion());
		}
		
		PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

			@Override
			protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
				
				final PatchingChain<Unit> units = b.getUnits();		
				//important to use snapshotIterator here
				for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
					final Unit u = iter.next();
					u.apply(new AbstractStmtSwitch() {

						public void caseInvokeStmt(InvokeStmt stmt) {
							InvokeExpr invokeExpr = stmt.getInvokeExpr();

							Local tmpTagStr = addTmpTagStr(b);
							Local tmpMsgStr = addTmpMsgStr(b);
							//Local tmpRetInt = addTmpRetInt(b);

						    // insert "tmpTagStr = 'GACALL';" 
						    units.insertBefore(Jimple.v().newAssignStmt(tmpTagStr, 
						                  StringConstant.v(TAG)), u);
						    
						 // insert "tmpMsgStr = method signature;" 
						    units.insertBefore(Jimple.v().newAssignStmt(tmpTagStr, 
						                  StringConstant.v(invokeExpr.getMethod().getSignature())), u);
						    
						    // insert "tmpRef.println(tmpString);" 
						    SootMethod toCall = Scene.v().getSootClass("android.util.Log").getMethod("int i(java.lang.String,java.lang.String)");                    
						    //units.insertBefore(Jimple.v().newVirtualInvokeExpr(tmpTagStr, toCall.makeRef(), tmpMsgStr), u);
						    List<Value> vals = new ArrayList<>();
						    vals.add(tmpTagStr);
						    vals.add(tmpMsgStr);
						    units.insertBefore(Jimple.v().newInvokeStmt(
						    		Jimple.v().newStaticInvokeExpr(toCall.makeRef(), vals)), u);
						    //check that we did not mess up the Jimple
						    b.validate();
						}

					});
				}
			}
		}));
		
		soot.Main.main(args);
	}
    
    private static Local addTmpTagStr(Body body)
    {
        Local tmpString = Jimple.v().newLocal("tmpTagStr", RefType.v("java.lang.String")); 
        body.getLocals().add(tmpString);
        return tmpString;
    }
	
    private static Local addTmpMsgStr(Body body)
    {
        Local tmpString = Jimple.v().newLocal("tmpMsgStr", RefType.v("java.lang.String")); 
        body.getLocals().add(tmpString);
        return tmpString;
    }
    
    private static Local addTmpRetInt(Body body)
    {
        Local tmpInt = Jimple.v().newLocal("tmpRetInt", RefType.v("int")); 
        body.getLocals().add(tmpInt);
        return tmpInt;
    }
	
}
