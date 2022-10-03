package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class LiteralExpr extends Expression
{
    private final String _value;
    private final String _namespace;
    
    public LiteralExpr(final String value) {
        this._value = value;
        this._namespace = null;
    }
    
    public LiteralExpr(final String value, final String namespace) {
        this._value = value;
        this._namespace = (namespace.equals("") ? null : namespace);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return this._type = Type.String;
    }
    
    @Override
    public String toString() {
        return "literal-expr(" + this._value + ')';
    }
    
    @Override
    protected boolean contextDependent() {
        return false;
    }
    
    protected String getValue() {
        return this._value;
    }
    
    protected String getNamespace() {
        return this._namespace;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new PUSH(cpg, this._value));
    }
}
