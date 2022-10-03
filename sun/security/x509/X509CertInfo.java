package sun.security.x509;

import java.util.HashMap;
import sun.security.util.DerInputStream;
import java.util.Iterator;
import sun.misc.HexDumpEncoder;
import java.security.cert.CertificateEncodingException;
import java.util.Enumeration;
import java.security.cert.CertificateException;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.security.cert.CertificateParsingException;
import sun.security.util.DerValue;
import java.util.Map;

public class X509CertInfo implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info";
    public static final String NAME = "info";
    public static final String DN_NAME = "dname";
    public static final String VERSION = "version";
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String ALGORITHM_ID = "algorithmID";
    public static final String ISSUER = "issuer";
    public static final String SUBJECT = "subject";
    public static final String VALIDITY = "validity";
    public static final String KEY = "key";
    public static final String ISSUER_ID = "issuerID";
    public static final String SUBJECT_ID = "subjectID";
    public static final String EXTENSIONS = "extensions";
    protected CertificateVersion version;
    protected CertificateSerialNumber serialNum;
    protected CertificateAlgorithmId algId;
    protected X500Name issuer;
    protected X500Name subject;
    protected CertificateValidity interval;
    protected CertificateX509Key pubKey;
    protected UniqueIdentity issuerUniqueId;
    protected UniqueIdentity subjectUniqueId;
    protected CertificateExtensions extensions;
    private static final int ATTR_VERSION = 1;
    private static final int ATTR_SERIAL = 2;
    private static final int ATTR_ALGORITHM = 3;
    private static final int ATTR_ISSUER = 4;
    private static final int ATTR_VALIDITY = 5;
    private static final int ATTR_SUBJECT = 6;
    private static final int ATTR_KEY = 7;
    private static final int ATTR_ISSUER_ID = 8;
    private static final int ATTR_SUBJECT_ID = 9;
    private static final int ATTR_EXTENSIONS = 10;
    private byte[] rawCertInfo;
    private static final Map<String, Integer> map;
    
    public X509CertInfo() {
        this.version = new CertificateVersion();
        this.serialNum = null;
        this.algId = null;
        this.issuer = null;
        this.subject = null;
        this.interval = null;
        this.pubKey = null;
        this.issuerUniqueId = null;
        this.subjectUniqueId = null;
        this.extensions = null;
        this.rawCertInfo = null;
    }
    
    public X509CertInfo(final byte[] array) throws CertificateParsingException {
        this.version = new CertificateVersion();
        this.serialNum = null;
        this.algId = null;
        this.issuer = null;
        this.subject = null;
        this.interval = null;
        this.pubKey = null;
        this.issuerUniqueId = null;
        this.subjectUniqueId = null;
        this.extensions = null;
        this.rawCertInfo = null;
        try {
            this.parse(new DerValue(array));
        }
        catch (final IOException ex) {
            throw new CertificateParsingException(ex);
        }
    }
    
    public X509CertInfo(final DerValue derValue) throws CertificateParsingException {
        this.version = new CertificateVersion();
        this.serialNum = null;
        this.algId = null;
        this.issuer = null;
        this.subject = null;
        this.interval = null;
        this.pubKey = null;
        this.issuerUniqueId = null;
        this.subjectUniqueId = null;
        this.extensions = null;
        this.rawCertInfo = null;
        try {
            this.parse(derValue);
        }
        catch (final IOException ex) {
            throw new CertificateParsingException(ex);
        }
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws CertificateException, IOException {
        if (this.rawCertInfo == null) {
            final DerOutputStream derOutputStream = new DerOutputStream();
            this.emit(derOutputStream);
            this.rawCertInfo = derOutputStream.toByteArray();
        }
        outputStream.write(this.rawCertInfo.clone());
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("version");
        attributeNameEnumeration.addElement("serialNumber");
        attributeNameEnumeration.addElement("algorithmID");
        attributeNameEnumeration.addElement("issuer");
        attributeNameEnumeration.addElement("validity");
        attributeNameEnumeration.addElement("subject");
        attributeNameEnumeration.addElement("key");
        attributeNameEnumeration.addElement("issuerID");
        attributeNameEnumeration.addElement("subjectID");
        attributeNameEnumeration.addElement("extensions");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "info";
    }
    
    public byte[] getEncodedInfo() throws CertificateEncodingException {
        try {
            if (this.rawCertInfo == null) {
                final DerOutputStream derOutputStream = new DerOutputStream();
                this.emit(derOutputStream);
                this.rawCertInfo = derOutputStream.toByteArray();
            }
            return this.rawCertInfo.clone();
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
        catch (final CertificateException ex2) {
            throw new CertificateEncodingException(ex2.toString());
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof X509CertInfo && this.equals((X509CertInfo)o);
    }
    
    public boolean equals(final X509CertInfo x509CertInfo) {
        if (this == x509CertInfo) {
            return true;
        }
        if (this.rawCertInfo == null || x509CertInfo.rawCertInfo == null) {
            return false;
        }
        if (this.rawCertInfo.length != x509CertInfo.rawCertInfo.length) {
            return false;
        }
        for (int i = 0; i < this.rawCertInfo.length; ++i) {
            if (this.rawCertInfo[i] != x509CertInfo.rawCertInfo[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (byte b = 1; b < this.rawCertInfo.length; ++b) {
            n += this.rawCertInfo[b] * b;
        }
        return n;
    }
    
    @Override
    public String toString() {
        if (this.subject == null || this.pubKey == null || this.interval == null || this.issuer == null || this.algId == null || this.serialNum == null) {
            throw new NullPointerException("X.509 cert is incomplete");
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        sb.append("  " + this.version.toString() + "\n");
        sb.append("  Subject: " + this.subject.toString() + "\n");
        sb.append("  Signature Algorithm: " + this.algId.toString() + "\n");
        sb.append("  Key:  " + this.pubKey.toString() + "\n");
        sb.append("  " + this.interval.toString() + "\n");
        sb.append("  Issuer: " + this.issuer.toString() + "\n");
        sb.append("  " + this.serialNum.toString() + "\n");
        if (this.issuerUniqueId != null) {
            sb.append("  Issuer Id:\n" + this.issuerUniqueId.toString() + "\n");
        }
        if (this.subjectUniqueId != null) {
            sb.append("  Subject Id:\n" + this.subjectUniqueId.toString() + "\n");
        }
        if (this.extensions != null) {
            final Extension[] array = this.extensions.getAllExtensions().toArray(new Extension[0]);
            sb.append("\nCertificate Extensions: " + array.length);
            for (int i = 0; i < array.length; ++i) {
                sb.append("\n[" + (i + 1) + "]: ");
                final Extension extension = array[i];
                try {
                    if (OIDMap.getClass(extension.getExtensionId()) == null) {
                        sb.append(extension.toString());
                        final byte[] extensionValue = extension.getExtensionValue();
                        if (extensionValue != null) {
                            final DerOutputStream derOutputStream = new DerOutputStream();
                            derOutputStream.putOctetString(extensionValue);
                            sb.append("Extension unknown: DER encoded OCTET string =\n" + new HexDumpEncoder().encodeBuffer(derOutputStream.toByteArray()) + "\n");
                        }
                    }
                    else {
                        sb.append(extension.toString());
                    }
                }
                catch (final Exception ex) {
                    sb.append(", Error parsing this extension");
                }
            }
            final Map<String, Extension> unparseableExtensions = this.extensions.getUnparseableExtensions();
            if (!unparseableExtensions.isEmpty()) {
                sb.append("\nUnparseable certificate extensions: " + unparseableExtensions.size());
                int n = 1;
                for (final Extension extension2 : unparseableExtensions.values()) {
                    sb.append("\n[" + n++ + "]: ");
                    sb.append(extension2);
                }
            }
        }
        sb.append("\n]");
        return sb.toString();
    }
    
    @Override
    public void set(final String s, final Object o) throws CertificateException, IOException {
        final X509AttributeName x509AttributeName = new X509AttributeName(s);
        final int attributeMap = this.attributeMap(x509AttributeName.getPrefix());
        if (attributeMap == 0) {
            throw new CertificateException("Attribute name not recognized: " + s);
        }
        this.rawCertInfo = null;
        final String suffix = x509AttributeName.getSuffix();
        switch (attributeMap) {
            case 1: {
                if (suffix == null) {
                    this.setVersion(o);
                    break;
                }
                this.version.set(suffix, o);
                break;
            }
            case 2: {
                if (suffix == null) {
                    this.setSerialNumber(o);
                    break;
                }
                this.serialNum.set(suffix, o);
                break;
            }
            case 3: {
                if (suffix == null) {
                    this.setAlgorithmId(o);
                    break;
                }
                this.algId.set(suffix, o);
                break;
            }
            case 4: {
                this.setIssuer(o);
                break;
            }
            case 5: {
                if (suffix == null) {
                    this.setValidity(o);
                    break;
                }
                this.interval.set(suffix, o);
                break;
            }
            case 6: {
                this.setSubject(o);
                break;
            }
            case 7: {
                if (suffix == null) {
                    this.setKey(o);
                    break;
                }
                this.pubKey.set(suffix, o);
                break;
            }
            case 8: {
                this.setIssuerUniqueId(o);
                break;
            }
            case 9: {
                this.setSubjectUniqueId(o);
                break;
            }
            case 10: {
                if (suffix == null) {
                    this.setExtensions(o);
                    break;
                }
                if (this.extensions == null) {
                    this.extensions = new CertificateExtensions();
                }
                this.extensions.set(suffix, o);
                break;
            }
        }
    }
    
    @Override
    public void delete(final String s) throws CertificateException, IOException {
        final X509AttributeName x509AttributeName = new X509AttributeName(s);
        final int attributeMap = this.attributeMap(x509AttributeName.getPrefix());
        if (attributeMap == 0) {
            throw new CertificateException("Attribute name not recognized: " + s);
        }
        this.rawCertInfo = null;
        final String suffix = x509AttributeName.getSuffix();
        switch (attributeMap) {
            case 1: {
                if (suffix == null) {
                    this.version = null;
                    break;
                }
                this.version.delete(suffix);
                break;
            }
            case 2: {
                if (suffix == null) {
                    this.serialNum = null;
                    break;
                }
                this.serialNum.delete(suffix);
                break;
            }
            case 3: {
                if (suffix == null) {
                    this.algId = null;
                    break;
                }
                this.algId.delete(suffix);
                break;
            }
            case 4: {
                this.issuer = null;
                break;
            }
            case 5: {
                if (suffix == null) {
                    this.interval = null;
                    break;
                }
                this.interval.delete(suffix);
                break;
            }
            case 6: {
                this.subject = null;
                break;
            }
            case 7: {
                if (suffix == null) {
                    this.pubKey = null;
                    break;
                }
                this.pubKey.delete(suffix);
                break;
            }
            case 8: {
                this.issuerUniqueId = null;
                break;
            }
            case 9: {
                this.subjectUniqueId = null;
                break;
            }
            case 10: {
                if (suffix == null) {
                    this.extensions = null;
                    break;
                }
                if (this.extensions != null) {
                    this.extensions.delete(suffix);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public Object get(final String s) throws CertificateException, IOException {
        final X509AttributeName x509AttributeName = new X509AttributeName(s);
        final int attributeMap = this.attributeMap(x509AttributeName.getPrefix());
        if (attributeMap == 0) {
            throw new CertificateParsingException("Attribute name not recognized: " + s);
        }
        final String suffix = x509AttributeName.getSuffix();
        switch (attributeMap) {
            case 10: {
                if (suffix == null) {
                    return this.extensions;
                }
                if (this.extensions == null) {
                    return null;
                }
                return this.extensions.get(suffix);
            }
            case 6: {
                if (suffix == null) {
                    return this.subject;
                }
                return this.getX500Name(suffix, false);
            }
            case 4: {
                if (suffix == null) {
                    return this.issuer;
                }
                return this.getX500Name(suffix, true);
            }
            case 7: {
                if (suffix == null) {
                    return this.pubKey;
                }
                return this.pubKey.get(suffix);
            }
            case 3: {
                if (suffix == null) {
                    return this.algId;
                }
                return this.algId.get(suffix);
            }
            case 5: {
                if (suffix == null) {
                    return this.interval;
                }
                return this.interval.get(suffix);
            }
            case 1: {
                if (suffix == null) {
                    return this.version;
                }
                return this.version.get(suffix);
            }
            case 2: {
                if (suffix == null) {
                    return this.serialNum;
                }
                return this.serialNum.get(suffix);
            }
            case 8: {
                return this.issuerUniqueId;
            }
            case 9: {
                return this.subjectUniqueId;
            }
            default: {
                return null;
            }
        }
    }
    
    private Object getX500Name(final String s, final boolean b) throws IOException {
        if (s.equalsIgnoreCase("dname")) {
            return b ? this.issuer : this.subject;
        }
        if (s.equalsIgnoreCase("x500principal")) {
            return b ? this.issuer.asX500Principal() : this.subject.asX500Principal();
        }
        throw new IOException("Attribute name not recognized.");
    }
    
    private void parse(final DerValue derValue) throws CertificateParsingException, IOException {
        if (derValue.tag != 48) {
            throw new CertificateParsingException("signed fields invalid");
        }
        this.rawCertInfo = derValue.toByteArray();
        final DerInputStream data = derValue.data;
        DerValue derValue2 = data.getDerValue();
        if (derValue2.isContextSpecific((byte)0)) {
            this.version = new CertificateVersion(derValue2);
            derValue2 = data.getDerValue();
        }
        this.serialNum = new CertificateSerialNumber(derValue2);
        this.algId = new CertificateAlgorithmId(data);
        this.issuer = new X500Name(data);
        if (this.issuer.isEmpty()) {
            throw new CertificateParsingException("Empty issuer DN not allowed in X509Certificates");
        }
        this.interval = new CertificateValidity(data);
        this.subject = new X500Name(data);
        if (this.version.compare(0) == 0 && this.subject.isEmpty()) {
            throw new CertificateParsingException("Empty subject DN not allowed in v1 certificate");
        }
        this.pubKey = new CertificateX509Key(data);
        if (data.available() == 0) {
            return;
        }
        if (this.version.compare(0) == 0) {
            throw new CertificateParsingException("no more data allowed for version 1 certificate");
        }
        DerValue derValue3 = data.getDerValue();
        if (derValue3.isContextSpecific((byte)1)) {
            this.issuerUniqueId = new UniqueIdentity(derValue3);
            if (data.available() == 0) {
                return;
            }
            derValue3 = data.getDerValue();
        }
        if (derValue3.isContextSpecific((byte)2)) {
            this.subjectUniqueId = new UniqueIdentity(derValue3);
            if (data.available() == 0) {
                return;
            }
            derValue3 = data.getDerValue();
        }
        if (this.version.compare(2) != 0) {
            throw new CertificateParsingException("Extensions not allowed in v2 certificate");
        }
        if (derValue3.isConstructed() && derValue3.isContextSpecific((byte)3)) {
            this.extensions = new CertificateExtensions(derValue3.data);
        }
        this.verifyCert(this.subject, this.extensions);
    }
    
    private void verifyCert(final X500Name x500Name, final CertificateExtensions certificateExtensions) throws CertificateParsingException, IOException {
        if (x500Name.isEmpty()) {
            if (certificateExtensions == null) {
                throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and certificate has no extensions");
            }
            SubjectAlternativeNameExtension subjectAlternativeNameExtension;
            GeneralNames value;
            try {
                subjectAlternativeNameExtension = (SubjectAlternativeNameExtension)certificateExtensions.get("SubjectAlternativeName");
                value = subjectAlternativeNameExtension.get("subject_name");
            }
            catch (final IOException ex) {
                throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is absent");
            }
            if (value == null || value.isEmpty()) {
                throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is empty");
            }
            if (!subjectAlternativeNameExtension.isCritical()) {
                throw new CertificateParsingException("X.509 Certificate is incomplete: SubjectAlternativeName extension MUST be marked critical when subject field is empty");
            }
        }
    }
    
    private void emit(final DerOutputStream derOutputStream) throws CertificateException, IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.version.encode(derOutputStream2);
        this.serialNum.encode(derOutputStream2);
        this.algId.encode(derOutputStream2);
        if (this.version.compare(0) == 0 && this.issuer.toString() == null) {
            throw new CertificateParsingException("Null issuer DN not allowed in v1 certificate");
        }
        this.issuer.encode(derOutputStream2);
        this.interval.encode(derOutputStream2);
        if (this.version.compare(0) == 0 && this.subject.toString() == null) {
            throw new CertificateParsingException("Null subject DN not allowed in v1 certificate");
        }
        this.subject.encode(derOutputStream2);
        this.pubKey.encode(derOutputStream2);
        if (this.issuerUniqueId != null) {
            this.issuerUniqueId.encode(derOutputStream2, DerValue.createTag((byte)(-128), false, (byte)1));
        }
        if (this.subjectUniqueId != null) {
            this.subjectUniqueId.encode(derOutputStream2, DerValue.createTag((byte)(-128), false, (byte)2));
        }
        if (this.extensions != null) {
            this.extensions.encode(derOutputStream2);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    private int attributeMap(final String s) {
        final Integer n = X509CertInfo.map.get(s);
        if (n == null) {
            return 0;
        }
        return n;
    }
    
    private void setVersion(final Object o) throws CertificateException {
        if (!(o instanceof CertificateVersion)) {
            throw new CertificateException("Version class type invalid.");
        }
        this.version = (CertificateVersion)o;
    }
    
    private void setSerialNumber(final Object o) throws CertificateException {
        if (!(o instanceof CertificateSerialNumber)) {
            throw new CertificateException("SerialNumber class type invalid.");
        }
        this.serialNum = (CertificateSerialNumber)o;
    }
    
    private void setAlgorithmId(final Object o) throws CertificateException {
        if (!(o instanceof CertificateAlgorithmId)) {
            throw new CertificateException("AlgorithmId class type invalid.");
        }
        this.algId = (CertificateAlgorithmId)o;
    }
    
    private void setIssuer(final Object o) throws CertificateException {
        if (!(o instanceof X500Name)) {
            throw new CertificateException("Issuer class type invalid.");
        }
        this.issuer = (X500Name)o;
    }
    
    private void setValidity(final Object o) throws CertificateException {
        if (!(o instanceof CertificateValidity)) {
            throw new CertificateException("CertificateValidity class type invalid.");
        }
        this.interval = (CertificateValidity)o;
    }
    
    private void setSubject(final Object o) throws CertificateException {
        if (!(o instanceof X500Name)) {
            throw new CertificateException("Subject class type invalid.");
        }
        this.subject = (X500Name)o;
    }
    
    private void setKey(final Object o) throws CertificateException {
        if (!(o instanceof CertificateX509Key)) {
            throw new CertificateException("Key class type invalid.");
        }
        this.pubKey = (CertificateX509Key)o;
    }
    
    private void setIssuerUniqueId(final Object o) throws CertificateException {
        if (this.version.compare(1) < 0) {
            throw new CertificateException("Invalid version");
        }
        if (!(o instanceof UniqueIdentity)) {
            throw new CertificateException("IssuerUniqueId class type invalid.");
        }
        this.issuerUniqueId = (UniqueIdentity)o;
    }
    
    private void setSubjectUniqueId(final Object o) throws CertificateException {
        if (this.version.compare(1) < 0) {
            throw new CertificateException("Invalid version");
        }
        if (!(o instanceof UniqueIdentity)) {
            throw new CertificateException("SubjectUniqueId class type invalid.");
        }
        this.subjectUniqueId = (UniqueIdentity)o;
    }
    
    private void setExtensions(final Object o) throws CertificateException {
        if (this.version.compare(2) < 0) {
            throw new CertificateException("Invalid version");
        }
        if (!(o instanceof CertificateExtensions)) {
            throw new CertificateException("Extensions class type invalid.");
        }
        this.extensions = (CertificateExtensions)o;
    }
    
    static {
        (map = new HashMap<String, Integer>()).put("version", 1);
        X509CertInfo.map.put("serialNumber", 2);
        X509CertInfo.map.put("algorithmID", 3);
        X509CertInfo.map.put("issuer", 4);
        X509CertInfo.map.put("validity", 5);
        X509CertInfo.map.put("subject", 6);
        X509CertInfo.map.put("key", 7);
        X509CertInfo.map.put("issuerID", 8);
        X509CertInfo.map.put("subjectID", 9);
        X509CertInfo.map.put("extensions", 10);
    }
}
