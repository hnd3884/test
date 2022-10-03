package sun.security.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;
import java.util.Arrays;
import java.util.ArrayList;
import java.security.AccessController;
import javax.net.ssl.SSLParameters;
import java.util.Iterator;
import java.util.Collections;
import java.security.AccessControlContext;
import javax.net.ssl.HandshakeCompletedListener;
import java.util.HashMap;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocket;
import java.util.function.BiFunction;
import javax.net.ssl.SNIMatcher;
import java.util.Collection;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.security.AlgorithmConstraints;

final class SSLConfiguration implements Cloneable
{
    AlgorithmConstraints userSpecifiedAlgorithmConstraints;
    List<ProtocolVersion> enabledProtocols;
    List<CipherSuite> enabledCipherSuites;
    ClientAuthType clientAuthType;
    String identificationProtocol;
    List<SNIServerName> serverNames;
    Collection<SNIMatcher> sniMatchers;
    String[] applicationProtocols;
    boolean preferLocalCipherSuites;
    int maximumPacketSize;
    List<SignatureScheme> signatureSchemes;
    ProtocolVersion maximumProtocolVersion;
    boolean isClientMode;
    boolean enableSessionCreation;
    BiFunction<SSLSocket, List<String>, String> socketAPSelector;
    BiFunction<SSLEngine, List<String>, String> engineAPSelector;
    HashMap<HandshakeCompletedListener, AccessControlContext> handshakeListeners;
    boolean noSniExtension;
    boolean noSniMatcher;
    static final boolean useExtendedMasterSecret;
    static final boolean allowLegacyResumption;
    static final boolean allowLegacyMasterSecret;
    static final boolean useCompatibilityMode;
    static final boolean acknowledgeCloseNotify;
    static final int maxHandshakeMessageSize;
    static final int maxCertificateChainLength;
    
    SSLConfiguration(final SSLContextImpl sslContextImpl, final boolean isClientMode) {
        this.maximumPacketSize = 0;
        this.userSpecifiedAlgorithmConstraints = SSLAlgorithmConstraints.DEFAULT;
        this.enabledProtocols = sslContextImpl.getDefaultProtocolVersions(!isClientMode);
        this.enabledCipherSuites = sslContextImpl.getDefaultCipherSuites(!isClientMode);
        this.clientAuthType = ClientAuthType.CLIENT_AUTH_NONE;
        this.identificationProtocol = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.preferLocalCipherSuites = false;
        this.applicationProtocols = new String[0];
        this.signatureSchemes = (isClientMode ? CustomizedClientSignatureSchemes.signatureSchemes : CustomizedServerSignatureSchemes.signatureSchemes);
        this.maximumProtocolVersion = ProtocolVersion.NONE;
        for (final ProtocolVersion maximumProtocolVersion : this.enabledProtocols) {
            if (maximumProtocolVersion.compareTo(this.maximumProtocolVersion) > 0) {
                this.maximumProtocolVersion = maximumProtocolVersion;
            }
        }
        this.isClientMode = isClientMode;
        this.enableSessionCreation = true;
        this.socketAPSelector = null;
        this.engineAPSelector = null;
        this.handshakeListeners = null;
        this.noSniExtension = false;
        this.noSniMatcher = false;
    }
    
    SSLParameters getSSLParameters() {
        final SSLParameters sslParameters = new SSLParameters();
        sslParameters.setAlgorithmConstraints(this.userSpecifiedAlgorithmConstraints);
        sslParameters.setProtocols(ProtocolVersion.toStringArray(this.enabledProtocols));
        sslParameters.setCipherSuites(CipherSuite.namesOf(this.enabledCipherSuites));
        switch (this.clientAuthType) {
            case CLIENT_AUTH_REQUIRED: {
                sslParameters.setNeedClientAuth(true);
                break;
            }
            case CLIENT_AUTH_REQUESTED: {
                sslParameters.setWantClientAuth(true);
                break;
            }
            default: {
                sslParameters.setWantClientAuth(false);
                break;
            }
        }
        sslParameters.setEndpointIdentificationAlgorithm(this.identificationProtocol);
        if (this.serverNames.isEmpty() && !this.noSniExtension) {
            sslParameters.setServerNames(null);
        }
        else {
            sslParameters.setServerNames(this.serverNames);
        }
        if (this.sniMatchers.isEmpty() && !this.noSniMatcher) {
            sslParameters.setSNIMatchers(null);
        }
        else {
            sslParameters.setSNIMatchers(this.sniMatchers);
        }
        sslParameters.setApplicationProtocols(this.applicationProtocols);
        sslParameters.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
        return sslParameters;
    }
    
