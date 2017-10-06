package it.unige.cseclab.instr;

import it.unige.cseclab.log.Log;
import soot.*;
import soot.jimple.*;

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

    static Local addTmpHashInt(Body body) {
        Local tmpInt = Jimple.v().newLocal("tmpHashInt", RefType.v("int"));
        body.getLocals().add(tmpInt);
        return tmpInt;
    }

    static Local addTmpParam(Body body) {
        Local tmpParam = Jimple.v().newLocal("tmpParam", RefType.v("java.lang.Object"));
        body.getLocals().add(tmpParam);
        return tmpParam;
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
            Local tmpDelimiterStr,
            Body body
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



        // Object tmpParam;
        Local tmpParam = addTmpParam(body);

        Local hashCode = addTmpHashInt(body);

        // boolean isString;
        Local isString = Jimple.v().newLocal("tmpIsString", RefType.v("boolean"));
        body.getLocals().add(isString);

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
                Log.log("Unsupported param: AnySubType");
            } else if (type instanceof ArrayType) {
                Log.log("Unsupported param: ArrayType");
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
                Log.log("Unsupported param: ByteType");
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
                Log.log("MA CHE CAZZO DICI, AMANDA!");
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

                Unit getHashcode = Jimple.v().newAssignStmt(
                        hashCode,
                        // IntConstant.v(0)
                        Jimple.v().newVirtualInvokeExpr(
                                tmpParam,
                                Scene.v().getSootClass("java.lang.Object")
                                        .getMethod("int hashCode()").makeRef()
                        )
                );

                Unit stringAppend = Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        // È impossibile che dia problemi qui
                                        .getMethod("java.lang.StringBuffer append(java.lang.Object)")
                                        .makeRef(),
                                tmpParam
                        ));

                Unit objectAppend = Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        // È impossibile che dia problemi qui
                                        .getMethod("java.lang.StringBuffer append(int)")
                                        .makeRef(),
                                hashCode
                        )
                );

                // tmpDelimiterString = "'";
                units.add(Jimple.v().newAssignStmt(
                        tmpDelimiterStr,
                        v2
                ));

                // tmpStringBuffer.append(tmpDelimiterString); // "'"
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

                // sb.append("'");
                Unit lastAppend = Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(
                                tmpStringBuffer,
                                stringBufferClazz
                                        .getMethod("java.lang.StringBuffer append(java.lang.String)")
                                        .makeRef(),
                                tmpDelimiterStr
                        ));


                // tmpParam = v;
                units.add(
                        Jimple.v().newAssignStmt(
                                tmpParam,
                                v
                        )
                );

                // if (tmpParam == null)
                //      goto lastAppend;
                units.add(
                        Jimple.v().newIfStmt(
                                Jimple.v().newEqExpr(
                                        NullConstant.v(),
                                        tmpParam
                                ),
                                lastAppend
                        )
                );

                // isString = tmpParam instanceof String;
                units.add(Jimple.v().newAssignStmt(
                        isString,
                        Jimple.v().newInstanceOfExpr(tmpParam, RefType.v("java.lang.String"))
                ));

                // if (isString != 0)
                //      goto stringAppend;
                units.add(
                        Jimple.v().newIfStmt(
                                Jimple.v().newNeExpr(
                                        isString, // instanceof returns 0 if not instanceof
                                                  // and != 0 if instanceof
                                        IntConstant.v(0)
                                ),
                                stringAppend
                        )
                );

                // objectAppend:
                // tmpHashInt = v.hashCode();
                units.add(getHashcode);

                // tmpStringBuffer.append(tmpHashInt);
                units.add(objectAppend);

                // goto lastAppend;
                units.add(Jimple.v().newGotoStmt(lastAppend));

                // stringAppend:
                // stringParam = (String)v;
                // tmpStringBuffer.append(v);
                units.add(stringAppend);

                // lastAppend:
                // tmpStringBuffer.append("\"");
                units.add(lastAppend);

            } else if (type instanceof VoidType) {
                Log.log("Unsupported param: VoidType");
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
