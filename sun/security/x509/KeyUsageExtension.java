package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;

public class KeyUsageExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.KeyUsage";
    public static final String NAME = "KeyUsage";
    public static final String DIGITAL_SIGNATURE = "digital_signature";
    public static final String NON_REPUDIATION = "non_repudiation";
    public static final String KEY_ENCIPHERMENT = "key_encipherment";
    public static final String DATA_ENCIPHERMENT = "data_encipherment";
    public static final String KEY_AGREEMENT = "key_agreement";
    public static final String KEY_CERTSIGN = "key_certsign";
    public static final String CRL_SIGN = "crl_sign";
    public static final String ENCIPHER_ONLY = "encipher_only";
    public static final String DECIPHER_ONLY = "decipher_only";
    private boolean[] bitString;
    
    private void encodeThis() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    private boolean isSet(final int n) {
        return n < this.bitString.length && this.bitString[n];
    }
    
    private void set(final int n, final boolean b) {
        if (n >= this.bitString.length) {
            final boolean[] bitString = new boolean[n + 1];
            System.arraycopy(this.bitString, 0, bitString, 0, this.bitString.length);
            this.bitString = bitString;
        }
        this.bitString[n] = b;
    }
    
    public KeyUsageExtension(final byte[] array) throws IOException {
        this.bitString = new BitArray(array.length * 8, array).toBooleanArray();
        this.extensionId = PKIXExtensions.KeyUsage_Id;
        this.critical = true;
        this.encodeThis();
    }
    
    public KeyUsageExtension(final boolean[] bitString) throws IOException {
        this.bitString = bitString;
        this.extensionId = PKIXExtensions.KeyUsage_Id;
        this.critical = true;
        this.encodeThis();
    }
    
    public KeyUsageExtension(final BitArray bitArray) throws IOException {
        this.bitString = bitArray.toBooleanArray();
        this.extensionId = PKIXExtensions.KeyUsage_Id;
        this.critical = true;
        this.encodeThis();
    }
    
    public KeyUsageExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.KeyUsage_Id;
        this.critical = b;
        final byte[] extensionValue = (byte[])o;
        if (extensionValue[0] == 4) {
            this.extensionValue = new DerValue(extensionValue).getOctetString();
        }
        else {
            this.extensionValue = extensionValue;
        }
        this.bitString = new DerValue(this.extensionValue).getUnalignedBitString().toBooleanArray();
    }
    
    public KeyUsageExtension() {
        this.extensionId = PKIXExtensions.KeyUsage_Id;
        this.critical = true;
        this.bitString = new boolean[0];
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Boolean)) {
            throw new IOException("Attribute must be of type Boolean.");
        }
        final boolean booleanValue = (boolean)o;
        if (s.equalsIgnoreCase("digital_signature")) {
            this.set(0, booleanValue);
        }
        else if (s.equalsIgnoreCase("non_repudiation")) {
            this.set(1, booleanValue);
        }
        else if (s.equalsIgnoreCase("key_encipherment")) {
            this.set(2, booleanValue);
        }
        else if (s.equalsIgnoreCase("data_encipherment")) {
            this.set(3, booleanValue);
        }
        else if (s.equalsIgnoreCase("key_agreement")) {
            this.set(4, booleanValue);
        }
        else if (s.equalsIgnoreCase("key_certsign")) {
            this.set(5, booleanValue);
        }
        else if (s.equalsIgnoreCase("crl_sign")) {
            this.set(6, booleanValue);
        }
        else if (s.equalsIgnoreCase("encipher_only")) {
            this.set(7, booleanValue);
        }
        else {
            if (!s.equalsIgnoreCase("decipher_only")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
            }
            this.set(8, booleanValue);
        }
        this.encodeThis();
    }
    
    @Override
    public Boolean get(final String s) throws IOException {
        if (s.equalsIgnoreCase("digital_signature")) {
            return this.isSet(0);
        }
        if (s.equalsIgnoreCase("non_repudiation")) {
            return this.isSet(1);
        }
        if (s.equalsIgnoreCase("key_encipherment")) {
            return this.isSet(2);
        }
        if (s.equalsIgnoreCase("data_encipherment")) {
            return this.isSet(3);
        }
        if (s.equalsIgnoreCase("key_agreement")) {
            return this.isSet(4);
        }
        if (s.equalsIgnoreCase("key_certsign")) {
            return this.isSet(5);
        }
        if (s.equalsIgnoreCase("crl_sign")) {
            return this.isSet(6);
        }
        if (s.equalsIgnoreCase("encipher_only")) {
            return this.isSet(7);
        }
        if (s.equalsIgnoreCase("decipher_only")) {
            return this.isSet(8);
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("digital_signature")) {
            this.set(0, false);
        }
        else if (s.equalsIgnoreCase("non_repudiation")) {
            this.set(1, false);
        }
        else if (s.equalsIgnoreCase("key_encipherment")) {
            this.set(2, false);
        }
        else if (s.equalsIgnoreCase("data_encipherment")) {
            this.set(3, false);
        }
        else if (s.equalsIgnoreCase("key_agreement")) {
            this.set(4, false);
        }
        else if (s.equalsIgnoreCase("key_certsign")) {
            this.set(5, false);
        }
        else if (s.equalsIgnoreCase("crl_sign")) {
            this.set(6, false);
        }
        else if (s.equalsIgnoreCase("encipher_only")) {
            this.set(7, false);
        }
        else {
            if (!s.equalsIgnoreCase("decipher_only")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
            }
            this.set(8, false);
        }
        this.encodeThis();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("KeyUsage [\n");
        if (this.isSet(0)) {
            sb.append("  DigitalSignature\n");
        }
        if (this.isSet(1)) {
            sb.append("  Non_repudiation\n");
        }
        if (this.isSet(2)) {
            sb.append("  Key_Encipherment\n");
        }
        if (this.isSet(3)) {
            sb.append("  Data_Encipherment\n");
        }
        if (this.isSet(4)) {
            sb.append("  Key_Agreement\n");
        }
        if (this.isSet(5)) {
            sb.append("  Key_CertSign\n");
        }
        if (this.isSet(6)) {
            sb.append("  Crl_Sign\n");
        }
        if (this.isSet(7)) {
            sb.append("  Encipher_Only\n");
        }
        if (this.isSet(8)) {
            sb.append("  Decipher_Only\n");
        }
        sb.append("]\n");
        return sb.toString();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.KeyUsage_Id;
            this.critical = true;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("digital_signature");
        attributeNameEnumeration.addElement("non_repudiation");
        attributeNameEnumeration.addElement("key_encipherment");
        attributeNameEnumeration.addElement("data_encipherment");
        attributeNameEnumeration.addElement("key_agreement");
        attributeNameEnumeration.addElement("key_certsign");
        attributeNameEnumeration.addElement("crl_sign");
        attributeNameEnumeration.addElement("encipher_only");
        attributeNameEnumeration.addElement("decipher_only");
        return attributeNameEnumeration.elements();
    }
    
    public boolean[] getBits() {
        return this.bitString.clone();
    }
    
    @Override
    public String getName() {
        return "KeyUsage";
    }
}
