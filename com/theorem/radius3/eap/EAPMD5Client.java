package com.theorem.radius3.eap;

import com.theorem.radius3.PacketType;
import com.theorem.radius3.EAPPacket;
import java.net.InetAddress;
import com.theorem.radius3.EAPException;
import com.theorem.radius3.radutil.MD5Digest;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.RADIUSClient;
import java.security.MessageDigest;

public final class EAPMD5Client extends EAPClient
{
    protected MessageDigest a;
    
    public EAPMD5Client(final RADIUSClient radiusClient, final AttributeList list) throws EAPException {
        super(radiusClient, list);
        this.a = MD5Digest.get();
    }
    
    public EAPMD5Client(final byte[] array, final InetAddress inetAddress, final int n, final String s, final int n2, final InetAddress inetAddress2, final String s2) throws EAPException {
        super(array, inetAddress, n, s, n2, inetAddress2, s2);
        this.a = MD5Digest.get();
    }
    
    public EAPMD5Client(final byte[] array, final RADIUSClient radiusClient, final InetAddress inetAddress, final String s) throws EAPException {
        super(array, radiusClient, inetAddress, s);
        this.a = MD5Digest.get();
    }
    
    public final boolean authenticate(final byte[] array) throws EAPException {
        return this.a(array);
    }
    
    public final boolean authenticate(final byte[] array, final byte[] array2) throws EAPException {
        this.reset(array);
        return this.a(array2);
    }
    
    public final void setAttributes(final AttributeList list) {
        super.addAttributes(list);
    }
    
    private final boolean a(final byte[] array) throws EAPException {
        final int send = this.send(new EAPPacket().createIdentityResponse(this.createPacketId(), super.a));
        if (send != 11) {
            throw new EAPException("Unexpected packet type of " + new PacketType().getName(send) + " was returned from the server, error = " + super.radClient.getErrorString());
        }
        final EAPPacket eapPacket = this.getEAPPacket();
        if (eapPacket.getType() != 4) {
            throw new EAPException("Unexpected EAP type of " + eapPacket.getTypeName() + " was returned from the server.");
        }
        final byte[] data = eapPacket.getData();
        final int n = data[0] & 0xFF;
        final byte[] array2 = new byte[n];
        System.arraycopy(data, 1, array2, 0, n);
        final int send2 = this.send(new EAPPacket().createMD5Response(eapPacket.getPacketIdentifier(), array2, array));
        if (send2 != 2 && send2 != 3) {
            throw new EAPException("Unexpected packet type of " + new PacketType().getName(send2) + " was returned from the server, error = " + super.radClient.getErrorString());
        }
        final EAPPacket eapPacket2 = this.getEAPPacket();
        final int code = eapPacket2.getCode();
        if (code != 3 && code != 4) {
            throw new EAPException("Unexpected EAP type of " + eapPacket2.getTypeName() + " was returned from the server - expecting SUCCESS or FAILURE.");
        }
        return code == 3;
    }
}
