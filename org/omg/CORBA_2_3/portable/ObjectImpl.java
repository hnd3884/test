package org.omg.CORBA_2_3.portable;

import org.omg.CORBA.Object;

public abstract class ObjectImpl extends org.omg.CORBA.portable.ObjectImpl
{
    public String _get_codebase() {
        final org.omg.CORBA.portable.Delegate get_delegate = this._get_delegate();
        if (get_delegate instanceof Delegate) {
            return ((Delegate)get_delegate).get_codebase(this);
        }
        return null;
    }
}
