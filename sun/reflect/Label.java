package sun.reflect;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

class Label
{
    private List<PatchInfo> patches;
    
    public Label() {
        this.patches = new ArrayList<PatchInfo>();
    }
    
    void add(final ClassFileAssembler classFileAssembler, final short n, final short n2, final int n3) {
        this.patches.add(new PatchInfo(classFileAssembler, n, n2, n3));
    }
    
    public void bind() {
        for (final PatchInfo patchInfo : this.patches) {
            patchInfo.asm.emitShort(patchInfo.patchBCI, (short)(patchInfo.asm.getLength() - patchInfo.instrBCI));
            patchInfo.asm.setStack(patchInfo.stackDepth);
        }
    }
    
    static class PatchInfo
    {
        final ClassFileAssembler asm;
        final short instrBCI;
        final short patchBCI;
        final int stackDepth;
        
        PatchInfo(final ClassFileAssembler asm, final short instrBCI, final short patchBCI, final int stackDepth) {
            this.asm = asm;
            this.instrBCI = instrBCI;
            this.patchBCI = patchBCI;
            this.stackDepth = stackDepth;
        }
    }
}
