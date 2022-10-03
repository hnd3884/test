package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.x509.DigestInfo;

public class MessageImprint
{
    private final DigestInfo messageImprint;
    
    public MessageImprint(final DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }
    
    public DigestInfo toASN1Structure() {
        return this.messageImprint;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof MessageImprint && this.messageImprint.equals((Object)((MessageImprint)o).messageImprint));
    }
    
    @Override
    public int hashCode() {
        return this.messageImprint.hashCode();
    }
}
