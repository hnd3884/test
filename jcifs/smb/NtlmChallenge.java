package jcifs.smb;

import jcifs.util.Hexdump;
import jcifs.UniAddress;
import java.io.Serializable;

public final class NtlmChallenge implements Serializable
{
    public byte[] challenge;
    public UniAddress dc;
    
    NtlmChallenge(final byte[] challenge, final UniAddress dc) {
        this.challenge = challenge;
        this.dc = dc;
    }
    
    public String toString() {
        return "NtlmChallenge[challenge=0x" + Hexdump.toHexString(this.challenge, 0, this.challenge.length * 2) + ",dc=" + this.dc.toString() + "]";
    }
}
