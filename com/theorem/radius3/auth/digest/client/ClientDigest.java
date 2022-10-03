package com.theorem.radius3.auth.digest.client;

import java.util.Arrays;
import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.RADIUSException;
import java.net.SocketException;
import com.theorem.radius3.RADIUSClient;
import com.theorem.radius3.AttributeList;

public class ClientDigest
{
    private int a;
    private AttributeList b;
    private RADIUSClient c;
    private String[] d;
    private String[] e;
    private int f;
    private String g;
    
    public ClientDigest(final RADIUSClient c, final AttributeList b) {
        this.a = 1;
        this.d = new String[10];
        this.e = new String[10];
        this.c = c;
        this.b = b;
        this.a();
    }
    
    public final void reset() throws SocketException {
        this.a();
        this.c.reset();
    }
    
    public final void set(final int n, final String s) {
        if (n < 1 || n > 10) {
            return;
        }
        this.d[n - 1] = s;
    }
    
    public final void setRealm(final String s) {
        this.d[0] = s;
    }
    
    public final void setNonce(final String s) {
        this.d[1] = s;
    }
    
    public final void setMethod(final String s) {
        this.d[2] = s;
    }
    
    public final void setURI(final String s) {
        this.d[3] = s;
    }
    
    public final void setDigestQop(final String s, final String s2, final String s3) {
        this.d[4] = s;
        this.d[7] = s2;
        this.d[8] = s3;
    }
    
    public final void setAlgorithm(final String s) {
        this.d[5] = s;
    }
    
    public final void setBodyDigest(final String s) {
        this.d[6] = s;
    }
    
    public final void setUserName(final String s) {
        this.d[9] = s;
    }
    
    public final void setResponse(final String g) {
        this.g = g;
    }
    
    public final int authenticate() throws RADIUSException, ClientSendException, ClientReceiveException {
        final AttributeList list = new AttributeList(this.b);
        list.mergeAttributes(this.b());
        this.f = this.c.authenticate(list);
        return this.c.getPacketType();
    }
    
    private final void a() {
        Arrays.fill(this.d, null);
        Arrays.fill(this.e, null);
    }
    
    private final AttributeList b() throws RADIUSException {
        final AttributeList list = new AttributeList();
        for (int i = 0; i < 10; ++i) {
            if (this.d[i] != null) {
                list.addAttribute(i + 1, this.d[i]);
            }
        }
        final byte[] radiusAttributeBlock = list.createRadiusAttributeBlock();
        list.clearAttributes();
        final int n = radiusAttributeBlock.length / 253;
        int j = 0;
        if (n > 0) {
            final byte[] array = new byte[253];
            while (j < n * 253) {
                System.arraycopy(radiusAttributeBlock, j, array, 0, 253);
                list.addAttribute(207, array);
                j += 253;
            }
        }
        final int n2 = radiusAttributeBlock.length % 253;
        if (n2 > 0) {
            final byte[] array2 = new byte[n2];
            System.arraycopy(radiusAttributeBlock, j, array2, 0, n2);
            list.addAttribute(207, array2);
        }
        if (this.g == null) {
            throw new RADIUSException("Missing the required Digest-Response attribute.");
        }
        list.addAttribute(206, this.g);
        return list;
    }
}
