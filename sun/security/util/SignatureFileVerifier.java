package sun.security.util;

import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.util.Base64;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import sun.security.pkcs.SignerInfo;
import java.util.jar.Attributes;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.jar.Manifest;
import java.util.jar.JarException;
import java.util.List;
import java.util.Hashtable;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.util.Locale;
import sun.security.jca.Providers;
import java.security.Timestamp;
import java.util.Map;
import java.security.cert.CertificateFactory;
import java.security.MessageDigest;
import java.util.HashMap;
import sun.security.pkcs.PKCS7;
import java.security.CodeSigner;
import java.util.ArrayList;

public class SignatureFileVerifier
{
    private static final Debug debug;
    private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK;
    private ArrayList<CodeSigner[]> signerCache;
    private static final String ATTR_DIGEST;
    private PKCS7 block;
    private byte[] sfBytes;
    private String name;
    private ManifestDigester md;
    private HashMap<String, MessageDigest> createdDigests;
    private boolean workaround;
    private CertificateFactory certificateFactory;
    private Map<String, Boolean> permittedAlgs;
    private Timestamp timestamp;
    private static final char[] hexc;
    
    public SignatureFileVerifier(final ArrayList<CodeSigner[]> signerCache, final ManifestDigester md, final String s, final byte[] array) throws IOException, CertificateException {
        this.workaround = false;
        this.certificateFactory = null;
        this.permittedAlgs = new HashMap<String, Boolean>();
        this.timestamp = null;
        Object startJarVerification = null;
        try {
            startJarVerification = Providers.startJarVerification();
            this.block = new PKCS7(array);
            this.sfBytes = this.block.getContentInfo().getData();
            this.certificateFactory = CertificateFactory.getInstance("X509");
        }
        finally {
            Providers.stopJarVerification(startJarVerification);
        }
        this.name = s.substring(0, s.lastIndexOf(46)).toUpperCase(Locale.ENGLISH);
        this.md = md;
        this.signerCache = signerCache;
    }
    
    public boolean needSignatureFileBytes() {
        return this.sfBytes == null;
    }
    
    public boolean needSignatureFile(final String s) {
        return this.name.equalsIgnoreCase(s);
    }
    
    public void setSignatureFile(final byte[] sfBytes) {
        this.sfBytes = sfBytes;
    }
    
    public static boolean isBlockOrSF(final String s) {
        return s.endsWith(".SF") || s.endsWith(".DSA") || s.endsWith(".RSA") || s.endsWith(".EC");
    }
    
