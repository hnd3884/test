package java.util.jar;

import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.io.IOException;
import java.util.Locale;
import sun.security.util.ManifestEntryVerifier;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Enumeration;
import java.security.CodeSource;
import java.net.URL;
import java.util.Map;
import java.util.List;
import sun.security.util.ManifestDigester;
import java.io.ByteArrayOutputStream;
import sun.security.util.SignatureFileVerifier;
import java.util.ArrayList;
import java.security.CodeSigner;
import java.util.Hashtable;
import sun.security.util.Debug;

class JarVerifier
{
    static final Debug debug;
    private Hashtable<String, CodeSigner[]> verifiedSigners;
    private Hashtable<String, CodeSigner[]> sigFileSigners;
    private Hashtable<String, byte[]> sigFileData;
    private ArrayList<SignatureFileVerifier> pendingBlocks;
    private ArrayList<CodeSigner[]> signerCache;
    private boolean parsingBlockOrSF;
    private boolean parsingMeta;
    private boolean anyToVerify;
    private ByteArrayOutputStream baos;
    private volatile ManifestDigester manDig;
    byte[] manifestRawBytes;
    boolean eagerValidation;
    private Object csdomain;
    private List<Object> manifestDigests;
    private Map<URL, Map<CodeSigner[], CodeSource>> urlToCodeSourceMap;
    private Map<CodeSigner[], CodeSource> signerToCodeSource;
    private URL lastURL;
    private Map<CodeSigner[], CodeSource> lastURLMap;
    private CodeSigner[] emptySigner;
    private Map<String, CodeSigner[]> signerMap;
    private Enumeration<String> emptyEnumeration;
    private List<CodeSigner[]> jarCodeSigners;
    
    public JarVerifier(final byte[] manifestRawBytes) {
        this.parsingBlockOrSF = false;
        this.parsingMeta = true;
        this.anyToVerify = true;
        this.manifestRawBytes = null;
        this.csdomain = new Object();
        this.urlToCodeSourceMap = new HashMap<URL, Map<CodeSigner[], CodeSource>>();
        this.signerToCodeSource = new HashMap<CodeSigner[], CodeSource>();
        this.emptySigner = new CodeSigner[0];
        this.emptyEnumeration = new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }
            
