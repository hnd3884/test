package org.apache.commons.compress.harmony.pack200;

public class CPClass extends CPConstant implements Comparable
{
    private final String className;
    private final CPUTF8 utf8;
    private final boolean isInnerClass;
    
    public CPClass(final CPUTF8 utf8) {
        this.utf8 = utf8;
        this.className = utf8.getUnderlyingString();
        final char[] chars = this.className.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] <= '-') {
                this.isInnerClass = true;
                return;
            }
        }
        this.isInnerClass = false;
    }
    
    @Override
    public int compareTo(final Object arg0) {
        return this.className.compareTo(((CPClass)arg0).className);
    }
    
    @Override
    public String toString() {
        return this.className;
    }
    
    public int getIndexInCpUtf8() {
        return this.utf8.getIndex();
    }
    
    public boolean isInnerClass() {
        return this.isInnerClass;
    }
}
