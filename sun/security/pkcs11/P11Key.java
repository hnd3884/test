package sun.security.pkcs11;

import java.io.IOException;
import sun.security.util.DerValue;
import java.security.spec.ECPoint;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.ECParameterSpec;
import java.security.interfaces.ECPrivateKey;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.interfaces.DHPublicKey;
import java.util.Objects;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.InvalidKeyException;
import sun.security.rsa.RSAPublicKeyImpl;
import java.security.interfaces.RSAPublicKey;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAUtil;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import sun.security.internal.interfaces.TlsMasterSecret;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import java.io.ObjectStreamException;
import java.io.NotSerializableException;
import java.security.KeyRep;
import java.security.MessageDigest;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.util.Length;
import java.security.Key;

abstract class P11Key implements Key, Length
{
    private static final long serialVersionUID = -2575874101938349339L;
    private static final String PUBLIC = "public";
    private static final String PRIVATE = "private";
    private static final String SECRET = "secret";
    final String type;
    final Token token;
    final String algorithm;
    final int keyLength;
    final boolean tokenObject;
    final boolean sensitive;
    final boolean extractable;
    private final NativeKeyHolder keyIDHolder;
    private static final boolean DISABLE_NATIVE_KEYS_EXTRACTION;
    private static final CK_ATTRIBUTE[] A0;
    
    P11Key(final String type, final Session session, final long n, final String algorithm, final int keyLength, final CK_ATTRIBUTE[] array) {
        this.type = type;
        this.token = session.token;
        this.algorithm = algorithm;
        this.keyLength = keyLength;
        boolean boolean1 = false;
        boolean boolean2 = false;
        boolean boolean3 = true;
        for (int n2 = (array == null) ? 0 : array.length, i = 0; i < n2; ++i) {
            final CK_ATTRIBUTE ck_ATTRIBUTE = array[i];
            if (ck_ATTRIBUTE.type == 1L) {
                boolean1 = ck_ATTRIBUTE.getBoolean();
            }
            else if (ck_ATTRIBUTE.type == 259L) {
                boolean2 = ck_ATTRIBUTE.getBoolean();
            }
            else if (ck_ATTRIBUTE.type == 354L) {
                boolean3 = ck_ATTRIBUTE.getBoolean();
            }
        }
        this.tokenObject = boolean1;
        this.sensitive = boolean2;
        this.extractable = boolean3;
        final char[] label = this.token.tokenInfo.label;
        final boolean b = label[0] == 'N' && label[1] == 'S' && label[2] == 'S';
        this.keyIDHolder = new NativeKeyHolder(this, n, session, !P11Key.DISABLE_NATIVE_KEYS_EXTRACTION && b && boolean3 && !boolean1, boolean1);
    }
    
    public long getKeyID() {
        return this.keyIDHolder.getKeyID();
    }
    
    public void releaseKeyID() {
        this.keyIDHolder.releaseKeyID();
    }
    
    @Override
    public final String getAlgorithm() {
        this.token.ensureValid();
        return this.algorithm;
    }
    
    @Override
    public final byte[] getEncoded() {
        final byte[] encodedInternal = this.getEncodedInternal();
        return (byte[])((encodedInternal == null) ? null : ((byte[])encodedInternal.clone()));
    }
    
