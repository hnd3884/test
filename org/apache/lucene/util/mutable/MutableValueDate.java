package org.apache.lucene.util.mutable;

import java.util.Date;

public class MutableValueDate extends MutableValueLong
{
    @Override
    public Object toObject() {
        return this.exists ? new Date(this.value) : null;
    }
    
    @Override
    public MutableValue duplicate() {
        final MutableValueDate v = new MutableValueDate();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }
}
