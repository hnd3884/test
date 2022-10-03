package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;

final class PositionCall extends FunctionCall
{
    public PositionCall(final QName fname) {
        super(fname);
    }
    
    @Override
    public boolean hasPositionCall() {
        return true;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        if (methodGen instanceof CompareGenerator) {
            il.append(((CompareGenerator)methodGen).loadCurrentNode());
        }
        else if (methodGen instanceof TestGenerator) {
            il.append(new ILOAD(2));
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "getPosition", "()I");
            il.append(methodGen.loadIterator());
            il.append(new INVOKEINTERFACE(index, 1));
        }
    }
}
