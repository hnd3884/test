package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import com.sun.org.apache.bcel.internal.Constants;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.Serializable;

public abstract class Constant implements Cloneable, Node, Serializable
{
    protected byte tag;
    
    Constant(final byte tag) {
        this.tag = tag;
    }
    
    @Override
    public abstract void accept(final Visitor p0);
    
    public abstract void dump(final DataOutputStream p0) throws IOException;
    
    public final byte getTag() {
        return this.tag;
    }
    
    @Override
    public String toString() {
        return Constants.CONSTANT_NAMES[this.tag] + "[" + this.tag + "]";
    }
    
    public Constant copy() {
        try {
            return (Constant)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    static final Constant readConstant(final DataInputStream file) throws IOException, ClassFormatException {
        final byte b = file.readByte();
        switch (b) {
            case 7: {
                return new ConstantClass(file);
            }
            case 9: {
                return new ConstantFieldref(file);
            }
            case 10: {
                return new ConstantMethodref(file);
            }
            case 11: {
                return new ConstantInterfaceMethodref(file);
            }
            case 8: {
                return new ConstantString(file);
            }
            case 3: {
                return new ConstantInteger(file);
            }
            case 4: {
                return new ConstantFloat(file);
            }
            case 5: {
                return new ConstantLong(file);
            }
            case 6: {
                return new ConstantDouble(file);
            }
            case 12: {
                return new ConstantNameAndType(file);
            }
            case 1: {
                return new ConstantUtf8(file);
            }
            default: {
                throw new ClassFormatException("Invalid byte tag in constant pool: " + b);
            }
        }
    }
}
