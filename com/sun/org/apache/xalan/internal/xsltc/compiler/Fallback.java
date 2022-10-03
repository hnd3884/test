package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class Fallback extends Instruction
{
    private boolean _active;
    
    Fallback() {
        this._active = false;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._active) {
            return this.typeCheckContents(stable);
        }
        return Type.Void;
    }
    
    public void activate() {
        this._active = true;
    }
    
    @Override
    public String toString() {
        return "fallback";
    }
    
    @Override
    public void parseContents(final Parser parser) {
        if (this._active) {
            this.parseChildren(parser);
        }
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._active) {
            this.translateContents(classGen, methodGen);
        }
    }
}
