package sun.security.provider.certpath;

import java.io.IOException;
import java.util.Iterator;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerValue;
import sun.security.x509.PKIXExtensions;
import java.io.OutputStream;
import sun.security.util.DerOutputStream;
import java.util.Collections;
import java.security.cert.Extension;
import java.util.List;
import sun.security.util.Debug;

class OCSPRequest
{
    private static final Debug debug;
    private static final boolean dump;
    private final List<CertId> certIds;
    private final List<Extension> extensions;
    private byte[] nonce;
    
    OCSPRequest(final CertId certId) {
        this(Collections.singletonList(certId));
    }
    
    OCSPRequest(final List<CertId> certIds) {
        this.certIds = certIds;
        this.extensions = Collections.emptyList();
    }
    
    OCSPRequest(final List<CertId> certIds, final List<Extension> extensions) {
        this.certIds = certIds;
        this.extensions = extensions;
    }
    
    byte[] encodeBytes() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        for (final CertId certId : this.certIds) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            certId.encode(derOutputStream3);
            derOutputStream2.write((byte)48, derOutputStream3);
        }
        derOutputStream.write((byte)48, derOutputStream2);
        if (!this.extensions.isEmpty()) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            for (final Extension extension : this.extensions) {
                extension.encode(derOutputStream4);
                if (extension.getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
                    this.nonce = extension.getValue();
                }
            }
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            derOutputStream5.write((byte)48, derOutputStream4);
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream5);
        }
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write((byte)48, derOutputStream);
        final DerOutputStream derOutputStream7 = new DerOutputStream();
        derOutputStream7.write((byte)48, derOutputStream6);
        final byte[] byteArray = derOutputStream7.toByteArray();
        if (OCSPRequest.dump) {
            OCSPRequest.debug.println("OCSPRequest bytes...\n\n" + new HexDumpEncoder().encode(byteArray) + "\n");
        }
        return byteArray;
    }
    
    List<CertId> getCertIds() {
        return this.certIds;
    }
    
    byte[] getNonce() {
        return this.nonce;
    }
    
    static {
        debug = Debug.getInstance("certpath");
        dump = (OCSPRequest.debug != null && Debug.isOn("ocsp"));
    }
}
