package sun.security.jgss.krb5;

import sun.security.jgss.GSSHeader;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import sun.security.krb5.Confounder;
import java.io.IOException;
import sun.security.jgss.GSSToken;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import java.io.InputStream;

class WrapToken extends MessageToken
{
    static final int CONFOUNDER_SIZE = 8;
    static final byte[][] pads;
    private boolean readTokenFromInputStream;
    private InputStream is;
    private byte[] tokenBytes;
    private int tokenOffset;
    private int tokenLen;
    private byte[] dataBytes;
    private int dataOffset;
    private int dataLen;
    private int dataSize;
    byte[] confounder;
    byte[] padding;
    private boolean privacy;
    
    public WrapToken(final Krb5Context krb5Context, final byte[] tokenBytes, final int tokenOffset, final int tokenLen, final MessageProp messageProp) throws GSSException {
        super(513, krb5Context, tokenBytes, tokenOffset, tokenLen, messageProp);
        this.readTokenFromInputStream = true;
        this.is = null;
        this.tokenBytes = null;
        this.tokenOffset = 0;
        this.tokenLen = 0;
        this.dataBytes = null;
        this.dataOffset = 0;
        this.dataLen = 0;
        this.dataSize = 0;
        this.confounder = null;
        this.padding = null;
        this.privacy = false;
        this.readTokenFromInputStream = false;
        this.tokenBytes = tokenBytes;
        this.tokenOffset = tokenOffset;
        this.tokenLen = tokenLen;
        this.privacy = messageProp.getPrivacy();
        this.dataSize = this.getGSSHeader().getMechTokenLength() - this.getKrb5TokenSize();
    }
    
    public WrapToken(final Krb5Context krb5Context, final InputStream is, final MessageProp messageProp) throws GSSException {
        super(513, krb5Context, is, messageProp);
        this.readTokenFromInputStream = true;
        this.is = null;
        this.tokenBytes = null;
        this.tokenOffset = 0;
        this.tokenLen = 0;
        this.dataBytes = null;
        this.dataOffset = 0;
        this.dataLen = 0;
        this.dataSize = 0;
        this.confounder = null;
        this.padding = null;
        this.privacy = false;
        this.is = is;
        this.privacy = messageProp.getPrivacy();
        this.dataSize = this.getGSSHeader().getMechTokenLength() - this.getTokenSize();
    }
    
    public byte[] getData() throws GSSException {
        final byte[] array = new byte[this.dataSize];
        this.getData(array, 0);
        final byte[] array2 = new byte[this.dataSize - this.confounder.length - this.padding.length];
        System.arraycopy(array, 0, array2, 0, array2.length);
        return array2;
    }
    
    public int getData(final byte[] array, final int n) throws GSSException {
        if (this.readTokenFromInputStream) {
            this.getDataFromStream(array, n);
        }
        else {
            this.getDataFromBuffer(array, n);
        }
        return this.dataSize - this.confounder.length - this.padding.length;
    }
    
    private void getDataFromBuffer(final byte[] array, final int n) throws GSSException {
        final int n2 = this.tokenOffset + this.getGSSHeader().getLength() + this.getTokenSize();
        if (n2 + this.dataSize > this.tokenOffset + this.tokenLen) {
            throw new GSSException(10, -1, "Insufficient data in " + Krb5Token.getTokenName(this.getTokenId()));
        }
        this.confounder = new byte[8];
        if (this.privacy) {
            this.cipherHelper.decryptData(this, this.tokenBytes, n2, this.dataSize, array, n);
        }
        else {
            System.arraycopy(this.tokenBytes, n2, this.confounder, 0, 8);
            int n3 = this.tokenBytes[n2 + this.dataSize - 1];
            if (n3 < 0) {
                n3 = 0;
            }
            if (n3 > 8) {
                n3 %= 8;
            }
            this.padding = WrapToken.pads[n3];
            System.arraycopy(this.tokenBytes, n2 + 8, array, n, this.dataSize - 8 - n3);
        }
        if (!this.verifySignAndSeqNumber(this.confounder, array, n, this.dataSize - 8 - this.padding.length, this.padding)) {
            throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token");
        }
    }
    
