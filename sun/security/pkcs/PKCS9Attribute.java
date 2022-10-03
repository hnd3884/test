package sun.security.pkcs;

import sun.misc.HexDumpEncoder;
import java.util.Locale;
import java.security.cert.CertificateException;
import java.util.Date;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.x509.CertificateExtensions;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import java.util.Hashtable;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;

public class PKCS9Attribute implements DerEncoder
{
    private static final Debug debug;
    static final ObjectIdentifier[] PKCS9_OIDS;
    private static final Class<?> BYTE_ARRAY_CLASS;
    public static final ObjectIdentifier EMAIL_ADDRESS_OID;
    public static final ObjectIdentifier UNSTRUCTURED_NAME_OID;
    public static final ObjectIdentifier CONTENT_TYPE_OID;
    public static final ObjectIdentifier MESSAGE_DIGEST_OID;
    public static final ObjectIdentifier SIGNING_TIME_OID;
    public static final ObjectIdentifier COUNTERSIGNATURE_OID;
    public static final ObjectIdentifier CHALLENGE_PASSWORD_OID;
    public static final ObjectIdentifier UNSTRUCTURED_ADDRESS_OID;
    public static final ObjectIdentifier EXTENDED_CERTIFICATE_ATTRIBUTES_OID;
    public static final ObjectIdentifier ISSUER_SERIALNUMBER_OID;
    public static final ObjectIdentifier EXTENSION_REQUEST_OID;
    public static final ObjectIdentifier SMIME_CAPABILITY_OID;
    public static final ObjectIdentifier SIGNING_CERTIFICATE_OID;
    public static final ObjectIdentifier SIGNATURE_TIMESTAMP_TOKEN_OID;
    public static final String EMAIL_ADDRESS_STR = "EmailAddress";
    public static final String UNSTRUCTURED_NAME_STR = "UnstructuredName";
    public static final String CONTENT_TYPE_STR = "ContentType";
    public static final String MESSAGE_DIGEST_STR = "MessageDigest";
    public static final String SIGNING_TIME_STR = "SigningTime";
    public static final String COUNTERSIGNATURE_STR = "Countersignature";
    public static final String CHALLENGE_PASSWORD_STR = "ChallengePassword";
    public static final String UNSTRUCTURED_ADDRESS_STR = "UnstructuredAddress";
    public static final String EXTENDED_CERTIFICATE_ATTRIBUTES_STR = "ExtendedCertificateAttributes";
    public static final String ISSUER_SERIALNUMBER_STR = "IssuerAndSerialNumber";
    private static final String RSA_PROPRIETARY_STR = "RSAProprietary";
    private static final String SMIME_SIGNING_DESC_STR = "SMIMESigningDesc";
    public static final String EXTENSION_REQUEST_STR = "ExtensionRequest";
    public static final String SMIME_CAPABILITY_STR = "SMIMECapability";
    public static final String SIGNING_CERTIFICATE_STR = "SigningCertificate";
    public static final String SIGNATURE_TIMESTAMP_TOKEN_STR = "SignatureTimestampToken";
    private static final Hashtable<String, ObjectIdentifier> NAME_OID_TABLE;
    private static final Hashtable<ObjectIdentifier, String> OID_NAME_TABLE;
    private static final Byte[][] PKCS9_VALUE_TAGS;
    private static final Class<?>[] VALUE_CLASSES;
    private static final boolean[] SINGLE_VALUED;
    private ObjectIdentifier oid;
    private int index;
    private Object value;
    
    public PKCS9Attribute(final ObjectIdentifier objectIdentifier, final Object o) throws IllegalArgumentException {
        this.init(objectIdentifier, o);
    }
    
    public PKCS9Attribute(final String s, final Object o) throws IllegalArgumentException {
        final ObjectIdentifier oid = getOID(s);
        if (oid == null) {
            throw new IllegalArgumentException("Unrecognized attribute name " + s + " constructing PKCS9Attribute.");
        }
        this.init(oid, o);
    }
    
