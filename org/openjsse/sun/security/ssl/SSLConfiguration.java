package org.openjsse.sun.security.ssl;

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
    boolean enableRetransmissions;
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
    
    SSLConfiguration(final SSLContextImpl sslContext, final boolean isClientMode) {
        this.userSpecifiedAlgorithmConstraints = SSLAlgorithmConstraints.DEFAULT;
        this.enabledProtocols = sslContext.getDefaultProtocolVersions(!isClientMode);
        this.enabledCipherSuites = sslContext.getDefaultCipherSuites(!isClientMode);
        this.clientAuthType = ClientAuthType.CLIENT_AUTH_NONE;
        this.identificationProtocol = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.preferLocalCipherSuites = false;
        this.applicationProtocols = new String[0];
        this.enableRetransmissions = sslContext.isDTLS();
        this.maximumPacketSize = 0;
        this.signatureSchemes = (isClientMode ? CustomizedClientSignatureSchemes.signatureSchemes : CustomizedServerSignatureSchemes.signatureSchemes);
        this.maximumProtocolVersion = ProtocolVersion.NONE;
        for (final ProtocolVersion pv : this.enabledProtocols) {
            if (pv.compareTo(this.maximumProtocolVersion) > 0) {
                this.maximumProtocolVersion = pv;
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
        final org.openjsse.javax.net.ssl.SSLParameters params = new org.openjsse.javax.net.ssl.SSLParameters();
        params.setAlgorithmConstraints(this.userSpecifiedAlgorithmConstraints);
        params.setProtocols(ProtocolVersion.toStringArray(this.enabledProtocols));
        params.setCipherSuites(CipherSuite.namesOf(this.enabledCipherSuites));
        switch (this.clientAuthType) {
            case CLIENT_AUTH_REQUIRED: {
                params.setNeedClientAuth(true);
                break;
            }
            case CLIENT_AUTH_REQUESTED: {
                params.setWantClientAuth(true);
                break;
            }
            default: {
                params.setWantClientAuth(false);
                break;
            }
        }
        params.setEndpointIdentificationAlgorithm(this.identificationProtocol);
        if (this.serverNames.isEmpty() && !this.noSniExtension) {
            params.setServerNames(null);
        }
        else {
            params.setServerNames(this.serverNames);
        }
        if (this.sniMatchers.isEmpty() && !this.noSniMatcher) {
            params.setSNIMatchers(null);
        }
        else {
            params.setSNIMatchers(this.sniMatchers);
        }
        params.setApplicationProtocols(this.applicationProtocols);
        params.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
        params.setEnableRetransmissions(this.enableRetransmissions);
        params.setMaximumPacketSize(this.maximumPacketSize);
        return params;
    }
    
    void setSSLParameters(final SSLParameters params) {
        final AlgorithmConstraints ac = params.getAlgorithmConstraints();
        if (ac != null) {
            this.userSpecifiedAlgorithmConstraints = ac;
        }
        String[] sa = params.getCipherSuites();
        if (sa != null) {
            this.enabledCipherSuites = CipherSuite.validValuesOf(sa);
        }
        sa = params.getProtocols();
        if (sa != null) {
            this.enabledProtocols = ProtocolVersion.namesOf(sa);
            this.maximumProtocolVersion = ProtocolVersion.NONE;
            for (final ProtocolVersion pv : this.enabledProtocols) {
                if (pv.compareTo(this.maximumProtocolVersion) > 0) {
                    this.maximumProtocolVersion = pv;
                }
            }
        }
        if (params.getNeedClientAuth()) {
            this.clientAuthType = ClientAuthType.CLIENT_AUTH_REQUIRED;
        }
        else if (params.getWantClientAuth()) {
            this.clientAuthType = ClientAuthType.CLIENT_AUTH_REQUESTED;
        }
        else {
            this.clientAuthType = ClientAuthType.CLIENT_AUTH_NONE;
        }
        final String s = params.getEndpointIdentificationAlgorithm();
        if (s != null) {
            this.identificationProtocol = s;
        }
        final List<SNIServerName> sniNames = params.getServerNames();
        if (sniNames != null) {
            this.noSniExtension = sniNames.isEmpty();
            this.serverNames = sniNames;
        }
        final Collection<SNIMatcher> matchers = params.getSNIMatchers();
        if (matchers != null) {
            this.noSniMatcher = matchers.isEmpty();
            this.sniMatchers = matchers;
        }
        if (params instanceof org.openjsse.javax.net.ssl.SSLParameters) {
            sa = ((org.openjsse.javax.net.ssl.SSLParameters)params).getApplicationProtocols();
            if (sa != null) {
                this.applicationProtocols = sa;
            }
            this.enableRetransmissions = ((org.openjsse.javax.net.ssl.SSLParameters)params).getEnableRetransmissions();
            this.maximumPacketSize = ((org.openjsse.javax.net.ssl.SSLParameters)params).getMaximumPacketSize();
        }
        this.preferLocalCipherSuites = params.getUseCipherSuitesOrder();
    }
    
    void addHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        if (this.handshakeListeners == null) {
            this.handshakeListeners = new HashMap<HandshakeCompletedListener, AccessControlContext>(4);
        }
        this.handshakeListeners.put(listener, AccessController.getContext());
    }
    
    void removeHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        if (this.handshakeListeners == null) {
            throw new IllegalArgumentException("no listeners");
        }
        if (this.handshakeListeners.remove(listener) == null) {
            throw new IllegalArgumentException("listener not registered");
        }
        if (this.handshakeListeners.isEmpty()) {
            this.handshakeListeners = null;
        }
    }
    
    boolean isAvailable(final SSLExtension extension) {
        for (final ProtocolVersion protocolVersion : this.enabledProtocols) {
            if (extension.isAvailable(protocolVersion)) {
                if (this.isClientMode) {
                    if (!SSLExtension.ClientExtensions.defaults.contains(extension)) {
                        continue;
                    }
                }
                else if (!SSLExtension.ServerExtensions.defaults.contains(extension)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    boolean isAvailable(final SSLExtension extension, final ProtocolVersion protocolVersion) {
        return extension.isAvailable(protocolVersion) && (this.isClientMode ? SSLExtension.ClientExtensions.defaults.contains(extension) : SSLExtension.ServerExtensions.defaults.contains(extension));
    }
    
    SSLExtension[] getEnabledExtensions(final SSLHandshake handshakeType) {
        final List<SSLExtension> extensions = new ArrayList<SSLExtension>();
        for (final SSLExtension extension : SSLExtension.values()) {
            if (extension.handshakeType == handshakeType && this.isAvailable(extension)) {
                extensions.add(extension);
            }
        }
        return extensions.toArray(new SSLExtension[0]);
    }
    
    SSLExtension[] getExclusiveExtensions(final SSLHandshake handshakeType, final List<SSLExtension> excluded) {
        final List<SSLExtension> extensions = new ArrayList<SSLExtension>();
        for (final SSLExtension extension : SSLExtension.values()) {
            if (extension.handshakeType == handshakeType && this.isAvailable(extension) && !excluded.contains(extension)) {
                extensions.add(extension);
            }
        }
        return extensions.toArray(new SSLExtension[0]);
    }
    
    SSLExtension[] getEnabledExtensions(final SSLHandshake handshakeType, final ProtocolVersion protocolVersion) {
        return this.getEnabledExtensions(handshakeType, Arrays.asList(protocolVersion));
    }
    
    SSLExtension[] getEnabledExtensions(final SSLHandshake handshakeType, final List<ProtocolVersion> activeProtocols) {
        final List<SSLExtension> extensions = new ArrayList<SSLExtension>();
        for (final SSLExtension extension : SSLExtension.values()) {
            if (extension.handshakeType == handshakeType) {
                if (this.isAvailable(extension)) {
                    for (final ProtocolVersion protocolVersion : activeProtocols) {
                        if (extension.isAvailable(protocolVersion)) {
                            extensions.add(extension);
                            break;
                        }
                    }
                }
            }
        }
        return extensions.toArray(new SSLExtension[0]);
    }
    
    void toggleClientMode() {
        this.isClientMode ^= true;
        this.signatureSchemes = (this.isClientMode ? CustomizedClientSignatureSchemes.signatureSchemes : CustomizedServerSignatureSchemes.signatureSchemes);
    }
    
    public Object clone() {
        try {
            final SSLConfiguration config = (SSLConfiguration)super.clone();
            if (this.handshakeListeners != null) {
                config.handshakeListeners = (HashMap)this.handshakeListeners.clone();
            }
            return config;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    private static List<SignatureScheme> getCustomizedSignatureScheme(final String propertyName) {
        String property = GetPropertyAction.privilegedGetProperty(propertyName);
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
            SSLLogger.fine("System property " + propertyName + " is set to '" + property + "'", new Object[0]);
        }
        if (property != null && !property.isEmpty() && property.length() > 1 && property.charAt(0) == '\"' && property.charAt(property.length() - 1) == '\"') {
            property = property.substring(1, property.length() - 1);
        }
        if (property != null && !property.isEmpty()) {
            final String[] signatureSchemeNames = property.split(",");
            final List<SignatureScheme> signatureSchemes = new ArrayList<SignatureScheme>(signatureSchemeNames.length);
            for (int i = 0; i < signatureSchemeNames.length; ++i) {
                signatureSchemeNames[i] = signatureSchemeNames[i].trim();
                if (!signatureSchemeNames[i].isEmpty()) {
                    final SignatureScheme scheme = SignatureScheme.nameOf(signatureSchemeNames[i]);
                    if (scheme != null && scheme.isAvailable) {
                        signatureSchemes.add(scheme);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                        SSLLogger.fine("The current installed providers do not support signature scheme: " + signatureSchemeNames[i], new Object[0]);
                    }
                }
            }
            return signatureSchemes;
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
        boolean supportExtendedMasterSecret = Utilities.getBooleanProperty("jdk.tls.useExtendedMasterSecret", true);
        if (supportExtendedMasterSecret) {
            try {
                JsseJce.getKeyGenerator("SunTlsExtendedMasterSecret");
            }
            catch (final NoSuchAlgorithmException nae) {
                supportExtendedMasterSecret = false;
            }
        }
        useExtendedMasterSecret = supportExtendedMasterSecret;
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