    private void getDataFromStream(final byte[] array, final int n) throws GSSException {
        this.getGSSHeader();
        this.confounder = new byte[8];
        try {
            if (this.privacy) {
                this.cipherHelper.decryptData(this, this.is, this.dataSize, array, n);
            }
            else {
                GSSToken.readFully(this.is, this.confounder);
                if (this.cipherHelper.isArcFour()) {
                    this.padding = WrapToken.pads[1];
                    GSSToken.readFully(this.is, array, n, this.dataSize - 8 - 1);
                }
                else {
                    final int n2 = (this.dataSize - 8) / 8 - 1;
                    int n3 = n;
                    for (int i = 0; i < n2; ++i) {
                        GSSToken.readFully(this.is, array, n3, 8);
                        n3 += 8;
                    }
                    final byte[] array2 = new byte[8];
                    GSSToken.readFully(this.is, array2);
                    final byte b = array2[7];
                    this.padding = WrapToken.pads[b];
                    System.arraycopy(array2, 0, array, n3, array2.length - b);
                }
            }
        }
        catch (final IOException ex) {
            throw new GSSException(10, -1, Krb5Token.getTokenName(this.getTokenId()) + ": " + ex.getMessage());
        }
        if (!this.verifySignAndSeqNumber(this.confounder, array, n, this.dataSize - 8 - this.padding.length, this.padding)) {
            throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token");
        }
    }
    
    private byte[] getPadding(final int n) {
        int n2;
        if (this.cipherHelper.isArcFour()) {
            n2 = 1;
        }
        else {
            n2 = 8 - n % 8;
        }
        return WrapToken.pads[n2];
    }
    
    public WrapToken(final Krb5Context krb5Context, final MessageProp messageProp, final byte[] dataBytes, final int dataOffset, final int dataLen) throws GSSException {
        super(513, krb5Context);
        this.readTokenFromInputStream = true;
        this.is = null;
        this.tokenBytes = null;
        this.tokenOffset = 0;
        this.tokenLen = 0;
        this.dataBytes = null;
        this.dataOffset = 0;
        this.dataLen = 0;
        this.dataSize = 0;
        this.confounder = null;
        this.padding = null;
        this.privacy = false;
        this.confounder = Confounder.bytes(8);
        this.padding = this.getPadding(dataLen);
        this.dataSize = this.confounder.length + dataLen + this.padding.length;
        this.dataBytes = dataBytes;
        this.dataOffset = dataOffset;
        this.dataLen = dataLen;
        this.genSignAndSeqNumber(messageProp, this.confounder, dataBytes, dataOffset, dataLen, this.padding);
        if (!krb5Context.getConfState()) {
            messageProp.setPrivacy(false);
        }
        this.privacy = messageProp.getPrivacy();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException, GSSException {
        super.encode(outputStream);
        if (!this.privacy) {
            outputStream.write(this.confounder);
            outputStream.write(this.dataBytes, this.dataOffset, this.dataLen);
            outputStream.write(this.padding);
        }
        else {
            this.cipherHelper.encryptData(this, this.confounder, this.dataBytes, this.dataOffset, this.dataLen, this.padding, outputStream);
        }
    }
    
    public byte[] encode() throws IOException, GSSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.dataSize + 50);
        this.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public int encode(final byte[] array, int n) throws IOException, GSSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        super.encode(byteArrayOutputStream);
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        System.arraycopy(byteArray, 0, array, n, byteArray.length);
        n += byteArray.length;
        if (!this.privacy) {
            System.arraycopy(this.confounder, 0, array, n, this.confounder.length);
            n += this.confounder.length;
            System.arraycopy(this.dataBytes, this.dataOffset, array, n, this.dataLen);
            n += this.dataLen;
            System.arraycopy(this.padding, 0, array, n, this.padding.length);
        }
        else {
            this.cipherHelper.encryptData(this, this.confounder, this.dataBytes, this.dataOffset, this.dataLen, this.padding, array, n);
        }
        return byteArray.length + this.confounder.length + this.dataLen + this.padding.length;
    }
    
    @Override
    protected int getKrb5TokenSize() throws GSSException {
        return this.getTokenSize() + this.dataSize;
    }
    
    @Override
    protected int getSealAlg(final boolean b, final int n) throws GSSException {
        if (!b) {
            return 65535;
        }
        return this.cipherHelper.getSealAlg();
    }
    
    static int getSizeLimit(final int n, final boolean b, final int n2, final CipherHelper cipherHelper) throws GSSException {
        return GSSHeader.getMaxMechTokenSize(WrapToken.OID, n2) - (MessageToken.getTokenSize(cipherHelper) + 8) - 8;
    }
    
    static {
        pads = new byte[][] { null, { 1 }, { 2, 2 }, { 3, 3, 3 }, { 4, 4, 4, 4 }, { 5, 5, 5, 5, 5 }, { 6, 6, 6, 6, 6, 6 }, { 7, 7, 7, 7, 7, 7, 7 }, { 8, 8, 8, 8, 8, 8, 8, 8 } };
    }
}
