package org.apache.commons.compress.harmony.unpack200.bytecode;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.util.List;
import java.io.IOException;
import java.io.DataOutputStream;

public abstract class BCIRenumberedAttribute extends Attribute
{
    protected boolean renumbered;
    
    @Override
    public boolean hasBCIRenumbering() {
        return true;
    }
    
    public BCIRenumberedAttribute(final CPUTF8 attributeName) {
        super(attributeName);
    }
    
    @Override
    protected abstract int getLength();
    
    @Override
    protected abstract void writeBody(final DataOutputStream p0) throws IOException;
    
    @Override
    public abstract String toString();
    
    protected abstract int[] getStartPCs();
    
    public void renumber(final List byteCodeOffsets) throws Pack200Exception {
        if (this.renumbered) {
            throw new Error("Trying to renumber a line number table that has already been renumbered");
        }
        this.renumbered = true;
        final int[] startPCs = this.getStartPCs();
        for (int index = 0; index < startPCs.length; ++index) {
            startPCs[index] = byteCodeOffsets.get(startPCs[index]);
        }
    }
}
