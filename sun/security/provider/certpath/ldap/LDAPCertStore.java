package sun.security.provider.certpath.ldap;

import java.util.Set;
import java.security.PublicKey;
import java.util.Date;
import java.math.BigInteger;
import java.io.IOException;
import sun.security.x509.X500Name;
import java.util.Locale;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.NameNotFoundException;
import com.sun.jndi.ldap.LdapReferralException;
import javax.naming.ldap.LdapContext;
import java.util.HashMap;
import javax.naming.InvalidNameException;
import javax.naming.CompositeName;
import java.util.List;
import java.util.Map;
import sun.security.action.GetPropertyAction;
import javax.naming.directory.BasicAttributes;
import java.net.URI;
import javax.security.auth.x500.X500Principal;
import java.security.cert.CRLSelector;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.util.HashSet;
import java.security.cert.CertSelector;
import java.util.Iterator;
import sun.security.provider.certpath.X509CertificatePair;
import java.security.cert.Certificate;
import sun.misc.HexDumpEncoder;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.X509CertSelector;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.util.Hashtable;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.security.cert.LDAPCertStoreParameters;
import sun.security.util.Cache;
import javax.naming.directory.DirContext;
import java.security.cert.CertificateFactory;
import javax.naming.directory.Attributes;
import sun.security.util.Debug;
import java.security.cert.CertStoreSpi;

public final class LDAPCertStore extends CertStoreSpi
{
    private static final Debug debug;
    private static final boolean DEBUG = false;
    private static final String USER_CERT = "userCertificate;binary";
    private static final String CA_CERT = "cACertificate;binary";
    private static final String CROSS_CERT = "crossCertificatePair;binary";
    private static final String CRL = "certificateRevocationList;binary";
    private static final String ARL = "authorityRevocationList;binary";
    private static final String DELTA_CRL = "deltaRevocationList;binary";
    private static final String[] STRING0;
    private static final byte[][] BB0;
    private static final Attributes EMPTY_ATTRIBUTES;
    private static final int DEFAULT_CACHE_SIZE = 750;
    private static final int DEFAULT_CACHE_LIFETIME = 30;
    private static final int LIFETIME;
    private static final String PROP_LIFETIME = "sun.security.certpath.ldap.cache.lifetime";
    private static final String PROP_DISABLE_APP_RESOURCE_FILES = "sun.security.certpath.ldap.disable.app.resource.files";
    private CertificateFactory cf;
    private DirContext ctx;
    private boolean prefetchCRLs;
    private final Cache<String, byte[][]> valueCache;
    private int cacheHits;
    private int cacheMisses;
    private int requests;
    private static final Cache<LDAPCertStoreParameters, CertStore> certStoreCache;
    
