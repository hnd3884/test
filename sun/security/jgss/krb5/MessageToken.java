package sun.security.jgss.krb5;

import java.io.OutputStream;
import sun.security.krb5.EncryptionKey;
import java.security.MessageDigest;
import java.io.IOException;
import sun.security.jgss.GSSToken;
import org.ietf.jgss.GSSException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;

abstract class MessageToken extends Krb5Token
{
    private static final int TOKEN_NO_CKSUM_SIZE = 16;
    private static final int FILLER = 65535;
    static final int SGN_ALG_DES_MAC_MD5 = 0;
    static final int SGN_ALG_DES_MAC = 512;
    static final int SGN_ALG_HMAC_SHA1_DES3_KD = 1024;
    static final int SEAL_ALG_NONE = 65535;
    static final int SEAL_ALG_DES = 0;
    static final int SEAL_ALG_DES3_KD = 512;
    static final int SEAL_ALG_ARCFOUR_HMAC = 4096;
    static final int SGN_ALG_HMAC_MD5_ARCFOUR = 4352;
    private static final int TOKEN_ID_POS = 0;
    private static final int SIGN_ALG_POS = 2;
    private static final int SEAL_ALG_POS = 4;
    private int seqNumber;
    private boolean confState;
    private boolean initiator;
    private int tokenId;
    private GSSHeader gssHeader;
    private MessageTokenHeader tokenHeader;
    private byte[] checksum;
    private byte[] encSeqNumber;
    private byte[] seqNumberData;
    CipherHelper cipherHelper;
    
    MessageToken(final int n, final Krb5Context krb5Context, final byte[] array, final int n2, final int n3, final MessageProp messageProp) throws GSSException {
        this(n, krb5Context, new ByteArrayInputStream(array, n2, n3), messageProp);
    }
    
    MessageToken(final int n, final Krb5Context krb5Context, final InputStream inputStream, final MessageProp messageProp) throws GSSException {
        this.confState = true;
        this.initiator = true;
        this.tokenId = 0;
        this.gssHeader = null;
        this.tokenHeader = null;
        this.checksum = null;
        this.encSeqNumber = null;
        this.seqNumberData = null;
        this.cipherHelper = null;
        this.init(n, krb5Context);
        try {
            this.gssHeader = new GSSHeader(inputStream);
            if (!this.gssHeader.getOid().equals((Object)MessageToken.OID)) {
                throw new GSSException(10, -1, Krb5Token.getTokenName(n));
            }
            if (!this.confState) {
                messageProp.setPrivacy(false);
            }
            this.tokenHeader = new MessageTokenHeader(inputStream, messageProp);
            GSSToken.readFully(inputStream, this.encSeqNumber = new byte[8]);
            GSSToken.readFully(inputStream, this.checksum = new byte[this.cipherHelper.getChecksumLength()]);
        }
        catch (final IOException ex) {
            throw new GSSException(10, -1, Krb5Token.getTokenName(n) + ":" + ex.getMessage());
        }
    }
    
    public final GSSHeader getGSSHeader() {
        return this.gssHeader;
    }
    
    public final int getTokenId() {
        return this.tokenId;
    }
    
    public final byte[] getEncSeqNumber() {
        return this.encSeqNumber;
    }
    
    public final byte[] getChecksum() {
        return this.checksum;
    }
    
    public final boolean getConfState() {
        return this.confState;
    }
    
    public void genSignAndSeqNumber(final MessageProp messageProp, final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3) throws GSSException {
        int qop = messageProp.getQOP();
        if (qop != 0) {
            qop = 0;
            messageProp.setQOP(qop);
        }
        if (!this.confState) {
            messageProp.setPrivacy(false);
        }
        this.tokenHeader = new MessageTokenHeader(this.tokenId, messageProp.getPrivacy(), qop);
        this.checksum = this.getChecksum(array, array2, n, n2, array3);
        this.seqNumberData = new byte[8];
        if (this.cipherHelper.isArcFour()) {
            GSSToken.writeBigEndian(this.seqNumber, this.seqNumberData);
        }
        else {
            GSSToken.writeLittleEndian(this.seqNumber, this.seqNumberData);
        }
        if (!this.initiator) {
            this.seqNumberData[4] = -1;
            this.seqNumberData[5] = -1;
            this.seqNumberData[6] = -1;
            this.seqNumberData[7] = -1;
        }
        this.encSeqNumber = this.cipherHelper.encryptSeq(this.checksum, this.seqNumberData, 0, 8);
    }
    
    public final boolean verifySignAndSeqNumber(final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3) throws GSSException {
        if (MessageDigest.isEqual(this.checksum, this.getChecksum(array, array2, n, n2, array3))) {
            this.seqNumberData = this.cipherHelper.decryptSeq(this.checksum, this.encSeqNumber, 0, 8);
            byte b = 0;
            if (this.initiator) {
                b = -1;
            }
            if (this.seqNumberData[4] == b && this.seqNumberData[5] == b && this.seqNumberData[6] == b && this.seqNumberData[7] == b) {
                return true;
            }
        }
        return false;
    }
    
