package org.tukaani.xz.check;

public class None extends Check
{
    public None() {
        this.size = 0;
        this.name = "None";
    }
    
    @Override
    public void update(final byte[] array, final int n, final int n2) {
    }
    
    @Override
    public byte[] finish() {
        return new byte[0];
    }
}
