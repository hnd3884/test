package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Iterator;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class ApplyTemplates extends Instruction
{
    private Expression _select;
    private Type _type;
    private QName _modeName;
    private String _functionName;
    
    ApplyTemplates() {
        this._type = null;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("ApplyTemplates");
        this.indent(indent + 4);
        Util.println("select " + this._select.toString());
        if (this._modeName != null) {
            this.indent(indent + 4);
            Util.println("mode " + this._modeName);
        }
    }
    
    public boolean hasWithParams() {
        return this.hasContents();
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final String select = this.getAttribute("select");
        final String mode = this.getAttribute("mode");
        if (select.length() > 0) {
            this._select = parser.parseExpression(this, "select", null);
        }
        if (mode.length() > 0) {
            if (!XML11Char.isXML11ValidQName(mode)) {
                final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", mode, this);
                parser.reportError(3, err);
            }
            this._modeName = parser.getQNameIgnoreDefaultNs(mode);
        }
        this._functionName = parser.getTopLevelStylesheet().getMode(this._modeName).functionName();
        this.parseChildren(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._select == null) {
            this.typeCheckContents(stable);
            return Type.Void;
        }
        this._type = this._select.typeCheck(stable);
        if (this._type instanceof NodeType || this._type instanceof ReferenceType) {
            this._select = new CastExpr(this._select, Type.NodeSet);
            this._type = Type.NodeSet;
        }
        if (this._type instanceof NodeSetType || this._type instanceof ResultTreeType) {
            this.typeCheckContents(stable);
            return Type.Void;
        }
        throw new TypeCheckError(this);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        boolean setStartNodeCalled = false;
        final Stylesheet stylesheet = classGen.getStylesheet();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int current = methodGen.getLocalIndex("current");
        final Vector<Sort> sortObjects = new Vector<Sort>();
        for (final SyntaxTreeNode child : this.getContents()) {
            if (child instanceof Sort) {
                sortObjects.addElement((Sort)child);
            }
        }
        if (stylesheet.hasLocalParams() || this.hasContents()) {
            il.append(classGen.loadTranslet());
            final int pushFrame = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
            il.append(new INVOKEVIRTUAL(pushFrame));
            this.translateContents(classGen, methodGen);
        }
        il.append(classGen.loadTranslet());
        if (this._type != null && this._type instanceof ResultTreeType) {
            if (sortObjects.size() > 0) {
                final ErrorMsg err = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
                this.getParser().reportError(4, err);
            }
            this._select.translate(classGen, methodGen);
            this._type.translateTo(classGen, methodGen, Type.NodeSet);
        }
        else {
            il.append(methodGen.loadDOM());
            if (sortObjects.size() > 0) {
                Sort.translateSortIterator(classGen, methodGen, this._select, sortObjects);
                final int setStartNode = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "setStartNode", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                il.append(methodGen.loadCurrentNode());
                il.append(new INVOKEINTERFACE(setStartNode, 2));
                setStartNodeCalled = true;
            }
            else if (this._select == null) {
                Mode.compileGetChildren(classGen, methodGen, current);
            }
            else {
                this._select.translate(classGen, methodGen);
            }
        }
        if (this._select != null && !setStartNodeCalled) {
            this._select.startIterator(classGen, methodGen);
        }
        final String className = classGen.getStylesheet().getClassName();
        il.append(methodGen.loadHandler());
        final String applyTemplatesSig = classGen.getApplyTemplatesSig();
        final int applyTemplates = cpg.addMethodref(className, this._functionName, applyTemplatesSig);
        il.append(new INVOKEVIRTUAL(applyTemplates));
        for (final SyntaxTreeNode child2 : this.getContents()) {
            if (child2 instanceof WithParam) {
                ((WithParam)child2).releaseResultTree(classGen, methodGen);
            }
        }
        if (stylesheet.hasLocalParams() || this.hasContents()) {
            il.append(classGen.loadTranslet());
            final int popFrame = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
            il.append(new INVOKEVIRTUAL(popFrame));
        }
    }
}
