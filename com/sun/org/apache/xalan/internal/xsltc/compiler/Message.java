package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class Message extends Instruction
{
    private boolean _terminate;
    
    Message() {
        this._terminate = false;
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final String termstr = this.getAttribute("terminate");
        if (termstr != null) {
            this._terminate = termstr.equals("yes");
        }
        this.parseChildren(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(classGen.loadTranslet());
        Label_0495: {
            switch (this.elementCount()) {
                case 0: {
                    il.append(new PUSH(cpg, ""));
                    break Label_0495;
                }
                case 1: {
                    final SyntaxTreeNode child = this.elementAt(0);
                    if (child instanceof Text) {
                        il.append(new PUSH(cpg, ((Text)child).getText()));
                        break Label_0495;
                    }
                    break;
                }
            }
            il.append(methodGen.loadHandler());
            il.append(new NEW(cpg.addClass("com.sun.org.apache.xml.internal.serializer.ToXMLStream")));
            il.append(methodGen.storeHandler());
            il.append(new NEW(cpg.addClass("java.io.StringWriter")));
            il.append(Message.DUP);
            il.append(Message.DUP);
            il.append(new INVOKESPECIAL(cpg.addMethodref("java.io.StringWriter", "<init>", "()V")));
            il.append(methodGen.loadHandler());
            il.append(new INVOKESPECIAL(cpg.addMethodref("com.sun.org.apache.xml.internal.serializer.ToXMLStream", "<init>", "()V")));
            il.append(methodGen.loadHandler());
            il.append(Message.SWAP);
            il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setWriter", "(Ljava/io/Writer;)V"), 2));
            il.append(methodGen.loadHandler());
            il.append(new PUSH(cpg, "UTF-8"));
            il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setEncoding", "(Ljava/lang/String;)V"), 2));
            il.append(methodGen.loadHandler());
            il.append(Message.ICONST_1);
            il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setOmitXMLDeclaration", "(Z)V"), 2));
            il.append(methodGen.loadHandler());
            il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V"), 1));
            this.translateContents(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V"), 1));
            il.append(new INVOKEVIRTUAL(cpg.addMethodref("java.io.StringWriter", "toString", "()Ljava/lang/String;")));
            il.append(Message.SWAP);
            il.append(methodGen.storeHandler());
        }
        il.append(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "displayMessage", "(Ljava/lang/String;)V")));
        if (this._terminate) {
            final int einit = cpg.addMethodref("java.lang.RuntimeException", "<init>", "(Ljava/lang/String;)V");
            il.append(new NEW(cpg.addClass("java.lang.RuntimeException")));
            il.append(Message.DUP);
            il.append(new PUSH(cpg, "Termination forced by an xsl:message instruction"));
            il.append(new INVOKESPECIAL(einit));
            il.append(Message.ATHROW);
        }
    }
}
