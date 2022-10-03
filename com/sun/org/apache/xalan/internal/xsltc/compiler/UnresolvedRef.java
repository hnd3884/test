package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;

final class UnresolvedRef extends VariableRefBase
{
    private QName _variableName;
    private VariableRefBase _ref;
    
    public UnresolvedRef(final QName name) {
        this._variableName = null;
        this._ref = null;
        this._variableName = name;
    }
    
    public QName getName() {
        return this._variableName;
    }
    
    private ErrorMsg reportError() {
        final ErrorMsg err = new ErrorMsg("VARIABLE_UNDEF_ERR", this._variableName, this);
        this.getParser().reportError(3, err);
        return err;
    }
    
    private VariableRefBase resolve(final Parser parser, final SymbolTable stable) {
        VariableBase ref = parser.lookupVariable(this._variableName);
        if (ref == null) {
            ref = (VariableBase)stable.lookupName(this._variableName);
        }
        if (ref == null) {
            this.reportError();
            return null;
        }
        this._variable = ref;
        this.addParentDependency();
        if (ref instanceof Variable) {
            return new VariableRef((Variable)ref);
        }
        if (ref instanceof Param) {
            return new ParameterRef((Param)ref);
        }
        return null;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._ref != null) {
            final String name = this._variableName.toString();
            final ErrorMsg errorMsg = new ErrorMsg("CIRCULAR_VARIABLE_ERR", name, this);
        }
        if ((this._ref = this.resolve(this.getParser(), stable)) != null) {
            return this._type = this._ref.typeCheck(stable);
        }
        throw new TypeCheckError(this.reportError());
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        if (this._ref != null) {
            this._ref.translate(classGen, methodGen);
        }
        else {
            this.reportError();
        }
    }
    
    @Override
    public String toString() {
        return "unresolved-ref()";
    }
}
