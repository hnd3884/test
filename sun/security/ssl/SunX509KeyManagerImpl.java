package sun.security.ssl;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Locale;
import java.util.ArrayList;
import javax.security.auth.x500.X500Principal;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.Key;
import java.util.Enumeration;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.security.KeyStore;
import java.util.Map;
import javax.net.ssl.X509ExtendedKeyManager;

final class SunX509KeyManagerImpl extends X509ExtendedKeyManager
{
    private static final String[] STRING0;
    private Map<String, X509Credentials> credentialsMap;
    private final Map<String, String[]> serverAliasCache;
    
    SunX509KeyManagerImpl(final KeyStore keyStore, final char[] array) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.credentialsMap = new HashMap<String, X509Credentials>();
        this.serverAliasCache = Collections.synchronizedMap(new HashMap<String, String[]>());
        if (keyStore == null) {
            return;
        }
        final Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            if (!keyStore.isKeyEntry(s)) {
                continue;
            }
            final Key key = keyStore.getKey(s, array);
            if (!(key instanceof PrivateKey)) {
                continue;
            }
            Certificate[] certificateChain = keyStore.getCertificateChain(s);
            if (certificateChain == null || certificateChain.length == 0) {
                continue;
            }
            if (!(certificateChain[0] instanceof X509Certificate)) {
                continue;
            }
            if (!(certificateChain instanceof X509Certificate[])) {
                final X509Certificate[] array2 = new X509Certificate[((X509Certificate[])certificateChain).length];
                System.arraycopy(certificateChain, 0, array2, 0, ((X509Certificate[])certificateChain).length);
                certificateChain = array2;
            }
            this.credentialsMap.put(s, new X509Credentials((PrivateKey)key, (X509Certificate[])certificateChain));
            if (!SSLLogger.isOn || !SSLLogger.isOn("keymanager")) {
                continue;
            }
            SSLLogger.fine("found key for : " + s, (Object[])certificateChain);
        }
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String s) {
        if (s == null) {
            return null;
        }
        final X509Credentials x509Credentials = this.credentialsMap.get(s);
        if (x509Credentials == null) {
            return null;
        }
        return x509Credentials.certificates.clone();
    }
    
    @Override
    public PrivateKey getPrivateKey(final String s) {
        if (s == null) {
            return null;
        }
        final X509Credentials x509Credentials = this.credentialsMap.get(s);
        if (x509Credentials == null) {
            return null;
        }
        return x509Credentials.privateKey;
    }
    
    @Override
    public String chooseClientAlias(final String[] array, final Principal[] array2, final Socket socket) {
        if (array == null) {
            return null;
        }
        for (int i = 0; i < array.length; ++i) {
            final String[] clientAliases = this.getClientAliases(array[i], array2);
            if (clientAliases != null && clientAliases.length > 0) {
                return clientAliases[0];
            }
        }
        return null;
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] array, final Principal[] array2, final SSLEngine sslEngine) {
        return this.chooseClientAlias(array, array2, null);
    }
    
    @Override
    public String chooseServerAlias(final String s, final Principal[] array, final Socket socket) {
        if (s == null) {
            return null;
        }
        String[] array2;
        if (array == null || array.length == 0) {
            array2 = this.serverAliasCache.get(s);
            if (array2 == null) {
                array2 = this.getServerAliases(s, array);
                if (array2 == null) {
                    array2 = SunX509KeyManagerImpl.STRING0;
                }
                this.serverAliasCache.put(s, array2);
            }
        }
        else {
            array2 = this.getServerAliases(s, array);
        }
        if (array2 != null && array2.length > 0) {
            return array2[0];
        }
        return null;
    }
    
    @Override
    public String chooseEngineServerAlias(final String s, final Principal[] array, final SSLEngine sslEngine) {
        return this.chooseServerAlias(s, array, null);
    }
    
    @Override
    public String[] getClientAliases(final String s, final Principal[] array) {
        return this.getAliases(s, array);
    }
    
    @Override
    public String[] getServerAliases(final String s, final Principal[] array) {
        return this.getAliases(s, array);
    }
    
    private String[] getAliases(String substring, Principal[] convertPrincipals) {
        if (substring == null) {
            return null;
        }
        if (convertPrincipals == null) {
            convertPrincipals = new X500Principal[0];
        }
        if (!(convertPrincipals instanceof X500Principal[])) {
            convertPrincipals = convertPrincipals(convertPrincipals);
        }
        String substring2;
        if (substring.contains("_")) {
            final int index = substring.indexOf(95);
            substring2 = substring.substring(index + 1);
            substring = substring.substring(0, index);
        }
        else {
            substring2 = null;
        }
        final X500Principal[] array = (X500Principal[])convertPrincipals;
        final ArrayList list = new ArrayList();
        for (final Map.Entry entry : this.credentialsMap.entrySet()) {
            final String s = (String)entry.getKey();
            final X509Credentials x509Credentials = (X509Credentials)entry.getValue();
            final X509Certificate[] certificates = x509Credentials.certificates;
            if (!substring.equals(certificates[0].getPublicKey().getAlgorithm())) {
                continue;
            }
            if (substring2 != null) {
                if (certificates.length > 1) {
                    if (!substring2.equals(certificates[1].getPublicKey().getAlgorithm())) {
                        continue;
                    }
                }
                else if (!certificates[0].getSigAlgName().toUpperCase(Locale.ENGLISH).contains("WITH" + substring2.toUpperCase(Locale.ENGLISH))) {
                    continue;
                }
            }
            if (convertPrincipals.length == 0) {
                list.add(s);
                if (!SSLLogger.isOn || !SSLLogger.isOn("keymanager")) {
                    continue;
                }
                SSLLogger.fine("matching alias: " + s, new Object[0]);
            }
            else {
                final Set<X500Principal> issuerX500Principals = x509Credentials.getIssuerX500Principals();
                int i = 0;
                while (i < array.length) {
                    if (issuerX500Principals.contains(convertPrincipals[i])) {
                        list.add(s);
                        if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                            SSLLogger.fine("matching alias: " + s, new Object[0]);
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
        final String[] array2 = (String[])list.toArray(SunX509KeyManagerImpl.STRING0);
        return (String[])((array2.length == 0) ? null : array2);
    }
    
    private static X500Principal[] convertPrincipals(final Principal[] array) {
        final ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; ++i) {
            final Principal principal = array[i];
            if (principal instanceof X500Principal) {
                list.add(principal);
            }
            else {
                try {
                    list.add(new X500Principal(principal.getName()));
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
        return (X500Principal[])list.toArray(new X500Principal[list.size()]);
    }
    
    static {
        STRING0 = new String[0];
    }
    
    private static class X509Credentials
    {
        PrivateKey privateKey;
        X509Certificate[] certificates;
        private Set<X500Principal> issuerX500Principals;
        
        X509Credentials(final PrivateKey privateKey, final X509Certificate[] certificates) {
            this.privateKey = privateKey;
            this.certificates = certificates;
        }
        
        synchronized Set<X500Principal> getIssuerX500Principals() {
            if (this.issuerX500Principals == null) {
                this.issuerX500Principals = new HashSet<X500Principal>();
                for (int i = 0; i < this.certificates.length; ++i) {
                    this.issuerX500Principals.add(this.certificates[i].getIssuerX500Principal());
                }
            }
            return this.issuerX500Principals;
        }
    }
}
