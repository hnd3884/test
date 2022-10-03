package sun.security.ssl;

import java.util.LinkedList;
import java.util.ArrayList;
import sun.security.action.GetPropertyAction;
import java.util.HashMap;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;
import java.util.Collections;
import java.util.EnumSet;
import java.security.AlgorithmConstraints;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.AlgorithmParameters;
import java.util.Map;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.CryptoPrimitive;
import java.util.Set;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

final class SupportedGroupsExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnTradAbsence;
    static final SSLStringizer sgsStringizer;
    static final HandshakeProducer eeNetworkProducer;
    static final SSLExtension.ExtensionConsumer eeOnLoadConsumer;
    
    static {
        chNetworkProducer = new CHSupportedGroupsProducer();
        chOnLoadConsumer = new CHSupportedGroupsConsumer();
        chOnTradAbsence = new CHSupportedGroupsOnTradeAbsence();
        sgsStringizer = new SupportedGroupsStringizer();
        eeNetworkProducer = new EESupportedGroupsProducer();
        eeOnLoadConsumer = new EESupportedGroupsConsumer();
    }
    
    static final class SupportedGroupsSpec implements SSLExtension.SSLExtensionSpec
    {
        final int[] namedGroupsIds;
        
        private SupportedGroupsSpec(final int[] namedGroupsIds) {
            this.namedGroupsIds = namedGroupsIds;
        }
        
        private SupportedGroupsSpec(final List<NamedGroup> list) {
            this.namedGroupsIds = new int[list.size()];
            int n = 0;
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.namedGroupsIds[n++] = ((NamedGroup)iterator.next()).id;
            }
        }
        
        private SupportedGroupsSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid supported_groups extension: insufficient data");
            }
            final byte[] bytes16 = Record.getBytes16(byteBuffer);
            if (byteBuffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid supported_groups extension: unknown extra data");
            }
            if (bytes16 == null || bytes16.length == 0 || bytes16.length % 2 != 0) {
                throw new SSLProtocolException("Invalid supported_groups extension: incomplete data");
            }
            final int[] namedGroupsIds = new int[bytes16.length / 2];
            for (int i = 0, n = 0; i < bytes16.length; namedGroupsIds[n++] = ((bytes16[i++] & 0xFF) << 8 | (bytes16[i++] & 0xFF))) {}
            this.namedGroupsIds = namedGroupsIds;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"versions\": '['{0}']'", Locale.ENGLISH);
            if (this.namedGroupsIds == null || this.namedGroupsIds.length == 0) {
                return messageFormat.format(new Object[] { "<no supported named group specified>" });
            }
            final StringBuilder sb = new StringBuilder(512);
            int n = 1;
            for (final int n2 : this.namedGroupsIds) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(", ");
                }
                sb.append(NamedGroup.nameOf(n2));
            }
            return messageFormat.format(new Object[] { sb.toString() });
        }
    }
    
    private static final class SupportedGroupsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new SupportedGroupsSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    enum NamedGroupType
    {
        NAMED_GROUP_ECDHE, 
        NAMED_GROUP_FFDHE, 
        NAMED_GROUP_XDH, 
        NAMED_GROUP_ARBITRARY, 
        NAMED_GROUP_NONE;
        
        boolean isSupported(final List<CipherSuite> list) {
            for (final CipherSuite cipherSuite : list) {
                if (cipherSuite.keyExchange == null || cipherSuite.keyExchange.groupType == this) {
                    return true;
                }
            }
            return false;
        }
    }
    
    enum NamedGroup
    {
        SECT163_K1(1, "sect163k1", "1.3.132.0.1", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT163_R1(2, "sect163r1", "1.3.132.0.2", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT163_R2(3, "sect163r2", "1.3.132.0.15", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT193_R1(4, "sect193r1", "1.3.132.0.24", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT193_R2(5, "sect193r2", "1.3.132.0.25", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT233_K1(6, "sect233k1", "1.3.132.0.26", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT233_R1(7, "sect233r1", "1.3.132.0.27", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT239_K1(8, "sect239k1", "1.3.132.0.3", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT283_K1(9, "sect283k1", "1.3.132.0.16", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT283_R1(10, "sect283r1", "1.3.132.0.17", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT409_K1(11, "sect409k1", "1.3.132.0.36", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT409_R1(12, "sect409r1", "1.3.132.0.37", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT571_K1(13, "sect571k1", "1.3.132.0.38", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECT571_R1(14, "sect571r1", "1.3.132.0.39", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP160_K1(15, "secp160k1", "1.3.132.0.9", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP160_R1(16, "secp160r1", "1.3.132.0.8", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP160_R2(17, "secp160r2", "1.3.132.0.30", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP192_K1(18, "secp192k1", "1.3.132.0.31", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP192_R1(19, "secp192r1", "1.2.840.10045.3.1.1", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP224_K1(20, "secp224k1", "1.3.132.0.32", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP224_R1(21, "secp224r1", "1.3.132.0.33", true, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP256_K1(22, "secp256k1", "1.3.132.0.10", false, ProtocolVersion.PROTOCOLS_TO_12), 
        SECP256_R1(23, "secp256r1", "1.2.840.10045.3.1.7", true, ProtocolVersion.PROTOCOLS_TO_13), 
        SECP384_R1(24, "secp384r1", "1.3.132.0.34", true, ProtocolVersion.PROTOCOLS_TO_13), 
        SECP521_R1(25, "secp521r1", "1.3.132.0.35", true, ProtocolVersion.PROTOCOLS_TO_13), 
        X25519(29, "x25519", true, "x25519", ProtocolVersion.PROTOCOLS_TO_13), 
        X448(30, "x448", true, "x448", ProtocolVersion.PROTOCOLS_TO_13), 
        FFDHE_2048(256, "ffdhe2048", true, ProtocolVersion.PROTOCOLS_TO_13), 
        FFDHE_3072(257, "ffdhe3072", true, ProtocolVersion.PROTOCOLS_TO_13), 
        FFDHE_4096(258, "ffdhe4096", true, ProtocolVersion.PROTOCOLS_TO_13), 
        FFDHE_6144(259, "ffdhe6144", true, ProtocolVersion.PROTOCOLS_TO_13), 
        FFDHE_8192(260, "ffdhe8192", true, ProtocolVersion.PROTOCOLS_TO_13), 
        ARBITRARY_PRIME(65281, "arbitrary_explicit_prime_curves", ProtocolVersion.PROTOCOLS_TO_12), 
        ARBITRARY_CHAR2(65282, "arbitrary_explicit_char2_curves", ProtocolVersion.PROTOCOLS_TO_12);
        
        final int id;
        final NamedGroupType type;
        final String name;
        final String oid;
        final String algorithm;
        final boolean isFips;
        final ProtocolVersion[] supportedProtocols;
        final boolean isEcAvailable;
        private static final Set<CryptoPrimitive> KEY_AGREEMENT_PRIMITIVE_SET;
        
        private NamedGroup(final int id, final String name, final String oid, final boolean isFips, final ProtocolVersion[] supportedProtocols) {
            this.id = id;
            this.type = NamedGroupType.NAMED_GROUP_ECDHE;
            this.name = name;
            this.oid = oid;
            this.algorithm = "EC";
            this.isFips = isFips;
            this.supportedProtocols = supportedProtocols;
            this.isEcAvailable = JsseJce.isEcAvailable();
        }
        
        private NamedGroup(final int id, final String name, final boolean isFips, final String algorithm, final ProtocolVersion[] supportedProtocols) {
            this.id = id;
            this.type = NamedGroupType.NAMED_GROUP_XDH;
            this.name = name;
            this.oid = null;
            this.algorithm = algorithm;
            this.isFips = isFips;
            this.supportedProtocols = supportedProtocols;
            this.isEcAvailable = true;
        }
        
        private NamedGroup(final int id, final String name, final boolean isFips, final ProtocolVersion[] supportedProtocols) {
            this.id = id;
            this.type = NamedGroupType.NAMED_GROUP_FFDHE;
            this.name = name;
            this.oid = null;
            this.algorithm = "DiffieHellman";
            this.isFips = isFips;
            this.supportedProtocols = supportedProtocols;
            this.isEcAvailable = true;
        }
        
        private NamedGroup(final int id, final String name, final ProtocolVersion[] supportedProtocols) {
            this.id = id;
            this.type = NamedGroupType.NAMED_GROUP_ARBITRARY;
            this.name = name;
            this.oid = null;
            this.algorithm = "EC";
            this.isFips = false;
            this.supportedProtocols = supportedProtocols;
            this.isEcAvailable = true;
        }
        
        static NamedGroup valueOf(final int n) {
            for (final NamedGroup namedGroup : values()) {
                if (namedGroup.id == n) {
                    return namedGroup;
                }
            }
            return null;
        }
        
        static NamedGroup valueOf(final ECParameterSpec ecParameterSpec) {
            final String namedCurveOid = JsseJce.getNamedCurveOid(ecParameterSpec);
            if (namedCurveOid != null && !namedCurveOid.isEmpty()) {
                for (final NamedGroup namedGroup : values()) {
                    if (namedGroup.type == NamedGroupType.NAMED_GROUP_ECDHE && namedCurveOid.equals(namedGroup.oid)) {
                        return namedGroup;
                    }
                }
            }
            return null;
        }
        
        static NamedGroup valueOf(final DHParameterSpec dhParameterSpec) {
            for (final Map.Entry entry : SupportedGroups.namedGroupParams.entrySet()) {
                final NamedGroup namedGroup = (NamedGroup)entry.getKey();
                if (namedGroup.type != NamedGroupType.NAMED_GROUP_FFDHE) {
                    continue;
                }
                DHParameterSpec dhParameterSpec2 = null;
                final AlgorithmParameters algorithmParameters = (AlgorithmParameters)entry.getValue();
                try {
                    dhParameterSpec2 = algorithmParameters.getParameterSpec(DHParameterSpec.class);
                }
                catch (final InvalidParameterSpecException ex) {}
                if (dhParameterSpec2 == null) {
                    continue;
                }
                if (dhParameterSpec2.getP().equals(dhParameterSpec.getP()) && dhParameterSpec2.getG().equals(dhParameterSpec.getG())) {
                    return namedGroup;
                }
            }
            return null;
        }
        
        static NamedGroup nameOf(final String s) {
            for (final NamedGroup namedGroup : values()) {
                if (namedGroup.name.equals(s)) {
                    return namedGroup;
                }
            }
            return null;
        }
        
        static String nameOf(final int n) {
            for (final NamedGroup namedGroup : values()) {
                if (namedGroup.id == n) {
                    return namedGroup.name;
                }
            }
            return "UNDEFINED-NAMED-GROUP(" + n + ")";
        }
        
        boolean isAvailable(final List<ProtocolVersion> list) {
            if (this.isEcAvailable) {
                final ProtocolVersion[] supportedProtocols = this.supportedProtocols;
                for (int length = supportedProtocols.length, i = 0; i < length; ++i) {
                    if (list.contains(supportedProtocols[i])) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        boolean isAvailable(final ProtocolVersion protocolVersion) {
            if (this.isEcAvailable) {
                final ProtocolVersion[] supportedProtocols = this.supportedProtocols;
                for (int length = supportedProtocols.length, i = 0; i < length; ++i) {
                    if (protocolVersion == supportedProtocols[i]) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        boolean isSupported(final List<CipherSuite> list) {
            for (final CipherSuite cipherSuite : list) {
                if (this.isAvailable(cipherSuite.supportedProtocols) && (cipherSuite.keyExchange == null || cipherSuite.keyExchange.groupType == this.type)) {
                    return true;
                }
            }
            return false;
        }
        
        AlgorithmParameters getParameters() {
            return SupportedGroups.namedGroupParams.get(this);
        }
        
        AlgorithmParameterSpec getParameterSpec() {
            if (this.type == NamedGroupType.NAMED_GROUP_ECDHE) {
                return SupportedGroups.getECGenParamSpec(this);
            }
            if (this.type == NamedGroupType.NAMED_GROUP_FFDHE) {
                return SupportedGroups.getDHParameterSpec(this);
            }
            return null;
        }
        
        boolean isPermitted(final AlgorithmConstraints algorithmConstraints) {
            return algorithmConstraints.permits(NamedGroup.KEY_AGREEMENT_PRIMITIVE_SET, this.name, null) && algorithmConstraints.permits(NamedGroup.KEY_AGREEMENT_PRIMITIVE_SET, this.algorithm, this.getParameters());
        }
        
        static {
            KEY_AGREEMENT_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.KEY_AGREEMENT));
        }
    }
    
    static class SupportedGroups
    {
        static final boolean enableFFDHE;
        static final Map<NamedGroup, AlgorithmParameters> namedGroupParams;
        static final NamedGroup[] supportedNamedGroups;
        
        private static boolean isAvailableGroup(final NamedGroup namedGroup) {
            AlgorithmParameters algorithmParameters = null;
            AlgorithmParameterSpec ffdhedhParameterSpec = null;
            Label_0072: {
                if (namedGroup.type == NamedGroupType.NAMED_GROUP_ECDHE) {
                    if (namedGroup.oid == null) {
                        break Label_0072;
                    }
                    try {
                        algorithmParameters = JsseJce.getAlgorithmParameters("EC");
                        ffdhedhParameterSpec = new ECGenParameterSpec(namedGroup.oid);
                        break Label_0072;
                    }
                    catch (final NoSuchAlgorithmException ex) {
                        return false;
                    }
                }
                if (namedGroup.type == NamedGroupType.NAMED_GROUP_FFDHE) {
                    try {
                        algorithmParameters = JsseJce.getAlgorithmParameters("DiffieHellman");
                        ffdhedhParameterSpec = getFFDHEDHParameterSpec(namedGroup);
                    }
                    catch (final NoSuchAlgorithmException ex2) {
                        return false;
                    }
                }
            }
            if (algorithmParameters != null && ffdhedhParameterSpec != null) {
                try {
                    algorithmParameters.init(ffdhedhParameterSpec);
                }
                catch (final InvalidParameterSpecException ex3) {
                    return false;
                }
                SupportedGroups.namedGroupParams.put(namedGroup, algorithmParameters);
                return true;
            }
            return false;
        }
        
        private static DHParameterSpec getFFDHEDHParameterSpec(final NamedGroup namedGroup) {
            DHParameterSpec dhParameterSpec = null;
            switch (namedGroup) {
                case FFDHE_2048: {
                    dhParameterSpec = PredefinedDHParameterSpecs.ffdheParams.get(2048);
                    break;
                }
                case FFDHE_3072: {
                    dhParameterSpec = PredefinedDHParameterSpecs.ffdheParams.get(3072);
                    break;
                }
                case FFDHE_4096: {
                    dhParameterSpec = PredefinedDHParameterSpecs.ffdheParams.get(4096);
                    break;
                }
                case FFDHE_6144: {
                    dhParameterSpec = PredefinedDHParameterSpecs.ffdheParams.get(6144);
                    break;
                }
                case FFDHE_8192: {
                    dhParameterSpec = PredefinedDHParameterSpecs.ffdheParams.get(8192);
                    break;
                }
            }
            return dhParameterSpec;
        }
        
        private static DHParameterSpec getPredefinedDHParameterSpec(final NamedGroup namedGroup) {
            DHParameterSpec dhParameterSpec = null;
            switch (namedGroup) {
                case FFDHE_2048: {
                    dhParameterSpec = PredefinedDHParameterSpecs.definedParams.get(2048);
                    break;
                }
                case FFDHE_3072: {
                    dhParameterSpec = PredefinedDHParameterSpecs.definedParams.get(3072);
                    break;
                }
                case FFDHE_4096: {
                    dhParameterSpec = PredefinedDHParameterSpecs.definedParams.get(4096);
                    break;
                }
                case FFDHE_6144: {
                    dhParameterSpec = PredefinedDHParameterSpecs.definedParams.get(6144);
                    break;
                }
                case FFDHE_8192: {
                    dhParameterSpec = PredefinedDHParameterSpecs.definedParams.get(8192);
                    break;
                }
            }
            return dhParameterSpec;
        }
        
        static ECGenParameterSpec getECGenParamSpec(final NamedGroup namedGroup) {
            if (namedGroup.type != NamedGroupType.NAMED_GROUP_ECDHE) {
                throw new RuntimeException("Not a named EC group: " + namedGroup);
            }
            final AlgorithmParameters algorithmParameters = SupportedGroups.namedGroupParams.get(namedGroup);
            if (algorithmParameters == null) {
                throw new RuntimeException("Not a supported EC named group: " + namedGroup);
            }
            try {
                return algorithmParameters.getParameterSpec(ECGenParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ex) {
                return new ECGenParameterSpec(namedGroup.oid);
            }
        }
        
        static DHParameterSpec getDHParameterSpec(final NamedGroup namedGroup) {
            if (namedGroup.type != NamedGroupType.NAMED_GROUP_FFDHE) {
                throw new RuntimeException("Not a named DH group: " + namedGroup);
            }
            final AlgorithmParameters algorithmParameters = SupportedGroups.namedGroupParams.get(namedGroup);
            if (algorithmParameters == null) {
                throw new RuntimeException("Not a supported DH named group: " + namedGroup);
            }
            try {
                return algorithmParameters.getParameterSpec(DHParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ex) {
                return getPredefinedDHParameterSpec(namedGroup);
            }
        }
        
        static boolean isActivatable(final AlgorithmConstraints algorithmConstraints, final NamedGroupType namedGroupType) {
            int n = 0;
            for (final NamedGroup namedGroup : SupportedGroups.supportedNamedGroups) {
                if (namedGroup.type == namedGroupType) {
                    if (namedGroup.isPermitted(algorithmConstraints)) {
                        return true;
                    }
                    if (n == 0 && namedGroupType == NamedGroupType.NAMED_GROUP_FFDHE) {
                        n = 1;
                    }
                }
            }
            return n == 0 && namedGroupType == NamedGroupType.NAMED_GROUP_FFDHE;
        }
        
        static boolean isActivatable(final AlgorithmConstraints algorithmConstraints, final NamedGroup namedGroup) {
            return isSupported(namedGroup) && namedGroup.isPermitted(algorithmConstraints);
        }
        
        static boolean isSupported(final NamedGroup namedGroup) {
            final NamedGroup[] supportedNamedGroups = SupportedGroups.supportedNamedGroups;
            for (int length = supportedNamedGroups.length, i = 0; i < length; ++i) {
                if (namedGroup.id == supportedNamedGroups[i].id) {
                    return true;
                }
            }
            return false;
        }
        
        static NamedGroup getPreferredGroup(final ProtocolVersion protocolVersion, final AlgorithmConstraints algorithmConstraints, final NamedGroupType namedGroupType, final List<NamedGroup> list) {
            for (final NamedGroup namedGroup : list) {
                if (namedGroup.type == namedGroupType && namedGroup.isAvailable(protocolVersion) && isSupported(namedGroup) && namedGroup.isPermitted(algorithmConstraints)) {
                    return namedGroup;
                }
            }
            return null;
        }
        
        static NamedGroup getPreferredGroup(final ProtocolVersion protocolVersion, final AlgorithmConstraints algorithmConstraints, final NamedGroupType namedGroupType) {
            for (final NamedGroup namedGroup : SupportedGroups.supportedNamedGroups) {
                if (namedGroup.type == namedGroupType && namedGroup.isAvailable(protocolVersion) && namedGroup.isPermitted(algorithmConstraints)) {
                    return namedGroup;
                }
            }
            return null;
        }
        
        static {
            enableFFDHE = Utilities.getBooleanProperty("jsse.enableFFDHE", true);
            namedGroupParams = new HashMap<NamedGroup, AlgorithmParameters>();
            final boolean fips = SunJSSE.isFIPS();
            String s = GetPropertyAction.privilegedGetProperty("jdk.tls.namedGroups");
            if (s != null && !s.isEmpty() && s.length() > 1 && s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') {
                s = s.substring(1, s.length() - 1);
            }
            ArrayList list;
            if (s != null && !s.isEmpty()) {
                final String[] split = s.split(",");
                list = new ArrayList<NamedGroup>(split.length);
                final String[] array = split;
                for (int length = array.length, i = 0; i < length; ++i) {
                    final String trim = array[i].trim();
                    if (!trim.isEmpty()) {
                        final NamedGroup name = NamedGroup.nameOf(trim);
                        if (name != null && (!fips || name.isFips) && isAvailableGroup(name)) {
                            list.add(name);
                        }
                    }
                }
                if (list.isEmpty()) {
                    throw new IllegalArgumentException("System property jdk.tls.namedGroups(" + s + ") contains no supported named groups");
                }
            }
            else {
                NamedGroup[] array2;
                if (fips) {
                    array2 = new NamedGroup[] { NamedGroup.SECP256_R1, NamedGroup.SECP384_R1, NamedGroup.SECP521_R1, NamedGroup.FFDHE_2048, NamedGroup.FFDHE_3072, NamedGroup.FFDHE_4096, NamedGroup.FFDHE_6144, NamedGroup.FFDHE_8192 };
                }
                else {
                    array2 = new NamedGroup[] { NamedGroup.SECP256_R1, NamedGroup.SECP384_R1, NamedGroup.SECP521_R1, NamedGroup.FFDHE_2048, NamedGroup.FFDHE_3072, NamedGroup.FFDHE_4096, NamedGroup.FFDHE_6144, NamedGroup.FFDHE_8192 };
                }
                list = new ArrayList<NamedGroup>(array2.length);
                for (final NamedGroup namedGroup : array2) {
                    if (isAvailableGroup(namedGroup)) {
                        list.add(namedGroup);
                    }
                }
                if (list.isEmpty() && SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("No default named groups", new Object[0]);
                }
            }
            supportedNamedGroups = new NamedGroup[list.size()];
            int n = 0;
            final Iterator<NamedGroup> iterator = list.iterator();
            while (iterator.hasNext()) {
                SupportedGroups.supportedNamedGroups[n++] = iterator.next();
            }
        }
    }
    
    private static final class CHSupportedGroupsProducer extends SupportedGroups implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return null;
            }
            final ArrayList list = new ArrayList(SupportedGroups.supportedNamedGroups.length);
            for (final NamedGroup namedGroup : SupportedGroups.supportedNamedGroups) {
                if (SupportedGroups.enableFFDHE || namedGroup.type != NamedGroupType.NAMED_GROUP_FFDHE) {
                    if (namedGroup.isAvailable(clientHandshakeContext.activeProtocols) && namedGroup.isSupported(clientHandshakeContext.activeCipherSuites) && clientHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), namedGroup.algorithm, CHSupportedGroupsProducer.namedGroupParams.get(namedGroup))) {
                        list.add(namedGroup);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Ignore inactive or disabled named group: " + namedGroup.name, new Object[0]);
                    }
                }
            }
            if (list.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("no available named group", new Object[0]);
                }
                return null;
            }
            final int n = list.size() << 1;
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                Record.putInt16(wrap, ((NamedGroup)iterator.next()).id);
            }
            clientHandshakeContext.clientRequestedNamedGroups = (List<NamedGroup>)Collections.unmodifiableList((List<?>)list);
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_GROUPS, new SupportedGroupsSpec((List)list));
            return array;
        }
    }
    
    private static final class CHSupportedGroupsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return;
            }
            SupportedGroupsSpec supportedGroupsSpec;
            try {
                supportedGroupsSpec = new SupportedGroupsSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            final LinkedList clientRequestedNamedGroups = new LinkedList();
            final int[] namedGroupsIds = supportedGroupsSpec.namedGroupsIds;
            for (int length = namedGroupsIds.length, i = 0; i < length; ++i) {
                final NamedGroup value = NamedGroup.valueOf(namedGroupsIds[i]);
                if (value != null) {
                    clientRequestedNamedGroups.add(value);
                }
            }
            serverHandshakeContext.clientRequestedNamedGroups = clientRequestedNamedGroups;
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_GROUPS, supportedGroupsSpec);
        }
    }
    
    private static final class CHSupportedGroupsOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec() && serverHandshakeContext.handshakeExtensions.containsKey(SSLExtension.CH_KEY_SHARE)) {
                throw serverHandshakeContext.conContext.fatal(Alert.MISSING_EXTENSION, "No supported_groups extension to work with the key_share extension");
            }
        }
    }
    
    private static final class EESupportedGroupsProducer extends SupportedGroups implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.EE_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return null;
            }
            final ArrayList list = new ArrayList(SupportedGroups.supportedNamedGroups.length);
            for (final NamedGroup namedGroup : SupportedGroups.supportedNamedGroups) {
                if (SupportedGroups.enableFFDHE || namedGroup.type != NamedGroupType.NAMED_GROUP_FFDHE) {
                    if (namedGroup.isAvailable(serverHandshakeContext.activeProtocols) && namedGroup.isSupported(serverHandshakeContext.activeCipherSuites) && serverHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), namedGroup.algorithm, EESupportedGroupsProducer.namedGroupParams.get(namedGroup))) {
                        list.add(namedGroup);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Ignore inactive or disabled named group: " + namedGroup.name, new Object[0]);
                    }
                }
            }
            if (list.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("no available named group", new Object[0]);
                }
                return null;
            }
            final int n = list.size() << 1;
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                Record.putInt16(wrap, ((NamedGroup)iterator.next()).id);
            }
            serverHandshakeContext.conContext.serverRequestedNamedGroups = (List<NamedGroup>)Collections.unmodifiableList((List<?>)list);
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.EE_SUPPORTED_GROUPS, new SupportedGroupsSpec((List)list));
            return array;
        }
    }
    
    private static final class EESupportedGroupsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.EE_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return;
            }
            SupportedGroupsSpec supportedGroupsSpec;
            try {
                supportedGroupsSpec = new SupportedGroupsSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            final ArrayList serverRequestedNamedGroups = new ArrayList(supportedGroupsSpec.namedGroupsIds.length);
            final int[] namedGroupsIds = supportedGroupsSpec.namedGroupsIds;
            for (int length = namedGroupsIds.length, i = 0; i < length; ++i) {
                final NamedGroup value = NamedGroup.valueOf(namedGroupsIds[i]);
                if (value != null) {
                    serverRequestedNamedGroups.add((Object)value);
                }
            }
            clientHandshakeContext.conContext.serverRequestedNamedGroups = (List<NamedGroup>)serverRequestedNamedGroups;
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.EE_SUPPORTED_GROUPS, supportedGroupsSpec);
        }
    }
}
