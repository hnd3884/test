package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class When extends Instruction
{
    private Expression _test;
    private boolean _ignore;
    
    When() {
        this._ignore = false;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("When");
        this.indent(indent + 4);
        System.out.print("test ");
        Util.println(this._test.toString());
        this.displayContents(indent + 4);
    }
    
    public Expression getTest() {
        return this._test;
    }
    
    public boolean ignore() {
        return this._ignore;
    }
    
    @Override
    public void parseContents(final Parser parser) {
        this._test = parser.parseExpression(this, "test", null);
        final Object result = this._test.evaluateAtCompileTime();
        if (result != null && result instanceof Boolean) {
            this._ignore = !(boolean)result;
        }
        this.parseChildren(parser);
        if (this._test.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "test");
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (!(this._test.typeCheck(stable) instanceof BooleanType)) {
            this._test = new CastExpr(this._test, Type.Boolean);
        }
        if (!this._ignore) {
            this.typeCheckContents(stable);
        }
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ErrorMsg msg = new ErrorMsg("STRAY_WHEN_ERR", this);
        this.getParser().reportError(3, msg);
    }
}
