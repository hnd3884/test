package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

class HpackHeaderField
{
    static final int HEADER_ENTRY_OVERHEAD = 32;
    final CharSequence name;
    final CharSequence value;
    
    static long sizeOf(final CharSequence name, final CharSequence value) {
        return name.length() + value.length() + 32;
    }
    
    HpackHeaderField(final CharSequence name, final CharSequence value) {
        this.name = ObjectUtil.checkNotNull(name, "name");
        this.value = ObjectUtil.checkNotNull(value, "value");
    }
    
    final int size() {
        return this.name.length() + this.value.length() + 32;
    }
    
    public final boolean equalsForTest(final HpackHeaderField other) {
        return HpackUtil.equalsVariableTime(this.name, other.name) && HpackUtil.equalsVariableTime(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return (Object)this.name + ": " + (Object)this.value;
    }
}
