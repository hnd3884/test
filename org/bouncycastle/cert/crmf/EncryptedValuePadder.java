package org.bouncycastle.cert.crmf;

public interface EncryptedValuePadder
{
    byte[] getPaddedData(final byte[] p0);
    
    byte[] getUnpaddedData(final byte[] p0);
}
