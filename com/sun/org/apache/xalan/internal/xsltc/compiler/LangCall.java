package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class LangCall extends FunctionCall
{
    private Expression _lang;
    private Type _langType;
    
    public LangCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._lang = this.argument(0);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this._langType = this._lang.typeCheck(stable);
        if (!(this._langType instanceof StringType)) {
            this._lang = new CastExpr(this._lang, Type.String);
        }
        return Type.Boolean;
    }
    
    @Override
    public Type getType() {
        return Type.Boolean;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int tst = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "testLanguage", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)Z");
        this._lang.translate(classGen, methodGen);
        il.append(methodGen.loadDOM());
        if (classGen instanceof FilterGenerator) {
            il.append(new ILOAD(1));
        }
        else {
            il.append(methodGen.loadContextNode());
        }
        il.append(new INVOKESTATIC(tst));
    }
}
