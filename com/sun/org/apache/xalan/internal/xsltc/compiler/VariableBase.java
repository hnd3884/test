package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

class VariableBase extends TopLevelElement
{
    protected QName _name;
    protected String _escapedName;
    protected Type _type;
    protected boolean _isLocal;
    protected LocalVariableGen _local;
    protected Instruction _loadInstruction;
    protected Instruction _storeInstruction;
    protected Expression _select;
    protected String select;
    protected Vector<VariableRefBase> _refs;
    protected boolean _ignore;
    
    VariableBase() {
        this._refs = new Vector<VariableRefBase>(2);
        this._ignore = false;
    }
    
    public void disable() {
        this._ignore = true;
    }
    
    public void addReference(final VariableRefBase vref) {
        this._refs.addElement(vref);
    }
    
    public void copyReferences(final VariableBase var) {
        for (int size = this._refs.size(), i = 0; i < size; ++i) {
            var.addReference(this._refs.get(i));
        }
    }
    
    public void mapRegister(final MethodGenerator methodGen) {
        if (this._local == null) {
            final InstructionList il = methodGen.getInstructionList();
            final String name = this.getEscapedName();
            final com.sun.org.apache.bcel.internal.generic.Type varType = this._type.toJCType();
            this._local = methodGen.addLocalVariable2(name, varType, il.getEnd());
        }
    }
    
    public void unmapRegister(final ClassGenerator classGen, final MethodGenerator methodGen) {
        if (this._local != null) {
            if (this._type instanceof ResultTreeType) {
                final ConstantPoolGen cpg = classGen.getConstantPool();
                final InstructionList il = methodGen.getInstructionList();
                if (classGen.getStylesheet().callsNodeset() && classGen.getDOMClass().equals("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")) {
                    final int removeDA = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "removeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)V");
                    il.append(methodGen.loadDOM());
                    il.append(new CHECKCAST(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")));
                    il.append(this.loadInstruction());
                    il.append(new CHECKCAST(cpg.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
                    il.append(new INVOKEVIRTUAL(removeDA));
                }
                final int release = cpg.addInterfaceMethodref("com/sun/org/apache/xalan/internal/xsltc/DOM", "release", "()V");
                il.append(this.loadInstruction());
                il.append(new INVOKEINTERFACE(release, 1));
            }
            this._local.setEnd(methodGen.getInstructionList().getEnd());
            methodGen.removeLocalVariable(this._local);
            this._refs = null;
            this._local = null;
        }
    }
    
    public Instruction loadInstruction() {
        if (this._loadInstruction == null) {
            this._loadInstruction = this._type.LOAD(this._local.getIndex());
        }
        return this._loadInstruction;
    }
    
    public Instruction storeInstruction() {
        if (this._storeInstruction == null) {
            this._storeInstruction = this._type.STORE(this._local.getIndex());
        }
        return this._storeInstruction;
    }
    
    public Expression getExpression() {
        return this._select;
    }
    
    @Override
    public String toString() {
        return "variable(" + this._name + ")";
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        System.out.println("Variable " + this._name);
        if (this._select != null) {
            this.indent(indent + 4);
            System.out.println("select " + this._select.toString());
        }
        this.displayContents(indent + 4);
    }
    
    public Type getType() {
        return this._type;
    }
    
    public QName getName() {
        return this._name;
    }
    
    public String getEscapedName() {
        return this._escapedName;
    }
    
    public void setName(final QName name) {
        this._name = name;
        this._escapedName = Util.escape(name.getStringRep());
    }
    
    public boolean isLocal() {
        return this._isLocal;
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final String name = this.getAttribute("name");
        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", name, this);
                parser.reportError(3, err);
            }
            this.setName(parser.getQNameIgnoreDefaultNs(name));
        }
        else {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "name");
        }
        final VariableBase other = parser.lookupVariable(this._name);
        if (other != null && other.getParent() == this.getParent()) {
            this.reportError(this, parser, "VARIABLE_REDEF_ERR", name);
        }
        this.select = this.getAttribute("select");
        if (this.select.length() > 0) {
            this._select = this.getParser().parseExpression(this, "select", null);
            if (this._select.isDummy()) {
                this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
                return;
            }
        }
        this.parseChildren(parser);
    }
    
    public void translateValue(final ClassGenerator classGen, final MethodGenerator methodGen) {
        if (this._select != null) {
            this._select.translate(classGen, methodGen);
            if (this._select.getType() instanceof NodeSetType) {
                final ConstantPoolGen cpg = classGen.getConstantPool();
                final InstructionList il = methodGen.getInstructionList();
                final int initCNI = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
                il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator")));
                il.append(VariableBase.DUP_X1);
                il.append(VariableBase.SWAP);
                il.append(new INVOKESPECIAL(initCNI));
            }
            this._select.startIterator(classGen, methodGen);
        }
        else if (this.hasContents()) {
            this.compileResultTree(classGen, methodGen);
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            il.append(new PUSH(cpg, ""));
        }
    }
}
