package sun.reflect;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class UnsafeFieldAccessorImpl extends FieldAccessorImpl
{
    static final Unsafe unsafe;
    protected final Field field;
    protected final long fieldOffset;
    protected final boolean isFinal;
    
    UnsafeFieldAccessorImpl(final Field field) {
        this.field = field;
        if (Modifier.isStatic(field.getModifiers())) {
            this.fieldOffset = UnsafeFieldAccessorImpl.unsafe.staticFieldOffset(field);
        }
        else {
            this.fieldOffset = UnsafeFieldAccessorImpl.unsafe.objectFieldOffset(field);
        }
        this.isFinal = Modifier.isFinal(field.getModifiers());
    }
    
    protected void ensureObj(final Object o) {
        if (!this.field.getDeclaringClass().isAssignableFrom(o.getClass())) {
            this.throwSetIllegalArgumentException(o);
        }
    }
    
    private String getQualifiedFieldName() {
        return this.field.getDeclaringClass().getName() + "." + this.field.getName();
    }
    
    protected IllegalArgumentException newGetIllegalArgumentException(final String s) {
        return new IllegalArgumentException("Attempt to get " + this.field.getType().getName() + " field \"" + this.getQualifiedFieldName() + "\" with illegal data type conversion to " + s);
    }
    
    protected void throwFinalFieldIllegalAccessException(final String s, final String s2) throws IllegalAccessException {
        throw new IllegalAccessException(this.getSetMessage(s, s2));
    }
    
    protected void throwFinalFieldIllegalAccessException(final Object o) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException((o != null) ? o.getClass().getName() : "", "");
    }
    
    protected void throwFinalFieldIllegalAccessException(final boolean b) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("boolean", Boolean.toString(b));
    }
    
    protected void throwFinalFieldIllegalAccessException(final char c) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("char", Character.toString(c));
    }
    
    protected void throwFinalFieldIllegalAccessException(final byte b) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("byte", Byte.toString(b));
    }
    
    protected void throwFinalFieldIllegalAccessException(final short n) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("short", Short.toString(n));
    }
    
    protected void throwFinalFieldIllegalAccessException(final int n) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("int", Integer.toString(n));
    }
    
    protected void throwFinalFieldIllegalAccessException(final long n) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("long", Long.toString(n));
    }
    
    protected void throwFinalFieldIllegalAccessException(final float n) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("float", Float.toString(n));
    }
    
    protected void throwFinalFieldIllegalAccessException(final double n) throws IllegalAccessException {
        this.throwFinalFieldIllegalAccessException("double", Double.toString(n));
    }
    
    protected IllegalArgumentException newGetBooleanIllegalArgumentException() {
        return this.newGetIllegalArgumentException("boolean");
    }
    
    protected IllegalArgumentException newGetByteIllegalArgumentException() {
        return this.newGetIllegalArgumentException("byte");
    }
    
    protected IllegalArgumentException newGetCharIllegalArgumentException() {
        return this.newGetIllegalArgumentException("char");
    }
    
    protected IllegalArgumentException newGetShortIllegalArgumentException() {
        return this.newGetIllegalArgumentException("short");
    }
    
    protected IllegalArgumentException newGetIntIllegalArgumentException() {
        return this.newGetIllegalArgumentException("int");
    }
    
    protected IllegalArgumentException newGetLongIllegalArgumentException() {
        return this.newGetIllegalArgumentException("long");
    }
    
    protected IllegalArgumentException newGetFloatIllegalArgumentException() {
        return this.newGetIllegalArgumentException("float");
    }
    
    protected IllegalArgumentException newGetDoubleIllegalArgumentException() {
        return this.newGetIllegalArgumentException("double");
    }
    
    protected String getSetMessage(final String s, final String s2) {
        String s3 = "Can not set";
        if (Modifier.isStatic(this.field.getModifiers())) {
            s3 += " static";
        }
        if (this.isFinal) {
            s3 += " final";
        }
        final String string = s3 + " " + this.field.getType().getName() + " field " + this.getQualifiedFieldName() + " to ";
        String s4;
        if (s2.length() > 0) {
            s4 = string + "(" + s + ")" + s2;
        }
        else if (s.length() > 0) {
            s4 = string + s;
        }
        else {
            s4 = string + "null value";
        }
        return s4;
    }
    
    protected void throwSetIllegalArgumentException(final String s, final String s2) {
        throw new IllegalArgumentException(this.getSetMessage(s, s2));
    }
    
    protected void throwSetIllegalArgumentException(final Object o) {
        this.throwSetIllegalArgumentException((o != null) ? o.getClass().getName() : "", "");
    }
    
    protected void throwSetIllegalArgumentException(final boolean b) {
        this.throwSetIllegalArgumentException("boolean", Boolean.toString(b));
    }
    
    protected void throwSetIllegalArgumentException(final byte b) {
        this.throwSetIllegalArgumentException("byte", Byte.toString(b));
    }
    
    protected void throwSetIllegalArgumentException(final char c) {
        this.throwSetIllegalArgumentException("char", Character.toString(c));
    }
    
    protected void throwSetIllegalArgumentException(final short n) {
        this.throwSetIllegalArgumentException("short", Short.toString(n));
    }
    
    protected void throwSetIllegalArgumentException(final int n) {
        this.throwSetIllegalArgumentException("int", Integer.toString(n));
    }
    
    protected void throwSetIllegalArgumentException(final long n) {
        this.throwSetIllegalArgumentException("long", Long.toString(n));
    }
    
    protected void throwSetIllegalArgumentException(final float n) {
        this.throwSetIllegalArgumentException("float", Float.toString(n));
    }
    
    protected void throwSetIllegalArgumentException(final double n) {
        this.throwSetIllegalArgumentException("double", Double.toString(n));
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
