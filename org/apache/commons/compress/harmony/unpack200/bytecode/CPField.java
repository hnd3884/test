package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;

public class CPField extends CPMember
{
    public CPField(final CPUTF8 name, final CPUTF8 descriptor, final long flags, final List attributes) {
        super(name, descriptor, flags, attributes);
    }
    
    @Override
    public String toString() {
        return "Field: " + this.name + "(" + this.descriptor + ")";
    }
}
