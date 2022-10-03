package com.sun.media.sound;

import javax.sound.midi.Patch;

public final class ModelPatch extends Patch
{
    private boolean percussion;
    
    public ModelPatch(final int n, final int n2) {
        super(n, n2);
        this.percussion = false;
    }
    
    public ModelPatch(final int n, final int n2, final boolean percussion) {
        super(n, n2);
        this.percussion = false;
        this.percussion = percussion;
    }
    
    public boolean isPercussion() {
        return this.percussion;
    }
}
