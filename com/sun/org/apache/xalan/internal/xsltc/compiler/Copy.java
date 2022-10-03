package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Copy extends Instruction
{
    private UseAttributeSets _useSets;
    
    @Override
    public void parseContents(final Parser parser) {
        final String useSets = this.getAttribute("use-attribute-sets");
        if (useSets.length() > 0) {
            if (!Util.isValidQNames(useSets)) {
                final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", useSets, this);
                parser.reportError(3, err);
            }
            this._useSets = new UseAttributeSets(useSets, parser);
        }
        this.parseChildren(parser);
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("Copy");
        this.indent(indent + 4);
        this.displayContents(indent + 4);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._useSets != null) {
            this._useSets.typeCheck(stable);
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final LocalVariableGen name = methodGen.addLocalVariable2("name", Util.getJCRefType("Ljava/lang/String;"), null);
        final LocalVariableGen length = methodGen.addLocalVariable2("length", Util.getJCRefType("I"), null);
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadHandler());
        final int cpy = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "shallowCopy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)Ljava/lang/String;");
        il.append(new INVOKEINTERFACE(cpy, 3));
        il.append(Copy.DUP);
        name.setStart(il.append(new ASTORE(name.getIndex())));
        final BranchHandle ifBlock1 = il.append(new IFNULL(null));
        il.append(new ALOAD(name.getIndex()));
        final int lengthMethod = cpg.addMethodref("java.lang.String", "length", "()I");
        il.append(new INVOKEVIRTUAL(lengthMethod));
        il.append(Copy.DUP);
        length.setStart(il.append(new ISTORE(length.getIndex())));
        final BranchHandle ifBlock2 = il.append(new IFEQ(null));
        if (this._useSets != null) {
            final SyntaxTreeNode parent = this.getParent();
            if (parent instanceof LiteralElement || parent instanceof LiteralElement) {
                this._useSets.translate(classGen, methodGen);
            }
            else {
                il.append(new ILOAD(length.getIndex()));
                final BranchHandle ifBlock3 = il.append(new IFEQ(null));
                this._useSets.translate(classGen, methodGen);
                ifBlock3.setTarget(il.append(Copy.NOP));
            }
        }
        ifBlock2.setTarget(il.append(Copy.NOP));
        this.translateContents(classGen, methodGen);
        length.setEnd(il.append(new ILOAD(length.getIndex())));
        final BranchHandle ifBlock4 = il.append(new IFEQ(null));
        il.append(methodGen.loadHandler());
        name.setEnd(il.append(new ALOAD(name.getIndex())));
        il.append(methodGen.endElement());
        final InstructionHandle end = il.append(Copy.NOP);
        ifBlock1.setTarget(end);
        ifBlock4.setTarget(end);
        methodGen.removeLocalVariable(name);
        methodGen.removeLocalVariable(length);
    }
}
