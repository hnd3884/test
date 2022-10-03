package cryptix.jce.provider.mac;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.MacSpi;

class HMAC extends MacSpi
{
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private final MessageDigest md;
    private final int mdBlockSize;
    private final int mdLen;
    private final byte[] iv_i;
    private final byte[] iv_o;
    
    protected final int engineGetMacLength() {
        return this.mdLen;
    }
    
    protected final void engineInit(final Key key, final AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("HMAC doesn't take AlgorithmParameters.");
        }
        if (!key.getFormat().equals("RAW")) {
            throw new InvalidKeyException("HMAC accepts keys in 'RAW' format only.");
        }
        byte[] keyBytes = key.getEncoded();
        if (keyBytes.length > this.mdBlockSize) {
            this.md.reset();
            keyBytes = this.md.digest(keyBytes);
        }
        for (int i = 0; i < this.iv_i.length; ++i) {
            this.iv_i[i] = (this.iv_o[i] = 0);
        }
        System.arraycopy(keyBytes, 0, this.iv_i, 0, keyBytes.length);
        for (int i = 0; i < this.iv_i.length; ++i) {
            final byte[] iv_i = this.iv_i;
            final int n = i;
            iv_i[n] ^= 0x36;
        }
        System.arraycopy(keyBytes, 0, this.iv_o, 0, keyBytes.length);
        for (int i = 0; i < this.iv_i.length; ++i) {
            final byte[] iv_o = this.iv_o;
            final int n2 = i;
            iv_o[n2] ^= 0x5C;
        }
        this.engineReset();
    }
    
    protected final void engineUpdate(final byte input) {
        this.md.update(input);
    }
    
    protected final void engineUpdate(final byte[] input, final int offset, final int len) {
        this.md.update(input, offset, len);
    }
    
    protected final byte[] engineDoFinal() {
        final byte[] tmp = this.md.digest();
        this.md.reset();
        this.md.update(this.iv_o);
        this.md.update(tmp);
        final byte[] output = this.md.digest();
        this.engineReset();
        return output;
    }
    
    protected final void engineReset() {
        this.md.reset();
        this.md.update(this.iv_i);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final MessageDigest md = (MessageDigest)this.md.clone();
        return new HMAC(md, this.mdBlockSize, this.mdLen, this.iv_i.clone(), this.iv_o.clone());
    }
    
    protected HMAC(final String mdName, final int mdBlockSize, final int mdLen) {
        try {
            this.md = MessageDigest.getInstance(mdName);
            this.mdBlockSize = mdBlockSize;
            this.mdLen = mdLen;
            this.iv_i = new byte[mdBlockSize];
            this.iv_o = new byte[mdBlockSize];
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Underlying MesageDigest not found: " + mdName);
        }
    }
    
    private HMAC(final MessageDigest md, final int mdBlockSize, final int mdLen, final byte[] iv_i, final byte[] iv_o) {
        this.md = md;
        this.mdBlockSize = mdBlockSize;
        this.mdLen = mdLen;
        this.iv_i = new byte[mdBlockSize];
        this.iv_o = new byte[mdBlockSize];
    }
}