    public static boolean isSigningRelated(String s) {
        s = s.toUpperCase(Locale.ENGLISH);
        if (!s.startsWith("META-INF/")) {
            return false;
        }
        s = s.substring(9);
        if (s.indexOf(47) != -1) {
            return false;
        }
        if (isBlockOrSF(s) || s.equals("MANIFEST.MF")) {
            return true;
        }
        if (s.startsWith("SIG-")) {
            final int lastIndex = s.lastIndexOf(46);
            if (lastIndex != -1) {
                final String substring = s.substring(lastIndex + 1);
                if (substring.length() > 3 || substring.length() < 1) {
                    return false;
                }
                for (int i = 0; i < substring.length(); ++i) {
                    final char char1 = substring.charAt(i);
                    if ((char1 < 'A' || char1 > 'Z') && (char1 < '0' || char1 > '9')) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private MessageDigest getDigest(final String s) throws SignatureException {
        if (this.createdDigests == null) {
            this.createdDigests = new HashMap<String, MessageDigest>();
        }
        MessageDigest instance = this.createdDigests.get(s);
        if (instance == null) {
            try {
                instance = MessageDigest.getInstance(s);
                this.createdDigests.put(s, instance);
            }
            catch (final NoSuchAlgorithmException ex) {}
        }
        return instance;
    }
    
    public void process(final Hashtable<String, CodeSigner[]> hashtable, final List<Object> list) throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException {
        Object startJarVerification = null;
        try {
            startJarVerification = Providers.startJarVerification();
            this.processImpl(hashtable, list);
        }
        finally {
            Providers.stopJarVerification(startJarVerification);
        }
    }
    
    private void processImpl(final Hashtable<String, CodeSigner[]> hashtable, final List<Object> list) throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException {
        final Manifest manifest = new Manifest();
        manifest.read(new ByteArrayInputStream(this.sfBytes));
        final String value = manifest.getMainAttributes().getValue(Attributes.Name.SIGNATURE_VERSION);
        if (value == null || !value.equalsIgnoreCase("1.0")) {
            return;
        }
        final SignerInfo[] verify = this.block.verify(this.sfBytes);
        if (verify == null) {
            throw new SecurityException("cannot verify signature block file " + this.name);
        }
        final CodeSigner[] signers = this.getSigners(verify, this.block);
        if (signers == null) {
            return;
        }
        for (final CodeSigner codeSigner : signers) {
            if (SignatureFileVerifier.debug != null) {
                SignatureFileVerifier.debug.println("Gathering timestamp for:  " + codeSigner.toString());
            }
            if (codeSigner.getTimestamp() == null) {
                this.timestamp = null;
                break;
            }
            if (this.timestamp == null) {
                this.timestamp = codeSigner.getTimestamp();
            }
            else if (this.timestamp.getTimestamp().before(codeSigner.getTimestamp().getTimestamp())) {
                this.timestamp = codeSigner.getTimestamp();
            }
        }
        final Iterator<Map.Entry<String, Attributes>> iterator = manifest.getEntries().entrySet().iterator();
        final boolean verifyManifestHash = this.verifyManifestHash(manifest, this.md, list);
        if (!verifyManifestHash && !this.verifyManifestMainAttrs(manifest, this.md)) {
            throw new SecurityException("Invalid signature file digest for Manifest main attributes");
        }
        while (iterator.hasNext()) {
            final Map.Entry entry = iterator.next();
            String s = (String)entry.getKey();
            if (verifyManifestHash || this.verifySection((Attributes)entry.getValue(), s, this.md)) {
                if (s.startsWith("./")) {
                    s = s.substring(2);
                }
                if (s.startsWith("/")) {
                    s = s.substring(1);
                }
                this.updateSigners(signers, hashtable, s);
                if (SignatureFileVerifier.debug == null) {
                    continue;
                }
                SignatureFileVerifier.debug.println("processSignature signed name = " + s);
            }
            else {
                if (SignatureFileVerifier.debug == null) {
                    continue;
                }
                SignatureFileVerifier.debug.println("processSignature unsigned name = " + s);
            }
        }
        this.updateSigners(signers, hashtable, "META-INF/MANIFEST.MF");
    }
    
    boolean permittedCheck(final String s, final String s2) {
        final Boolean b = this.permittedAlgs.get(s2);
        if (b == null) {
            try {
                SignatureFileVerifier.JAR_DISABLED_CHECK.permits(s2, new ConstraintsParameters(this.timestamp));
            }
            catch (final GeneralSecurityException ex) {
                this.permittedAlgs.put(s2, Boolean.FALSE);
                this.permittedAlgs.put(s.toUpperCase(), Boolean.FALSE);
                if (SignatureFileVerifier.debug != null) {
                    if (ex.getMessage() != null) {
                        SignatureFileVerifier.debug.println(s + ":  " + ex.getMessage());
                    }
                    else {
                        SignatureFileVerifier.debug.println("Debug info only. " + s + ":  " + s2 + " was disabled, no exception msg given.");
                        ex.printStackTrace();
                    }
                }
                return false;
            }
            this.permittedAlgs.put(s2, Boolean.TRUE);
            return true;
        }
        return b;
    }
    
    String getWeakAlgorithms(final String s) {
        String s2 = "";
        try {
            for (final String s3 : this.permittedAlgs.keySet()) {
                if (s3.endsWith(s)) {
                    s2 = s2 + s3.substring(0, s3.length() - s.length()) + " ";
                }
            }
        }
        catch (final RuntimeException ex) {
            s2 = "Unknown Algorithm(s).  Error processing " + s + ".  " + ex.getMessage();
        }
        if (s2.length() == 0) {
            return "Unknown Algorithm(s)";
        }
        return s2;
    }
    
    private boolean verifyManifestHash(final Manifest manifest, final ManifestDigester manifestDigester, final List<Object> list) throws IOException, SignatureException {
        final Attributes mainAttributes = manifest.getMainAttributes();
        boolean b = false;
        boolean b2 = true;
        boolean b3 = false;
        for (final Map.Entry entry : mainAttributes.entrySet()) {
            final String string = entry.getKey().toString();
            if (string.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST")) {
                final String substring = string.substring(0, string.length() - 16);
                b3 = true;
                if (!this.permittedCheck(string, substring)) {
                    continue;
                }
                b2 = false;
                list.add(string);
                list.add(entry.getValue());
                final MessageDigest digest = this.getDigest(substring);
                if (digest == null) {
                    continue;
                }
                final byte[] manifestDigest = manifestDigester.manifestDigest(digest);
                final byte[] decode = Base64.getMimeDecoder().decode((String)entry.getValue());
                if (SignatureFileVerifier.debug != null) {
                    SignatureFileVerifier.debug.println("Signature File: Manifest digest " + substring);
                    SignatureFileVerifier.debug.println("  sigfile  " + toHex(decode));
                    SignatureFileVerifier.debug.println("  computed " + toHex(manifestDigest));
                    SignatureFileVerifier.debug.println();
                }
                if (!MessageDigest.isEqual(manifestDigest, decode)) {
                    continue;
                }
                b = true;
            }
        }
        if (SignatureFileVerifier.debug != null) {
            SignatureFileVerifier.debug.println("PermittedAlgs mapping: ");
            for (final String s : this.permittedAlgs.keySet()) {
                SignatureFileVerifier.debug.println(s + " : " + this.permittedAlgs.get(s).toString());
            }
        }
        if (b3 && b2) {
            throw new SignatureException("Manifest hash check failed (DIGEST-MANIFEST). Disabled algorithm(s) used: " + this.getWeakAlgorithms("-DIGEST-MANIFEST"));
        }
        return b;
    }
    
    private boolean verifyManifestMainAttrs(final Manifest manifest, final ManifestDigester manifestDigester) throws IOException, SignatureException {
        final Attributes mainAttributes = manifest.getMainAttributes();
        boolean b = true;
        boolean b2 = true;
        boolean b3 = false;
        for (final Map.Entry entry : mainAttributes.entrySet()) {
            final String string = entry.getKey().toString();
            if (string.toUpperCase(Locale.ENGLISH).endsWith(SignatureFileVerifier.ATTR_DIGEST)) {
                final String substring = string.substring(0, string.length() - SignatureFileVerifier.ATTR_DIGEST.length());
                b3 = true;
                if (!this.permittedCheck(string, substring)) {
                    continue;
                }
                b2 = false;
                final MessageDigest digest = this.getDigest(substring);
                if (digest == null) {
                    continue;
                }
                final byte[] digest2 = manifestDigester.get("Manifest-Main-Attributes", false).digest(digest);
                final byte[] decode = Base64.getMimeDecoder().decode((String)entry.getValue());
                if (SignatureFileVerifier.debug != null) {
                    SignatureFileVerifier.debug.println("Signature File: Manifest Main Attributes digest " + digest.getAlgorithm());
                    SignatureFileVerifier.debug.println("  sigfile  " + toHex(decode));
                    SignatureFileVerifier.debug.println("  computed " + toHex(digest2));
                    SignatureFileVerifier.debug.println();
                }
                if (MessageDigest.isEqual(digest2, decode)) {
                    continue;
                }
                b = false;
                if (SignatureFileVerifier.debug != null) {
                    SignatureFileVerifier.debug.println("Verification of Manifest main attributes failed");
                    SignatureFileVerifier.debug.println();
                    break;
                }
                break;
            }
        }
        if (SignatureFileVerifier.debug != null) {
            SignatureFileVerifier.debug.println("PermittedAlgs mapping: ");
            for (final String s : this.permittedAlgs.keySet()) {
                SignatureFileVerifier.debug.println(s + " : " + this.permittedAlgs.get(s).toString());
            }
        }
        if (b3 && b2) {
            throw new SignatureException("Manifest Main Attribute check failed (" + SignatureFileVerifier.ATTR_DIGEST + ").  Disabled algorithm(s) used: " + this.getWeakAlgorithms(SignatureFileVerifier.ATTR_DIGEST));
        }
        return b;
    }
    
    private boolean verifySection(final Attributes attributes, final String s, final ManifestDigester manifestDigester) throws IOException, SignatureException {
        boolean b = false;
        final ManifestDigester.Entry value = manifestDigester.get(s, this.block.isOldStyle());
        boolean b2 = true;
        boolean b3 = false;
        if (value == null) {
            throw new SecurityException("no manifest section for signature file entry " + s);
        }
        if (attributes != null) {
            for (final Map.Entry entry : attributes.entrySet()) {
                final String string = entry.getKey().toString();
                if (string.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST")) {
                    final String substring = string.substring(0, string.length() - 7);
                    b3 = true;
                    if (!this.permittedCheck(string, substring)) {
                        continue;
                    }
                    b2 = false;
                    final MessageDigest digest = this.getDigest(substring);
                    if (digest == null) {
                        continue;
                    }
                    boolean b4 = false;
                    final byte[] decode = Base64.getMimeDecoder().decode((String)entry.getValue());
                    byte[] array;
                    if (this.workaround) {
                        array = value.digestWorkaround(digest);
                    }
                    else {
                        array = value.digest(digest);
                    }
                    if (SignatureFileVerifier.debug != null) {
                        SignatureFileVerifier.debug.println("Signature Block File: " + s + " digest=" + digest.getAlgorithm());
                        SignatureFileVerifier.debug.println("  expected " + toHex(decode));
                        SignatureFileVerifier.debug.println("  computed " + toHex(array));
                        SignatureFileVerifier.debug.println();
                    }
                    if (MessageDigest.isEqual(array, decode)) {
                        b = true;
                        b4 = true;
                    }
                    else if (!this.workaround) {
                        final byte[] digestWorkaround = value.digestWorkaround(digest);
                        if (MessageDigest.isEqual(digestWorkaround, decode)) {
                            if (SignatureFileVerifier.debug != null) {
                                SignatureFileVerifier.debug.println("  re-computed " + toHex(digestWorkaround));
                                SignatureFileVerifier.debug.println();
                            }
                            this.workaround = true;
                            b = true;
                            b4 = true;
                        }
                    }
                    if (!b4) {
                        throw new SecurityException("invalid " + digest.getAlgorithm() + " signature file digest for " + s);
                    }
                    continue;
                }
            }
        }
        if (SignatureFileVerifier.debug != null) {
            SignatureFileVerifier.debug.println("PermittedAlgs mapping: ");
            for (final String s2 : this.permittedAlgs.keySet()) {
                SignatureFileVerifier.debug.println(s2 + " : " + this.permittedAlgs.get(s2).toString());
            }
        }
        if (b3 && b2) {
            throw new SignatureException("Manifest Main Attribute check failed (DIGEST).  Disabled algorithm(s) used: " + this.getWeakAlgorithms("DIGEST"));
        }
        return b;
    }
    
    private CodeSigner[] getSigners(final SignerInfo[] array, final PKCS7 pkcs7) throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException {
        ArrayList<CodeSigner> list = null;
        for (int i = 0; i < array.length; ++i) {
            final SignerInfo signerInfo = array[i];
            final ArrayList<X509Certificate> certificateChain = signerInfo.getCertificateChain(pkcs7);
            final CertPath generateCertPath = this.certificateFactory.generateCertPath(certificateChain);
            if (list == null) {
                list = new ArrayList<CodeSigner>();
            }
            list.add(new CodeSigner(generateCertPath, signerInfo.getTimestamp()));
            if (SignatureFileVerifier.debug != null) {
                SignatureFileVerifier.debug.println("Signature Block Certificate: " + certificateChain.get(0));
            }
        }
        if (list != null) {
            return list.toArray(new CodeSigner[list.size()]);
        }
        return null;
    }
    
    static String toHex(final byte[] array) {
        final StringBuilder sb = new StringBuilder(array.length * 2);
        for (int i = 0; i < array.length; ++i) {
            sb.append(SignatureFileVerifier.hexc[array[i] >> 4 & 0xF]);
            sb.append(SignatureFileVerifier.hexc[array[i] & 0xF]);
        }
        return sb.toString();
    }
    
    static boolean contains(final CodeSigner[] array, final CodeSigner codeSigner) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(codeSigner)) {
                return true;
            }
        }
        return false;
    }
    
    static boolean isSubSet(final CodeSigner[] array, final CodeSigner[] array2) {
        if (array2 == array) {
            return true;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!contains(array2, array[i])) {
                return false;
            }
        }
        return true;
    }
    
