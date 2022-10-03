package sun.security.jgss.krb5;

import java.io.OutputStream;
import sun.security.krb5.EncryptionKey;
import java.security.MessageDigest;
import java.io.IOException;
import java.util.Arrays;
import sun.security.jgss.GSSToken;
import org.ietf.jgss.GSSException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.ietf.jgss.MessageProp;

abstract class MessageToken_v2 extends Krb5Token
{
    protected static final int TOKEN_HEADER_SIZE = 16;
    private static final int TOKEN_ID_POS = 0;
    private static final int TOKEN_FLAG_POS = 2;
    private static final int TOKEN_EC_POS = 4;
    private static final int TOKEN_RRC_POS = 6;
    protected static final int CONFOUNDER_SIZE = 16;
    static final int KG_USAGE_ACCEPTOR_SEAL = 22;
    static final int KG_USAGE_ACCEPTOR_SIGN = 23;
    static final int KG_USAGE_INITIATOR_SEAL = 24;
    static final int KG_USAGE_INITIATOR_SIGN = 25;
    private static final int FLAG_SENDER_IS_ACCEPTOR = 1;
    private static final int FLAG_WRAP_CONFIDENTIAL = 2;
    private static final int FLAG_ACCEPTOR_SUBKEY = 4;
    private static final int FILLER = 255;
    private MessageTokenHeader tokenHeader;
    private int tokenId;
    private int seqNumber;
    protected byte[] tokenData;
    protected int tokenDataLen;
    private int key_usage;
    private int ec;
    private int rrc;
    byte[] checksum;
    private boolean confState;
    private boolean initiator;
    private boolean have_acceptor_subkey;
    CipherHelper cipherHelper;
    
    MessageToken_v2(final int n, final Krb5Context krb5Context, final byte[] array, final int n2, final int n3, final MessageProp messageProp) throws GSSException {
        this(n, krb5Context, new ByteArrayInputStream(array, n2, n3), messageProp);
    }
    
    MessageToken_v2(final int n, final Krb5Context krb5Context, final InputStream inputStream, final MessageProp messageProp) throws GSSException {
        this.tokenHeader = null;
        this.tokenId = 0;
        this.key_usage = 0;
        this.ec = 0;
        this.rrc = 0;
        this.checksum = null;
        this.confState = true;
        this.initiator = true;
        this.have_acceptor_subkey = false;
        this.cipherHelper = null;
        this.init(n, krb5Context);
        try {
            if (!this.confState) {
                messageProp.setPrivacy(false);
            }
            this.tokenHeader = new MessageTokenHeader(inputStream, messageProp, n);
            if (n == 1284) {
                this.key_usage = (this.initiator ? 22 : 24);
            }
            else if (n == 1028) {
                this.key_usage = (this.initiator ? 23 : 25);
            }
            int checksumLength;
            if (n == 1284 && messageProp.getPrivacy()) {
                checksumLength = 32 + this.cipherHelper.getChecksumLength();
            }
            else {
                checksumLength = this.cipherHelper.getChecksumLength();
            }
            if (n == 1028) {
                this.tokenDataLen = checksumLength;
                GSSToken.readFully(inputStream, this.tokenData = new byte[checksumLength]);
            }
            else {
                this.tokenDataLen = inputStream.available();
                if (this.tokenDataLen >= checksumLength) {
                    GSSToken.readFully(inputStream, this.tokenData = new byte[this.tokenDataLen]);
                }
                else {
                    final byte[] array = new byte[checksumLength];
                    GSSToken.readFully(inputStream, array);
                    final int available = inputStream.available();
                    this.tokenDataLen = checksumLength + available;
                    GSSToken.readFully(inputStream, this.tokenData = Arrays.copyOf(array, this.tokenDataLen), checksumLength, available);
                }
            }
            if (n == 1284) {
                this.rotate();
            }
            if (n == 1028 || (n == 1284 && !messageProp.getPrivacy())) {
                final int checksumLength2 = this.cipherHelper.getChecksumLength();
                this.checksum = new byte[checksumLength2];
                System.arraycopy(this.tokenData, this.tokenDataLen - checksumLength2, this.checksum, 0, checksumLength2);
                if (n == 1284 && !messageProp.getPrivacy() && checksumLength2 != this.ec) {
                    throw new GSSException(10, -1, Krb5Token.getTokenName(n) + ":EC incorrect!");
                }
            }
        }
        catch (final IOException ex) {
            throw new GSSException(10, -1, Krb5Token.getTokenName(n) + ":" + ex.getMessage());
        }
    }
    
    public final int getTokenId() {
        return this.tokenId;
    }
    
    public final int getKeyUsage() {
        return this.key_usage;
    }
    
    public final boolean getConfState() {
        return this.confState;
    }
    
    public void genSignAndSeqNumber(final MessageProp messageProp, final byte[] array, final int n, final int n2) throws GSSException {
        if (messageProp.getQOP() != 0) {
            messageProp.setQOP(0);
        }
        if (!this.confState) {
            messageProp.setPrivacy(false);
        }
        this.tokenHeader = new MessageTokenHeader(this.tokenId, messageProp.getPrivacy());
        if (this.tokenId == 1284) {
            this.key_usage = (this.initiator ? 24 : 22);
        }
        else if (this.tokenId == 1028) {
            this.key_usage = (this.initiator ? 25 : 23);
        }
        if (this.tokenId == 1028 || (!messageProp.getPrivacy() && this.tokenId == 1284)) {
            this.checksum = this.getChecksum(array, n, n2);
        }
        if (!messageProp.getPrivacy() && this.tokenId == 1284) {
            final byte[] bytes = this.tokenHeader.getBytes();
            bytes[4] = (byte)(this.checksum.length >>> 8);
            bytes[5] = (byte)this.checksum.length;
        }
    }
    