    abstract byte[] getEncodedInternal();
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!this.token.isValid()) {
            return false;
        }
        if (!(o instanceof Key)) {
            return false;
        }
        final String format = this.getFormat();
        if (format == null) {
            return false;
        }
        final Key key = (Key)o;
        if (!format.equals(key.getFormat())) {
            return false;
        }
        final byte[] encodedInternal = this.getEncodedInternal();
        byte[] array;
        if (o instanceof P11Key) {
            array = ((P11Key)key).getEncodedInternal();
        }
        else {
            array = key.getEncoded();
        }
        return MessageDigest.isEqual(encodedInternal, array);
    }
    
    @Override
    public int hashCode() {
        if (!this.token.isValid()) {
            return 0;
        }
        final byte[] encodedInternal = this.getEncodedInternal();
        if (encodedInternal == null) {
            return 0;
        }
        int length = encodedInternal.length;
        for (int i = 0; i < encodedInternal.length; ++i) {
            length += (encodedInternal[i] & 0xFF) * 37;
        }
        return length;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        final String format = this.getFormat();
        KeyRep.Type type;
        if (this.isPrivate() && "PKCS#8".equals(format)) {
            type = KeyRep.Type.PRIVATE;
        }
        else if (this.isPublic() && "X.509".equals(format)) {
            type = KeyRep.Type.PUBLIC;
        }
        else {
            if (!this.isSecret() || !"RAW".equals(format)) {
                throw new NotSerializableException("Cannot serialize sensitive and unextractable keys");
            }
            type = KeyRep.Type.SECRET;
        }
        return new KeyRep(type, this.getAlgorithm(), format, this.getEncoded());
    }
    
    @Override
    public String toString() {
        this.token.ensureValid();
        final String string = this.token.provider.getName() + " " + this.algorithm + " " + this.type + " key, " + this.keyLength + " bits" + (this.tokenObject ? "token" : "session") + " object";
        String s;
        if (this.isPublic()) {
            s = string + ")";
        }
        else {
            s = string + ", " + (this.sensitive ? "" : "not ") + "sensitive" + ", " + (this.extractable ? "" : "un") + "extractable)";
        }
        return s;
    }
    
    @Override
    public int length() {
        return this.keyLength;
    }
    
    boolean isPublic() {
        return this.type == "public";
    }
    
    boolean isPrivate() {
        return this.type == "private";
    }
    
    boolean isSecret() {
        return this.type == "secret";
    }
    
    void fetchAttributes(final CK_ATTRIBUTE[] array) {
        Session opSession = null;
        final long keyID = this.getKeyID();
        try {
            opSession = this.token.getOpSession();
            this.token.p11.C_GetAttributeValue(opSession.id(), keyID, array);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException(ex);
        }
        finally {
            this.releaseKeyID();
            this.token.releaseSession(opSession);
        }
    }
    
    private static CK_ATTRIBUTE[] getAttributes(final Session session, final long n, CK_ATTRIBUTE[] a0, final CK_ATTRIBUTE[] array) {
        if (a0 == null) {
            a0 = P11Key.A0;
        }
        for (int i = 0; i < array.length; ++i) {
            final CK_ATTRIBUTE ck_ATTRIBUTE = array[i];
            for (final CK_ATTRIBUTE ck_ATTRIBUTE2 : a0) {
                if (ck_ATTRIBUTE.type == ck_ATTRIBUTE2.type && ck_ATTRIBUTE2.pValue != null) {
                    ck_ATTRIBUTE.pValue = ck_ATTRIBUTE2.pValue;
                    break;
                }
            }
            if (ck_ATTRIBUTE.pValue == null) {
                for (int k = 0; k < i; ++k) {
                    array[k].pValue = null;
                }
                try {
                    session.token.p11.C_GetAttributeValue(session.id(), n, array);
                    break;
                }
                catch (final PKCS11Exception ex) {
                    throw new ProviderException(ex);
                }
            }
        }
        return array;
    }
    
    static SecretKey secretKey(final Session session, final long n, final String s, final int n2, CK_ATTRIBUTE[] attributes) {
        attributes = getAttributes(session, n, attributes, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(1L), new CK_ATTRIBUTE(259L), new CK_ATTRIBUTE(354L) });
        return new P11SecretKey(session, n, s, n2, attributes);
    }
    
    static SecretKey masterSecretKey(final Session session, final long n, final String s, final int n2, CK_ATTRIBUTE[] attributes, final int n3, final int n4) {
        attributes = getAttributes(session, n, attributes, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(1L), new CK_ATTRIBUTE(259L), new CK_ATTRIBUTE(354L) });
        return new P11TlsMasterSecretKey(session, n, s, n2, attributes, n3, n4);
    }
    
    static PublicKey publicKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
        switch (s) {
            case "RSA": {
                return new P11RSAPublicKey(session, n, s, n2, array);
            }
            case "DSA": {
                return new P11DSAPublicKey(session, n, s, n2, array);
            }
            case "DH": {
                return new P11DHPublicKey(session, n, s, n2, array);
            }
            case "EC": {
                return new P11ECPublicKey(session, n, s, n2, array);
            }
            default: {
                throw new ProviderException("Unknown public key algorithm " + s);
            }
        }
    }
    
    static PrivateKey privateKey(final Session session, final long n, final String s, final int n2, CK_ATTRIBUTE[] attributes) {
        attributes = getAttributes(session, n, attributes, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(1L), new CK_ATTRIBUTE(259L), new CK_ATTRIBUTE(354L) });
        if (attributes[1].getBoolean() || !attributes[2].getBoolean()) {
            return new P11PrivateKey(session, n, s, n2, attributes);
        }
        switch (s) {
            case "RSA": {
                final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(290L) };
                boolean b;
                try {
                    session.token.p11.C_GetAttributeValue(session.id(), n, array);
                    b = (array[0].pValue instanceof byte[]);
                }
                catch (final PKCS11Exception ex) {
                    b = false;
                }
                if (b) {
                    return new P11RSAPrivateKey(session, n, s, n2, attributes);
                }
                return new P11RSAPrivateNonCRTKey(session, n, s, n2, attributes);
            }
            case "DSA": {
                return new P11DSAPrivateKey(session, n, s, n2, attributes);
            }
            case "DH": {
                return new P11DHPrivateKey(session, n, s, n2, attributes);
            }
            case "EC": {
                return new P11ECPrivateKey(session, n, s, n2, attributes);
            }
            default: {
                throw new ProviderException("Unknown private key algorithm " + s);
            }
        }
    }
    
    static {
        DISABLE_NATIVE_KEYS_EXTRACTION = "true".equalsIgnoreCase(AccessController.doPrivileged(() -> System.getProperty("sun.security.pkcs11.disableKeyExtraction", "false")));
        A0 = new CK_ATTRIBUTE[0];
    }
    
    private static final class P11PrivateKey extends P11Key implements PrivateKey
    {
        private static final long serialVersionUID = -2138581185214187615L;
        
        P11PrivateKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("private", session, n, s, n2, array);
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return null;
        }
        
        @Override
        byte[] getEncodedInternal() {
            this.token.ensureValid();
            return null;
        }
    }
    
    private static class P11SecretKey extends P11Key implements SecretKey
    {
        private static final long serialVersionUID = -7828241727014329084L;
        private volatile byte[] encoded;
        
        P11SecretKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("secret", session, n, s, n2, array);
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            if (this.sensitive || !this.extractable) {
                return null;
            }
            return "RAW";
        }
        
        @Override
        byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.getFormat() == null) {
                return null;
            }
            byte[] encoded = this.encoded;
            if (encoded == null) {
                synchronized (this) {
                    encoded = this.encoded;
                    if (encoded == null) {
                        Session opSession = null;
                        final long keyID = this.getKeyID();
                        try {
                            opSession = this.token.getOpSession();
                            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L) };
                            this.token.p11.C_GetAttributeValue(opSession.id(), keyID, array);
                            encoded = array[0].getByteArray();
                        }
                        catch (final PKCS11Exception ex) {
                            throw new ProviderException(ex);
                        }
                        finally {
                            this.releaseKeyID();
                            this.token.releaseSession(opSession);
                        }
                        this.encoded = encoded;
                    }
                }
            }
            return encoded;
        }
    }
    
    private static class P11TlsMasterSecretKey extends P11SecretKey implements TlsMasterSecret
    {
        private static final long serialVersionUID = -1318560923770573441L;
        private final int majorVersion;
        private final int minorVersion;
        
        P11TlsMasterSecretKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array, final int majorVersion, final int minorVersion) {
            super(session, n, s, n2, array);
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
        }
        
        @Override
        public int getMajorVersion() {
            return this.majorVersion;
        }
        
        @Override
        public int getMinorVersion() {
            return this.minorVersion;
        }
    }
    
    private static final class P11RSAPrivateKey extends P11Key implements RSAPrivateCrtKey
    {
        private static final long serialVersionUID = 9215872438913515220L;
        private BigInteger n;
        private BigInteger e;
        private BigInteger d;
        private BigInteger p;
        private BigInteger q;
        private BigInteger pe;
        private BigInteger qe;
        private BigInteger coeff;
        private byte[] encoded;
        
        P11RSAPrivateKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("private", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.n != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(288L), new CK_ATTRIBUTE(290L), new CK_ATTRIBUTE(291L), new CK_ATTRIBUTE(292L), new CK_ATTRIBUTE(293L), new CK_ATTRIBUTE(294L), new CK_ATTRIBUTE(295L), new CK_ATTRIBUTE(296L) };
            this.fetchAttributes(array);
            this.n = array[0].getBigInteger();
            this.e = array[1].getBigInteger();
            this.d = array[2].getBigInteger();
            this.p = array[3].getBigInteger();
            this.q = array[4].getBigInteger();
            this.pe = array[5].getBigInteger();
            this.qe = array[6].getBigInteger();
            this.coeff = array[7].getBigInteger();
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "PKCS#8";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = RSAPrivateCrtKeyImpl.newKey(RSAUtil.KeyType.RSA, (AlgorithmParameterSpec)null, this.n, this.e, this.d, this.p, this.q, this.pe, this.qe, this.coeff).getEncoded();
                }
                catch (final GeneralSecurityException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getModulus() {
            this.fetchValues();
            return this.n;
        }
        
        @Override
        public BigInteger getPublicExponent() {
            this.fetchValues();
            return this.e;
        }
        
        @Override
        public BigInteger getPrivateExponent() {
            this.fetchValues();
            return this.d;
        }
        
        @Override
        public BigInteger getPrimeP() {
            this.fetchValues();
            return this.p;
        }
        
        @Override
        public BigInteger getPrimeQ() {
            this.fetchValues();
            return this.q;
        }
        
        @Override
        public BigInteger getPrimeExponentP() {
            this.fetchValues();
            return this.pe;
        }
        
        @Override
        public BigInteger getPrimeExponentQ() {
            this.fetchValues();
            return this.qe;
        }
        
        @Override
        public BigInteger getCrtCoefficient() {
            this.fetchValues();
            return this.coeff;
        }
    }
    
    private static final class P11RSAPrivateNonCRTKey extends P11Key implements RSAPrivateKey
    {
        private static final long serialVersionUID = 1137764983777411481L;
        private BigInteger n;
        private BigInteger d;
        private byte[] encoded;
        
        P11RSAPrivateNonCRTKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("private", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.n != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(288L), new CK_ATTRIBUTE(291L) };
            this.fetchAttributes(array);
            this.n = array[0].getBigInteger();
            this.d = array[1].getBigInteger();
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "PKCS#8";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = KeyFactory.getInstance("RSA", P11Util.getSunRsaSignProvider()).translateKey(this).getEncoded();
                }
                catch (final GeneralSecurityException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getModulus() {
            this.fetchValues();
            return this.n;
        }
        
        @Override
        public BigInteger getPrivateExponent() {
            this.fetchValues();
            return this.d;
        }
    }
    
    private static final class P11RSAPublicKey extends P11Key implements RSAPublicKey
    {
        private static final long serialVersionUID = -826726289023854455L;
        private BigInteger n;
        private BigInteger e;
        private byte[] encoded;
        
        P11RSAPublicKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("public", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.n != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(288L), new CK_ATTRIBUTE(290L) };
            this.fetchAttributes(array);
            this.n = array[0].getBigInteger();
            this.e = array[1].getBigInteger();
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "X.509";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = RSAPublicKeyImpl.newKey(RSAUtil.KeyType.RSA, (AlgorithmParameterSpec)null, this.n, this.e).getEncoded();
                }
                catch (final InvalidKeyException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getModulus() {
            this.fetchValues();
            return this.n;
        }
        
        @Override
        public BigInteger getPublicExponent() {
            this.fetchValues();
            return this.e;
        }
        
        @Override
        public String toString() {
            this.fetchValues();
            return super.toString() + "\n  modulus: " + this.n + "\n  public exponent: " + this.e;
        }
    }
    
    private static final class P11DSAPublicKey extends P11Key implements DSAPublicKey
    {
        private static final long serialVersionUID = 5989753793316396637L;
        private BigInteger y;
        private DSAParams params;
        private byte[] encoded;
        
        P11DSAPublicKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("public", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.y != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(305L), new CK_ATTRIBUTE(306L) };
            this.fetchAttributes(array);
            this.y = array[0].getBigInteger();
            this.params = new DSAParameterSpec(array[1].getBigInteger(), array[2].getBigInteger(), array[3].getBigInteger());
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "X.509";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = new sun.security.provider.DSAPublicKey(this.y, this.params.getP(), this.params.getQ(), this.params.getG()).getEncoded();
                }
                catch (final InvalidKeyException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getY() {
            this.fetchValues();
            return this.y;
        }
        
        @Override
        public DSAParams getParams() {
            this.fetchValues();
            return this.params;
        }
        
        @Override
        public String toString() {
            this.fetchValues();
            return super.toString() + "\n  y: " + this.y + "\n  p: " + this.params.getP() + "\n  q: " + this.params.getQ() + "\n  g: " + this.params.getG();
        }
    }
    
    private static final class P11DSAPrivateKey extends P11Key implements DSAPrivateKey
    {
        private static final long serialVersionUID = 3119629997181999389L;
        private BigInteger x;
        private DSAParams params;
        private byte[] encoded;
        
        P11DSAPrivateKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("private", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.x != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(305L), new CK_ATTRIBUTE(306L) };
            this.fetchAttributes(array);
            this.x = array[0].getBigInteger();
            this.params = new DSAParameterSpec(array[1].getBigInteger(), array[2].getBigInteger(), array[3].getBigInteger());
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "PKCS#8";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = new sun.security.provider.DSAPrivateKey(this.x, this.params.getP(), this.params.getQ(), this.params.getG()).getEncoded();
                }
                catch (final InvalidKeyException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getX() {
            this.fetchValues();
            return this.x;
        }
        
        @Override
        public DSAParams getParams() {
            this.fetchValues();
            return this.params;
        }
    }
    
    private static final class P11DHPrivateKey extends P11Key implements DHPrivateKey
    {
        private static final long serialVersionUID = -1698576167364928838L;
        private BigInteger x;
        private DHParameterSpec params;
        private byte[] encoded;
        
        P11DHPrivateKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("private", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.x != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(306L) };
            this.fetchAttributes(array);
            this.x = array[0].getBigInteger();
            this.params = new DHParameterSpec(array[1].getBigInteger(), array[2].getBigInteger());
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "PKCS#8";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = KeyFactory.getInstance("DH", P11Util.getSunJceProvider()).generatePrivate(new DHPrivateKeySpec(this.x, this.params.getP(), this.params.getG())).getEncoded();
                }
                catch (final GeneralSecurityException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getX() {
            this.fetchValues();
            return this.x;
        }
        
        @Override
        public DHParameterSpec getParams() {
            this.fetchValues();
            return this.params;
        }
        
        @Override
        public int hashCode() {
            if (!this.token.isValid()) {
                return 0;
            }
            this.fetchValues();
            return Objects.hash(this.x, this.params.getP(), this.params.getG());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!this.token.isValid()) {
                return false;
            }
            if (!(o instanceof DHPrivateKey)) {
                return false;
            }
            this.fetchValues();
            final DHPrivateKey dhPrivateKey = (DHPrivateKey)o;
            final DHParameterSpec params = dhPrivateKey.getParams();
            return this.x.compareTo(dhPrivateKey.getX()) == 0 && this.params.getP().compareTo(params.getP()) == 0 && this.params.getG().compareTo(params.getG()) == 0;
        }
    }
    
    private static final class P11DHPublicKey extends P11Key implements DHPublicKey
    {
        static final long serialVersionUID = -598383872153843657L;
        private BigInteger y;
        private DHParameterSpec params;
        private byte[] encoded;
        
        P11DHPublicKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("public", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.y != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(306L) };
            this.fetchAttributes(array);
            this.y = array[0].getBigInteger();
            this.params = new DHParameterSpec(array[1].getBigInteger(), array[2].getBigInteger());
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "X.509";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = KeyFactory.getInstance("DH", P11Util.getSunJceProvider()).generatePublic(new DHPublicKeySpec(this.y, this.params.getP(), this.params.getG())).getEncoded();
                }
                catch (final GeneralSecurityException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getY() {
            this.fetchValues();
            return this.y;
        }
        
        @Override
        public DHParameterSpec getParams() {
            this.fetchValues();
            return this.params;
        }
        
        @Override
        public String toString() {
            this.fetchValues();
            return super.toString() + "\n  y: " + this.y + "\n  p: " + this.params.getP() + "\n  g: " + this.params.getG();
        }
        
        @Override
        public int hashCode() {
            if (!this.token.isValid()) {
                return 0;
            }
            this.fetchValues();
            return Objects.hash(this.y, this.params.getP(), this.params.getG());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!this.token.isValid()) {
                return false;
            }
            if (!(o instanceof DHPublicKey)) {
                return false;
            }
            this.fetchValues();
            final DHPublicKey dhPublicKey = (DHPublicKey)o;
            final DHParameterSpec params = dhPublicKey.getParams();
            return this.y.compareTo(dhPublicKey.getY()) == 0 && this.params.getP().compareTo(params.getP()) == 0 && this.params.getG().compareTo(params.getG()) == 0;
        }
    }
    
    private static final class P11ECPrivateKey extends P11Key implements ECPrivateKey
    {
        private static final long serialVersionUID = -7786054399510515515L;
        private BigInteger s;
        private ECParameterSpec params;
        private byte[] encoded;
        
        P11ECPrivateKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("private", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.s != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(384L, this.params) };
            this.fetchAttributes(array);
            this.s = array[0].getBigInteger();
            try {
                this.params = P11ECKeyFactory.decodeParameters(array[1].getByteArray());
            }
            catch (final Exception ex) {
                throw new RuntimeException("Could not parse key values", ex);
            }
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "PKCS#8";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    this.encoded = P11ECUtil.generateECPrivateKey(this.s, this.params).getEncoded();
                }
                catch (final InvalidKeySpecException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public BigInteger getS() {
            this.fetchValues();
            return this.s;
        }
        
        @Override
        public ECParameterSpec getParams() {
            this.fetchValues();
            return this.params;
        }
    }
    
    private static final class P11ECPublicKey extends P11Key implements ECPublicKey
    {
        private static final long serialVersionUID = -6371481375154806089L;
        private ECPoint w;
        private ECParameterSpec params;
        private byte[] encoded;
        
        P11ECPublicKey(final Session session, final long n, final String s, final int n2, final CK_ATTRIBUTE[] array) {
            super("public", session, n, s, n2, array);
        }
        
        private synchronized void fetchValues() {
            this.token.ensureValid();
            if (this.w != null) {
                return;
            }
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(385L), new CK_ATTRIBUTE(384L) };
            this.fetchAttributes(array);
            try {
                this.params = P11ECKeyFactory.decodeParameters(array[1].getByteArray());
                final byte[] byteArray = array[0].getByteArray();
                if (!this.token.config.getUseEcX963Encoding()) {
                    final DerValue derValue = new DerValue(byteArray);
                    if (derValue.getTag() != 4) {
                        throw new IOException("Could not DER decode EC point. Unexpected tag: " + derValue.getTag());
                    }
                    this.w = P11ECKeyFactory.decodePoint(derValue.getDataBytes(), this.params.getCurve());
                }
                else {
                    this.w = P11ECKeyFactory.decodePoint(byteArray, this.params.getCurve());
                }
            }
            catch (final Exception ex) {
                throw new RuntimeException("Could not parse key values", ex);
            }
        }
        
        @Override
        public String getFormat() {
            this.token.ensureValid();
            return "X.509";
        }
        
        @Override
        synchronized byte[] getEncodedInternal() {
            this.token.ensureValid();
            if (this.encoded == null) {
                this.fetchValues();
                try {
                    return P11ECUtil.x509EncodeECPublicKey(this.w, this.params);
                }
                catch (final InvalidKeySpecException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.encoded;
        }
        
        @Override
        public ECPoint getW() {
            this.fetchValues();
            return this.w;
        }
        
        @Override
        public ECParameterSpec getParams() {
            this.fetchValues();
            return this.params;
        }
        
        @Override
        public String toString() {
            this.fetchValues();
            return super.toString() + "\n  public x coord: " + this.w.getAffineX() + "\n  public y coord: " + this.w.getAffineY() + "\n  parameters: " + this.params;
        }
    }
}