    public LDAPCertStore(final CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException {
        super(certStoreParameters);
        this.prefetchCRLs = false;
        this.cacheHits = 0;
        this.cacheMisses = 0;
        this.requests = 0;
        if (!(certStoreParameters instanceof LDAPCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException("parameters must be LDAPCertStoreParameters");
        }
        final LDAPCertStoreParameters ldapCertStoreParameters = (LDAPCertStoreParameters)certStoreParameters;
        this.createInitialDirContext(ldapCertStoreParameters.getServerName(), ldapCertStoreParameters.getPort());
        try {
            this.cf = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex) {
            throw new InvalidAlgorithmParameterException("unable to create CertificateFactory for X.509");
        }
        if (LDAPCertStore.LIFETIME == 0) {
            this.valueCache = Cache.newNullCache();
        }
        else if (LDAPCertStore.LIFETIME < 0) {
            this.valueCache = Cache.newSoftMemoryCache(750);
        }
        else {
            this.valueCache = Cache.newSoftMemoryCache(750, LDAPCertStore.LIFETIME);
        }
    }
    
    static synchronized CertStore getInstance(final LDAPCertStoreParameters ldapCertStoreParameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkConnect(ldapCertStoreParameters.getServerName(), ldapCertStoreParameters.getPort());
        }
        CertStore instance = LDAPCertStore.certStoreCache.get(ldapCertStoreParameters);
        if (instance == null) {
            instance = CertStore.getInstance("LDAP", ldapCertStoreParameters);
            LDAPCertStore.certStoreCache.put(ldapCertStoreParameters, instance);
        }
        else if (LDAPCertStore.debug != null) {
            LDAPCertStore.debug.println("LDAPCertStore.getInstance: cache hit");
        }
        return instance;
    }
    
    private void createInitialDirContext(final String s, final int n) throws InvalidAlgorithmParameterException {
        final String string = "ldap://" + s + ":" + n;
        final Hashtable hashtable = new Hashtable();
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        hashtable.put("java.naming.provider.url", string);
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.certpath.ldap.disable.app.resource.files"))) {
            if (LDAPCertStore.debug != null) {
                LDAPCertStore.debug.println("LDAPCertStore disabling app resource files");
            }
            hashtable.put("com.sun.naming.disable.app.resource.files", "true");
        }
        try {
            this.ctx = new InitialDirContext(hashtable);
            if (this.ctx.getEnvironment().get("java.naming.referral") == null) {
                this.ctx.addToEnvironment("java.naming.referral", "throw");
            }
        }
        catch (final NamingException ex) {
            if (LDAPCertStore.debug != null) {
                LDAPCertStore.debug.println("LDAPCertStore.engineInit about to throw InvalidAlgorithmParameterException");
                ex.printStackTrace();
            }
            final InvalidAlgorithmParameterException ex2 = new InvalidAlgorithmParameterException("unable to create InitialDirContext using supplied parameters");
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private Collection<X509Certificate> getCertificates(final LDAPRequest ldapRequest, final String s, final X509CertSelector x509CertSelector) throws CertStoreException {
        byte[][] values;
        try {
            values = ldapRequest.getValues(s);
        }
        catch (final NamingException ex) {
            throw new CertStoreException(ex);
        }
        final int length = values.length;
        if (length == 0) {
            return (Collection<X509Certificate>)Collections.emptySet();
        }
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(values[i]);
            try {
                final Certificate generateCertificate = this.cf.generateCertificate(byteArrayInputStream);
                if (x509CertSelector.match(generateCertificate)) {
                    list.add((Object)generateCertificate);
                }
            }
            catch (final CertificateException ex2) {
                if (LDAPCertStore.debug != null) {
                    LDAPCertStore.debug.println("LDAPCertStore.getCertificates() encountered exception while parsing cert, skipping the bad data: ");
                    LDAPCertStore.debug.println("[ " + new HexDumpEncoder().encodeBuffer(values[i]) + " ]");
                }
            }
        }
        return (Collection<X509Certificate>)list;
    }
    
    private Collection<X509CertificatePair> getCertPairs(final LDAPRequest ldapRequest, final String s) throws CertStoreException {
        byte[][] values;
        try {
            values = ldapRequest.getValues(s);
        }
        catch (final NamingException ex) {
            throw new CertStoreException(ex);
        }
        final int length = values.length;
        if (length == 0) {
            return (Collection<X509CertificatePair>)Collections.emptySet();
        }
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            try {
                list.add((Object)X509CertificatePair.generateCertificatePair(values[i]));
            }
            catch (final CertificateException ex2) {
                if (LDAPCertStore.debug != null) {
                    LDAPCertStore.debug.println("LDAPCertStore.getCertPairs() encountered exception while parsing cert, skipping the bad data: ");
                    LDAPCertStore.debug.println("[ " + new HexDumpEncoder().encodeBuffer(values[i]) + " ]");
                }
            }
        }
        return (Collection<X509CertificatePair>)list;
    }
    
