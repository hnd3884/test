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

final class LastCall extends FunctionCall
{
    public LastCall(final QName fname) {
        super(fname);
    }
    
    @Override
    public boolean hasPositionCall() {
        return true;
    }
    
    @Override
    public boolean hasLastCall() {
        return true;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        if (methodGen instanceof CompareGenerator) {
            il.append(((CompareGenerator)methodGen).loadLastNode());
        }
        else if (methodGen instanceof TestGenerator) {
            il.append(new ILOAD(3));
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final int getLast = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "getLast", "()I");
            il.append(methodGen.loadIterator());
            il.append(new INVOKEINTERFACE(getLast, 1));
        }
    }
}
