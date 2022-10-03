package com.sun.security.ntlm;

import java.util.Arrays;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

public final class Client extends NTLM
{
    private final String hostname;
    private final String username;
    private String domain;
    private byte[] pw1;
    private byte[] pw2;
    
    public Client(final String s, final String hostname, final String username, final String s2, final char[] array) throws NTLMException {
        super(s);
        if (username == null || array == null) {
            throw new NTLMException(6, "username/password cannot be null");
        }
        this.hostname = hostname;
        this.username = username;
        this.domain = ((s2 == null) ? "" : s2);
        this.pw1 = NTLM.getP1(array);
        this.pw2 = NTLM.getP2(array);
        this.debug("NTLM Client: (h,u,t,version(v)) = (%s,%s,%s,%s(%s))\n", new Object[] { hostname, username, s2, s, this.v.toString() });
    }
    
    public byte[] type1() {
        final Writer writer = new Writer(1, 32);
        int n = 33287;
        if (this.v != Version.NTLM) {
            n |= 0x80000;
        }
        writer.writeInt(12, n);
        this.debug("NTLM Client: Type 1 created\n", new Object[0]);
        this.debug(writer.getBytes());
        return writer.getBytes();
    }
    
    public byte[] type3(final byte[] array, final byte[] array2) throws NTLMException {
        if (array == null || (this.v != Version.NTLM && array2 == null)) {
            throw new NTLMException(6, "type2 and nonce cannot be null");
        }
        this.debug("NTLM Client: Type 2 received\n", new Object[0]);
        this.debug(array);
        final Reader reader = new Reader(array);
        final byte[] bytes = reader.readBytes(24, 8);
        final int int1 = reader.readInt(20);
        final boolean b = (int1 & 0x1) == 0x1;
        final int n = 0x88200 | (int1 & 0x3);
        final Writer writer = new Writer(3, 64);
        byte[] array3 = null;
        byte[] array4 = null;
        writer.writeSecurityBuffer(28, this.domain, b);
        writer.writeSecurityBuffer(36, this.username, b);
        writer.writeSecurityBuffer(44, this.hostname, b);
        if (this.v == Version.NTLM) {
            final byte[] calcLMHash = this.calcLMHash(this.pw1);
            final byte[] calcNTHash = this.calcNTHash(this.pw2);
            if (this.writeLM) {
                array3 = this.calcResponse(calcLMHash, bytes);
            }
            if (this.writeNTLM) {
                array4 = this.calcResponse(calcNTHash, bytes);
            }
        }
        else if (this.v == Version.NTLM2) {
            final byte[] calcNTHash2 = this.calcNTHash(this.pw2);
            array3 = NTLM.ntlm2LM(array2);
            array4 = this.ntlm2NTLM(calcNTHash2, array2, bytes);
        }
        else {
            final byte[] calcNTHash3 = this.calcNTHash(this.pw2);
            if (this.writeLM) {
                array3 = this.calcV2(calcNTHash3, this.username.toUpperCase(Locale.US) + this.domain, array2, bytes);
            }
            if (this.writeNTLM) {
                final byte[] array5 = ((int1 & 0x800000) != 0x0) ? reader.readSecurityBuffer(40) : new byte[0];
                final byte[] array6 = new byte[32 + array5.length];
                System.arraycopy(new byte[] { 1, 1, 0, 0, 0, 0, 0, 0 }, 0, array6, 0, 8);
                final byte[] byteArray = BigInteger.valueOf(new Date().getTime()).add(new BigInteger("11644473600000")).multiply(BigInteger.valueOf(10000L)).toByteArray();
                for (int i = 0; i < byteArray.length; ++i) {
                    array6[8 + byteArray.length - i - 1] = byteArray[i];
                }
                System.arraycopy(array2, 0, array6, 16, 8);
                System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, array6, 24, 4);
                System.arraycopy(array5, 0, array6, 28, array5.length);
                System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, array6, 28 + array5.length, 4);
                array4 = this.calcV2(calcNTHash3, this.username.toUpperCase(Locale.US) + this.domain, array6, bytes);
            }
        }
        writer.writeSecurityBuffer(12, array3);
        writer.writeSecurityBuffer(20, array4);
        writer.writeSecurityBuffer(52, new byte[0]);
        writer.writeInt(60, n);
        this.debug("NTLM Client: Type 3 created\n", new Object[0]);
        this.debug(writer.getBytes());
        return writer.getBytes();
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void dispose() {
        Arrays.fill(this.pw1, (byte)0);
        Arrays.fill(this.pw2, (byte)0);
    }
}
