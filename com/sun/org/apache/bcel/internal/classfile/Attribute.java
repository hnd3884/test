package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.io.Serializable;

public abstract class Attribute implements Cloneable, Node, Serializable
{
    protected int name_index;
    protected int length;
    protected byte tag;
    protected ConstantPool constant_pool;
    private static HashMap readers;
    
    protected Attribute(final byte tag, final int name_index, final int length, final ConstantPool constant_pool) {
        this.tag = tag;
        this.name_index = name_index;
        this.length = length;
        this.constant_pool = constant_pool;
    }
    
    @Override
    public abstract void accept(final Visitor p0);
    
    public void dump(final DataOutputStream file) throws IOException {
        file.writeShort(this.name_index);
        file.writeInt(this.length);
    }
    
    public static void addAttributeReader(final String name, final AttributeReader r) {
        Attribute.readers.put(name, r);
    }
    
    public static void removeAttributeReader(final String name) {
        Attribute.readers.remove(name);
    }
    
    public static final Attribute readAttribute(final DataInputStream file, final ConstantPool constant_pool) throws IOException, ClassFormatException {
        byte tag = -1;
        final int name_index = file.readUnsignedShort();
        final ConstantUtf8 c = (ConstantUtf8)constant_pool.getConstant(name_index, (byte)1);
        final String name = c.getBytes();
        final int length = file.readInt();
        for (byte i = 0; i < 13; ++i) {
            if (name.equals(Constants.ATTRIBUTE_NAMES[i])) {
                tag = i;
                break;
            }
        }
        switch (tag) {
            case -1: {
                final AttributeReader r = Attribute.readers.get(name);
                if (r != null) {
                    return r.createAttribute(name_index, length, file, constant_pool);
                }
                return new Unknown(name_index, length, file, constant_pool);
            }
            case 1: {
                return new ConstantValue(name_index, length, file, constant_pool);
            }
            case 0: {
                return new SourceFile(name_index, length, file, constant_pool);
            }
            case 2: {
                return new Code(name_index, length, file, constant_pool);
            }
            case 3: {
                return new ExceptionTable(name_index, length, file, constant_pool);
            }
            case 4: {
                return new LineNumberTable(name_index, length, file, constant_pool);
            }
            case 5: {
                return new LocalVariableTable(name_index, length, file, constant_pool);
            }
            case 12: {
                return new LocalVariableTypeTable(name_index, length, file, constant_pool);
            }
            case 6: {
                return new InnerClasses(name_index, length, file, constant_pool);
            }
            case 7: {
                return new Synthetic(name_index, length, file, constant_pool);
            }
            case 8: {
                return new Deprecated(name_index, length, file, constant_pool);
            }
            case 9: {
                return new PMGClass(name_index, length, file, constant_pool);
            }
            case 10: {
                return new Signature(name_index, length, file, constant_pool);
            }
            case 11: {
                return new StackMap(name_index, length, file, constant_pool);
            }
            default: {
                throw new IllegalStateException("Ooops! default case reached.");
            }
        }
    }
    
    public final int getLength() {
        return this.length;
    }
    
    public final void setLength(final int length) {
        this.length = length;
    }
    
    public final void setNameIndex(final int name_index) {
        this.name_index = name_index;
    }
    
    public final int getNameIndex() {
        return this.name_index;
    }
    
    public final byte getTag() {
        return this.tag;
    }
    
    public final ConstantPool getConstantPool() {
        return this.constant_pool;
    }
    
    public final void setConstantPool(final ConstantPool constant_pool) {
        this.constant_pool = constant_pool;
    }
    
    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        }
        catch (final CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
    
    public abstract Attribute copy(final ConstantPool p0);
    
    @Override
    public String toString() {
        return Constants.ATTRIBUTE_NAMES[this.tag];
    }
    
    static {
        Attribute.readers = new HashMap();
    }
}
