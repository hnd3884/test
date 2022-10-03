package org.bouncycastle.x509.util;

import java.sql.Date;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.jce.provider.X509AttrCertParser;
import java.io.IOException;
import java.security.cert.CertificateParsingException;
import org.bouncycastle.x509.X509CertificatePair;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.jce.provider.X509CertPairParser;
import java.security.cert.X509CRL;
import org.bouncycastle.jce.provider.X509CRLParser;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import org.bouncycastle.x509.X509CRLStoreSelector;
import java.security.Principal;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509CertPairStoreSelector;
import java.util.Collection;
import java.util.ArrayList;
import org.bouncycastle.util.StoreException;
import java.util.Iterator;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.jce.provider.X509CertParser;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.x509.X509CertStoreSelector;
import java.util.List;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.directory.InitialDirContext;
import java.util.Properties;
import javax.naming.directory.DirContext;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;

public class LDAPStoreHelper
{
    private X509LDAPCertStoreParameters params;
    private static String LDAP_PROVIDER;
    private static String REFERRALS_IGNORE;
    private static final String SEARCH_SECURITY_LEVEL = "none";
    private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
    private Map cacheMap;
    private static int cacheSize;
    private static long lifeTime;
    
    public LDAPStoreHelper(final X509LDAPCertStoreParameters params) {
        this.cacheMap = new HashMap(LDAPStoreHelper.cacheSize);
        this.params = params;
    }
    
    private DirContext connectLDAP() throws NamingException {
        final Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", LDAPStoreHelper.LDAP_PROVIDER);
        properties.setProperty("java.naming.batchsize", "0");
        properties.setProperty("java.naming.provider.url", this.params.getLdapURL());
        properties.setProperty("java.naming.factory.url.pkgs", "com.sun.jndi.url");
        properties.setProperty("java.naming.referral", LDAPStoreHelper.REFERRALS_IGNORE);
        properties.setProperty("java.naming.security.authentication", "none");
        return new InitialDirContext(properties);
    }
    
