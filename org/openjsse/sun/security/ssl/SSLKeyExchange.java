package org.openjsse.sun.security.ssl;

import java.util.HashMap;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Arrays;
import java.io.IOException;

final class SSLKeyExchange implements SSLKeyAgreementGenerator, SSLHandshakeBinding
{
    private final SSLAuthentication authentication;
    private final SSLKeyAgreement keyAgreement;
    
    SSLKeyExchange(final X509Authentication authentication, final SSLKeyAgreement keyAgreement) {
        this.authentication = authentication;
        this.keyAgreement = keyAgreement;
    }
    
    SSLPossession[] createPossessions(final HandshakeContext context) {
        SSLPossession authPossession = null;
        if (this.authentication != null) {
            authPossession = this.authentication.createPossession(context);
            if (authPossession == null) {
                return new SSLPossession[0];
            }
            if (context instanceof ServerHandshakeContext) {
                final ServerHandshakeContext shc = (ServerHandshakeContext)context;
                shc.interimAuthn = authPossession;
            }
        }
        if (this.keyAgreement == T12KeyAgreement.RSA_EXPORT) {
            final X509Authentication.X509Possession x509Possession = (X509Authentication.X509Possession)authPossession;
            if (JsseJce.getRSAKeyLength(x509Possession.popCerts[0].getPublicKey()) <= 512) {
                return (this.authentication != null) ? new SSLPossession[] { authPossession } : new SSLPossession[0];
            }
            final SSLPossession kaPossession = this.keyAgreement.createPossession(context);
            if (kaPossession == null) {
                return new SSLPossession[0];
            }
            return (this.authentication != null) ? new SSLPossession[] { authPossession, kaPossession } : new SSLPossession[] { kaPossession };
        }
        else {
            final SSLPossession kaPossession = this.keyAgreement.createPossession(context);
            if (kaPossession != null) {
                return (this.authentication != null) ? new SSLPossession[] { authPossession, kaPossession } : new SSLPossession[] { kaPossession };
            }
            if (this.keyAgreement == T12KeyAgreement.RSA || this.keyAgreement == T12KeyAgreement.ECDH) {
                return (this.authentication != null) ? new SSLPossession[] { authPossession } : new SSLPossession[0];
            }
            return new SSLPossession[0];
        }
    }
    
