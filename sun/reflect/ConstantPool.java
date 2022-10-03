package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

public class ConstantPool
{
    private Object constantPoolOop;
    
    public int getSize() {
        return this.getSize0(this.constantPoolOop);
    }
    
    public Class<?> getClassAt(final int n) {
        return this.getClassAt0(this.constantPoolOop, n);
    }
    
    public Class<?> getClassAtIfLoaded(final int n) {
        return this.getClassAtIfLoaded0(this.constantPoolOop, n);
    }
    
    public Member getMethodAt(final int n) {
        return this.getMethodAt0(this.constantPoolOop, n);
    }
    
    public Member getMethodAtIfLoaded(final int n) {
        return this.getMethodAtIfLoaded0(this.constantPoolOop, n);
    }
    
    public Field getFieldAt(final int n) {
        return this.getFieldAt0(this.constantPoolOop, n);
    }
    
    public Field getFieldAtIfLoaded(final int n) {
        return this.getFieldAtIfLoaded0(this.constantPoolOop, n);
    }
    
    public String[] getMemberRefInfoAt(final int n) {
        return this.getMemberRefInfoAt0(this.constantPoolOop, n);
    }
    
    public int getIntAt(final int n) {
        return this.getIntAt0(this.constantPoolOop, n);
    }
    
    public long getLongAt(final int n) {
        return this.getLongAt0(this.constantPoolOop, n);
    }
    
    public float getFloatAt(final int n) {
        return this.getFloatAt0(this.constantPoolOop, n);
    }
    
    public double getDoubleAt(final int n) {
        return this.getDoubleAt0(this.constantPoolOop, n);
    }
    
    public String getStringAt(final int n) {
        return this.getStringAt0(this.constantPoolOop, n);
    }
    
    public String getUTF8At(final int n) {
        return this.getUTF8At0(this.constantPoolOop, n);
    }
    
    private native int getSize0(final Object p0);
    
    private native Class<?> getClassAt0(final Object p0, final int p1);
    
    private native Class<?> getClassAtIfLoaded0(final Object p0, final int p1);
    
    private native Member getMethodAt0(final Object p0, final int p1);
    
    private native Member getMethodAtIfLoaded0(final Object p0, final int p1);
    
    private native Field getFieldAt0(final Object p0, final int p1);
    
    private native Field getFieldAtIfLoaded0(final Object p0, final int p1);
    
    private native String[] getMemberRefInfoAt0(final Object p0, final int p1);
    
    private native int getIntAt0(final Object p0, final int p1);
    
    private native long getLongAt0(final Object p0, final int p1);
    
    private native float getFloatAt0(final Object p0, final int p1);
    
    private native double getDoubleAt0(final Object p0, final int p1);
    
    private native String getStringAt0(final Object p0, final int p1);
    
    private native String getUTF8At0(final Object p0, final int p1);
    
    static {
        Reflection.registerFieldsToFilter(ConstantPool.class, "constantPoolOop");
    }
}
