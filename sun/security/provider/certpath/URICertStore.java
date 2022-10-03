package sun.security.provider.certpath;

import java.security.cert.CRL;
import java.security.cert.CRLException;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509CRLSelector;
import java.security.cert.CRLSelector;
import java.util.Iterator;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.security.cert.CertStoreException;
import java.security.cert.X509CertSelector;
import java.security.cert.CertSelector;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.URIName;
import sun.security.x509.AccessDescription;
import java.security.Provider;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.security.InvalidAlgorithmParameterException;
import java.util.Collections;
import java.security.cert.CertStoreParameters;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import sun.security.util.Cache;
import java.security.cert.CertStore;
import java.net.URI;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.CertificateFactory;
import sun.security.util.Debug;
import java.security.cert.CertStoreSpi;

class URICertStore extends CertStoreSpi
{
    private static final Debug debug;
    private static final int CHECK_INTERVAL = 30000;
    private static final int CACHE_SIZE = 185;
    private final CertificateFactory factory;
    private Collection<X509Certificate> certs;
    private X509CRL crl;
    private long lastChecked;
    private long lastModified;
    private URI uri;
    private boolean ldap;
    private CertStoreHelper ldapHelper;
    private CertStore ldapCertStore;
    private String ldapPath;
    private static final int DEFAULT_CRL_CONNECT_TIMEOUT = 15000;
    private static final int CRL_CONNECT_TIMEOUT;
    private static final Cache<URICertStoreParameters, CertStore> certStoreCache;
    
