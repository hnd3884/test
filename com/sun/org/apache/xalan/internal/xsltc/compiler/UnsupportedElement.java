package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.List;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.Vector;

final class UnsupportedElement extends SyntaxTreeNode
{
    private Vector _fallbacks;
    private ErrorMsg _message;
    private boolean _isExtension;
    
    public UnsupportedElement(final String uri, final String prefix, final String local, final boolean isExtension) {
        super(uri, prefix, local);
        this._fallbacks = null;
        this._message = null;
        this._isExtension = false;
        this._isExtension = isExtension;
    }
    
    public void setErrorMessage(final ErrorMsg message) {
        this._message = message;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("Unsupported element = " + this._qname.getNamespace() + ":" + this._qname.getLocalPart());
        this.displayContents(indent + 4);
    }
    
    private void processFallbacks(final Parser parser) {
        final List<SyntaxTreeNode> children = this.getContents();
        if (children != null) {
            for (int count = children.size(), i = 0; i < count; ++i) {
                final SyntaxTreeNode child = children.get(i);
                if (child instanceof Fallback) {
                    final Fallback fallback = (Fallback)child;
                    fallback.activate();
                    fallback.parseContents(parser);
                    if (this._fallbacks == null) {
                        this._fallbacks = new Vector();
                    }
                    this._fallbacks.addElement(child);
                }
            }
        }
    }
    
    @Override
    public void parseContents(final Parser parser) {
        this.processFallbacks(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._fallbacks != null) {
            for (int count = this._fallbacks.size(), i = 0; i < count; ++i) {
                final Fallback fallback = this._fallbacks.elementAt(i);
                fallback.typeCheck(stable);
            }
        }
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        if (this._fallbacks != null) {
            for (int count = this._fallbacks.size(), i = 0; i < count; ++i) {
                final Fallback fallback = this._fallbacks.elementAt(i);
                fallback.translate(classGen, methodGen);
            }
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            final int unsupportedElem = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unsupported_ElementF", "(Ljava/lang/String;Z)V");
            il.append(new PUSH(cpg, this.getQName().toString()));
            il.append(new PUSH(cpg, this._isExtension));
            il.append(new INVOKESTATIC(unsupportedElem));
        }
    }
}