    private String parseDN(final String s, final String s2) {
        final int index = s.toLowerCase().indexOf(s2.toLowerCase() + "=");
        if (index == -1) {
            return "";
        }
        final String substring = s.substring(index + s2.length());
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
    
    private Set createCerts(final List list, final X509CertStoreSelector x509CertStoreSelector) throws StoreException {
        final HashSet set = new HashSet();
        final Iterator iterator = list.iterator();
        final X509CertParser x509CertParser = new X509CertParser();
        while (iterator.hasNext()) {
            try {
                x509CertParser.engineInit(new ByteArrayInputStream((byte[])iterator.next()));
                final X509Certificate x509Certificate = (X509Certificate)x509CertParser.engineRead();
                if (!x509CertStoreSelector.match((Object)x509Certificate)) {
                    continue;
                }
                set.add(x509Certificate);
            }
            catch (final Exception ex) {}
        }
        return set;
    }
    
    private List certSubjectSerialSearch(final X509CertStoreSelector x509CertStoreSelector, final String[] array, final String[] array2, final String[] array3) throws StoreException {
        final ArrayList list = new ArrayList();
        String s = null;
        String s2 = this.getSubjectAsString(x509CertStoreSelector);
        if (x509CertStoreSelector.getSerialNumber() != null) {
            s = x509CertStoreSelector.getSerialNumber().toString();
        }
        if (x509CertStoreSelector.getCertificate() != null) {
            s2 = x509CertStoreSelector.getCertificate().getSubjectX500Principal().getName("RFC1779");
            s = x509CertStoreSelector.getCertificate().getSerialNumber().toString();
        }
        if (s2 != null) {
            for (int i = 0; i < array3.length; ++i) {
                list.addAll(this.search(array2, "*" + this.parseDN(s2, array3[i]) + "*", array));
            }
        }
        if (s != null && this.params.getSearchForSerialNumberIn() != null) {
            list.addAll(this.search(this.splitString(this.params.getSearchForSerialNumberIn()), s, array));
        }
        if (s == null && s2 == null) {
            list.addAll(this.search(array2, "*", array));
        }
        return list;
    }
    
    private List crossCertificatePairSubjectSearch(final X509CertPairStoreSelector x509CertPairStoreSelector, final String[] array, final String[] array2, final String[] array3) throws StoreException {
        final ArrayList list = new ArrayList();
        String s = null;
        if (x509CertPairStoreSelector.getForwardSelector() != null) {
            s = this.getSubjectAsString(x509CertPairStoreSelector.getForwardSelector());
        }
        if (x509CertPairStoreSelector.getCertPair() != null && x509CertPairStoreSelector.getCertPair().getForward() != null) {
            s = x509CertPairStoreSelector.getCertPair().getForward().getSubjectX500Principal().getName("RFC1779");
        }
        if (s != null) {
            for (int i = 0; i < array3.length; ++i) {
                list.addAll(this.search(array2, "*" + this.parseDN(s, array3[i]) + "*", array));
            }
        }
        if (s == null) {
            list.addAll(this.search(array2, "*", array));
        }
        return list;
    }
    
    private List attrCertSubjectSerialSearch(final X509AttributeCertStoreSelector x509AttributeCertStoreSelector, final String[] array, final String[] array2, final String[] array3) throws StoreException {
        final ArrayList list = new ArrayList();
        String s = null;
        final HashSet set = new HashSet();
        Principal[] array4 = null;
        if (x509AttributeCertStoreSelector.getHolder() != null) {
            if (x509AttributeCertStoreSelector.getHolder().getSerialNumber() != null) {
                set.add(x509AttributeCertStoreSelector.getHolder().getSerialNumber().toString());
            }
            if (x509AttributeCertStoreSelector.getHolder().getEntityNames() != null) {
                array4 = x509AttributeCertStoreSelector.getHolder().getEntityNames();
            }
        }
        if (x509AttributeCertStoreSelector.getAttributeCert() != null) {
            if (x509AttributeCertStoreSelector.getAttributeCert().getHolder().getEntityNames() != null) {
                array4 = x509AttributeCertStoreSelector.getAttributeCert().getHolder().getEntityNames();
            }
            set.add(x509AttributeCertStoreSelector.getAttributeCert().getSerialNumber().toString());
        }
        if (array4 != null) {
            if (array4[0] instanceof X500Principal) {
                s = ((X500Principal)array4[0]).getName("RFC1779");
            }
            else {
                s = array4[0].getName();
            }
        }
        if (x509AttributeCertStoreSelector.getSerialNumber() != null) {
            set.add(x509AttributeCertStoreSelector.getSerialNumber().toString());
        }
        if (s != null) {
            for (int i = 0; i < array3.length; ++i) {
                list.addAll(this.search(array2, "*" + this.parseDN(s, array3[i]) + "*", array));
            }
        }
        if (set.size() > 0 && this.params.getSearchForSerialNumberIn() != null) {
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                list.addAll(this.search(this.splitString(this.params.getSearchForSerialNumberIn()), (String)iterator.next(), array));
            }
        }
        if (set.size() == 0 && s == null) {
            list.addAll(this.search(array2, "*", array));
        }
        return list;
    }
    
    private List cRLIssuerSearch(final X509CRLStoreSelector x509CRLStoreSelector, final String[] array, final String[] array2, final String[] array3) throws StoreException {
        final ArrayList list = new ArrayList();
        String name = null;
        final HashSet set = new HashSet();
        if (x509CRLStoreSelector.getIssuers() != null) {
            set.addAll(x509CRLStoreSelector.getIssuers());
        }
        if (x509CRLStoreSelector.getCertificateChecking() != null) {
            set.add(this.getCertificateIssuer(x509CRLStoreSelector.getCertificateChecking()));
        }
        if (x509CRLStoreSelector.getAttrCertificateChecking() != null) {
            final Principal[] principals = x509CRLStoreSelector.getAttrCertificateChecking().getIssuer().getPrincipals();
            for (int i = 0; i < principals.length; ++i) {
                if (principals[i] instanceof X500Principal) {
                    set.add(principals[i]);
                }
            }
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            name = ((X500Principal)iterator.next()).getName("RFC1779");
            for (int j = 0; j < array3.length; ++j) {
                list.addAll(this.search(array2, "*" + this.parseDN(name, array3[j]) + "*", array));
            }
        }
        if (name == null) {
            list.addAll(this.search(array2, "*", array));
        }
        return list;
    }
    
