package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class NotFoundReason implements IDLEntity
{
    private int __value;
    private static int __size;
    private static NotFoundReason[] __array;
    public static final int _missing_node = 0;
    public static final NotFoundReason missing_node;
    public static final int _not_context = 1;
    public static final NotFoundReason not_context;
    public static final int _not_object = 2;
    public static final NotFoundReason not_object;
    
    public int value() {
        return this.__value;
    }
    
    public static NotFoundReason from_int(final int n) {
        if (n >= 0 && n < NotFoundReason.__size) {
            return NotFoundReason.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected NotFoundReason(final int _value) {
        this.__value = _value;
        NotFoundReason.__array[this.__value] = this;
    }
    
    static {
        NotFoundReason.__size = 3;
        NotFoundReason.__array = new NotFoundReason[NotFoundReason.__size];
        missing_node = new NotFoundReason(0);
        not_context = new NotFoundReason(1);
        not_object = new NotFoundReason(2);
    }
}
