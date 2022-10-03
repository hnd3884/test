package cryptix.jce.provider.cipher;

import cryptix.jce.provider.key.RawSecretKey;
import java.security.InvalidKeyException;
import java.security.Key;

public final class TripleDES extends BlockCipher
{
    private static final int BLOCK_SIZE = 8;
    private static final int KEY_LENGTH = 24;
    private static final int ALT_KEY_LENGTH = 21;
    private static final int DES_KEY_LENGTH = 8;
    private DES des1;
    private DES des2;
    private DES des3;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("Null user key");
        }
        int len = 0;
        if (userkey.length == 24) {
            len = 8;
        }
        else {
            if (userkey.length != 21) {
                throw new InvalidKeyException("Invalid user key length");
            }
            len = 7;
        }
        final byte[] k = new byte[len];
        System.arraycopy(userkey, 0, k, 0, len);
        RawSecretKey sk = new RawSecretKey("DES", k);
        this.des1.coreInit(sk, decrypt);
        System.arraycopy(userkey, len, k, 0, len);
        sk = new RawSecretKey("DES", k);
        this.des2.coreInit(sk, !decrypt);
        System.arraycopy(userkey, len + len, k, 0, len);
        sk = new RawSecretKey("DES", k);
        this.des3.coreInit(sk, decrypt);
        if (decrypt) {
            final DES des = this.des1;
            this.des1 = this.des3;
            this.des3 = des;
        }
    }
    
    protected void coreCrypt(final byte[] in, final int inOffset, final byte[] out, final int outOffset) {
        this.des1.coreCrypt(in, inOffset, out, outOffset);
        this.des2.coreCrypt(out, outOffset, out, outOffset);
        this.des3.coreCrypt(out, outOffset, out, outOffset);
    }
    
    public TripleDES() {
        super(8);
        this.des1 = new DES();
        this.des2 = new DES();
        this.des3 = new DES();
    }
}
