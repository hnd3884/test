package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.Vector;

public final class MethodType extends Type
{
    private final Type _resultType;
    private final Vector _argsType;
    
    public MethodType(final Type resultType) {
        this._argsType = null;
        this._resultType = resultType;
    }
    
    public MethodType(final Type resultType, final Type arg1) {
        if (arg1 != Type.Void) {
            (this._argsType = new Vector()).addElement(arg1);
        }
        else {
            this._argsType = null;
        }
        this._resultType = resultType;
    }
    
    public MethodType(final Type resultType, final Type arg1, final Type arg2) {
        (this._argsType = new Vector(2)).addElement(arg1);
        this._argsType.addElement(arg2);
        this._resultType = resultType;
    }
    
    public MethodType(final Type resultType, final Type arg1, final Type arg2, final Type arg3) {
        (this._argsType = new Vector(3)).addElement(arg1);
        this._argsType.addElement(arg2);
        this._argsType.addElement(arg3);
        this._resultType = resultType;
    }
    
    public MethodType(final Type resultType, final Vector argsType) {
        this._resultType = resultType;
        this._argsType = ((argsType.size() > 0) ? argsType : null);
    }
    
    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer("method{");
        if (this._argsType != null) {
            for (int count = this._argsType.size(), i = 0; i < count; ++i) {
                result.append(this._argsType.elementAt(i));
                if (i != count - 1) {
                    result.append(',');
                }
            }
        }
        else {
            result.append("void");
        }
        result.append('}');
        return result.toString();
    }
    
    @Override
    public String toSignature() {
        return this.toSignature("");
    }
    
    public String toSignature(final String lastArgSig) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append('(');
        if (this._argsType != null) {
            for (int n = this._argsType.size(), i = 0; i < n; ++i) {
                buffer.append(this._argsType.elementAt(i).toSignature());
            }
        }
        return buffer.append(lastArgSig).append(')').append(this._resultType.toSignature()).toString();
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return null;
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        boolean result = false;
        if (other instanceof MethodType) {
            final MethodType temp = (MethodType)other;
            if (this._resultType.identicalTo(temp._resultType)) {
                final int len = this.argsCount();
                result = (len == temp.argsCount());
                Type arg1;
                Type arg2;
                for (int i = 0; i < len && result; result = arg1.identicalTo(arg2), ++i) {
                    arg1 = this._argsType.elementAt(i);
                    arg2 = temp._argsType.elementAt(i);
                }
            }
        }
        return result;
    }
    
    @Override
    public int distanceTo(final Type other) {
        int result = Integer.MAX_VALUE;
        if (other instanceof MethodType) {
            final MethodType mtype = (MethodType)other;
            if (this._argsType != null) {
                final int len = this._argsType.size();
                if (len == mtype._argsType.size()) {
                    result = 0;
                    for (int i = 0; i < len; ++i) {
                        final Type arg1 = this._argsType.elementAt(i);
                        final Type arg2 = mtype._argsType.elementAt(i);
                        final int temp = arg1.distanceTo(arg2);
                        if (temp == Integer.MAX_VALUE) {
                            result = temp;
                            break;
                        }
                        result += arg1.distanceTo(arg2);
                    }
                }
            }
            else if (mtype._argsType == null) {
                result = 0;
            }
        }
        return result;
    }
    
    public Type resultType() {
        return this._resultType;
    }
    
    public Vector argsType() {
        return this._argsType;
    }
    
    public int argsCount() {
        return (this._argsType == null) ? 0 : this._argsType.size();
    }
}
