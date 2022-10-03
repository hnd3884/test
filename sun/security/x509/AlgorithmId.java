package sun.security.x509;

import sun.security.rsa.PSSParameters;
import java.security.spec.MGF1ParameterSpec;
import java.security.Key;
import sun.security.util.KeyUtil;
import java.security.spec.PSSParameterSpec;
import java.security.PrivateKey;
import java.security.spec.InvalidParameterSpecException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Enumeration;
import java.security.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.security.Security;
import sun.security.util.DerInputStream;
import java.io.OutputStream;
import sun.security.util.DerOutputStream;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.util.Map;
import sun.security.util.DerValue;
import java.security.AlgorithmParameters;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerEncoder;
import java.io.Serializable;

public class AlgorithmId implements Serializable, DerEncoder
{
    private static final long serialVersionUID = 7205873507486557157L;
    private ObjectIdentifier algid;
    private AlgorithmParameters algParams;
    private boolean constructedFromDer;
    protected DerValue params;
    private static boolean initOidTable;
    private static Map<String, ObjectIdentifier> oidTable;
    private static final Map<ObjectIdentifier, String> nameTable;
    public static final ObjectIdentifier MD2_oid;
    public static final ObjectIdentifier MD5_oid;
    public static final ObjectIdentifier SHA_oid;
    public static final ObjectIdentifier SHA224_oid;
    public static final ObjectIdentifier SHA256_oid;
    public static final ObjectIdentifier SHA384_oid;
    public static final ObjectIdentifier SHA512_oid;
    public static final ObjectIdentifier SHA512_224_oid;
    public static final ObjectIdentifier SHA512_256_oid;
    private static final int[] DH_data;
    private static final int[] DH_PKIX_data;
    private static final int[] DSA_OIW_data;
    private static final int[] DSA_PKIX_data;
    private static final int[] RSA_data;
    public static final ObjectIdentifier DH_oid;
    public static final ObjectIdentifier DH_PKIX_oid;
    public static final ObjectIdentifier DSA_oid;
    public static final ObjectIdentifier DSA_OIW_oid;
    public static final ObjectIdentifier EC_oid;
    public static final ObjectIdentifier ECDH_oid;
    public static final ObjectIdentifier RSA_oid;
    public static final ObjectIdentifier RSAEncryption_oid;
    public static final ObjectIdentifier RSAES_OAEP_oid;
    public static final ObjectIdentifier mgf1_oid;
    public static final ObjectIdentifier RSASSA_PSS_oid;
    public static final ObjectIdentifier AES_oid;
    private static final int[] md2WithRSAEncryption_data;
    private static final int[] md5WithRSAEncryption_data;
    private static final int[] sha1WithRSAEncryption_data;
    private static final int[] sha1WithRSAEncryption_OIW_data;
    private static final int[] sha224WithRSAEncryption_data;
    private static final int[] sha256WithRSAEncryption_data;
    private static final int[] sha384WithRSAEncryption_data;
    private static final int[] sha512WithRSAEncryption_data;
    private static final int[] shaWithDSA_OIW_data;
    private static final int[] sha1WithDSA_OIW_data;
    private static final int[] dsaWithSHA1_PKIX_data;
    public static final ObjectIdentifier md2WithRSAEncryption_oid;
    public static final ObjectIdentifier md5WithRSAEncryption_oid;
    public static final ObjectIdentifier sha1WithRSAEncryption_oid;
    public static final ObjectIdentifier sha1WithRSAEncryption_OIW_oid;
    public static final ObjectIdentifier sha224WithRSAEncryption_oid;
    public static final ObjectIdentifier sha256WithRSAEncryption_oid;
    public static final ObjectIdentifier sha384WithRSAEncryption_oid;
    public static final ObjectIdentifier sha512WithRSAEncryption_oid;
    public static final ObjectIdentifier sha512_224WithRSAEncryption_oid;
    public static final ObjectIdentifier sha512_256WithRSAEncryption_oid;
    public static final ObjectIdentifier shaWithDSA_OIW_oid;
    public static final ObjectIdentifier sha1WithDSA_OIW_oid;
    public static final ObjectIdentifier sha1WithDSA_oid;
    public static final ObjectIdentifier sha224WithDSA_oid;
    public static final ObjectIdentifier sha256WithDSA_oid;
    public static final ObjectIdentifier sha1WithECDSA_oid;
    public static final ObjectIdentifier sha224WithECDSA_oid;
    public static final ObjectIdentifier sha256WithECDSA_oid;
    public static final ObjectIdentifier sha384WithECDSA_oid;
    public static final ObjectIdentifier sha512WithECDSA_oid;
    public static final ObjectIdentifier specifiedWithECDSA_oid;
    public static final ObjectIdentifier pbeWithMD5AndDES_oid;
    public static final ObjectIdentifier pbeWithMD5AndRC2_oid;
    public static final ObjectIdentifier pbeWithSHA1AndDES_oid;
    public static final ObjectIdentifier pbeWithSHA1AndRC2_oid;
    public static ObjectIdentifier pbeWithSHA1AndDESede_oid;
    public static ObjectIdentifier pbeWithSHA1AndRC2_40_oid;
    
