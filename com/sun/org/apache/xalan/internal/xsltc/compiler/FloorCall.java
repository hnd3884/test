package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import java.util.Vector;

final class FloorCall extends FunctionCall
{
    public FloorCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this.argument().translate(classGen, methodGen);
        methodGen.getInstructionList().append(new INVOKESTATIC(classGen.getConstantPool().addMethodref("java.lang.Math", "floor", "(D)D")));
    }
}
