package org.bouncycastle.cms;

import org.bouncycastle.util.Arrays;

public class KEKRecipientId extends RecipientId
{
    private byte[] keyIdentifier;
    
    public KEKRecipientId(final byte[] keyIdentifier) {
        super(1);
        this.keyIdentifier = keyIdentifier;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.keyIdentifier);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof KEKRecipientId && Arrays.areEqual(this.keyIdentifier, ((KEKRecipientId)o).keyIdentifier);
    }
    
    public byte[] getKeyIdentifier() {
        return Arrays.clone(this.keyIdentifier);
    }
    
    @Override
    public Object clone() {
        return new KEKRecipientId(this.keyIdentifier);
    }
    
    public boolean match(final Object o) {
        if (o instanceof byte[]) {
            return Arrays.areEqual(this.keyIdentifier, (byte[])o);
        }
        return o instanceof KEKRecipientInformation && ((KEKRecipientInformation)o).getRID().equals(this);
    }
}