    public final int getSequenceNumber() {
        int n;
        if (this.cipherHelper.isArcFour()) {
            n = GSSToken.readBigEndian(this.seqNumberData, 0, 4);
        }
        else {
            n = GSSToken.readLittleEndian(this.seqNumberData, 0, 4);
        }
        return n;
    }
    
    private byte[] getChecksum(final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3) throws GSSException {
        byte[] bytes;
        final byte[] array4 = bytes = this.tokenHeader.getBytes();
        if (array != null) {
            bytes = new byte[array4.length + array.length];
            System.arraycopy(array4, 0, bytes, 0, array4.length);
            System.arraycopy(array, 0, bytes, array4.length, array.length);
        }
        return this.cipherHelper.calculateChecksum(this.tokenHeader.getSignAlg(), bytes, array3, array2, n, n2, this.tokenId);
    }
    
    MessageToken(final int n, final Krb5Context krb5Context) throws GSSException {
        this.confState = true;
        this.initiator = true;
        this.tokenId = 0;
        this.gssHeader = null;
        this.tokenHeader = null;
        this.checksum = null;
        this.encSeqNumber = null;
        this.seqNumberData = null;
        this.cipherHelper = null;
        this.init(n, krb5Context);
        this.seqNumber = krb5Context.incrementMySequenceNumber();
    }
    
    private void init(final int tokenId, final Krb5Context krb5Context) throws GSSException {
        this.tokenId = tokenId;
        this.confState = krb5Context.getConfState();
        this.initiator = krb5Context.isInitiator();
        this.cipherHelper = krb5Context.getCipherHelper(null);
    }
    
    public void encode(final OutputStream outputStream) throws IOException, GSSException {
        (this.gssHeader = new GSSHeader(MessageToken.OID, this.getKrb5TokenSize())).encode(outputStream);
        this.tokenHeader.encode(outputStream);
        outputStream.write(this.encSeqNumber);
        outputStream.write(this.checksum);
    }
    
    protected int getKrb5TokenSize() throws GSSException {
        return this.getTokenSize();
    }
    
    protected final int getTokenSize() throws GSSException {
        return 16 + this.cipherHelper.getChecksumLength();
    }
    
    protected static final int getTokenSize(final CipherHelper cipherHelper) throws GSSException {
        return 16 + cipherHelper.getChecksumLength();
    }
    
    protected abstract int getSealAlg(final boolean p0, final int p1) throws GSSException;
    
    protected int getSgnAlg(final int n) throws GSSException {
        return this.cipherHelper.getSgnAlg();
    }
    
    class MessageTokenHeader
    {
        private int tokenId;
        private int signAlg;
        private int sealAlg;
        private byte[] bytes;
        
        public MessageTokenHeader(final int tokenId, final boolean b, final int n) throws GSSException {
            this.bytes = new byte[8];
            this.tokenId = tokenId;
            this.signAlg = MessageToken.this.getSgnAlg(n);
            this.sealAlg = MessageToken.this.getSealAlg(b, n);
            this.bytes[0] = (byte)(tokenId >>> 8);
            this.bytes[1] = (byte)tokenId;
            this.bytes[2] = (byte)(this.signAlg >>> 8);
            this.bytes[3] = (byte)this.signAlg;
            this.bytes[4] = (byte)(this.sealAlg >>> 8);
            this.bytes[5] = (byte)this.sealAlg;
            this.bytes[6] = -1;
            this.bytes[7] = -1;
        }
        
        public MessageTokenHeader(final InputStream inputStream, final MessageProp messageProp) throws IOException {
            GSSToken.readFully(inputStream, this.bytes = new byte[8]);
            this.tokenId = GSSToken.readInt(this.bytes, 0);
            this.signAlg = GSSToken.readInt(this.bytes, 2);
            this.sealAlg = GSSToken.readInt(this.bytes, 4);
            GSSToken.readInt(this.bytes, 6);
            switch (this.sealAlg) {
                case 0:
                case 512:
                case 4096: {
                    messageProp.setPrivacy(true);
                    break;
                }
                default: {
                    messageProp.setPrivacy(false);
                    break;
                }
            }
            messageProp.setQOP(0);
        }
        
        public final void encode(final OutputStream outputStream) throws IOException {
            outputStream.write(this.bytes);
        }
        
        public final int getTokenId() {
            return this.tokenId;
        }
        
        public final int getSignAlg() {
            return this.signAlg;
        }
        
        public final int getSealAlg() {
            return this.sealAlg;
        }
        
        public final byte[] getBytes() {
            return this.bytes;
        }
    }
}
