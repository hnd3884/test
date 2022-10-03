package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ValueOf extends Instruction
{
    private Expression _select;
    private boolean _escaping;
    private boolean _isString;
    
    ValueOf() {
        this._escaping = true;
        this._isString = false;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("ValueOf");
        this.indent(indent + 4);
        Util.println("select " + this._select.toString());
    }
    
    @Override
    public void parseContents(final Parser parser) {
        this._select = parser.parseExpression(this, "select", null);
        if (this._select.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
            return;
        }
        final String str = this.getAttribute("disable-output-escaping");
        if (str != null && str.equals("yes")) {
            this._escaping = false;
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type type = this._select.typeCheck(stable);
        if (type != null && !type.identicalTo(Type.Node)) {
            if (type.identicalTo(Type.NodeSet)) {
                this._select = new CastExpr(this._select, Type.Node);
            }
            else {
                this._isString = true;
                if (!type.identicalTo(Type.String)) {
                    this._select = new CastExpr(this._select, Type.String);
                }
                this._isString = true;
            }
        }
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int setEscaping = cpg.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "setEscaping", "(Z)Z");
        if (!this._escaping) {
            il.append(methodGen.loadHandler());
            il.append(new PUSH(cpg, false));
            il.append(new INVOKEINTERFACE(setEscaping, 2));
        }
        if (this._isString) {
            final int characters = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
            il.append(classGen.loadTranslet());
            this._select.translate(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEVIRTUAL(characters));
        }
        else {
            final int characters = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "characters", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
            il.append(methodGen.loadDOM());
            this._select.translate(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEINTERFACE(characters, 3));
        }
        if (!this._escaping) {
            il.append(methodGen.loadHandler());
            il.append(ValueOf.SWAP);
            il.append(new INVOKEINTERFACE(setEscaping, 2));
            il.append(ValueOf.POP);
        }
    }
}
