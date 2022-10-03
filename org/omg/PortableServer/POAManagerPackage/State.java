package org.omg.PortableServer.POAManagerPackage;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class State implements IDLEntity
{
    private int __value;
    private static int __size;
    private static State[] __array;
    public static final int _HOLDING = 0;
    public static final State HOLDING;
    public static final int _ACTIVE = 1;
    public static final State ACTIVE;
    public static final int _DISCARDING = 2;
    public static final State DISCARDING;
    public static final int _INACTIVE = 3;
    public static final State INACTIVE;
    
    public int value() {
        return this.__value;
    }
    
    public static State from_int(final int n) {
        if (n >= 0 && n < State.__size) {
            return State.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected State(final int _value) {
        this.__value = _value;
        State.__array[this.__value] = this;
    }
    
    static {
        State.__size = 4;
        State.__array = new State[State.__size];
        HOLDING = new State(0);
        ACTIVE = new State(1);
        DISCARDING = new State(2);
        INACTIVE = new State(3);
    }
}
