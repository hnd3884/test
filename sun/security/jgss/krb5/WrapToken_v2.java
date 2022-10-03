package sun.security.jgss.krb5;

import sun.security.jgss.GSSHeader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import sun.security.krb5.Confounder;
import java.util.Arrays;
import java.io.InputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;

class WrapToken_v2 extends MessageToken_v2
{
    byte[] confounder;
    private final boolean privacy;
    
    public WrapToken_v2(final Krb5Context krb5Context, final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        super(1284, krb5Context, array, n, n2, messageProp);
        this.confounder = null;
        this.privacy = messageProp.getPrivacy();
    }
    
    public WrapToken_v2(final Krb5Context krb5Context, final InputStream inputStream, final MessageProp messageProp) throws GSSException {
        super(1284, krb5Context, inputStream, messageProp);
        this.confounder = null;
        this.privacy = messageProp.getPrivacy();
    }
    
    public byte[] getData() throws GSSException {
        final byte[] array = new byte[this.tokenDataLen];
        return Arrays.copyOf(array, this.getData(array, 0));
    }
    
    public int getData(final byte[] array, final int n) throws GSSException {
        if (this.privacy) {
            this.cipherHelper.decryptData(this, this.tokenData, 0, this.tokenDataLen, array, n, this.getKeyUsage());
            return this.tokenDataLen - 16 - 16 - this.cipherHelper.getChecksumLength();
        }
        final int n2 = this.tokenDataLen - this.cipherHelper.getChecksumLength();
        System.arraycopy(this.tokenData, 0, array, n, n2);
        if (!this.verifySign(array, n, n2)) {
            throw new GSSException(6, -1, "Corrupt checksum in Wrap token");
        }
        return n2;
    }
    
    public WrapToken_v2(final Krb5Context krb5Context, final MessageProp messageProp, final byte[] array, final int n, final int n2) throws GSSException {
        super(1284, krb5Context);
        this.confounder = null;
        this.confounder = Confounder.bytes(16);
        this.genSignAndSeqNumber(messageProp, array, n, n2);
        if (!krb5Context.getConfState()) {
            messageProp.setPrivacy(false);
        }
        if (!(this.privacy = messageProp.getPrivacy())) {
            System.arraycopy(array, n, this.tokenData = new byte[n2 + this.checksum.length], 0, n2);
            System.arraycopy(this.checksum, 0, this.tokenData, n2, this.checksum.length);
        }
        else {
            this.tokenData = this.cipherHelper.encryptData(this, this.confounder, this.getTokenHeader(), array, n, n2, this.getKeyUsage());
        }
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        this.encodeHeader(outputStream);
        outputStream.write(this.tokenData);
    }
    
    public byte[] encode() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(16 + this.tokenData.length);
        this.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public int encode(final byte[] array, final int n) throws IOException {
        final byte[] encode = this.encode();
        System.arraycopy(encode, 0, array, n, encode.length);
        return encode.length;
    }
    
    static int getSizeLimit(final int n, final boolean b, final int n2, final CipherHelper cipherHelper) throws GSSException {
        return GSSHeader.getMaxMechTokenSize(WrapToken_v2.OID, n2) - (16 + cipherHelper.getChecksumLength() + 16) - 8;
    }
}