    @Deprecated
    public AlgorithmId() {
        this.constructedFromDer = true;
    }
    
    public AlgorithmId(final ObjectIdentifier algid) {
        this.constructedFromDer = true;
        this.algid = algid;
    }
    
    public AlgorithmId(final ObjectIdentifier algid, final AlgorithmParameters algParams) {
        this.constructedFromDer = true;
        this.algid = algid;
        this.algParams = algParams;
        this.constructedFromDer = false;
    }
    
    private AlgorithmId(final ObjectIdentifier algid, final DerValue params) throws IOException {
        this.constructedFromDer = true;
        this.algid = algid;
        this.params = params;
        if (this.params != null) {
            this.decodeParams();
        }
    }
    
    protected void decodeParams() throws IOException {
        final String name = this.getName();
        try {
            this.algParams = AlgorithmParameters.getInstance(name);
        }
        catch (final NoSuchAlgorithmException ex) {
            this.algParams = null;
            return;
        }
        this.algParams.init(this.params.toByteArray());
    }
    
    public final void encode(final DerOutputStream derOutputStream) throws IOException {
        this.derEncode(derOutputStream);
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.putOID(this.algid);
        if (!this.constructedFromDer) {
            if (this.algParams != null) {
                this.params = new DerValue(this.algParams.getEncoded());
            }
            else {
                this.params = null;
            }
        }
        if (this.params == null) {
            if (!this.algid.equals(AlgorithmId.RSASSA_PSS_oid)) {
                derOutputStream.putNull();
            }
        }
        else {
            derOutputStream.putDerValue(this.params);
        }
        derOutputStream2.write((byte)48, derOutputStream);
        outputStream.write(derOutputStream2.toByteArray());
    }
    
