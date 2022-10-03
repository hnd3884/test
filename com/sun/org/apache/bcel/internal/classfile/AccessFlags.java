package com.sun.org.apache.bcel.internal.classfile;

import java.io.Serializable;

public abstract class AccessFlags implements Serializable
{
    protected int access_flags;
    
    public AccessFlags() {
    }
    
    public AccessFlags(final int a) {
        this.access_flags = a;
    }
    
    public final int getAccessFlags() {
        return this.access_flags;
    }
    
    public final int getModifiers() {
        return this.access_flags;
    }
    
    public final void setAccessFlags(final int access_flags) {
        this.access_flags = access_flags;
    }
    
    public final void setModifiers(final int access_flags) {
        this.setAccessFlags(access_flags);
    }
    
    private final void setFlag(final int flag, final boolean set) {
        if ((this.access_flags & flag) != 0x0) {
            if (!set) {
                this.access_flags ^= flag;
            }
        }
        else if (set) {
            this.access_flags |= flag;
        }
    }
    
    public final void isPublic(final boolean flag) {
        this.setFlag(1, flag);
    }
    
    public final boolean isPublic() {
        return (this.access_flags & 0x1) != 0x0;
    }
    
    public final void isPrivate(final boolean flag) {
        this.setFlag(2, flag);
    }
    
    public final boolean isPrivate() {
        return (this.access_flags & 0x2) != 0x0;
    }
    
    public final void isProtected(final boolean flag) {
        this.setFlag(4, flag);
    }
    
    public final boolean isProtected() {
        return (this.access_flags & 0x4) != 0x0;
    }
    
    public final void isStatic(final boolean flag) {
        this.setFlag(8, flag);
    }
    
    public final boolean isStatic() {
        return (this.access_flags & 0x8) != 0x0;
    }
    
    public final void isFinal(final boolean flag) {
        this.setFlag(16, flag);
    }
    
    public final boolean isFinal() {
        return (this.access_flags & 0x10) != 0x0;
    }
    
    public final void isSynchronized(final boolean flag) {
        this.setFlag(32, flag);
    }
    
    public final boolean isSynchronized() {
        return (this.access_flags & 0x20) != 0x0;
    }
    
    public final void isVolatile(final boolean flag) {
        this.setFlag(64, flag);
    }
    
    public final boolean isVolatile() {
        return (this.access_flags & 0x40) != 0x0;
    }
    
    public final void isTransient(final boolean flag) {
        this.setFlag(128, flag);
    }
    
    public final boolean isTransient() {
        return (this.access_flags & 0x80) != 0x0;
    }
    
    public final void isNative(final boolean flag) {
        this.setFlag(256, flag);
    }
    
    public final boolean isNative() {
        return (this.access_flags & 0x100) != 0x0;
    }
    
    public final void isInterface(final boolean flag) {
        this.setFlag(512, flag);
    }
    
    public final boolean isInterface() {
        return (this.access_flags & 0x200) != 0x0;
    }
    
    public final void isAbstract(final boolean flag) {
        this.setFlag(1024, flag);
    }
    
    public final boolean isAbstract() {
        return (this.access_flags & 0x400) != 0x0;
    }
    
    public final void isStrictfp(final boolean flag) {
        this.setFlag(2048, flag);
    }
    
    public final boolean isStrictfp() {
        return (this.access_flags & 0x800) != 0x0;
    }
}
