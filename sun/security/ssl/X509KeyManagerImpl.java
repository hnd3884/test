package sun.security.ssl;

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
    public X509Certificate[] getCertificateChain(final String s) {
        final KeyStore.PrivateKeyEntry entry = this.getEntry(s);
        return (X509Certificate[])((entry == null) ? null : ((X509Certificate[])entry.getCertificateChain()));
    }
    
    @Override
    public PrivateKey getPrivateKey(final String s) {
        final KeyStore.PrivateKeyEntry entry = this.getEntry(s);
        return (entry == null) ? null : entry.getPrivateKey();
    }
    
    @Override
    public String chooseClientAlias(final String[] array, final Principal[] array2, final Socket socket) {
        return this.chooseAlias(getKeyTypes(array), array2, CheckType.CLIENT, this.getAlgorithmConstraints(socket));
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] array, final Principal[] array2, final SSLEngine sslEngine) {
        return this.chooseAlias(getKeyTypes(array), array2, CheckType.CLIENT, this.getAlgorithmConstraints(sslEngine));
    }
    
    @Override
    public String chooseServerAlias(final String s, final Principal[] array, final Socket socket) {
        return this.chooseAlias(getKeyTypes(s), array, CheckType.SERVER, this.getAlgorithmConstraints(socket), X509TrustManagerImpl.getRequestedServerNames(socket), "HTTPS");
    }
    
    @Override
    public String chooseEngineServerAlias(final String s, final Principal[] array, final SSLEngine sslEngine) {
        return this.chooseAlias(getKeyTypes(s), array, CheckType.SERVER, this.getAlgorithmConstraints(sslEngine), X509TrustManagerImpl.getRequestedServerNames(sslEngine), "HTTPS");
    }
    
    @Override
    public String[] getClientAliases(final String s, final Principal[] array) {
        return this.getAliases(s, array, CheckType.CLIENT, null);
    }
    
    @Override
    public String[] getServerAliases(final String s, final Principal[] array) {
        return this.getAliases(s, array, CheckType.SERVER, null);
    }
    
    private AlgorithmConstraints getAlgorithmConstraints(final Socket socket) {
        if (socket == null || !socket.isConnected() || !(socket instanceof SSLSocket)) {
            return new SSLAlgorithmConstraints((SSLSocket)null, true);
        }
        final SSLSocket sslSocket = (SSLSocket)socket;
        final SSLSession handshakeSession = sslSocket.getHandshakeSession();
        if (handshakeSession != null && ProtocolVersion.useTLS12PlusSpec(handshakeSession.getProtocol())) {
            String[] peerSupportedSignatureAlgorithms = null;
            if (handshakeSession instanceof ExtendedSSLSession) {
                peerSupportedSignatureAlgorithms = ((ExtendedSSLSession)handshakeSession).getPeerSupportedSignatureAlgorithms();
            }
            return new SSLAlgorithmConstraints(sslSocket, peerSupportedSignatureAlgorithms, true);
        }
        return new SSLAlgorithmConstraints(sslSocket, true);
    }
    
    private AlgorithmConstraints getAlgorithmConstraints(final SSLEngine sslEngine) {
        if (sslEngine != null) {
            final SSLSession handshakeSession = sslEngine.getHandshakeSession();
            if (handshakeSession != null && ProtocolVersion.useTLS12PlusSpec(handshakeSession.getProtocol())) {
                String[] peerSupportedSignatureAlgorithms = null;
                if (handshakeSession instanceof ExtendedSSLSession) {
                    peerSupportedSignatureAlgorithms = ((ExtendedSSLSession)handshakeSession).getPeerSupportedSignatureAlgorithms();
                }
                return new SSLAlgorithmConstraints(sslEngine, peerSupportedSignatureAlgorithms, true);
            }
        }
        return new SSLAlgorithmConstraints(sslEngine, true);
    }
    
    private String makeAlias(final EntryStatus entryStatus) {
        return this.uidCounter.incrementAndGet() + "." + entryStatus.builderIndex + "." + entryStatus.alias;
    }
    
    private KeyStore.PrivateKeyEntry getEntry(final String s) {
        if (s == null) {
            return null;
        }
        final Reference reference = this.entryCacheMap.get(s);
        final KeyStore.PrivateKeyEntry privateKeyEntry = (reference != null) ? ((KeyStore.PrivateKeyEntry)reference.get()) : null;
        if (privateKeyEntry != null) {
            return privateKeyEntry;
        }
        final int index = s.indexOf(46);
        final int index2 = s.indexOf(46, index + 1);
        if (index == -1 || index2 == index) {
            return null;
        }
        try {
            final int int1 = Integer.parseInt(s.substring(index + 1, index2));
            final String substring = s.substring(index2 + 1);
            final KeyStore.Builder builder = this.builders.get(int1);
            final KeyStore.Entry entry = builder.getKeyStore().getEntry(substring, builder.getProtectionParameter(s));
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                return null;
            }
            final KeyStore.PrivateKeyEntry privateKeyEntry2 = (KeyStore.PrivateKeyEntry)entry;
            this.entryCacheMap.put(s, new SoftReference<KeyStore.PrivateKeyEntry>(privateKeyEntry2));
            return privateKeyEntry2;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private static List<KeyType> getKeyTypes(final String... array) {
        if (array == null || array.length == 0 || array[0] == null) {
            return null;
        }
        final ArrayList list = new ArrayList(array.length);
        for (int length = array.length, i = 0; i < length; ++i) {
            list.add(new KeyType(array[i]));
        }
        return list;
    }
    
    private String chooseAlias(final List<KeyType> list, final Principal[] array, final CheckType checkType, final AlgorithmConstraints algorithmConstraints) {
        return this.chooseAlias(list, array, checkType, algorithmConstraints, null, null);
    }
    
    private String chooseAlias(final List<KeyType> list, final Principal[] array, final CheckType checkType, final AlgorithmConstraints algorithmConstraints, final List<SNIServerName> list2, final String s) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        final Set<Principal> issuerSet = this.getIssuerSet(array);
        List list3 = null;
        for (int i = 0; i < this.builders.size(); ++i) {
            try {
                final List<EntryStatus> aliases = this.getAliases(i, list, issuerSet, false, checkType, algorithmConstraints, list2, s);
                if (aliases != null) {
                    final EntryStatus entryStatus = aliases.get(0);
                    if (entryStatus.checkResult == CheckResult.OK) {
                        if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                            SSLLogger.fine("KeyMgr: choosing key: " + entryStatus, new Object[0]);
                        }
                        return this.makeAlias(entryStatus);
                    }
                    if (list3 == null) {
                        list3 = new ArrayList();
                    }
                    list3.addAll(aliases);
                }
            }
            catch (final Exception ex) {}
        }
        if (list3 == null) {
            if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                SSLLogger.fine("KeyMgr: no matching key found", new Object[0]);
            }
            return null;
        }
        Collections.sort((List<Comparable>)list3);
        if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
            SSLLogger.fine("KeyMgr: no good matching key found, returning best match out of", list3);
        }
        return this.makeAlias((EntryStatus)list3.get(0));
    }
    
    public String[] getAliases(final String s, final Principal[] array, final CheckType checkType, final AlgorithmConstraints algorithmConstraints) {
        if (s == null) {
            return null;
        }
        final Set<Principal> issuerSet = this.getIssuerSet(array);
        final List<KeyType> keyTypes = getKeyTypes(s);
        List list = null;
        for (int i = 0; i < this.builders.size(); ++i) {
            try {
                final List<EntryStatus> aliases = this.getAliases(i, keyTypes, issuerSet, true, checkType, algorithmConstraints, null, null);
                if (aliases != null) {
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.addAll(aliases);
                }
            }
            catch (final Exception ex) {}
        }
        if (list == null || list.isEmpty()) {
            if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                SSLLogger.fine("KeyMgr: no matching alias found", new Object[0]);
            }
            return null;
        }
        Collections.sort((List<Comparable>)list);
        if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
            SSLLogger.fine("KeyMgr: getting aliases", list);
        }
        return this.toAliases(list);
    }
    
    private String[] toAliases(final List<EntryStatus> list) {
        final String[] array = new String[list.size()];
        int n = 0;
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            array[n++] = this.makeAlias((EntryStatus)iterator.next());
        }
        return array;
    }
    
    private Set<Principal> getIssuerSet(final Principal[] array) {
        if (array != null && array.length != 0) {
            return new HashSet<Principal>(Arrays.asList(array));
        }
        return null;
    }
    
    private List<EntryStatus> getAliases(final int n, final List<KeyType> list, final Set<Principal> set, final boolean b, final CheckType checkType, final AlgorithmConstraints algorithmConstraints, final List<SNIServerName> list2, final String s) throws Exception {
        final KeyStore keyStore = this.builders.get(n).getKeyStore();
        List<EntryStatus> list3 = null;
        Date verificationDate = X509KeyManagerImpl.verificationDate;
        int n2 = 0;
        final Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s2 = aliases.nextElement();
            if (!keyStore.isKeyEntry(s2)) {
                continue;
            }
            final Certificate[] certificateChain = keyStore.getCertificateChain(s2);
            if (certificateChain == null) {
                continue;
            }
            if (certificateChain.length == 0) {
                continue;
            }
            boolean b2 = false;
            final Certificate[] array = certificateChain;
            for (int length = array.length, i = 0; i < length; ++i) {
                if (!(array[i] instanceof X509Certificate)) {
                    b2 = true;
                    break;
                }
            }
            if (b2) {
                continue;
            }
            int n3 = -1;
            int n4 = 0;
            final Iterator<KeyType> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().matches(certificateChain)) {
                    n3 = n4;
                    break;
                }
                ++n4;
            }
            if (n3 == -1) {
                if (!SSLLogger.isOn || !SSLLogger.isOn("keymanager")) {
                    continue;
                }
                SSLLogger.fine("Ignore alias " + s2 + ": key algorithm does not match", new Object[0]);
            }
            else {
                if (set != null) {
                    boolean b3 = false;
                    final Certificate[] array2 = certificateChain;
                    for (int length2 = array2.length, j = 0; j < length2; ++j) {
                        if (set.contains(((X509Certificate)array2[j]).getIssuerX500Principal())) {
                            b3 = true;
                            break;
                        }
                    }
                    if (!b3) {
                        if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                            SSLLogger.fine("Ignore alias " + s2 + ": issuers do not match", new Object[0]);
                            continue;
                        }
                        continue;
                    }
                }
                if (algorithmConstraints != null && !conformsToAlgorithmConstraints(algorithmConstraints, certificateChain, checkType.getValidator())) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("keymanager")) {
                        continue;
                    }
                    SSLLogger.fine("Ignore alias " + s2 + ": certificate list does not conform to algorithm constraints", new Object[0]);
                }
                else {
                    if (verificationDate == null) {
                        verificationDate = new Date();
                    }
                    final CheckResult check = checkType.check((X509Certificate)certificateChain[0], verificationDate, list2, s);
                    final EntryStatus entryStatus = new EntryStatus(n, n3, s2, certificateChain, check);
                    if (n2 == 0 && check == CheckResult.OK && n3 == 0) {
                        n2 = 1;
                    }
                    if (n2 != 0 && !b) {
                        return Collections.singletonList(entryStatus);
                    }
                    if (list3 == null) {
                        list3 = new ArrayList<EntryStatus>();
                    }
                    list3.add(entryStatus);
                }
            }
        }
        return list3;
    }
    
    private static boolean conformsToAlgorithmConstraints(final AlgorithmConstraints algorithmConstraints, final Certificate[] array, final String s) {
        final AlgorithmChecker algorithmChecker = new AlgorithmChecker(algorithmConstraints, (Timestamp)null, s);
        try {
            algorithmChecker.init(false);
        }
        catch (final CertPathValidatorException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                SSLLogger.fine("Cannot initialize algorithm constraints checker", ex);
            }
            return false;
        }
        for (int i = array.length - 1; i >= 0; --i) {
            final Certificate certificate = array[i];
            try {
                algorithmChecker.check(certificate, (Collection<String>)Collections.emptySet());
            }
            catch (final CertPathValidatorException ex2) {
                if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                    SSLLogger.fine("Certificate does not conform to algorithm constraints", certificate, ex2);
                }
                return false;
            }
        }
        return true;
    }
    
    private static class SizedMap<K, V> extends LinkedHashMap<K, V>
    {
        private static final long serialVersionUID = -8211222668790986062L;
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<K, V> entry) {
            return this.size() > 10;
        }
    }
    
    private static class KeyType
    {
        final String keyAlgorithm;
        final String sigKeyAlgorithm;
        
        KeyType(final String keyAlgorithm) {
            final int index = keyAlgorithm.indexOf(95);
            if (index == -1) {
                this.keyAlgorithm = keyAlgorithm;
                this.sigKeyAlgorithm = null;
            }
            else {
                this.keyAlgorithm = keyAlgorithm.substring(0, index);
                this.sigKeyAlgorithm = keyAlgorithm.substring(index + 1);
            }
        }
        
        boolean matches(final Certificate[] array) {
            if (!array[0].getPublicKey().getAlgorithm().equals(this.keyAlgorithm)) {
                return false;
            }
            if (this.sigKeyAlgorithm == null) {
                return true;
            }
            if (array.length > 1) {
                return this.sigKeyAlgorithm.equals(array[1].getPublicKey().getAlgorithm());
            }
            return ((X509Certificate)array[0]).getSigAlgName().toUpperCase(Locale.ENGLISH).contains("WITH" + this.sigKeyAlgorithm.toUpperCase(Locale.ENGLISH));
        }
    }
    
    private static class EntryStatus implements Comparable<EntryStatus>
    {
        final int builderIndex;
        final int keyIndex;
        final String alias;
        final CheckResult checkResult;
        
        EntryStatus(final int builderIndex, final int keyIndex, final String alias, final Certificate[] array, final CheckResult checkResult) {
            this.builderIndex = builderIndex;
            this.keyIndex = keyIndex;
            this.alias = alias;
            this.checkResult = checkResult;
        }
        
        @Override
        public int compareTo(final EntryStatus entryStatus) {
            final int compareTo = this.checkResult.compareTo(entryStatus.checkResult);
            return (compareTo == 0) ? (this.keyIndex - entryStatus.keyIndex) : compareTo;
        }
        
        @Override
        public String toString() {
            final String string = this.alias + " (verified: " + this.checkResult + ")";
            if (this.builderIndex == 0) {
                return string;
            }
            return "Builder #" + this.builderIndex + ", alias: " + string;
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
        
        private static boolean getBit(final boolean[] array, final int n) {
            return n < array.length && array[n];
        }
        
        CheckResult check(final X509Certificate x509Certificate, final Date date, final List<SNIServerName> list, final String s) {
            if (this == CheckType.NONE) {
                return CheckResult.OK;
            }
            try {
                final List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
                if (extendedKeyUsage != null && Collections.disjoint(this.validEku, extendedKeyUsage)) {
                    return CheckResult.EXTENSION_MISMATCH;
                }
                final boolean[] keyUsage = x509Certificate.getKeyUsage();
                if (keyUsage != null) {
                    final String algorithm = x509Certificate.getPublicKey().getAlgorithm();
                    final boolean bit = getBit(keyUsage, 0);
                    final String s2 = algorithm;
                    switch (s2) {
                        case "RSA": {
                            if (!bit && (this == CheckType.CLIENT || !getBit(keyUsage, 2))) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                        case "RSASSA-PSS": {
                            if (!bit && this == CheckType.SERVER) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                        case "DSA": {
                            if (!bit) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                        case "DH": {
                            if (!getBit(keyUsage, 4)) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                        case "EC": {
                            if (!bit) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            if (this == CheckType.SERVER && !getBit(keyUsage, 4)) {
                                return CheckResult.EXTENSION_MISMATCH;
                            }
                            break;
                        }
                    }
                }
            }
            catch (final CertificateException ex) {
                return CheckResult.EXTENSION_MISMATCH;
            }
            try {
                x509Certificate.checkValidity(date);
            }
            catch (final CertificateException ex2) {
                return CheckResult.EXPIRED;
            }
            if (list != null && !list.isEmpty()) {
                for (SNIServerName sniServerName : list) {
                    if (sniServerName.getType() == 0) {
                        if (!(sniServerName instanceof SNIHostName)) {
                            try {
                                sniServerName = new SNIHostName(sniServerName.getEncoded());
                            }
                            catch (final IllegalArgumentException ex3) {
                                if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                                    SSLLogger.fine("Illegal server name: " + sniServerName, new Object[0]);
                                }
                                return CheckResult.INSENSITIVE;
                            }
                        }
                        final String asciiName = ((SNIHostName)sniServerName).getAsciiName();
                        try {
                            X509TrustManagerImpl.checkIdentity(asciiName, x509Certificate, s);
                            break;
                        }
                        catch (final CertificateException ex4) {
                            if (SSLLogger.isOn && SSLLogger.isOn("keymanager")) {
                                SSLLogger.fine("Certificate identity does not match Server Name Inidication (SNI): " + asciiName, new Object[0]);
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