    public final byte[] encode() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.derEncode(derOutputStream);
        return derOutputStream.toByteArray();
    }
    
    public final ObjectIdentifier getOID() {
        return this.algid;
    }
    
    public String getName() {
        String sigAlg = AlgorithmId.nameTable.get(this.algid);
        if (sigAlg != null) {
            return sigAlg;
        }
        if (this.params != null && this.algid.equals((Object)AlgorithmId.specifiedWithECDSA_oid)) {
            try {
                sigAlg = makeSigAlg(parse(new DerValue(this.params.toByteArray())).getName(), "EC");
            }
            catch (final IOException ex) {}
        }
        return (sigAlg == null) ? this.algid.toString() : sigAlg;
    }
    
    public AlgorithmParameters getParameters() {
        return this.algParams;
    }
    
    public byte[] getEncodedParams() throws IOException {
        return (byte[])((this.params == null || this.algid.equals(AlgorithmId.specifiedWithECDSA_oid)) ? null : this.params.toByteArray());
    }
    
    public boolean equals(final AlgorithmId algorithmId) {
        final boolean b = (this.params == null) ? (algorithmId.params == null) : this.params.equals(algorithmId.params);
        return this.algid.equals((Object)algorithmId.algid) && b;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof AlgorithmId) {
            return this.equals((AlgorithmId)o);
        }
        return o instanceof ObjectIdentifier && this.equals((ObjectIdentifier)o);
    }
    
    public final boolean equals(final ObjectIdentifier objectIdentifier) {
        return this.algid.equals((Object)objectIdentifier);
    }
    
    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.algid.toString());
        sb.append(this.paramsToString());
        return sb.toString().hashCode();
    }
    
    protected String paramsToString() {
        if (this.params == null) {
            return "";
        }
        if (this.algParams != null) {
            return this.algParams.toString();
        }
        return ", params unparsed";
    }
    
    @Override
    public String toString() {
        return this.getName() + this.paramsToString();
    }
    
    public static AlgorithmId parse(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("algid parse error, not a sequence");
        }
        final DerInputStream derInputStream = derValue.toDerInputStream();
        final ObjectIdentifier oid = derInputStream.getOID();
        DerValue derValue2;
        if (derInputStream.available() == 0) {
            derValue2 = null;
        }
        else {
            derValue2 = derInputStream.getDerValue();
            if (derValue2.tag == 5) {
                if (derValue2.length() != 0) {
                    throw new IOException("invalid NULL");
                }
                derValue2 = null;
            }
            if (derInputStream.available() != 0) {
                throw new IOException("Invalid AlgorithmIdentifier: extra data");
            }
        }
        return new AlgorithmId(oid, derValue2);
    }
    
    @Deprecated
    public static AlgorithmId getAlgorithmId(final String s) throws NoSuchAlgorithmException {
        return get(s);
    }
    
    public static AlgorithmId get(final String s) throws NoSuchAlgorithmException {
        ObjectIdentifier algOID;
        try {
            algOID = algOID(s);
        }
        catch (final IOException ex) {
            throw new NoSuchAlgorithmException("Invalid ObjectIdentifier " + s);
        }
        if (algOID == null) {
            throw new NoSuchAlgorithmException("unrecognized algorithm name: " + s);
        }
        return new AlgorithmId(algOID);
    }
    
    public static AlgorithmId get(final AlgorithmParameters algorithmParameters) throws NoSuchAlgorithmException {
        final String algorithm = algorithmParameters.getAlgorithm();
        ObjectIdentifier algOID;
        try {
            algOID = algOID(algorithm);
        }
        catch (final IOException ex) {
            throw new NoSuchAlgorithmException("Invalid ObjectIdentifier " + algorithm);
        }
        if (algOID == null) {
            throw new NoSuchAlgorithmException("unrecognized algorithm name: " + algorithm);
        }
        return new AlgorithmId(algOID, algorithmParameters);
    }
    
    private static ObjectIdentifier algOID(final String s) throws IOException {
        if (s.indexOf(46) != -1) {
            if (s.startsWith("OID.")) {
                return new ObjectIdentifier(s.substring("OID.".length()));
            }
            return new ObjectIdentifier(s);
        }
        else {
            if (s.equalsIgnoreCase("MD5")) {
                return AlgorithmId.MD5_oid;
            }
            if (s.equalsIgnoreCase("MD2")) {
                return AlgorithmId.MD2_oid;
            }
            if (s.equalsIgnoreCase("SHA") || s.equalsIgnoreCase("SHA1") || s.equalsIgnoreCase("SHA-1")) {
                return AlgorithmId.SHA_oid;
            }
            if (s.equalsIgnoreCase("SHA-256") || s.equalsIgnoreCase("SHA256")) {
                return AlgorithmId.SHA256_oid;
            }
            if (s.equalsIgnoreCase("SHA-384") || s.equalsIgnoreCase("SHA384")) {
                return AlgorithmId.SHA384_oid;
            }
            if (s.equalsIgnoreCase("SHA-512") || s.equalsIgnoreCase("SHA512")) {
                return AlgorithmId.SHA512_oid;
            }
            if (s.equalsIgnoreCase("SHA-224") || s.equalsIgnoreCase("SHA224")) {
                return AlgorithmId.SHA224_oid;
            }
            if (s.equalsIgnoreCase("SHA-512/224") || s.equalsIgnoreCase("SHA512/224")) {
                return AlgorithmId.SHA512_224_oid;
            }
            if (s.equalsIgnoreCase("SHA-512/256") || s.equalsIgnoreCase("SHA512/256")) {
                return AlgorithmId.SHA512_256_oid;
            }
            if (s.equalsIgnoreCase("RSA")) {
                return AlgorithmId.RSAEncryption_oid;
            }
            if (s.equalsIgnoreCase("RSASSA-PSS")) {
                return AlgorithmId.RSASSA_PSS_oid;
            }
            if (s.equalsIgnoreCase("RSAES-OAEP")) {
                return AlgorithmId.RSAES_OAEP_oid;
            }
            if (s.equalsIgnoreCase("Diffie-Hellman") || s.equalsIgnoreCase("DH")) {
                return AlgorithmId.DH_oid;
            }
            if (s.equalsIgnoreCase("DSA")) {
                return AlgorithmId.DSA_oid;
            }
            if (s.equalsIgnoreCase("EC")) {
                return AlgorithmId.EC_oid;
            }
            if (s.equalsIgnoreCase("ECDH")) {
                return AlgorithmId.ECDH_oid;
            }
            if (s.equalsIgnoreCase("AES")) {
                return AlgorithmId.AES_oid;
            }
            if (s.equalsIgnoreCase("MD5withRSA") || s.equalsIgnoreCase("MD5/RSA")) {
                return AlgorithmId.md5WithRSAEncryption_oid;
            }
            if (s.equalsIgnoreCase("MD2withRSA") || s.equalsIgnoreCase("MD2/RSA")) {
                return AlgorithmId.md2WithRSAEncryption_oid;
            }
            if (s.equalsIgnoreCase("SHAwithDSA") || s.equalsIgnoreCase("SHA1withDSA") || s.equalsIgnoreCase("SHA/DSA") || s.equalsIgnoreCase("SHA1/DSA") || s.equalsIgnoreCase("DSAWithSHA1") || s.equalsIgnoreCase("DSS") || s.equalsIgnoreCase("SHA-1/DSA")) {
                return AlgorithmId.sha1WithDSA_oid;
            }
            if (s.equalsIgnoreCase("SHA224WithDSA")) {
                return AlgorithmId.sha224WithDSA_oid;
            }
            if (s.equalsIgnoreCase("SHA256WithDSA")) {
                return AlgorithmId.sha256WithDSA_oid;
            }
            if (s.equalsIgnoreCase("SHA1WithRSA") || s.equalsIgnoreCase("SHA1/RSA")) {
                return AlgorithmId.sha1WithRSAEncryption_oid;
            }
            if (s.equalsIgnoreCase("SHA1withECDSA") || s.equalsIgnoreCase("ECDSA")) {
                return AlgorithmId.sha1WithECDSA_oid;
            }
            if (s.equalsIgnoreCase("SHA224withECDSA")) {
                return AlgorithmId.sha224WithECDSA_oid;
            }
            if (s.equalsIgnoreCase("SHA256withECDSA")) {
                return AlgorithmId.sha256WithECDSA_oid;
            }
            if (s.equalsIgnoreCase("SHA384withECDSA")) {
                return AlgorithmId.sha384WithECDSA_oid;
            }
            if (s.equalsIgnoreCase("SHA512withECDSA")) {
                return AlgorithmId.sha512WithECDSA_oid;
            }
            if (!AlgorithmId.initOidTable) {
                final Provider[] providers = Security.getProviders();
                for (int i = 0; i < providers.length; ++i) {
                    final Enumeration<Object> keys = providers[i].keys();
                    while (keys.hasMoreElements()) {
                        final String s2 = keys.nextElement();
                        final String upperCase = s2.toUpperCase(Locale.ENGLISH);
                        final int index;
                        if (upperCase.startsWith("ALG.ALIAS") && (index = upperCase.indexOf("OID.", 0)) != -1) {
                            final int n = index + "OID.".length();
                            if (n == s2.length()) {
                                break;
                            }
                            if (AlgorithmId.oidTable == null) {
                                AlgorithmId.oidTable = new HashMap<String, ObjectIdentifier>();
                            }
                            final String substring = s2.substring(n);
                            String s3 = providers[i].getProperty(s2);
                            if (s3 != null) {
                                s3 = s3.toUpperCase(Locale.ENGLISH);
                            }
                            if (s3 == null || AlgorithmId.oidTable.get(s3) != null) {
                                continue;
                            }
                            AlgorithmId.oidTable.put(s3, new ObjectIdentifier(substring));
                        }
                    }
                }
                if (AlgorithmId.oidTable == null) {
                    AlgorithmId.oidTable = Collections.emptyMap();
                }
                AlgorithmId.initOidTable = true;
            }
            return AlgorithmId.oidTable.get(s.toUpperCase(Locale.ENGLISH));
        }
    }
    
    private static ObjectIdentifier oid(final int... array) {
        return ObjectIdentifier.newInternal(array);
    }
    
    public static String makeSigAlg(String replace, String s) {
        replace = replace.replace("-", "");
        if (s.equalsIgnoreCase("EC")) {
            s = "ECDSA";
        }
        return replace + "with" + s;
    }
    
    public static String getEncAlgFromSigAlg(String upperCase) {
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        final int index = upperCase.indexOf("WITH");
        String s = null;
        if (index > 0) {
            final int index2 = upperCase.indexOf("AND", index + 4);
            if (index2 > 0) {
                s = upperCase.substring(index + 4, index2);
            }
            else {
                s = upperCase.substring(index + 4);
            }
            if (s.equalsIgnoreCase("ECDSA")) {
                s = "EC";
            }
        }
        return s;
    }
    
    public static String getDigAlgFromSigAlg(String upperCase) {
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        final int index = upperCase.indexOf("WITH");
        if (index > 0) {
            return upperCase.substring(0, index);
        }
        return null;
    }
    
    public static AlgorithmId getWithParameterSpec(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws NoSuchAlgorithmException {
        if (algorithmParameterSpec == null) {
            return get(s);
        }
        if (algorithmParameterSpec == PSSParamsHolder.PSS_256_SPEC) {
            return PSSParamsHolder.PSS_256_ID;
        }
        if (algorithmParameterSpec == PSSParamsHolder.PSS_384_SPEC) {
            return PSSParamsHolder.PSS_384_ID;
        }
        if (algorithmParameterSpec == PSSParamsHolder.PSS_512_SPEC) {
            return PSSParamsHolder.PSS_512_ID;
        }
        try {
            final AlgorithmParameters instance = AlgorithmParameters.getInstance(s);
            instance.init(algorithmParameterSpec);
            return get(instance);
        }
        catch (final InvalidParameterSpecException | NoSuchAlgorithmException ex) {
            throw new ProviderException((Throwable)ex);
        }
    }
    
    public static PSSParameterSpec getDefaultAlgorithmParameterSpec(final String s, final PrivateKey privateKey) {
        if (!s.equalsIgnoreCase("RSASSA-PSS")) {
            return null;
        }
        final String ifcFfcStrength = ifcFfcStrength(KeyUtil.getKeySize(privateKey));
        switch (ifcFfcStrength) {
            case "SHA256": {
                return PSSParamsHolder.PSS_256_SPEC;
            }
            case "SHA384": {
                return PSSParamsHolder.PSS_384_SPEC;
            }
            case "SHA512": {
                return PSSParamsHolder.PSS_512_SPEC;
            }
            default: {
                throw new AssertionError((Object)"Should not happen");
            }
        }
    }
    
    private static String ifcFfcStrength(final int n) {
        if (n > 7680) {
            return "SHA512";
        }
        if (n > 3072) {
            return "SHA384";
        }
        return "SHA256";
    }
    
    static {
        AlgorithmId.initOidTable = false;
        MD2_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 2, 2 });
        MD5_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 2, 5 });
        SHA_oid = ObjectIdentifier.newInternal(new int[] { 1, 3, 14, 3, 2, 26 });
        SHA224_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 4 });
        SHA256_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 1 });
        SHA384_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 2 });
        SHA512_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 3 });
        SHA512_224_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 5 });
        SHA512_256_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 6 });
        DH_data = new int[] { 1, 2, 840, 113549, 1, 3, 1 };
        DH_PKIX_data = new int[] { 1, 2, 840, 10046, 2, 1 };
        DSA_OIW_data = new int[] { 1, 3, 14, 3, 2, 12 };
        DSA_PKIX_data = new int[] { 1, 2, 840, 10040, 4, 1 };
        RSA_data = new int[] { 2, 5, 8, 1, 1 };
        EC_oid = oid(1, 2, 840, 10045, 2, 1);
        ECDH_oid = oid(1, 3, 132, 1, 12);
        RSAEncryption_oid = oid(1, 2, 840, 113549, 1, 1, 1);
        RSAES_OAEP_oid = oid(1, 2, 840, 113549, 1, 1, 7);
        mgf1_oid = oid(1, 2, 840, 113549, 1, 1, 8);
        RSASSA_PSS_oid = oid(1, 2, 840, 113549, 1, 1, 10);
        AES_oid = oid(2, 16, 840, 1, 101, 3, 4, 1);
        md2WithRSAEncryption_data = new int[] { 1, 2, 840, 113549, 1, 1, 2 };
        md5WithRSAEncryption_data = new int[] { 1, 2, 840, 113549, 1, 1, 4 };
        sha1WithRSAEncryption_data = new int[] { 1, 2, 840, 113549, 1, 1, 5 };
        sha1WithRSAEncryption_OIW_data = new int[] { 1, 3, 14, 3, 2, 29 };
        sha224WithRSAEncryption_data = new int[] { 1, 2, 840, 113549, 1, 1, 14 };
        sha256WithRSAEncryption_data = new int[] { 1, 2, 840, 113549, 1, 1, 11 };
        sha384WithRSAEncryption_data = new int[] { 1, 2, 840, 113549, 1, 1, 12 };
        sha512WithRSAEncryption_data = new int[] { 1, 2, 840, 113549, 1, 1, 13 };
        shaWithDSA_OIW_data = new int[] { 1, 3, 14, 3, 2, 13 };
        sha1WithDSA_OIW_data = new int[] { 1, 3, 14, 3, 2, 27 };
        dsaWithSHA1_PKIX_data = new int[] { 1, 2, 840, 10040, 4, 3 };
        sha512_224WithRSAEncryption_oid = oid(1, 2, 840, 113549, 1, 1, 15);
        sha512_256WithRSAEncryption_oid = oid(1, 2, 840, 113549, 1, 1, 16);
        sha224WithDSA_oid = oid(2, 16, 840, 1, 101, 3, 4, 3, 1);
        sha256WithDSA_oid = oid(2, 16, 840, 1, 101, 3, 4, 3, 2);
        sha1WithECDSA_oid = oid(1, 2, 840, 10045, 4, 1);
        sha224WithECDSA_oid = oid(1, 2, 840, 10045, 4, 3, 1);
        sha256WithECDSA_oid = oid(1, 2, 840, 10045, 4, 3, 2);
        sha384WithECDSA_oid = oid(1, 2, 840, 10045, 4, 3, 3);
        sha512WithECDSA_oid = oid(1, 2, 840, 10045, 4, 3, 4);
        specifiedWithECDSA_oid = oid(1, 2, 840, 10045, 4, 3);
        pbeWithMD5AndDES_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 3 });
        pbeWithMD5AndRC2_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 6 });
        pbeWithSHA1AndDES_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 10 });
        pbeWithSHA1AndRC2_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 11 });
        AlgorithmId.pbeWithSHA1AndDESede_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 12, 1, 3 });
        AlgorithmId.pbeWithSHA1AndRC2_40_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 12, 1, 6 });
        DH_oid = ObjectIdentifier.newInternal(AlgorithmId.DH_data);
        DH_PKIX_oid = ObjectIdentifier.newInternal(AlgorithmId.DH_PKIX_data);
        DSA_OIW_oid = ObjectIdentifier.newInternal(AlgorithmId.DSA_OIW_data);
        DSA_oid = ObjectIdentifier.newInternal(AlgorithmId.DSA_PKIX_data);
        RSA_oid = ObjectIdentifier.newInternal(AlgorithmId.RSA_data);
        md2WithRSAEncryption_oid = ObjectIdentifier.newInternal(AlgorithmId.md2WithRSAEncryption_data);
        md5WithRSAEncryption_oid = ObjectIdentifier.newInternal(AlgorithmId.md5WithRSAEncryption_data);
        sha1WithRSAEncryption_oid = ObjectIdentifier.newInternal(AlgorithmId.sha1WithRSAEncryption_data);
        sha1WithRSAEncryption_OIW_oid = ObjectIdentifier.newInternal(AlgorithmId.sha1WithRSAEncryption_OIW_data);
        sha224WithRSAEncryption_oid = ObjectIdentifier.newInternal(AlgorithmId.sha224WithRSAEncryption_data);
        sha256WithRSAEncryption_oid = ObjectIdentifier.newInternal(AlgorithmId.sha256WithRSAEncryption_data);
        sha384WithRSAEncryption_oid = ObjectIdentifier.newInternal(AlgorithmId.sha384WithRSAEncryption_data);
        sha512WithRSAEncryption_oid = ObjectIdentifier.newInternal(AlgorithmId.sha512WithRSAEncryption_data);
        shaWithDSA_OIW_oid = ObjectIdentifier.newInternal(AlgorithmId.shaWithDSA_OIW_data);
        sha1WithDSA_OIW_oid = ObjectIdentifier.newInternal(AlgorithmId.sha1WithDSA_OIW_data);
        sha1WithDSA_oid = ObjectIdentifier.newInternal(AlgorithmId.dsaWithSHA1_PKIX_data);
        (nameTable = new HashMap<ObjectIdentifier, String>()).put(AlgorithmId.MD5_oid, "MD5");
        AlgorithmId.nameTable.put(AlgorithmId.MD2_oid, "MD2");
        AlgorithmId.nameTable.put(AlgorithmId.SHA_oid, "SHA-1");
        AlgorithmId.nameTable.put(AlgorithmId.SHA224_oid, "SHA-224");
        AlgorithmId.nameTable.put(AlgorithmId.SHA256_oid, "SHA-256");
        AlgorithmId.nameTable.put(AlgorithmId.SHA384_oid, "SHA-384");
        AlgorithmId.nameTable.put(AlgorithmId.SHA512_oid, "SHA-512");
        AlgorithmId.nameTable.put(AlgorithmId.SHA512_224_oid, "SHA-512/224");
        AlgorithmId.nameTable.put(AlgorithmId.SHA512_256_oid, "SHA-512/256");
        AlgorithmId.nameTable.put(AlgorithmId.RSAEncryption_oid, "RSA");
        AlgorithmId.nameTable.put(AlgorithmId.RSA_oid, "RSA");
        AlgorithmId.nameTable.put(AlgorithmId.DH_oid, "Diffie-Hellman");
        AlgorithmId.nameTable.put(AlgorithmId.DH_PKIX_oid, "Diffie-Hellman");
        AlgorithmId.nameTable.put(AlgorithmId.DSA_oid, "DSA");
        AlgorithmId.nameTable.put(AlgorithmId.DSA_OIW_oid, "DSA");
        AlgorithmId.nameTable.put(AlgorithmId.EC_oid, "EC");
        AlgorithmId.nameTable.put(AlgorithmId.ECDH_oid, "ECDH");
        AlgorithmId.nameTable.put(AlgorithmId.AES_oid, "AES");
        AlgorithmId.nameTable.put(AlgorithmId.sha1WithECDSA_oid, "SHA1withECDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha224WithECDSA_oid, "SHA224withECDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha256WithECDSA_oid, "SHA256withECDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha384WithECDSA_oid, "SHA384withECDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha512WithECDSA_oid, "SHA512withECDSA");
        AlgorithmId.nameTable.put(AlgorithmId.md5WithRSAEncryption_oid, "MD5withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.md2WithRSAEncryption_oid, "MD2withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha1WithDSA_oid, "SHA1withDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha1WithDSA_OIW_oid, "SHA1withDSA");
        AlgorithmId.nameTable.put(AlgorithmId.shaWithDSA_OIW_oid, "SHA1withDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha224WithDSA_oid, "SHA224withDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha256WithDSA_oid, "SHA256withDSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha1WithRSAEncryption_oid, "SHA1withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha1WithRSAEncryption_OIW_oid, "SHA1withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha224WithRSAEncryption_oid, "SHA224withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha256WithRSAEncryption_oid, "SHA256withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha384WithRSAEncryption_oid, "SHA384withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha512WithRSAEncryption_oid, "SHA512withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha512_224WithRSAEncryption_oid, "SHA512/224withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.sha512_256WithRSAEncryption_oid, "SHA512/256withRSA");
        AlgorithmId.nameTable.put(AlgorithmId.RSASSA_PSS_oid, "RSASSA-PSS");
        AlgorithmId.nameTable.put(AlgorithmId.RSAES_OAEP_oid, "RSAES-OAEP");
        AlgorithmId.nameTable.put(AlgorithmId.pbeWithMD5AndDES_oid, "PBEWithMD5AndDES");
        AlgorithmId.nameTable.put(AlgorithmId.pbeWithMD5AndRC2_oid, "PBEWithMD5AndRC2");
        AlgorithmId.nameTable.put(AlgorithmId.pbeWithSHA1AndDES_oid, "PBEWithSHA1AndDES");
        AlgorithmId.nameTable.put(AlgorithmId.pbeWithSHA1AndRC2_oid, "PBEWithSHA1AndRC2");
        AlgorithmId.nameTable.put(AlgorithmId.pbeWithSHA1AndDESede_oid, "PBEWithSHA1AndDESede");
        AlgorithmId.nameTable.put(AlgorithmId.pbeWithSHA1AndRC2_40_oid, "PBEWithSHA1AndRC2_40");
    }
    
    private static class PSSParamsHolder
    {
        static final PSSParameterSpec PSS_256_SPEC;
        static final PSSParameterSpec PSS_384_SPEC;
        static final PSSParameterSpec PSS_512_SPEC;
        static final AlgorithmId PSS_256_ID;
        static final AlgorithmId PSS_384_ID;
        static final AlgorithmId PSS_512_ID;
        
        static {
            PSS_256_SPEC = new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1);
            PSS_384_SPEC = new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1);
            PSS_512_SPEC = new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
            try {
                PSS_256_ID = new AlgorithmId(AlgorithmId.RSASSA_PSS_oid, new DerValue(PSSParameters.getEncoded(PSSParamsHolder.PSS_256_SPEC)), null);
                PSS_384_ID = new AlgorithmId(AlgorithmId.RSASSA_PSS_oid, new DerValue(PSSParameters.getEncoded(PSSParamsHolder.PSS_384_SPEC)), null);
                PSS_512_ID = new AlgorithmId(AlgorithmId.RSASSA_PSS_oid, new DerValue(PSSParameters.getEncoded(PSSParamsHolder.PSS_512_SPEC)), null);
            }
            catch (final IOException ex) {
                throw new AssertionError("Should not happen", ex);
            }
        }
    }
}
