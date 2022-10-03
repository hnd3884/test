package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import java.util.Collections;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.ReadOnlyModifyDNRequest;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.ReadOnlyModifyRequest;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ReadOnlyDeleteRequest;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.ReadOnlyCompareRequest;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.PLAINBindRequest;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.util.ByteStringBuffer;
import java.util.Arrays;
import com.unboundid.ldap.sdk.ReadOnlyAddRequest;
import com.unboundid.ldif.LDIFException;
import java.util.Collection;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.RootDSE;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFReader;
import java.io.File;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import java.net.InetAddress;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.DN;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptorRequestHandler;
import java.io.IOException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.LDAPException;
import javax.net.SocketFactory;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.ldap.sdk.FullLDAPInterface;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InMemoryDirectoryServer implements FullLDAPInterface
{
    private final InMemoryRequestHandler inMemoryHandler;
    private final Map<String, LDAPListener> listeners;
    private final Map<String, LDAPListenerConfig> ldapListenerConfigs;
    private final Map<String, SocketFactory> clientSocketFactories;
    private final ReadOnlyInMemoryDirectoryServerConfig config;
    
    public InMemoryDirectoryServer(final String... baseDNs) throws LDAPException {
        this(new InMemoryDirectoryServerConfig(baseDNs));
    }
    
    public InMemoryDirectoryServer(final InMemoryDirectoryServerConfig cfg) throws LDAPException {
        Validator.ensureNotNull(cfg);
        this.config = new ReadOnlyInMemoryDirectoryServerConfig(cfg);
        this.inMemoryHandler = new InMemoryRequestHandler(this.config);
        LDAPListenerRequestHandler requestHandler = this.inMemoryHandler;
        if (this.config.getAccessLogHandler() != null) {
            requestHandler = new AccessLogRequestHandler(this.config.getAccessLogHandler(), requestHandler);
        }
        if (this.config.getLDAPDebugLogHandler() != null) {
            requestHandler = new LDAPDebuggerRequestHandler(this.config.getLDAPDebugLogHandler(), requestHandler);
        }
        if (this.config.getCodeLogPath() != null) {
            try {
                requestHandler = new ToCodeRequestHandler(this.config.getCodeLogPath(), this.config.includeRequestProcessingInCodeLog(), requestHandler);
            }
            catch (final IOException ioe) {
                Debug.debugException(ioe);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_CANNOT_OPEN_CODE_LOG.get(this.config.getCodeLogPath(), StaticUtils.getExceptionMessage(ioe)), ioe);
            }
        }
        if (!this.config.getOperationInterceptors().isEmpty()) {
            requestHandler = new InMemoryOperationInterceptorRequestHandler(this.config.getOperationInterceptors(), requestHandler);
        }
        final List<InMemoryListenerConfig> listenerConfigs = this.config.getListenerConfigs();
        this.listeners = new LinkedHashMap<String, LDAPListener>(StaticUtils.computeMapCapacity(listenerConfigs.size()));
        this.ldapListenerConfigs = new LinkedHashMap<String, LDAPListenerConfig>(StaticUtils.computeMapCapacity(listenerConfigs.size()));
        this.clientSocketFactories = new LinkedHashMap<String, SocketFactory>(StaticUtils.computeMapCapacity(listenerConfigs.size()));
        for (final InMemoryListenerConfig c : listenerConfigs) {
            final String name = StaticUtils.toLowerCase(c.getListenerName());
            LDAPListenerRequestHandler listenerRequestHandler;
            if (c.getStartTLSSocketFactory() == null) {
                listenerRequestHandler = requestHandler;
            }
            else {
                listenerRequestHandler = new StartTLSRequestHandler(c.getStartTLSSocketFactory(), requestHandler);
            }
            final LDAPListenerConfig listenerCfg = new LDAPListenerConfig(c.getListenPort(), listenerRequestHandler);
            listenerCfg.setMaxConnections(this.config.getMaxConnections());
            listenerCfg.setExceptionHandler(this.config.getListenerExceptionHandler());
            listenerCfg.setListenAddress(c.getListenAddress());
            listenerCfg.setServerSocketFactory(c.getServerSocketFactory());
            this.ldapListenerConfigs.put(name, listenerCfg);
            if (c.getClientSocketFactory() != null) {
                this.clientSocketFactories.put(name, c.getClientSocketFactory());
            }
        }
    }
    
    public synchronized void startListening() throws LDAPException {
        final ArrayList<String> messages = new ArrayList<String>(this.listeners.size());
        for (final Map.Entry<String, LDAPListenerConfig> cfgEntry : this.ldapListenerConfigs.entrySet()) {
            final String name = cfgEntry.getKey();
            if (this.listeners.containsKey(name)) {
                continue;
            }
            final LDAPListenerConfig listenerConfig = cfgEntry.getValue();
            final LDAPListener listener = new LDAPListener(listenerConfig);
            try {
                listener.startListening();
                listenerConfig.setListenPort(listener.getListenPort());
                this.listeners.put(name, listener);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                messages.add(ListenerMessages.ERR_MEM_DS_START_FAILED.get(name, StaticUtils.getExceptionMessage(e)));
            }
        }
        if (!messages.isEmpty()) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, StaticUtils.concatenateStrings(messages));
        }
    }
    
    public synchronized void startListening(final String listenerName) throws LDAPException {
        final String name = StaticUtils.toLowerCase(listenerName);
        if (this.listeners.containsKey(name)) {
            return;
        }
        final LDAPListenerConfig listenerConfig = this.ldapListenerConfigs.get(name);
        if (listenerConfig == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_NO_SUCH_LISTENER.get(listenerName));
        }
        final LDAPListener listener = new LDAPListener(listenerConfig);
        try {
            listener.startListening();
            listenerConfig.setListenPort(listener.getListenPort());
            this.listeners.put(name, listener);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_START_FAILED.get(name, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void close() {
        this.shutDown(true);
    }
    
    public synchronized void closeAllConnections(final boolean sendNoticeOfDisconnection) {
        for (final LDAPListener l : this.listeners.values()) {
            try {
                l.closeAllConnections(sendNoticeOfDisconnection);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
    }
    
    public synchronized void shutDown(final boolean closeExistingConnections) {
        for (final LDAPListener l : this.listeners.values()) {
            try {
                l.shutDown(closeExistingConnections);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        this.listeners.clear();
    }
    
    public synchronized void shutDown(final String listenerName, final boolean closeExistingConnections) {
        final String name = StaticUtils.toLowerCase(listenerName);
        final LDAPListener listener = this.listeners.remove(name);
        if (listener != null) {
            listener.shutDown(closeExistingConnections);
        }
    }
    
    public synchronized void restartServer() throws LDAPException {
        this.shutDown(true);
        try {
            Thread.sleep(100L);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        this.startListening();
    }
    
    public synchronized void restartListener(final String listenerName) throws LDAPException {
        this.shutDown(listenerName, true);
        try {
            Thread.sleep(100L);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        this.startListening(listenerName);
    }
    
    public ReadOnlyInMemoryDirectoryServerConfig getConfig() {
        return this.config;
    }
    
    InMemoryRequestHandler getInMemoryRequestHandler() {
        return this.inMemoryHandler;
    }
    
    public InMemoryDirectoryServerSnapshot createSnapshot() {
        return this.inMemoryHandler.createSnapshot();
    }
    
    public void restoreSnapshot(final InMemoryDirectoryServerSnapshot snapshot) {
        this.inMemoryHandler.restoreSnapshot(snapshot);
    }
    
    public List<DN> getBaseDNs() {
        return this.inMemoryHandler.getBaseDNs();
    }
    
    public LDAPConnection getConnection() throws LDAPException {
        return this.getConnection(null, null);
    }
    
    public LDAPConnection getConnection(final LDAPConnectionOptions options) throws LDAPException {
        return this.getConnection(null, options);
    }
    
    public LDAPConnection getConnection(final String listenerName) throws LDAPException {
        return this.getConnection(listenerName, null);
    }
    
    public synchronized LDAPConnection getConnection(final String listenerName, final LDAPConnectionOptions options) throws LDAPException {
        LDAPListenerConfig listenerConfig;
        SocketFactory clientSocketFactory;
        if (listenerName == null) {
            final String name = this.getFirstListenerName();
            if (name == null) {
                throw new LDAPException(ResultCode.CONNECT_ERROR, ListenerMessages.ERR_MEM_DS_GET_CONNECTION_NO_LISTENERS.get());
            }
            listenerConfig = this.ldapListenerConfigs.get(name);
            clientSocketFactory = this.clientSocketFactories.get(name);
        }
        else {
            final String name = StaticUtils.toLowerCase(listenerName);
            if (!this.listeners.containsKey(name)) {
                throw new LDAPException(ResultCode.CONNECT_ERROR, ListenerMessages.ERR_MEM_DS_GET_CONNECTION_LISTENER_NOT_RUNNING.get(listenerName));
            }
            listenerConfig = this.ldapListenerConfigs.get(name);
            clientSocketFactory = this.clientSocketFactories.get(name);
        }
        final InetAddress listenAddress = listenerConfig.getListenAddress();
        if (listenAddress != null) {
            if (!listenAddress.isAnyLocalAddress()) {
                final String hostAddress = listenAddress.getHostAddress();
                return new LDAPConnection(clientSocketFactory, options, hostAddress, listenerConfig.getListenPort());
            }
        }
        String hostAddress;
        try {
            hostAddress = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getLocalHost().getHostAddress();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            hostAddress = "127.0.0.1";
        }
        return new LDAPConnection(clientSocketFactory, options, hostAddress, listenerConfig.getListenPort());
    }
    
    public LDAPConnectionPool getConnectionPool(final int maxConnections) throws LDAPException {
        return this.getConnectionPool(null, null, 1, maxConnections);
    }
    
    public LDAPConnectionPool getConnectionPool(final String listenerName, final LDAPConnectionOptions options, final int initialConnections, final int maxConnections) throws LDAPException {
        final LDAPConnection conn = this.getConnection(listenerName, options);
        return new LDAPConnectionPool(conn, initialConnections, maxConnections);
    }
    
    public InetAddress getListenAddress() {
        return this.getListenAddress(null);
    }
    
    public synchronized InetAddress getListenAddress(final String listenerName) {
        String name;
        if (listenerName == null) {
            name = this.getFirstListenerName();
        }
        else {
            name = StaticUtils.toLowerCase(listenerName);
        }
        final LDAPListenerConfig listenerCfg = this.ldapListenerConfigs.get(name);
        if (listenerCfg == null) {
            return null;
        }
        return listenerCfg.getListenAddress();
    }
    
    public int getListenPort() {
        return this.getListenPort(null);
    }
    
    public synchronized int getListenPort(final String listenerName) {
        String name;
        if (listenerName == null) {
            name = this.getFirstListenerName();
        }
        else {
            name = StaticUtils.toLowerCase(listenerName);
        }
        final LDAPListener listener = this.listeners.get(name);
        if (listener == null) {
            return -1;
        }
        return listener.getListenPort();
    }
    
    public SocketFactory getClientSocketFactory() {
        return this.getClientSocketFactory(null);
    }
    
    public synchronized SocketFactory getClientSocketFactory(final String listenerName) {
        String name;
        if (listenerName == null) {
            name = this.getFirstListenerName();
        }
        else {
            name = StaticUtils.toLowerCase(listenerName);
        }
        return this.clientSocketFactories.get(name);
    }
    
    private String getFirstListenerName() {
        for (final Map.Entry<String, LDAPListenerConfig> e : this.ldapListenerConfigs.entrySet()) {
            final String name = e.getKey();
            if (this.listeners.containsKey(name)) {
                return name;
            }
        }
        return null;
    }
    
    public long getProcessingDelayMillis() {
        return this.inMemoryHandler.getProcessingDelayMillis();
    }
    
    public void setProcessingDelayMillis(final long processingDelayMillis) {
        this.inMemoryHandler.setProcessingDelayMillis(processingDelayMillis);
    }
    
    public int countEntries() {
        return this.countEntries(false);
    }
    
    public int countEntries(final boolean includeChangeLog) {
        return this.inMemoryHandler.countEntries(includeChangeLog);
    }
    
    public int countEntriesBelow(final String baseDN) throws LDAPException {
        return this.inMemoryHandler.countEntriesBelow(baseDN);
    }
    
    public void clear() {
        this.inMemoryHandler.clear();
    }
    
    public int importFromLDIF(final boolean clear, final String path) throws LDAPException {
        return this.importFromLDIF(clear, new File(path));
    }
    
    public int importFromLDIF(final boolean clear, final File ldifFile) throws LDAPException {
        LDIFReader reader;
        try {
            reader = new LDIFReader(ldifFile);
            final Schema schema = this.getSchema();
            if (schema != null) {
                reader.setSchema(schema);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_INIT_FROM_LDIF_CANNOT_CREATE_READER.get(ldifFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
        }
        return this.importFromLDIF(clear, reader);
    }
    
    public int importFromLDIF(final boolean clear, final LDIFReader reader) throws LDAPException {
        return this.inMemoryHandler.importFromLDIF(clear, reader);
    }
    
    public int exportToLDIF(final String path, final boolean excludeGeneratedAttrs, final boolean excludeChangeLog) throws LDAPException {
        LDIFWriter ldifWriter;
        try {
            ldifWriter = new LDIFWriter(path);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_EXPORT_TO_LDIF_CANNOT_CREATE_WRITER.get(path, StaticUtils.getExceptionMessage(e)), e);
        }
        return this.exportToLDIF(ldifWriter, excludeGeneratedAttrs, excludeChangeLog, true);
    }
    
    public int exportToLDIF(final LDIFWriter ldifWriter, final boolean excludeGeneratedAttrs, final boolean excludeChangeLog, final boolean closeWriter) throws LDAPException {
        return this.inMemoryHandler.exportToLDIF(ldifWriter, excludeGeneratedAttrs, excludeChangeLog, closeWriter);
    }
    
    public int applyChangesFromLDIF(final String path) throws LDAPException {
        return this.applyChangesFromLDIF(new File(path));
    }
    
    public int applyChangesFromLDIF(final File ldifFile) throws LDAPException {
        LDIFReader reader;
        try {
            reader = new LDIFReader(ldifFile);
            final Schema schema = this.getSchema();
            if (schema != null) {
                reader.setSchema(schema);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_DS_APPLY_CHANGES_FROM_LDIF_CANNOT_CREATE_READER.get(ldifFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
        }
        return this.applyChangesFromLDIF(reader);
    }
    
    public int applyChangesFromLDIF(final LDIFReader reader) throws LDAPException {
        return this.inMemoryHandler.applyChangesFromLDIF(reader);
    }
    
    @Override
    public RootDSE getRootDSE() throws LDAPException {
        return new RootDSE(this.inMemoryHandler.getEntry(""));
    }
    
    @Override
    public Schema getSchema() throws LDAPException {
        return this.inMemoryHandler.getSchema();
    }
    
    @Override
    public Schema getSchema(final String entryDN) throws LDAPException {
        return this.inMemoryHandler.getSchema();
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn) throws LDAPException {
        return this.searchForEntry(dn, SearchScope.BASE, Filter.createPresenceFilter("objectClass"), new String[0]);
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn, final String... attributes) throws LDAPException {
        return this.searchForEntry(dn, SearchScope.BASE, Filter.createPresenceFilter("objectClass"), attributes);
    }
    
    @Override
    public LDAPResult add(final String dn, final Attribute... attributes) throws LDAPException {
        return this.add(new AddRequest(dn, attributes));
    }
    
    @Override
    public LDAPResult add(final String dn, final Collection<Attribute> attributes) throws LDAPException {
        return this.add(new AddRequest(dn, attributes));
    }
    
    @Override
    public LDAPResult add(final Entry entry) throws LDAPException {
        return this.add(new AddRequest(entry));
    }
    
    @Override
    public LDAPResult add(final String... ldifLines) throws LDIFException, LDAPException {
        return this.add(new AddRequest(ldifLines));
    }
    
    @Override
    public LDAPResult add(final AddRequest addRequest) throws LDAPException {
        return this.inMemoryHandler.add(addRequest);
    }
    
    @Override
    public LDAPResult add(final ReadOnlyAddRequest addRequest) throws LDAPException {
        return this.add(addRequest.duplicate());
    }
    
    public void addEntries(final Entry... entries) throws LDAPException {
        this.addEntries(Arrays.asList(entries));
    }
    
    public void addEntries(final List<? extends Entry> entries) throws LDAPException {
        this.inMemoryHandler.addEntries(entries);
    }
    
    public void addEntries(final String... ldifEntryLines) throws LDAPException {
        final ByteStringBuffer buffer = new ByteStringBuffer();
        for (final String line : ldifEntryLines) {
            buffer.append((CharSequence)line);
            buffer.append(StaticUtils.EOL_BYTES);
        }
        final ArrayList<Entry> entryList = new ArrayList<Entry>(10);
        final LDIFReader reader = new LDIFReader(buffer.asInputStream());
        final Schema schema = this.getSchema();
        Label_0092: {
            if (schema == null) {
                break Label_0092;
            }
            reader.setSchema(schema);
            try {
                while (true) {
                    final Entry entry = reader.readEntry();
                    if (entry == null) {
                        break;
                    }
                    entryList.add(entry);
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_ADD_ENTRIES_LDIF_PARSE_EXCEPTION.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        this.addEntries(entryList);
    }
    
    @Override
    public BindResult bind(final String bindDN, final String password) throws LDAPException {
        return this.bind(new SimpleBindRequest(bindDN, password));
    }
    
    @Override
    public BindResult bind(final BindRequest bindRequest) throws LDAPException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(bindRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        BindRequestProtocolOp bindOp;
        if (bindRequest instanceof SimpleBindRequest) {
            final SimpleBindRequest r = (SimpleBindRequest)bindRequest;
            bindOp = new BindRequestProtocolOp(r.getBindDN(), r.getPassword().getValue());
        }
        else {
            if (!(bindRequest instanceof PLAINBindRequest)) {
                throw new LDAPException(ResultCode.AUTH_METHOD_NOT_SUPPORTED, ListenerMessages.ERR_MEM_DS_UNSUPPORTED_BIND_TYPE.get());
            }
            final PLAINBindRequest r2 = (PLAINBindRequest)bindRequest;
            final byte[] authZIDBytes = StaticUtils.getBytes(r2.getAuthorizationID());
            final byte[] authNIDBytes = StaticUtils.getBytes(r2.getAuthenticationID());
            final byte[] passwordBytes = r2.getPasswordBytes();
            final byte[] credBytes = new byte[2 + authZIDBytes.length + authNIDBytes.length + passwordBytes.length];
            System.arraycopy(authZIDBytes, 0, credBytes, 0, authZIDBytes.length);
            int pos = authZIDBytes.length + 1;
            System.arraycopy(authNIDBytes, 0, credBytes, pos, authNIDBytes.length);
            pos += authNIDBytes.length + 1;
            System.arraycopy(passwordBytes, 0, credBytes, pos, passwordBytes.length);
            bindOp = new BindRequestProtocolOp(null, "PLAIN", new ASN1OctetString(credBytes));
        }
        final LDAPMessage responseMessage = this.inMemoryHandler.processBindRequest(1, bindOp, requestControlList);
        final BindResponseProtocolOp bindResponse = responseMessage.getBindResponseProtocolOp();
        final BindResult bindResult = new BindResult(new LDAPResult(responseMessage.getMessageID(), ResultCode.valueOf(bindResponse.getResultCode()), bindResponse.getDiagnosticMessage(), bindResponse.getMatchedDN(), bindResponse.getReferralURLs(), responseMessage.getControls()));
        switch (bindResponse.getResultCode()) {
            case 0: {
                return bindResult;
            }
            default: {
                throw new LDAPException(bindResult);
            }
        }
    }
    
    @Override
    public CompareResult compare(final String dn, final String attributeName, final String assertionValue) throws LDAPException {
        return this.compare(new CompareRequest(dn, attributeName, assertionValue));
    }
    
    @Override
    public CompareResult compare(final CompareRequest compareRequest) throws LDAPException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(compareRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        final LDAPMessage responseMessage = this.inMemoryHandler.processCompareRequest(1, new CompareRequestProtocolOp(compareRequest.getDN(), compareRequest.getAttributeName(), compareRequest.getRawAssertionValue()), requestControlList);
        final CompareResponseProtocolOp compareResponse = responseMessage.getCompareResponseProtocolOp();
        final LDAPResult compareResult = new LDAPResult(responseMessage.getMessageID(), ResultCode.valueOf(compareResponse.getResultCode()), compareResponse.getDiagnosticMessage(), compareResponse.getMatchedDN(), compareResponse.getReferralURLs(), responseMessage.getControls());
        switch (compareResponse.getResultCode()) {
            case 5:
            case 6: {
                return new CompareResult(compareResult);
            }
            default: {
                throw new LDAPException(compareResult);
            }
        }
    }
    
    @Override
    public CompareResult compare(final ReadOnlyCompareRequest compareRequest) throws LDAPException {
        return this.compare(compareRequest.duplicate());
    }
    
    @Override
    public LDAPResult delete(final String dn) throws LDAPException {
        return this.delete(new DeleteRequest(dn));
    }
    
    @Override
    public LDAPResult delete(final DeleteRequest deleteRequest) throws LDAPException {
        return this.inMemoryHandler.delete(deleteRequest);
    }
    
    @Override
    public LDAPResult delete(final ReadOnlyDeleteRequest deleteRequest) throws LDAPException {
        return this.delete(deleteRequest.duplicate());
    }
    
    public int deleteSubtree(final String baseDN) throws LDAPException {
        return this.inMemoryHandler.deleteSubtree(baseDN);
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final String requestOID) throws LDAPException {
        Validator.ensureNotNull(requestOID);
        return this.processExtendedOperation(new ExtendedRequest(requestOID));
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final String requestOID, final ASN1OctetString requestValue) throws LDAPException {
        Validator.ensureNotNull(requestOID);
        return this.processExtendedOperation(new ExtendedRequest(requestOID, requestValue));
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final ExtendedRequest extendedRequest) throws LDAPException {
        Validator.ensureNotNull(extendedRequest);
        final ArrayList<Control> requestControlList = new ArrayList<Control>(extendedRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        final LDAPMessage responseMessage = this.inMemoryHandler.processExtendedRequest(1, new ExtendedRequestProtocolOp(extendedRequest.getOID(), extendedRequest.getValue()), requestControlList);
        final ExtendedResponseProtocolOp extendedResponse = responseMessage.getExtendedResponseProtocolOp();
        final ResultCode rc = ResultCode.valueOf(extendedResponse.getResultCode());
        final List<String> referralURLList = extendedResponse.getReferralURLs();
        String[] referralURLs;
        if (referralURLList == null || referralURLList.isEmpty()) {
            referralURLs = StaticUtils.NO_STRINGS;
        }
        else {
            referralURLs = new String[referralURLList.size()];
            referralURLList.toArray(referralURLs);
        }
        final List<Control> controlList = responseMessage.getControls();
        Control[] responseControls;
        if (controlList == null || controlList.isEmpty()) {
            responseControls = StaticUtils.NO_CONTROLS;
        }
        else {
            responseControls = new Control[controlList.size()];
            controlList.toArray(responseControls);
        }
        final ExtendedResult extendedResult = new ExtendedResult(responseMessage.getMessageID(), rc, extendedResponse.getDiagnosticMessage(), extendedResponse.getMatchedDN(), referralURLs, extendedResponse.getResponseOID(), extendedResponse.getResponseValue(), responseControls);
        if (extendedResult.getOID() == null && extendedResult.getValue() == null) {
            switch (rc.intValue()) {
                case 1:
                case 2:
                case 51:
                case 52:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 90:
                case 91: {
                    throw new LDAPException(extendedResult);
                }
            }
        }
        return extendedResult;
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification mod) throws LDAPException {
        return this.modify(new ModifyRequest(dn, mod));
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification... mods) throws LDAPException {
        return this.modify(new ModifyRequest(dn, mods));
    }
    
    @Override
    public LDAPResult modify(final String dn, final List<Modification> mods) throws LDAPException {
        return this.modify(new ModifyRequest(dn, mods));
    }
    
    @Override
    public LDAPResult modify(final String... ldifModificationLines) throws LDIFException, LDAPException {
        return this.modify(new ModifyRequest(ldifModificationLines));
    }
    
    @Override
    public LDAPResult modify(final ModifyRequest modifyRequest) throws LDAPException {
        return this.inMemoryHandler.modify(modifyRequest);
    }
    
    @Override
    public LDAPResult modify(final ReadOnlyModifyRequest modifyRequest) throws LDAPException {
        return this.modify(modifyRequest.duplicate());
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN) throws LDAPException {
        return this.modifyDN(new ModifyDNRequest(dn, newRDN, deleteOldRDN));
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) throws LDAPException {
        return this.modifyDN(new ModifyDNRequest(dn, newRDN, deleteOldRDN, newSuperiorDN));
    }
    
    @Override
    public LDAPResult modifyDN(final ModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.inMemoryHandler.modifyDN(modifyDNRequest);
    }
    
    @Override
    public LDAPResult modifyDN(final ReadOnlyModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.modifyDN(modifyDNRequest.duplicate());
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, parseFilter(filter), attributes));
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, filter, attributes));
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, parseFilter(filter), attributes));
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, filter, attributes));
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, parseFilter(filter), attributes));
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, parseFilter(filter), attributes));
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public SearchResult search(final SearchRequest searchRequest) throws LDAPSearchException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(searchRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        final List<SearchResultEntry> entryList = new ArrayList<SearchResultEntry>(10);
        final List<SearchResultReference> referenceList = new ArrayList<SearchResultReference>(10);
        final LDAPMessage responseMessage = this.inMemoryHandler.processSearchRequest(1, new SearchRequestProtocolOp(searchRequest.getBaseDN(), searchRequest.getScope(), searchRequest.getDereferencePolicy(), searchRequest.getSizeLimit(), searchRequest.getTimeLimitSeconds(), searchRequest.typesOnly(), searchRequest.getFilter(), searchRequest.getAttributeList()), requestControlList, entryList, referenceList);
        final SearchResultListener searchListener = searchRequest.getSearchResultListener();
        List<SearchResultEntry> returnEntryList;
        List<SearchResultReference> returnReferenceList;
        if (searchListener == null) {
            returnEntryList = Collections.unmodifiableList((List<? extends SearchResultEntry>)entryList);
            returnReferenceList = Collections.unmodifiableList((List<? extends SearchResultReference>)referenceList);
        }
        else {
            returnEntryList = null;
            returnReferenceList = null;
            for (final SearchResultEntry e : entryList) {
                searchListener.searchEntryReturned(e);
            }
            for (final SearchResultReference r : referenceList) {
                searchListener.searchReferenceReturned(r);
            }
        }
        final SearchResultDoneProtocolOp searchDone = responseMessage.getSearchResultDoneProtocolOp();
        final ResultCode rc = ResultCode.valueOf(searchDone.getResultCode());
        final List<String> referralURLList = searchDone.getReferralURLs();
        String[] referralURLs;
        if (referralURLList == null || referralURLList.isEmpty()) {
            referralURLs = StaticUtils.NO_STRINGS;
        }
        else {
            referralURLs = new String[referralURLList.size()];
            referralURLList.toArray(referralURLs);
        }
        final List<Control> controlList = responseMessage.getControls();
        Control[] responseControls;
        if (controlList == null || controlList.isEmpty()) {
            responseControls = StaticUtils.NO_CONTROLS;
        }
        else {
            responseControls = new Control[controlList.size()];
            controlList.toArray(responseControls);
        }
        final SearchResult searchResult = new SearchResult(responseMessage.getMessageID(), rc, searchDone.getDiagnosticMessage(), searchDone.getMatchedDN(), referralURLs, returnEntryList, returnReferenceList, entryList.size(), referenceList.size(), responseControls);
        if (rc == ResultCode.SUCCESS) {
            return searchResult;
        }
        throw new LDAPSearchException(searchResult);
    }
    
    @Override
    public SearchResult search(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.search(searchRequest.duplicate());
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, parseFilter(filter), attributes));
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, filter, attributes));
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, derefPolicy, 1, timeLimit, typesOnly, parseFilter(filter), attributes));
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, derefPolicy, 1, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public SearchResultEntry searchForEntry(final SearchRequest searchRequest) throws LDAPSearchException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(searchRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        SearchRequest r;
        if (searchRequest.getSizeLimit() == 1 && searchRequest.getSearchResultListener() == null) {
            r = searchRequest;
        }
        else {
            r = new SearchRequest(searchRequest.getBaseDN(), searchRequest.getScope(), searchRequest.getDereferencePolicy(), 1, searchRequest.getTimeLimitSeconds(), searchRequest.typesOnly(), searchRequest.getFilter(), searchRequest.getAttributes());
            r.setFollowReferrals(InternalSDKHelper.followReferralsInternal(r));
            r.setReferralConnector(InternalSDKHelper.getReferralConnectorInternal(r));
            r.setResponseTimeoutMillis(searchRequest.getResponseTimeoutMillis(null));
            r.setControls(requestControlList);
        }
        SearchResult result;
        try {
            result = this.search(r);
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            if (lse.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                return null;
            }
            throw lse;
        }
        if (result.getEntryCount() == 0) {
            return null;
        }
        return result.getSearchEntries().get(0);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.searchForEntry(searchRequest.duplicate());
    }
    
    public List<String> getPasswordAttributes() {
        return this.inMemoryHandler.getPasswordAttributes();
    }
    
    public InMemoryPasswordEncoder getPrimaryPasswordEncoder() {
        return this.inMemoryHandler.getPrimaryPasswordEncoder();
    }
    
    public List<InMemoryPasswordEncoder> getAllPasswordEncoders() {
        return this.inMemoryHandler.getAllPasswordEncoders();
    }
    
    public List<InMemoryDirectoryServerPassword> getPasswordsInEntry(final Entry entry, final ASN1OctetString clearPasswordToMatch) {
        return this.inMemoryHandler.getPasswordsInEntry(entry, clearPasswordToMatch);
    }
    
    private static Filter parseFilter(final String s) throws LDAPSearchException {
        try {
            return Filter.create(s);
        }
        catch (final LDAPException le) {
            throw new LDAPSearchException(le);
        }
    }
    
    public boolean entryExists(final String dn) throws LDAPException {
        return this.inMemoryHandler.entryExists(dn);
    }
    
    public boolean entryExists(final String dn, final String filter) throws LDAPException {
        return this.inMemoryHandler.entryExists(dn, filter);
    }
    
    public boolean entryExists(final Entry entry) throws LDAPException {
        return this.inMemoryHandler.entryExists(entry);
    }
    
    public void assertEntryExists(final String dn) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertEntryExists(dn);
    }
    
    public void assertEntryExists(final String dn, final String filter) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertEntryExists(dn, filter);
    }
    
    public void assertEntryExists(final Entry entry) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertEntryExists(entry);
    }
    
    public List<String> getMissingEntryDNs(final String... dns) throws LDAPException {
        return this.inMemoryHandler.getMissingEntryDNs(StaticUtils.toList(dns));
    }
    
    public List<String> getMissingEntryDNs(final Collection<String> dns) throws LDAPException {
        return this.inMemoryHandler.getMissingEntryDNs(dns);
    }
    
    public void assertEntriesExist(final String... dns) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertEntriesExist(StaticUtils.toList(dns));
    }
    
    public void assertEntriesExist(final Collection<String> dns) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertEntriesExist(dns);
    }
    
    public List<String> getMissingAttributeNames(final String dn, final String... attributeNames) throws LDAPException {
        return this.inMemoryHandler.getMissingAttributeNames(dn, StaticUtils.toList(attributeNames));
    }
    
    public List<String> getMissingAttributeNames(final String dn, final Collection<String> attributeNames) throws LDAPException {
        return this.inMemoryHandler.getMissingAttributeNames(dn, attributeNames);
    }
    
    public void assertAttributeExists(final String dn, final String... attributeNames) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertAttributeExists(dn, StaticUtils.toList(attributeNames));
    }
    
    public void assertAttributeExists(final String dn, final Collection<String> attributeNames) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertAttributeExists(dn, attributeNames);
    }
    
    public List<String> getMissingAttributeValues(final String dn, final String attributeName, final String... attributeValues) throws LDAPException {
        return this.inMemoryHandler.getMissingAttributeValues(dn, attributeName, StaticUtils.toList(attributeValues));
    }
    
    public List<String> getMissingAttributeValues(final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException {
        return this.inMemoryHandler.getMissingAttributeValues(dn, attributeName, attributeValues);
    }
    
    public void assertValueExists(final String dn, final String attributeName, final String... attributeValues) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertValueExists(dn, attributeName, StaticUtils.toList(attributeValues));
    }
    
    public void assertValueExists(final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertValueExists(dn, attributeName, attributeValues);
    }
    
    public void assertEntryMissing(final String dn) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertEntryMissing(dn);
    }
    
    public void assertAttributeMissing(final String dn, final String... attributeNames) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertAttributeMissing(dn, StaticUtils.toList(attributeNames));
    }
    
    public void assertAttributeMissing(final String dn, final Collection<String> attributeNames) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertAttributeMissing(dn, attributeNames);
    }
    
    public void assertValueMissing(final String dn, final String attributeName, final String... attributeValues) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertValueMissing(dn, attributeName, StaticUtils.toList(attributeValues));
    }
    
    public void assertValueMissing(final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException, AssertionError {
        this.inMemoryHandler.assertValueMissing(dn, attributeName, attributeValues);
    }
}
