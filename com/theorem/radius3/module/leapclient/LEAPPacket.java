package com.theorem.radius3.module.leapclient;

import com.theorem.radius3.EAPException;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.radutil.RadRand;
import com.theorem.radius3.EAPPacket;

public class LEAPPacket extends EAPPacket
{
    private byte[] a;
    private RadRand b;
    byte[] c;
    
    public LEAPPacket(final AttributeList list) throws EAPException {
        super(list);
        this.b = new RadRand();
        this.getLEAPData();
    }
    
    public LEAPPacket(final int n, final int n2, final byte[] array) {
        this.b = new RadRand();
        this.setLEAPData(n, n2, array);
    }
    
    public LEAPPacket() {
        this.b = new RadRand();
    }
    
    public final byte[] getLEAPData() throws EAPException {
        if (this.a == null) {
            final byte[] data = super.getData();
            if (data.length < 3) {
                return this.a = new byte[0];
            }
            System.arraycopy(data, 3, this.a = new byte[data.length - 3], 0, this.a.length);
        }
        return this.a;
    }
    
    public final byte[] getChallenge() {
        return this.c;
    }
    
    public final void setLEAPData(final int packetIdentifier, final int code, final byte[] array) {
        final byte[] array2 = new byte[3 + array.length];
        array2[0] = 1;
        array2[2] = (byte)array.length;
        System.arraycopy(array, 0, array2, 3, array.length);
        super.setData(17, array2);
        super.setCode(code);
        super.setPacketIdentifier(packetIdentifier);
    }
}
