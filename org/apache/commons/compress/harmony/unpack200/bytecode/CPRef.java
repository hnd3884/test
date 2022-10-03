package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public abstract class CPRef extends ConstantPoolEntry
{
    CPClass className;
    transient int classNameIndex;
    protected CPNameAndType nameAndType;
    transient int nameAndTypeIndex;
    protected String cachedToString;
    
    public CPRef(final byte type, final CPClass className, final CPNameAndType descriptor, final int globalIndex) {
        super(type, globalIndex);
        this.className = className;
        this.nameAndType = descriptor;
        if (descriptor == null || className == null) {
            throw new NullPointerException("Null arguments are not allowed");
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        if (this.hashCode() != obj.hashCode()) {
            return false;
        }
        final CPRef other = (CPRef)obj;
        return this.className.equals(other.className) && this.nameAndType.equals(other.nameAndType);
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final ClassFileEntry[] entries = { this.className, this.nameAndType };
        return entries;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.nameAndTypeIndex = pool.indexOf(this.nameAndType);
        this.classNameIndex = pool.indexOf(this.className);
    }
    
    @Override
    public String toString() {
        if (this.cachedToString == null) {
            String type;
            if (this.getTag() == 9) {
                type = "FieldRef";
            }
            else if (this.getTag() == 10) {
                type = "MethoddRef";
            }
            else if (this.getTag() == 11) {
                type = "InterfaceMethodRef";
            }
            else {
                type = "unknown";
            }
            this.cachedToString = type + ": " + this.className + "#" + this.nameAndType;
        }
        return this.cachedToString;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.classNameIndex);
        dos.writeShort(this.nameAndTypeIndex);
    }
}
