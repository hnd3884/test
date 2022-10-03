package sun.security.provider.certpath;

import sun.security.pkcs.SignerInfo;
import sun.security.pkcs.ContentInfo;
import sun.security.x509.AlgorithmId;
import java.util.ListIterator;
import sun.security.util.DerOutputStream;
import java.security.cert.CertificateEncodingException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import sun.security.pkcs.PKCS7;
import sun.security.util.DerValue;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import sun.security.util.DerInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.security.cert.CertificateException;
import java.security.cert.Certificate;
import java.util.Collection;
import java.security.cert.X509Certificate;
import java.util.List;
import java.security.cert.CertPath;

public class X509CertPath extends CertPath
{
    private static final long serialVersionUID = 4989800333263052980L;
    private List<X509Certificate> certs;
    private static final String COUNT_ENCODING = "count";
    private static final String PKCS7_ENCODING = "PKCS7";
    private static final String PKIPATH_ENCODING = "PkiPath";
    private static final Collection<String> encodingList;
    
    public X509CertPath(final List<? extends Certificate> list) throws CertificateException {
        super("X.509");
        for (final Object next : list) {
            if (!(next instanceof X509Certificate)) {
                throw new CertificateException("List is not all X509Certificates: " + next.getClass().getName());
            }
        }
        this.certs = Collections.unmodifiableList((List<? extends X509Certificate>)new ArrayList<X509Certificate>((Collection<? extends X509Certificate>)list));
    }
    
    public X509CertPath(final InputStream inputStream) throws CertificateException {
        this(inputStream, "PkiPath");
    }
    
    public X509CertPath(final InputStream inputStream, final String s) throws CertificateException {
        super("X.509");
        switch (s) {
            case "PkiPath": {
                this.certs = parsePKIPATH(inputStream);
                break;
            }
            case "PKCS7": {
                this.certs = parsePKCS7(inputStream);
                break;
            }
            default: {
                throw new CertificateException("unsupported encoding");
            }
        }
    }
    
    private static List<X509Certificate> parsePKIPATH(final InputStream inputStream) throws CertificateException {
        if (inputStream == null) {
            throw new CertificateException("input stream is null");
        }
        try {
            final DerValue[] sequence = new DerInputStream(readAllBytes(inputStream)).getSequence(3);
            if (sequence.length == 0) {
                return Collections.emptyList();
            }
            final CertificateFactory instance = CertificateFactory.getInstance("X.509");
            final ArrayList list = new ArrayList(sequence.length);
            for (int i = sequence.length - 1; i >= 0; --i) {
                list.add((Object)instance.generateCertificate(new ByteArrayInputStream(sequence[i].toByteArray())));
            }
            return Collections.unmodifiableList((List<? extends X509Certificate>)list);
        }
        catch (final IOException ex) {
            throw new CertificateException("IOException parsing PkiPath data: " + ex, ex);
        }
    }
    
    private static List<X509Certificate> parsePKCS7(InputStream inputStream) throws CertificateException {
        if (inputStream == null) {
            throw new CertificateException("input stream is null");
        }
        List<X509Certificate> list;
        try {
            if (!inputStream.markSupported()) {
                inputStream = new ByteArrayInputStream(readAllBytes(inputStream));
            }
            final X509Certificate[] certificates = new PKCS7(inputStream).getCertificates();
            if (certificates != null) {
                list = Arrays.asList(certificates);
            }
            else {
                list = new ArrayList<X509Certificate>(0);
            }
        }
        catch (final IOException ex) {
            throw new CertificateException("IOException parsing PKCS7 data: " + ex);
        }
        return (List<X509Certificate>)Collections.unmodifiableList((List<?>)list);
    }
    
    private static byte[] readAllBytes(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[8192];
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
        int read;
        while ((read = inputStream.read(array)) != -1) {
            byteArrayOutputStream.write(array, 0, read);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        return this.encodePKIPATH();
    }
    
    private byte[] encodePKIPATH() throws CertificateEncodingException {
        final ListIterator<X509Certificate> listIterator = this.certs.listIterator(this.certs.size());
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            while (listIterator.hasPrevious()) {
                final X509Certificate x509Certificate = listIterator.previous();
                if (this.certs.lastIndexOf(x509Certificate) != this.certs.indexOf(x509Certificate)) {
                    throw new CertificateEncodingException("Duplicate Certificate");
                }
                derOutputStream.write(x509Certificate.getEncoded());
            }
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            derOutputStream2.write((byte)48, derOutputStream);
            return derOutputStream2.toByteArray();
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException("IOException encoding PkiPath data: " + ex, ex);
        }
    }
    
    private byte[] encodePKCS7() throws CertificateEncodingException {
        final PKCS7 pkcs7 = new PKCS7(new AlgorithmId[0], new ContentInfo(ContentInfo.DATA_OID, null), this.certs.toArray(new X509Certificate[this.certs.size()]), new SignerInfo[0]);
        final DerOutputStream derOutputStream = new DerOutputStream();
        try {
            pkcs7.encodeSignedData(derOutputStream);
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.getMessage());
        }
        return derOutputStream.toByteArray();
    }
    
    @Override
    public byte[] getEncoded(final String s) throws CertificateEncodingException {
        switch (s) {
            case "PkiPath": {
                return this.encodePKIPATH();
            }
            case "PKCS7": {
                return this.encodePKCS7();
            }
            default: {
                throw new CertificateEncodingException("unsupported encoding");
            }
        }
    }
    
    public static Iterator<String> getEncodingsStatic() {
        return X509CertPath.encodingList.iterator();
    }
    
    @Override
    public Iterator<String> getEncodings() {
        return getEncodingsStatic();
    }
    
    @Override
    public List<X509Certificate> getCertificates() {
        return this.certs;
    }
    
    static {
        final ArrayList list = new ArrayList(2);
        list.add("PkiPath");
        list.add("PKCS7");
        encodingList = Collections.unmodifiableCollection((Collection<?>)list);
    }
}
