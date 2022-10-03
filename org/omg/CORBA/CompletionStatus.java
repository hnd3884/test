package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class CompletionStatus implements IDLEntity
{
    public static final int _COMPLETED_YES = 0;
    public static final int _COMPLETED_NO = 1;
    public static final int _COMPLETED_MAYBE = 2;
    public static final CompletionStatus COMPLETED_YES;
    public static final CompletionStatus COMPLETED_NO;
    public static final CompletionStatus COMPLETED_MAYBE;
    private int _value;
    
    public int value() {
        return this._value;
    }
    
    public static CompletionStatus from_int(final int n) {
        switch (n) {
            case 0: {
                return CompletionStatus.COMPLETED_YES;
            }
            case 1: {
                return CompletionStatus.COMPLETED_NO;
            }
            case 2: {
                return CompletionStatus.COMPLETED_MAYBE;
            }
            default: {
                throw new BAD_PARAM();
            }
        }
    }
    
    private CompletionStatus(final int value) {
        this._value = value;
    }
    
    static {
        COMPLETED_YES = new CompletionStatus(0);
        COMPLETED_NO = new CompletionStatus(1);
        COMPLETED_MAYBE = new CompletionStatus(2);
    }
}
