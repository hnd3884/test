package org.openjsse.sun.security.ssl;

import java.util.LinkedList;
import java.util.Collections;
import java.util.ArrayList;
import sun.security.action.GetPropertyAction;
import java.util.HashMap;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.AlgorithmConstraints;
import java.security.spec.ECGenParameterSpec;
import java.util.Map;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.ECParameterSpec;
import org.openjsse.sun.security.util.CurveDB;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidParameterSpecException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyAgreement;
import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
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
        
        private SupportedGroupsSpec(final List<NamedGroup> namedGroups) {
            this.namedGroupsIds = new int[namedGroups.size()];
            int i = 0;
            for (final NamedGroup ng : namedGroups) {
                this.namedGroupsIds[i++] = ng.id;
            }
        }
        
        private SupportedGroupsSpec(final ByteBuffer m) throws IOException {
            if (m.remaining() < 2) {
                throw new SSLProtocolException("Invalid supported_groups extension: insufficient data");
            }
            final byte[] ngs = Record.getBytes16(m);
            if (m.hasRemaining()) {
                throw new SSLProtocolException("Invalid supported_groups extension: unknown extra data");
            }
            if (ngs == null || ngs.length == 0 || ngs.length % 2 != 0) {
                throw new SSLProtocolException("Invalid supported_groups extension: incomplete data");
            }
            final int[] ids = new int[ngs.length / 2];
            for (int i = 0, j = 0; i < ngs.length; ids[j++] = ((ngs[i++] & 0xFF) << 8 | (ngs[i++] & 0xFF))) {}
            this.namedGroupsIds = ids;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"versions\": '['{0}']'", Locale.ENGLISH);
            if (this.namedGroupsIds == null || this.namedGroupsIds.length == 0) {
                final Object[] messageFields = { "<no supported named group specified>" };
                return messageFormat.format(messageFields);
            }
            final StringBuilder builder = new StringBuilder(512);
            boolean isFirst = true;
            for (final int ngid : this.namedGroupsIds) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append(", ");
                }
                builder.append(NamedGroup.nameOf(ngid));
            }
            final Object[] messageFields2 = { builder.toString() };
            return messageFormat.format(messageFields2);
        }
    }
    
    private static final class SupportedGroupsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new SupportedGroupsSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    enum NamedGroupType
    {
        NAMED_GROUP_ECDHE("EC"), 
        NAMED_GROUP_FFDHE("DiffieHellman"), 
        NAMED_GROUP_XDH("XDH"), 
        NAMED_GROUP_ARBITRARY("EC"), 
        NAMED_GROUP_NONE("");
        
        private final String algorithm;
        
        private NamedGroupType(final String algorithm) {
            this.algorithm = algorithm;
        }
        
        boolean isSupported(final List<CipherSuite> cipherSuites) {
            for (final CipherSuite cs : cipherSuites) {
                if (cs.keyExchange == null || cs.keyExchange.groupType == this) {
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
        X25519(29, "x25519", true, "x25519", ProtocolVersion.PROTOCOLS_TO_13, (AlgorithmParameterSpec)NamedParameterSpec.X25519), 
        X448(30, "x448", true, "x448", ProtocolVersion.PROTOCOLS_TO_13, (AlgorithmParameterSpec)NamedParameterSpec.X448), 
        FFDHE_2048(256, "ffdhe2048", true, ProtocolVersion.PROTOCOLS_TO_13, (AlgorithmParameterSpec)PredefinedDHParameterSpecs.ffdheParams.get(2048)), 
        FFDHE_3072(257, "ffdhe3072", true, ProtocolVersion.PROTOCOLS_TO_13, (AlgorithmParameterSpec)PredefinedDHParameterSpecs.ffdheParams.get(3072)), 
        FFDHE_4096(258, "ffdhe4096", true, ProtocolVersion.PROTOCOLS_TO_13, (AlgorithmParameterSpec)PredefinedDHParameterSpecs.ffdheParams.get(4096)), 
        FFDHE_6144(259, "ffdhe6144", true, ProtocolVersion.PROTOCOLS_TO_13, (AlgorithmParameterSpec)PredefinedDHParameterSpecs.ffdheParams.get(6144)), 
        FFDHE_8192(260, "ffdhe8192", true, ProtocolVersion.PROTOCOLS_TO_13, (AlgorithmParameterSpec)PredefinedDHParameterSpecs.ffdheParams.get(8192)), 
        ARBITRARY_PRIME(65281, "arbitrary_explicit_prime_curves", ProtocolVersion.PROTOCOLS_TO_12), 
        ARBITRARY_CHAR2(65282, "arbitrary_explicit_char2_curves", ProtocolVersion.PROTOCOLS_TO_12);
        
        final int id;
        final NamedGroupType type;
        final String name;
        final String oid;
        final String algorithm;
        final boolean isFips;
        final ProtocolVersion[] supportedProtocols;
        final AlgorithmParameterSpec keAlgParamSpec;
        AlgorithmParameters keAlgParams;
        boolean isAvailable;
        
        private NamedGroup(final int id, final NamedGroupType type, final String name, final String oid, final String algorithm, final boolean isFips, final ProtocolVersion[] supportedProtocols, final AlgorithmParameterSpec keAlgParamSpec) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.oid = oid;
            this.algorithm = algorithm;
            this.isFips = isFips;
            this.supportedProtocols = supportedProtocols;
            this.keAlgParamSpec = keAlgParamSpec;
            boolean mediator = keAlgParamSpec != null;
            if (mediator && type == NamedGroupType.NAMED_GROUP_ECDHE) {
                mediator = JsseJce.isEcAvailable();
            }
            Label_0230: {
                if (mediator) {
                    try {
                        final AlgorithmParameters algParams = AlgorithmParameters.getInstance(type.algorithm);
                        algParams.init(keAlgParamSpec);
                    }
                    catch (final InvalidParameterSpecException | NoSuchAlgorithmException exp) {
                        if (type != NamedGroupType.NAMED_GROUP_XDH) {
                            mediator = false;
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                                SSLLogger.warning("No AlgorithmParameters for " + name, exp);
                            }
                        }
                        else {
                            try {
                                KeyAgreement.getInstance(name);
                            }
                            catch (final NoSuchAlgorithmException nsae) {
                                mediator = false;
                                if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                                    break Label_0230;
                                }
                                SSLLogger.warning("No AlgorithmParameters for " + name, nsae);
                            }
                        }
                    }
                }
            }
            this.isAvailable = mediator;
        }
        
        private NamedGroup(final int id, final String name, final String oid, final boolean isFips, final ProtocolVersion[] supportedProtocols) {
            this(id, NamedGroupType.NAMED_GROUP_ECDHE, name, oid, "EC", isFips, supportedProtocols, CurveDB.lookup(name));
        }
        
        private NamedGroup(final int id, final String name, final boolean isFips, final String algorithm, final ProtocolVersion[] supportedProtocols, final AlgorithmParameterSpec keAlgParamSpec) {
            this(id, NamedGroupType.NAMED_GROUP_XDH, name, null, algorithm, isFips, supportedProtocols, keAlgParamSpec);
        }
        
        private NamedGroup(final int id, final String name, final boolean isFips, final ProtocolVersion[] supportedProtocols, final AlgorithmParameterSpec keAlgParamSpec) {
            this(id, NamedGroupType.NAMED_GROUP_FFDHE, name, null, "DiffieHellman", isFips, supportedProtocols, keAlgParamSpec);
        }
        
        private NamedGroup(final int id, final String name, final ProtocolVersion[] supportedProtocols) {
            this(id, NamedGroupType.NAMED_GROUP_ARBITRARY, name, null, "EC", false, supportedProtocols, null);
        }
        
        static NamedGroup valueOf(final int id) {
            for (final NamedGroup group : values()) {
                if (group.id == id) {
                    return group;
                }
            }
            return null;
        }
        
        static NamedGroup valueOf(final ECParameterSpec params) {
            for (final NamedGroup ng : values()) {
                if (ng.type == NamedGroupType.NAMED_GROUP_ECDHE && (params == ng.keAlgParamSpec || ng.keAlgParamSpec == CurveDB.lookup(params))) {
                    return ng;
                }
            }
            return null;
        }
        
        static NamedGroup valueOf(final DHParameterSpec params) {
            for (final NamedGroup ng : values()) {
                if (ng.type == NamedGroupType.NAMED_GROUP_FFDHE) {
                    final DHParameterSpec ngParams = (DHParameterSpec)ng.keAlgParamSpec;
                    if (ngParams.getP().equals(params.getP()) && ngParams.getG().equals(params.getG())) {
                        return ng;
                    }
                }
            }
            return null;
        }
        
        static NamedGroup nameOf(final String name) {
            for (final NamedGroup group : values()) {
                if (group.name.equals(name)) {
                    return group;
                }
            }
            return null;
        }
        
        static String nameOf(final int id) {
            for (final NamedGroup group : values()) {
                if (group.id == id) {
                    return group.name;
                }
            }
            return "UNDEFINED-NAMED-GROUP(" + id + ")";
        }
        
        boolean isAvailable(final List<ProtocolVersion> protocolVersions) {
            if (this.isAvailable) {
                for (final ProtocolVersion pv : this.supportedProtocols) {
                    if (protocolVersions.contains(pv)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        boolean isAvailable(final ProtocolVersion protocolVersion) {
            if (this.isAvailable) {
                for (final ProtocolVersion pv : this.supportedProtocols) {
                    if (protocolVersion == pv) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        boolean isSupported(final List<CipherSuite> cipherSuites) {
            for (final CipherSuite cs : cipherSuites) {
                final boolean isMatch = this.isAvailable(cs.supportedProtocols);
                if (isMatch && (cs.keyExchange == null || cs.keyExchange.groupType == this.type)) {
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
    }
    
    static class SupportedGroups
    {
        static final boolean enableFFDHE;
        static final Map<NamedGroup, AlgorithmParameters> namedGroupParams;
        static final NamedGroup[] supportedNamedGroups;
        
        private static boolean isAvailableGroup(final NamedGroup namedGroup) {
            AlgorithmParameters params = null;
            AlgorithmParameterSpec spec = null;
            Label_0072: {
                if (namedGroup.type == NamedGroupType.NAMED_GROUP_ECDHE) {
                    if (namedGroup.oid == null) {
                        break Label_0072;
                    }
                    try {
                        params = JsseJce.getAlgorithmParameters("EC");
                        spec = new ECGenParameterSpec(namedGroup.oid);
                        break Label_0072;
                    }
                    catch (final NoSuchAlgorithmException e) {
                        return false;
                    }
                }
                if (namedGroup.type == NamedGroupType.NAMED_GROUP_FFDHE) {
                    try {
                        params = JsseJce.getAlgorithmParameters("DiffieHellman");
                        spec = getFFDHEDHParameterSpec(namedGroup);
                    }
                    catch (final NoSuchAlgorithmException e) {
                        return false;
                    }
                }
            }
            if (params != null && spec != null) {
                try {
                    params.init(spec);
                }
                catch (final InvalidParameterSpecException e2) {
                    return false;
                }
                SupportedGroups.namedGroupParams.put(namedGroup, params);
                return true;
            }
            return false;
        }
        
        private static DHParameterSpec getFFDHEDHParameterSpec(final NamedGroup namedGroup) {
            DHParameterSpec spec = null;
            switch (namedGroup) {
                case FFDHE_2048: {
                    spec = PredefinedDHParameterSpecs.ffdheParams.get(2048);
                    break;
                }
                case FFDHE_3072: {
                    spec = PredefinedDHParameterSpecs.ffdheParams.get(3072);
                    break;
                }
                case FFDHE_4096: {
                    spec = PredefinedDHParameterSpecs.ffdheParams.get(4096);
                    break;
                }
                case FFDHE_6144: {
                    spec = PredefinedDHParameterSpecs.ffdheParams.get(6144);
                    break;
                }
                case FFDHE_8192: {
                    spec = PredefinedDHParameterSpecs.ffdheParams.get(8192);
                    break;
                }
            }
            return spec;
        }
        
        private static DHParameterSpec getPredefinedDHParameterSpec(final NamedGroup namedGroup) {
            DHParameterSpec spec = null;
            switch (namedGroup) {
                case FFDHE_2048: {
                    spec = PredefinedDHParameterSpecs.definedParams.get(2048);
                    break;
                }
                case FFDHE_3072: {
                    spec = PredefinedDHParameterSpecs.definedParams.get(3072);
                    break;
                }
                case FFDHE_4096: {
                    spec = PredefinedDHParameterSpecs.definedParams.get(4096);
                    break;
                }
                case FFDHE_6144: {
                    spec = PredefinedDHParameterSpecs.definedParams.get(6144);
                    break;
                }
                case FFDHE_8192: {
                    spec = PredefinedDHParameterSpecs.definedParams.get(8192);
                    break;
                }
            }
            return spec;
        }
        
        static ECGenParameterSpec getECGenParamSpec(final NamedGroup namedGroup) {
            if (namedGroup.type != NamedGroupType.NAMED_GROUP_ECDHE) {
                throw new RuntimeException("Not a named EC group: " + namedGroup);
            }
            final AlgorithmParameters params = SupportedGroups.namedGroupParams.get(namedGroup);
            if (params == null) {
                throw new RuntimeException("Not a supported EC named group: " + namedGroup);
            }
            try {
                return params.getParameterSpec(ECGenParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ipse) {
                return new ECGenParameterSpec(namedGroup.oid);
            }
        }
        
        static DHParameterSpec getDHParameterSpec(final NamedGroup namedGroup) {
            if (namedGroup.type != NamedGroupType.NAMED_GROUP_FFDHE) {
                throw new RuntimeException("Not a named DH group: " + namedGroup);
            }
            final AlgorithmParameters params = SupportedGroups.namedGroupParams.get(namedGroup);
            if (params == null) {
                throw new RuntimeException("Not a supported DH named group: " + namedGroup);
            }
            try {
                return params.getParameterSpec(DHParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ipse) {
                return getPredefinedDHParameterSpec(namedGroup);
            }
        }
        
        static boolean isActivatable(final AlgorithmConstraints constraints, final NamedGroupType type) {
            boolean hasFFDHEGroups = false;
            for (final NamedGroup namedGroup : SupportedGroups.supportedNamedGroups) {
                if (namedGroup.type == type) {
                    if (constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), namedGroup.algorithm, SupportedGroups.namedGroupParams.get(namedGroup))) {
                        return true;
                    }
                    if (!hasFFDHEGroups && type == NamedGroupType.NAMED_GROUP_FFDHE) {
                        hasFFDHEGroups = true;
                    }
                }
            }
            return !hasFFDHEGroups && type == NamedGroupType.NAMED_GROUP_FFDHE;
        }
        
        static boolean isActivatable(final AlgorithmConstraints constraints, final NamedGroup namedGroup) {
            return isSupported(namedGroup) && constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), namedGroup.algorithm, SupportedGroups.namedGroupParams.get(namedGroup));
        }
        
        static boolean isSupported(final NamedGroup namedGroup) {
            for (final NamedGroup group : SupportedGroups.supportedNamedGroups) {
                if (namedGroup.id == group.id) {
                    return true;
                }
            }
            return false;
        }
        
        static NamedGroup getPreferredGroup(final ProtocolVersion negotiatedProtocol, final AlgorithmConstraints constraints, final NamedGroupType type, final List<NamedGroup> requestedNamedGroups) {
            for (final NamedGroup namedGroup : requestedNamedGroups) {
                if (namedGroup.type == type && namedGroup.isAvailable(negotiatedProtocol) && isSupported(namedGroup) && constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), namedGroup.algorithm, SupportedGroups.namedGroupParams.get(namedGroup))) {
                    return namedGroup;
                }
            }
            return null;
        }
        
        static NamedGroup getPreferredGroup(final ProtocolVersion negotiatedProtocol, final AlgorithmConstraints constraints, final NamedGroupType type) {
            for (final NamedGroup namedGroup : SupportedGroups.supportedNamedGroups) {
                if (namedGroup.type == type && namedGroup.isAvailable(negotiatedProtocol) && constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), namedGroup.algorithm, SupportedGroups.namedGroupParams.get(namedGroup))) {
                    return namedGroup;
                }
            }
            return null;
        }
        
        static {
            enableFFDHE = Utilities.getBooleanProperty("jsse.enableFFDHE", true);
            namedGroupParams = new HashMap<NamedGroup, AlgorithmParameters>();
            final boolean requireFips = OpenJSSE.isFIPS();
            String property = GetPropertyAction.privilegedGetProperty("jdk.tls.namedGroups");
            if (property != null && property.length() != 0 && property.length() > 1 && property.charAt(0) == '\"' && property.charAt(property.length() - 1) == '\"') {
                property = property.substring(1, property.length() - 1);
            }
            ArrayList<NamedGroup> groupList;
            if (property != null && property.length() != 0) {
                final String[] groups = property.split(",");
                groupList = new ArrayList<NamedGroup>(groups.length);
                for (String group : groups) {
                    group = group.trim();
                    if (!group.isEmpty()) {
                        final NamedGroup namedGroup = NamedGroup.nameOf(group);
                        if (namedGroup != null && (!requireFips || namedGroup.isFips) && isAvailableGroup(namedGroup)) {
                            groupList.add(namedGroup);
                        }
                    }
                }
                if (groupList.isEmpty()) {
                    throw new IllegalArgumentException("System property jdk.tls.namedGroups(" + property + ") contains no supported named groups");
                }
            }
            else {
                NamedGroup[] groups2;
                if (requireFips) {
                    groups2 = new NamedGroup[] { NamedGroup.SECP256_R1, NamedGroup.SECP384_R1, NamedGroup.SECP521_R1, NamedGroup.FFDHE_2048, NamedGroup.FFDHE_3072, NamedGroup.FFDHE_4096, NamedGroup.FFDHE_6144, NamedGroup.FFDHE_8192 };
                }
                else {
                    groups2 = new NamedGroup[] { NamedGroup.SECP256_R1, NamedGroup.SECP384_R1, NamedGroup.SECP521_R1, NamedGroup.FFDHE_2048, NamedGroup.FFDHE_3072, NamedGroup.FFDHE_4096, NamedGroup.FFDHE_6144, NamedGroup.FFDHE_8192 };
                }
                groupList = new ArrayList<NamedGroup>(groups2.length);
                for (final NamedGroup group2 : groups2) {
                    if (isAvailableGroup(group2)) {
                        groupList.add(group2);
                    }
                }
                if (groupList.isEmpty() && SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("No default named groups", new Object[0]);
                }
            }
            supportedNamedGroups = new NamedGroup[groupList.size()];
            int i = 0;
            for (final NamedGroup namedGroup2 : groupList) {
                SupportedGroups.supportedNamedGroups[i++] = namedGroup2;
            }
        }
    }
    
    private static final class CHSupportedGroupsProducer extends SupportedGroups implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return null;
            }
            final ArrayList<NamedGroup> namedGroups = new ArrayList<NamedGroup>(SupportedGroups.supportedNamedGroups.length);
            for (final NamedGroup ng : SupportedGroups.supportedNamedGroups) {
                if (SupportedGroups.enableFFDHE || ng.type != NamedGroupType.NAMED_GROUP_FFDHE) {
                    if (ng.isAvailable(chc.activeProtocols) && ng.isSupported(chc.activeCipherSuites) && chc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), ng.algorithm, CHSupportedGroupsProducer.namedGroupParams.get(ng))) {
                        namedGroups.add(ng);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Ignore inactive or disabled named group: " + ng.name, new Object[0]);
                    }
                }
            }
            if (namedGroups.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("no available named group", new Object[0]);
                }
                return null;
            }
            final int vectorLen = namedGroups.size() << 1;
            final byte[] extData = new byte[vectorLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, vectorLen);
            for (final NamedGroup namedGroup : namedGroups) {
                Record.putInt16(m, namedGroup.id);
            }
            chc.clientRequestedNamedGroups = Collections.unmodifiableList((List<? extends NamedGroup>)namedGroups);
            chc.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_GROUPS, new SupportedGroupsSpec((List)namedGroups));
            return extData;
        }
    }
    
    private static final class CHSupportedGroupsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return;
            }
            SupportedGroupsSpec spec;
            try {
                spec = new SupportedGroupsSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            final List<NamedGroup> knownNamedGroups = new LinkedList<NamedGroup>();
            for (final int id : spec.namedGroupsIds) {
                final NamedGroup ng = NamedGroup.valueOf(id);
                if (ng != null) {
                    knownNamedGroups.add(ng);
                }
            }
            shc.clientRequestedNamedGroups = knownNamedGroups;
            shc.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_GROUPS, spec);
        }
    }
    
    private static final class CHSupportedGroupsOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.negotiatedProtocol.useTLS13PlusSpec() && shc.handshakeExtensions.containsKey(SSLExtension.CH_KEY_SHARE)) {
                throw shc.conContext.fatal(Alert.MISSING_EXTENSION, "No supported_groups extension to work with the key_share extension");
            }
        }
    }
    
    private static final class EESupportedGroupsProducer extends SupportedGroups implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.EE_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return null;
            }
            final ArrayList<NamedGroup> namedGroups = new ArrayList<NamedGroup>(SupportedGroups.supportedNamedGroups.length);
            for (final NamedGroup ng : SupportedGroups.supportedNamedGroups) {
                if (SupportedGroups.enableFFDHE || ng.type != NamedGroupType.NAMED_GROUP_FFDHE) {
                    if (ng.isAvailable(shc.activeProtocols) && ng.isSupported(shc.activeCipherSuites) && shc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), ng.algorithm, EESupportedGroupsProducer.namedGroupParams.get(ng))) {
                        namedGroups.add(ng);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Ignore inactive or disabled named group: " + ng.name, new Object[0]);
                    }
                }
            }
            if (namedGroups.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("no available named group", new Object[0]);
                }
                return null;
            }
            final int vectorLen = namedGroups.size() << 1;
            final byte[] extData = new byte[vectorLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, vectorLen);
            for (final NamedGroup namedGroup : namedGroups) {
                Record.putInt16(m, namedGroup.id);
            }
            shc.conContext.serverRequestedNamedGroups = Collections.unmodifiableList((List<? extends NamedGroup>)namedGroups);
            final SupportedGroupsSpec spec = new SupportedGroupsSpec((List)namedGroups);
            shc.handshakeExtensions.put(SSLExtension.EE_SUPPORTED_GROUPS, spec);
            return extData;
        }
    }
    
    private static final class EESupportedGroupsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.EE_SUPPORTED_GROUPS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable supported_groups extension", new Object[0]);
                }
                return;
            }
            SupportedGroupsSpec spec;
            try {
                spec = new SupportedGroupsSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            final List<NamedGroup> knownNamedGroups = new ArrayList<NamedGroup>(spec.namedGroupsIds.length);
            for (final int id : spec.namedGroupsIds) {
                final NamedGroup ng = NamedGroup.valueOf(id);
                if (ng != null) {
                    knownNamedGroups.add(ng);
                }
            }
            chc.conContext.serverRequestedNamedGroups = knownNamedGroups;
            chc.handshakeExtensions.put(SSLExtension.EE_SUPPORTED_GROUPS, spec);
        }
    }
    
    static class NamedParameterSpec implements AlgorithmParameterSpec
    {
        public static final NamedParameterSpec X25519;
        public static final NamedParameterSpec X448;
        private String name;
        
        public NamedParameterSpec(final String stdName) {
            this.name = stdName;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            X25519 = new NamedParameterSpec("X25519");
            X448 = new NamedParameterSpec("X448");
        }
    }
}