    static boolean matches(final CodeSigner[] array, final CodeSigner[] array2, final CodeSigner[] array3) {
        if (array2 == null && array == array3) {
            return true;
        }
        if (array2 != null && !isSubSet(array2, array)) {
            return false;
        }
        if (!isSubSet(array3, array)) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if ((array2 == null || !contains(array2, array[i])) && !contains(array3, array[i])) {
                return false;
            }
        }
        return true;
    }
    
    void updateSigners(final CodeSigner[] array, final Hashtable<String, CodeSigner[]> hashtable, final String s) {
        final CodeSigner[] array2 = hashtable.get(s);
        for (int i = this.signerCache.size() - 1; i != -1; --i) {
            final CodeSigner[] array3 = this.signerCache.get(i);
            if (matches(array3, array2, array)) {
                hashtable.put(s, array3);
                return;
            }
        }
        CodeSigner[] array4;
        if (array2 == null) {
            array4 = array;
        }
        else {
            array4 = new CodeSigner[array2.length + array.length];
            System.arraycopy(array2, 0, array4, 0, array2.length);
            System.arraycopy(array, 0, array4, array2.length, array.length);
        }
        this.signerCache.add(array4);
        hashtable.put(s, array4);
    }
    
    static {
        debug = Debug.getInstance("jar");
        JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
        ATTR_DIGEST = "-DIGEST-Manifest-Main-Attributes".toUpperCase(Locale.ENGLISH);
        hexc = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
