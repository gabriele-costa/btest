package it.unige.cseclab.instr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unige.cseclab.log.Log;
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
import soot.validation.ValidationException;

public class MethodCallInliner {

    public final static String TAG = "GACALL";

    public final static String WORK_DIR = "./out";

    public static String[] args = {"-v", "-d", WORK_DIR};

    public static void instrument(String apk, Set<String> api) {

        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk);

        //output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_dex);

        Options.v().set_android_jars("/home/avalz/Android/Sdk/platforms/");

        Options.v().set_process_dir(Collections.singletonList(apk));

        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);

        // Options.v().set_android_api_version(19); // 4.4.2
        // Options.v().set_android_api_version(22); // 5.1

        // resolve the PrintStream and System soot-classes
        //Scene.v().addBasicClass("android.os.Debug",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        Scene.v().addBasicClass("android.util.Log", SootClass.SIGNATURES);

        ProcessManifest processMan = null;
        try {
            processMan = new ProcessManifest(apk);
        } catch (IOException | XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (processMan != null) {
            processMan.addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
            processMan.getActivities();
            //System.out.println(processMan.targetSdkVersion());
        }

        PackManager.v().getPack("jtp").add(
                new Transform("jtp.instrumentApi", new BodyTransformer() {

                    @Override
                    protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {

                        final PatchingChain<Unit> units = b.getUnits();
                        //important to use snapshotIterator here
                        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
                            final Unit u = iter.next();
                            u.apply(new AbstractStmtSwitch() {

                                public void caseInvokeStmt(InvokeStmt stmt) {

                                    InvokeExpr invokeExpr = stmt.getInvokeExpr();
                                    String signature = invokeExpr.getMethod().getSignature();
                                    if (api.contains(signature)) {


                                        Local tmpTagStr = addTmpTagStr(b);
                                        Local tmpMsgStr = addTmpMsgStr(b);
                                        Local tmpRetInt = addTmpRetInt(b);

                                        // insert "tmpTagStr = 'GACALL';"
                                        units.insertBefore(Jimple.v().newAssignStmt(tmpTagStr,
                                                StringConstant.v(TAG)), u);

                                        // insert "tmpMsgStr = method signature;"
                                        units.insertBefore(Jimple.v().newAssignStmt(tmpMsgStr,
                                                StringConstant.v(signature)), u);
                                        // TODO: tmpMsgStr needs parameter values
                                        // insert "tmpRef.println(tmpString);"
                                        SootMethod toCall = Scene.v().getSootClass("android.util.Log").
                                                getMethod("int i(java.lang.String,java.lang.String)");
                                        //units.insertBefore(Jimple.v().newVirtualInvokeExpr(tmpTagStr, toCall.makeRef(), tmpMsgStr), u);
                                        List<Value> vals = new ArrayList<>();
                                        vals.add(tmpTagStr);
                                        vals.add(tmpMsgStr);
                                        units.insertBefore(Jimple.v().newAssignStmt(tmpRetInt,
                                                Jimple.v().newStaticInvokeExpr(toCall.makeRef(), tmpTagStr, tmpMsgStr)), u);
                                        //check that we did not mess up the Jimple
                                        List<ValidationException> exceptionList = new ArrayList<>();
                                        b.validate(exceptionList);

                                        if (!exceptionList.isEmpty()) {
                                            for (ValidationException e : exceptionList) {
                                                System.out.println(e);
                                            }

                                            System.exit(0);
                                        }
                                    }
                                }

                            });
                        }
                    }
                }));

        PackManager.v().getPack("jtp").add(
                new Transform("jtp.instrumentCall", new BodyTransformer() {

                    @Override
                    protected void internalTransform(Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                        PatchingChain<Unit> units = b.getUnits();

                        int paramNum = b.getMethod().getParameterCount();
                        boolean isStatic = b.getMethod().isStatic();

                        // If static, add 1
                        paramNum += (isStatic) ? 0 : 1;

                        Iterator<Unit> iter = units.iterator();

                        Unit u = null;

                        for ( int i = 0; i < paramNum; i++) {
                            u = iter.next();
                        }

                        Local tmpTagStr = addTmpTagStr(b);
                        Local tmpMsgStr = addTmpMsgStr(b);
                        Local tmpRetInt = addTmpRetInt(b);

                        SootMethod toCall = Scene.v().getSootClass("android.util.Log").
                                getMethod("int i(java.lang.String,java.lang.String)");
                        //units.insertBefore(Jimple.v().newVirtualInvokeExpr(tmpTagStr, toCall.makeRef(), tmpMsgStr), u);
                        List<Value> vals = new ArrayList<>();
                        vals.add(tmpTagStr);
                        vals.add(tmpMsgStr);
                        // 3rd instruction

                        if (u != null) {
                            units.insertAfter(Jimple.v().newAssignStmt(tmpRetInt,
                                    Jimple.v().newStaticInvokeExpr(toCall.makeRef(), tmpTagStr, tmpMsgStr)), u);

                            // 2nd instruction
                            // insert "tmpMsgStr = method signature;"
                            units.insertAfter(Jimple.v().newAssignStmt(tmpMsgStr,
                                    StringConstant.v(b.getMethod().getSignature())), u);

                            // 1st instruction
                            // insert "tmpTagStr = 'GACALL';"
                            units.insertAfter(Jimple.v().newAssignStmt(tmpTagStr,
                                    StringConstant.v(TAG)), u);
                        } else {

                            units.addFirst(Jimple.v().newAssignStmt(tmpRetInt,
                                    Jimple.v().newStaticInvokeExpr(toCall.makeRef(), tmpTagStr, tmpMsgStr)));

                            // 2nd instruction
                            // insert "tmpMsgStr = method signature;"
                            units.addFirst(Jimple.v().newAssignStmt(tmpMsgStr,
                                    StringConstant.v(b.getMethod().getSignature())));

                            // 1st instruction
                            // insert "tmpTagStr = 'GACALL';"
                            units.addFirst(Jimple.v().newAssignStmt(tmpTagStr,
                                    StringConstant.v(TAG)));
                        }
                        //check that we did not mess up the Jimple
                        List<ValidationException> exceptionList = new ArrayList<>();
                        b.validate(exceptionList);

                        if (!exceptionList.isEmpty()) {
                            for (ValidationException e : exceptionList) {
                                System.out.println(e);
                            }

                            System.exit(0);
                        }

                    }
                }));

        soot.Main.main(args);
    }

    private static Local addTmpTagStr(Body body) {
        Local tmpString = Jimple.v().newLocal("tmpTagStr", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }

    private static Local addTmpMsgStr(Body body) {
        Local tmpString = Jimple.v().newLocal("tmpMsgStr", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }

    private static Local addTmpRetInt(Body body) {
        Local tmpInt = Jimple.v().newLocal("tmpRetInt", RefType.v("int"));
        body.getLocals().add(tmpInt);
        return tmpInt;
    }

}
