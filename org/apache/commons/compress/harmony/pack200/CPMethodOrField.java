package org.apache.commons.compress.harmony.pack200;

public class CPMethodOrField extends ConstantPoolEntry implements Comparable
{
    private final CPClass className;
    private final CPNameAndType nameAndType;
    private int indexInClass;
    private int indexInClassForConstructor;
    
    public CPMethodOrField(final CPClass className, final CPNameAndType nameAndType) {
        this.indexInClass = -1;
        this.indexInClassForConstructor = -1;
        this.className = className;
        this.nameAndType = nameAndType;
    }
    
    @Override
    public String toString() {
        return this.className + ": " + this.nameAndType;
    }
    
    @Override
    public int compareTo(final Object obj) {
        if (!(obj instanceof CPMethodOrField)) {
            return 0;
        }
        final CPMethodOrField mof = (CPMethodOrField)obj;
        final int compareName = this.className.compareTo(mof.className);
        if (compareName == 0) {
            return this.nameAndType.compareTo(mof.nameAndType);
        }
        return compareName;
    }
    
    public int getClassIndex() {
        return this.className.getIndex();
    }
    
    public CPClass getClassName() {
        return this.className;
    }
    
    public int getDescIndex() {
        return this.nameAndType.getIndex();
    }
    
    public CPNameAndType getDesc() {
        return this.nameAndType;
    }
    
    public int getIndexInClass() {
        return this.indexInClass;
    }
    
    public void setIndexInClass(final int index) {
        this.indexInClass = index;
    }
    
    public int getIndexInClassForConstructor() {
        return this.indexInClassForConstructor;
    }
    
    public void setIndexInClassForConstructor(final int index) {
        this.indexInClassForConstructor = index;
    }
}