    public final boolean verifySign(final byte[] array, final int n, final int n2) throws GSSException {
        return MessageDigest.isEqual(this.checksum, this.getChecksum(array, n, n2));
    }
    
    private void rotate() {
        if (this.rrc % this.tokenDataLen != 0) {
            this.rrc %= this.tokenDataLen;
            final byte[] tokenData = new byte[this.tokenDataLen];
            System.arraycopy(this.tokenData, this.rrc, tokenData, 0, this.tokenDataLen - this.rrc);
            System.arraycopy(this.tokenData, 0, tokenData, this.tokenDataLen - this.rrc, this.rrc);
            this.tokenData = tokenData;
        }
    }
    
    public final int getSequenceNumber() {
        return this.seqNumber;
    }
    
    byte[] getChecksum(final byte[] array, final int n, final int n2) throws GSSException {
        final byte[] bytes = this.tokenHeader.getBytes();
        if ((bytes[2] & 0x2) == 0x0 && this.tokenId == 1284) {
            bytes[5] = (bytes[4] = 0);
            bytes[7] = (bytes[6] = 0);
        }
        return this.cipherHelper.calculateChecksum(bytes, array, n, n2, this.key_usage);
    }
    
    MessageToken_v2(final int n, final Krb5Context krb5Context) throws GSSException {
        this.tokenHeader = null;
        this.tokenId = 0;
        this.key_usage = 0;
        this.ec = 0;
        this.rrc = 0;
        this.checksum = null;
        this.confState = true;
        this.initiator = true;
        this.have_acceptor_subkey = false;
        this.cipherHelper = null;
        this.init(n, krb5Context);
        this.seqNumber = krb5Context.incrementMySequenceNumber();
    }
    
    private void init(final int tokenId, final Krb5Context krb5Context) throws GSSException {
        this.tokenId = tokenId;
        this.confState = krb5Context.getConfState();
        this.initiator = krb5Context.isInitiator();
        this.have_acceptor_subkey = (krb5Context.getKeySrc() == 2);
        this.cipherHelper = krb5Context.getCipherHelper(null);
    }
    
    protected void encodeHeader(final OutputStream outputStream) throws IOException {
        this.tokenHeader.encode(outputStream);
    }
    
    public abstract void encode(final OutputStream p0) throws IOException;
    
    protected final byte[] getTokenHeader() {
        return this.tokenHeader.getBytes();
    }
    
    class MessageTokenHeader
    {
        private int tokenId;
        private byte[] bytes;
        
        public MessageTokenHeader(final int tokenId, final boolean b) throws GSSException {
            this.bytes = new byte[16];
            this.tokenId = tokenId;
            this.bytes[0] = (byte)(tokenId >>> 8);
            this.bytes[1] = (byte)tokenId;
            this.bytes[2] = (byte)((MessageToken_v2.this.initiator ? 0 : 1) | ((b && tokenId != 1028) ? 2 : 0) | (MessageToken_v2.this.have_acceptor_subkey ? 4 : 0));
            this.bytes[3] = -1;
            if (tokenId == 1284) {
                this.bytes[4] = 0;
                this.bytes[5] = 0;
                this.bytes[6] = 0;
                this.bytes[7] = 0;
            }
            else if (tokenId == 1028) {
                for (int i = 4; i < 8; ++i) {
                    this.bytes[i] = -1;
                }
            }
            GSSToken.writeBigEndian(MessageToken_v2.this.seqNumber, this.bytes, 12);
        }
        
        public MessageTokenHeader(final InputStream inputStream, final MessageProp messageProp, final int n) throws IOException, GSSException {
            GSSToken.readFully(inputStream, this.bytes = new byte[16], 0, 16);
            this.tokenId = GSSToken.readInt(this.bytes, 0);
            if (this.tokenId != n) {
                throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token ID!");
            }
            if ((this.bytes[2] & 0x1) != (MessageToken_v2.this.initiator ? 1 : 0)) {
                throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Acceptor Flag Error!");
            }
            if ((this.bytes[2] & 0x2) == 0x2 && this.tokenId == 1284) {
                messageProp.setPrivacy(true);
            }
            else {
                messageProp.setPrivacy(false);
            }
            if (this.tokenId == 1284) {
                if ((this.bytes[3] & 0xFF) != 0xFF) {
                    throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token Filler!");
                }
                MessageToken_v2.this.ec = GSSToken.readBigEndian(this.bytes, 4, 2);
                MessageToken_v2.this.rrc = GSSToken.readBigEndian(this.bytes, 6, 2);
            }
            else if (this.tokenId == 1028) {
                for (int i = 3; i < 8; ++i) {
                    if ((this.bytes[i] & 0xFF) != 0xFF) {
                        throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token Filler!");
                    }
                }
            }
            messageProp.setQOP(0);
            MessageToken_v2.this.seqNumber = GSSToken.readBigEndian(this.bytes, 12, 4);
        }
        
        public final void encode(final OutputStream outputStream) throws IOException {
            outputStream.write(this.bytes);
        }
        
        public final int getTokenId() {
            return this.tokenId;
        }
        
        public final byte[] getBytes() {
            return this.bytes;
        }
    }
}
