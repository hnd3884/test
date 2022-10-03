package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class FunctionAvailableCall extends FunctionCall
{
    private Expression _arg;
    private String _nameOfFunct;
    private String _namespaceOfFunct;
    private boolean _isFunctionAvailable;
    
    public FunctionAvailableCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._nameOfFunct = null;
        this._namespaceOfFunct = null;
        this._isFunctionAvailable = false;
        this._arg = arguments.elementAt(0);
        this._type = null;
        if (this._arg instanceof LiteralExpr) {
            final LiteralExpr arg = (LiteralExpr)this._arg;
            this._namespaceOfFunct = arg.getNamespace();
            this._nameOfFunct = arg.getValue();
            if (!this.isInternalNamespace()) {
                this._isFunctionAvailable = this.hasMethods();
            }
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._type != null) {
            return this._type;
        }
        if (this._arg instanceof LiteralExpr) {
            return this._type = Type.Boolean;
        }
        final ErrorMsg err = new ErrorMsg("NEED_LITERAL_ERR", "function-available", this);
        throw new TypeCheckError(err);
    }
    
    @Override
    public Object evaluateAtCompileTime() {
        return this.getResult() ? Boolean.TRUE : Boolean.FALSE;
    }
    
    private boolean hasMethods() {
        String className = this.getClassNameFromUri(this._namespaceOfFunct);
        String methodName = null;
        final int colonIndex = this._nameOfFunct.indexOf(58);
        if (colonIndex > 0) {
            final String functionName = this._nameOfFunct.substring(colonIndex + 1);
            final int lastDotIndex = functionName.lastIndexOf(46);
            if (lastDotIndex > 0) {
                methodName = functionName.substring(lastDotIndex + 1);
                if (className != null && className.length() != 0) {
                    className = className + "." + functionName.substring(0, lastDotIndex);
                }
                else {
                    className = functionName.substring(0, lastDotIndex);
                }
            }
            else {
                methodName = functionName;
            }
        }
        else {
            methodName = this._nameOfFunct;
        }
        if (className == null || methodName == null) {
            return false;
        }
        if (methodName.indexOf(45) > 0) {
            methodName = FunctionCall.replaceDash(methodName);
        }
        try {
            final Class clazz = ObjectFactory.findProviderClass(className, true);
            if (clazz == null) {
                return false;
            }
            final Method[] methods = clazz.getMethods();
            for (int i = 0; i < methods.length; ++i) {
                final int mods = methods[i].getModifiers();
                if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && methods[i].getName().equals(methodName)) {
                    return true;
                }
            }
        }
        catch (final ClassNotFoundException e) {
            return false;
        }
        return false;
    }
    
    public boolean getResult() {
        if (this._nameOfFunct == null) {
            return false;
        }
        if (this.isInternalNamespace()) {
            final Parser parser = this.getParser();
            this._isFunctionAvailable = parser.functionSupported(Util.getLocalName(this._nameOfFunct));
        }
        return this._isFunctionAvailable;
    }
    
    private boolean isInternalNamespace() {
        return this._namespaceOfFunct == null || this._namespaceOfFunct.equals("") || this._namespaceOfFunct.equals("http://xml.apache.org/xalan/xsltc");
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        methodGen.getInstructionList().append(new PUSH(cpg, this.getResult()));
    }
}
