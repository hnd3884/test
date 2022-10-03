package sun.security.jgss.krb5;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;

class MicToken extends MessageToken
{
    public MicToken(final Krb5Context krb5Context, final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        super(257, krb5Context, array, n, n2, messageProp);
    }
    
    public MicToken(final Krb5Context krb5Context, final InputStream inputStream, final MessageProp messageProp) throws GSSException {
        super(257, krb5Context, inputStream, messageProp);
    }
    
    public void verify(final byte[] array, final int n, final int n2) throws GSSException {
        if (!this.verifySignAndSeqNumber(null, array, n, n2, null)) {
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
    
    public MicToken(final Krb5Context krb5Context, MessageProp messageProp, final byte[] array, final int n, final int n2) throws GSSException {
        super(257, krb5Context);
        if (messageProp == null) {
            messageProp = new MessageProp(0, false);
        }
        this.genSignAndSeqNumber(messageProp, null, array, n, n2, null);
    }
    
    public MicToken(final Krb5Context krb5Context, MessageProp messageProp, final InputStream inputStream) throws GSSException, IOException {
        super(257, krb5Context);
        final byte[] array = new byte[inputStream.available()];
        inputStream.read(array);
        if (messageProp == null) {
            messageProp = new MessageProp(0, false);
        }
        this.genSignAndSeqNumber(messageProp, null, array, 0, array.length, null);
    }
    
    @Override
    protected int getSealAlg(final boolean b, final int n) {
        return 65535;
    }
    
    public int encode(final byte[] array, final int n) throws IOException, GSSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        super.encode(byteArrayOutputStream);
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        System.arraycopy(byteArray, 0, array, n, byteArray.length);
        return byteArray.length;
    }
    
    public byte[] encode() throws IOException, GSSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(50);
        this.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
