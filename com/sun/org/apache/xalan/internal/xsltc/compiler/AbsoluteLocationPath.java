package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class AbsoluteLocationPath extends Expression
{
    private Expression _path;
    
    public AbsoluteLocationPath() {
        this._path = null;
    }
    
    public AbsoluteLocationPath(final Expression path) {
        this._path = path;
        if (path != null) {
            this._path.setParent(this);
        }
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        if (this._path != null) {
            this._path.setParser(parser);
        }
    }
    
    public Expression getPath() {
        return this._path;
    }
    
    @Override
    public String toString() {
        return "AbsoluteLocationPath(" + ((this._path != null) ? this._path.toString() : "null") + ')';
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._path != null) {
            final Type ptype = this._path.typeCheck(stable);
            if (ptype instanceof NodeType) {
                this._path = new CastExpr(this._path, Type.NodeSet);
            }
        }
        return this._type = Type.NodeSet;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._path != null) {
            final int initAI = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
            this._path.translate(classGen, methodGen);
            final LocalVariableGen relPathIterator = methodGen.addLocalVariable("abs_location_path_tmp", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
            relPathIterator.setStart(il.append(new ASTORE(relPathIterator.getIndex())));
            il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator")));
            il.append(AbsoluteLocationPath.DUP);
            relPathIterator.setEnd(il.append(new ALOAD(relPathIterator.getIndex())));
            il.append(new INVOKESPECIAL(initAI));
        }
        else {
            final int gitr = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append(new INVOKEINTERFACE(gitr, 1));
        }
    }
}
