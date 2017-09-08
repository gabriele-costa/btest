package it.unige.cseclab.instr;

import it.unige.cseclab.log.Log;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author avalz
 * @since 08/09/17
 */
public abstract class Instrumenter extends BodyTransformer {

    static Local addTmpTagStr(Body body) {
        Local tmpString = Jimple.v().newLocal("tmpTagStr", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }

    static Local addTmpMsgStr(Body body) {
        Local tmpString = Jimple.v().newLocal("tmpMsgStr", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }

    static Local addTmpRetInt(Body body) {
        Local tmpInt = Jimple.v().newLocal("tmpRetInt", RefType.v("int"));
        body.getLocals().add(tmpInt);
        return tmpInt;
    }

    static Local addTmpRetStringBuffer(Body body) {
        Local tmpStringBuffer = Jimple.v().newLocal("tmpStringBuffer", RefType.v("java.lang.StringBuffer"));
        body.getLocals().add(tmpStringBuffer);
        return tmpStringBuffer;
    }

    static Local addDelimiterStr(Body body) {
        Local tmpString = Jimple.v().newLocal("tmpDelimiterStr", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }

    static List<Unit> buildLogMessage(
            Local tmpMsgStr,
            Local tmpStringBuffer,
            String signature,
            List<Value> values,
            Local tmpDelimiterStr
    ) {

        List<Unit> units = new ArrayList<>();

        SootClass stringBufferClazz = Scene.v().getSootClass("java.lang.StringBuffer");

        // tmpStringBuffer = new StringBuffer("signature");
        units.add(Jimple.v().newAssignStmt(tmpStringBuffer,
                Jimple.v().newNewExpr((RefType) tmpStringBuffer.getType())));

        StringConstant constSignature = StringConstant.v(signature);
        units.add(Jimple.v().newAssignStmt(
                tmpDelimiterStr,
                constSignature
        ));
        units.add(
                Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(
                        tmpStringBuffer,
                        stringBufferClazz.getMethod("void <init>(java.lang.String)").makeRef(),
                        tmpDelimiterStr
                        )
                )
        );


        for (Value v : values) {
            // tmpStringBuffer.append(":");
            StringConstant v1 = StringConstant.v(":");
            units.add(Jimple.v().newAssignStmt(
                    tmpDelimiterStr,
                    v1
            ));
            units.add(Jimple.v().newInvokeStmt(
                    Jimple.v().newVirtualInvokeExpr(
                            tmpStringBuffer,
                            stringBufferClazz
                                    // È impossibile che dia problemi qui
                                    .getMethod("java.lang.StringBuffer append(java.lang.String)")
                                    .makeRef(),
                            tmpDelimiterStr
                    )
            ));

            // tmpStringBuffer.append(v);
            Type type = v.getType();
            if (type instanceof AnySubType) {
                Log.log("Unsupported param");
            } else if (type instanceof ArrayType) {
                Log.log("Unsupported param");
            } else if (type instanceof BooleanType) {
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(boolean)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof ByteType) {
                Log.log("Unsupported param");
            } else if (type instanceof CharType) {
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(char)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof DoubleType) {
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(double)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof FloatType) {
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(float)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof IntegerType) {
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(int)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof IntType) {
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(int)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof LongType) {
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(long)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof NullType) {
                // TODO: Smettere di segnare tutto quello che Gabriele dice
                Log.log("Non vuol dire niente in Java");
            } else if (type instanceof ShortType) {
                // TODO: Test me
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        // È impossibile che dia problemi qui
                                        .getMethod("java.lang.StringBuffer append(int)")
                                        .makeRef(),
                                v
                        )
                ));
            } else if (type instanceof RefType) {
                // Questa cosa mi spaventa e mi fa sentire stupido

                StringConstant v2 = StringConstant.v("'");
                units.add(Jimple.v().newAssignStmt(
                        tmpDelimiterStr,
                        v2
                ));
                // tmpStringBuffer.append("\"");
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        // È impossibile che dia problemi qui
                                        .getMethod("java.lang.StringBuffer append(java.lang.String)")
                                        .makeRef(),
                                tmpDelimiterStr
                        )
                ));
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        // È impossibile che dia problemi qui
                                        .getMethod("java.lang.StringBuffer append(java.lang.Object)")
                                        .makeRef(),
                                v
                        )
                ));
                // tmpStringBuffer.append("\"");
                units.add(Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        // È impossibile che dia problemi qui
                                        .getMethod("java.lang.StringBuffer append(java.lang.String)")
                                        .makeRef(),
                                tmpDelimiterStr
                        )
                ));
            } else if (type instanceof VoidType) {
                Log.log("Unsupported param");
            }


        }

        units.add(Jimple.v().newAssignStmt(
                tmpMsgStr,
                Jimple.v().newVirtualInvokeExpr(
                        tmpStringBuffer,
                        stringBufferClazz.getMethod("java.lang.String toString()").makeRef()
                )
        ));

        return units;
    }
}
