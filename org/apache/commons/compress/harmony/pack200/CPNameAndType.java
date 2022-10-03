package org.apache.commons.compress.harmony.pack200;

public class CPNameAndType extends ConstantPoolEntry implements Comparable
{
    private final CPUTF8 name;
    private final CPSignature signature;
    
    public CPNameAndType(final CPUTF8 name, final CPSignature signature) {
        this.name = name;
        this.signature = signature;
    }
    
    @Override
    public String toString() {
        return this.name + ":" + this.signature;
    }
    
    @Override
    public int compareTo(final Object obj) {
        if (!(obj instanceof CPNameAndType)) {
            return 0;
        }
        final CPNameAndType nat = (CPNameAndType)obj;
        final int compareSignature = this.signature.compareTo(nat.signature);
        if (compareSignature == 0) {
            return this.name.compareTo(nat.name);
        }
        return compareSignature;
    }
    
    public int getNameIndex() {
        return this.name.getIndex();
    }
    
    public String getName() {
        return this.name.getUnderlyingString();
    }
    
    public int getTypeIndex() {
        return this.signature.getIndex();
    }
}
