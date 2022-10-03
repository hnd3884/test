package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class ElementAvailableCall extends FunctionCall
{
    public ElementAvailableCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this.argument() instanceof LiteralExpr) {
            return this._type = Type.Boolean;
        }
        final ErrorMsg err = new ErrorMsg("NEED_LITERAL_ERR", "element-available", this);
        throw new TypeCheckError(err);
    }
    
    @Override
    public Object evaluateAtCompileTime() {
        return this.getResult() ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public boolean getResult() {
        try {
            final LiteralExpr arg = (LiteralExpr)this.argument();
            final String qname = arg.getValue();
            final int index = qname.indexOf(58);
            final String localName = (index > 0) ? qname.substring(index + 1) : qname;
            return this.getParser().elementSupported(arg.getNamespace(), localName);
        }
        catch (final ClassCastException e) {
            return false;
        }
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final boolean result = this.getResult();
        methodGen.getInstructionList().append(new PUSH(cpg, result));
    }
}
