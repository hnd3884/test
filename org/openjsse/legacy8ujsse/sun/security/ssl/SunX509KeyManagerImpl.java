package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
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
    private static final Debug debug;
    private static final String[] STRING0;
    private Map<String, X509Credentials> credentialsMap;
    private final Map<String, String[]> serverAliasCache;
    
    SunX509KeyManagerImpl(final KeyStore ks, final char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.credentialsMap = new HashMap<String, X509Credentials>();
        this.serverAliasCache = Collections.synchronizedMap(new HashMap<String, String[]>());
        if (ks == null) {
            return;
        }
        final Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            if (!ks.isKeyEntry(alias)) {
                continue;
            }
            final Key key = ks.getKey(alias, password);
            if (!(key instanceof PrivateKey)) {
                continue;
            }
            Certificate[] certs = ks.getCertificateChain(alias);
            if (certs == null || certs.length == 0) {
                continue;
            }
            if (!(certs[0] instanceof X509Certificate)) {
                continue;
            }
            if (!(certs instanceof X509Certificate[])) {
                final Certificate[] tmp = new X509Certificate[certs.length];
                System.arraycopy(certs, 0, tmp, 0, certs.length);
                certs = tmp;
            }
            final X509Credentials cred = new X509Credentials((PrivateKey)key, (X509Certificate[])certs);
            this.credentialsMap.put(alias, cred);
            if (SunX509KeyManagerImpl.debug == null || !Debug.isOn("keymanager")) {
                continue;
            }
            System.out.println("***");
            System.out.println("found key for : " + alias);
            for (int i = 0; i < certs.length; ++i) {
                System.out.println("chain [" + i + "] = " + certs[i]);
            }
            System.out.println("***");
        }
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        if (alias == null) {
            return null;
        }
        final X509Credentials cred = this.credentialsMap.get(alias);
        if (cred == null) {
            return null;
        }
        return cred.certificates.clone();
    }
    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        if (alias == null) {
            return null;
        }
        final X509Credentials cred = this.credentialsMap.get(alias);
        if (cred == null) {
            return null;
        }
        return cred.privateKey;
    }
    
    @Override
    public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
        if (keyTypes == null) {
            return null;
        }
        for (int i = 0; i < keyTypes.length; ++i) {
            final String[] aliases = this.getClientAliases(keyTypes[i], issuers);
            if (aliases != null && aliases.length > 0) {
                return aliases[0];
            }
        }
        return null;
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] keyType, final Principal[] issuers, final SSLEngine engine) {
        return this.chooseClientAlias(keyType, issuers, null);
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        if (keyType == null) {
            return null;
        }
        String[] aliases;
        if (issuers == null || issuers.length == 0) {
            aliases = this.serverAliasCache.get(keyType);
            if (aliases == null) {
                aliases = this.getServerAliases(keyType, issuers);
                if (aliases == null) {
                    aliases = SunX509KeyManagerImpl.STRING0;
                }
                this.serverAliasCache.put(keyType, aliases);
            }
        }
        else {
            aliases = this.getServerAliases(keyType, issuers);
        }
        if (aliases != null && aliases.length > 0) {
            return aliases[0];
        }
        return null;
    }
    
    @Override
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        return this.chooseServerAlias(keyType, issuers, null);
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this.getAliases(keyType, issuers);
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this.getAliases(keyType, issuers);
    }
    
    private String[] getAliases(String keyType, Principal[] issuers) {
        if (keyType == null) {
            return null;
        }
        if (issuers == null) {
            issuers = new X500Principal[0];
        }
        if (!(issuers instanceof X500Principal[])) {
            issuers = convertPrincipals(issuers);
        }
        String sigType;
        if (keyType.contains("_")) {
            final int k = keyType.indexOf("_");
            sigType = keyType.substring(k + 1);
            keyType = keyType.substring(0, k);
        }
        else {
            sigType = null;
        }
        final X500Principal[] x500Issuers = (X500Principal[])issuers;
        final List<String> aliases = new ArrayList<String>();
        for (final Map.Entry<String, X509Credentials> entry : this.credentialsMap.entrySet()) {
            final String alias = entry.getKey();
            final X509Credentials credentials = entry.getValue();
            final X509Certificate[] certs = credentials.certificates;
            if (!keyType.equals(certs[0].getPublicKey().getAlgorithm())) {
                continue;
            }
            if (sigType != null) {
                if (certs.length > 1) {
                    if (!sigType.equals(certs[1].getPublicKey().getAlgorithm())) {
                        continue;
                    }
                }
                else {
                    final String sigAlgName = certs[0].getSigAlgName().toUpperCase(Locale.ENGLISH);
                    final String pattern = "WITH" + sigType.toUpperCase(Locale.ENGLISH);
                    if (!sigAlgName.contains(pattern)) {
                        continue;
                    }
                }
            }
            if (issuers.length == 0) {
                aliases.add(alias);
                if (SunX509KeyManagerImpl.debug == null || !Debug.isOn("keymanager")) {
                    continue;
                }
                System.out.println("matching alias: " + alias);
            }
            else {
                final Set<X500Principal> certIssuers = credentials.getIssuerX500Principals();
                int i = 0;
                while (i < x500Issuers.length) {
                    if (certIssuers.contains(issuers[i])) {
                        aliases.add(alias);
                        if (SunX509KeyManagerImpl.debug != null && Debug.isOn("keymanager")) {
                            System.out.println("matching alias: " + alias);
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
        final String[] aliasStrings = aliases.toArray(SunX509KeyManagerImpl.STRING0);
        return (String[])((aliasStrings.length == 0) ? null : aliasStrings);
    }
    
    private static X500Principal[] convertPrincipals(final Principal[] principals) {
        final List<X500Principal> list = new ArrayList<X500Principal>(principals.length);
        for (int i = 0; i < principals.length; ++i) {
            final Principal p = principals[i];
            if (p instanceof X500Principal) {
                list.add((X500Principal)p);
            }
            else {
                try {
                    list.add(new X500Principal(p.getName()));
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
        return list.toArray(new X500Principal[list.size()]);
    }
    
    static {
        debug = Debug.getInstance("ssl");
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
