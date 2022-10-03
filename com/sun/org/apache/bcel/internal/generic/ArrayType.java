package com.sun.org.apache.bcel.internal.generic;

public final class ArrayType extends ReferenceType
{
    private int dimensions;
    private Type basic_type;
    
    public ArrayType(final byte type, final int dimensions) {
        this(BasicType.getType(type), dimensions);
    }
    
    public ArrayType(final String class_name, final int dimensions) {
        this(new ObjectType(class_name), dimensions);
    }
    
    public ArrayType(final Type type, final int dimensions) {
        super((byte)13, "<dummy>");
        if (dimensions < 1 || dimensions > 255) {
            throw new ClassGenException("Invalid number of dimensions: " + dimensions);
        }
        switch (type.getType()) {
            case 13: {
                final ArrayType array = (ArrayType)type;
                this.dimensions = dimensions + array.dimensions;
                this.basic_type = array.basic_type;
                break;
            }
            case 12: {
                throw new ClassGenException("Invalid type: void[]");
            }
            default: {
                this.dimensions = dimensions;
                this.basic_type = type;
                break;
            }
        }
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.dimensions; ++i) {
            buf.append('[');
        }
        buf.append(this.basic_type.getSignature());
        this.signature = buf.toString();
    }
    
    public Type getBasicType() {
        return this.basic_type;
    }
    
    public Type getElementType() {
        if (this.dimensions == 1) {
            return this.basic_type;
        }
        return new ArrayType(this.basic_type, this.dimensions - 1);
    }
    
    public int getDimensions() {
        return this.dimensions;
    }
    
    @Override
    public int hashCode() {
        return this.basic_type.hashCode() ^ this.dimensions;
    }
    
    @Override
    public boolean equals(final Object type) {
        if (type instanceof ArrayType) {
            final ArrayType array = (ArrayType)type;
            return array.dimensions == this.dimensions && array.basic_type.equals(this.basic_type);
        }
        return false;
    }
}
