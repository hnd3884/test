package sun.security.ssl;

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
import sun.misc.HexDumpEncoder;
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
            final byte[] array = new byte[this.keyExchange.length + 4];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            try {
                Record.putInt16(wrap, this.namedGroupId);
                Record.putBytes16(wrap, this.keyExchange);
            }
            catch (final IOException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Unlikely IOException", ex);
                }
            }
            return array;
        }
        
        private int getEncodedSize() {
            return this.keyExchange.length + 4;
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\n'{'\n  \"named group\": {0}\n  \"key_exchange\": '{'\n{1}\n  '}'\n'}',", Locale.ENGLISH).format(new Object[] { SupportedGroupsExtension.NamedGroup.nameOf(this.namedGroupId), Utilities.indent(new HexDumpEncoder().encode(this.keyExchange), "    ") });
        }
    }
    
    static final class CHKeyShareSpec implements SSLExtension.SSLExtensionSpec
    {
        final List<KeyShareEntry> clientShares;
        
        private CHKeyShareSpec(final List<KeyShareEntry> clientShares) {
            this.clientShares = clientShares;
        }
        
        private CHKeyShareSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid key_share extension: insufficient data (length=" + byteBuffer.remaining() + ")");
            }
            final int int16 = Record.getInt16(byteBuffer);
            if (int16 != byteBuffer.remaining()) {
                throw new SSLProtocolException("Invalid key_share extension: incorrect list length (length=" + int16 + ")");
            }
            final LinkedList list = new LinkedList();
            while (byteBuffer.hasRemaining()) {
                final int int17 = Record.getInt16(byteBuffer);
                final byte[] bytes16 = Record.getBytes16(byteBuffer);
                if (bytes16.length == 0) {
                    throw new SSLProtocolException("Invalid key_share extension: empty key_exchange");
                }
                list.add(new KeyShareEntry(int17, bytes16));
            }
            this.clientShares = (List<KeyShareEntry>)Collections.unmodifiableList((List<?>)list);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"client_shares\": '['{0}\n']'", Locale.ENGLISH);
            final StringBuilder sb = new StringBuilder(512);
            final Iterator<KeyShareEntry> iterator = this.clientShares.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().toString());
            }
            return messageFormat.format(new Object[] { Utilities.indent(sb.toString()) });
        }
    }
    
    private static final class CHKeyShareStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CHKeyShareSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHKeyShareProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable key_share extension", new Object[0]);
                }
                return null;
            }
            List<SupportedGroupsExtension.NamedGroup> list;
            if (clientHandshakeContext.serverSelectedNamedGroup != null) {
                list = Arrays.asList(clientHandshakeContext.serverSelectedNamedGroup);
            }
            else {
                list = clientHandshakeContext.clientRequestedNamedGroups;
                if (list == null || list.isEmpty()) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Ignore key_share extension, no supported groups", new Object[0]);
                    }
                    return null;
                }
            }
            final LinkedList list2 = new LinkedList();
            for (final SupportedGroupsExtension.NamedGroup namedGroup : list) {
                final SSLKeyExchange value = SSLKeyExchange.valueOf(namedGroup);
                if (value == null) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.warning("No key exchange for named group " + namedGroup.name, new Object[0]);
                }
                else {
                    for (final SSLPossession sslPossession : value.createPossessions(clientHandshakeContext)) {
                        clientHandshakeContext.handshakePossessions.add(sslPossession);
                        if (sslPossession instanceof ECDHKeyExchange.ECDHEPossession || sslPossession instanceof DHKeyExchange.DHEPossession) {
                            list2.add(new KeyShareEntry(namedGroup.id, sslPossession.encode()));
                        }
                    }
                    if (!list2.isEmpty()) {
                        break;
                    }
                    continue;
                }
            }
            int n = 0;
            final Iterator iterator2 = list2.iterator();
            while (iterator2.hasNext()) {
                n += ((KeyShareEntry)iterator2.next()).getEncodedSize();
            }
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            final Iterator iterator3 = list2.iterator();
            while (iterator3.hasNext()) {
                wrap.put(((KeyShareEntry)iterator3.next()).getEncoded());
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_KEY_SHARE, new CHKeyShareSpec((List)list2));
            return array;
        }
    }
    
    private static final class CHKeyShareConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeExtensions.containsKey(SSLExtension.CH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("The key_share extension has been loaded", new Object[0]);
                }
                return;
            }
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable key_share extension", new Object[0]);
                }
                return;
            }
            CHKeyShareSpec chKeyShareSpec;
            try {
                chKeyShareSpec = new CHKeyShareSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            final LinkedList list = new LinkedList();
            for (final KeyShareEntry keyShareEntry : chKeyShareSpec.clientShares) {
                final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(keyShareEntry.namedGroupId);
                if (value == null || !SupportedGroupsExtension.SupportedGroups.isActivatable(serverHandshakeContext.algorithmConstraints, value)) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.fine("Ignore unsupported named group: " + SupportedGroupsExtension.NamedGroup.nameOf(keyShareEntry.namedGroupId), new Object[0]);
                }
                else if (value.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                    try {
                        final ECDHKeyExchange.ECDHECredentials value2 = ECDHKeyExchange.ECDHECredentials.valueOf(value, keyShareEntry.keyExchange);
                        if (value2 == null) {
                            continue;
                        }
                        if (!serverHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), value2.popPublicKey)) {
                            SSLLogger.warning("ECDHE key share entry does not comply to algorithm constraints", new Object[0]);
                        }
                        else {
                            list.add(value2);
                        }
                    }
                    catch (final IOException | GeneralSecurityException ex2) {
                        SSLLogger.warning("Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(keyShareEntry.namedGroupId), new Object[0]);
                    }
                }
                else {
                    if (value.type != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                        continue;
                    }
                    try {
                        final DHKeyExchange.DHECredentials value3 = DHKeyExchange.DHECredentials.valueOf(value, keyShareEntry.keyExchange);
                        if (value3 == null) {
                            continue;
                        }
                        if (!serverHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), value3.popPublicKey)) {
                            SSLLogger.warning("DHE key share entry does not comply to algorithm constraints", new Object[0]);
                        }
                        else {
                            list.add(value3);
                        }
                    }
                    catch (final IOException | GeneralSecurityException ex3) {
                        SSLLogger.warning("Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(keyShareEntry.namedGroupId), new Object[0]);
                    }
                }
            }
            if (!list.isEmpty()) {
                serverHandshakeContext.handshakeCredentials.addAll(list);
            }
            else {
                serverHandshakeContext.handshakeProducers.put(SSLHandshake.HELLO_RETRY_REQUEST.id, SSLHandshake.HELLO_RETRY_REQUEST);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_KEY_SHARE, chKeyShareSpec);
        }
    }
    
    private static final class CHKeyShareOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec() && serverHandshakeContext.handshakeExtensions.containsKey(SSLExtension.CH_SUPPORTED_GROUPS)) {
                throw serverHandshakeContext.conContext.fatal(Alert.MISSING_EXTENSION, "No key_share extension to work with the supported_groups extension");
            }
        }
    }
    
    static final class SHKeyShareSpec implements SSLExtension.SSLExtensionSpec
    {
        final KeyShareEntry serverShare;
        
        SHKeyShareSpec(final KeyShareEntry serverShare) {
            this.serverShare = serverShare;
        }
        
        private SHKeyShareSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 5) {
                throw new SSLProtocolException("Invalid key_share extension: insufficient data (length=" + byteBuffer.remaining() + ")");
            }
            final int int16 = Record.getInt16(byteBuffer);
            final byte[] bytes16 = Record.getBytes16(byteBuffer);
            if (byteBuffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid key_share extension: unknown extra data");
            }
            this.serverShare = new KeyShareEntry(int16, bytes16);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"server_share\": '{'\n  \"named group\": {0}\n  \"key_exchange\": '{'\n{1}\n  '}'\n'}',", Locale.ENGLISH).format(new Object[] { SupportedGroupsExtension.NamedGroup.nameOf(this.serverShare.namedGroupId), Utilities.indent(new HexDumpEncoder().encode(this.serverShare.keyExchange), "    ") });
        }
    }
    
    private static final class SHKeyShareStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new SHKeyShareSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class SHKeyShareProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_KEY_SHARE) == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no client key_share extension", new Object[0]);
                }
                return null;
            }
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.SH_KEY_SHARE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no available server key_share extension", new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.handshakeCredentials == null || serverHandshakeContext.handshakeCredentials.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client key share entries", new Object[0]);
                }
                return null;
            }
            KeyShareEntry keyShareEntry = null;
            for (final SSLCredentials sslCredentials : serverHandshakeContext.handshakeCredentials) {
                SupportedGroupsExtension.NamedGroup namedGroup = null;
                if (sslCredentials instanceof ECDHKeyExchange.ECDHECredentials) {
                    namedGroup = ((ECDHKeyExchange.ECDHECredentials)sslCredentials).namedGroup;
                }
                else if (sslCredentials instanceof DHKeyExchange.DHECredentials) {
                    namedGroup = ((DHKeyExchange.DHECredentials)sslCredentials).namedGroup;
                }
                if (namedGroup == null) {
                    continue;
                }
                final SSLKeyExchange value = SSLKeyExchange.valueOf(namedGroup);
                if (value == null) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.warning("No key exchange for named group " + namedGroup.name, new Object[0]);
                }
                else {
                    for (final SSLPossession sslPossession : value.createPossessions(serverHandshakeContext)) {
                        if (sslPossession instanceof ECDHKeyExchange.ECDHEPossession || sslPossession instanceof DHKeyExchange.DHEPossession) {
                            serverHandshakeContext.handshakeKeyExchange = value;
                            serverHandshakeContext.handshakePossessions.add(sslPossession);
                            keyShareEntry = new KeyShareEntry(namedGroup.id, sslPossession.encode());
                            break;
                        }
                    }
                    if (keyShareEntry != null) {
                        for (final Map.Entry<Byte, HandshakeProducer> entry : value.getHandshakeProducers(serverHandshakeContext)) {
                            serverHandshakeContext.handshakeProducers.put(entry.getKey(), entry.getValue());
                        }
                        break;
                    }
                    continue;
                }
            }
            if (keyShareEntry == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available server key_share extension", new Object[0]);
                }
                return null;
            }
            final byte[] access$1500 = keyShareEntry.getEncoded();
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_KEY_SHARE, new SHKeyShareSpec(keyShareEntry));
            return access$1500;
        }
    }
    
    private static final class SHKeyShareConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.clientRequestedNamedGroups == null || clientHandshakeContext.clientRequestedNamedGroups.isEmpty()) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected key_share extension in ServerHello");
            }
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.SH_KEY_SHARE)) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in ServerHello");
            }
            SHKeyShareSpec shKeyShareSpec;
            try {
                shKeyShareSpec = new SHKeyShareSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            final KeyShareEntry serverShare = shKeyShareSpec.serverShare;
            final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(serverShare.namedGroupId);
            if (value == null || !SupportedGroupsExtension.SupportedGroups.isActivatable(clientHandshakeContext.algorithmConstraints, value)) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported named group: " + SupportedGroupsExtension.NamedGroup.nameOf(serverShare.namedGroupId));
            }
            final SSLKeyExchange value2 = SSLKeyExchange.valueOf(value);
            if (value2 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No key exchange for named group " + value.name);
            }
            SSLCredentials sslCredentials = null;
            Label_0486: {
                if (value.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                    try {
                        final ECDHKeyExchange.ECDHECredentials value3 = ECDHKeyExchange.ECDHECredentials.valueOf(value, serverShare.keyExchange);
                        if (value3 != null) {
                            if (!clientHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), value3.popPublicKey)) {
                                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "ECDHE key share entry does not comply to algorithm constraints");
                            }
                            sslCredentials = value3;
                        }
                        break Label_0486;
                    }
                    catch (final IOException | GeneralSecurityException ex2) {
                        throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(serverShare.namedGroupId));
                    }
                }
                if (value.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                    try {
                        final DHKeyExchange.DHECredentials value4 = DHKeyExchange.DHECredentials.valueOf(value, serverShare.keyExchange);
                        if (value4 != null) {
                            if (!clientHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), value4.popPublicKey)) {
                                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "DHE key share entry does not comply to algorithm constraints");
                            }
                            sslCredentials = value4;
                        }
                        break Label_0486;
                    }
                    catch (final IOException | GeneralSecurityException ex3) {
                        throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Cannot decode named group: " + SupportedGroupsExtension.NamedGroup.nameOf(serverShare.namedGroupId));
                    }
                }
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported named group: " + SupportedGroupsExtension.NamedGroup.nameOf(serverShare.namedGroupId));
            }
            if (sslCredentials == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported named group: " + value.name);
            }
            clientHandshakeContext.handshakeKeyExchange = value2;
            clientHandshakeContext.handshakeCredentials.add(sslCredentials);
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_KEY_SHARE, shKeyShareSpec);
        }
    }
    
    private static final class SHKeyShareAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                SSLLogger.fine("No key_share extension in ServerHello, cleanup the key shares if necessary", new Object[0]);
            }
            clientHandshakeContext.handshakePossessions.clear();
        }
    }
    
    static final class HRRKeyShareSpec implements SSLExtension.SSLExtensionSpec
    {
        final int selectedGroup;
        
        HRRKeyShareSpec(final SupportedGroupsExtension.NamedGroup namedGroup) {
            this.selectedGroup = namedGroup.id;
        }
        
        private HRRKeyShareSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() != 2) {
                throw new SSLProtocolException("Invalid key_share extension: improper data (length=" + byteBuffer.remaining() + ")");
            }
            this.selectedGroup = Record.getInt16(byteBuffer);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"selected group\": '['{0}']'", Locale.ENGLISH).format(new Object[] { SupportedGroupsExtension.NamedGroup.nameOf(this.selectedGroup) });
        }
    }
    
    private static final class HRRKeyShareStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new HRRKeyShareSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class HRRKeyShareProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_KEY_SHARE)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in HelloRetryRequest");
            }
            if (serverHandshakeContext.clientRequestedNamedGroups == null || serverHandshakeContext.clientRequestedNamedGroups.isEmpty()) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected key_share extension in HelloRetryRequest");
            }
            SupportedGroupsExtension.NamedGroup serverSelectedNamedGroup = null;
            for (final SupportedGroupsExtension.NamedGroup namedGroup : serverHandshakeContext.clientRequestedNamedGroups) {
                if (SupportedGroupsExtension.SupportedGroups.isActivatable(serverHandshakeContext.algorithmConstraints, namedGroup)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("HelloRetryRequest selected named group: " + namedGroup.name, new Object[0]);
                    }
                    serverSelectedNamedGroup = namedGroup;
                    break;
                }
            }
            if (serverSelectedNamedGroup == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No common named group");
            }
            final byte[] array = { (byte)(serverSelectedNamedGroup.id >> 8 & 0xFF), (byte)(serverSelectedNamedGroup.id & 0xFF) };
            serverHandshakeContext.serverSelectedNamedGroup = serverSelectedNamedGroup;
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.HRR_KEY_SHARE, new HRRKeyShareSpec(serverSelectedNamedGroup));
            return array;
        }
    }
    
    private static final class HRRKeyShareReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_KEY_SHARE)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in HelloRetryRequest");
            }
            final CHKeyShareSpec chKeyShareSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_KEY_SHARE);
            if (chKeyShareSpec != null && chKeyShareSpec.clientShares != null && chKeyShareSpec.clientShares.size() == 1) {
                final int namedGroupId = chKeyShareSpec.clientShares.get(0).namedGroupId;
                return new byte[] { (byte)(namedGroupId >> 8 & 0xFF), (byte)(namedGroupId & 0xFF) };
            }
            return null;
        }
    }
    
    private static final class HRRKeyShareConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_KEY_SHARE)) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported key_share extension in HelloRetryRequest");
            }
            if (clientHandshakeContext.clientRequestedNamedGroups == null || clientHandshakeContext.clientRequestedNamedGroups.isEmpty()) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected key_share extension in HelloRetryRequest");
            }
            HRRKeyShareSpec hrrKeyShareSpec;
            try {
                hrrKeyShareSpec = new HRRKeyShareSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(hrrKeyShareSpec.selectedGroup);
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported HelloRetryRequest selected group: " + SupportedGroupsExtension.NamedGroup.nameOf(hrrKeyShareSpec.selectedGroup));
            }
            if (!clientHandshakeContext.clientRequestedNamedGroups.contains(value)) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected HelloRetryRequest selected group: " + value.name);
            }
            clientHandshakeContext.serverSelectedNamedGroup = value;
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.HRR_KEY_SHARE, hrrKeyShareSpec);
        }
    }
}