    @Override
    public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext) throws IOException {
        return this.keyAgreement.createKeyDerivation(handshakeContext);
    }
    
    @Override
    public SSLHandshake[] getRelatedHandshakers(final HandshakeContext handshakeContext) {
        SSLHandshake[] auHandshakes;
        if (this.authentication != null) {
            auHandshakes = this.authentication.getRelatedHandshakers(handshakeContext);
        }
        else {
            auHandshakes = null;
        }
        final SSLHandshake[] kaHandshakes = this.keyAgreement.getRelatedHandshakers(handshakeContext);
        if (auHandshakes == null || auHandshakes.length == 0) {
            return kaHandshakes;
        }
        if (kaHandshakes == null || kaHandshakes.length == 0) {
            return auHandshakes;
        }
        final SSLHandshake[] producers = Arrays.copyOf(auHandshakes, auHandshakes.length + kaHandshakes.length);
        System.arraycopy(kaHandshakes, 0, producers, auHandshakes.length, kaHandshakes.length);
        return producers;
    }
    
    @Override
    public Map.Entry<Byte, HandshakeProducer>[] getHandshakeProducers(final HandshakeContext handshakeContext) {
        Map.Entry<Byte, HandshakeProducer>[] auProducers;
        if (this.authentication != null) {
            auProducers = this.authentication.getHandshakeProducers(handshakeContext);
        }
        else {
            auProducers = null;
        }
        final Map.Entry<Byte, HandshakeProducer>[] kaProducers = this.keyAgreement.getHandshakeProducers(handshakeContext);
        if (auProducers == null || auProducers.length == 0) {
            return kaProducers;
        }
        if (kaProducers == null || kaProducers.length == 0) {
            return auProducers;
        }
        final Map.Entry<Byte, HandshakeProducer>[] producers = Arrays.copyOf(auProducers, auProducers.length + kaProducers.length);
        System.arraycopy(kaProducers, 0, producers, auProducers.length, kaProducers.length);
        return producers;
    }
    
    @Override
    public Map.Entry<Byte, SSLConsumer>[] getHandshakeConsumers(final HandshakeContext handshakeContext) {
        Map.Entry<Byte, SSLConsumer>[] auConsumers;
        if (this.authentication != null) {
            auConsumers = this.authentication.getHandshakeConsumers(handshakeContext);
        }
        else {
            auConsumers = null;
        }
        final Map.Entry<Byte, SSLConsumer>[] kaConsumers = this.keyAgreement.getHandshakeConsumers(handshakeContext);
        if (auConsumers == null || auConsumers.length == 0) {
            return kaConsumers;
        }
        if (kaConsumers == null || kaConsumers.length == 0) {
            return auConsumers;
        }
        final Map.Entry<Byte, SSLConsumer>[] producers = Arrays.copyOf(auConsumers, auConsumers.length + kaConsumers.length);
        System.arraycopy(kaConsumers, 0, producers, auConsumers.length, kaConsumers.length);
        return producers;
    }
    
    static SSLKeyExchange valueOf(final CipherSuite.KeyExchange keyExchange, final ProtocolVersion protocolVersion) {
        if (keyExchange == null || protocolVersion == null) {
            return null;
        }
        switch (keyExchange) {
            case K_RSA: {
                return SSLKeyExRSA.KE;
            }
            case K_RSA_EXPORT: {
                return SSLKeyExRSAExport.KE;
            }
            case K_DHE_DSS: {
                return SSLKeyExDHEDSS.KE;
            }
            case K_DHE_DSS_EXPORT: {
                return SSLKeyExDHEDSSExport.KE;
            }
            case K_DHE_RSA: {
                if (protocolVersion.useTLS12PlusSpec()) {
                    return SSLKeyExDHERSAOrPSS.KE;
                }
                return SSLKeyExDHERSA.KE;
            }
            case K_DHE_RSA_EXPORT: {
                return SSLKeyExDHERSAExport.KE;
            }
            case K_DH_ANON: {
                return SSLKeyExDHANON.KE;
            }
            case K_DH_ANON_EXPORT: {
                return SSLKeyExDHANONExport.KE;
            }
            case K_ECDH_ECDSA: {
                return SSLKeyExECDHECDSA.KE;
            }
            case K_ECDH_RSA: {
                return SSLKeyExECDHRSA.KE;
            }
            case K_ECDHE_ECDSA: {
                return SSLKeyExECDHEECDSA.KE;
            }
            case K_ECDHE_RSA: {
                if (protocolVersion.useTLS12PlusSpec()) {
                    return SSLKeyExECDHERSAOrPSS.KE;
                }
                return SSLKeyExECDHERSA.KE;
            }
            case K_ECDH_ANON: {
                return SSLKeyExECDHANON.KE;
            }
            default: {
                return null;
            }
        }
    }
    
    static SSLKeyExchange valueOf(final SupportedGroupsExtension.NamedGroup namedGroup) {
        final SSLKeyAgreement ka = T13KeyAgreement.valueOf(namedGroup);
        if (ka != null) {
            return new SSLKeyExchange(null, T13KeyAgreement.valueOf(namedGroup));
        }
        return null;
    }
    
    private static class SSLKeyExRSA
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExRSA.KE = new SSLKeyExchange(X509Authentication.RSA, T12KeyAgreement.RSA);
        }
    }
    
    private static class SSLKeyExRSAExport
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExRSAExport.KE = new SSLKeyExchange(X509Authentication.RSA, T12KeyAgreement.RSA_EXPORT);
        }
    }
    
    private static class SSLKeyExDHEDSS
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExDHEDSS.KE = new SSLKeyExchange(X509Authentication.DSA, T12KeyAgreement.DHE);
        }
    }
    
    private static class SSLKeyExDHEDSSExport
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExDHEDSSExport.KE = new SSLKeyExchange(X509Authentication.DSA, T12KeyAgreement.DHE_EXPORT);
        }
    }
    
    private static class SSLKeyExDHERSA
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExDHERSA.KE = new SSLKeyExchange(X509Authentication.RSA, T12KeyAgreement.DHE);
        }
    }
    
    private static class SSLKeyExDHERSAOrPSS
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExDHERSAOrPSS.KE = new SSLKeyExchange(X509Authentication.RSA_OR_PSS, T12KeyAgreement.DHE);
        }
    }
    
    private static class SSLKeyExDHERSAExport
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExDHERSAExport.KE = new SSLKeyExchange(X509Authentication.RSA, T12KeyAgreement.DHE_EXPORT);
        }
    }
    
    private static class SSLKeyExDHANON
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExDHANON.KE = new SSLKeyExchange(null, T12KeyAgreement.DHE);
        }
    }
    
    private static class SSLKeyExDHANONExport
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExDHANONExport.KE = new SSLKeyExchange(null, T12KeyAgreement.DHE_EXPORT);
        }
    }
    
    private static class SSLKeyExECDHECDSA
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExECDHECDSA.KE = new SSLKeyExchange(X509Authentication.EC, T12KeyAgreement.ECDH);
        }
    }
    
    private static class SSLKeyExECDHRSA
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExECDHRSA.KE = new SSLKeyExchange(X509Authentication.EC, T12KeyAgreement.ECDH);
        }
    }
    
    private static class SSLKeyExECDHEECDSA
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExECDHEECDSA.KE = new SSLKeyExchange(X509Authentication.EC, T12KeyAgreement.ECDHE);
        }
    }
    
    private static class SSLKeyExECDHERSA
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExECDHERSA.KE = new SSLKeyExchange(X509Authentication.RSA, T12KeyAgreement.ECDHE);
        }
    }
    
    private static class SSLKeyExECDHERSAOrPSS
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExECDHERSAOrPSS.KE = new SSLKeyExchange(X509Authentication.RSA_OR_PSS, T12KeyAgreement.ECDHE);
        }
    }
    
    private static class SSLKeyExECDHANON
    {
        private static SSLKeyExchange KE;
        
        static {
            SSLKeyExECDHANON.KE = new SSLKeyExchange(null, T12KeyAgreement.ECDHE);
        }
    }
    
    private enum T12KeyAgreement implements SSLKeyAgreement
    {
        RSA("rsa", (SSLPossessionGenerator)null, RSAKeyExchange.kaGenerator), 
        RSA_EXPORT("rsa_export", RSAKeyExchange.poGenerator, RSAKeyExchange.kaGenerator), 
        DHE("dhe", DHKeyExchange.poGenerator, DHKeyExchange.kaGenerator), 
        DHE_EXPORT("dhe_export", DHKeyExchange.poExportableGenerator, DHKeyExchange.kaGenerator), 
        ECDH("ecdh", (SSLPossessionGenerator)null, ECDHKeyExchange.ecdhKAGenerator), 
        ECDHE("ecdhe", ECDHKeyExchange.poGenerator, ECDHKeyExchange.ecdheKAGenerator);
        
        final String name;
        final SSLPossessionGenerator possessionGenerator;
        final SSLKeyAgreementGenerator keyAgreementGenerator;
        
        private T12KeyAgreement(final String name, final SSLPossessionGenerator possessionGenerator, final SSLKeyAgreementGenerator keyAgreementGenerator) {
            this.name = name;
            this.possessionGenerator = possessionGenerator;
            this.keyAgreementGenerator = keyAgreementGenerator;
        }
        
        @Override
        public SSLPossession createPossession(final HandshakeContext context) {
            if (this.possessionGenerator != null) {
                return this.possessionGenerator.createPossession(context);
            }
            return null;
        }
        
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context) throws IOException {
            return this.keyAgreementGenerator.createKeyDerivation(context);
        }
        
        @Override
        public SSLHandshake[] getRelatedHandshakers(final HandshakeContext handshakeContext) {
            if (!handshakeContext.negotiatedProtocol.useTLS13PlusSpec() && this.possessionGenerator != null) {
                return new SSLHandshake[] { SSLHandshake.SERVER_KEY_EXCHANGE };
            }
            return new SSLHandshake[0];
        }
        
        @Override
        public Map.Entry<Byte, HandshakeProducer>[] getHandshakeProducers(final HandshakeContext handshakeContext) {
            if (handshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                return new Map.Entry[0];
            }
            if (handshakeContext.sslConfig.isClientMode) {
                switch (this) {
                    case RSA:
                    case RSA_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, RSAClientKeyExchange.rsaHandshakeProducer) };
                    }
                    case DHE:
                    case DHE_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, DHClientKeyExchange.dhHandshakeProducer) };
                    }
                    case ECDH: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, ECDHClientKeyExchange.ecdhHandshakeProducer) };
                    }
                    case ECDHE: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, ECDHClientKeyExchange.ecdheHandshakeProducer) };
                    }
                }
            }
            else {
                switch (this) {
                    case RSA_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.SERVER_KEY_EXCHANGE.id, RSAServerKeyExchange.rsaHandshakeProducer) };
                    }
                    case DHE:
                    case DHE_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.SERVER_KEY_EXCHANGE.id, DHServerKeyExchange.dhHandshakeProducer) };
                    }
                    case ECDHE: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.SERVER_KEY_EXCHANGE.id, ECDHServerKeyExchange.ecdheHandshakeProducer) };
                    }
                }
            }
            return new Map.Entry[0];
        }
        
        @Override
        public Map.Entry<Byte, SSLConsumer>[] getHandshakeConsumers(final HandshakeContext handshakeContext) {
            if (handshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                return new Map.Entry[0];
            }
            if (handshakeContext.sslConfig.isClientMode) {
                switch (this) {
                    case RSA_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.SERVER_KEY_EXCHANGE.id, RSAServerKeyExchange.rsaHandshakeConsumer) };
                    }
                    case DHE:
                    case DHE_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.SERVER_KEY_EXCHANGE.id, DHServerKeyExchange.dhHandshakeConsumer) };
                    }
                    case ECDHE: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.SERVER_KEY_EXCHANGE.id, ECDHServerKeyExchange.ecdheHandshakeConsumer) };
                    }
                }
            }
            else {
                switch (this) {
                    case RSA:
                    case RSA_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, RSAClientKeyExchange.rsaHandshakeConsumer) };
                    }
                    case DHE:
                    case DHE_EXPORT: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, DHClientKeyExchange.dhHandshakeConsumer) };
                    }
                    case ECDH: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, ECDHClientKeyExchange.ecdhHandshakeConsumer) };
                    }
                    case ECDHE: {
                        return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CLIENT_KEY_EXCHANGE.id, ECDHClientKeyExchange.ecdheHandshakeConsumer) };
                    }
                }
            }
            return new Map.Entry[0];
        }
    }
    
    private static final class T13KeyAgreement implements SSLKeyAgreement
    {
        private final SupportedGroupsExtension.NamedGroup namedGroup;
        static final Map<SupportedGroupsExtension.NamedGroup, T13KeyAgreement> supportedKeyShares;
        
        private T13KeyAgreement(final SupportedGroupsExtension.NamedGroup namedGroup) {
            this.namedGroup = namedGroup;
        }
        
        static T13KeyAgreement valueOf(final SupportedGroupsExtension.NamedGroup namedGroup) {
            return T13KeyAgreement.supportedKeyShares.get(namedGroup);
        }
        
        @Override
        public SSLPossession createPossession(final HandshakeContext hc) {
            if (this.namedGroup.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                return new ECDHKeyExchange.ECDHEPossession(this.namedGroup, hc.sslContext.getSecureRandom());
            }
            if (this.namedGroup.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                return new DHKeyExchange.DHEPossession(this.namedGroup, hc.sslContext.getSecureRandom());
            }
            return null;
        }
        
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext hc) throws IOException {
            if (this.namedGroup.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                return ECDHKeyExchange.ecdheKAGenerator.createKeyDerivation(hc);
            }
            if (this.namedGroup.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                return DHKeyExchange.kaGenerator.createKeyDerivation(hc);
            }
            return null;
        }
        
        static {
            supportedKeyShares = new HashMap<SupportedGroupsExtension.NamedGroup, T13KeyAgreement>();
            for (final SupportedGroupsExtension.NamedGroup namedGroup : SupportedGroupsExtension.SupportedGroups.supportedNamedGroups) {
                T13KeyAgreement.supportedKeyShares.put(namedGroup, new T13KeyAgreement(namedGroup));
            }
        }
    }
}
