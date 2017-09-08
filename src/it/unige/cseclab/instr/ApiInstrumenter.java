package it.unige.cseclab.instr;

import soot.*;
import soot.jimple.*;
import soot.validation.ValidationException;

import java.util.*;

import static it.unige.cseclab.instr.MethodCallInliner.TAG;

/**
 * @author avalz
 * @since 08/09/17
 */
public class ApiInstrumenter extends Instrumenter {

    Set<String> api;

    public ApiInstrumenter(Set<String> api) {
        this.api = api;
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map options) {

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

                        units.insertBefore(Jimple.v().newAssignStmt(tmpRetInt,
                                Jimple.v().newStaticInvokeExpr(
                                        toCall.makeRef(),
                                        tmpTagStr,
                                        tmpMsgStr)
                        ), u);
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
}
