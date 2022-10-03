package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.asn1.ocsp.ResponderID;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.Extensions;
import java.util.Vector;

public class OCSPStatusRequest
{
    protected Vector responderIDList;
    protected Extensions requestExtensions;
    
    public OCSPStatusRequest(final Vector responderIDList, final Extensions requestExtensions) {
        this.responderIDList = responderIDList;
        this.requestExtensions = requestExtensions;
    }
    
    public Vector getResponderIDList() {
        return this.responderIDList;
    }
    
    public Extensions getRequestExtensions() {
        return this.requestExtensions;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        if (this.responderIDList == null || this.responderIDList.isEmpty()) {
            TlsUtils.writeUint16(0, outputStream);
        }
        else {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (int i = 0; i < this.responderIDList.size(); ++i) {
                TlsUtils.writeOpaque16(((ResponderID)this.responderIDList.elementAt(i)).getEncoded("DER"), byteArrayOutputStream);
            }
            TlsUtils.checkUint16(byteArrayOutputStream.size());
            TlsUtils.writeUint16(byteArrayOutputStream.size(), outputStream);
            Streams.writeBufTo(byteArrayOutputStream, outputStream);
        }
        if (this.requestExtensions == null) {
            TlsUtils.writeUint16(0, outputStream);
        }
        else {
            final byte[] encoded = this.requestExtensions.getEncoded("DER");
            TlsUtils.checkUint16(encoded.length);
            TlsUtils.writeUint16(encoded.length, outputStream);
            outputStream.write(encoded);
        }
    }
    
    public static OCSPStatusRequest parse(final InputStream inputStream) throws IOException {
        final Vector vector = new Vector();
        final int uint16 = TlsUtils.readUint16(inputStream);
        if (uint16 > 0) {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(TlsUtils.readFully(uint16, inputStream));
            do {
                vector.addElement(ResponderID.getInstance(TlsUtils.readDERObject(TlsUtils.readOpaque16(byteArrayInputStream))));
            } while (byteArrayInputStream.available() > 0);
        }
        Extensions instance = null;
        final int uint17 = TlsUtils.readUint16(inputStream);
        if (uint17 > 0) {
            instance = Extensions.getInstance(TlsUtils.readDERObject(TlsUtils.readFully(uint17, inputStream)));
        }
        return new OCSPStatusRequest(vector, instance);
    }
}
