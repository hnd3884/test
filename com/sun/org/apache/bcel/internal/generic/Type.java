package com.sun.org.apache.bcel.internal.generic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.Serializable;

public abstract class Type implements Serializable
{
    protected byte type;
    protected String signature;
    public static final BasicType VOID;
    public static final BasicType BOOLEAN;
    public static final BasicType INT;
    public static final BasicType SHORT;
    public static final BasicType BYTE;
    public static final BasicType LONG;
    public static final BasicType DOUBLE;
    public static final BasicType FLOAT;
    public static final BasicType CHAR;
    public static final ObjectType OBJECT;
    public static final ObjectType STRING;
    public static final ObjectType STRINGBUFFER;
    public static final ObjectType THROWABLE;
    public static final Type[] NO_ARGS;
    public static final ReferenceType NULL;
    public static final Type UNKNOWN;
    private static int consumed_chars;
    
    protected Type(final byte t, final String s) {
        this.type = t;
        this.signature = s;
    }
    
    public String getSignature() {
        return this.signature;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public int getSize() {
        switch (this.type) {
            case 7:
            case 11: {
                return 2;
            }
            case 12: {
                return 0;
            }
            default: {
                return 1;
            }
        }
    }
    
    @Override
    public String toString() {
        return (this.equals(Type.NULL) || this.type >= 15) ? this.signature : Utility.signatureToString(this.signature, false);
    }
    
    public static String getMethodSignature(final Type return_type, final Type[] arg_types) {
        final StringBuffer buf = new StringBuffer("(");
        for (int length = (arg_types == null) ? 0 : arg_types.length, i = 0; i < length; ++i) {
            buf.append(arg_types[i].getSignature());
        }
        buf.append(')');
        buf.append(return_type.getSignature());
        return buf.toString();
    }
    
    public static final Type getType(final String signature) throws StringIndexOutOfBoundsException {
        final byte type = Utility.typeOfSignature(signature);
        if (type <= 12) {
            Type.consumed_chars = 1;
            return BasicType.getType(type);
        }
        if (type == 13) {
            int dim = 0;
            do {
                ++dim;
            } while (signature.charAt(dim) == '[');
            final Type t = getType(signature.substring(dim));
            Type.consumed_chars += dim;
            return new ArrayType(t, dim);
        }
        final int index = signature.indexOf(59);
        if (index < 0) {
            throw new ClassFormatException("Invalid signature: " + signature);
        }
        Type.consumed_chars = index + 1;
        return new ObjectType(signature.substring(1, index).replace('/', '.'));
    }
    
    public static Type getReturnType(final String signature) {
        try {
            final int index = signature.lastIndexOf(41) + 1;
            return getType(signature.substring(index));
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
    }
    
    public static Type[] getArgumentTypes(final String signature) {
        final ArrayList vec = new ArrayList();
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            for (int index = 1; signature.charAt(index) != ')'; index += Type.consumed_chars) {
                vec.add(getType(signature.substring(index)));
            }
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        final Type[] types = new Type[vec.size()];
        vec.toArray(types);
        return types;
    }
    
    public static Type getType(final Class cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (cl.isArray()) {
            return getType(cl.getName());
        }
        if (!cl.isPrimitive()) {
            return new ObjectType(cl.getName());
        }
        if (cl == Integer.TYPE) {
            return Type.INT;
        }
        if (cl == Void.TYPE) {
            return Type.VOID;
        }
        if (cl == Double.TYPE) {
            return Type.DOUBLE;
        }
        if (cl == Float.TYPE) {
            return Type.FLOAT;
        }
        if (cl == Boolean.TYPE) {
            return Type.BOOLEAN;
        }
        if (cl == Byte.TYPE) {
            return Type.BYTE;
        }
        if (cl == Short.TYPE) {
            return Type.SHORT;
        }
        if (cl == Byte.TYPE) {
            return Type.BYTE;
        }
        if (cl == Long.TYPE) {
            return Type.LONG;
        }
        if (cl == Character.TYPE) {
            return Type.CHAR;
        }
        throw new IllegalStateException("Ooops, what primitive type is " + cl);
    }
    
    public static String getSignature(final Method meth) {
        final StringBuffer sb = new StringBuffer("(");
        final Class[] params = meth.getParameterTypes();
        for (int j = 0; j < params.length; ++j) {
            sb.append(getType(params[j]).getSignature());
        }
        sb.append(")");
        sb.append(getType(meth.getReturnType()).getSignature());
        return sb.toString();
    }
    
    static {
        VOID = new BasicType((byte)12);
        BOOLEAN = new BasicType((byte)4);
        INT = new BasicType((byte)10);
        SHORT = new BasicType((byte)9);
        BYTE = new BasicType((byte)8);
        LONG = new BasicType((byte)11);
        DOUBLE = new BasicType((byte)7);
        FLOAT = new BasicType((byte)6);
        CHAR = new BasicType((byte)5);
        OBJECT = new ObjectType("java.lang.Object");
        STRING = new ObjectType("java.lang.String");
        STRINGBUFFER = new ObjectType("java.lang.StringBuffer");
        THROWABLE = new ObjectType("java.lang.Throwable");
        NO_ARGS = new Type[0];
        NULL = new ReferenceType() {};
        UNKNOWN = new Type((byte)15, "<unknown object>") {};
        Type.consumed_chars = 0;
    }
}
