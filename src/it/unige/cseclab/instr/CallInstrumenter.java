package it.unige.cseclab.instr;

import soot.*;
import soot.jimple.*;
import soot.validation.ValidationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static it.unige.cseclab.instr.MethodCallInliner.TAG;

/**
 * @author avalz
 * @since 08/09/17
 */
public class CallInstrumenter extends Instrumenter {

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

        String methodName = body.getMethod().getSignature();

        if (methodName.contains("hashCode") ||
                methodName.contains("valueOf") ||
                methodName.contains("toString") ||
                methodName.contains("configureSharedElementsUnoptimized") ||
                methodName.contains("android.")
                ) {
            return;
        }

        PatchingChain<Unit> units = body.getUnits();

        int paramNum = body.getMethod().getParameterCount();
        int skipNum = 0;
        boolean isStatic = body.getMethod().isStatic();
        boolean isConstructor = body.getMethod().isConstructor();

        // If static, add 1
        paramNum += (isStatic || isConstructor) ? 0 : 1;

        if (body.getMethod().getName().contains("init>")) {
            skipNum = 2;
        }

        Iterator<Unit> iter = units.iterator();

        Unit u = null;

        List<Value> params = new ArrayList<>();

        for (int i = 0; i < skipNum; i++) {
            u = iter.next();
        }

        for (int i = 0; i < paramNum; i++) {
            // $r0 := "stuff"
            u = iter.next();

            Value left = null;
            // Potrebbe dare errore

            /*
            if (u instanceof AssignStmt) {
                left = ((AssignStmt) u).getLeftOp();
            }
            */

            if (u instanceof IdentityStmt) {
                left = ((IdentityStmt) u).getLeftOp();
            }
                            /*
                            if (u instanceof AssignStmt) {
                                left = ((AssignStmt) u).getLeftOp();
                            }
                            else if (u instanceof InvokeStmt) {
                                left = ((InvokeStmt)u).getInvokeExprBox().getValue();
                            }
                            else {
                                System.err.println("WARNING: not Assign or Invoke Statement");
                            }
                            */

            if (left != null) params.add(left);
        }


        Local tmpTagStr = addTmpTagStr(body);
        Local tmpMsgStr = addTmpMsgStr(body);
        Local tmpRetInt = addTmpRetInt(body);
        Local tmpRetStringBuffer = addTmpRetStringBuffer(body);
        Local tmpDelimiterStr = addDelimiterStr(body);

        SootMethod toCall = Scene.v().getSootClass("android.util.Log").
                getMethod("int i(java.lang.String,java.lang.String)");
        //units.insertBefore(Jimple.v().newVirtualInvokeExpr(tmpTagStr, toCall.makeRef(), tmpMsgStr), u);
        // List<Value> vals = new ArrayList<>();
        // vals.add(tmpTagStr);
        // vals.add(tmpMsgStr);


        AssignStmt gaCall = Jimple.v().newAssignStmt(tmpTagStr,
                StringConstant.v(TAG));

        AssignStmt logStmt = Jimple.v().newAssignStmt(tmpRetInt,
                Jimple.v().newStaticInvokeExpr(
                        toCall.makeRef(),
                        tmpTagStr,
                        tmpMsgStr
                )
        );

        if (u != null) {

            // 1st instruction
            // insert "tmpTagStr = 'GACALL';"
            units.insertAfter(gaCall, u);

            // 2nd instruction
            // insert "tmpMsgStr = method signature;"
            // units.insertAfter(Jimple.v().newAssignStmt(tmpMsgStr,
            // StringConstant.v(b.getMethod().getSignature())), u);

            List<Unit> stringBufferList = buildLogMessage(
                    tmpMsgStr,
                    tmpRetStringBuffer,
                    body.getMethod().getSignature(),
                    params,
                    tmpDelimiterStr,
                    body
            );

            Unit last = gaCall;

            while (!stringBufferList.isEmpty()) {
                Unit temp = last;
                last = stringBufferList.remove(0);
                units.insertAfter(
                        last,
                        temp);
            }

            units.insertAfter(logStmt, last);


        } else {

            // 3rd instruction
            units.addFirst(logStmt);


            // 1st instruction
            // insert "tmpTagStr = 'GACALL';"
            units.insertBefore(gaCall,
                    logStmt);

            // 2nd instruction
            // insert "tmpMsgStr = method signature;"
            units.insertBefore(Jimple.v().newAssignStmt(tmpMsgStr,
                    StringConstant.v(body.getMethod().getSignature())),
                    logStmt);
        }
        //check that we did not mess up the Jimple
        List<ValidationException> exceptionList = new ArrayList<>();
        body.validate(exceptionList);

        if (!exceptionList.isEmpty()) {
            for (ValidationException e : exceptionList) {
                System.out.println(e);
            }

            System.exit(0);
        }

    }
}
