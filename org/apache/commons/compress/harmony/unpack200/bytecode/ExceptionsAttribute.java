package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;
import java.util.Arrays;

public class ExceptionsAttribute extends Attribute
{
    private static CPUTF8 attributeName;
    private transient int[] exceptionIndexes;
    private final CPClass[] exceptions;
    
    private static int hashCode(final Object[] array) {
        final int prime = 31;
        if (array == null) {
            return 0;
        }
        int result = 1;
        for (int index = 0; index < array.length; ++index) {
            result = 31 * result + ((array[index] == null) ? 0 : array[index].hashCode());
        }
        return result;
    }
    
    public ExceptionsAttribute(final CPClass[] exceptions) {
        super(ExceptionsAttribute.attributeName);
        this.exceptions = exceptions;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ExceptionsAttribute other = (ExceptionsAttribute)obj;
        return Arrays.equals(this.exceptions, other.exceptions);
    }
    
    @Override
    protected int getLength() {
        return 2 + 2 * this.exceptions.length;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final ClassFileEntry[] result = new ClassFileEntry[this.exceptions.length + 1];
        for (int i = 0; i < this.exceptions.length; ++i) {
            result[i] = this.exceptions[i];
        }
        result[this.exceptions.length] = this.getAttributeName();
        return result;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + hashCode(this.exceptions);
        return result;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.exceptionIndexes = new int[this.exceptions.length];
        for (int i = 0; i < this.exceptions.length; ++i) {
            this.exceptions[i].resolve(pool);
            this.exceptionIndexes[i] = pool.indexOf(this.exceptions[i]);
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Exceptions: ");
        for (int i = 0; i < this.exceptions.length; ++i) {
            sb.append(this.exceptions[i]);
            sb.append(' ');
        }
        return sb.toString();
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.exceptionIndexes.length);
        for (int i = 0; i < this.exceptionIndexes.length; ++i) {
            dos.writeShort(this.exceptionIndexes[i]);
        }
    }
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        ExceptionsAttribute.attributeName = cpUTF8Value;
    }
}
