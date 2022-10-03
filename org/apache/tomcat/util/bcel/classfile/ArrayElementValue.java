package org.apache.tomcat.util.bcel.classfile;

public class ArrayElementValue extends ElementValue
{
    private final ElementValue[] elementValues;
    
    ArrayElementValue(final int type, final ElementValue[] datums, final ConstantPool cpool) {
        super(type, cpool);
        if (type != 91) {
            throw new IllegalArgumentException("Only element values of type array can be built with this ctor - type specified: " + type);
        }
        this.elementValues = datums;
    }
    
    @Override
    public String stringifyValue() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < this.elementValues.length; ++i) {
            sb.append(this.elementValues[i].stringifyValue());
            if (i + 1 < this.elementValues.length) {
                sb.append(',');
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    public ElementValue[] getElementValuesArray() {
        return this.elementValues;
    }
}
