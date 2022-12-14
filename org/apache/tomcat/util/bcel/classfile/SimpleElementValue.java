package org.apache.tomcat.util.bcel.classfile;

public class SimpleElementValue extends ElementValue
{
    private final int index;
    
    SimpleElementValue(final int type, final int index, final ConstantPool cpool) {
        super(type, cpool);
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    @Override
    public String stringifyValue() {
        final ConstantPool cpool = super.getConstantPool();
        final int _type = super.getType();
        switch (_type) {
            case 73: {
                final ConstantInteger c = (ConstantInteger)cpool.getConstant(this.getIndex(), (byte)3);
                return Integer.toString(c.getBytes());
            }
            case 74: {
                final ConstantLong j = (ConstantLong)cpool.getConstant(this.getIndex(), (byte)5);
                return Long.toString(j.getBytes());
            }
            case 68: {
                final ConstantDouble d = (ConstantDouble)cpool.getConstant(this.getIndex(), (byte)6);
                return Double.toString(d.getBytes());
            }
            case 70: {
                final ConstantFloat f = (ConstantFloat)cpool.getConstant(this.getIndex(), (byte)4);
                return Float.toString(f.getBytes());
            }
            case 83: {
                final ConstantInteger s = (ConstantInteger)cpool.getConstant(this.getIndex(), (byte)3);
                return Integer.toString(s.getBytes());
            }
            case 66: {
                final ConstantInteger b = (ConstantInteger)cpool.getConstant(this.getIndex(), (byte)3);
                return Integer.toString(b.getBytes());
            }
            case 67: {
                final ConstantInteger ch = (ConstantInteger)cpool.getConstant(this.getIndex(), (byte)3);
                return String.valueOf((char)ch.getBytes());
            }
            case 90: {
                final ConstantInteger bo = (ConstantInteger)cpool.getConstant(this.getIndex(), (byte)3);
                if (bo.getBytes() == 0) {
                    return "false";
                }
                return "true";
            }
            case 115: {
                final ConstantUtf8 cu8 = (ConstantUtf8)cpool.getConstant(this.getIndex(), (byte)1);
                return cu8.getBytes();
            }
            default: {
                throw new IllegalStateException("SimpleElementValue class does not know how to stringify type " + _type);
            }
        }
    }
}
