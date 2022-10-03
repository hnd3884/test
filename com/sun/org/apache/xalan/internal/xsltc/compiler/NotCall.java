package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import java.util.Vector;

final class NotCall extends FunctionCall
{
    public NotCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        this.argument().translate(classGen, methodGen);
        il.append(NotCall.ICONST_1);
        il.append(NotCall.IXOR);
    }
    
    @Override
    public void translateDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        final Expression exp = this.argument();
        exp.translateDesynthesized(classGen, methodGen);
        final BranchHandle gotoh = il.append(new GOTO(null));
        this._trueList = exp._falseList;
        (this._falseList = exp._trueList).add(gotoh);
    }
}