    private List search(final String[] array, String s, final String[] returningAttributes) throws StoreException {
        String string;
        if (array == null) {
            string = null;
        }
        else {
            String string2 = "";
            if (s.equals("**")) {
                s = "*";
            }
            for (int i = 0; i < array.length; ++i) {
                string2 = string2 + "(" + array[i] + "=" + s + ")";
            }
            string = "(|" + string2 + ")";
        }
        String string3 = "";
        for (int j = 0; j < returningAttributes.length; ++j) {
            string3 = string3 + "(" + returningAttributes[j] + "=*)";
        }
        final String string4 = "(|" + string3 + ")";
        String string5 = "(&" + string + "" + string4 + ")";
        if (string == null) {
            string5 = string4;
        }
        final List fromCache = this.getFromCache(string5);
        if (fromCache != null) {
            return fromCache;
        }
        DirContext connectLDAP = null;
        final ArrayList list = new ArrayList();
        try {
            connectLDAP = this.connectLDAP();
            final SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(2);
            searchControls.setCountLimit(0L);
            searchControls.setReturningAttributes(returningAttributes);
            final NamingEnumeration<SearchResult> search = connectLDAP.search(this.params.getBaseDN(), string5, searchControls);
            while (search.hasMoreElements()) {
                final NamingEnumeration<?> all = ((Attribute)search.next().getAttributes().getAll().next()).getAll();
                while (all.hasMore()) {
                    list.add(all.next());
                }
            }
            this.addToCache(string5, list);
        }
        catch (final NamingException ex) {}
        finally {
            try {
                if (null != connectLDAP) {
                    connectLDAP.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return list;
    }
    
    private Set createCRLs(final List list, final X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        final HashSet set = new HashSet();
        final X509CRLParser x509CRLParser = new X509CRLParser();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            try {
                x509CRLParser.engineInit(new ByteArrayInputStream((byte[])iterator.next()));
                final X509CRL x509CRL = (X509CRL)x509CRLParser.engineRead();
                if (!x509CRLStoreSelector.match((Object)x509CRL)) {
                    continue;
                }
                set.add(x509CRL);
            }
            catch (final StreamParsingException ex) {}
        }
        return set;
    }
    
    private Set createCrossCertificatePairs(final List list, final X509CertPairStoreSelector x509CertPairStoreSelector) throws StoreException {
        final HashSet set = new HashSet();
        for (int i = 0; i < list.size(); ++i) {
            try {
                Object o;
                try {
                    final X509CertPairParser x509CertPairParser = new X509CertPairParser();
                    x509CertPairParser.engineInit(new ByteArrayInputStream(list.get(i)));
                    o = x509CertPairParser.engineRead();
                }
                catch (final StreamParsingException ex) {
                    o = new X509CertificatePair(new CertificatePair(Certificate.getInstance(new ASN1InputStream(list.get(i)).readObject()), Certificate.getInstance(new ASN1InputStream(list.get(i + 1)).readObject())));
                    ++i;
                }
                if (x509CertPairStoreSelector.match(o)) {
                    set.add(o);
                }
            }
            catch (final CertificateParsingException ex2) {}
            catch (final IOException ex3) {}
        }
        return set;
    }
    
    private Set createAttributeCertificates(final List list, final X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        final HashSet set = new HashSet();
        final Iterator iterator = list.iterator();
        final X509AttrCertParser x509AttrCertParser = new X509AttrCertParser();
        while (iterator.hasNext()) {
            try {
                x509AttrCertParser.engineInit(new ByteArrayInputStream((byte[])iterator.next()));
                final X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate)x509AttrCertParser.engineRead();
                if (!x509AttributeCertStoreSelector.match(x509AttributeCertificate)) {
                    continue;
                }
                set.add(x509AttributeCertificate);
            }
            catch (final StreamParsingException ex) {}
        }
        return set;
    }
    
    public Collection getAuthorityRevocationLists(final X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getAuthorityRevocationListAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapAuthorityRevocationListAttributeName());
        final String[] splitString3 = this.splitString(this.params.getAuthorityRevocationListIssuerAttributeName());
        final Set crLs = this.createCRLs(this.cRLIssuerSearch(x509CRLStoreSelector, splitString, splitString2, splitString3), x509CRLStoreSelector);
        if (crLs.size() == 0) {
            crLs.addAll(this.createCRLs(this.cRLIssuerSearch(new X509CRLStoreSelector(), splitString, splitString2, splitString3), x509CRLStoreSelector));
        }
        return crLs;
    }
    
