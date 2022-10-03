package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

class TopLevelElement extends SyntaxTreeNode
{
    protected Vector _dependencies;
    
    TopLevelElement() {
        this._dependencies = null;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return this.typeCheckContents(stable);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ErrorMsg msg = new ErrorMsg("NOT_IMPLEMENTED_ERR", this.getClass(), this);
        this.getParser().reportError(2, msg);
    }
    
    public InstructionList compile(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList save = methodGen.getInstructionList();
        final InstructionList result;
        methodGen.setInstructionList(result = new InstructionList());
        this.translate(classGen, methodGen);
        methodGen.setInstructionList(save);
        return result;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("TopLevelElement");
        this.displayContents(indent + 4);
    }
    
    public void addDependency(final TopLevelElement other) {
        if (this._dependencies == null) {
            this._dependencies = new Vector();
        }
        if (!this._dependencies.contains(other)) {
            this._dependencies.addElement(other);
        }
    }
    
    public Vector getDependencies() {
        return this._dependencies;
    }
}