            @Override
            public String nextElement() {
                throw new NoSuchElementException();
            }
        };
        this.manifestRawBytes = manifestRawBytes;
        this.sigFileSigners = new Hashtable<String, CodeSigner[]>();
        this.verifiedSigners = new Hashtable<String, CodeSigner[]>();
        this.sigFileData = new Hashtable<String, byte[]>(11);
        this.pendingBlocks = new ArrayList<SignatureFileVerifier>();
        this.baos = new ByteArrayOutputStream();
        this.manifestDigests = new ArrayList<Object>();
    }
    
    public void beginEntry(final JarEntry jarEntry, final ManifestEntryVerifier manifestEntryVerifier) throws IOException {
        if (jarEntry == null) {
            return;
        }
        if (JarVerifier.debug != null) {
            JarVerifier.debug.println("beginEntry " + jarEntry.getName());
        }
        String s = jarEntry.getName();
        if (this.parsingMeta) {
            final String upperCase = s.toUpperCase(Locale.ENGLISH);
            if (upperCase.startsWith("META-INF/") || upperCase.startsWith("/META-INF/")) {
                if (jarEntry.isDirectory()) {
                    manifestEntryVerifier.setEntry(null, jarEntry);
                    return;
                }
                if (upperCase.equals("META-INF/MANIFEST.MF") || upperCase.equals("META-INF/INDEX.LIST")) {
                    return;
                }
                if (SignatureFileVerifier.isBlockOrSF(upperCase)) {
                    this.parsingBlockOrSF = true;
                    this.baos.reset();
                    manifestEntryVerifier.setEntry(null, jarEntry);
                    return;
                }
            }
        }
        if (this.parsingMeta) {
            this.doneWithMeta();
        }
        if (jarEntry.isDirectory()) {
            manifestEntryVerifier.setEntry(null, jarEntry);
            return;
        }
        if (s.startsWith("./")) {
            s = s.substring(2);
        }
        if (s.startsWith("/")) {
            s = s.substring(1);
        }
        if (!s.equals("META-INF/MANIFEST.MF") && (this.sigFileSigners.get(s) != null || this.verifiedSigners.get(s) != null)) {
            manifestEntryVerifier.setEntry(s, jarEntry);
            return;
        }
        manifestEntryVerifier.setEntry(null, jarEntry);
    }
    
    public void update(final int n, final ManifestEntryVerifier manifestEntryVerifier) throws IOException {
        if (n != -1) {
            if (this.parsingBlockOrSF) {
                this.baos.write(n);
            }
            else {
                manifestEntryVerifier.update((byte)n);
            }
        }
        else {
            this.processEntry(manifestEntryVerifier);
        }
    }
    
    public void update(final int n, final byte[] array, final int n2, final int n3, final ManifestEntryVerifier manifestEntryVerifier) throws IOException {
        if (n != -1) {
            if (this.parsingBlockOrSF) {
                this.baos.write(array, n2, n);
            }
            else {
                manifestEntryVerifier.update(array, n2, n);
            }
        }
        else {
            this.processEntry(manifestEntryVerifier);
        }
    }
    
    private void processEntry(final ManifestEntryVerifier manifestEntryVerifier) throws IOException {
        if (!this.parsingBlockOrSF) {
            final JarEntry entry = manifestEntryVerifier.getEntry();
            if (entry != null && entry.signers == null) {
                entry.signers = manifestEntryVerifier.verify(this.verifiedSigners, this.sigFileSigners);
                entry.certs = mapSignersToCertArray(entry.signers);
            }
        }
        else {
            try {
                this.parsingBlockOrSF = false;
                if (JarVerifier.debug != null) {
                    JarVerifier.debug.println("processEntry: processing block");
                }
                final String upperCase = manifestEntryVerifier.getEntry().getName().toUpperCase(Locale.ENGLISH);
                if (upperCase.endsWith(".SF")) {
                    final String substring = upperCase.substring(0, upperCase.length() - 3);
                    final byte[] byteArray = this.baos.toByteArray();
                    this.sigFileData.put(substring, byteArray);
                    for (final SignatureFileVerifier signatureFileVerifier : this.pendingBlocks) {
                        if (signatureFileVerifier.needSignatureFile(substring)) {
                            if (JarVerifier.debug != null) {
                                JarVerifier.debug.println("processEntry: processing pending block");
                            }
                            signatureFileVerifier.setSignatureFile(byteArray);
                            signatureFileVerifier.process(this.sigFileSigners, this.manifestDigests);
                        }
                    }
                    return;
                }
                final String substring2 = upperCase.substring(0, upperCase.lastIndexOf("."));
                if (this.signerCache == null) {
                    this.signerCache = new ArrayList<CodeSigner[]>();
                }
                if (this.manDig == null) {
                    synchronized (this.manifestRawBytes) {
                        if (this.manDig == null) {
                            this.manDig = new ManifestDigester(this.manifestRawBytes);
                            this.manifestRawBytes = null;
                        }
                    }
                }
                final SignatureFileVerifier signatureFileVerifier2 = new SignatureFileVerifier(this.signerCache, this.manDig, upperCase, this.baos.toByteArray());
                if (signatureFileVerifier2.needSignatureFileBytes()) {
                    final byte[] signatureFile = this.sigFileData.get(substring2);
                    if (signatureFile == null) {
                        if (JarVerifier.debug != null) {
                            JarVerifier.debug.println("adding pending block");
                        }
                        this.pendingBlocks.add(signatureFileVerifier2);
                        return;
                    }
                    signatureFileVerifier2.setSignatureFile(signatureFile);
                }
                signatureFileVerifier2.process(this.sigFileSigners, this.manifestDigests);
            }
            catch (final IOException ex) {
                if (JarVerifier.debug != null) {
                    JarVerifier.debug.println("processEntry caught: " + ex);
                }
            }
            catch (final SignatureException ex2) {
                if (JarVerifier.debug != null) {
                    JarVerifier.debug.println("processEntry caught: " + ex2);
                }
            }
            catch (final NoSuchAlgorithmException ex3) {
                if (JarVerifier.debug != null) {
                    JarVerifier.debug.println("processEntry caught: " + ex3);
                }
            }
            catch (final CertificateException ex4) {
                if (JarVerifier.debug != null) {
                    JarVerifier.debug.println("processEntry caught: " + ex4);
                }
            }
        }
    }
    
    @Deprecated
    public Certificate[] getCerts(final String s) {
        return mapSignersToCertArray(this.getCodeSigners(s));
    }
    
    public Certificate[] getCerts(final JarFile jarFile, final JarEntry jarEntry) {
        return mapSignersToCertArray(this.getCodeSigners(jarFile, jarEntry));
    }
    
    public CodeSigner[] getCodeSigners(final String s) {
        return this.verifiedSigners.get(s);
    }
    
    public CodeSigner[] getCodeSigners(final JarFile jarFile, final JarEntry jarEntry) {
        final String name = jarEntry.getName();
        if (this.eagerValidation && this.sigFileSigners.get(name) != null) {
            try {
                final InputStream inputStream = jarFile.getInputStream(jarEntry);
                final byte[] array = new byte[1024];
                for (int i = array.length; i != -1; i = inputStream.read(array, 0, array.length)) {}
                inputStream.close();
            }
            catch (final IOException ex) {}
        }
        return this.getCodeSigners(name);
    }
    
    private static Certificate[] mapSignersToCertArray(final CodeSigner[] array) {
        if (array != null) {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < array.length; ++i) {
                list.addAll(array[i].getSignerCertPath().getCertificates());
            }
            return list.toArray(new Certificate[list.size()]);
        }
        return null;
    }
    
    boolean nothingToVerify() {
        return !this.anyToVerify;
    }
    
    void doneWithMeta() {
        this.parsingMeta = false;
        this.anyToVerify = !this.sigFileSigners.isEmpty();
        this.baos = null;
        this.sigFileData = null;
        this.pendingBlocks = null;
        this.signerCache = null;
        this.manDig = null;
        if (this.sigFileSigners.containsKey("META-INF/MANIFEST.MF")) {
            this.verifiedSigners.put("META-INF/MANIFEST.MF", this.sigFileSigners.remove("META-INF/MANIFEST.MF"));
        }
    }
    
    private synchronized CodeSource mapSignersToCodeSource(final URL lastURL, final CodeSigner[] array) {
        Map<CodeSigner[], CodeSource> lastURLMap;
        if (lastURL == this.lastURL) {
            lastURLMap = this.lastURLMap;
        }
        else {
            lastURLMap = this.urlToCodeSourceMap.get(lastURL);
            if (lastURLMap == null) {
                lastURLMap = new HashMap<CodeSigner[], CodeSource>();
                this.urlToCodeSourceMap.put(lastURL, lastURLMap);
            }
            this.lastURLMap = lastURLMap;
            this.lastURL = lastURL;
        }
        CodeSource codeSource = lastURLMap.get(array);
        if (codeSource == null) {
            codeSource = new VerifierCodeSource(this.csdomain, lastURL, array);
            this.signerToCodeSource.put(array, codeSource);
        }
        return codeSource;
    }
    
    private CodeSource[] mapSignersToCodeSources(final URL url, final List<CodeSigner[]> list, final boolean b) {
        final ArrayList list2 = new ArrayList();
        for (int i = 0; i < list.size(); ++i) {
            list2.add(this.mapSignersToCodeSource(url, (CodeSigner[])list.get(i)));
        }
        if (b) {
            list2.add(this.mapSignersToCodeSource(url, null));
        }
        return (CodeSource[])list2.toArray(new CodeSource[list2.size()]);
    }
    
    private CodeSigner[] findMatchingSigners(final CodeSource codeSource) {
        if (codeSource instanceof VerifierCodeSource && ((VerifierCodeSource)codeSource).isSameDomain(this.csdomain)) {
            return ((VerifierCodeSource)codeSource).getPrivateSigners();
        }
        final CodeSource[] mapSignersToCodeSources = this.mapSignersToCodeSources(codeSource.getLocation(), this.getJarCodeSigners(), true);
        final ArrayList list = new ArrayList();
        for (int i = 0; i < mapSignersToCodeSources.length; ++i) {
            list.add(mapSignersToCodeSources[i]);
        }
        final int index = list.indexOf(codeSource);
        if (index != -1) {
            CodeSigner[] array = ((VerifierCodeSource)list.get(index)).getPrivateSigners();
            if (array == null) {
                array = this.emptySigner;
            }
            return array;
        }
        return null;
    }
    
    private synchronized Map<String, CodeSigner[]> signerMap() {
        if (this.signerMap == null) {
            (this.signerMap = new HashMap<String, CodeSigner[]>(this.verifiedSigners.size() + this.sigFileSigners.size())).putAll(this.verifiedSigners);
            this.signerMap.putAll(this.sigFileSigners);
        }
        return this.signerMap;
    }
    
    public synchronized Enumeration<String> entryNames(final JarFile jarFile, final CodeSource[] array) {
        final Iterator<Map.Entry<String, CodeSigner[]>> iterator = this.signerMap().entrySet().iterator();
        boolean b = false;
        final ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; ++i) {
            final CodeSigner[] matchingSigners = this.findMatchingSigners(array[i]);
            if (matchingSigners != null) {
                if (matchingSigners.length > 0) {
                    list.add(matchingSigners);
                }
                else {
                    b = true;
                }
            }
            else {
                b = true;
            }
        }
        return new Enumeration<String>() {
            String name;
            final /* synthetic */ Enumeration val$enum2 = b ? JarVerifier.this.unsignedEntryNames(jarFile) : JarVerifier.this.emptyEnumeration;
            
            @Override
            public boolean hasMoreElements() {
                if (this.name != null) {
                    return true;
                }
                while (iterator.hasNext()) {
                    final Map.Entry entry = iterator.next();
                    if (list.contains(entry.getValue())) {
                        this.name = (String)entry.getKey();
                        return true;
                    }
                }
                if (this.val$enum2.hasMoreElements()) {
                    this.name = this.val$enum2.nextElement();
                    return true;
                }
                return false;
            }
            
            @Override
            public String nextElement() {
                if (this.hasMoreElements()) {
                    final String name = this.name;
                    this.name = null;
                    return name;
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    public Enumeration<JarEntry> entries2(final JarFile jarFile, final Enumeration<? extends ZipEntry> enumeration) {
        final HashMap hashMap = new HashMap();
        hashMap.putAll(this.signerMap());
        return new Enumeration<JarEntry>() {
            Enumeration<String> signers = null;
            JarEntry entry;
            
            @Override
            public boolean hasMoreElements() {
                if (this.entry != null) {
                    return true;
                }
                while (enumeration.hasMoreElements()) {
                    final ZipEntry zipEntry = enumeration.nextElement();
                    if (JarVerifier.isSigningRelated(zipEntry.getName())) {
                        continue;
                    }
                    this.entry = jarFile.newEntry(zipEntry);
                    return true;
                }
                if (this.signers == null) {
                    this.signers = Collections.enumeration((Collection<String>)hashMap.keySet());
                }
                if (this.signers.hasMoreElements()) {
                    this.entry = jarFile.newEntry(new ZipEntry(this.signers.nextElement()));
                    return true;
                }
                return false;
            }
            
            @Override
            public JarEntry nextElement() {
                if (this.hasMoreElements()) {
                    final JarEntry entry = this.entry;
                    hashMap.remove(entry.getName());
                    this.entry = null;
                    return entry;
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    static boolean isSigningRelated(final String s) {
        return SignatureFileVerifier.isSigningRelated(s);
    }
    
    private Enumeration<String> unsignedEntryNames(final JarFile jarFile) {
        return new Enumeration<String>() {
            String name;
            final /* synthetic */ Enumeration val$entries = jarFile.entries();
            final /* synthetic */ Map val$map = JarVerifier.this.signerMap();
            
            @Override
            public boolean hasMoreElements() {
                if (this.name != null) {
                    return true;
                }
                while (this.val$entries.hasMoreElements()) {
                    final ZipEntry zipEntry = this.val$entries.nextElement();
                    final String name = zipEntry.getName();
                    if (!zipEntry.isDirectory()) {
                        if (JarVerifier.isSigningRelated(name)) {
                            continue;
                        }
                        if (this.val$map.get(name) == null) {
                            this.name = name;
                            return true;
                        }
                        continue;
                    }
                }
                return false;
            }
            
            @Override
            public String nextElement() {
                if (this.hasMoreElements()) {
                    final String name = this.name;
                    this.name = null;
                    return name;
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    private synchronized List<CodeSigner[]> getJarCodeSigners() {
        if (this.jarCodeSigners == null) {
            final HashSet set = new HashSet();
            set.addAll(this.signerMap().values());
            (this.jarCodeSigners = new ArrayList<CodeSigner[]>()).addAll(set);
        }
        return this.jarCodeSigners;
    }
    
    public synchronized CodeSource[] getCodeSources(final JarFile jarFile, final URL url) {
        return this.mapSignersToCodeSources(url, this.getJarCodeSigners(), this.unsignedEntryNames(jarFile).hasMoreElements());
    }
    
    public CodeSource getCodeSource(final URL url, final String s) {
        return this.mapSignersToCodeSource(url, this.signerMap().get(s));
    }
    
    public CodeSource getCodeSource(final URL url, final JarFile jarFile, final JarEntry jarEntry) {
        return this.mapSignersToCodeSource(url, this.getCodeSigners(jarFile, jarEntry));
    }
    
    public void setEagerValidation(final boolean eagerValidation) {
        this.eagerValidation = eagerValidation;
    }
    
    public synchronized List<Object> getManifestDigests() {
        return Collections.unmodifiableList((List<?>)this.manifestDigests);
    }
    
    static CodeSource getUnsignedCS(final URL url) {
        return new VerifierCodeSource(null, url, (Certificate[])null);
    }
    
    boolean isTrustedManifestEntry(final String s) {
        final CodeSigner[] array = this.verifiedSigners.get("META-INF/MANIFEST.MF");
        if (array == null) {
            return true;
        }
        CodeSigner[] array2 = this.sigFileSigners.get(s);
        if (array2 == null) {
            array2 = this.verifiedSigners.get(s);
        }
        return array2 != null && array2.length == array.length;
    }
    
    static {
        debug = Debug.getInstance("jar");
    }
    
    static class VerifierStream extends InputStream
    {
        private InputStream is;
        private JarVerifier jv;
        private ManifestEntryVerifier mev;
        private long numLeft;
        
        VerifierStream(final Manifest manifest, final JarEntry jarEntry, final InputStream is, final JarVerifier jv) throws IOException {
            this.is = is;
            this.jv = jv;
            this.mev = new ManifestEntryVerifier(manifest);
            this.jv.beginEntry(jarEntry, this.mev);
            this.numLeft = jarEntry.getSize();
            if (this.numLeft == 0L) {
                this.jv.update(-1, this.mev);
            }
        }
        
        @Override
        public int read() throws IOException {
            if (this.numLeft > 0L) {
                final int read = this.is.read();
                this.jv.update(read, this.mev);
                --this.numLeft;
                if (this.numLeft == 0L) {
                    this.jv.update(-1, this.mev);
                }
                return read;
            }
            return -1;
        }
        
        @Override
        public int read(final byte[] array, final int n, int n2) throws IOException {
            if (this.numLeft > 0L && this.numLeft < n2) {
                n2 = (int)this.numLeft;
            }
            if (this.numLeft > 0L) {
                final int read = this.is.read(array, n, n2);
                this.jv.update(read, array, n, n2, this.mev);
                this.numLeft -= read;
                if (this.numLeft == 0L) {
                    this.jv.update(-1, array, n, n2, this.mev);
                }
                return read;
            }
            return -1;
        }
        
        @Override
        public void close() throws IOException {
            if (this.is != null) {
                this.is.close();
            }
            this.is = null;
            this.mev = null;
            this.jv = null;
        }
        
        @Override
        public int available() throws IOException {
            return this.is.available();
        }
    }
    
    private static class VerifierCodeSource extends CodeSource
    {
        private static final long serialVersionUID = -9047366145967768825L;
        URL vlocation;
        CodeSigner[] vsigners;
        Certificate[] vcerts;
        Object csdomain;
        
        VerifierCodeSource(final Object csdomain, final URL vlocation, final CodeSigner[] vsigners) {
            super(vlocation, vsigners);
            this.csdomain = csdomain;
            this.vlocation = vlocation;
            this.vsigners = vsigners;
        }
        
        VerifierCodeSource(final Object csdomain, final URL vlocation, final Certificate[] vcerts) {
            super(vlocation, vcerts);
            this.csdomain = csdomain;
            this.vlocation = vlocation;
            this.vcerts = vcerts;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof VerifierCodeSource) {
                final VerifierCodeSource verifierCodeSource = (VerifierCodeSource)o;
                if (this.isSameDomain(verifierCodeSource.csdomain)) {
                    if (verifierCodeSource.vsigners != this.vsigners || verifierCodeSource.vcerts != this.vcerts) {
                        return false;
                    }
                    if (verifierCodeSource.vlocation != null) {
                        return verifierCodeSource.vlocation.equals(this.vlocation);
                    }
                    return this.vlocation == null || this.vlocation.equals(verifierCodeSource.vlocation);
                }
            }
            return super.equals(o);
        }
        
        boolean isSameDomain(final Object o) {
            return this.csdomain == o;
        }
        
        private CodeSigner[] getPrivateSigners() {
            return this.vsigners;
        }
        
        private Certificate[] getPrivateCertificates() {
            return this.vcerts;
        }
    }
}
