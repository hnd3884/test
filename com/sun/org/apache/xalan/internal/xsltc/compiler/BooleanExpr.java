package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class BooleanExpr extends Expression
{
    private boolean _value;
    
    public BooleanExpr(final boolean value) {
        this._value = value;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return this._type = Type.Boolean;
    }
    
    @Override
    public String toString() {
        return this._value ? "true()" : "false()";
    }
    
    public boolean getValue() {
        return this._value;
    }
    
    public boolean contextDependent() {
        return false;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new PUSH(cpg, this._value));
    }
    
    @Override
    public void translateDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        if (this._value) {
            il.append(BooleanExpr.NOP);
        }
        else {
            this._falseList.add(il.append(new GOTO(null)));
        }
    }
}
