package sun.misc;

public class CRC16
{
    public int value;
    
    public CRC16() {
        this.value = 0;
    }
    
    public void update(final byte b) {
        int n = b;
        for (int i = 7; i >= 0; --i) {
            n <<= 1;
            final int n2 = n >>> 8 & 0x1;
            if ((this.value & 0x8000) != 0x0) {
                this.value = ((this.value << 1) + n2 ^ 0x1021);
            }
            else {
                this.value = (this.value << 1) + n2;
            }
        }
        this.value &= 0xFFFF;
    }
    
    public void reset() {
        this.value = 0;
    }
}
