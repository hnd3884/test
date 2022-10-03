package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class ConcatCall extends FunctionCall
{
    public ConcatCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        for (int i = 0; i < this.argumentCount(); ++i) {
            final Expression exp = this.argument(i);
            if (!exp.typeCheck(stable).identicalTo(Type.String)) {
                this.setArgument(i, new CastExpr(exp, Type.String));
            }
        }
        return this._type = Type.String;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int nArgs = this.argumentCount();
        switch (nArgs) {
            case 0: {
                il.append(new PUSH(cpg, ""));
                break;
            }
            case 1: {
                this.argument().translate(classGen, methodGen);
                break;
            }
            default: {
                final int initBuffer = cpg.addMethodref("java.lang.StringBuffer", "<init>", "()V");
                final Instruction append = new INVOKEVIRTUAL(cpg.addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
                final int toString = cpg.addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
                il.append(new NEW(cpg.addClass("java.lang.StringBuffer")));
                il.append(ConcatCall.DUP);
                il.append(new INVOKESPECIAL(initBuffer));
                for (int i = 0; i < nArgs; ++i) {
                    this.argument(i).translate(classGen, methodGen);
                    il.append(append);
                }
                il.append(new INVOKEVIRTUAL(toString));
                break;
            }
        }
    }
}
