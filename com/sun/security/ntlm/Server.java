package com.sun.security.ntlm;

import java.util.Locale;
import java.util.Arrays;

public abstract class Server extends NTLM
{
    private final String domain;
    private final boolean allVersion;
    
    public Server(final String s, final String domain) throws NTLMException {
        super(s);
        if (domain == null) {
            throw new NTLMException(6, "domain cannot be null");
        }
        this.allVersion = (s == null);
        this.domain = domain;
        this.debug("NTLM Server: (t,version) = (%s,%s)\n", new Object[] { domain, s });
    }
    
    public byte[] type2(final byte[] array, final byte[] array2) throws NTLMException {
        if (array2 == null) {
            throw new NTLMException(6, "nonce cannot be null");
        }
        this.debug("NTLM Server: Type 1 received\n", new Object[0]);
        if (array != null) {
            this.debug(array);
        }
        final Writer writer = new Writer(2, 32);
        final int n = 590341;
        writer.writeSecurityBuffer(12, this.domain, true);
        writer.writeInt(20, n);
        writer.writeBytes(24, array2);
        this.debug("NTLM Server: Type 2 created\n", new Object[0]);
        this.debug(writer.getBytes());
        return writer.getBytes();
    }
    
    public String[] verify(final byte[] array, final byte[] array2) throws NTLMException {
        if (array == null || array2 == null) {
            throw new NTLMException(6, "type1 or nonce cannot be null");
        }
        this.debug("NTLM Server: Type 3 received\n", new Object[0]);
        if (array != null) {
            this.debug(array);
        }
        final Reader reader = new Reader(array);
        final String securityBuffer = reader.readSecurityBuffer(36, true);
        final String securityBuffer2 = reader.readSecurityBuffer(44, true);
        final String securityBuffer3 = reader.readSecurityBuffer(28, true);
        boolean b = false;
        final char[] password = this.getPassword(securityBuffer3, securityBuffer);
        if (password == null) {
            throw new NTLMException(3, "Unknown user");
        }
        final byte[] securityBuffer4 = reader.readSecurityBuffer(12);
        final byte[] securityBuffer5 = reader.readSecurityBuffer(20);
        if (!b && (this.allVersion || this.v == Version.NTLM)) {
            if (securityBuffer4.length > 0 && Arrays.equals(this.calcResponse(this.calcLMHash(NTLM.getP1(password)), array2), securityBuffer4)) {
                b = true;
            }
            if (securityBuffer5.length > 0 && Arrays.equals(this.calcResponse(this.calcNTHash(NTLM.getP2(password)), array2), securityBuffer5)) {
                b = true;
            }
            this.debug("NTLM Server: verify using NTLM: " + b + "\n", new Object[0]);
        }
        if (!b && (this.allVersion || this.v == Version.NTLM2)) {
            if (Arrays.equals(securityBuffer5, this.ntlm2NTLM(this.calcNTHash(NTLM.getP2(password)), Arrays.copyOf(securityBuffer4, 8), array2))) {
                b = true;
            }
            this.debug("NTLM Server: verify using NTLM2: " + b + "\n", new Object[0]);
        }
        if (!b && (this.allVersion || this.v == Version.NTLMv2)) {
            final byte[] calcNTHash = this.calcNTHash(NTLM.getP2(password));
            if (securityBuffer4.length > 0 && Arrays.equals(this.calcV2(calcNTHash, securityBuffer.toUpperCase(Locale.US) + securityBuffer3, Arrays.copyOfRange(securityBuffer4, 16, securityBuffer4.length), array2), securityBuffer4)) {
                b = true;
            }
            if (securityBuffer5.length > 0 && Arrays.equals(this.calcV2(calcNTHash, securityBuffer.toUpperCase(Locale.US) + securityBuffer3, Arrays.copyOfRange(securityBuffer5, 16, securityBuffer5.length), array2), securityBuffer5)) {
                b = true;
            }
            this.debug("NTLM Server: verify using NTLMv2: " + b + "\n", new Object[0]);
        }
        if (!b) {
            throw new NTLMException(4, "None of LM and NTLM verified");
        }
        return new String[] { securityBuffer, securityBuffer2, securityBuffer3 };
    }
    
    public abstract char[] getPassword(final String p0, final String p1);
}
