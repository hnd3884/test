package org.bouncycastle.jce.provider;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import java.security.cert.CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.CRLSelector;
import javax.security.auth.x500.X500Principal;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Set;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.asn1.ASN1InputStream;
import java.util.ArrayList;
import java.security.cert.CertificateFactory;
import java.util.HashSet;
import java.security.cert.CertStoreException;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import java.security.cert.CertSelector;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.directory.InitialDirContext;
import java.util.Properties;
import javax.naming.directory.DirContext;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertStoreParameters;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import java.security.cert.CertStoreSpi;

public class X509LDAPCertStoreSpi extends CertStoreSpi
{
    private X509LDAPCertStoreParameters params;
    private static String LDAP_PROVIDER;
    private static String REFERRALS_IGNORE;
    private static final String SEARCH_SECURITY_LEVEL = "none";
    private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
    
    public X509LDAPCertStoreSpi(final CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException {
        super(certStoreParameters);
        if (!(certStoreParameters instanceof X509LDAPCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException(X509LDAPCertStoreSpi.class.getName() + ": parameter must be a " + X509LDAPCertStoreParameters.class.getName() + " object\n" + certStoreParameters.toString());
        }
        this.params = (X509LDAPCertStoreParameters)certStoreParameters;
    }
    
    private DirContext connectLDAP() throws NamingException {
        final Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", X509LDAPCertStoreSpi.LDAP_PROVIDER);
        properties.setProperty("java.naming.batchsize", "0");
        properties.setProperty("java.naming.provider.url", this.params.getLdapURL());
        properties.setProperty("java.naming.factory.url.pkgs", "com.sun.jndi.url");
        properties.setProperty("java.naming.referral", X509LDAPCertStoreSpi.REFERRALS_IGNORE);
        properties.setProperty("java.naming.security.authentication", "none");
        return new InitialDirContext(properties);
    }
    
    private String parseDN(final String s, final String s2) {
        final String substring = s.substring(s.toLowerCase().indexOf(s2.toLowerCase()) + s2.length());
        int n = substring.indexOf(44);
        if (n == -1) {
            n = substring.length();
        }
        while (substring.charAt(n - 1) == '\\') {
            n = substring.indexOf(44, n + 1);
            if (n == -1) {
                n = substring.length();
            }
        }
        final String substring2 = substring.substring(0, n);
        String s3 = substring2.substring(substring2.indexOf(61) + 1);
        if (s3.charAt(0) == ' ') {
            s3 = s3.substring(1);
        }
        if (s3.startsWith("\"")) {
            s3 = s3.substring(1);
        }
        if (s3.endsWith("\"")) {
            s3 = s3.substring(0, s3.length() - 1);
        }
        return s3;
    }
    
    @Override
    public Collection engineGetCertificates(final CertSelector certSelector) throws CertStoreException {
        if (!(certSelector instanceof X509CertSelector)) {
            throw new CertStoreException("selector is not a X509CertSelector");
        }
        final X509CertSelector x509CertSelector = (X509CertSelector)certSelector;
        final HashSet set = new HashSet();
        final Set endCertificates = this.getEndCertificates(x509CertSelector);
        endCertificates.addAll(this.getCACertificates(x509CertSelector));
        endCertificates.addAll(this.getCrossCertificates(x509CertSelector));
        final Iterator iterator = endCertificates.iterator();
        try {
            final CertificateFactory instance = CertificateFactory.getInstance("X.509", "BC");
            while (iterator.hasNext()) {
                final byte[] array = (byte[])iterator.next();
                if (array != null) {
                    if (array.length == 0) {
                        continue;
                    }
                    final ArrayList list = new ArrayList();
                    list.add(array);
                    try {
                        final CertificatePair instance2 = CertificatePair.getInstance(new ASN1InputStream(array).readObject());
                        list.clear();
                        if (instance2.getForward() != null) {
                            list.add(instance2.getForward().getEncoded());
                        }
                        if (instance2.getReverse() != null) {
                            list.add(instance2.getReverse().getEncoded());
                        }
                    }
                    catch (final IOException ex) {}
                    catch (final IllegalArgumentException ex2) {}
                    final Iterator iterator2 = list.iterator();
                    while (iterator2.hasNext()) {
                        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((byte[])iterator2.next());
                        try {
                            final Certificate generateCertificate = instance.generateCertificate(byteArrayInputStream);
                            if (!x509CertSelector.match(generateCertificate)) {
                                continue;
                            }
                            set.add(generateCertificate);
                        }
                        catch (final Exception ex3) {}
                    }
                }
            }
        }
        catch (final Exception ex4) {
            throw new CertStoreException("certificate cannot be constructed from LDAP result: " + ex4);
        }
        return set;
    }
    
    private Set certSubjectSerialSearch(final X509CertSelector x509CertSelector, final String[] array, String searchForSerialNumberIn, final String s) throws CertStoreException {
        final HashSet set = new HashSet();
        try {
            if (x509CertSelector.getSubjectAsBytes() != null || x509CertSelector.getSubjectAsString() != null || x509CertSelector.getCertificate() != null) {
                String string = null;
                String s2;
                if (x509CertSelector.getCertificate() != null) {
                    s2 = x509CertSelector.getCertificate().getSubjectX500Principal().getName("RFC1779");
                    string = x509CertSelector.getCertificate().getSerialNumber().toString();
                }
                else if (x509CertSelector.getSubjectAsBytes() != null) {
                    s2 = new X500Principal(x509CertSelector.getSubjectAsBytes()).getName("RFC1779");
                }
                else {
                    s2 = x509CertSelector.getSubjectAsString();
                }
                set.addAll(this.search(searchForSerialNumberIn, "*" + this.parseDN(s2, s) + "*", array));
                if (string != null && this.params.getSearchForSerialNumberIn() != null) {
                    final String s3 = string;
                    searchForSerialNumberIn = this.params.getSearchForSerialNumberIn();
                    set.addAll(this.search(searchForSerialNumberIn, "*" + s3 + "*", array));
                }
            }
            else {
                set.addAll(this.search(searchForSerialNumberIn, "*", array));
            }
        }
        catch (final IOException ex) {
            throw new CertStoreException("exception processing selector: " + ex);
        }
        return set;
    }
    
    private Set getEndCertificates(final X509CertSelector x509CertSelector) throws CertStoreException {
        return this.certSubjectSerialSearch(x509CertSelector, new String[] { this.params.getUserCertificateAttribute() }, this.params.getLdapUserCertificateAttributeName(), this.params.getUserCertificateSubjectAttributeName());
    }
    
    private Set getCACertificates(final X509CertSelector x509CertSelector) throws CertStoreException {
        final String[] array = { this.params.getCACertificateAttribute() };
        final Set certSubjectSerialSearch = this.certSubjectSerialSearch(x509CertSelector, array, this.params.getLdapCACertificateAttributeName(), this.params.getCACertificateSubjectAttributeName());
        if (certSubjectSerialSearch.isEmpty()) {
            certSubjectSerialSearch.addAll(this.search(null, "*", array));
        }
        return certSubjectSerialSearch;
    }
    
    private Set getCrossCertificates(final X509CertSelector x509CertSelector) throws CertStoreException {
        final String[] array = { this.params.getCrossCertificateAttribute() };
        final Set certSubjectSerialSearch = this.certSubjectSerialSearch(x509CertSelector, array, this.params.getLdapCrossCertificateAttributeName(), this.params.getCrossCertificateSubjectAttributeName());
        if (certSubjectSerialSearch.isEmpty()) {
            certSubjectSerialSearch.addAll(this.search(null, "*", array));
        }
        return certSubjectSerialSearch;
    }
    
    @Override
    public Collection engineGetCRLs(final CRLSelector crlSelector) throws CertStoreException {
        final String[] array = { this.params.getCertificateRevocationListAttribute() };
        if (!(crlSelector instanceof X509CRLSelector)) {
            throw new CertStoreException("selector is not a X509CRLSelector");
        }
        final X509CRLSelector x509CRLSelector = (X509CRLSelector)crlSelector;
        final HashSet set = new HashSet();
        final String ldapCertificateRevocationListAttributeName = this.params.getLdapCertificateRevocationListAttributeName();
        final HashSet set2 = new HashSet();
        if (x509CRLSelector.getIssuerNames() != null) {
            for (final byte[] next : x509CRLSelector.getIssuerNames()) {
                String s;
                if (next instanceof String) {
                    s = this.parseDN((String)(Object)next, this.params.getCertificateRevocationListIssuerAttributeName());
                }
                else {
                    s = this.parseDN(new X500Principal(next).getName("RFC1779"), this.params.getCertificateRevocationListIssuerAttributeName());
                }
                set2.addAll(this.search(ldapCertificateRevocationListAttributeName, "*" + s + "*", array));
            }
        }
        else {
            set2.addAll(this.search(ldapCertificateRevocationListAttributeName, "*", array));
        }
        set2.addAll(this.search(null, "*", array));
        final Iterator iterator2 = set2.iterator();
        try {
            final CertificateFactory instance = CertificateFactory.getInstance("X.509", "BC");
            while (iterator2.hasNext()) {
                final CRL generateCRL = instance.generateCRL(new ByteArrayInputStream((byte[])iterator2.next()));
                if (x509CRLSelector.match(generateCRL)) {
                    set.add(generateCRL);
                }
            }
        }
        catch (final Exception ex) {
            throw new CertStoreException("CRL cannot be constructed from LDAP result " + ex);
        }
        return set;
    }
    
    private Set search(final String s, final String s2, final String[] array) throws CertStoreException {
        String string = s + "=" + s2;
        if (s == null) {
            string = null;
        }
        DirContext connectLDAP = null;
        final HashSet set = new HashSet();
        try {
            connectLDAP = this.connectLDAP();
            final SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(2);
            searchControls.setCountLimit(0L);
            for (int i = 0; i < array.length; ++i) {
                final String[] returningAttributes = { array[i] };
                searchControls.setReturningAttributes(returningAttributes);
                String s3 = "(&(" + string + ")(" + returningAttributes[0] + "=*))";
                if (string == null) {
                    s3 = "(" + returningAttributes[0] + "=*)";
                }
                final NamingEnumeration<SearchResult> search = connectLDAP.search(this.params.getBaseDN(), s3, searchControls);
                while (search.hasMoreElements()) {
                    final NamingEnumeration<?> all = ((Attribute)search.next().getAttributes().getAll().next()).getAll();
                    while (all.hasMore()) {
                        set.add(all.next());
                    }
                }
            }
        }
        catch (final Exception ex) {
            throw new CertStoreException("Error getting results from LDAP directory " + ex);
        }
        finally {
            try {
                if (null != connectLDAP) {
                    connectLDAP.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return set;
    }
    
    static {
        X509LDAPCertStoreSpi.LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
        X509LDAPCertStoreSpi.REFERRALS_IGNORE = "ignore";
    }
}
