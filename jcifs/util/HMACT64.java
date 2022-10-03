package jcifs.util;

import java.security.MessageDigest;

public class HMACT64 extends MessageDigest implements Cloneable
{
    private static final int BLOCK_LENGTH = 64;
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private MessageDigest md5;
    private byte[] ipad;
    private byte[] opad;
    
    public HMACT64(final byte[] key) {
        super("HMACT64");
        this.ipad = new byte[64];
        this.opad = new byte[64];
        final int length = Math.min(key.length, 64);
        for (int i = 0; i < length; ++i) {
            this.ipad[i] = (byte)(key[i] ^ 0x36);
            this.opad[i] = (byte)(key[i] ^ 0x5C);
        }
        for (int i = length; i < 64; ++i) {
            this.ipad[i] = 54;
            this.opad[i] = 92;
        }
        try {
            this.md5 = MessageDigest.getInstance("MD5");
        }
        catch (final Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
        this.engineReset();
    }
    
    private HMACT64(final HMACT64 hmac) throws CloneNotSupportedException {
        super("HMACT64");
        this.ipad = new byte[64];
        this.opad = new byte[64];
        this.ipad = hmac.ipad;
        this.opad = hmac.opad;
        this.md5 = (MessageDigest)hmac.md5.clone();
    }
    
    public Object clone() {
        try {
            return new HMACT64(this);
        }
        catch (final CloneNotSupportedException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    protected byte[] engineDigest() {
        final byte[] digest = this.md5.digest();
        this.md5.update(this.opad);
        return this.md5.digest(digest);
    }
    
    protected int engineDigest(final byte[] buf, final int offset, final int len) {
        final byte[] digest = this.md5.digest();
        this.md5.update(this.opad);
        this.md5.update(digest);
        try {
            return this.md5.digest(buf, offset, len);
        }
        catch (final Exception ex) {
            throw new IllegalStateException();
        }
    }
    
    protected int engineGetDigestLength() {
        return this.md5.getDigestLength();
    }
    
    protected void engineReset() {
        this.md5.reset();
        this.md5.update(this.ipad);
    }
    
    protected void engineUpdate(final byte b) {
        this.md5.update(b);
    }
    
    protected void engineUpdate(final byte[] input, final int offset, final int len) {
        this.md5.update(input, offset, len);
    }
}