    public Collection getAttributeCertificateRevocationLists(final X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getAttributeCertificateRevocationListAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapAttributeCertificateRevocationListAttributeName());
        final String[] splitString3 = this.splitString(this.params.getAttributeCertificateRevocationListIssuerAttributeName());
        final Set crLs = this.createCRLs(this.cRLIssuerSearch(x509CRLStoreSelector, splitString, splitString2, splitString3), x509CRLStoreSelector);
        if (crLs.size() == 0) {
            crLs.addAll(this.createCRLs(this.cRLIssuerSearch(new X509CRLStoreSelector(), splitString, splitString2, splitString3), x509CRLStoreSelector));
        }
        return crLs;
    }
    
    public Collection getAttributeAuthorityRevocationLists(final X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getAttributeAuthorityRevocationListAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapAttributeAuthorityRevocationListAttributeName());
        final String[] splitString3 = this.splitString(this.params.getAttributeAuthorityRevocationListIssuerAttributeName());
        final Set crLs = this.createCRLs(this.cRLIssuerSearch(x509CRLStoreSelector, splitString, splitString2, splitString3), x509CRLStoreSelector);
        if (crLs.size() == 0) {
            crLs.addAll(this.createCRLs(this.cRLIssuerSearch(new X509CRLStoreSelector(), splitString, splitString2, splitString3), x509CRLStoreSelector));
        }
        return crLs;
    }
    
    public Collection getCrossCertificatePairs(final X509CertPairStoreSelector x509CertPairStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getCrossCertificateAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapCrossCertificateAttributeName());
        final String[] splitString3 = this.splitString(this.params.getCrossCertificateSubjectAttributeName());
        final Set crossCertificatePairs = this.createCrossCertificatePairs(this.crossCertificatePairSubjectSearch(x509CertPairStoreSelector, splitString, splitString2, splitString3), x509CertPairStoreSelector);
        if (crossCertificatePairs.size() == 0) {
            final X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
            final X509CertPairStoreSelector x509CertPairStoreSelector2 = new X509CertPairStoreSelector();
            x509CertPairStoreSelector2.setForwardSelector(x509CertStoreSelector);
            x509CertPairStoreSelector2.setReverseSelector(x509CertStoreSelector);
            crossCertificatePairs.addAll(this.createCrossCertificatePairs(this.crossCertificatePairSubjectSearch(x509CertPairStoreSelector2, splitString, splitString2, splitString3), x509CertPairStoreSelector));
        }
        return crossCertificatePairs;
    }
    
    public Collection getUserCertificates(final X509CertStoreSelector x509CertStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getUserCertificateAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapUserCertificateAttributeName());
        final String[] splitString3 = this.splitString(this.params.getUserCertificateSubjectAttributeName());
        final Set certs = this.createCerts(this.certSubjectSerialSearch(x509CertStoreSelector, splitString, splitString2, splitString3), x509CertStoreSelector);
        if (certs.size() == 0) {
            certs.addAll(this.createCerts(this.certSubjectSerialSearch(new X509CertStoreSelector(), splitString, splitString2, splitString3), x509CertStoreSelector));
        }
        return certs;
    }
    
    public Collection getAACertificates(final X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getAACertificateAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapAACertificateAttributeName());
        final String[] splitString3 = this.splitString(this.params.getAACertificateSubjectAttributeName());
        final Set attributeCertificates = this.createAttributeCertificates(this.attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, splitString, splitString2, splitString3), x509AttributeCertStoreSelector);
        if (attributeCertificates.size() == 0) {
            attributeCertificates.addAll(this.createAttributeCertificates(this.attrCertSubjectSerialSearch(new X509AttributeCertStoreSelector(), splitString, splitString2, splitString3), x509AttributeCertStoreSelector));
        }
        return attributeCertificates;
    }
    
    public Collection getAttributeDescriptorCertificates(final X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getAttributeDescriptorCertificateAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapAttributeDescriptorCertificateAttributeName());
        final String[] splitString3 = this.splitString(this.params.getAttributeDescriptorCertificateSubjectAttributeName());
        final Set attributeCertificates = this.createAttributeCertificates(this.attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, splitString, splitString2, splitString3), x509AttributeCertStoreSelector);
        if (attributeCertificates.size() == 0) {
            attributeCertificates.addAll(this.createAttributeCertificates(this.attrCertSubjectSerialSearch(new X509AttributeCertStoreSelector(), splitString, splitString2, splitString3), x509AttributeCertStoreSelector));
        }
        return attributeCertificates;
    }
    
    public Collection getCACertificates(final X509CertStoreSelector x509CertStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getCACertificateAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapCACertificateAttributeName());
        final String[] splitString3 = this.splitString(this.params.getCACertificateSubjectAttributeName());
        final Set certs = this.createCerts(this.certSubjectSerialSearch(x509CertStoreSelector, splitString, splitString2, splitString3), x509CertStoreSelector);
        if (certs.size() == 0) {
            certs.addAll(this.createCerts(this.certSubjectSerialSearch(new X509CertStoreSelector(), splitString, splitString2, splitString3), x509CertStoreSelector));
        }
        return certs;
    }
    
    public Collection getDeltaCertificateRevocationLists(final X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getDeltaRevocationListAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapDeltaRevocationListAttributeName());
        final String[] splitString3 = this.splitString(this.params.getDeltaRevocationListIssuerAttributeName());
        final Set crLs = this.createCRLs(this.cRLIssuerSearch(x509CRLStoreSelector, splitString, splitString2, splitString3), x509CRLStoreSelector);
        if (crLs.size() == 0) {
            crLs.addAll(this.createCRLs(this.cRLIssuerSearch(new X509CRLStoreSelector(), splitString, splitString2, splitString3), x509CRLStoreSelector));
        }
        return crLs;
    }
    
    public Collection getAttributeCertificateAttributes(final X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getAttributeCertificateAttributeAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapAttributeCertificateAttributeAttributeName());
        final String[] splitString3 = this.splitString(this.params.getAttributeCertificateAttributeSubjectAttributeName());
        final Set attributeCertificates = this.createAttributeCertificates(this.attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, splitString, splitString2, splitString3), x509AttributeCertStoreSelector);
        if (attributeCertificates.size() == 0) {
            attributeCertificates.addAll(this.createAttributeCertificates(this.attrCertSubjectSerialSearch(new X509AttributeCertStoreSelector(), splitString, splitString2, splitString3), x509AttributeCertStoreSelector));
        }
        return attributeCertificates;
    }
    
    public Collection getCertificateRevocationLists(final X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        final String[] splitString = this.splitString(this.params.getCertificateRevocationListAttribute());
        final String[] splitString2 = this.splitString(this.params.getLdapCertificateRevocationListAttributeName());
        final String[] splitString3 = this.splitString(this.params.getCertificateRevocationListIssuerAttributeName());
        final Set crLs = this.createCRLs(this.cRLIssuerSearch(x509CRLStoreSelector, splitString, splitString2, splitString3), x509CRLStoreSelector);
        if (crLs.size() == 0) {
            crLs.addAll(this.createCRLs(this.cRLIssuerSearch(new X509CRLStoreSelector(), splitString, splitString2, splitString3), x509CRLStoreSelector));
        }
        return crLs;
    }
    
    private synchronized void addToCache(final String s, final List list) {
        final Date date = new Date(System.currentTimeMillis());
        final ArrayList list2 = new ArrayList();
        list2.add(date);
        list2.add(list);
        if (this.cacheMap.containsKey(s)) {
            this.cacheMap.put(s, list2);
        }
        else {
            if (this.cacheMap.size() >= LDAPStoreHelper.cacheSize) {
                final Iterator iterator = this.cacheMap.entrySet().iterator();
                long time = date.getTime();
                Object key = null;
                while (iterator.hasNext()) {
                    final Map.Entry entry = (Map.Entry)iterator.next();
                    final long time2 = ((List<Date>)entry.getValue()).get(0).getTime();
                    if (time2 < time) {
                        time = time2;
                        key = entry.getKey();
                    }
                }
                this.cacheMap.remove(key);
            }
            this.cacheMap.put(s, list2);
        }
    }
    
    private List getFromCache(final String s) {
        final List list = this.cacheMap.get(s);
        final long currentTimeMillis = System.currentTimeMillis();
        if (list == null) {
            return null;
        }
        if (((Date)list.get(0)).getTime() < currentTimeMillis - LDAPStoreHelper.lifeTime) {
            return null;
        }
        return (List)list.get(1);
    }
    
    private String[] splitString(final String s) {
        return s.split("\\s+");
    }
    
    private String getSubjectAsString(final X509CertStoreSelector x509CertStoreSelector) {
        try {
            final byte[] subjectAsBytes = x509CertStoreSelector.getSubjectAsBytes();
            if (subjectAsBytes != null) {
                return new X500Principal(subjectAsBytes).getName("RFC1779");
            }
        }
        catch (final IOException ex) {
            throw new StoreException("exception processing name: " + ex.getMessage(), ex);
        }
        return null;
    }
    
    private X500Principal getCertificateIssuer(final X509Certificate x509Certificate) {
        return x509Certificate.getIssuerX500Principal();
    }
    
    static {
        LDAPStoreHelper.LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
        LDAPStoreHelper.REFERRALS_IGNORE = "ignore";
        LDAPStoreHelper.cacheSize = 32;
        LDAPStoreHelper.lifeTime = 60000L;
    }
}
