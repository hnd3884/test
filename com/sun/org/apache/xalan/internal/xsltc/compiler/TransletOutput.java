package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class TransletOutput extends Instruction
{
    private Expression _filename;
    private boolean _append;
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("TransletOutput: " + this._filename);
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final String filename = this.getAttribute("file");
        final String append = this.getAttribute("append");
        if (filename == null || filename.equals("")) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "file");
        }
        this._filename = AttributeValue.create(this, filename, parser);
        if (append != null && (append.toLowerCase().equals("yes") || append.toLowerCase().equals("true"))) {
            this._append = true;
        }
        else {
            this._append = false;
        }
        this.parseChildren(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type type = this._filename.typeCheck(stable);
        if (!(type instanceof StringType)) {
            this._filename = new CastExpr(this._filename, Type.String);
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final boolean isSecureProcessing = classGen.getParser().getXSLTC().isSecureProcessing();
        if (isSecureProcessing) {
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unallowed_extension_elementF", "(Ljava/lang/String;)V");
            il.append(new PUSH(cpg, "redirect"));
            il.append(new INVOKESTATIC(index));
            return;
        }
        il.append(methodGen.loadHandler());
        final int open = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "openOutputHandler", "(Ljava/lang/String;Z)Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
        final int close = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "closeOutputHandler", "(Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
        il.append(classGen.loadTranslet());
        this._filename.translate(classGen, methodGen);
        il.append(new PUSH(cpg, this._append));
        il.append(new INVOKEVIRTUAL(open));
        il.append(methodGen.storeHandler());
        this.translateContents(classGen, methodGen);
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadHandler());
        il.append(new INVOKEVIRTUAL(close));
        il.append(methodGen.storeHandler());
    }
}