    void setSSLParameters(final SSLParameters sslParameters) {
        final AlgorithmConstraints algorithmConstraints = sslParameters.getAlgorithmConstraints();
        if (algorithmConstraints != null) {
            this.userSpecifiedAlgorithmConstraints = algorithmConstraints;
        }
        final String[] cipherSuites = sslParameters.getCipherSuites();
        if (cipherSuites != null) {
            this.enabledCipherSuites = CipherSuite.validValuesOf(cipherSuites);
        }
        final String[] protocols = sslParameters.getProtocols();
        if (protocols != null) {
            this.enabledProtocols = ProtocolVersion.namesOf(protocols);
            this.maximumProtocolVersion = ProtocolVersion.NONE;
            for (final ProtocolVersion maximumProtocolVersion : this.enabledProtocols) {
                if (maximumProtocolVersion.compareTo(this.maximumProtocolVersion) > 0) {
                    this.maximumProtocolVersion = maximumProtocolVersion;
                }
            }
        }
        if (sslParameters.getNeedClientAuth()) {
            this.clientAuthType = ClientAuthType.CLIENT_AUTH_REQUIRED;
        }
        else if (sslParameters.getWantClientAuth()) {
            this.clientAuthType = ClientAuthType.CLIENT_AUTH_REQUESTED;
        }
        else {
            this.clientAuthType = ClientAuthType.CLIENT_AUTH_NONE;
        }
        final String endpointIdentificationAlgorithm = sslParameters.getEndpointIdentificationAlgorithm();
        if (endpointIdentificationAlgorithm != null) {
            this.identificationProtocol = endpointIdentificationAlgorithm;
        }
        final List<SNIServerName> serverNames = sslParameters.getServerNames();
        if (serverNames != null) {
            this.noSniExtension = serverNames.isEmpty();
            this.serverNames = serverNames;
        }
        final Collection<SNIMatcher> sniMatchers = sslParameters.getSNIMatchers();
        if (sniMatchers != null) {
            this.noSniMatcher = sniMatchers.isEmpty();
            this.sniMatchers = sniMatchers;
        }
        final String[] applicationProtocols = sslParameters.getApplicationProtocols();
        if (applicationProtocols != null) {
            this.applicationProtocols = applicationProtocols;
        }
        this.preferLocalCipherSuites = sslParameters.getUseCipherSuitesOrder();
    }
    
    void addHandshakeCompletedListener(final HandshakeCompletedListener handshakeCompletedListener) {
        if (this.handshakeListeners == null) {
            this.handshakeListeners = new HashMap<HandshakeCompletedListener, AccessControlContext>(4);
        }
        this.handshakeListeners.put(handshakeCompletedListener, AccessController.getContext());
    }
    
    void removeHandshakeCompletedListener(final HandshakeCompletedListener handshakeCompletedListener) {
        if (this.handshakeListeners == null) {
            throw new IllegalArgumentException("no listeners");
        }
        if (this.handshakeListeners.remove(handshakeCompletedListener) == null) {
            throw new IllegalArgumentException("listener not registered");
        }
        if (this.handshakeListeners.isEmpty()) {
            this.handshakeListeners = null;
        }
    }
    