    private void init(final ObjectIdentifier oid, final Object value) throws IllegalArgumentException {
        this.oid = oid;
        this.index = indexOf(oid, PKCS9Attribute.PKCS9_OIDS, 1);
        final Class<?> clazz = (this.index == -1) ? PKCS9Attribute.BYTE_ARRAY_CLASS : PKCS9Attribute.VALUE_CLASSES[this.index];
        if (!clazz.isInstance(value)) {
            throw new IllegalArgumentException("Wrong value class  for attribute " + oid + " constructing PKCS9Attribute; was " + value.getClass().toString() + ", should be " + clazz.toString());
        }
        this.value = value;
    }
    
    public PKCS9Attribute(final DerValue derValue) throws IOException {
        final DerInputStream derInputStream = new DerInputStream(derValue.toByteArray());
        final DerValue[] sequence = derInputStream.getSequence(2);
        if (derInputStream.available() != 0) {
            throw new IOException("Excess data parsing PKCS9Attribute");
        }
        if (sequence.length != 2) {
            throw new IOException("PKCS9Attribute doesn't have two components");
        }
        this.oid = sequence[0].getOID();
        final byte[] byteArray = sequence[1].toByteArray();
        final DerValue[] set = new DerInputStream(byteArray).getSet(1);
        this.index = indexOf(this.oid, PKCS9Attribute.PKCS9_OIDS, 1);
        if (this.index == -1) {
            if (PKCS9Attribute.debug != null) {
                PKCS9Attribute.debug.println("Unsupported signer attribute: " + this.oid);
            }
            this.value = byteArray;
            return;
        }
        if (PKCS9Attribute.SINGLE_VALUED[this.index] && set.length > 1) {
            this.throwSingleValuedException();
        }
        for (int i = 0; i < set.length; ++i) {
            final Byte b = new Byte(set[i].tag);
            if (indexOf(b, PKCS9Attribute.PKCS9_VALUE_TAGS[this.index], 0) == -1) {
                this.throwTagException(b);
            }
        }
        switch (this.index) {
            case 1:
            case 2:
            case 8: {
                final String[] value = new String[set.length];
                for (int j = 0; j < set.length; ++j) {
                    value[j] = set[j].getAsString();
                }
                this.value = value;
                break;
            }
            case 3: {
                this.value = set[0].getOID();
                break;
            }
            case 4: {
                this.value = set[0].getOctetString();
                break;
            }
            case 5: {
                this.value = new DerInputStream(set[0].toByteArray()).getUTCTime();
                break;
            }
            case 6: {
                final SignerInfo[] value2 = new SignerInfo[set.length];
                for (int k = 0; k < set.length; ++k) {
                    value2[k] = new SignerInfo(set[k].toDerInputStream());
                }
                this.value = value2;
                break;
            }
            case 7: {
                this.value = set[0].getAsString();
                break;
            }
            case 9: {
                throw new IOException("PKCS9 extended-certificate attribute not supported.");
            }
            case 10: {
                throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
            }
            case 11:
            case 12: {
                throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
            }
            case 13: {
                throw new IOException("PKCS9 attribute #13 not supported.");
            }
            case 14: {
                this.value = new CertificateExtensions(new DerInputStream(set[0].toByteArray()));
                break;
            }
            case 15: {
                throw new IOException("PKCS9 SMIMECapability attribute not supported.");
            }
            case 16: {
                this.value = new SigningCertificateInfo(set[0].toByteArray());
                break;
            }
            case 17: {
                this.value = set[0].toByteArray();
                break;
            }
        }
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOID(this.oid);
        switch (this.index) {
            case -1: {
                derOutputStream.write((byte[])this.value);
                break;
            }
            case 1:
            case 2: {
                final String[] array = (String[])this.value;
                final DerOutputStream[] array2 = new DerOutputStream[array.length];
                for (int i = 0; i < array.length; ++i) {
                    (array2[i] = new DerOutputStream()).putIA5String(array[i]);
                }
                derOutputStream.putOrderedSetOf((byte)49, array2);
                break;
            }
            case 3: {
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                derOutputStream2.putOID((ObjectIdentifier)this.value);
                derOutputStream.write((byte)49, derOutputStream2.toByteArray());
                break;
            }
            case 4: {
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.putOctetString((byte[])this.value);
                derOutputStream.write((byte)49, derOutputStream3.toByteArray());
                break;
            }
            case 5: {
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                derOutputStream4.putUTCTime((Date)this.value);
                derOutputStream.write((byte)49, derOutputStream4.toByteArray());
                break;
            }
            case 6: {
                derOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])this.value);
                break;
            }
            case 7: {
                final DerOutputStream derOutputStream5 = new DerOutputStream();
                derOutputStream5.putPrintableString((String)this.value);
                derOutputStream.write((byte)49, derOutputStream5.toByteArray());
                break;
            }
            case 8: {
                final String[] array3 = (String[])this.value;
                final DerOutputStream[] array4 = new DerOutputStream[array3.length];
                for (int j = 0; j < array3.length; ++j) {
                    (array4[j] = new DerOutputStream()).putPrintableString(array3[j]);
                }
                derOutputStream.putOrderedSetOf((byte)49, array4);
                break;
            }
            case 9: {
                throw new IOException("PKCS9 extended-certificate attribute not supported.");
            }
            case 10: {
                throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
            }
            case 11:
            case 12: {
                throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
            }
            case 13: {
                throw new IOException("PKCS9 attribute #13 not supported.");
            }
            case 14: {
                final DerOutputStream derOutputStream6 = new DerOutputStream();
                final CertificateExtensions certificateExtensions = (CertificateExtensions)this.value;
                try {
                    certificateExtensions.encode(derOutputStream6, true);
                }
                catch (final CertificateException ex) {
                    throw new IOException(ex.toString());
                }
                derOutputStream.write((byte)49, derOutputStream6.toByteArray());
                break;
            }
            case 15: {
                throw new IOException("PKCS9 attribute #15 not supported.");
            }
            case 16: {
                throw new IOException("PKCS9 SigningCertificate attribute not supported.");
            }
            case 17: {
                derOutputStream.write((byte)49, (byte[])this.value);
                break;
            }
        }
        final DerOutputStream derOutputStream7 = new DerOutputStream();
        derOutputStream7.write((byte)48, derOutputStream.toByteArray());
        outputStream.write(derOutputStream7.toByteArray());
    }
    
    public boolean isKnown() {
        return this.index != -1;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public boolean isSingleValued() {
        return this.index == -1 || PKCS9Attribute.SINGLE_VALUED[this.index];
    }
    
    public ObjectIdentifier getOID() {
        return this.oid;
    }
    
    public String getName() {
        return (this.index == -1) ? this.oid.toString() : PKCS9Attribute.OID_NAME_TABLE.get(PKCS9Attribute.PKCS9_OIDS[this.index]);
    }
    
    public static ObjectIdentifier getOID(final String s) {
        return PKCS9Attribute.NAME_OID_TABLE.get(s.toLowerCase(Locale.ENGLISH));
    }
    
    public static String getName(final ObjectIdentifier objectIdentifier) {
        return PKCS9Attribute.OID_NAME_TABLE.get(objectIdentifier);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("[");
        if (this.index == -1) {
            sb.append(this.oid.toString());
        }
        else {
            sb.append(PKCS9Attribute.OID_NAME_TABLE.get(PKCS9Attribute.PKCS9_OIDS[this.index]));
        }
        sb.append(": ");
        if (this.index == -1 || PKCS9Attribute.SINGLE_VALUED[this.index]) {
            if (this.value instanceof byte[]) {
                sb.append(new HexDumpEncoder().encodeBuffer((byte[])this.value));
            }
            else {
                sb.append(this.value.toString());
            }
            sb.append("]");
            return sb.toString();
        }
        int n = 1;
        final Object[] array = (Object[])this.value;
        for (int i = 0; i < array.length; ++i) {
            if (n != 0) {
                n = 0;
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i].toString());
        }
        return sb.toString();
    }
    
    static int indexOf(final Object o, final Object[] array, final int n) {
        for (int i = n; i < array.length; ++i) {
            if (o.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
    
    private void throwSingleValuedException() throws IOException {
        throw new IOException("Single-value attribute " + this.oid + " (" + this.getName() + ") has multiple values.");
    }
    
    private void throwTagException(final Byte b) throws IOException {
        final Byte[] array = PKCS9Attribute.PKCS9_VALUE_TAGS[this.index];
        final StringBuffer sb = new StringBuffer(100);
        sb.append("Value of attribute ");
        sb.append(this.oid.toString());
        sb.append(" (");
        sb.append(this.getName());
        sb.append(") has wrong tag: ");
        sb.append(b.toString());
        sb.append(".  Expected tags: ");
        sb.append(array[0].toString());
        for (int i = 1; i < array.length; ++i) {
            sb.append(", ");
            sb.append(array[i].toString());
        }
        sb.append(".");
        throw new IOException(sb.toString());
    }
    
    static {
        debug = Debug.getInstance("jar");
        PKCS9_OIDS = new ObjectIdentifier[18];
        for (int i = 1; i < PKCS9Attribute.PKCS9_OIDS.length - 2; ++i) {
            PKCS9Attribute.PKCS9_OIDS[i] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, i });
        }
        PKCS9Attribute.PKCS9_OIDS[PKCS9Attribute.PKCS9_OIDS.length - 2] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 12 });
        PKCS9Attribute.PKCS9_OIDS[PKCS9Attribute.PKCS9_OIDS.length - 1] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 14 });
        try {
            BYTE_ARRAY_CLASS = Class.forName("[B");
        }
        catch (final ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex.toString());
        }
        EMAIL_ADDRESS_OID = PKCS9Attribute.PKCS9_OIDS[1];
        UNSTRUCTURED_NAME_OID = PKCS9Attribute.PKCS9_OIDS[2];
        CONTENT_TYPE_OID = PKCS9Attribute.PKCS9_OIDS[3];
        MESSAGE_DIGEST_OID = PKCS9Attribute.PKCS9_OIDS[4];
        SIGNING_TIME_OID = PKCS9Attribute.PKCS9_OIDS[5];
        COUNTERSIGNATURE_OID = PKCS9Attribute.PKCS9_OIDS[6];
        CHALLENGE_PASSWORD_OID = PKCS9Attribute.PKCS9_OIDS[7];
        UNSTRUCTURED_ADDRESS_OID = PKCS9Attribute.PKCS9_OIDS[8];
        EXTENDED_CERTIFICATE_ATTRIBUTES_OID = PKCS9Attribute.PKCS9_OIDS[9];
        ISSUER_SERIALNUMBER_OID = PKCS9Attribute.PKCS9_OIDS[10];
        EXTENSION_REQUEST_OID = PKCS9Attribute.PKCS9_OIDS[14];
        SMIME_CAPABILITY_OID = PKCS9Attribute.PKCS9_OIDS[15];
        SIGNING_CERTIFICATE_OID = PKCS9Attribute.PKCS9_OIDS[16];
        SIGNATURE_TIMESTAMP_TOKEN_OID = PKCS9Attribute.PKCS9_OIDS[17];
        (NAME_OID_TABLE = new Hashtable<String, ObjectIdentifier>(18)).put("emailaddress", PKCS9Attribute.PKCS9_OIDS[1]);
        PKCS9Attribute.NAME_OID_TABLE.put("unstructuredname", PKCS9Attribute.PKCS9_OIDS[2]);
        PKCS9Attribute.NAME_OID_TABLE.put("contenttype", PKCS9Attribute.PKCS9_OIDS[3]);
        PKCS9Attribute.NAME_OID_TABLE.put("messagedigest", PKCS9Attribute.PKCS9_OIDS[4]);
        PKCS9Attribute.NAME_OID_TABLE.put("signingtime", PKCS9Attribute.PKCS9_OIDS[5]);
        PKCS9Attribute.NAME_OID_TABLE.put("countersignature", PKCS9Attribute.PKCS9_OIDS[6]);
        PKCS9Attribute.NAME_OID_TABLE.put("challengepassword", PKCS9Attribute.PKCS9_OIDS[7]);
        PKCS9Attribute.NAME_OID_TABLE.put("unstructuredaddress", PKCS9Attribute.PKCS9_OIDS[8]);
        PKCS9Attribute.NAME_OID_TABLE.put("extendedcertificateattributes", PKCS9Attribute.PKCS9_OIDS[9]);
        PKCS9Attribute.NAME_OID_TABLE.put("issuerandserialnumber", PKCS9Attribute.PKCS9_OIDS[10]);
        PKCS9Attribute.NAME_OID_TABLE.put("rsaproprietary", PKCS9Attribute.PKCS9_OIDS[11]);
        PKCS9Attribute.NAME_OID_TABLE.put("rsaproprietary", PKCS9Attribute.PKCS9_OIDS[12]);
        PKCS9Attribute.NAME_OID_TABLE.put("signingdescription", PKCS9Attribute.PKCS9_OIDS[13]);
        PKCS9Attribute.NAME_OID_TABLE.put("extensionrequest", PKCS9Attribute.PKCS9_OIDS[14]);
        PKCS9Attribute.NAME_OID_TABLE.put("smimecapability", PKCS9Attribute.PKCS9_OIDS[15]);
        PKCS9Attribute.NAME_OID_TABLE.put("signingcertificate", PKCS9Attribute.PKCS9_OIDS[16]);
        PKCS9Attribute.NAME_OID_TABLE.put("signaturetimestamptoken", PKCS9Attribute.PKCS9_OIDS[17]);
        (OID_NAME_TABLE = new Hashtable<ObjectIdentifier, String>(16)).put(PKCS9Attribute.PKCS9_OIDS[1], "EmailAddress");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[2], "UnstructuredName");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[3], "ContentType");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[4], "MessageDigest");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[5], "SigningTime");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[6], "Countersignature");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[7], "ChallengePassword");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[8], "UnstructuredAddress");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[9], "ExtendedCertificateAttributes");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[10], "IssuerAndSerialNumber");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[11], "RSAProprietary");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[12], "RSAProprietary");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[13], "SMIMESigningDesc");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[14], "ExtensionRequest");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[15], "SMIMECapability");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[16], "SigningCertificate");
        PKCS9Attribute.OID_NAME_TABLE.put(PKCS9Attribute.PKCS9_OIDS[17], "SignatureTimestampToken");
        PKCS9_VALUE_TAGS = new Byte[][] { null, { new Byte((byte)22) }, { new Byte((byte)22), new Byte((byte)19) }, { new Byte((byte)6) }, { new Byte((byte)4) }, { new Byte((byte)23) }, { new Byte((byte)48) }, { new Byte((byte)19), new Byte((byte)20) }, { new Byte((byte)19), new Byte((byte)20) }, { new Byte((byte)49) }, { new Byte((byte)48) }, null, null, null, { new Byte((byte)48) }, { new Byte((byte)48) }, { new Byte((byte)48) }, { new Byte((byte)48) } };
        VALUE_CLASSES = new Class[18];
        try {
            final Class<?> forName = Class.forName("[Ljava.lang.String;");
            PKCS9Attribute.VALUE_CLASSES[0] = null;
            PKCS9Attribute.VALUE_CLASSES[1] = forName;
            PKCS9Attribute.VALUE_CLASSES[2] = forName;
            PKCS9Attribute.VALUE_CLASSES[3] = Class.forName("sun.security.util.ObjectIdentifier");
            PKCS9Attribute.VALUE_CLASSES[4] = PKCS9Attribute.BYTE_ARRAY_CLASS;
            PKCS9Attribute.VALUE_CLASSES[5] = Class.forName("java.util.Date");
            PKCS9Attribute.VALUE_CLASSES[6] = Class.forName("[Lsun.security.pkcs.SignerInfo;");
            PKCS9Attribute.VALUE_CLASSES[7] = Class.forName("java.lang.String");
            PKCS9Attribute.VALUE_CLASSES[8] = forName;
            PKCS9Attribute.VALUE_CLASSES[9] = null;
            PKCS9Attribute.VALUE_CLASSES[10] = null;
            PKCS9Attribute.VALUE_CLASSES[11] = null;
            PKCS9Attribute.VALUE_CLASSES[12] = null;
            PKCS9Attribute.VALUE_CLASSES[13] = null;
            PKCS9Attribute.VALUE_CLASSES[14] = Class.forName("sun.security.x509.CertificateExtensions");
            PKCS9Attribute.VALUE_CLASSES[15] = null;
            PKCS9Attribute.VALUE_CLASSES[16] = null;
            PKCS9Attribute.VALUE_CLASSES[17] = PKCS9Attribute.BYTE_ARRAY_CLASS;
        }
        catch (final ClassNotFoundException ex2) {
            throw new ExceptionInInitializerError(ex2.toString());
        }
        SINGLE_VALUED = new boolean[] { false, false, false, true, true, true, false, true, false, false, true, false, false, false, true, true, true, true };
    }
}
