package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ImplicitActivationPolicyValue implements IDLEntity
{
    private int __value;
    private static int __size;
    private static ImplicitActivationPolicyValue[] __array;
    public static final int _IMPLICIT_ACTIVATION = 0;
    public static final ImplicitActivationPolicyValue IMPLICIT_ACTIVATION;
    public static final int _NO_IMPLICIT_ACTIVATION = 1;
    public static final ImplicitActivationPolicyValue NO_IMPLICIT_ACTIVATION;
    
    public int value() {
        return this.__value;
    }
    
    public static ImplicitActivationPolicyValue from_int(final int n) {
        if (n >= 0 && n < ImplicitActivationPolicyValue.__size) {
            return ImplicitActivationPolicyValue.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected ImplicitActivationPolicyValue(final int _value) {
        this.__value = _value;
        ImplicitActivationPolicyValue.__array[this.__value] = this;
    }
    
    static {
        ImplicitActivationPolicyValue.__size = 2;
        ImplicitActivationPolicyValue.__array = new ImplicitActivationPolicyValue[ImplicitActivationPolicyValue.__size];
        IMPLICIT_ACTIVATION = new ImplicitActivationPolicyValue(0);
        NO_IMPLICIT_ACTIVATION = new ImplicitActivationPolicyValue(1);
    }
}