    private static int initializeTimeout() {
        final Integer n = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("com.sun.security.crl.timeout"));
        if (n == null || n < 0) {
            return 15000;
        }
        return n * 1000;
    }
    
    URICertStore(final CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        super(certStoreParameters);
        this.certs = (Collection<X509Certificate>)Collections.emptySet();
        this.ldap = false;
        if (!(certStoreParameters instanceof URICertStoreParameters)) {
            throw new InvalidAlgorithmParameterException("params must be instanceof URICertStoreParameters");
        }
        this.uri = ((URICertStoreParameters)certStoreParameters).uri;
        if (this.uri.getScheme().toLowerCase(Locale.ENGLISH).equals("ldap")) {
            this.ldap = true;
            this.ldapHelper = CertStoreHelper.getInstance("LDAP");
            this.ldapCertStore = this.ldapHelper.getCertStore(this.uri);
            this.ldapPath = this.uri.getPath();
            if (this.ldapPath.charAt(0) == '/') {
                this.ldapPath = this.ldapPath.substring(1);
            }
        }
        try {
            this.factory = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex) {
            throw new RuntimeException();
        }
    }
    
    static synchronized CertStore getInstance(final URICertStoreParameters uriCertStoreParameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        if (URICertStore.debug != null) {
            URICertStore.debug.println("CertStore URI:" + uriCertStoreParameters.uri);
        }
        CertStore certStore = URICertStore.certStoreCache.get(uriCertStoreParameters);
        if (certStore == null) {
            certStore = new UCS(new URICertStore(uriCertStoreParameters), null, "URI", uriCertStoreParameters);
            URICertStore.certStoreCache.put(uriCertStoreParameters, certStore);
        }
        else if (URICertStore.debug != null) {
            URICertStore.debug.println("URICertStore.getInstance: cache hit");
        }
        return certStore;
    }
    
    static CertStore getInstance(final AccessDescription accessDescription) {
        if (!accessDescription.getAccessMethod().equals((Object)AccessDescription.Ad_CAISSUERS_Id)) {
            return null;
        }
        final GeneralNameInterface name = accessDescription.getAccessLocation().getName();
        if (!(name instanceof URIName)) {
            return null;
        }
        final URI uri = ((URIName)name).getURI();
        try {
            return getInstance(new URICertStoreParameters(uri));
        }
        catch (final Exception ex) {
            if (URICertStore.debug != null) {
                URICertStore.debug.println("exception creating CertStore: " + ex);
                ex.printStackTrace();
            }
            return null;
        }
    }
    
    @Override
    public synchronized Collection<X509Certificate> engineGetCertificates(final CertSelector certSelector) throws CertStoreException {
        if (this.ldap) {
            final X509CertSelector x509CertSelector = (X509CertSelector)certSelector;
            X509CertSelector wrap;
            try {
                wrap = this.ldapHelper.wrap(x509CertSelector, x509CertSelector.getSubject(), this.ldapPath);
            }
            catch (final IOException ex) {
                throw new CertStoreException(ex);
            }
            return (Collection<X509Certificate>)this.ldapCertStore.getCertificates(wrap);
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastChecked < 30000L) {
            if (URICertStore.debug != null) {
                URICertStore.debug.println("Returning certificates from cache");
            }
            return getMatchingCerts(this.certs, certSelector);
        }
        this.lastChecked = currentTimeMillis;
        try {
            final URLConnection openConnection = this.uri.toURL().openConnection();
            if (this.lastModified != 0L) {
                openConnection.setIfModifiedSince(this.lastModified);
            }
            final long lastModified = this.lastModified;
            try (final InputStream inputStream = openConnection.getInputStream()) {
                this.lastModified = openConnection.getLastModified();
                if (lastModified != 0L) {
                    if (lastModified == this.lastModified) {
                        if (URICertStore.debug != null) {
                            URICertStore.debug.println("Not modified, using cached copy");
                        }
                        return getMatchingCerts(this.certs, certSelector);
                    }
                    if (openConnection instanceof HttpURLConnection && ((HttpURLConnection)openConnection).getResponseCode() == 304) {
                        if (URICertStore.debug != null) {
                            URICertStore.debug.println("Not modified, using cached copy");
                        }
                        return getMatchingCerts(this.certs, certSelector);
                    }
                }
                if (URICertStore.debug != null) {
                    URICertStore.debug.println("Downloading new certificates...");
                }
                this.certs = (Collection<X509Certificate>)this.factory.generateCertificates(inputStream);
            }
            return getMatchingCerts(this.certs, certSelector);
        }
        catch (final IOException | CertificateException ex2) {
            if (URICertStore.debug != null) {
                URICertStore.debug.println("Exception fetching certificates:");
                ((Throwable)ex2).printStackTrace();
            }
            this.lastModified = 0L;
            return this.certs = (Collection<X509Certificate>)Collections.emptySet();
        }
    }
    
    private static Collection<X509Certificate> getMatchingCerts(final Collection<X509Certificate> collection, final CertSelector certSelector) {
        if (certSelector == null) {
            return collection;
        }
        final ArrayList list = new ArrayList(collection.size());
        for (final X509Certificate x509Certificate : collection) {
            if (certSelector.match(x509Certificate)) {
                list.add(x509Certificate);
            }
        }
        return list;
    }
    
    @Override
    public synchronized Collection<X509CRL> engineGetCRLs(final CRLSelector crlSelector) throws CertStoreException {
        if (this.ldap) {
            final X509CRLSelector x509CRLSelector = (X509CRLSelector)crlSelector;
            X509CRLSelector wrap;
            try {
                wrap = this.ldapHelper.wrap(x509CRLSelector, null, this.ldapPath);
            }
            catch (final IOException ex) {
                throw new CertStoreException(ex);
            }
            try {
                return (Collection<X509CRL>)this.ldapCertStore.getCRLs(wrap);
            }
            catch (final CertStoreException ex2) {
                throw new PKIX.CertStoreTypeException("LDAP", ex2);
            }
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastChecked < 30000L) {
            if (URICertStore.debug != null) {
                URICertStore.debug.println("Returning CRL from cache");
            }
            return getMatchingCRLs(this.crl, crlSelector);
        }
        this.lastChecked = currentTimeMillis;
        try {
            final URLConnection openConnection = this.uri.toURL().openConnection();
            if (this.lastModified != 0L) {
                openConnection.setIfModifiedSince(this.lastModified);
            }
            final long lastModified = this.lastModified;
            openConnection.setConnectTimeout(URICertStore.CRL_CONNECT_TIMEOUT);
            try (final InputStream inputStream = openConnection.getInputStream()) {
                this.lastModified = openConnection.getLastModified();
                if (lastModified != 0L) {
                    if (lastModified == this.lastModified) {
                        if (URICertStore.debug != null) {
                            URICertStore.debug.println("Not modified, using cached copy");
                        }
                        return getMatchingCRLs(this.crl, crlSelector);
                    }
                    if (openConnection instanceof HttpURLConnection && ((HttpURLConnection)openConnection).getResponseCode() == 304) {
                        if (URICertStore.debug != null) {
                            URICertStore.debug.println("Not modified, using cached copy");
                        }
                        return getMatchingCRLs(this.crl, crlSelector);
                    }
                }
                if (URICertStore.debug != null) {
                    URICertStore.debug.println("Downloading new CRL...");
                }
                this.crl = (X509CRL)this.factory.generateCRL(inputStream);
            }
            return getMatchingCRLs(this.crl, crlSelector);
        }
        catch (final IOException | CRLException ex3) {
            if (URICertStore.debug != null) {
                URICertStore.debug.println("Exception fetching CRL:");
                ((Throwable)ex3).printStackTrace();
            }
            this.lastModified = 0L;
            this.crl = null;
            throw new PKIX.CertStoreTypeException("URI", new CertStoreException((Throwable)ex3));
        }
    }
    
    private static Collection<X509CRL> getMatchingCRLs(final X509CRL x509CRL, final CRLSelector crlSelector) {
        if (crlSelector == null || (x509CRL != null && crlSelector.match(x509CRL))) {
            return Collections.singletonList(x509CRL);
        }
        return (Collection<X509CRL>)Collections.emptyList();
    }
    
    static {
        debug = Debug.getInstance("certpath");
        CRL_CONNECT_TIMEOUT = initializeTimeout();
        certStoreCache = Cache.newSoftMemoryCache(185);
    }
    
    static class URICertStoreParameters implements CertStoreParameters
    {
        private final URI uri;
        private volatile int hashCode;
        
        URICertStoreParameters(final URI uri) {
            this.hashCode = 0;
            this.uri = uri;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof URICertStoreParameters && this.uri.equals(((URICertStoreParameters)o).uri);
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                this.hashCode = 37 * 17 + this.uri.hashCode();
            }
            return this.hashCode;
        }
        
        @Override
        public Object clone() {
            try {
                return super.clone();
            }
            catch (final CloneNotSupportedException ex) {
                throw new InternalError(ex.toString(), ex);
            }
        }
    }
    
    private static class UCS extends CertStore
    {
        protected UCS(final CertStoreSpi certStoreSpi, final Provider provider, final String s, final CertStoreParameters certStoreParameters) {
            super(certStoreSpi, provider, s, certStoreParameters);
        }
    }
}
