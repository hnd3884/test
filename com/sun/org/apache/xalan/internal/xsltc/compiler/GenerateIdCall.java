package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import java.util.Vector;

final class GenerateIdCall extends FunctionCall
{
    public GenerateIdCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        if (this.argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
        }
        else {
            this.argument().translate(classGen, methodGen);
        }
        final ConstantPoolGen cpg = classGen.getConstantPool();
        il.append(new INVOKESTATIC(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "generate_idF", "(I)Ljava/lang/String;")));
    }
}
