package org.tukaani.xz.check;

public class CRC32 extends Check
{
    private final java.util.zip.CRC32 state;
    
    public CRC32() {
        this.state = new java.util.zip.CRC32();
        this.size = 4;
        this.name = "CRC32";
    }
    
    @Override
    public void update(final byte[] array, final int n, final int n2) {
        this.state.update(array, n, n2);
    }
    
    @Override
    public byte[] finish() {
        final long value = this.state.getValue();
        final byte[] array = { (byte)value, (byte)(value >>> 8), (byte)(value >>> 16), (byte)(value >>> 24) };
        this.state.reset();
        return array;
    }
}
