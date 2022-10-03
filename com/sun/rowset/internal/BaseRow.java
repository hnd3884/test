package com.sun.rowset.internal;

import java.sql.SQLException;
import java.util.Arrays;
import java.io.Serializable;

public abstract class BaseRow implements Serializable, Cloneable
{
    private static final long serialVersionUID = 4152013523511412238L;
    protected Object[] origVals;
    
    public Object[] getOrigRow() {
        final Object[] origVals = this.origVals;
        return (Object[])((origVals == null) ? null : Arrays.copyOf(origVals, origVals.length));
    }
    
    public abstract Object getColumnObject(final int p0) throws SQLException;
    
    public abstract void setColumnObject(final int p0, final Object p1) throws SQLException;
}
