package com.google.common.collect;

import javax.annotation.CheckForNull;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class Count implements Serializable
{
    private int value;
    
    Count(final int value) {
        this.value = value;
    }
    
    public int get() {
        return this.value;
    }
    
    public void add(final int delta) {
        this.value += delta;
    }
    
    public int addAndGet(final int delta) {
        return this.value += delta;
    }
    
    public void set(final int newValue) {
        this.value = newValue;
    }
    
    public int getAndSet(final int newValue) {
        final int result = this.value;
        this.value = newValue;
        return result;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        return obj instanceof Count && ((Count)obj).value == this.value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