    boolean isAvailable(final SSLExtension sslExtension) {
        final Iterator<ProtocolVersion> iterator = this.enabledProtocols.iterator();
        while (iterator.hasNext()) {
            if (sslExtension.isAvailable(iterator.next())) {
                if (this.isClientMode) {
                    if (!SSLExtension.ClientExtensions.defaults.contains(sslExtension)) {
                        continue;
                    }
                }
                else if (!SSLExtension.ServerExtensions.defaults.contains(sslExtension)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    boolean isAvailable(final SSLExtension sslExtension, final ProtocolVersion protocolVersion) {
        return sslExtension.isAvailable(protocolVersion) && (this.isClientMode ? SSLExtension.ClientExtensions.defaults.contains(sslExtension) : SSLExtension.ServerExtensions.defaults.contains(sslExtension));
    }
    
    SSLExtension[] getEnabledExtensions(final SSLHandshake sslHandshake) {
        final ArrayList list = new ArrayList();
        for (final SSLExtension sslExtension : SSLExtension.values()) {
            if (sslExtension.handshakeType == sslHandshake && this.isAvailable(sslExtension)) {
                list.add(sslExtension);
            }
        }
        return (SSLExtension[])list.toArray(new SSLExtension[0]);
    }
    
    SSLExtension[] getExclusiveExtensions(final SSLHandshake sslHandshake, final List<SSLExtension> list) {
        final ArrayList list2 = new ArrayList();
        for (final SSLExtension sslExtension : SSLExtension.values()) {
            if (sslExtension.handshakeType == sslHandshake && this.isAvailable(sslExtension) && !list.contains(sslExtension)) {
                list2.add(sslExtension);
            }
        }
        return (SSLExtension[])list2.toArray(new SSLExtension[0]);
    }
    
    SSLExtension[] getEnabledExtensions(final SSLHandshake sslHandshake, final ProtocolVersion protocolVersion) {
        return this.getEnabledExtensions(sslHandshake, Arrays.asList(protocolVersion));
    }
    
    SSLExtension[] getEnabledExtensions(final SSLHandshake sslHandshake, final List<ProtocolVersion> list) {
        final ArrayList list2 = new ArrayList();
        for (final SSLExtension sslExtension : SSLExtension.values()) {
            if (sslExtension.handshakeType == sslHandshake) {
                if (this.isAvailable(sslExtension)) {
                    final Iterator<ProtocolVersion> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        if (sslExtension.isAvailable(iterator.next())) {
                            list2.add(sslExtension);
                            break;
                        }
                    }
                }
            }
        }
        return (SSLExtension[])list2.toArray(new SSLExtension[0]);
    }
    
    void toggleClientMode() {
        this.isClientMode ^= true;
        this.signatureSchemes = (this.isClientMode ? CustomizedClientSignatureSchemes.signatureSchemes : CustomizedServerSignatureSchemes.signatureSchemes);
    }
    
    public Object clone() {
        try {
            final SSLConfiguration sslConfiguration = (SSLConfiguration)super.clone();
            if (this.handshakeListeners != null) {
                sslConfiguration.handshakeListeners = (HashMap)this.handshakeListeners.clone();
            }
            return sslConfiguration;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    private static List<SignatureScheme> getCustomizedSignatureScheme(final String s) {
        String s2 = GetPropertyAction.privilegedGetProperty(s);
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
            SSLLogger.fine("System property " + s + " is set to '" + s2 + "'", new Object[0]);
        }
        if (s2 != null && !s2.isEmpty() && s2.length() > 1 && s2.charAt(0) == '\"' && s2.charAt(s2.length() - 1) == '\"') {
            s2 = s2.substring(1, s2.length() - 1);
        }
        if (s2 != null && !s2.isEmpty()) {
            final String[] split = s2.split(",");
            final ArrayList list = new ArrayList(split.length);
            for (int i = 0; i < split.length; ++i) {
                split[i] = split[i].trim();
                if (!split[i].isEmpty()) {
                    final SignatureScheme name = SignatureScheme.nameOf(split[i]);
                    if (name != null && name.isAvailable) {
                        list.add((Object)name);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                        SSLLogger.fine("The current installed providers do not support signature scheme: " + split[i], new Object[0]);
                    }
                }
            }
            return (List<SignatureScheme>)list;
        }
        return Collections.emptyList();
    }
    
    static {
        allowLegacyResumption = Utilities.getBooleanProperty("jdk.tls.allowLegacyResumption", true);
        allowLegacyMasterSecret = Utilities.getBooleanProperty("jdk.tls.allowLegacyMasterSecret", true);
        useCompatibilityMode = Utilities.getBooleanProperty("jdk.tls.client.useCompatibilityMode", true);
        acknowledgeCloseNotify = Utilities.getBooleanProperty("jdk.tls.acknowledgeCloseNotify", false);
        maxHandshakeMessageSize = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.maxHandshakeMessageSize", 32768));
        maxCertificateChainLength = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.maxCertificateChainLength", 10));
        boolean booleanProperty = Utilities.getBooleanProperty("jdk.tls.useExtendedMasterSecret", true);
        if (booleanProperty) {
            try {
                JsseJce.getKeyGenerator("SunTlsExtendedMasterSecret");
            }
            catch (final NoSuchAlgorithmException ex) {
                booleanProperty = false;
            }
        }
        useExtendedMasterSecret = booleanProperty;
    }
    
    private static final class CustomizedClientSignatureSchemes
    {
        private static List<SignatureScheme> signatureSchemes;
        
        static {
            CustomizedClientSignatureSchemes.signatureSchemes = getCustomizedSignatureScheme("jdk.tls.client.SignatureSchemes");
        }
    }
    
    private static final class CustomizedServerSignatureSchemes
    {
        private static List<SignatureScheme> signatureSchemes;
        
        static {
            CustomizedServerSignatureSchemes.signatureSchemes = getCustomizedSignatureScheme("jdk.tls.server.SignatureSchemes");
        }
    }
}
