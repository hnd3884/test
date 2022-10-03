package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class FormatNumberCall extends FunctionCall
{
    private Expression _value;
    private Expression _format;
    private Expression _name;
    private QName _resolvedQName;
    
    public FormatNumberCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._resolvedQName = null;
        this._value = this.argument(0);
        this._format = this.argument(1);
        this._name = ((this.argumentCount() == 3) ? this.argument(2) : null);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this.getStylesheet().numberFormattingUsed();
        final Type tvalue = this._value.typeCheck(stable);
        if (!(tvalue instanceof RealType)) {
            this._value = new CastExpr(this._value, Type.Real);
        }
        final Type tformat = this._format.typeCheck(stable);
        if (!(tformat instanceof StringType)) {
            this._format = new CastExpr(this._format, Type.String);
        }
        if (this.argumentCount() == 3) {
            final Type tname = this._name.typeCheck(stable);
            if (this._name instanceof LiteralExpr) {
                final LiteralExpr literal = (LiteralExpr)this._name;
                this._resolvedQName = this.getParser().getQNameIgnoreDefaultNs(literal.getValue());
            }
            else if (!(tname instanceof StringType)) {
                this._name = new CastExpr(this._name, Type.String);
            }
        }
        return this._type = Type.String;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        this._value.translate(classGen, methodGen);
        this._format.translate(classGen, methodGen);
        final int fn3arg = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "formatNumber", "(DLjava/lang/String;Ljava/text/DecimalFormat;)Ljava/lang/String;");
        final int get = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getDecimalFormat", "(Ljava/lang/String;)Ljava/text/DecimalFormat;");
        il.append(classGen.loadTranslet());
        if (this._name == null) {
            il.append(new PUSH(cpg, ""));
        }
        else if (this._resolvedQName != null) {
            il.append(new PUSH(cpg, this._resolvedQName.toString()));
        }
        else {
            this._name.translate(classGen, methodGen);
        }
        il.append(new INVOKEVIRTUAL(get));
        il.append(new INVOKESTATIC(fn3arg));
    }
}
