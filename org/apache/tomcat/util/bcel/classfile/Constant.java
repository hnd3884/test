package org.apache.tomcat.util.bcel.classfile;

import java.io.IOException;
import java.io.DataInput;

public abstract class Constant
{
    protected final byte tag;
    
    Constant(final byte tag) {
        this.tag = tag;
    }
    
    public final byte getTag() {
        return this.tag;
    }
    
    static Constant readConstant(final DataInput dataInput) throws IOException, ClassFormatException {
        final byte b = dataInput.readByte();
        int skipSize = 0;
        switch (b) {
            case 7: {
                return new ConstantClass(dataInput);
            }
            case 3: {
                return new ConstantInteger(dataInput);
            }
            case 4: {
                return new ConstantFloat(dataInput);
            }
            case 5: {
                return new ConstantLong(dataInput);
            }
            case 6: {
                return new ConstantDouble(dataInput);
            }
            case 1: {
                return ConstantUtf8.getInstance(dataInput);
            }
            case 8:
            case 16:
            case 19:
            case 20: {
                skipSize = 2;
                break;
            }
            case 15: {
                skipSize = 3;
                break;
            }
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
            case 18: {
                skipSize = 4;
                break;
            }
            default: {
                throw new ClassFormatException("Invalid byte tag in constant pool: " + b);
            }
        }
        Utility.skipFully(dataInput, skipSize);
        return null;
    }
    
    @Override
    public String toString() {
        return "[" + this.tag + "]";
    }
}
