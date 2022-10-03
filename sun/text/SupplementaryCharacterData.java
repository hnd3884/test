package sun.text;

public final class SupplementaryCharacterData implements Cloneable
{
    private static final byte IGNORE = -1;
    private int[] dataTable;
    
    public SupplementaryCharacterData(final int[] dataTable) {
        this.dataTable = dataTable;
    }
    
    public int getValue(final int n) {
        assert n >= 65536 && n <= 1114111 : "Invalid code point:" + Integer.toHexString(n);
        int n2 = 0;
        int n3 = this.dataTable.length - 1;
        int n4;
        while (true) {
            n4 = (n2 + n3) / 2;
            final int n5 = this.dataTable[n4] >> 8;
            final int n6 = this.dataTable[n4 + 1] >> 8;
            if (n < n5) {
                n3 = n4;
            }
            else {
                if (n <= n6 - 1) {
                    break;
                }
                n2 = n4;
            }
        }
        final int n7 = this.dataTable[n4] & 0xFF;
        return (n7 == 255) ? -1 : n7;
    }
    
    public int[] getArray() {
        return this.dataTable;
    }
}
