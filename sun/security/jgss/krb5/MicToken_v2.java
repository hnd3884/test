package sun.security.jgss.krb5;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;

class MicToken_v2 extends MessageToken_v2
{
    public MicToken_v2(final Krb5Context krb5Context, final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        super(1028, krb5Context, array, n, n2, messageProp);
    }
    
    public MicToken_v2(final Krb5Context krb5Context, final InputStream inputStream, final MessageProp messageProp) throws GSSException {
        super(1028, krb5Context, inputStream, messageProp);
    }
    
    public void verify(final byte[] array, final int n, final int n2) throws GSSException {
        if (!this.verifySign(array, n, n2)) {
            throw new GSSException(6, -1, "Corrupt checksum or sequence number in MIC token");
        }
    }
    
    public void verify(final InputStream inputStream) throws GSSException {
        byte[] array;
        try {
            array = new byte[inputStream.available()];
            inputStream.read(array);
        }
        catch (final IOException ex) {
            throw new GSSException(6, -1, "Corrupt checksum or sequence number in MIC token");
        }
        this.verify(array, 0, array.length);
    }
    
    public MicToken_v2(final Krb5Context krb5Context, MessageProp messageProp, final byte[] array, final int n, final int n2) throws GSSException {
        super(1028, krb5Context);
        if (messageProp == null) {
            messageProp = new MessageProp(0, false);
        }
        this.genSignAndSeqNumber(messageProp, array, n, n2);
    }
    
    public MicToken_v2(final Krb5Context krb5Context, MessageProp messageProp, final InputStream inputStream) throws GSSException, IOException {
        super(1028, krb5Context);
        final byte[] array = new byte[inputStream.available()];
        inputStream.read(array);
        if (messageProp == null) {
            messageProp = new MessageProp(0, false);
        }
        this.genSignAndSeqNumber(messageProp, array, 0, array.length);
    }
    
    public byte[] encode() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(50);
        this.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public int encode(final byte[] array, final int n) throws IOException {
        final byte[] encode = this.encode();
        System.arraycopy(encode, 0, array, n, encode.length);
        return encode.length;
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        this.encodeHeader(outputStream);
        outputStream.write(this.checksum);
    }
}
