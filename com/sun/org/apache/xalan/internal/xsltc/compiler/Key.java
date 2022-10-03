package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class Key extends TopLevelElement
{
    private QName _name;
    private Pattern _match;
    private Expression _use;
    private Type _useType;
    
    @Override
    public void parseContents(final Parser parser) {
        final String name = this.getAttribute("name");
        if (!XML11Char.isXML11ValidQName(name)) {
            final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", name, this);
            parser.reportError(3, err);
        }
        this._name = parser.getQNameIgnoreDefaultNs(name);
        this.getSymbolTable().addKey(this._name, this);
        this._match = parser.parsePattern(this, "match", null);
        this._use = parser.parseExpression(this, "use", null);
        if (this._name == null) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "name");
            return;
        }
        if (this._match.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "match");
            return;
        }
        if (this._use.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "use");
        }
    }
    
    public String getName() {
        return this._name.toString();
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this._match.typeCheck(stable);
        this._useType = this._use.typeCheck(stable);
        if (!(this._useType instanceof StringType) && !(this._useType instanceof NodeSetType)) {
            this._use = new CastExpr(this._use, Type.String);
        }
        return Type.Void;
    }
    
    public void traverseNodeSet(final ClassGenerator classGen, final MethodGenerator methodGen, final int buildKeyIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int getNodeValue = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
        final int getNodeIdent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
        final int keyDom = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
        final LocalVariableGen parentNode = methodGen.addLocalVariable("parentNode", Util.getJCRefType("I"), null, null);
        parentNode.setStart(il.append(new ISTORE(parentNode.getIndex())));
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());
        this._use.translate(classGen, methodGen);
        this._use.startIterator(classGen, methodGen);
        il.append(methodGen.storeIterator());
        final BranchHandle nextNode = il.append(new GOTO(null));
        final InstructionHandle loop = il.append(Key.NOP);
        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, this._name.toString()));
        parentNode.setEnd(il.append(new ILOAD(parentNode.getIndex())));
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEINTERFACE(getNodeValue, 2));
        il.append(new INVOKEVIRTUAL(buildKeyIndex));
        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, this.getName()));
        il.append(methodGen.loadDOM());
        il.append(new INVOKEVIRTUAL(keyDom));
        nextNode.setTarget(il.append(methodGen.loadIterator()));
        il.append(methodGen.nextNode());
        il.append(Key.DUP);
        il.append(methodGen.storeCurrentNode());
        il.append(new IFGE(loop));
        il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int current = methodGen.getLocalIndex("current");
        final int key = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "buildKeyIndex", "(Ljava/lang/String;ILjava/lang/String;)V");
        final int keyDom = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
        final int getNodeIdent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
        final int git = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());
        il.append(methodGen.loadDOM());
        il.append(new PUSH(cpg, 4));
        il.append(new INVOKEINTERFACE(git, 2));
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.setStartNode());
        il.append(methodGen.storeIterator());
        final BranchHandle nextNode = il.append(new GOTO(null));
        final InstructionHandle loop = il.append(Key.NOP);
        il.append(methodGen.loadCurrentNode());
        this._match.translate(classGen, methodGen);
        this._match.synthesize(classGen, methodGen);
        final BranchHandle skipNode = il.append(new IFEQ(null));
        if (this._useType instanceof NodeSetType) {
            il.append(methodGen.loadCurrentNode());
            this.traverseNodeSet(classGen, methodGen, key);
        }
        else {
            il.append(classGen.loadTranslet());
            il.append(Key.DUP);
            il.append(new PUSH(cpg, this._name.toString()));
            il.append(Key.DUP_X1);
            il.append(methodGen.loadCurrentNode());
            this._use.translate(classGen, methodGen);
            il.append(new INVOKEVIRTUAL(key));
            il.append(methodGen.loadDOM());
            il.append(new INVOKEVIRTUAL(keyDom));
        }
        final InstructionHandle skip = il.append(Key.NOP);
        il.append(methodGen.loadIterator());
        il.append(methodGen.nextNode());
        il.append(Key.DUP);
        il.append(methodGen.storeCurrentNode());
        il.append(new IFGT(loop));
        il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
        nextNode.setTarget(skip);
        skipNode.setTarget(skip);
    }
}
