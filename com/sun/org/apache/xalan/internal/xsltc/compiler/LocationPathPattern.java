package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

public abstract class LocationPathPattern extends Pattern
{
    private Template _template;
    private int _importPrecedence;
    private double _priority;
    private int _position;
    
    public LocationPathPattern() {
        this._priority = Double.NaN;
        this._position = 0;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
    }
    
    public void setTemplate(final Template template) {
        this._template = template;
        this._priority = template.getPriority();
        this._importPrecedence = template.getImportPrecedence();
        this._position = template.getPosition();
    }
    
    public Template getTemplate() {
        return this._template;
    }
    
    @Override
    public final double getPriority() {
        return Double.isNaN(this._priority) ? this.getDefaultPriority() : this._priority;
    }
    
    public double getDefaultPriority() {
        return 0.5;
    }
    
    public boolean noSmallerThan(final LocationPathPattern other) {
        if (this._importPrecedence > other._importPrecedence) {
            return true;
        }
        if (this._importPrecedence == other._importPrecedence) {
            if (this._priority > other._priority) {
                return true;
            }
            if (this._priority == other._priority && this._position > other._position) {
                return true;
            }
        }
        return false;
    }
    
    public abstract StepPattern getKernelPattern();
    
    public abstract void reduceKernelPattern();
    
    public abstract boolean isWildcard();
    
    public int getAxis() {
        final StepPattern sp = this.getKernelPattern();
        return (sp != null) ? sp.getAxis() : 3;
    }
    
    @Override
    public String toString() {
        return "root()";
    }
}
