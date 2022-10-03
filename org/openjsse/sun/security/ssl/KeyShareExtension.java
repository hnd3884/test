package org.openjsse.sun.security.ssl;

import java.util.Map;
import java.util.Collection;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedList;
import javax.net.ssl.SSLProtocolException;
import java.util.List;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import java.nio.ByteBuffer;

final class KeyShareExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnTradAbsence;
    static final SSLStringizer chStringizer;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final HandshakeAbsence shOnLoadAbsence;
    static final SSLStringizer shStringizer;
    static final HandshakeProducer hrrNetworkProducer;
    static final SSLExtension.ExtensionConsumer hrrOnLoadConsumer;
    static final HandshakeProducer hrrNetworkReproducer;
    static final SSLStringizer hrrStringizer;
    
    static {
        chNetworkProducer = new CHKeyShareProducer();
        chOnLoadConsumer = new CHKeyShareConsumer();
        chOnTradAbsence = new CHKeyShareOnTradeAbsence();
        chStringizer = new CHKeyShareStringizer();
        shNetworkProducer = new SHKeyShareProducer();
        shOnLoadConsumer = new SHKeyShareConsumer();
        shOnLoadAbsence = new SHKeyShareAbsence();
        shStringizer = new SHKeyShareStringizer();
        hrrNetworkProducer = new HRRKeyShareProducer();
        hrrOnLoadConsumer = new HRRKeyShareConsumer();
        hrrNetworkReproducer = new HRRKeyShareReproducer();
        hrrStringizer = new HRRKeyShareStringizer();
    }
    
    private static final class KeyShareEntry
    {
        final int namedGroupId;
        final byte[] keyExchange;
        
        private KeyShareEntry(final int namedGroupId, final byte[] keyExchange) {
            this.namedGroupId = namedGroupId;
            this.keyExchange = keyExchange;
        }
        
        private byte[] getEncoded() {
            final byte[] buffer = new byte[this.keyExchange.length + 4];
            final ByteBuffer m = ByteBuffer.wrap(buffer);
            try {
                Record.putInt16(m, this.namedGroupId);
                Record.putBytes16(m, this.keyExchange);
            }
            catch (final IOException ioe) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Unlikely IOException", ioe);
                }
            }
            return buffer;
        }
        
        private int getEncodedSize() {
            return this.keyExchange.length + 4;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\n'{'\n  \"named group\": {0}\n  \"key_exchange\": '{'\n{1}\n  '}'\n'}',", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { SupportedGroupsExtension.NamedGroup.nameOf(this.namedGroupId), Utilities.indent(hexEncoder.encode(this.keyExchange), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    static final class CHKeyShareSpec implements SSLExtension.SSLExtensionSpec
    {
        final List<KeyShareEntry> clientShares;
        
        private CHKeyShareSpec(final List<KeyShareEntry> clientShares) {
            this.clientShares = clientShares;
        }
        
        private CHKeyShareSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid key_share extension: insufficient data (length=" + buffer.remaining() + ")");
            }
            final int listLen = Record.getInt16(buffer);
            if (listLen != buffer.remaining()) {
                throw new SSLProtocolException("Invalid key_share extension: incorrect list length (length=" + listLen + ")");
            }
            final List<KeyShareEntry> keyShares = new LinkedList<KeyShareEntry>();
            while (buffer.hasRemaining()) {
                final int namedGroupId = Record.getInt16(buffer);
                final byte[] keyExchange = Record.getBytes16(buffer);
                if (keyExchange.length == 0) {
                    throw new SSLProtocolException("Invalid key_share extension: empty key_exchange");
                }
                keyShares.add(new KeyShareEntry(namedGroupId, keyExchange));
            }
            this.clientShares = Collections.unmodifiableList((List<? extends KeyShareEntry>)keyShares);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"client_shares\": '['{0}\n']'", Locale.ENGLISH);
            final StringBuilder builder = new StringBuilder(512);
            for (final KeyShareEntry entry : this.clientShares) {
                builder.append(entry.toString());
            }
            final Object[] messageFields = { Utilities.indent(builder.toString()) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class CHKeyShareStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CHKeyShareSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHKeyShareProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable key_share extension", new Object[0]);
                }
                return null;
            }
            List<SupportedGroupsExtension.NamedGroup> namedGroups;
            if (chc.serverSelectedNamedGroup != null) {
                namedGroups = Arrays.asList(chc.serverSelectedNamedGroup);
            }
            else {
                namedGroups = chc.clientRequestedNamedGroups;
                if (namedGroups == null || namedGroups.isEmpty()) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Ignore key_share extension, no supported groups", new Object[0]);
                    }
                    return null;
                }
            }
            final List<KeyShareEntry> keyShares = new LinkedList<KeyShareEntry>();
            for (final SupportedGroupsExtension.NamedGroup ng : namedGroups) {
                final SSLKeyExchange ke = SSLKeyExchange.valueOf(ng);
                if (ke == null) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.warning("No key exchange for named group " + ng.name, new Object[0]);
                }
                else {
                    final SSLPossession[] possessions;
                    final SSLPossession[] poses = possessions = ke.createPossessions(chc);
                    for (final SSLPossession pos : possessions) {
                        chc.handshakePossessions.add(pos);
                        if (pos instanceof ECDHKeyExchange.ECDHEPossession || pos instanceof DHKeyExchange.DHEPossession) {
                            keyShares.add(new KeyShareEntry(ng.id, pos.encode()));
                        }
                    }
                    if (!keyShares.isEmpty()) {
                        break;
                    }
                    continue;
                }
            }
            int listLen = 0;
            for (final KeyShareEntry entry : keyShares) {
                listLen += entry.getEncodedSize();
            }
            final byte[] extData = new byte[listLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, listLen);
            for (final KeyShareEntry entry2 : keyShares) {
                m.put(entry2.getEncoded());
            }
            chc.handshakeExtensions.put(SSLExtension.CH_KEY_SHARE, new CHKeyShareSpec((List)keyShares));
            return extData;
        }
    }
    
    private static final class CHKeyShareConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.handshakeExtensions.containsKey(SSLExtension.CH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("The key_share extension has been loaded", new Object[0]);
                }
                return;
            }
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable key_share extension", new Object[0]);
                }
                return;
            }
            CHKeyShareSpec spec;
            try {
                spec = new CHKeyShareSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            final List<SSLCredentials> credentials = new LinkedList<SSLCredentials>();
            for (final KeyShareEntry entry : spec.clientShares) {
                final SupportedGroupsExtension.NamedGroup ng = SupportedGroupsExtension.NamedGroup.valueOf(entry.namedGroupId);
                if (ng == null || !SupportedGroupsExtension.SupportedGroups.isActivatable(shc.algorithmConstraints, ng)) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.fine("Ignore unsupported named group: " + SupportedGroupsExtension.NamedGroup.nameOf(entry.namedGroupId), new Object[0]);
                }
                else if (ng.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                    try {
                        final ECDHKeyExchange.ECDHECredentials ecdhec = ECDHKeyExchange.ECDHECredentials.valueOf(ng, entry.keyExchange);
                        if (ecdhec == null) {
                            continue;
                        }
                        if (!shc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), ecdhec.popPublicKey)) {
                            SSLLogger.warning("ECDHE key share entry does not comply to algorithm constraints", new Object[0]);
                        }
                        else {
                            credentials.add(ecdhec);
                        }
                    }
                    catch (final IOException | GeneralSecurityException ex) {
                        SSLLogger.warning("Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(entry.namedGroupId), new Object[0]);
                    }
                }
                else {
                    if (ng.type != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                        continue;
                    }
                    try {
                        final DHKeyExchange.DHECredentials dhec = DHKeyExchange.DHECredentials.valueOf(ng, entry.keyExchange);
                        if (dhec == null) {
                            continue;
                        }
                        if (!shc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), dhec.popPublicKey)) {
                            SSLLogger.warning("DHE key share entry does not comply to algorithm constraints", new Object[0]);
                        }
                        else {
                            credentials.add(dhec);
                        }
                    }
                    catch (final IOException | GeneralSecurityException ex) {
                        SSLLogger.warning("Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(entry.namedGroupId), new Object[0]);
                    }
                }
            }
            if (!credentials.isEmpty()) {
                shc.handshakeCredentials.addAll(credentials);
            }
            else {
                shc.handshakeProducers.put(SSLHandshake.HELLO_RETRY_REQUEST.id, SSLHandshake.HELLO_RETRY_REQUEST);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_KEY_SHARE, spec);
        }
    }
    
    private static final class CHKeyShareOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.negotiatedProtocol.useTLS13PlusSpec() && shc.handshakeExtensions.containsKey(SSLExtension.CH_SUPPORTED_GROUPS)) {
                throw shc.conContext.fatal(Alert.MISSING_EXTENSION, "No key_share extension to work with the supported_groups extension");
            }
        }
    }
    
    static final class SHKeyShareSpec implements SSLExtension.SSLExtensionSpec
    {
        final KeyShareEntry serverShare;
        
        SHKeyShareSpec(final KeyShareEntry serverShare) {
            this.serverShare = serverShare;
        }
        
        private SHKeyShareSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() < 5) {
                throw new SSLProtocolException("Invalid key_share extension: insufficient data (length=" + buffer.remaining() + ")");
            }
            final int namedGroupId = Record.getInt16(buffer);
            final byte[] keyExchange = Record.getBytes16(buffer);
            if (buffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid key_share extension: unknown extra data");
            }
            this.serverShare = new KeyShareEntry(namedGroupId, keyExchange);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"server_share\": '{'\n  \"named group\": {0}\n  \"key_exchange\": '{'\n{1}\n  '}'\n'}',", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { SupportedGroupsExtension.NamedGroup.nameOf(this.serverShare.namedGroupId), Utilities.indent(hexEncoder.encode(this.serverShare.keyExchange), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class SHKeyShareStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new SHKeyShareSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class SHKeyShareProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final CHKeyShareSpec kss = shc.handshakeExtensions.get(SSLExtension.CH_KEY_SHARE);
            if (kss == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no client key_share extension", new Object[0]);
                }
                return null;
            }
            if (!shc.sslConfig.isAvailable(SSLExtension.SH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no available server key_share extension", new Object[0]);
                }
                return null;
            }
            if (shc.handshakeCredentials == null || shc.handshakeCredentials.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client key share entries", new Object[0]);
                }
                return null;
            }
            KeyShareEntry keyShare = null;
            for (final SSLCredentials cd : shc.handshakeCredentials) {
                SupportedGroupsExtension.NamedGroup ng = null;
                if (cd instanceof ECDHKeyExchange.ECDHECredentials) {
                    ng = ((ECDHKeyExchange.ECDHECredentials)cd).namedGroup;
                }
                else if (cd instanceof DHKeyExchange.DHECredentials) {
                    ng = ((DHKeyExchange.DHECredentials)cd).namedGroup;
                }
                if (ng == null) {
                    continue;
                }
                final SSLKeyExchange ke = SSLKeyExchange.valueOf(ng);
                if (ke == null) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.warning("No key exchange for named group " + ng.name, new Object[0]);
                }
                else {
                    final SSLPossession[] possessions;
                    final SSLPossession[] poses = possessions = ke.createPossessions(shc);
                    for (final SSLPossession pos : possessions) {
                        if (pos instanceof ECDHKeyExchange.ECDHEPossession || pos instanceof DHKeyExchange.DHEPossession) {
                            shc.handshakeKeyExchange = ke;
                            shc.handshakePossessions.add(pos);
                            keyShare = new KeyShareEntry(ng.id, pos.encode());
                            break;
                        }
                    }
                    if (keyShare != null) {
                        for (final Map.Entry<Byte, HandshakeProducer> me : ke.getHandshakeProducers(shc)) {
                            shc.handshakeProducers.put(me.getKey(), me.getValue());
                        }
                        break;
                    }
                    continue;
                }
            }
            if (keyShare == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available server key_share extension", new Object[0]);
                }
                return null;
            }
            final byte[] extData = keyShare.getEncoded();
            final SHKeyShareSpec spec = new SHKeyShareSpec(keyShare);
            shc.handshakeExtensions.put(SSLExtension.SH_KEY_SHARE, spec);
            return extData;
        }
    }
    
    private static final class SHKeyShareConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (chc.clientRequestedNamedGroups == null || chc.clientRequestedNamedGroups.isEmpty()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected key_share extension in ServerHello");
            }
            if (!chc.sslConfig.isAvailable(SSLExtension.SH_KEY_SHARE)) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in ServerHello");
            }
            SHKeyShareSpec spec;
            try {
                spec = new SHKeyShareSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            final KeyShareEntry keyShare = spec.serverShare;
            final SupportedGroupsExtension.NamedGroup ng = SupportedGroupsExtension.NamedGroup.valueOf(keyShare.namedGroupId);
            if (ng == null || !SupportedGroupsExtension.SupportedGroups.isActivatable(chc.algorithmConstraints, ng)) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported named group: " + SupportedGroupsExtension.NamedGroup.nameOf(keyShare.namedGroupId));
            }
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(ng);
            if (ke == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No key exchange for named group " + ng.name);
            }
            SSLCredentials credentials = null;
            Label_0486: {
                if (ng.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                    try {
                        final ECDHKeyExchange.ECDHECredentials ecdhec = ECDHKeyExchange.ECDHECredentials.valueOf(ng, keyShare.keyExchange);
                        if (ecdhec != null) {
                            if (!chc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), ecdhec.popPublicKey)) {
                                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "ECDHE key share entry does not comply to algorithm constraints");
                            }
                            credentials = ecdhec;
                        }
                        break Label_0486;
                    }
                    catch (final IOException | GeneralSecurityException ex) {
                        throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(keyShare.namedGroupId));
                    }
                }
                if (ng.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                    try {
                        final DHKeyExchange.DHECredentials dhec = DHKeyExchange.DHECredentials.valueOf(ng, keyShare.keyExchange);
                        if (dhec != null) {
                            if (!chc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), dhec.popPublicKey)) {
                                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "DHE key share entry does not comply to algorithm constraints");
                            }
                            credentials = dhec;
                        }
                        break Label_0486;
                    }
                    catch (final IOException | GeneralSecurityException ex) {
                        throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(keyShare.namedGroupId));
                    }
                }
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported named group: " + SupportedGroupsExtension.NamedGroup.nameOf(keyShare.namedGroupId));
            }
            if (credentials == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported named group: " + ng.name);
            }
            chc.handshakeKeyExchange = ke;
            chc.handshakeCredentials.add(credentials);
            chc.handshakeExtensions.put(SSLExtension.SH_KEY_SHARE, spec);
        }
    }
    
    private static final class SHKeyShareAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                SSLLogger.fine("No key_share extension in ServerHello, cleanup the key shares if necessary", new Object[0]);
            }
            chc.handshakePossessions.clear();
        }
    }
    
    static final class HRRKeyShareSpec implements SSLExtension.SSLExtensionSpec
    {
        final int selectedGroup;
        
        HRRKeyShareSpec(final SupportedGroupsExtension.NamedGroup serverGroup) {
            this.selectedGroup = serverGroup.id;
        }
        
        private HRRKeyShareSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() != 2) {
                throw new SSLProtocolException("Invalid key_share extension: improper data (length=" + buffer.remaining() + ")");
            }
            this.selectedGroup = Record.getInt16(buffer);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"selected group\": '['{0}']'", Locale.ENGLISH);
            final Object[] messageFields = { SupportedGroupsExtension.NamedGroup.nameOf(this.selectedGroup) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class HRRKeyShareStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new HRRKeyShareSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class HRRKeyShareProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.HRR_KEY_SHARE)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in HelloRetryRequest");
            }
            if (shc.clientRequestedNamedGroups == null || shc.clientRequestedNamedGroups.isEmpty()) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected key_share extension in HelloRetryRequest");
            }
            SupportedGroupsExtension.NamedGroup selectedGroup = null;
            for (final SupportedGroupsExtension.NamedGroup ng : shc.clientRequestedNamedGroups) {
                if (SupportedGroupsExtension.SupportedGroups.isActivatable(shc.algorithmConstraints, ng)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("HelloRetryRequest selected named group: " + ng.name, new Object[0]);
                    }
                    selectedGroup = ng;
                    break;
                }
            }
            if (selectedGroup == null) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No common named group");
            }
            final byte[] extdata = { (byte)(selectedGroup.id >> 8 & 0xFF), (byte)(selectedGroup.id & 0xFF) };
            shc.serverSelectedNamedGroup = selectedGroup;
            shc.handshakeExtensions.put(SSLExtension.HRR_KEY_SHARE, new HRRKeyShareSpec(selectedGroup));
            return extdata;
        }
    }
    
    private static final class HRRKeyShareReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.HRR_KEY_SHARE)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in HelloRetryRequest");
            }
            final CHKeyShareSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_KEY_SHARE);
            if (spec != null && spec.clientShares != null && spec.clientShares.size() == 1) {
                final int namedGroupId = spec.clientShares.get(0).namedGroupId;
                final byte[] extdata = { (byte)(namedGroupId >> 8 & 0xFF), (byte)(namedGroupId & 0xFF) };
                return extdata;
            }
            return null;
        }
    }
    
    private static final class HRRKeyShareConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.HRR_KEY_SHARE)) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in HelloRetryRequest");
            }
            if (chc.clientRequestedNamedGroups == null || chc.clientRequestedNamedGroups.isEmpty()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected key_share extension in HelloRetryRequest");
            }
            HRRKeyShareSpec spec;
            try {
                spec = new HRRKeyShareSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            final SupportedGroupsExtension.NamedGroup serverGroup = SupportedGroupsExtension.NamedGroup.valueOf(spec.selectedGroup);
            if (serverGroup == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported HelloRetryRequest selected group: " + SupportedGroupsExtension.NamedGroup.nameOf(spec.selectedGroup));
            }
            if (!chc.clientRequestedNamedGroups.contains(serverGroup)) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected HelloRetryRequest selected group: " + serverGroup.name);
            }
            chc.serverSelectedNamedGroup = serverGroup;
            chc.handshakeExtensions.put(SSLExtension.HRR_KEY_SHARE, spec);
        }
    }
}
