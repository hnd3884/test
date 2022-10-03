package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class Comment extends Instruction
{
    @Override
    public void parseContents(final Parser parser) {
        this.parseChildren(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.String;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        Text rawText = null;
        if (this.elementCount() == 1) {
            final Object content = this.elementAt(0);
            if (content instanceof Text) {
                rawText = (Text)content;
            }
        }
        if (rawText != null) {
            il.append(methodGen.loadHandler());
            if (rawText.canLoadAsArrayOffsetLength()) {
                rawText.loadAsArrayOffsetLength(classGen, methodGen);
                final int comment = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "([CII)V");
                il.append(new INVOKEINTERFACE(comment, 4));
            }
            else {
                il.append(new PUSH(cpg, rawText.getText()));
                final int comment = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
                il.append(new INVOKEINTERFACE(comment, 2));
            }
        }
        else {
            il.append(methodGen.loadHandler());
            il.append(Comment.DUP);
            il.append(classGen.loadTranslet());
            il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
            il.append(Comment.DUP);
            il.append(methodGen.storeHandler());
            this.translateContents(classGen, methodGen);
            il.append(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;")));
            final int comment = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
            il.append(new INVOKEINTERFACE(comment, 2));
            il.append(methodGen.storeHandler());
        }
    }
}
