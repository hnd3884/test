package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.IOP.TaggedProfile;
import org.omg.CORBA.portable.IDLEntity;

public final class TargetAddress implements IDLEntity
{
    private byte[] ___object_key;
    private TaggedProfile ___profile;
    private IORAddressingInfo ___ior;
    private short __discriminator;
    private boolean __uninitialized;
    
    public TargetAddress() {
        this.__uninitialized = true;
    }
    
    public short discriminator() {
        if (this.__uninitialized) {
            throw new BAD_OPERATION();
        }
        return this.__discriminator;
    }
    
    public byte[] object_key() {
        if (this.__uninitialized) {
            throw new BAD_OPERATION();
        }
        this.verifyobject_key(this.__discriminator);
        return this.___object_key;
    }
    
    public void object_key(final byte[] __object_key) {
        this.__discriminator = 0;
        this.___object_key = __object_key;
        this.__uninitialized = false;
    }
    
    private void verifyobject_key(final short n) {
        if (n != 0) {
            throw new BAD_OPERATION();
        }
    }
    
    public TaggedProfile profile() {
        if (this.__uninitialized) {
            throw new BAD_OPERATION();
        }
        this.verifyprofile(this.__discriminator);
        return this.___profile;
    }
    
    public void profile(final TaggedProfile __profile) {
        this.__discriminator = 1;
        this.___profile = __profile;
        this.__uninitialized = false;
    }
    
    private void verifyprofile(final short n) {
        if (n != 1) {
            throw new BAD_OPERATION();
        }
    }
    
    public IORAddressingInfo ior() {
        if (this.__uninitialized) {
            throw new BAD_OPERATION();
        }
        this.verifyior(this.__discriminator);
        return this.___ior;
    }
    
    public void ior(final IORAddressingInfo __ior) {
        this.__discriminator = 2;
        this.___ior = __ior;
        this.__uninitialized = false;
    }
    
    private void verifyior(final short n) {
        if (n != 2) {
            throw new BAD_OPERATION();
        }
    }
    
    public void _default() {
        this.__discriminator = -32768;
        this.__uninitialized = false;
    }
}
