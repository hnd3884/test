package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Objects;

class VariableRefBase extends Expression
{
    protected VariableBase _variable;
    protected Closure _closure;
    
    public VariableRefBase(final VariableBase variable) {
        this._closure = null;
        (this._variable = variable).addReference(this);
    }
    
    public VariableRefBase() {
        this._closure = null;
        this._variable = null;
    }
    
    public VariableBase getVariable() {
        return this._variable;
    }
    
    public void addParentDependency() {
        SyntaxTreeNode node;
        for (node = this; node != null && !(node instanceof TopLevelElement); node = node.getParent()) {}
        final TopLevelElement parent = (TopLevelElement)node;
        if (parent != null) {
            VariableBase var = this._variable;
            if (this._variable._ignore) {
                if (this._variable instanceof Variable) {
                    var = parent.getSymbolTable().lookupVariable(this._variable._name);
                }
                else if (this._variable instanceof Param) {
                    var = parent.getSymbolTable().lookupParam(this._variable._name);
                }
            }
            parent.addDependency(var);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof VariableRefBase && this._variable == ((VariableRefBase)obj)._variable);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this._variable);
    }
    
    @Override
    public String toString() {
        return "variable-ref(" + this._variable.getName() + '/' + this._variable.getType() + ')';
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._type != null) {
            return this._type;
        }
        Label_0081: {
            if (this._variable.isLocal()) {
                SyntaxTreeNode node = this.getParent();
                while (true) {
                    while (!(node instanceof Closure)) {
                        if (!(node instanceof TopLevelElement)) {
                            node = node.getParent();
                            if (node != null) {
                                continue;
                            }
                        }
                        if (this._closure != null) {
                            this._closure.addVariable(this);
                        }
                        break Label_0081;
                    }
                    this._closure = (Closure)node;
                    continue;
                }
            }
        }
        this._type = this._variable.getType();
        if (this._type == null) {
            this._variable.typeCheck(stable);
            this._type = this._variable.getType();
        }
        this.addParentDependency();
        return this._type;
    }
}