    private Collection<X509Certificate> getMatchingCrossCerts(final LDAPRequest ldapRequest, final X509CertSelector x509CertSelector, final X509CertSelector x509CertSelector2) throws CertStoreException {
        final Collection<X509CertificatePair> certPairs = this.getCertPairs(ldapRequest, "crossCertificatePair;binary");
        final ArrayList list = new ArrayList();
        for (final X509CertificatePair x509CertificatePair : certPairs) {
            if (x509CertSelector != null) {
                final X509Certificate forward = x509CertificatePair.getForward();
                if (forward != null && x509CertSelector.match(forward)) {
                    list.add(forward);
                }
            }
            if (x509CertSelector2 != null) {
                final X509Certificate reverse = x509CertificatePair.getReverse();
                if (reverse == null || !x509CertSelector2.match(reverse)) {
                    continue;
                }
                list.add(reverse);
            }
        }
        return list;
    }
    
    @Override
    public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector certSelector) throws CertStoreException {
        if (LDAPCertStore.debug != null) {
            LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() selector: " + String.valueOf(certSelector));
        }
        if (certSelector == null) {
            certSelector = new X509CertSelector();
        }
        if (!(certSelector instanceof X509CertSelector)) {
            throw new CertStoreException("LDAPCertStore needs an X509CertSelector to find certs");
        }
        final X509CertSelector x509CertSelector = (X509CertSelector)certSelector;
        final int basicConstraints = x509CertSelector.getBasicConstraints();
        final String subjectAsString = x509CertSelector.getSubjectAsString();
        final String issuerAsString = x509CertSelector.getIssuerAsString();
        final HashSet set = new HashSet();
        if (LDAPCertStore.debug != null) {
            LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() basicConstraints: " + basicConstraints);
        }
        if (subjectAsString != null) {
            if (LDAPCertStore.debug != null) {
                LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() subject is not null");
            }
            final LDAPRequest ldapRequest = new LDAPRequest(subjectAsString);
            if (basicConstraints > -2) {
                ldapRequest.addRequestedAttribute("crossCertificatePair;binary");
                ldapRequest.addRequestedAttribute("cACertificate;binary");
                ldapRequest.addRequestedAttribute("authorityRevocationList;binary");
                if (this.prefetchCRLs) {
                    ldapRequest.addRequestedAttribute("certificateRevocationList;binary");
                }
            }
            if (basicConstraints < 0) {
                ldapRequest.addRequestedAttribute("userCertificate;binary");
            }
            if (basicConstraints > -2) {
                set.addAll(this.getMatchingCrossCerts(ldapRequest, x509CertSelector, null));
                if (LDAPCertStore.debug != null) {
                    LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(subject,xsel,null),certs.size(): " + set.size());
                }
                set.addAll(this.getCertificates(ldapRequest, "cACertificate;binary", x509CertSelector));
                if (LDAPCertStore.debug != null) {
                    LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,CA_CERT,xsel),certs.size(): " + set.size());
                }
            }
            if (basicConstraints < 0) {
                set.addAll(this.getCertificates(ldapRequest, "userCertificate;binary", x509CertSelector));
                if (LDAPCertStore.debug != null) {
                    LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,USER_CERT, xsel),certs.size(): " + set.size());
                }
            }
        }
        else {
            if (LDAPCertStore.debug != null) {
                LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() subject is null");
            }
            if (basicConstraints == -2) {
                throw new CertStoreException("need subject to find EE certs");
            }
            if (issuerAsString == null) {
                throw new CertStoreException("need subject or issuer to find certs");
            }
        }
        if (LDAPCertStore.debug != null) {
            LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() about to getMatchingCrossCerts...");
        }
        if (issuerAsString != null && basicConstraints > -2) {
            final LDAPRequest ldapRequest2 = new LDAPRequest(issuerAsString);
            ldapRequest2.addRequestedAttribute("crossCertificatePair;binary");
            ldapRequest2.addRequestedAttribute("cACertificate;binary");
            ldapRequest2.addRequestedAttribute("authorityRevocationList;binary");
            if (this.prefetchCRLs) {
                ldapRequest2.addRequestedAttribute("certificateRevocationList;binary");
            }
            set.addAll(this.getMatchingCrossCerts(ldapRequest2, null, x509CertSelector));
            if (LDAPCertStore.debug != null) {
                LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(issuer,null,xsel),certs.size(): " + set.size());
            }
            set.addAll(this.getCertificates(ldapRequest2, "cACertificate;binary", x509CertSelector));
            if (LDAPCertStore.debug != null) {
                LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(issuer,CA_CERT,xsel),certs.size(): " + set.size());
            }
        }
        if (LDAPCertStore.debug != null) {
            LDAPCertStore.debug.println("LDAPCertStore.engineGetCertificates() returning certs");
        }
        return set;
    }
    
    private Collection<X509CRL> getCRLs(final LDAPRequest ldapRequest, final String s, final X509CRLSelector x509CRLSelector) throws CertStoreException {
        byte[][] values;
        try {
            values = ldapRequest.getValues(s);
        }
        catch (final NamingException ex) {
            throw new CertStoreException(ex);
        }
        final int length = values.length;
        if (length == 0) {
            return (Collection<X509CRL>)Collections.emptySet();
        }
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            try {
                final CRL generateCRL = this.cf.generateCRL(new ByteArrayInputStream(values[i]));
                if (x509CRLSelector.match(generateCRL)) {
                    list.add((Object)generateCRL);
                }
            }
            catch (final CRLException ex2) {
                if (LDAPCertStore.debug != null) {
                    LDAPCertStore.debug.println("LDAPCertStore.getCRLs() encountered exception while parsing CRL, skipping the bad data: ");
                    LDAPCertStore.debug.println("[ " + new HexDumpEncoder().encodeBuffer(values[i]) + " ]");
                }
            }
        }
        return (Collection<X509CRL>)list;
    }
    
    @Override
    public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector crlSelector) throws CertStoreException {
        if (LDAPCertStore.debug != null) {
            LDAPCertStore.debug.println("LDAPCertStore.engineGetCRLs() selector: " + crlSelector);
        }
        if (crlSelector == null) {
            crlSelector = new X509CRLSelector();
        }
        if (!(crlSelector instanceof X509CRLSelector)) {
            throw new CertStoreException("need X509CRLSelector to find CRLs");
        }
        final X509CRLSelector x509CRLSelector = (X509CRLSelector)crlSelector;
        final HashSet set = new HashSet();
        final X509Certificate certificateChecking = x509CRLSelector.getCertificateChecking();
        Object issuerNames;
        if (certificateChecking != null) {
            issuerNames = new HashSet<String>();
            ((Collection<String>)issuerNames).add(certificateChecking.getIssuerX500Principal().getName("RFC2253"));
        }
        else {
            issuerNames = x509CRLSelector.getIssuerNames();
            if (issuerNames == null) {
                throw new CertStoreException("need issuerNames or certChecking to find CRLs");
            }
        }
        for (final String next : issuerNames) {
            String name = null;
            Label_0216: {
                if (next instanceof byte[]) {
                    try {
                        name = new X500Principal((byte[])(Object)next).getName("RFC2253");
                        break Label_0216;
                    }
                    catch (final IllegalArgumentException ex) {
                        continue;
                    }
                }
                name = next;
            }
            Object o = Collections.emptySet();
            if (certificateChecking == null || certificateChecking.getBasicConstraints() != -1) {
                final LDAPRequest ldapRequest = new LDAPRequest(name);
                ldapRequest.addRequestedAttribute("crossCertificatePair;binary");
                ldapRequest.addRequestedAttribute("cACertificate;binary");
                ldapRequest.addRequestedAttribute("authorityRevocationList;binary");
                if (this.prefetchCRLs) {
                    ldapRequest.addRequestedAttribute("certificateRevocationList;binary");
                }
                try {
                    o = this.getCRLs(ldapRequest, "authorityRevocationList;binary", x509CRLSelector);
                    if (((Collection)o).isEmpty()) {
                        this.prefetchCRLs = true;
                    }
                    else {
                        set.addAll((Collection)o);
                    }
                }
                catch (final CertStoreException ex2) {
                    if (LDAPCertStore.debug != null) {
                        LDAPCertStore.debug.println("LDAPCertStore.engineGetCRLs non-fatal error retrieving ARLs:" + ex2);
                        ex2.printStackTrace();
                    }
                }
            }
            if (((Collection)o).isEmpty() || certificateChecking == null) {
                final LDAPRequest ldapRequest2 = new LDAPRequest(name);
                ldapRequest2.addRequestedAttribute("certificateRevocationList;binary");
                set.addAll(this.getCRLs(ldapRequest2, "certificateRevocationList;binary", x509CRLSelector));
            }
        }
        return set;
    }
    
    static LDAPCertStoreParameters getParameters(final URI uri) {
        final String host = uri.getHost();
        if (host == null) {
            return new SunLDAPCertStoreParameters();
        }
        final int port = uri.getPort();
        return (port == -1) ? new SunLDAPCertStoreParameters(host) : new SunLDAPCertStoreParameters(host, port);
    }
    
    static {
        debug = Debug.getInstance("certpath");
        STRING0 = new String[0];
        BB0 = new byte[0][];
        EMPTY_ATTRIBUTES = new BasicAttributes();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.certpath.ldap.cache.lifetime"));
        if (s != null) {
            LIFETIME = Integer.parseInt(s);
        }
        else {
            LIFETIME = 30;
        }
        certStoreCache = Cache.newSoftMemoryCache(185);
    }
    
    private class LDAPRequest
    {
        private final String name;
        private Map<String, byte[][]> valueMap;
        private final List<String> requestedAttributes;
        
        LDAPRequest(final String s) throws CertStoreException {
            this.name = this.checkName(s);
            this.requestedAttributes = new ArrayList<String>(5);
        }
        
        private String checkName(final String s) throws CertStoreException {
            if (s == null) {
                throw new CertStoreException("Name absent");
            }
            try {
                if (new CompositeName(s).size() > 1) {
                    throw new CertStoreException("Invalid name: " + s);
                }
            }
            catch (final InvalidNameException ex) {
                throw new CertStoreException("Invalid name: " + s, ex);
            }
            return s;
        }
        
        String getName() {
            return this.name;
        }
        
        void addRequestedAttribute(final String s) {
            if (this.valueMap != null) {
                throw new IllegalStateException("Request already sent");
            }
            this.requestedAttributes.add(s);
        }
        
        byte[][] getValues(final String s) throws NamingException {
            final byte[][] array = LDAPCertStore.this.valueCache.get(this.name + "|" + s);
            if (array != null) {
                LDAPCertStore.this.cacheHits++;
                return array;
            }
            LDAPCertStore.this.cacheMisses++;
            return this.getValueMap().get(s);
        }
        
        private Map<String, byte[][]> getValueMap() throws NamingException {
            if (this.valueMap != null) {
                return this.valueMap;
            }
            this.valueMap = new HashMap<String, byte[][]>(8);
            final String[] array = this.requestedAttributes.toArray(LDAPCertStore.STRING0);
            Attributes attributes;
            try {
                attributes = LDAPCertStore.this.ctx.getAttributes(this.name, array);
            }
            catch (final LdapReferralException ex) {
                while (true) {
                    try {
                        final URI uri = new URI((String)ex.getReferralInfo());
                        if (!uri.getScheme().equalsIgnoreCase("ldap")) {
                            throw new IllegalArgumentException("Not LDAP");
                        }
                        String s = uri.getPath();
                        if (s != null && s.charAt(0) == '/') {
                            s = s.substring(1);
                        }
                        this.checkName(s);
                    }
                    catch (final Exception ex2) {
                        throw new NamingException("Cannot follow referral to " + ex.getReferralInfo());
                    }
                    final LdapContext ldapContext = (LdapContext)ex.getReferralContext();
                    try {
                        attributes = ldapContext.getAttributes(this.name, array);
                    }
                    catch (final LdapReferralException ex3) {
                        ex = ex3;
                        continue;
                    }
                    finally {
                        ldapContext.close();
                    }
                    break;
                }
            }
            catch (final NameNotFoundException ex4) {
                attributes = LDAPCertStore.EMPTY_ATTRIBUTES;
            }
            for (final String s2 : this.requestedAttributes) {
                final byte[][] attributeValues = this.getAttributeValues(attributes.get(s2));
                this.cacheAttribute(s2, attributeValues);
                this.valueMap.put(s2, attributeValues);
            }
            return this.valueMap;
        }
        
        private void cacheAttribute(final String s, final byte[][] array) {
            LDAPCertStore.this.valueCache.put(this.name + "|" + s, array);
        }
        
        private byte[][] getAttributeValues(final Attribute attribute) throws NamingException {
            byte[][] access$600;
            if (attribute == null) {
                access$600 = LDAPCertStore.BB0;
            }
            else {
                access$600 = new byte[attribute.size()][];
                int n = 0;
                final NamingEnumeration<?> all = attribute.getAll();
                while (all.hasMore()) {
                    final Object next = all.next();
                    if (LDAPCertStore.debug != null && next instanceof String) {
                        LDAPCertStore.debug.println("LDAPCertStore.getAttrValues() enum.next is a string!: " + next);
                    }
                    access$600[n++] = (byte[])next;
                }
            }
            return access$600;
        }
    }
    
    private static class SunLDAPCertStoreParameters extends LDAPCertStoreParameters
    {
        private volatile int hashCode;
        
        SunLDAPCertStoreParameters(final String s, final int n) {
            super(s, n);
            this.hashCode = 0;
        }
        
        SunLDAPCertStoreParameters(final String s) {
            super(s);
            this.hashCode = 0;
        }
        
        SunLDAPCertStoreParameters() {
            this.hashCode = 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof LDAPCertStoreParameters)) {
                return false;
            }
            final LDAPCertStoreParameters ldapCertStoreParameters = (LDAPCertStoreParameters)o;
            return this.getPort() == ldapCertStoreParameters.getPort() && this.getServerName().equalsIgnoreCase(ldapCertStoreParameters.getServerName());
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                this.hashCode = 37 * (37 * 17 + this.getPort()) + this.getServerName().toLowerCase(Locale.ENGLISH).hashCode();
            }
            return this.hashCode;
        }
    }
    
    static class LDAPCertSelector extends X509CertSelector
    {
        private X500Principal certSubject;
        private X509CertSelector selector;
        private X500Principal subject;
        
        LDAPCertSelector(final X509CertSelector x509CertSelector, final X500Principal certSubject, final String s) throws IOException {
            this.selector = ((x509CertSelector == null) ? new X509CertSelector() : x509CertSelector);
            this.certSubject = certSubject;
            this.subject = new X500Name(s).asX500Principal();
        }
        
        @Override
        public X509Certificate getCertificate() {
            return this.selector.getCertificate();
        }
        
        @Override
        public BigInteger getSerialNumber() {
            return this.selector.getSerialNumber();
        }
        
        @Override
        public X500Principal getIssuer() {
            return this.selector.getIssuer();
        }
        
        @Override
        public String getIssuerAsString() {
            return this.selector.getIssuerAsString();
        }
        
        @Override
        public byte[] getIssuerAsBytes() throws IOException {
            return this.selector.getIssuerAsBytes();
        }
        
        @Override
        public X500Principal getSubject() {
            return this.subject;
        }
        
        @Override
        public String getSubjectAsString() {
            return this.subject.getName();
        }
        
        @Override
        public byte[] getSubjectAsBytes() throws IOException {
            return this.subject.getEncoded();
        }
        
        @Override
        public byte[] getSubjectKeyIdentifier() {
            return this.selector.getSubjectKeyIdentifier();
        }
        
        @Override
        public byte[] getAuthorityKeyIdentifier() {
            return this.selector.getAuthorityKeyIdentifier();
        }
        
        @Override
        public Date getCertificateValid() {
            return this.selector.getCertificateValid();
        }
        
        @Override
        public Date getPrivateKeyValid() {
            return this.selector.getPrivateKeyValid();
        }
        
        @Override
        public String getSubjectPublicKeyAlgID() {
            return this.selector.getSubjectPublicKeyAlgID();
        }
        
        @Override
        public PublicKey getSubjectPublicKey() {
            return this.selector.getSubjectPublicKey();
        }
        
        @Override
        public boolean[] getKeyUsage() {
            return this.selector.getKeyUsage();
        }
        
        @Override
        public Set<String> getExtendedKeyUsage() {
            return this.selector.getExtendedKeyUsage();
        }
        
        @Override
        public boolean getMatchAllSubjectAltNames() {
            return this.selector.getMatchAllSubjectAltNames();
        }
        
        @Override
        public Collection<List<?>> getSubjectAlternativeNames() {
            return this.selector.getSubjectAlternativeNames();
        }
        
        @Override
        public byte[] getNameConstraints() {
            return this.selector.getNameConstraints();
        }
        
        @Override
        public int getBasicConstraints() {
            return this.selector.getBasicConstraints();
        }
        
        @Override
        public Set<String> getPolicy() {
            return this.selector.getPolicy();
        }
        
        @Override
        public Collection<List<?>> getPathToNames() {
            return this.selector.getPathToNames();
        }
        
        @Override
        public boolean match(final Certificate certificate) {
            this.selector.setSubject(this.certSubject);
            final boolean match = this.selector.match(certificate);
            this.selector.setSubject(this.subject);
            return match;
        }
    }
    
    static class LDAPCRLSelector extends X509CRLSelector
    {
        private X509CRLSelector selector;
        private Collection<X500Principal> certIssuers;
        private Collection<X500Principal> issuers;
        private HashSet<Object> issuerNames;
        
        LDAPCRLSelector(final X509CRLSelector x509CRLSelector, final Collection<X500Principal> certIssuers, final String s) throws IOException {
            this.selector = ((x509CRLSelector == null) ? new X509CRLSelector() : x509CRLSelector);
            this.certIssuers = certIssuers;
            (this.issuerNames = new HashSet<Object>()).add(s);
            (this.issuers = new HashSet<X500Principal>()).add(new X500Name(s).asX500Principal());
        }
        
        @Override
        public Collection<X500Principal> getIssuers() {
            return Collections.unmodifiableCollection((Collection<? extends X500Principal>)this.issuers);
        }
        
        @Override
        public Collection<Object> getIssuerNames() {
            return Collections.unmodifiableCollection((Collection<?>)this.issuerNames);
        }
        
        @Override
        public BigInteger getMinCRL() {
            return this.selector.getMinCRL();
        }
        
        @Override
        public BigInteger getMaxCRL() {
            return this.selector.getMaxCRL();
        }
        
        @Override
        public Date getDateAndTime() {
            return this.selector.getDateAndTime();
        }
        
        @Override
        public X509Certificate getCertificateChecking() {
            return this.selector.getCertificateChecking();
        }
        
        @Override
        public boolean match(final CRL crl) {
            this.selector.setIssuers(this.certIssuers);
            final boolean match = this.selector.match(crl);
            this.selector.setIssuers(this.issuers);
            return match;
        }
    }
}
