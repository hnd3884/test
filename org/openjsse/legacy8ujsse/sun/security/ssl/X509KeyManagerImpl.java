package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.SNIHostName;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.security.cert.CertPathValidatorException;
import java.security.Timestamp;
import sun.security.provider.certpath.AlgorithmChecker;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import javax.net.ssl.SNIServerName;
import java.util.ArrayList;
import java.lang.ref.SoftReference;
import javax.net.ssl.SSLSession;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLSocket;
import java.security.AlgorithmConstraints;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.security.KeyStore;
import java.util.List;
import java.util.Date;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509ExtendedKeyManager;

final class X509KeyManagerImpl extends X509ExtendedKeyManager implements X509KeyManager
{
    private static final Debug debug;
    private static final boolean useDebug;
    private static Date verificationDate;
    private final List<KeyStore.Builder> builders;
    private final AtomicLong uidCounter;
    private final Map<String, Reference<KeyStore.PrivateKeyEntry>> entryCacheMap;
    
    X509KeyManagerImpl(final KeyStore.Builder builder) {
        this(Collections.singletonList(builder));
    }
    
    X509KeyManagerImpl(final List<KeyStore.Builder> builders) {
        this.builders = builders;
        this.uidCounter = new AtomicLong();
        this.entryCacheMap = Collections.synchronizedMap(new SizedMap<String, Reference<KeyStore.PrivateKeyEntry>>());
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        final KeyStore.PrivateKeyEntry entry = this.getEntry(alias);
        return (X509Certificate[])((entry == null) ? null : ((X509Certificate[])entry.getCertificateChain()));
    }
    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        final KeyStore.PrivateKeyEntry entry = this.getEntry(alias);
        return (entry == null) ? null : entry.getPrivateKey();
    }
    
    @Override
    public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
        return this.chooseAlias(getKeyTypes(keyTypes), issuers, CheckType.CLIENT, this.getAlgorithmConstraints(socket));
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] keyTypes, final Principal[] issuers, final SSLEngine engine) {
        return this.chooseAlias(getKeyTypes(keyTypes), issuers, CheckType.CLIENT, this.getAlgorithmConstraints(engine));
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return this.chooseAlias(getKeyTypes(keyType), issuers, CheckType.SERVER, this.getAlgorithmConstraints(socket), X509TrustManagerImpl.getRequestedServerNames(socket), "HTTPS");
    }
    
    @Override
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        return this.chooseAlias(getKeyTypes(keyType), issuers, CheckType.SERVER, this.getAlgorithmConstraints(engine), X509TrustManagerImpl.getRequestedServerNames(engine), "HTTPS");
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this.getAliases(keyType, issuers, CheckType.CLIENT, null);
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this.getAliases(keyType, issuers, CheckType.SERVER, null);
    }
    
    private AlgorithmConstraints getAlgorithmConstraints(final Socket socket) {
        if (socket != null && socket.isConnected() && socket instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)socket;
            final SSLSession session = sslSocket.getHandshakeSession();
            if (session != null) {
                final ProtocolVersion protocolVersion = ProtocolVersion.valueOf(session.getProtocol());
                if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    String[] peerSupportedSignAlgs = null;
                    if (session instanceof ExtendedSSLSession) {
                        final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                        peerSupportedSignAlgs = extSession.getPeerSupportedSignatureAlgorithms();
                    }
                    return new SSLAlgorithmConstraints(sslSocket, peerSupportedSignAlgs, true);
                }
            }
            return new SSLAlgorithmConstraints(sslSocket, true);
        }
        return new SSLAlgorithmConstraints((SSLSocket)null, true);
    }
    
    private AlgorithmConstraints getAlgorithmConstraints(final SSLEngine engine) {
        if (engine != null) {
            final SSLSession session = engine.getHandshakeSession();
            if (session != null) {
                final ProtocolVersion protocolVersion = ProtocolVersion.valueOf(session.getProtocol());
                if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    String[] peerSupportedSignAlgs = null;
                    if (session instanceof ExtendedSSLSession) {
                        final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                        peerSupportedSignAlgs = extSession.getPeerSupportedSignatureAlgorithms();
                    }
                    return new SSLAlgorithmConstraints(engine, peerSupportedSignAlgs, true);
                }
            }
        }
        return new SSLAlgorithmConstraints(engine, true);
    }
    
    private String makeAlias(final EntryStatus entry) {
        return this.uidCounter.incrementAndGet() + "." + entry.builderIndex + "." + entry.alias;
    }
    
    private KeyStore.PrivateKeyEntry getEntry(final String alias) {
        if (alias == null) {
            return null;
        }
        final Reference<KeyStore.PrivateKeyEntry> ref = this.entryCacheMap.get(alias);
        KeyStore.PrivateKeyEntry entry = (ref != null) ? ref.get() : null;
        if (entry != null) {
            return entry;
        }
        final int firstDot = alias.indexOf(46);
        final int secondDot = alias.indexOf(46, firstDot + 1);
        if (firstDot == -1 || secondDot == firstDot) {
            return null;
        }
        try {
            final int builderIndex = Integer.parseInt(alias.substring(firstDot + 1, secondDot));
            final String keyStoreAlias = alias.substring(secondDot + 1);
            final KeyStore.Builder builder = this.builders.get(builderIndex);
            final KeyStore ks = builder.getKeyStore();
            final KeyStore.Entry newEntry = ks.getEntry(keyStoreAlias, builder.getProtectionParameter(alias));
            if (!(newEntry instanceof KeyStore.PrivateKeyEntry)) {
                return null;
            }
            entry = (KeyStore.PrivateKeyEntry)newEntry;
            this.entryCacheMap.put(alias, new SoftReference<KeyStore.PrivateKeyEntry>(entry));
            return entry;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private static List<KeyType> getKeyTypes(final String... keyTypes) {
        if (keyTypes == null || keyTypes.length == 0 || keyTypes[0] == null) {
            return null;
        }
        final List<KeyType> list = new ArrayList<KeyType>(keyTypes.length);
        for (final String keyType : keyTypes) {
            list.add(new KeyType(keyType));
        }
        return list;
    }
    
    private String chooseAlias(final List<KeyType> keyTypeList, final Principal[] issuers, final CheckType checkType, final AlgorithmConstraints constraints) {
        return this.chooseAlias(keyTypeList, issuers, checkType, constraints, null, null);
    }
    
    private String chooseAlias(final List<KeyType> keyTypeList, final Principal[] issuers, final CheckType checkType, final AlgorithmConstraints constraints, final List<SNIServerName> requestedServerNames, final String idAlgorithm) {
        if (keyTypeList == null || keyTypeList.isEmpty()) {
            return null;
        }
        final Set<Principal> issuerSet = this.getIssuerSet(issuers);
        List<EntryStatus> allResults = null;
        for (int i = 0, n = this.builders.size(); i < n; ++i) {
            try {
                final List<EntryStatus> results = this.getAliases(i, keyTypeList, issuerSet, false, checkType, constraints, requestedServerNames, idAlgorithm);
                if (results != null) {
                    final EntryStatus status = results.get(0);
                    if (status.checkResult == CheckResult.OK) {
                        if (X509KeyManagerImpl.useDebug) {
                            X509KeyManagerImpl.debug.println("KeyMgr: choosing key: " + status);
                        }
                        return this.makeAlias(status);
                    }
                    if (allResults == null) {
                        allResults = new ArrayList<EntryStatus>();
                    }
                    allResults.addAll(results);
                }
            }
            catch (final Exception ex) {}
        }
        if (allResults == null) {
            if (X509KeyManagerImpl.useDebug) {
                X509KeyManagerImpl.debug.println("KeyMgr: no matching key found");
            }
            return null;
        }
        Collections.sort(allResults);
        if (X509KeyManagerImpl.useDebug) {
            X509KeyManagerImpl.debug.println("KeyMgr: no good matching key found, returning best match out of:");
            X509KeyManagerImpl.debug.println(allResults.toString());
        }
        return this.makeAlias(allResults.get(0));
    }
    
    public String[] getAliases(final String keyType, final Principal[] issuers, final CheckType checkType, final AlgorithmConstraints constraints) {
        if (keyType == null) {
            return null;
        }
        final Set<Principal> issuerSet = this.getIssuerSet(issuers);
        final List<KeyType> keyTypeList = getKeyTypes(keyType);
        List<EntryStatus> allResults = null;
        for (int i = 0, n = this.builders.size(); i < n; ++i) {
            try {
                final List<EntryStatus> results = this.getAliases(i, keyTypeList, issuerSet, true, checkType, constraints, null, null);
                if (results != null) {
                    if (allResults == null) {
                        allResults = new ArrayList<EntryStatus>();
                    }
                    allResults.addAll(results);
                }
            }
            catch (final Exception ex) {}
        }
        if (allResults == null || allResults.isEmpty()) {
            if (X509KeyManagerImpl.useDebug) {
                X509KeyManagerImpl.debug.println("KeyMgr: no matching alias found");
            }
            return null;
        }
        Collections.sort(allResults);
        if (X509KeyManagerImpl.useDebug) {
            X509KeyManagerImpl.debug.println("KeyMgr: getting aliases: " + allResults);
        }
        return this.toAliases(allResults);
    }
    
    private String[] toAliases(final List<EntryStatus> results) {
        final String[] s = new String[results.size()];
        int i = 0;
        for (final EntryStatus result : results) {
            s[i++] = this.makeAlias(result);
        }
        return s;
    }
    
    private Set<Principal> getIssuerSet(final Principal[] issuers) {
        if (issuers != null && issuers.length != 0) {
            return new HashSet<Principal>(Arrays.asList(issuers));
        }
        return null;
    }
    
    private List<EntryStatus> getAliases(final int builderIndex, final List<KeyType> keyTypes, final Set<Principal> issuerSet, final boolean findAll, final CheckType checkType, final AlgorithmConstraints constraints, final List<SNIServerName> requestedServerNames, final String idAlgorithm) throws Exception {
        final KeyStore.Builder builder = this.builders.get(builderIndex);
        final KeyStore ks = builder.getKeyStore();
        List<EntryStatus> results = null;
        Date date = X509KeyManagerImpl.verificationDate;
        boolean preferred = false;
        final Enumeration<String> e = ks.aliases();
        while (e.hasMoreElements()) {
            final String alias = e.nextElement();
            if (!ks.isKeyEntry(alias)) {
                continue;
            }
            final Certificate[] chain = ks.getCertificateChain(alias);
            if (chain == null) {
                continue;
            }
            if (chain.length == 0) {
                continue;
            }
            boolean incompatible = false;
            for (final Certificate cert : chain) {
                if (!(cert instanceof X509Certificate)) {
                    incompatible = true;
                    break;
                }
            }
            if (incompatible) {
                continue;
            }
            int keyIndex = -1;
            int j = 0;
            for (final KeyType keyType : keyTypes) {
                if (keyType.matches(chain)) {
                    keyIndex = j;
                    break;
                }
                ++j;
            }
            if (keyIndex == -1) {
                if (!X509KeyManagerImpl.useDebug) {
                    continue;
                }
                X509KeyManagerImpl.debug.println("Ignoring alias " + alias + ": key algorithm does not match");
            }
            else {
                if (issuerSet != null) {
                    boolean found = false;
                    for (final Certificate cert2 : chain) {
                        final X509Certificate xcert = (X509Certificate)cert2;
                        if (issuerSet.contains(xcert.getIssuerX500Principal())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if (X509KeyManagerImpl.useDebug) {
                            X509KeyManagerImpl.debug.println("Ignoring alias " + alias + ": issuers do not match");
                            continue;
                        }
                        continue;
                    }
                }
                if (constraints != null && !conformsToAlgorithmConstraints(constraints, chain, checkType.getValidator())) {
                    if (!X509KeyManagerImpl.useDebug) {
                        continue;
                    }
                    X509KeyManagerImpl.debug.println("Ignoring alias " + alias + ": certificate list does not conform to algorithm constraints");
                }
                else {
                    if (date == null) {
                        date = new Date();
                    }
                    final CheckResult checkResult = checkType.check((X509Certificate)chain[0], date, requestedServerNames, idAlgorithm);
                    final EntryStatus status = new EntryStatus(builderIndex, keyIndex, alias, chain, checkResult);
                    if (!preferred && checkResult == CheckResult.OK && keyIndex == 0) {
                        preferred = true;
                    }
                    if (preferred && !findAll) {
                        return Collections.singletonList(status);
                    }
                    if (results == null) {
                        results = new ArrayList<EntryStatus>();
                    }
                    results.add(status);
                }
            }
        }
        return results;
    }
    
    private static boolean conformsToAlgorithmConstraints(final AlgorithmConstraints constraints, final Certificate[] chain, final String variant) {
        final AlgorithmChecker checker = new AlgorithmChecker(constraints, (Timestamp)null, variant);
        try {
            checker.init(false);
        }
        catch (final CertPathValidatorException cpve) {
            if (X509KeyManagerImpl.useDebug) {
                X509KeyManagerImpl.debug.println("Cannot initialize algorithm constraints checker: " + cpve);
            }
            return false;
        }
        for (int i = chain.length - 1; i >= 0; --i) {
            final Certificate cert = chain[i];
            try {
                checker.check(cert, (Collection<String>)Collections.emptySet());
            }
            catch (final CertPathValidatorException cpve2) {
                if (X509KeyManagerImpl.useDebug) {
                    X509KeyManagerImpl.debug.println("Certificate (" + cert + ") does not conform to algorithm constraints: " + cpve2);
                }
                return false;
            }
        }
        return true;
    }
    
    static {
        debug = Debug.getInstance("ssl");
        useDebug = (X509KeyManagerImpl.debug != null && Debug.isOn("keymanager"));
    }
    
    private static class SizedMap<K, V> extends LinkedHashMap<K, V>
    {
        private static final long serialVersionUID = -8211222668790986062L;
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
            return this.size() > 10;
        }
    }
    
    private static class KeyType
    {
        final String keyAlgorithm;
        final String sigKeyAlgorithm;
        
        KeyType(final String algorithm) {
            final int k = algorithm.indexOf("_");
            if (k == -1) {
                this.keyAlgorithm = algorithm;
                this.sigKeyAlgorithm = null;
            }
            else {
                this.keyAlgorithm = algorithm.substring(0, k);
                this.sigKeyAlgorithm = algorithm.substring(k + 1);
            }
        }
        
        boolean matches(final Certificate[] chain) {
            if (!chain[0].getPublicKey().getAlgorithm().equals(this.keyAlgorithm)) {
                return false;
            }
            if (this.sigKeyAlgorithm == null) {
                return true;
            }
            if (chain.length > 1) {
                return this.sigKeyAlgorithm.equals(chain[1].getPublicKey().getAlgorithm());
            }
            final X509Certificate issuer = (X509Certificate)chain[0];
            final String sigAlgName = issuer.getSigAlgName().toUpperCase(Locale.ENGLISH);
            final String pattern = "WITH" + this.sigKeyAlgorithm.toUpperCase(Locale.ENGLISH);
            return sigAlgName.contains(pattern);
        }
    }
    
    private static class EntryStatus implements Comparable<EntryStatus>
    {
        final int builderIndex;
        final int keyIndex;
        final String alias;
        final CheckResult checkResult;
        
        EntryStatus(final int builderIndex, final int keyIndex, final String alias, final Certificate[] chain, final CheckResult checkResult) {
            this.builderIndex = builderIndex;
            this.keyIndex = keyIndex;
            this.alias = alias;
            this.checkResult = checkResult;
        }
        
        @Override
        public int compareTo(final EntryStatus other) {
            final int result = this.checkResult.compareTo(other.checkResult);
            return (result == 0) ? (this.keyIndex - other.keyIndex) : result;
        }
        
        @Override
        public String toString() {
            final String s = this.alias + " (verified: " + this.checkResult + ")";
            if (this.builderIndex == 0) {
                return s;
            }
            return "Builder #" + this.builderIndex + ", alias: " + s;
        }
    }
    
    private enum CheckType
    {
        NONE(Collections.emptySet()), 
        CLIENT((Set<String>)new HashSet<String>(Arrays.asList("2.5.29.37.0", "1.3.6.1.5.5.7.3.2"))), 
        SERVER((Set<String>)new HashSet<String>(Arrays.asList("2.5.29.37.0", "1.3.6.1.5.5.7.3.1", "2.16.840.1.113730.4.1", "1.3.6.1.4.1.311.10.3.3")));
        
        final Set<String> validEku;
        
        private CheckType(final Set<String> validEku) {
            this.validEku = validEku;
        }
        
        private static boolean getBit(final boolean[] keyUsage, final int bit) {
            return bit < keyUsage.length && keyUsage[bit];
        }
        
        CheckResult check(final X509Certificate cert, final Date date, final List<SNIServerName> serverNames, final String idAlgorithm) {
            if (this == CheckType.NONE) {
                return CheckResult.OK;
            }
            try {
                final List<String> certEku = cert.getExtendedKeyUsage();
                if (certEku != null && Collections.disjoint(this.validEku, certEku)) {
                    return CheckResult.EXTENSION_MISMATCH;
                }
                final boolean[] ku = cert.getKeyUsage();
                if (ku != null) {
                    final String algorithm = cert.getPublicKey().getAlgorithm();
                    final boolean kuSignature = getBit(ku, 0);
                    final String s = algorithm;
                    switch (s) {
                        case "RSA": {
                            if (!kuSignature && (this == CheckType.CLIENT || !getBit(ku, 2))) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                        case "DSA": {
                            if (!kuSignature) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                        case "DH": {
                            if (!getBit(ku, 4)) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                        case "EC": {
                            if (!kuSignature) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            if (this == CheckType.SERVER && !getBit(ku, 4)) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                    }
                }
            }
            catch (final CertificateException e) {
                return CheckResult.EXTENSION_MISMATCH;
            }
            try {
                cert.checkValidity(date);
            }
            catch (final CertificateException e) {
                return CheckResult.EXPIRED;
            }
            if (serverNames != null && !serverNames.isEmpty()) {
                for (SNIServerName serverName : serverNames) {
                    if (serverName.getType() == 0) {
                        if (!(serverName instanceof SNIHostName)) {
                            try {
                                serverName = new SNIHostName(serverName.getEncoded());
                            }
                            catch (final IllegalArgumentException iae) {
                                if (X509KeyManagerImpl.useDebug) {
                                    X509KeyManagerImpl.debug.println("Illegal server name: " + serverName);
                                }
                                return CheckResult.INSENSITIVE;
                            }
                        }
                        final String hostname = ((SNIHostName)serverName).getAsciiName();
                        try {
                            X509TrustManagerImpl.checkIdentity(hostname, cert, idAlgorithm);
                            break;
                        }
                        catch (final CertificateException e2) {
                            if (X509KeyManagerImpl.useDebug) {
                                X509KeyManagerImpl.debug.println("Certificate identity does not match Server Name Inidication (SNI): " + hostname);
                            }
                            return CheckResult.INSENSITIVE;
                        }
                    }
                }
            }
            return CheckResult.OK;
        }
        
        public String getValidator() {
            if (this == CheckType.CLIENT) {
                return "tls client";
            }
            if (this == CheckType.SERVER) {
                return "tls server";
            }
            return "generic";
        }
    }
    
    private enum CheckResult
    {
        OK, 
        INSENSITIVE, 
        EXPIRED, 
        EXTENSION_MISMATCH;
    }
}
