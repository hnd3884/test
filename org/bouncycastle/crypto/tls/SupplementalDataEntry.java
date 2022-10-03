package org.bouncycastle.crypto.tls;

public class SupplementalDataEntry
{
    protected int dataType;
    protected byte[] data;
    
    public SupplementalDataEntry(final int dataType, final byte[] data) {
        this.dataType = dataType;
        this.data = data;
    }
    
    public int getDataType() {
        return this.dataType;
    }
    
    public byte[] getData() {
        return this.data;
    }
}
