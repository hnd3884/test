package com.microsoft.sqlserver.jdbc;

import java.util.Hashtable;
import mssql.googlecode.cityhash.CityHash;
import java.sql.ResultSet;
import mssql.googlecode.concurrentlinkedhashmap.EvictionListener;
import java.util.concurrent.TimeUnit;
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.util.Enumeration;
import java.sql.SQLClientInfoException;
import java.sql.Struct;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.Savepoint;
import java.text.DateFormat;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import java.sql.DatabaseMetaData;
import javax.sql.XAConnection;
import java.sql.SQLException;
import java.security.Permission;
import java.sql.SQLPermission;
import java.util.concurrent.Executor;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.net.IDN;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.Iterator;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.function.ToIntFunction;
import java.util.LinkedList;
import java.sql.SQLWarning;
import java.util.UUID;
import java.util.logging.Logger;
import org.ietf.jgss.GSSCredential;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import mssql.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.Serializable;

public class SQLServerConnection implements ISQLServerConnection, Serializable
{
    private static final long serialVersionUID = 1965647556064751510L;
    long timerExpire;
    boolean attemptRefreshTokenLocked;
    static final int DEFAULT_SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD = 10;
    private int serverPreparedStatementDiscardThreshold;
    static final boolean DEFAULT_ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT_CALL = false;
    private Boolean enablePrepareOnFirstPreparedStatementCall;
    private ConcurrentLinkedQueue<PreparedStatementHandle> discardedPreparedStatementHandles;
    private AtomicInteger discardedPreparedStatementHandleCount;
    private SQLServerColumnEncryptionKeyStoreProvider keystoreProvider;
    private boolean fedAuthRequiredByUser;
    private boolean fedAuthRequiredPreLoginResponse;
    private boolean federatedAuthenticationRequested;
    private boolean federatedAuthenticationInfoRequested;
    private FederatedAuthenticationFeatureExtensionData fedAuthFeatureExtensionData;
    private String authenticationString;
    private byte[] accessTokenInByte;
    private SqlFedAuthToken fedAuthToken;
    private String originalHostNameInCertificate;
    final int ENGINE_EDITION_FOR_SQL_AZURE = 5;
    final int ENGINE_EDITION_FOR_SQL_AZURE_DW = 6;
    final int ENGINE_EDITION_FOR_SQL_AZURE_MI = 8;
    private Boolean isAzure;
    private Boolean isAzureDW;
    private Boolean isAzureMI;
    private SharedTimer sharedTimer;
    private static final int PARSED_SQL_CACHE_SIZE = 100;
    private static ConcurrentLinkedHashMap<CityHash128Key, ParsedSQLCacheItem> parsedSQLCache;
    static final int DEFAULT_STATEMENT_POOLING_CACHE_SIZE = 0;
    private int statementPoolingCacheSize;
    private ConcurrentLinkedHashMap<CityHash128Key, PreparedStatementHandle> preparedStatementHandleCache;
    private ConcurrentLinkedHashMap<CityHash128Key, SQLServerParameterMetaData> parameterMetadataCache;
    private boolean disableStatementPooling;
    private static final float TIMEOUTSTEP = 0.08f;
    private static final float TIMEOUTSTEP_TNIR = 0.125f;
    static final int TnirFirstAttemptTimeoutMs = 500;
    private static final int INTERMITTENT_TLS_MAX_RETRY = 5;
    private boolean isRoutedInCurrentAttempt;
    private ServerPortPlaceHolder routingInfo;
    private static final String callAbortPerm = "callAbort";
    private static final String SET_NETWORK_TIMEOUT_PERM = "setNetworkTimeout";
    private boolean sendStringParametersAsUnicode;
    private String hostName;
    private boolean lastUpdateCount;
    private boolean serverNameAsACE;
    private boolean multiSubnetFailover;
    private boolean transparentNetworkIPResolution;
    private ApplicationIntent applicationIntent;
    private int nLockTimeout;
    private String selectMethod;
    private String responseBuffering;
    private int queryTimeoutSeconds;
    private int cancelQueryTimeoutSeconds;
    private int socketTimeoutMilliseconds;
    private boolean useBulkCopyForBatchInsert;
    boolean userSetTNIR;
    private boolean sendTimeAsDatetime;
    private boolean useFmtOnly;
    private byte requestedEncryptionLevel;
    private boolean trustServerCertificate;
    private byte negotiatedEncryptionLevel;
    private String trustManagerClass;
    private String trustManagerConstructorArg;
    static final String RESERVED_PROVIDER_NAME_PREFIX = "MSSQL_";
    String columnEncryptionSetting;
    String enclaveAttestationUrl;
    String enclaveAttestationProtocol;
    String keyStoreAuthentication;
    String keyStoreSecret;
    String keyStoreLocation;
    private ColumnEncryptionVersion serverColumnEncryptionVersion;
    private String enclaveType;
    private boolean serverSupportsDataClassification;
    static Map<String, SQLServerColumnEncryptionKeyStoreProvider> globalSystemColumnEncryptionKeyStoreProviders;
    static Map<String, SQLServerColumnEncryptionKeyStoreProvider> globalCustomColumnEncryptionKeyStoreProviders;
    Map<String, SQLServerColumnEncryptionKeyStoreProvider> systemColumnEncryptionKeyStoreProvider;
    private String trustedServerNameAE;
    private static Map<String, List<String>> columnEncryptionTrustedMasterKeyPaths;
    Properties activeConnectionProperties;
    private boolean integratedSecurity;
    private boolean ntlmAuthentication;
    private byte[] ntlmPasswordHash;
    private AuthenticationScheme intAuthScheme;
    private GSSCredential impersonatedUserCred;
    private boolean isUserCreatedCredential;
    ServerPortPlaceHolder currentConnectPlaceHolder;
    String sqlServerVersion;
    boolean xopenStates;
    private boolean databaseAutoCommitMode;
    private boolean inXATransaction;
    private byte[] transactionDescriptor;
    private boolean rolledBackTransaction;
    private volatile State state;
    static final int maxDecimalPrecision = 38;
    static final int defaultDecimalPrecision = 18;
    final String traceID;
    private int maxFieldSize;
    private int maxRows;
    private SQLCollation databaseCollation;
    private static final AtomicInteger baseConnectionID;
    private String sCatalog;
    private String originalCatalog;
    private int transactionIsolationLevel;
    private SQLServerPooledConnection pooledConnectionParent;
    private SQLServerDatabaseMetaData databaseMetaData;
    private int nNextSavePointId;
    private static final Logger connectionlogger;
    private static final Logger loggerExternal;
    private final String loggingClassName;
    private String failoverPartnerServerProvided;
    private int holdability;
    private int tdsPacketSize;
    private int requestedPacketSize;
    private TDSChannel tdsChannel;
    private TDSCommand currentCommand;
    private int tdsVersion;
    private int serverMajorVersion;
    private SQLServerConnectionPoolProxy proxy;
    private UUID clientConnectionId;
    static final int MAX_SQL_LOGIN_NAME_WCHARS = 128;
    static final int DEFAULTPORT;
    private final transient Object schedulerLock;
    volatile SQLWarning sqlWarnings;
    private final Object warningSynchronization;
    private static final int ENVCHANGE_DATABASE = 1;
    private static final int ENVCHANGE_LANGUAGE = 2;
    private static final int ENVCHANGE_CHARSET = 3;
    private static final int ENVCHANGE_PACKETSIZE = 4;
    private static final int ENVCHANGE_SORTLOCALEID = 5;
    private static final int ENVCHANGE_SORTFLAGS = 6;
    private static final int ENVCHANGE_SQLCOLLATION = 7;
    private static final int ENVCHANGE_XACT_BEGIN = 8;
    private static final int ENVCHANGE_XACT_COMMIT = 9;
    private static final int ENVCHANGE_XACT_ROLLBACK = 10;
    private static final int ENVCHANGE_DTC_ENLIST = 11;
    private static final int ENVCHANGE_DTC_DEFECT = 12;
    private static final int ENVCHANGE_CHANGE_MIRROR = 13;
    private static final int ENVCHANGE_UNUSED_14 = 14;
    private static final int ENVCHANGE_DTC_PROMOTE = 15;
    private static final int ENVCHANGE_DTC_MGR_ADDR = 16;
    private static final int ENVCHANGE_XACT_ENDED = 17;
    private static final int ENVCHANGE_RESET_COMPLETE = 18;
    private static final int ENVCHANGE_USER_INFO = 19;
    private static final int ENVCHANGE_ROUTING = 20;
    private boolean requestStarted;
    private boolean originalDatabaseAutoCommitMode;
    private int originalTransactionIsolationLevel;
    private int originalNetworkTimeout;
    private int originalHoldability;
    private boolean originalSendTimeAsDatetime;
    private int originalStatementPoolingCacheSize;
    private boolean originalDisableStatementPooling;
    private int originalServerPreparedStatementDiscardThreshold;
    private Boolean originalEnablePrepareOnFirstPreparedStatementCall;
    private String originalSCatalog;
    private boolean originalUseBulkCopyForBatchInsert;
    private volatile SQLWarning originalSqlWarnings;
    private List<ISQLServerStatement> openStatements;
    private boolean originalUseFmtOnly;
    int aeVersion;
    static final char[] OUT;
    private static final int BROWSER_PORT = 1434;
    private static long columnEncryptionKeyCacheTtl;
    private ISQLServerEnclaveProvider enclaveProvider;
    
    SharedTimer getSharedTimer() throws SQLServerException {
        if (this.state == State.Closed) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), null, false);
        }
        if (null == this.sharedTimer) {
            this.sharedTimer = SharedTimer.getTimer();
        }
        return this.sharedTimer;
    }
    
    static ParsedSQLCacheItem getCachedParsedSQL(final CityHash128Key key) {
        return SQLServerConnection.parsedSQLCache.get(key);
    }
    
    static ParsedSQLCacheItem parseAndCacheSQL(final CityHash128Key key, final String sql) throws SQLServerException {
        final JDBCSyntaxTranslator translator = new JDBCSyntaxTranslator();
        final String parsedSql = translator.translate(sql);
        final String procName = translator.getProcedureName();
        final boolean returnValueSyntax = translator.hasReturnValueSyntax();
        final int[] parameterPositions = locateParams(parsedSql);
        final ParsedSQLCacheItem cacheItem = new ParsedSQLCacheItem(parsedSql, parameterPositions, procName, returnValueSyntax);
        SQLServerConnection.parsedSQLCache.putIfAbsent(key, cacheItem);
        return cacheItem;
    }
    
    private static int[] locateParams(final String sql) {
        final LinkedList<Integer> parameterPositions = new LinkedList<Integer>();
        int offset = -1;
        while ((offset = ParameterUtils.scanSQLForChar('?', sql, ++offset)) < sql.length()) {
            parameterPositions.add(offset);
        }
        return parameterPositions.stream().mapToInt((ToIntFunction<? super Object>)Integer::valueOf).toArray();
    }
    
    ServerPortPlaceHolder getRoutingInfo() {
        return this.routingInfo;
    }
    
    boolean sendStringParametersAsUnicode() {
        return this.sendStringParametersAsUnicode;
    }
    
    final boolean useLastUpdateCount() {
        return this.lastUpdateCount;
    }
    
    boolean serverNameAsACE() {
        return this.serverNameAsACE;
    }
    
    final boolean getMultiSubnetFailover() {
        return this.multiSubnetFailover;
    }
    
    final boolean getTransparentNetworkIPResolution() {
        return this.transparentNetworkIPResolution;
    }
    
    final ApplicationIntent getApplicationIntent() {
        return this.applicationIntent;
    }
    
    final String getSelectMethod() {
        return this.selectMethod;
    }
    
    final String getResponseBuffering() {
        return this.responseBuffering;
    }
    
    final int getQueryTimeoutSeconds() {
        return this.queryTimeoutSeconds;
    }
    
    final int getCancelQueryTimeoutSeconds() {
        return this.cancelQueryTimeoutSeconds;
    }
    
    final int getSocketTimeoutMilliseconds() {
        return this.socketTimeoutMilliseconds;
    }
    
    public boolean getUseBulkCopyForBatchInsert() {
        return this.useBulkCopyForBatchInsert;
    }
    
    public void setUseBulkCopyForBatchInsert(final boolean useBulkCopyForBatchInsert) {
        this.useBulkCopyForBatchInsert = useBulkCopyForBatchInsert;
    }
    
    @Override
    public final boolean getSendTimeAsDatetime() {
        return !this.isKatmaiOrLater() || this.sendTimeAsDatetime;
    }
    
    final int baseYear() {
        return this.getSendTimeAsDatetime() ? 1970 : 1900;
    }
    
    final byte getRequestedEncryptionLevel() {
        assert -1 != this.requestedEncryptionLevel;
        return this.requestedEncryptionLevel;
    }
    
    final boolean trustServerCertificate() {
        return this.trustServerCertificate;
    }
    
    final byte getNegotiatedEncryptionLevel() {
        assert -1 != this.negotiatedEncryptionLevel;
        return this.negotiatedEncryptionLevel;
    }
    
    final String getTrustManagerClass() {
        assert -1 != this.requestedEncryptionLevel;
        return this.trustManagerClass;
    }
    
    final String getTrustManagerConstructorArg() {
        assert -1 != this.requestedEncryptionLevel;
        return this.trustManagerConstructorArg;
    }
    
    boolean isColumnEncryptionSettingEnabled() {
        return this.columnEncryptionSetting.equalsIgnoreCase(ColumnEncryptionSetting.Enabled.toString());
    }
    
    boolean getServerSupportsColumnEncryption() {
        return this.serverColumnEncryptionVersion.value() > ColumnEncryptionVersion.AE_NotSupported.value();
    }
    
    ColumnEncryptionVersion getServerColumnEncryptionVersion() {
        return this.serverColumnEncryptionVersion;
    }
    
    boolean getServerSupportsDataClassification() {
        return this.serverSupportsDataClassification;
    }
    
    public static synchronized void registerColumnEncryptionKeyStoreProviders(final Map<String, SQLServerColumnEncryptionKeyStoreProvider> clientKeyStoreProviders) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(SQLServerConnection.class.getName(), "registerColumnEncryptionKeyStoreProviders", "Registering Column Encryption Key Store Providers");
        if (null == clientKeyStoreProviders) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_CustomKeyStoreProviderMapNull"), null, 0, false);
        }
        if (null != SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_CustomKeyStoreProviderSetOnce"), null, 0, false);
        }
        SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
        for (final Map.Entry<String, SQLServerColumnEncryptionKeyStoreProvider> entry : clientKeyStoreProviders.entrySet()) {
            final String providerName = entry.getKey();
            if (null == providerName || 0 == providerName.length()) {
                throw new SQLServerException(null, SQLServerException.getErrString("R_EmptyCustomKeyStoreProviderName"), null, 0, false);
            }
            if (providerName.substring(0, 6).equalsIgnoreCase("MSSQL_")) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCustomKeyStoreProviderName"));
                final Object[] msgArgs = { providerName, "MSSQL_" };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            if (null == entry.getValue()) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CustomKeyStoreProviderValueNull"));
                final Object[] msgArgs = { providerName, "MSSQL_" };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders.put(entry.getKey(), entry.getValue());
        }
        SQLServerConnection.loggerExternal.exiting(SQLServerConnection.class.getName(), "registerColumnEncryptionKeyStoreProviders", "Number of Key store providers that are registered:" + SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders.size());
    }
    
    synchronized SQLServerColumnEncryptionKeyStoreProvider getGlobalSystemColumnEncryptionKeyStoreProvider(final String providerName) {
        return (null != SQLServerConnection.globalSystemColumnEncryptionKeyStoreProviders && SQLServerConnection.globalSystemColumnEncryptionKeyStoreProviders.containsKey(providerName)) ? SQLServerConnection.globalSystemColumnEncryptionKeyStoreProviders.get(providerName) : null;
    }
    
    synchronized String getAllGlobalCustomSystemColumnEncryptionKeyStoreProviders() {
        return (null != SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders) ? SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders.keySet().toString() : null;
    }
    
    synchronized String getAllSystemColumnEncryptionKeyStoreProviders() {
        String keyStores = "";
        if (0 != this.systemColumnEncryptionKeyStoreProvider.size()) {
            keyStores = this.systemColumnEncryptionKeyStoreProvider.keySet().toString();
        }
        if (0 != SQLServerConnection.globalSystemColumnEncryptionKeyStoreProviders.size()) {
            keyStores = keyStores + "," + SQLServerConnection.globalSystemColumnEncryptionKeyStoreProviders.keySet().toString();
        }
        return keyStores;
    }
    
    synchronized SQLServerColumnEncryptionKeyStoreProvider getGlobalCustomColumnEncryptionKeyStoreProvider(final String providerName) {
        return (null != SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders && SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders.containsKey(providerName)) ? SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders.get(providerName) : null;
    }
    
    synchronized SQLServerColumnEncryptionKeyStoreProvider getSystemColumnEncryptionKeyStoreProvider(final String providerName) {
        return (null != this.systemColumnEncryptionKeyStoreProvider && this.systemColumnEncryptionKeyStoreProvider.containsKey(providerName)) ? this.systemColumnEncryptionKeyStoreProvider.get(providerName) : null;
    }
    
    synchronized SQLServerColumnEncryptionKeyStoreProvider getColumnEncryptionKeyStoreProvider(final String providerName) throws SQLServerException {
        this.keystoreProvider = this.getSystemColumnEncryptionKeyStoreProvider(providerName);
        if (null == this.keystoreProvider) {
            this.keystoreProvider = this.getGlobalSystemColumnEncryptionKeyStoreProvider(providerName);
        }
        if (null == this.keystoreProvider) {
            this.keystoreProvider = this.getGlobalCustomColumnEncryptionKeyStoreProvider(providerName);
        }
        if (null == this.keystoreProvider) {
            final String systemProviders = this.getAllSystemColumnEncryptionKeyStoreProviders();
            final String customProviders = this.getAllGlobalCustomSystemColumnEncryptionKeyStoreProviders();
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnrecognizedKeyStoreProviderName"));
            final Object[] msgArgs = { providerName, systemProviders, customProviders };
            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
        }
        return this.keystoreProvider;
    }
    
    public static synchronized void setColumnEncryptionTrustedMasterKeyPaths(final Map<String, List<String>> trustedKeyPaths) {
        SQLServerConnection.loggerExternal.entering(SQLServerConnection.class.getName(), "setColumnEncryptionTrustedMasterKeyPaths", "Setting Trusted Master Key Paths");
        SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.clear();
        for (final Map.Entry<String, List<String>> entry : trustedKeyPaths.entrySet()) {
            SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.put(entry.getKey().toUpperCase(), entry.getValue());
        }
        SQLServerConnection.loggerExternal.exiting(SQLServerConnection.class.getName(), "setColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.size());
    }
    
    public static synchronized void updateColumnEncryptionTrustedMasterKeyPaths(final String server, final List<String> trustedKeyPaths) {
        SQLServerConnection.loggerExternal.entering(SQLServerConnection.class.getName(), "updateColumnEncryptionTrustedMasterKeyPaths", "Updating Trusted Master Key Paths");
        SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.put(server.toUpperCase(), trustedKeyPaths);
        SQLServerConnection.loggerExternal.exiting(SQLServerConnection.class.getName(), "updateColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.size());
    }
    
    public static synchronized void removeColumnEncryptionTrustedMasterKeyPaths(final String server) {
        SQLServerConnection.loggerExternal.entering(SQLServerConnection.class.getName(), "removeColumnEncryptionTrustedMasterKeyPaths", "Removing Trusted Master Key Paths");
        SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.remove(server.toUpperCase());
        SQLServerConnection.loggerExternal.exiting(SQLServerConnection.class.getName(), "removeColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.size());
    }
    
    public static synchronized Map<String, List<String>> getColumnEncryptionTrustedMasterKeyPaths() {
        SQLServerConnection.loggerExternal.entering(SQLServerConnection.class.getName(), "getColumnEncryptionTrustedMasterKeyPaths", "Getting Trusted Master Key Paths");
        final Map<String, List<String>> masterKeyPathCopy = new HashMap<String, List<String>>();
        for (final Map.Entry<String, List<String>> entry : SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.entrySet()) {
            masterKeyPathCopy.put(entry.getKey(), entry.getValue());
        }
        SQLServerConnection.loggerExternal.exiting(SQLServerConnection.class.getName(), "getColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + masterKeyPathCopy.size());
        return masterKeyPathCopy;
    }
    
    static synchronized List<String> getColumnEncryptionTrustedMasterKeyPaths(final String server, final Boolean[] hasEntry) {
        if (SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.containsKey(server)) {
            hasEntry[0] = true;
            return SQLServerConnection.columnEncryptionTrustedMasterKeyPaths.get(server);
        }
        hasEntry[0] = false;
        return null;
    }
    
    final boolean rolledBackTransaction() {
        return this.rolledBackTransaction;
    }
    
    private void setState(final State state) {
        this.state = state;
    }
    
    final boolean isSessionUnAvailable() {
        return !this.state.equals(State.Opened);
    }
    
    final void setMaxFieldSize(final int limit) throws SQLServerException {
        if (this.maxFieldSize != limit) {
            if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
                SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
            }
            this.connectionCommand("SET TEXTSIZE " + ((0 == limit) ? Integer.MAX_VALUE : limit), "setMaxFieldSize");
            this.maxFieldSize = limit;
        }
    }
    
    final void initResettableValues() {
        this.rolledBackTransaction = false;
        this.transactionIsolationLevel = 2;
        this.maxFieldSize = 0;
        this.maxRows = 0;
        this.nLockTimeout = -1;
        this.databaseAutoCommitMode = true;
        this.holdability = 1;
        this.sqlWarnings = null;
        this.sCatalog = this.originalCatalog;
        this.databaseMetaData = null;
    }
    
    final void setMaxRows(final int limit) throws SQLServerException {
        if (this.maxRows != limit) {
            if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
                SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
            }
            this.connectionCommand("SET ROWCOUNT " + limit, "setMaxRows");
            this.maxRows = limit;
        }
    }
    
    final SQLCollation getDatabaseCollation() {
        return this.databaseCollation;
    }
    
    final int getHoldabilityInternal() {
        return this.holdability;
    }
    
    final int getTDSPacketSize() {
        return this.tdsPacketSize;
    }
    
    final boolean isKatmaiOrLater() {
        assert 0 != this.tdsVersion;
        assert this.tdsVersion >= 1913192450;
        return this.tdsVersion >= 1930100739;
    }
    
    final boolean isDenaliOrLater() {
        return this.tdsVersion >= 1946157060;
    }
    
    int getServerMajorVersion() {
        return this.serverMajorVersion;
    }
    
    @Override
    public UUID getClientConnectionId() throws SQLServerException {
        this.checkClosed();
        return this.clientConnectionId;
    }
    
    final UUID getClientConIdInternal() {
        return this.clientConnectionId;
    }
    
    final boolean attachConnId() {
        return this.state.equals(State.Connected);
    }
    
    SQLServerConnection(final String parentInfo) throws SQLServerException {
        this.attemptRefreshTokenLocked = false;
        this.serverPreparedStatementDiscardThreshold = -1;
        this.enablePrepareOnFirstPreparedStatementCall = null;
        this.discardedPreparedStatementHandles = new ConcurrentLinkedQueue<PreparedStatementHandle>();
        this.discardedPreparedStatementHandleCount = new AtomicInteger(0);
        this.keystoreProvider = null;
        this.fedAuthRequiredByUser = false;
        this.fedAuthRequiredPreLoginResponse = false;
        this.federatedAuthenticationRequested = false;
        this.federatedAuthenticationInfoRequested = false;
        this.fedAuthFeatureExtensionData = null;
        this.authenticationString = null;
        this.accessTokenInByte = null;
        this.fedAuthToken = null;
        this.originalHostNameInCertificate = null;
        this.isAzure = null;
        this.isAzureDW = null;
        this.isAzureMI = null;
        this.statementPoolingCacheSize = 0;
        this.disableStatementPooling = true;
        this.isRoutedInCurrentAttempt = false;
        this.routingInfo = null;
        this.sendStringParametersAsUnicode = SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue();
        this.hostName = null;
        this.serverNameAsACE = SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue();
        this.applicationIntent = null;
        this.userSetTNIR = true;
        this.sendTimeAsDatetime = SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue();
        this.useFmtOnly = SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue();
        this.requestedEncryptionLevel = -1;
        this.negotiatedEncryptionLevel = -1;
        this.trustManagerClass = null;
        this.trustManagerConstructorArg = null;
        this.columnEncryptionSetting = null;
        this.enclaveAttestationUrl = null;
        this.enclaveAttestationProtocol = null;
        this.keyStoreAuthentication = null;
        this.keyStoreSecret = null;
        this.keyStoreLocation = null;
        this.serverColumnEncryptionVersion = ColumnEncryptionVersion.AE_NotSupported;
        this.enclaveType = null;
        this.serverSupportsDataClassification = false;
        this.systemColumnEncryptionKeyStoreProvider = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
        this.trustedServerNameAE = null;
        this.integratedSecurity = SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.getDefaultValue();
        this.ntlmAuthentication = false;
        this.ntlmPasswordHash = null;
        this.intAuthScheme = AuthenticationScheme.nativeAuthentication;
        this.currentConnectPlaceHolder = null;
        this.inXATransaction = false;
        this.transactionDescriptor = new byte[8];
        this.state = State.Initialized;
        this.sCatalog = "master";
        this.originalCatalog = "master";
        this.nNextSavePointId = 10000;
        this.failoverPartnerServerProvided = null;
        this.tdsPacketSize = 4096;
        this.requestedPacketSize = 8000;
        this.currentCommand = null;
        this.tdsVersion = 0;
        this.clientConnectionId = null;
        this.schedulerLock = new Object();
        this.warningSynchronization = new Object();
        this.requestStarted = false;
        this.aeVersion = 0;
        final int connectionID = nextConnectionID();
        this.traceID = "ConnectionID:" + connectionID;
        this.loggingClassName = "com.microsoft.sqlserver.jdbc.SQLServerConnection:" + connectionID;
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
            SQLServerConnection.connectionlogger.fine(this.toString() + " created by (" + parentInfo + ")");
        }
        this.initResettableValues();
        if (!this.getDisableStatementPooling() && 0 < this.getStatementPoolingCacheSize()) {
            this.prepareCache();
        }
    }
    
    void setFailoverPartnerServerProvided(final String partner) {
        this.failoverPartnerServerProvided = partner;
    }
    
    final void setAssociatedProxy(final SQLServerConnectionPoolProxy proxy) {
        this.proxy = proxy;
    }
    
    final Connection getConnection() {
        if (null != this.proxy) {
            return this.proxy;
        }
        return this;
    }
    
    final void resetPooledConnection() {
        this.tdsChannel.resetPooledConnection();
        this.initResettableValues();
    }
    
    private static int nextConnectionID() {
        return SQLServerConnection.baseConnectionID.incrementAndGet();
    }
    
    Logger getConnectionLogger() {
        return SQLServerConnection.connectionlogger;
    }
    
    String getClassNameLogging() {
        return this.loggingClassName;
    }
    
    @Override
    public String toString() {
        if (null != this.clientConnectionId) {
            return this.traceID + " ClientConnectionId: " + this.clientConnectionId.toString();
        }
        return this.traceID;
    }
    
    void checkClosed() throws SQLServerException {
        if (this.isSessionUnAvailable()) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), null, false);
        }
    }
    
    protected boolean needsReconnect() {
        return null != this.fedAuthToken && Util.checkIfNeedNewAccessToken(this, this.fedAuthToken.expiresOn);
    }
    
    private boolean isBooleanPropertyOn(final String propName, final String propValue) throws SQLServerException {
        if (null == propValue) {
            return false;
        }
        if ("true".equalsIgnoreCase(propValue)) {
            return true;
        }
        if ("false".equalsIgnoreCase(propValue)) {
            return false;
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidBooleanValue"));
        final Object[] msgArgs = { propName };
        SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
        return false;
    }
    
    void validateMaxSQLLoginName(final String propName, final String propValue) throws SQLServerException {
        if (propValue != null && propValue.length() > 128) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_propertyMaximumExceedsChars"));
            final Object[] msgArgs = { propName, Integer.toString(128) };
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
        }
    }
    
    Connection connect(final Properties propsIn, final SQLServerPooledConnection pooledConnection) throws SQLServerException {
        int loginTimeoutSeconds = 0;
        final long start = System.currentTimeMillis();
        int retryAttempt = 0;
        try {
            return this.connectInternal(propsIn, pooledConnection);
        }
        catch (final SQLServerException e) {
            if (7 != e.getDriverErrorCode()) {
                throw e;
            }
            if (0 == retryAttempt) {
                loginTimeoutSeconds = SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue();
                final String sPropValue = propsIn.getProperty(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString());
                if (null != sPropValue && sPropValue.length() > 0) {
                    final int sPropValueInt = Integer.parseInt(sPropValue);
                    if (0 != sPropValueInt) {
                        loginTimeoutSeconds = sPropValueInt;
                    }
                }
            }
            ++retryAttempt;
            final long elapsedSeconds = (System.currentTimeMillis() - start) / 1000L;
            if (5 < retryAttempt) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                    SQLServerConnection.connectionlogger.fine("Connection failed during SSL handshake. Maximum retry attempt (5) reached.  ");
                }
                throw e;
            }
            if (elapsedSeconds >= loginTimeoutSeconds) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                    SQLServerConnection.connectionlogger.fine("Connection failed during SSL handshake. Not retrying as timeout expired.");
                }
                throw e;
            }
            if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                SQLServerConnection.connectionlogger.fine("Connection failed during SSL handshake. Retrying due to an intermittent TLS 1.2 failure issue. Retry attempt = " + retryAttempt + ".");
            }
            return this.connectInternal(propsIn, pooledConnection);
        }
    }
    
    private void registerKeyStoreProviderOnConnection(final String keyStoreAuth, final String keyStoreSecret, final String keyStoreLocation) throws SQLServerException {
        if (null == keyStoreAuth) {
            if (null != keyStoreSecret) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_keyStoreAuthenticationNotSet"));
                final Object[] msgArgs = { "keyStoreSecret" };
                throw new SQLServerException(form.format(msgArgs), (Throwable)null);
            }
            if (null != keyStoreLocation) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_keyStoreAuthenticationNotSet"));
                final Object[] msgArgs = { "keyStoreLocation" };
                throw new SQLServerException(form.format(msgArgs), (Throwable)null);
            }
        }
        else {
            final KeyStoreAuthentication keyStoreAuthentication = KeyStoreAuthentication.valueOfString(keyStoreAuth);
            switch (keyStoreAuthentication) {
                case JavaKeyStorePassword: {
                    if (null == keyStoreSecret || null == keyStoreLocation) {
                        throw new SQLServerException(SQLServerException.getErrString("R_keyStoreSecretOrLocationNotSet"), (Throwable)null);
                    }
                    final SQLServerColumnEncryptionJavaKeyStoreProvider provider = new SQLServerColumnEncryptionJavaKeyStoreProvider(keyStoreLocation, keyStoreSecret.toCharArray());
                    this.systemColumnEncryptionKeyStoreProvider.put(provider.getName(), provider);
                    break;
                }
            }
        }
    }
    
    Connection connectInternal(final Properties propsIn, final SQLServerPooledConnection pooledConnection) throws SQLServerException {
        try {
            this.activeConnectionProperties = (Properties)propsIn.clone();
            this.pooledConnectionParent = pooledConnection;
            final String hostNameInCertificate = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString());
            if (null == this.originalHostNameInCertificate && null != hostNameInCertificate && !hostNameInCertificate.isEmpty()) {
                this.originalHostNameInCertificate = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString());
            }
            if (null != this.originalHostNameInCertificate && !this.originalHostNameInCertificate.isEmpty()) {
                this.activeConnectionProperties.setProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), this.originalHostNameInCertificate);
            }
            String sPropKey = SQLServerDriverStringProperty.USER.toString();
            String sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.USER.getDefaultValue();
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.validateMaxSQLLoginName(sPropKey, sPropValue);
            sPropKey = SQLServerDriverStringProperty.PASSWORD.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.PASSWORD.getDefaultValue();
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.validateMaxSQLLoginName(sPropKey, sPropValue);
            sPropKey = SQLServerDriverStringProperty.DATABASE_NAME.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            this.validateMaxSQLLoginName(sPropKey, sPropValue);
            int loginTimeoutSeconds = SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue();
            sPropValue = this.activeConnectionProperties.getProperty(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString());
            if (null != sPropValue && sPropValue.length() > 0) {
                try {
                    loginTimeoutSeconds = Integer.parseInt(sPropValue);
                }
                catch (final NumberFormatException e) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTimeOut"));
                    final Object[] msgArgs = { sPropValue };
                    SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                }
                if (loginTimeoutSeconds < 0 || loginTimeoutSeconds > 65535) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidTimeOut"));
                    final Object[] msgArgs2 = { sPropValue };
                    SQLServerException.makeFromDriverError(this, this, form2.format(msgArgs2), null, false);
                }
            }
            sPropKey = SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.serverNameAsACE = this.isBooleanPropertyOn(sPropKey, sPropValue);
            sPropKey = SQLServerDriverStringProperty.SERVER_NAME.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = "localhost";
            }
            final String sPropKeyPort = SQLServerDriverIntProperty.PORT_NUMBER.toString();
            final String sPropValuePort = this.activeConnectionProperties.getProperty(sPropKeyPort);
            final int px = sPropValue.indexOf(92);
            String instanceValue = null;
            final String instanceNameProperty = SQLServerDriverStringProperty.INSTANCE_NAME.toString();
            if (px >= 0) {
                instanceValue = sPropValue.substring(px + 1, sPropValue.length());
                this.validateMaxSQLLoginName(instanceNameProperty, instanceValue);
                sPropValue = sPropValue.substring(0, px);
            }
            this.trustedServerNameAE = sPropValue;
            if (this.serverNameAsACE) {
                try {
                    sPropValue = IDN.toASCII(sPropValue);
                }
                catch (final IllegalArgumentException ex) {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
                    final Object[] msgArgs3 = { "serverNameAsACE", sPropValue };
                    throw new SQLServerException(form3.format(msgArgs3), ex);
                }
            }
            this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            final String instanceValueFromProp = this.activeConnectionProperties.getProperty(instanceNameProperty);
            if (null != instanceValueFromProp) {
                instanceValue = instanceValueFromProp;
            }
            if (instanceValue != null) {
                this.validateMaxSQLLoginName(instanceNameProperty, instanceValue);
                this.activeConnectionProperties.setProperty(instanceNameProperty, instanceValue);
                this.trustedServerNameAE = this.trustedServerNameAE + "\\" + instanceValue;
            }
            if (null != sPropValuePort) {
                this.trustedServerNameAE = this.trustedServerNameAE + ":" + sPropValuePort;
            }
            sPropKey = SQLServerDriverStringProperty.APPLICATION_NAME.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.validateMaxSQLLoginName(sPropKey, sPropValue);
            }
            else {
                this.activeConnectionProperties.setProperty(sPropKey, "Microsoft JDBC Driver for SQL Server");
            }
            sPropKey = SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            sPropKey = SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.COLUMN_ENCRYPTION.getDefaultValue();
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.columnEncryptionSetting = ColumnEncryptionSetting.valueOfString(sPropValue).toString();
            sPropKey = SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.enclaveAttestationUrl = sPropValue;
            }
            sPropKey = SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.enclaveAttestationProtocol = sPropValue;
                if (!AttestationProtocol.isValidAttestationProtocol(this.enclaveAttestationProtocol)) {
                    throw new SQLServerException(SQLServerException.getErrString("R_enclaveInvalidAttestationProtocol"), (Throwable)null);
                }
                if (this.enclaveAttestationProtocol.equalsIgnoreCase(AttestationProtocol.HGS.toString())) {
                    this.enclaveProvider = new SQLServerVSMEnclaveProvider();
                }
                else {
                    this.enclaveProvider = new SQLServerAASEnclaveProvider();
                }
            }
            if ((null != this.enclaveAttestationUrl && !this.enclaveAttestationUrl.isEmpty() && (null == this.enclaveAttestationProtocol || this.enclaveAttestationProtocol.isEmpty())) || (null != this.enclaveAttestationProtocol && !this.enclaveAttestationProtocol.isEmpty() && (null == this.enclaveAttestationUrl || this.enclaveAttestationUrl.isEmpty())) || (null != this.enclaveAttestationUrl && !this.enclaveAttestationUrl.isEmpty() && (null != this.enclaveAttestationProtocol || !this.enclaveAttestationProtocol.isEmpty()) && (null == this.columnEncryptionSetting || !this.isColumnEncryptionSettingEnabled()))) {
                throw new SQLServerException(SQLServerException.getErrString("R_enclavePropertiesError"), (Throwable)null);
            }
            sPropKey = SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.keyStoreAuthentication = KeyStoreAuthentication.valueOfString(sPropValue).toString();
            }
            sPropKey = SQLServerDriverStringProperty.KEY_STORE_SECRET.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.keyStoreSecret = sPropValue;
            }
            sPropKey = SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.keyStoreLocation = sPropValue;
            }
            this.registerKeyStoreProviderOnConnection(this.keyStoreAuthentication, this.keyStoreSecret, this.keyStoreLocation);
            if (null == SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders) {
                sPropKey = SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    final String keyVaultColumnEncryptionProviderClientId = sPropValue;
                    sPropKey = SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.toString();
                    sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                    if (null != sPropValue) {
                        final String keyVaultColumnEncryptionProviderClientKey = sPropValue;
                        final SQLServerColumnEncryptionAzureKeyVaultProvider akvProvider = new SQLServerColumnEncryptionAzureKeyVaultProvider(keyVaultColumnEncryptionProviderClientId, keyVaultColumnEncryptionProviderClientKey);
                        final Map<String, SQLServerColumnEncryptionKeyStoreProvider> keyStoreMap = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
                        keyStoreMap.put(akvProvider.getName(), akvProvider);
                        registerColumnEncryptionKeyStoreProviders(keyStoreMap);
                    }
                }
            }
            sPropKey = SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.multiSubnetFailover = this.isBooleanPropertyOn(sPropKey, sPropValue);
            sPropKey = SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                this.userSetTNIR = false;
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.transparentNetworkIPResolution = this.isBooleanPropertyOn(sPropKey, sPropValue);
            sPropKey = SQLServerDriverBooleanProperty.ENCRYPT.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.ENCRYPT.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.requestedEncryptionLevel = (byte)(this.isBooleanPropertyOn(sPropKey, sPropValue) ? 1 : 0);
            sPropKey = SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.trustServerCertificate = this.isBooleanPropertyOn(sPropKey, sPropValue);
            this.trustManagerClass = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString());
            this.trustManagerConstructorArg = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString());
            sPropKey = SQLServerDriverStringProperty.SELECT_METHOD.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.SELECT_METHOD.getDefaultValue();
            }
            if ("cursor".equalsIgnoreCase(sPropValue) || "direct".equalsIgnoreCase(sPropValue)) {
                sPropValue = sPropValue.toLowerCase(Locale.ENGLISH);
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                this.selectMethod = sPropValue;
            }
            else {
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_invalidselectMethod"));
                final Object[] msgArgs3 = { sPropValue };
                SQLServerException.makeFromDriverError(this, this, form3.format(msgArgs3), null, false);
            }
            sPropKey = SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.RESPONSE_BUFFERING.getDefaultValue();
            }
            if ("full".equalsIgnoreCase(sPropValue) || "adaptive".equalsIgnoreCase(sPropValue)) {
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue.toLowerCase(Locale.ENGLISH));
            }
            else {
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_invalidresponseBuffering"));
                final Object[] msgArgs3 = { sPropValue };
                SQLServerException.makeFromDriverError(this, this, form3.format(msgArgs3), null, false);
            }
            sPropKey = SQLServerDriverStringProperty.APPLICATION_INTENT.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.APPLICATION_INTENT.getDefaultValue();
            }
            this.applicationIntent = ApplicationIntent.valueOfString(sPropValue);
            this.activeConnectionProperties.setProperty(sPropKey, this.applicationIntent.toString());
            sPropKey = SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.sendTimeAsDatetime = this.isBooleanPropertyOn(sPropKey, sPropValue);
            sPropKey = SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue());
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            this.useFmtOnly = this.isBooleanPropertyOn(sPropKey, sPropValue);
            sPropKey = SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString();
            if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                try {
                    final int n = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                    this.setStatementPoolingCacheSize(n);
                }
                catch (final NumberFormatException e2) {
                    final MessageFormat form4 = new MessageFormat(SQLServerException.getErrString("R_statementPoolingCacheSize"));
                    final Object[] msgArgs4 = { this.activeConnectionProperties.getProperty(sPropKey) };
                    SQLServerException.makeFromDriverError(this, this, form4.format(msgArgs4), null, false);
                }
            }
            sPropKey = SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.setDisableStatementPooling(this.isBooleanPropertyOn(sPropKey, sPropValue));
            }
            sPropKey = SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.integratedSecurity = this.isBooleanPropertyOn(sPropKey, sPropValue);
            }
            if (this.integratedSecurity) {
                sPropKey = SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    this.intAuthScheme = AuthenticationScheme.valueOfString(sPropValue);
                }
            }
            if (this.intAuthScheme == AuthenticationScheme.javaKerberos) {
                sPropKey = SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString();
                if (this.activeConnectionProperties.containsKey(sPropKey)) {
                    this.impersonatedUserCred = ((Hashtable<K, GSSCredential>)this.activeConnectionProperties).get(sPropKey);
                    this.isUserCreatedCredential = true;
                }
            }
            else if (this.intAuthScheme == AuthenticationScheme.ntlm) {
                final String sPropKeyDomain = SQLServerDriverStringProperty.DOMAIN.toString();
                final String sPropValueDomain = this.activeConnectionProperties.getProperty(sPropKeyDomain);
                if (null == sPropValueDomain) {
                    this.activeConnectionProperties.setProperty(sPropKeyDomain, SQLServerDriverStringProperty.DOMAIN.getDefaultValue());
                }
                if (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty()) {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                        SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_NtlmNoUserPasswordDomain"));
                    }
                    throw new SQLServerException(SQLServerException.getErrString("R_NtlmNoUserPasswordDomain"), (Throwable)null);
                }
                this.ntlmAuthentication = true;
            }
            sPropKey = SQLServerDriverStringProperty.AUTHENTICATION.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.AUTHENTICATION.getDefaultValue();
            }
            this.authenticationString = SqlAuthentication.valueOfString(sPropValue).toString().trim();
            if (this.integratedSecurity && !this.authenticationString.equalsIgnoreCase(SqlAuthentication.NotSpecified.toString())) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_SetAuthenticationWhenIntegratedSecurityTrue"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_SetAuthenticationWhenIntegratedSecurityTrue"), (Throwable)null);
            }
            if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryIntegrated.toString()) && (!this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || !this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_IntegratedAuthenticationWithUserPassword"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_IntegratedAuthenticationWithUserPassword"), (Throwable)null);
            }
            if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryPassword.toString()) && (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_NoUserPasswordForActivePassword"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_NoUserPasswordForActivePassword"), (Throwable)null);
            }
            if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryMSI.toString()) && (!this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || !this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_MSIAuthenticationWithUserPassword"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_MSIAuthenticationWithUserPassword"), (Throwable)null);
            }
            if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.SqlPassword.toString()) && (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_NoUserPasswordForSqlPassword"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_NoUserPasswordForSqlPassword"), (Throwable)null);
            }
            sPropKey = SQLServerDriverStringProperty.ACCESS_TOKEN.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.accessTokenInByte = sPropValue.getBytes(StandardCharsets.UTF_16LE);
            }
            if (null != this.accessTokenInByte && 0 == this.accessTokenInByte.length) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_AccessTokenCannotBeEmpty"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_AccessTokenCannotBeEmpty"), (Throwable)null);
            }
            if (this.integratedSecurity && null != this.accessTokenInByte) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_SetAccesstokenWhenIntegratedSecurityTrue"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_SetAccesstokenWhenIntegratedSecurityTrue"), (Throwable)null);
            }
            if (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.NotSpecified.toString()) && null != this.accessTokenInByte) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_SetBothAuthenticationAndAccessToken"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_SetBothAuthenticationAndAccessToken"), (Throwable)null);
            }
            if (null != this.accessTokenInByte && (!this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || !this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + " " + SQLServerException.getErrString("R_AccessTokenWithUserPassword"));
                }
                throw new SQLServerException(SQLServerException.getErrString("R_AccessTokenWithUserPassword"), (Throwable)null);
            }
            if (!this.userSetTNIR && (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.NotSpecified.toString()) || null != this.accessTokenInByte)) {
                this.transparentNetworkIPResolution = false;
            }
            sPropKey = SQLServerDriverStringProperty.WORKSTATION_ID.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            this.validateMaxSQLLoginName(sPropKey, sPropValue);
            int nPort = 0;
            sPropKey = SQLServerDriverIntProperty.PORT_NUMBER.toString();
            try {
                final String strPort = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != strPort) {
                    nPort = Integer.parseInt(strPort);
                    if (nPort < 0 || nPort > 65535) {
                        final MessageFormat form5 = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                        final Object[] msgArgs5 = { Integer.toString(nPort) };
                        SQLServerException.makeFromDriverError(this, this, form5.format(msgArgs5), null, false);
                    }
                }
            }
            catch (final NumberFormatException e3) {
                final MessageFormat form5 = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                final Object[] msgArgs5 = { this.activeConnectionProperties.getProperty(sPropKey) };
                SQLServerException.makeFromDriverError(this, this, form5.format(msgArgs5), null, false);
            }
            sPropKey = SQLServerDriverIntProperty.PACKET_SIZE.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue && sPropValue.length() > 0) {
                try {
                    this.requestedPacketSize = Integer.parseInt(sPropValue);
                    if (-1 == this.requestedPacketSize) {
                        this.requestedPacketSize = 0;
                    }
                    else if (0 == this.requestedPacketSize) {
                        this.requestedPacketSize = 32767;
                    }
                }
                catch (final NumberFormatException e3) {
                    this.requestedPacketSize = -1;
                }
                if (0 != this.requestedPacketSize && (this.requestedPacketSize < 512 || this.requestedPacketSize > 32767)) {
                    final MessageFormat form4 = new MessageFormat(SQLServerException.getErrString("R_invalidPacketSize"));
                    final Object[] msgArgs4 = { sPropValue };
                    SQLServerException.makeFromDriverError(this, this, form4.format(msgArgs4), null, false);
                }
            }
            sPropKey = SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString();
            this.sendStringParametersAsUnicode = ((null == this.activeConnectionProperties.getProperty(sPropKey)) ? SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue() : this.isBooleanPropertyOn(sPropKey, this.activeConnectionProperties.getProperty(sPropKey)));
            sPropKey = SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString();
            this.lastUpdateCount = this.isBooleanPropertyOn(sPropKey, this.activeConnectionProperties.getProperty(sPropKey));
            sPropKey = SQLServerDriverBooleanProperty.XOPEN_STATES.toString();
            this.xopenStates = this.isBooleanPropertyOn(sPropKey, this.activeConnectionProperties.getProperty(sPropKey));
            sPropKey = SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString();
            this.responseBuffering = ((null != this.activeConnectionProperties.getProperty(sPropKey) && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) ? this.activeConnectionProperties.getProperty(sPropKey) : null);
            sPropKey = SQLServerDriverIntProperty.LOCK_TIMEOUT.toString();
            final int defaultLockTimeOut = SQLServerDriverIntProperty.LOCK_TIMEOUT.getDefaultValue();
            this.nLockTimeout = defaultLockTimeOut;
            if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                try {
                    final int n2 = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                    if (n2 >= defaultLockTimeOut) {
                        this.nLockTimeout = n2;
                    }
                    else {
                        final MessageFormat form6 = new MessageFormat(SQLServerException.getErrString("R_invalidLockTimeOut"));
                        final Object[] msgArgs6 = { this.activeConnectionProperties.getProperty(sPropKey) };
                        SQLServerException.makeFromDriverError(this, this, form6.format(msgArgs6), null, false);
                    }
                }
                catch (final NumberFormatException e4) {
                    final MessageFormat form6 = new MessageFormat(SQLServerException.getErrString("R_invalidLockTimeOut"));
                    final Object[] msgArgs6 = { this.activeConnectionProperties.getProperty(sPropKey) };
                    SQLServerException.makeFromDriverError(this, this, form6.format(msgArgs6), null, false);
                }
            }
            sPropKey = SQLServerDriverIntProperty.QUERY_TIMEOUT.toString();
            final int defaultQueryTimeout = SQLServerDriverIntProperty.QUERY_TIMEOUT.getDefaultValue();
            this.queryTimeoutSeconds = defaultQueryTimeout;
            if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                try {
                    final int n3 = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                    if (n3 >= defaultQueryTimeout) {
                        this.queryTimeoutSeconds = n3;
                    }
                    else {
                        final MessageFormat form7 = new MessageFormat(SQLServerException.getErrString("R_invalidQueryTimeout"));
                        final Object[] msgArgs7 = { this.activeConnectionProperties.getProperty(sPropKey) };
                        SQLServerException.makeFromDriverError(this, this, form7.format(msgArgs7), null, false);
                    }
                }
                catch (final NumberFormatException e5) {
                    final MessageFormat form7 = new MessageFormat(SQLServerException.getErrString("R_invalidQueryTimeout"));
                    final Object[] msgArgs7 = { this.activeConnectionProperties.getProperty(sPropKey) };
                    SQLServerException.makeFromDriverError(this, this, form7.format(msgArgs7), null, false);
                }
            }
            sPropKey = SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString();
            final int defaultSocketTimeout = SQLServerDriverIntProperty.SOCKET_TIMEOUT.getDefaultValue();
            this.socketTimeoutMilliseconds = defaultSocketTimeout;
            if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                try {
                    final int n4 = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                    if (n4 >= defaultSocketTimeout) {
                        this.socketTimeoutMilliseconds = n4;
                    }
                    else {
                        final MessageFormat form8 = new MessageFormat(SQLServerException.getErrString("R_invalidSocketTimeout"));
                        final Object[] msgArgs8 = { this.activeConnectionProperties.getProperty(sPropKey) };
                        SQLServerException.makeFromDriverError(this, this, form8.format(msgArgs8), null, false);
                    }
                }
                catch (final NumberFormatException e6) {
                    final MessageFormat form8 = new MessageFormat(SQLServerException.getErrString("R_invalidSocketTimeout"));
                    final Object[] msgArgs8 = { this.activeConnectionProperties.getProperty(sPropKey) };
                    SQLServerException.makeFromDriverError(this, this, form8.format(msgArgs8), null, false);
                }
            }
            sPropKey = SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString();
            final int cancelQueryTimeout = SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.getDefaultValue();
            if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                try {
                    final int n5 = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                    if (n5 >= cancelQueryTimeout) {
                        if (this.queryTimeoutSeconds > defaultQueryTimeout) {
                            this.cancelQueryTimeoutSeconds = n5;
                        }
                    }
                    else {
                        final MessageFormat form9 = new MessageFormat(SQLServerException.getErrString("R_invalidCancelQueryTimeout"));
                        final Object[] msgArgs9 = { this.activeConnectionProperties.getProperty(sPropKey) };
                        SQLServerException.makeFromDriverError(this, this, form9.format(msgArgs9), null, false);
                    }
                }
                catch (final NumberFormatException e7) {
                    final MessageFormat form9 = new MessageFormat(SQLServerException.getErrString("R_invalidCancelQueryTimeout"));
                    final Object[] msgArgs9 = { this.activeConnectionProperties.getProperty(sPropKey) };
                    SQLServerException.makeFromDriverError(this, this, form9.format(msgArgs9), null, false);
                }
            }
            sPropKey = SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString();
            if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                try {
                    final int n5 = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                    this.setServerPreparedStatementDiscardThreshold(n5);
                }
                catch (final NumberFormatException e7) {
                    final MessageFormat form9 = new MessageFormat(SQLServerException.getErrString("R_serverPreparedStatementDiscardThreshold"));
                    final Object[] msgArgs9 = { this.activeConnectionProperties.getProperty(sPropKey) };
                    SQLServerException.makeFromDriverError(this, this, form9.format(msgArgs9), null, false);
                }
            }
            sPropKey = SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.setEnablePrepareOnFirstPreparedStatementCall(this.isBooleanPropertyOn(sPropKey, sPropValue));
            }
            sPropKey = SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.useBulkCopyForBatchInsert = this.isBooleanPropertyOn(sPropKey, sPropValue);
            }
            sPropKey = SQLServerDriverStringProperty.SSL_PROTOCOL.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null == sPropValue) {
                sPropValue = SQLServerDriverStringProperty.SSL_PROTOCOL.getDefaultValue();
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            else {
                this.activeConnectionProperties.setProperty(sPropKey, SSLProtocol.valueOfString(sPropValue).toString());
            }
            sPropKey = SQLServerDriverStringProperty.MSI_CLIENT_ID.toString();
            sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
            if (null != sPropValue) {
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
            }
            FailoverInfo fo = null;
            final String databaseNameProperty = SQLServerDriverStringProperty.DATABASE_NAME.toString();
            final String serverNameProperty = SQLServerDriverStringProperty.SERVER_NAME.toString();
            final String failOverPartnerProperty = SQLServerDriverStringProperty.FAILOVER_PARTNER.toString();
            final String failOverPartnerPropertyValue = this.activeConnectionProperties.getProperty(failOverPartnerProperty);
            if (this.multiSubnetFailover && failOverPartnerPropertyValue != null) {
                SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_dbMirroringWithMultiSubnetFailover"), null, false);
            }
            if ((this.multiSubnetFailover || null != failOverPartnerPropertyValue) && !this.userSetTNIR) {
                this.transparentNetworkIPResolution = false;
            }
            if (this.applicationIntent != null && this.applicationIntent.equals(ApplicationIntent.READ_ONLY) && failOverPartnerPropertyValue != null) {
                SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_dbMirroringWithReadOnlyIntent"), null, false);
            }
            if (null != this.activeConnectionProperties.getProperty(databaseNameProperty)) {
                fo = FailoverMapSingleton.getFailoverInfo(this, this.activeConnectionProperties.getProperty(serverNameProperty), this.activeConnectionProperties.getProperty(instanceNameProperty), this.activeConnectionProperties.getProperty(databaseNameProperty));
            }
            else if (null != failOverPartnerPropertyValue) {
                SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_failoverPartnerWithoutDB"), null, true);
            }
            final String mirror = (null == fo) ? failOverPartnerPropertyValue : null;
            final long startTime = System.currentTimeMillis();
            this.login(this.activeConnectionProperties.getProperty(serverNameProperty), instanceValue, nPort, mirror, fo, loginTimeoutSeconds, startTime);
            if (1 == this.negotiatedEncryptionLevel || 3 == this.negotiatedEncryptionLevel) {
                final int sslRecordSize = Util.isIBM() ? 8192 : 16384;
                if (this.tdsPacketSize > sslRecordSize) {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.finer(this.toString() + " Negotiated tdsPacketSize " + this.tdsPacketSize + " is too large for SSL with JRE " + Util.SYSTEM_JRE + " (max size is " + sslRecordSize + ")");
                    }
                    final MessageFormat form10 = new MessageFormat(SQLServerException.getErrString("R_packetSizeTooBigForSSL"));
                    final Object[] msgArgs10 = { Integer.toString(sslRecordSize) };
                    this.terminate(6, form10.format(msgArgs10));
                }
            }
            this.state = State.Opened;
            if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                SQLServerConnection.connectionlogger.finer(this.toString() + " End of connect");
            }
        }
        finally {
            if (!this.state.equals(State.Opened) && !this.state.equals(State.Closed)) {
                this.close();
            }
        }
        return this;
    }
    
    private void login(final String primary, final String primaryInstanceName, final int primaryPortNumber, final String mirror, final FailoverInfo foActual, int timeout, final long timerStart) throws SQLServerException {
        final boolean isDBMirroring = null != mirror || null != foActual;
        int sleepInterval = 100;
        boolean useFailoverHost = false;
        FailoverInfo tempFailover = null;
        ServerPortPlaceHolder currentFOPlaceHolder = null;
        ServerPortPlaceHolder currentPrimaryPlaceHolder = null;
        if (null != foActual) {
            tempFailover = foActual;
            useFailoverHost = foActual.getUseFailoverPartner();
        }
        else if (isDBMirroring) {
            tempFailover = new FailoverInfo(mirror, this, false);
        }
        boolean useParallel = this.getMultiSubnetFailover();
        boolean useTnir = this.getTransparentNetworkIPResolution();
        if (0 == timeout) {
            timeout = SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue();
        }
        final long timerTimeout = timeout * 1000L;
        this.timerExpire = timerStart + timerTimeout;
        long timeoutUnitInterval;
        if (isDBMirroring || useParallel) {
            timeoutUnitInterval = (long)(0.08f * timerTimeout);
        }
        else if (useTnir) {
            timeoutUnitInterval = (long)(0.125f * timerTimeout);
        }
        else {
            timeoutUnitInterval = timerTimeout;
        }
        long intervalExpire = timerStart + timeoutUnitInterval;
        final long intervalExpireFullTimeout = timerStart + timerTimeout;
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
            SQLServerConnection.connectionlogger.finer(this.toString() + " Start time: " + timerStart + " Time out time: " + this.timerExpire + " Timeout Unit Interval: " + timeoutUnitInterval);
        }
        int attemptNumber = 0;
        int noOfRedirections = 0;
        while (true) {
            this.clientConnectionId = null;
            this.state = State.Initialized;
            try {
                if (isDBMirroring && useFailoverHost) {
                    if (null == currentFOPlaceHolder) {
                        currentFOPlaceHolder = tempFailover.failoverPermissionCheck(this, this.integratedSecurity);
                    }
                    this.currentConnectPlaceHolder = currentFOPlaceHolder;
                }
                else {
                    if (this.routingInfo != null) {
                        currentPrimaryPlaceHolder = this.routingInfo;
                        this.routingInfo = null;
                    }
                    else if (null == currentPrimaryPlaceHolder) {
                        currentPrimaryPlaceHolder = this.primaryPermissionCheck(primary, primaryInstanceName, primaryPortNumber);
                    }
                    this.currentConnectPlaceHolder = currentPrimaryPlaceHolder;
                }
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                    SQLServerConnection.connectionlogger.fine(this.toString() + " This attempt server name: " + this.currentConnectPlaceHolder.getServerName() + " port: " + this.currentConnectPlaceHolder.getPortNumber() + " InstanceName: " + this.currentConnectPlaceHolder.getInstanceName() + " useParallel: " + useParallel);
                    SQLServerConnection.connectionlogger.fine(this.toString() + " This attempt endtime: " + intervalExpire);
                    SQLServerConnection.connectionlogger.fine(this.toString() + " This attempt No: " + attemptNumber);
                }
                this.connectHelper(this.currentConnectPlaceHolder, timerRemaining(intervalExpire), timeout, useParallel, useTnir, 0 == attemptNumber, timerRemaining(intervalExpireFullTimeout));
                if (!this.isRoutedInCurrentAttempt) {
                    break;
                }
                if (isDBMirroring) {
                    final String msg = SQLServerException.getErrString("R_invalidRoutingInfo");
                    this.terminate(6, msg);
                }
                if (++noOfRedirections > 1) {
                    final String msg = SQLServerException.getErrString("R_multipleRedirections");
                    this.terminate(6, msg);
                }
                if (this.tdsChannel != null) {
                    this.tdsChannel.close();
                }
                this.initResettableValues();
                this.resetNonRoutingEnvchangeValues();
                ++attemptNumber;
                this.isRoutedInCurrentAttempt = false;
                useParallel = false;
                useTnir = false;
                intervalExpire = this.timerExpire;
                if (!timerHasExpired(this.timerExpire)) {
                    continue;
                }
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                final Object[] msgArgs = { this.currentConnectPlaceHolder.getServerName(), Integer.toString(this.currentConnectPlaceHolder.getPortNumber()), SQLServerException.getErrString("R_timedOutBeforeRouting") };
                final String msg2 = form.format(msgArgs);
                this.terminate(6, msg2);
            }
            catch (final SQLServerException sqlex) {
                if (18456 == sqlex.getErrorCode() || 18488 == sqlex.getErrorCode() || 18486 == sqlex.getErrorCode() || 4 == sqlex.getDriverErrorCode() || 5 == sqlex.getDriverErrorCode() || 7 == sqlex.getDriverErrorCode() || 6 == sqlex.getDriverErrorCode() || 8 == sqlex.getDriverErrorCode() || timerHasExpired(this.timerExpire) || (this.state.equals(State.Connected) && !isDBMirroring)) {
                    this.close();
                    throw sqlex;
                }
                if (null != this.tdsChannel) {
                    this.tdsChannel.close();
                }
                if (!isDBMirroring || 1 == attemptNumber % 2) {
                    final long remainingMilliseconds = timerRemaining(this.timerExpire);
                    if (remainingMilliseconds <= sleepInterval) {
                        throw sqlex;
                    }
                }
            }
            if (!isDBMirroring || 1 == attemptNumber % 2) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                    SQLServerConnection.connectionlogger.fine(this.toString() + " sleeping milisec: " + sleepInterval);
                }
                try {
                    Thread.sleep(sleepInterval);
                }
                catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                sleepInterval = ((sleepInterval < 500) ? (sleepInterval * 2) : 1000);
            }
            ++attemptNumber;
            if (useParallel) {
                intervalExpire = System.currentTimeMillis() + timeoutUnitInterval * (attemptNumber + 1);
            }
            else if (isDBMirroring) {
                intervalExpire = System.currentTimeMillis() + timeoutUnitInterval * (attemptNumber / 2 + 1);
            }
            else if (useTnir) {
                long timeSlice = timeoutUnitInterval * (1 << attemptNumber);
                if (1 == attemptNumber && 500L > timeSlice) {
                    timeSlice = 500L;
                }
                intervalExpire = System.currentTimeMillis() + timeSlice;
            }
            else {
                intervalExpire = this.timerExpire;
            }
            if (intervalExpire > this.timerExpire) {
                intervalExpire = this.timerExpire;
            }
            if (isDBMirroring) {
                useFailoverHost = !useFailoverHost;
            }
        }
        if (useFailoverHost && null == this.failoverPartnerServerProvided) {
            String curserverinfo = this.currentConnectPlaceHolder.getServerName();
            if (null != currentFOPlaceHolder.getInstanceName()) {
                curserverinfo += "\\";
                curserverinfo += currentFOPlaceHolder.getInstanceName();
            }
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidPartnerConfiguration"));
            final Object[] msgArgs2 = { this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.DATABASE_NAME.toString()), curserverinfo };
            this.terminate(6, form2.format(msgArgs2));
        }
        if (null != this.failoverPartnerServerProvided) {
            if (this.multiSubnetFailover) {
                final String msg = SQLServerException.getErrString("R_dbMirroringWithMultiSubnetFailover");
                this.terminate(6, msg);
            }
            if (this.applicationIntent != null && this.applicationIntent.equals(ApplicationIntent.READ_ONLY)) {
                final String msg = SQLServerException.getErrString("R_dbMirroringWithReadOnlyIntent");
                this.terminate(6, msg);
            }
            if (null == tempFailover) {
                tempFailover = new FailoverInfo(this.failoverPartnerServerProvided, this, false);
            }
            if (null != foActual) {
                foActual.failoverAdd(this, useFailoverHost, this.failoverPartnerServerProvided);
            }
            else {
                final String databaseNameProperty = SQLServerDriverStringProperty.DATABASE_NAME.toString();
                final String instanceNameProperty = SQLServerDriverStringProperty.INSTANCE_NAME.toString();
                final String serverNameProperty = SQLServerDriverStringProperty.SERVER_NAME.toString();
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                    SQLServerConnection.connectionlogger.fine(this.toString() + " adding new failover info server: " + this.activeConnectionProperties.getProperty(serverNameProperty) + " instance: " + this.activeConnectionProperties.getProperty(instanceNameProperty) + " database: " + this.activeConnectionProperties.getProperty(databaseNameProperty) + " server provided failover: " + this.failoverPartnerServerProvided);
                }
                tempFailover.failoverAdd(this, useFailoverHost, this.failoverPartnerServerProvided);
                FailoverMapSingleton.putFailoverInfo(this, primary, this.activeConnectionProperties.getProperty(instanceNameProperty), this.activeConnectionProperties.getProperty(databaseNameProperty), tempFailover, useFailoverHost, this.failoverPartnerServerProvided);
            }
        }
    }
    
    void resetNonRoutingEnvchangeValues() {
        this.tdsPacketSize = 4096;
        this.databaseCollation = null;
        this.rolledBackTransaction = false;
        Arrays.fill(this.getTransactionDescriptor(), (byte)0);
        this.sCatalog = this.originalCatalog;
        this.failoverPartnerServerProvided = null;
    }
    
    ServerPortPlaceHolder primaryPermissionCheck(final String primary, final String primaryInstanceName, int primaryPortNumber) throws SQLServerException {
        if (0 == primaryPortNumber) {
            if (null != primaryInstanceName) {
                final String instancePort = this.getInstancePort(primary, primaryInstanceName);
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    SQLServerConnection.connectionlogger.fine(this.toString() + " SQL Server port returned by SQL Browser: " + instancePort);
                }
                try {
                    if (null != instancePort) {
                        primaryPortNumber = Integer.parseInt(instancePort);
                        if (primaryPortNumber < 0 || primaryPortNumber > 65535) {
                            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                            final Object[] msgArgs = { Integer.toString(primaryPortNumber) };
                            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                        }
                    }
                    else {
                        primaryPortNumber = SQLServerConnection.DEFAULTPORT;
                    }
                }
                catch (final NumberFormatException e) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                    final Object[] msgArgs2 = { primaryPortNumber };
                    SQLServerException.makeFromDriverError(this, this, form2.format(msgArgs2), null, false);
                }
            }
            else {
                primaryPortNumber = SQLServerConnection.DEFAULTPORT;
            }
        }
        this.activeConnectionProperties.setProperty(SQLServerDriverIntProperty.PORT_NUMBER.toString(), String.valueOf(primaryPortNumber));
        return new ServerPortPlaceHolder(primary, primaryPortNumber, primaryInstanceName, this.integratedSecurity);
    }
    
    static boolean timerHasExpired(final long timerExpire) {
        return System.currentTimeMillis() > timerExpire;
    }
    
    static int timerRemaining(final long timerExpire) {
        final long remaining = timerExpire - System.currentTimeMillis();
        return (int)((remaining > 2147483647L) ? 2147483647L : ((remaining <= 0L) ? 1L : remaining));
    }
    
    private void connectHelper(final ServerPortPlaceHolder serverInfo, final int timeOutSliceInMillis, final int timeOutFullInSeconds, final boolean useParallel, final boolean useTnir, final boolean isTnirFirstAttempt, final int timeOutsliceInMillisForFullTimeout) throws SQLServerException {
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
            SQLServerConnection.connectionlogger.fine(this.toString() + " Connecting with server: " + serverInfo.getServerName() + " port: " + serverInfo.getPortNumber() + " Timeout slice: " + timeOutSliceInMillis + " Timeout Full: " + timeOutFullInSeconds);
        }
        this.hostName = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.WORKSTATION_ID.toString());
        if (StringUtils.isEmpty(this.hostName)) {
            this.hostName = Util.lookupHostName();
        }
        this.tdsChannel = new TDSChannel(this);
        if (0 == timeOutFullInSeconds) {
            this.tdsChannel.open(serverInfo.getServerName(), serverInfo.getPortNumber(), 0, useParallel, useTnir, isTnirFirstAttempt, timeOutsliceInMillisForFullTimeout);
        }
        else {
            this.tdsChannel.open(serverInfo.getServerName(), serverInfo.getPortNumber(), timeOutSliceInMillis, useParallel, useTnir, isTnirFirstAttempt, timeOutsliceInMillisForFullTimeout);
        }
        this.setState(State.Connected);
        this.clientConnectionId = UUID.randomUUID();
        assert null != this.clientConnectionId;
        this.Prelogin(serverInfo.getServerName(), serverInfo.getPortNumber());
        if (2 != this.negotiatedEncryptionLevel) {
            this.tdsChannel.enableSSL(serverInfo.getServerName(), serverInfo.getPortNumber());
        }
        this.executeCommand(new LogonCommand());
    }
    
    void Prelogin(final String serverName, final int portNumber) throws SQLServerException {
        if (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.NotSpecified.toString()) || null != this.accessTokenInByte) {
            this.fedAuthRequiredByUser = true;
        }
        byte messageLength;
        byte fedAuthOffset;
        if (this.fedAuthRequiredByUser) {
            messageLength = 73;
            this.requestedEncryptionLevel = 1;
            fedAuthOffset = 5;
        }
        else {
            messageLength = 67;
            fedAuthOffset = 0;
        }
        final byte[] preloginRequest = new byte[messageLength];
        int preloginRequestOffset = 0;
        final byte[] bufferHeader = { 18, 1, 0, messageLength, 0, 0, 0, 0 };
        System.arraycopy(bufferHeader, 0, preloginRequest, preloginRequestOffset, bufferHeader.length);
        preloginRequestOffset += bufferHeader.length;
        final byte[] preloginOptionsBeforeFedAuth = { 0, 0, (byte)(16 + fedAuthOffset), 0, 6, 1, 0, (byte)(22 + fedAuthOffset), 0, 1, 5, 0, (byte)(23 + fedAuthOffset), 0, 36 };
        System.arraycopy(preloginOptionsBeforeFedAuth, 0, preloginRequest, preloginRequestOffset, preloginOptionsBeforeFedAuth.length);
        preloginRequestOffset += preloginOptionsBeforeFedAuth.length;
        if (this.fedAuthRequiredByUser) {
            final byte[] preloginOptions2 = { 6, 0, 64, 0, 1 };
            System.arraycopy(preloginOptions2, 0, preloginRequest, preloginRequestOffset, preloginOptions2.length);
            preloginRequestOffset += preloginOptions2.length;
        }
        preloginRequest[preloginRequestOffset] = -1;
        ++preloginRequestOffset;
        final byte[] preloginOptionData = { 0, 0, 0, 0, 0, 0, this.requestedEncryptionLevel, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        System.arraycopy(preloginOptionData, 0, preloginRequest, preloginRequestOffset, preloginOptionData.length);
        preloginRequestOffset += preloginOptionData.length;
        if (this.fedAuthRequiredByUser) {
            preloginRequest[preloginRequestOffset] = 1;
            ++preloginRequestOffset;
        }
        final byte[] preloginResponse = new byte[4096];
        final String preloginErrorLogString = " Prelogin error: host " + serverName + " port " + portNumber;
        final byte[] conIdByteArray = Util.asGuidByteArray(this.clientConnectionId);
        int offset;
        if (this.fedAuthRequiredByUser) {
            offset = preloginRequest.length - 36 - 1;
        }
        else {
            offset = preloginRequest.length - 36;
        }
        System.arraycopy(conIdByteArray, 0, preloginRequest, offset, conIdByteArray.length);
        offset += conIdByteArray.length;
        if (Util.isActivityTraceOn()) {
            final ActivityId activityId = ActivityCorrelator.getNext();
            final byte[] actIdByteArray = Util.asGuidByteArray(activityId.getId());
            System.arraycopy(actIdByteArray, 0, preloginRequest, offset, actIdByteArray.length);
            offset += actIdByteArray.length;
            final long seqNum = activityId.getSequence();
            Util.writeInt((int)seqNum, preloginRequest, offset);
            offset += 4;
            if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                SQLServerConnection.connectionlogger.finer(this.toString() + " ActivityId " + activityId.toString());
            }
        }
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
            SQLServerConnection.connectionlogger.finer(this.toString() + " Requesting encryption level:" + TDS.getEncryptionLevel(this.requestedEncryptionLevel));
        }
        if (this.tdsChannel.isLoggingPackets()) {
            this.tdsChannel.logPacket(preloginRequest, 0, preloginRequest.length, this.toString() + " Prelogin request");
        }
        try {
            this.tdsChannel.write(preloginRequest, 0, preloginRequest.length);
            this.tdsChannel.flush();
        }
        catch (final SQLServerException e) {
            SQLServerConnection.connectionlogger.warning(this.toString() + preloginErrorLogString + " Error sending prelogin request: " + e.getMessage());
            throw e;
        }
        if (Util.isActivityTraceOn()) {
            ActivityCorrelator.setCurrentActivityIdSentFlag();
        }
        int responseLength = preloginResponse.length;
        int responseBytesRead = 0;
        boolean processedResponseHeader = false;
        while (responseBytesRead < responseLength) {
            int bytesRead;
            try {
                bytesRead = this.tdsChannel.read(preloginResponse, responseBytesRead, responseLength - responseBytesRead);
            }
            catch (final SQLServerException e2) {
                SQLServerConnection.connectionlogger.warning(this.toString() + preloginErrorLogString + " Error reading prelogin response: " + e2.getMessage());
                throw e2;
            }
            if (-1 == bytesRead) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + preloginErrorLogString + " Unexpected end of prelogin response after " + responseBytesRead + " bytes read");
                }
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                final Object[] msgArgs = { serverName, Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer") };
                this.terminate(3, form.format(msgArgs));
            }
            assert bytesRead >= 0;
            assert bytesRead <= responseLength - responseBytesRead;
            if (this.tdsChannel.isLoggingPackets()) {
                this.tdsChannel.logPacket(preloginResponse, responseBytesRead, bytesRead, this.toString() + " Prelogin response");
            }
            responseBytesRead += bytesRead;
            if (processedResponseHeader || responseBytesRead < 8) {
                continue;
            }
            if (4 != preloginResponse[0]) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + preloginErrorLogString + " Unexpected response type:" + preloginResponse[0]);
                }
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                final Object[] msgArgs = { serverName, Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer") };
                this.terminate(3, form.format(msgArgs));
            }
            if (0x1 != (0x1 & preloginResponse[1])) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + preloginErrorLogString + " Unexpected response status:" + preloginResponse[1]);
                }
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                final Object[] msgArgs = { serverName, Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer") };
                this.terminate(3, form.format(msgArgs));
            }
            responseLength = Util.readUnsignedShortBigEndian(preloginResponse, 2);
            assert responseLength >= 0;
            if (responseLength >= preloginResponse.length) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + preloginErrorLogString + " Response length:" + responseLength + " is greater than allowed length:" + preloginResponse.length);
                }
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                final Object[] msgArgs = { serverName, Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer") };
                this.terminate(3, form.format(msgArgs));
            }
            processedResponseHeader = true;
        }
        boolean receivedVersionOption = false;
        this.negotiatedEncryptionLevel = -1;
        int responseIndex = 8;
        while (true) {
            if (responseIndex >= responseLength) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + " Option token not found");
                }
                this.throwInvalidTDS();
            }
            final byte optionToken = preloginResponse[responseIndex++];
            if (-1 == optionToken) {
                if (!receivedVersionOption || -1 == this.negotiatedEncryptionLevel) {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                        SQLServerConnection.connectionlogger.warning(this.toString() + " Prelogin response is missing version and/or encryption option.");
                    }
                    this.throwInvalidTDS();
                }
                return;
            }
            if (responseIndex + 4 >= responseLength) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + " Offset/Length not found for option:" + optionToken);
                }
                this.throwInvalidTDS();
            }
            final int optionOffset = Util.readUnsignedShortBigEndian(preloginResponse, responseIndex) + 8;
            responseIndex += 2;
            assert optionOffset >= 0;
            final int optionLength = Util.readUnsignedShortBigEndian(preloginResponse, responseIndex);
            responseIndex += 2;
            assert optionLength >= 0;
            if (optionOffset + optionLength > responseLength) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + " Offset:" + optionOffset + " and length:" + optionLength + " exceed response length:" + responseLength);
                }
                this.throwInvalidTDS();
            }
            switch (optionToken) {
                case 0: {
                    if (receivedVersionOption) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                            SQLServerConnection.connectionlogger.warning(this.toString() + " Version option already received");
                        }
                        this.throwInvalidTDS();
                    }
                    if (6 != optionLength) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                            SQLServerConnection.connectionlogger.warning(this.toString() + " Version option length:" + optionLength + " is incorrect.  Correct value is 6.");
                        }
                        this.throwInvalidTDS();
                    }
                    this.serverMajorVersion = preloginResponse[optionOffset];
                    if (this.serverMajorVersion < 9) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                            SQLServerConnection.connectionlogger.warning(this.toString() + " Server major version:" + this.serverMajorVersion + " is not supported by this driver.");
                        }
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_unsupportedServerVersion"));
                        final Object[] msgArgs2 = { Integer.toString(preloginResponse[optionOffset]) };
                        this.terminate(6, form2.format(msgArgs2));
                    }
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                        SQLServerConnection.connectionlogger.fine(this.toString() + " Server returned major version:" + preloginResponse[optionOffset]);
                    }
                    receivedVersionOption = true;
                    continue;
                }
                case 1: {
                    if (-1 != this.negotiatedEncryptionLevel) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                            SQLServerConnection.connectionlogger.warning(this.toString() + " Encryption option already received");
                        }
                        this.throwInvalidTDS();
                    }
                    if (1 != optionLength) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                            SQLServerConnection.connectionlogger.warning(this.toString() + " Encryption option length:" + optionLength + " is incorrect.  Correct value is 1.");
                        }
                        this.throwInvalidTDS();
                    }
                    this.negotiatedEncryptionLevel = preloginResponse[optionOffset];
                    if (0 != this.negotiatedEncryptionLevel && 1 != this.negotiatedEncryptionLevel && 3 != this.negotiatedEncryptionLevel && 2 != this.negotiatedEncryptionLevel) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                            SQLServerConnection.connectionlogger.warning(this.toString() + " Server returned " + TDS.getEncryptionLevel(this.negotiatedEncryptionLevel));
                        }
                        this.throwInvalidTDS();
                    }
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.finer(this.toString() + " Negotiated encryption level:" + TDS.getEncryptionLevel(this.negotiatedEncryptionLevel));
                    }
                    if (1 == this.requestedEncryptionLevel && 1 != this.negotiatedEncryptionLevel && 3 != this.negotiatedEncryptionLevel) {
                        this.terminate(5, SQLServerException.getErrString("R_sslRequiredNoServerSupport"));
                    }
                    if (2 == this.requestedEncryptionLevel && 2 != this.negotiatedEncryptionLevel) {
                        if (3 == this.negotiatedEncryptionLevel) {
                            this.terminate(5, SQLServerException.getErrString("R_sslRequiredByServer"));
                        }
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                            SQLServerConnection.connectionlogger.warning(this.toString() + " Client requested encryption level: " + TDS.getEncryptionLevel(this.requestedEncryptionLevel) + " Server returned unexpected encryption level: " + TDS.getEncryptionLevel(this.negotiatedEncryptionLevel));
                        }
                        this.throwInvalidTDS();
                        continue;
                    }
                    continue;
                }
                case 6: {
                    if (0 != preloginResponse[optionOffset] && 1 != preloginResponse[optionOffset]) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                            SQLServerConnection.connectionlogger.severe(this.toString() + " Server sent an unexpected value for FedAuthRequired PreLogin Option. Value was " + preloginResponse[optionOffset]);
                        }
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_FedAuthRequiredPreLoginResponseInvalidValue"));
                        throw new SQLServerException(form2.format(new Object[] { preloginResponse[optionOffset] }), (Throwable)null);
                    }
                    if ((null != this.authenticationString && !this.authenticationString.equalsIgnoreCase(SqlAuthentication.NotSpecified.toString())) || null != this.accessTokenInByte) {
                        this.fedAuthRequiredPreLoginResponse = (preloginResponse[optionOffset] == 1);
                        continue;
                    }
                    continue;
                }
                default: {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.finer(this.toString() + " Ignoring prelogin response option:" + optionToken);
                        continue;
                    }
                    continue;
                }
            }
        }
    }
    
    final void throwInvalidTDS() throws SQLServerException {
        this.terminate(4, SQLServerException.getErrString("R_invalidTDS"));
    }
    
    final void throwInvalidTDSToken(final String tokenName) throws SQLServerException {
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unexpectedToken"));
        final Object[] msgArgs = { tokenName };
        final String message = SQLServerException.getErrString("R_invalidTDS") + form.format(msgArgs);
        this.terminate(4, message);
    }
    
    final void terminate(final int driverErrorCode, final String message) throws SQLServerException {
        this.terminate(driverErrorCode, message, null);
    }
    
    final void terminate(final int driverErrorCode, final String message, final Throwable throwable) throws SQLServerException {
        String state = this.state.equals(State.Opened) ? "08006" : "08001";
        if (!this.xopenStates) {
            state = SQLServerException.mapFromXopen(state);
        }
        final SQLServerException ex = new SQLServerException(this, SQLServerException.checkAndAppendClientConnId(message, this), state, 0, true);
        if (null != throwable) {
            ex.initCause(throwable);
        }
        ex.setDriverErrorCode(driverErrorCode);
        this.notifyPooledConnection(ex);
        this.close();
        throw ex;
    }
    
    boolean executeCommand(final TDSCommand newCommand) throws SQLServerException {
        synchronized (this.schedulerLock) {
            if (null != this.currentCommand) {
                try {
                    this.currentCommand.detach();
                }
                catch (final SQLServerException e) {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINE)) {
                        SQLServerConnection.connectionlogger.fine("Failed to detach current command : " + e.getMessage());
                    }
                }
                finally {
                    this.currentCommand = null;
                }
            }
            boolean commandComplete = false;
            try {
                commandComplete = newCommand.execute(this.tdsChannel.getWriter(), this.tdsChannel.getReader(newCommand));
            }
            finally {
                if (!commandComplete && !this.isSessionUnAvailable()) {
                    this.currentCommand = newCommand;
                }
            }
            return commandComplete;
        }
    }
    
    void resetCurrentCommand() throws SQLServerException {
        if (null != this.currentCommand) {
            this.currentCommand.detach();
            this.currentCommand = null;
        }
    }
    
    private void connectionCommand(final String sql, final String logContext) throws SQLServerException {
        final class ConnectionCommand extends UninterruptableTDSCommand
        {
            private static final long serialVersionUID = 1L;
            final String sql = sql;
            
            ConnectionCommand(final String logContext) {
                super(logContext);
            }
            
            @Override
            final boolean doExecute() throws SQLServerException {
                final TDSWriter tdsWriter = this.startRequest((byte)1);
                tdsWriter.sendEnclavePackage(null, null);
                tdsWriter.writeString(this.sql);
                TDSParser.parse(this.startResponse(), this.getLogContext());
                return true;
            }
        }
        this.executeCommand(new ConnectionCommand(logContext));
    }
    
    private String sqlStatementToInitialize() {
        String s = null;
        if (this.nLockTimeout > -1) {
            s = " set lock_timeout " + this.nLockTimeout;
        }
        return s;
    }
    
    void setCatalogName(final String sDB) {
        if (sDB != null && sDB.length() > 0) {
            this.sCatalog = sDB;
        }
    }
    
    String sqlStatementToSetTransactionIsolationLevel() throws SQLServerException {
        String sql = "set transaction isolation level ";
        switch (this.transactionIsolationLevel) {
            case 1: {
                sql += " read uncommitted ";
                break;
            }
            case 2: {
                sql += " read committed ";
                break;
            }
            case 4: {
                sql += " repeatable read ";
                break;
            }
            case 8: {
                sql += " serializable ";
                break;
            }
            case 4096: {
                sql += " snapshot ";
                break;
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTransactionLevel"));
                final Object[] msgArgs = { Integer.toString(this.transactionIsolationLevel) };
                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                break;
            }
        }
        return sql;
    }
    
    static String sqlStatementToSetCommit(final boolean autoCommit) {
        return autoCommit ? "set implicit_transactions off " : "set implicit_transactions on ";
    }
    
    @Override
    public Statement createStatement() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "createStatement");
        final Statement st = this.createStatement(1003, 1007);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "createStatement", st);
        return st;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", sql);
        final PreparedStatement pst = this.prepareStatement(sql, 1003, 1007);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", pst);
        return pst;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareCall", sql);
        final CallableStatement st = this.prepareCall(sql, 1003, 1007);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareCall", st);
        return st;
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "nativeSQL", sql);
        this.checkClosed();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "nativeSQL", sql);
        return sql;
    }
    
    @Override
    public void setAutoCommit(final boolean newAutoCommitMode) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setAutoCommit", newAutoCommitMode);
            if (Util.isActivityTraceOn()) {
                SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
            }
        }
        String commitPendingTransaction = "";
        this.checkClosed();
        if (newAutoCommitMode == this.databaseAutoCommitMode) {
            return;
        }
        if (newAutoCommitMode) {
            commitPendingTransaction = "IF @@TRANCOUNT > 0 COMMIT TRAN ";
        }
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
            SQLServerConnection.connectionlogger.finer(this.toString() + " Autocommitmode current :" + this.databaseAutoCommitMode + " new: " + newAutoCommitMode);
        }
        this.rolledBackTransaction = false;
        this.connectionCommand(sqlStatementToSetCommit(newAutoCommitMode) + commitPendingTransaction, "setAutoCommit");
        this.databaseAutoCommitMode = newAutoCommitMode;
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setAutoCommit");
    }
    
    @Override
    public boolean getAutoCommit() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getAutoCommit");
        this.checkClosed();
        final boolean res = !this.inXATransaction && this.databaseAutoCommitMode;
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getAutoCommit", res);
        }
        return res;
    }
    
    final byte[] getTransactionDescriptor() {
        return this.transactionDescriptor;
    }
    
    @Override
    public void commit() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "commit");
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        if (!this.databaseAutoCommitMode) {
            this.connectionCommand("IF @@TRANCOUNT > 0 COMMIT TRAN", "Connection.commit");
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "commit");
    }
    
    @Override
    public void rollback() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "rollback");
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        if (this.databaseAutoCommitMode) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_cantInvokeRollback"), null, true);
        }
        else {
            this.connectionCommand("IF @@TRANCOUNT > 0 ROLLBACK TRAN", "Connection.rollback");
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "rollback");
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "abort", executor);
        if (this.isClosed()) {
            return;
        }
        final SecurityManager secMgr = System.getSecurityManager();
        if (secMgr != null) {
            try {
                final SQLPermission perm = new SQLPermission("callAbort");
                secMgr.checkPermission(perm);
            }
            catch (final SecurityException ex) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_permissionDenied"));
                final Object[] msgArgs = { "callAbort" };
                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, true);
            }
        }
        if (null == executor) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
            final Object[] msgArgs2 = { "executor" };
            SQLServerException.makeFromDriverError(null, null, form2.format(msgArgs2), null, false);
        }
        else {
            this.setState(State.Closed);
            executor.execute(() -> this.clearConnectionResources());
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "abort");
    }
    
    @Override
    public void close() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "close");
        this.setState(State.Closed);
        this.clearConnectionResources();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "close");
    }
    
    private void clearConnectionResources() {
        if (this.sharedTimer != null) {
            this.sharedTimer.removeRef();
            this.sharedTimer = null;
        }
        if (null != this.tdsChannel) {
            this.tdsChannel.close();
        }
        if (null != this.preparedStatementHandleCache) {
            this.preparedStatementHandleCache.clear();
        }
        if (null != this.parameterMetadataCache) {
            this.parameterMetadataCache.clear();
        }
        this.cleanupPreparedStatementDiscardActions();
        if (Util.isActivityTraceOn()) {
            ActivityCorrelator.cleanupActivityId();
        }
    }
    
    final void poolCloseEventNotify() throws SQLServerException {
        if (this.state.equals(State.Opened) && null != this.pooledConnectionParent) {
            if (!this.databaseAutoCommitMode && !(this.pooledConnectionParent instanceof XAConnection)) {
                this.connectionCommand("IF @@TRANCOUNT > 0 ROLLBACK TRAN", "close connection");
            }
            this.notifyPooledConnection(null);
            if (Util.isActivityTraceOn()) {
                ActivityCorrelator.cleanupActivityId();
            }
            if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                SQLServerConnection.connectionlogger.finer(this.toString() + " Connection closed and returned to connection pool");
            }
        }
    }
    
    @Override
    public boolean isClosed() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "isClosed");
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "isClosed", this.isSessionUnAvailable());
        return this.isSessionUnAvailable();
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getMetaData");
        this.checkClosed();
        if (this.databaseMetaData == null) {
            this.databaseMetaData = new SQLServerDatabaseMetaData(this);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getMetaData", this.databaseMetaData);
        return this.databaseMetaData;
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setReadOnly", readOnly);
        }
        this.checkClosed();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setReadOnly");
    }
    
    @Override
    public boolean isReadOnly() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "isReadOnly");
        this.checkClosed();
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "isReadOnly", Boolean.FALSE);
        }
        return false;
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setCatalog", catalog);
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        if (catalog != null) {
            this.connectionCommand("use " + Util.escapeSQLId(catalog), "setCatalog");
            this.sCatalog = catalog;
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setCatalog");
    }
    
    @Override
    public String getCatalog() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getCatalog");
        this.checkClosed();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getCatalog", this.sCatalog);
        return this.sCatalog;
    }
    
    String getSCatalog() throws SQLServerException {
        return this.sCatalog;
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setTransactionIsolation", level);
            if (Util.isActivityTraceOn()) {
                SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
            }
        }
        this.checkClosed();
        if (level == 0) {
            return;
        }
        this.transactionIsolationLevel = level;
        final String sql = this.sqlStatementToSetTransactionIsolationLevel();
        this.connectionCommand(sql, "setTransactionIsolation");
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setTransactionIsolation");
    }
    
    @Override
    public int getTransactionIsolation() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getTransactionIsolation");
        this.checkClosed();
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getTransactionIsolation", this.transactionIsolationLevel);
        }
        return this.transactionIsolationLevel;
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getWarnings");
        this.checkClosed();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getWarnings", this.sqlWarnings);
        return this.sqlWarnings;
    }
    
    private void addWarning(final String warningString) {
        synchronized (this.warningSynchronization) {
            final SQLWarning warning = new SQLWarning(warningString);
            if (null == this.sqlWarnings) {
                this.sqlWarnings = warning;
            }
            else {
                this.sqlWarnings.setNextWarning(warning);
            }
        }
    }
    
    @Override
    public void clearWarnings() throws SQLServerException {
        synchronized (this.warningSynchronization) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "clearWarnings");
            this.checkClosed();
            this.sqlWarnings = null;
            SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "clearWarnings");
        }
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "createStatement", new Object[] { resultSetType, resultSetConcurrency });
        }
        this.checkClosed();
        final SQLServerStatement st = new SQLServerStatement(this, resultSetType, resultSetConcurrency, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "createStatement", st);
        return st;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, resultSetType, resultSetConcurrency });
        }
        this.checkClosed();
        final SQLServerPreparedStatement st = new SQLServerPreparedStatement(this, sql, resultSetType, resultSetConcurrency, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", st);
        return st;
    }
    
    private PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, resultSetType, resultSetConcurrency, stmtColEncSetting });
        }
        this.checkClosed();
        final SQLServerPreparedStatement st = new SQLServerPreparedStatement(this, sql, resultSetType, resultSetConcurrency, stmtColEncSetting);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", st);
        return st;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareCall", new Object[] { sql, resultSetType, resultSetConcurrency });
        }
        this.checkClosed();
        final SQLServerCallableStatement st = new SQLServerCallableStatement(this, sql, resultSetType, resultSetConcurrency, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareCall", st);
        return st;
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setTypeMap", map);
        this.checkClosed();
        if (map != null && map instanceof HashMap && map.isEmpty()) {
            SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setTypeMap");
            return;
        }
        SQLServerException.throwNotSupportedException(this, null);
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getTypeMap");
        this.checkClosed();
        final Map<String, Class<?>> mp = new HashMap<String, Class<?>>();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getTypeMap", mp);
        return mp;
    }
    
    int writeAEFeatureRequest(final boolean write, final TDSWriter tdsWriter) throws SQLServerException {
        final int len = 6;
        if (write) {
            tdsWriter.writeByte((byte)4);
            tdsWriter.writeInt(1);
            if (null == this.enclaveAttestationUrl || this.enclaveAttestationUrl.isEmpty()) {
                tdsWriter.writeByte((byte)1);
            }
            else {
                tdsWriter.writeByte((byte)2);
            }
        }
        return len;
    }
    
    int writeFedAuthFeatureRequest(final boolean write, final TDSWriter tdsWriter, final FederatedAuthenticationFeatureExtensionData fedAuthFeatureExtensionData) throws SQLServerException {
        assert fedAuthFeatureExtensionData.libraryType == 1;
        int dataLen = 0;
        switch (fedAuthFeatureExtensionData.libraryType) {
            case 2: {
                dataLen = 2;
                break;
            }
            case 1: {
                assert null != fedAuthFeatureExtensionData.accessToken;
                dataLen = 5 + fedAuthFeatureExtensionData.accessToken.length;
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        final int totalLen = dataLen + 5;
        if (write) {
            tdsWriter.writeByte((byte)2);
            byte options = 0;
            switch (fedAuthFeatureExtensionData.libraryType) {
                case 2: {
                    assert this.federatedAuthenticationInfoRequested;
                    options |= 0x4;
                    break;
                }
                case 1: {
                    assert this.federatedAuthenticationRequested;
                    options |= 0x2;
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
            options |= (byte)(fedAuthFeatureExtensionData.fedAuthRequiredPreLoginResponse ? 1 : 0);
            tdsWriter.writeInt(dataLen);
            tdsWriter.writeByte(options);
            switch (fedAuthFeatureExtensionData.libraryType) {
                case 2: {
                    byte workflow = 0;
                    switch (fedAuthFeatureExtensionData.authentication) {
                        case ActiveDirectoryPassword: {
                            workflow = 1;
                            break;
                        }
                        case ActiveDirectoryIntegrated: {
                            workflow = 2;
                            break;
                        }
                        case ActiveDirectoryMSI: {
                            workflow = 3;
                            break;
                        }
                        default: {
                            assert false;
                            break;
                        }
                    }
                    tdsWriter.writeByte(workflow);
                    break;
                }
                case 1: {
                    tdsWriter.writeInt(fedAuthFeatureExtensionData.accessToken.length);
                    tdsWriter.writeBytes(fedAuthFeatureExtensionData.accessToken, 0, fedAuthFeatureExtensionData.accessToken.length);
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        return totalLen;
    }
    
    int writeDataClassificationFeatureRequest(final boolean write, final TDSWriter tdsWriter) throws SQLServerException {
        final int len = 6;
        if (write) {
            tdsWriter.writeByte((byte)9);
            tdsWriter.writeInt(1);
            tdsWriter.writeByte((byte)1);
        }
        return len;
    }
    
    int writeUTF8SupportFeatureRequest(final boolean write, final TDSWriter tdsWriter) throws SQLServerException {
        final int len = 5;
        if (write) {
            tdsWriter.writeByte((byte)10);
            tdsWriter.writeInt(0);
        }
        return len;
    }
    
    private void logon(final LogonCommand command) throws SQLServerException {
        SSPIAuthentication authentication = null;
        if (this.integratedSecurity) {
            if (AuthenticationScheme.nativeAuthentication == this.intAuthScheme) {
                authentication = new AuthenticationJNI(this, this.currentConnectPlaceHolder.getServerName(), this.currentConnectPlaceHolder.getPortNumber());
            }
            else if (AuthenticationScheme.javaKerberos == this.intAuthScheme) {
                if (null != this.impersonatedUserCred) {
                    authentication = new KerbAuthentication(this, this.currentConnectPlaceHolder.getServerName(), this.currentConnectPlaceHolder.getPortNumber(), this.impersonatedUserCred, this.isUserCreatedCredential);
                }
                else {
                    authentication = new KerbAuthentication(this, this.currentConnectPlaceHolder.getServerName(), this.currentConnectPlaceHolder.getPortNumber());
                }
            }
            else if (this.ntlmAuthentication) {
                if (null == this.ntlmPasswordHash) {
                    this.ntlmPasswordHash = NTLMAuthentication.getNtlmPasswordHash(this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()));
                    this.activeConnectionProperties.remove(SQLServerDriverStringProperty.PASSWORD.toString());
                }
                authentication = new NTLMAuthentication(this, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.DOMAIN.toString()), this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()), this.ntlmPasswordHash, this.hostName);
            }
        }
        if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryPassword.toString()) || ((this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryIntegrated.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryMSI.toString())) && this.fedAuthRequiredPreLoginResponse)) {
            this.federatedAuthenticationInfoRequested = true;
            this.fedAuthFeatureExtensionData = new FederatedAuthenticationFeatureExtensionData(2, this.authenticationString, this.fedAuthRequiredPreLoginResponse);
        }
        if (null != this.accessTokenInByte) {
            this.fedAuthFeatureExtensionData = new FederatedAuthenticationFeatureExtensionData(1, this.fedAuthRequiredPreLoginResponse, this.accessTokenInByte);
            this.federatedAuthenticationRequested = true;
        }
        try {
            this.sendLogon(command, authentication, this.fedAuthFeatureExtensionData);
            if (!this.isRoutedInCurrentAttempt) {
                this.originalCatalog = this.sCatalog;
                final String sqlStmt = this.sqlStatementToInitialize();
                if (sqlStmt != null) {
                    this.connectionCommand(sqlStmt, "Change Settings");
                }
            }
        }
        finally {
            if (this.integratedSecurity) {
                if (null != authentication) {
                    authentication.releaseClientContext();
                    authentication = null;
                }
                if (null != this.impersonatedUserCred) {
                    this.impersonatedUserCred = null;
                }
            }
        }
    }
    
    final void processEnvChange(final TDSReader tdsReader) throws SQLServerException {
        tdsReader.readUnsignedByte();
        final int envValueLength = tdsReader.readUnsignedShort();
        final TDSReaderMark mark = tdsReader.mark();
        final int envchange = tdsReader.readUnsignedByte();
        switch (envchange) {
            case 4: {
                try {
                    this.tdsPacketSize = Integer.parseInt(tdsReader.readUnicodeString(tdsReader.readUnsignedByte()));
                }
                catch (final NumberFormatException e) {
                    tdsReader.throwInvalidTDS();
                }
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    SQLServerConnection.connectionlogger.finer(this.toString() + " Network packet size is " + this.tdsPacketSize + " bytes");
                    break;
                }
                break;
            }
            case 7: {
                if (SQLCollation.tdsLength() != tdsReader.readUnsignedByte()) {
                    tdsReader.throwInvalidTDS();
                }
                try {
                    this.databaseCollation = new SQLCollation(tdsReader);
                }
                catch (final UnsupportedEncodingException e2) {
                    this.terminate(4, e2.getMessage(), e2);
                }
                break;
            }
            case 8:
            case 11: {
                this.rolledBackTransaction = false;
                final byte[] transactionDescriptor = this.getTransactionDescriptor();
                if (transactionDescriptor.length != tdsReader.readUnsignedByte()) {
                    tdsReader.throwInvalidTDS();
                }
                tdsReader.readBytes(transactionDescriptor, 0, transactionDescriptor.length);
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    String op;
                    if (8 == envchange) {
                        op = " started";
                    }
                    else {
                        op = " enlisted";
                    }
                    SQLServerConnection.connectionlogger.finer(this.toString() + op);
                    break;
                }
                break;
            }
            case 10: {
                this.rolledBackTransaction = true;
                if (!this.inXATransaction) {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.finer(this.toString() + " rolled back");
                    }
                    Arrays.fill(this.getTransactionDescriptor(), (byte)0);
                    break;
                }
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    SQLServerConnection.connectionlogger.finer(this.toString() + " rolled back. (DTC)");
                    break;
                }
                break;
            }
            case 9: {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    SQLServerConnection.connectionlogger.finer(this.toString() + " committed");
                }
                Arrays.fill(this.getTransactionDescriptor(), (byte)0);
                break;
            }
            case 12: {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    SQLServerConnection.connectionlogger.finer(this.toString() + " defected");
                }
                Arrays.fill(this.getTransactionDescriptor(), (byte)0);
                break;
            }
            case 1: {
                this.setCatalogName(tdsReader.readUnicodeString(tdsReader.readUnsignedByte()));
                break;
            }
            case 13: {
                this.setFailoverPartnerServerProvided(tdsReader.readUnicodeString(tdsReader.readUnsignedByte()));
                break;
            }
            case 2:
            case 3:
            case 5:
            case 6:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19: {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    SQLServerConnection.connectionlogger.finer(this.toString() + " Ignored env change: " + envchange);
                    break;
                }
                break;
            }
            case 20: {
                int routingServerNameLength;
                int routingPortNumber;
                int routingDataValueLength;
                int routingProtocol = routingDataValueLength = (routingPortNumber = (routingServerNameLength = -1));
                String routingServerName = null;
                try {
                    routingDataValueLength = tdsReader.readUnsignedShort();
                    if (routingDataValueLength <= 5) {
                        this.throwInvalidTDS();
                    }
                    routingProtocol = tdsReader.readUnsignedByte();
                    if (routingProtocol != 0) {
                        this.throwInvalidTDS();
                    }
                    routingPortNumber = tdsReader.readUnsignedShort();
                    if (routingPortNumber <= 0 || routingPortNumber > 65535) {
                        this.throwInvalidTDS();
                    }
                    routingServerNameLength = tdsReader.readUnsignedShort();
                    if (routingServerNameLength <= 0 || routingServerNameLength > 1024) {
                        this.throwInvalidTDS();
                    }
                    routingServerName = tdsReader.readUnicodeString(routingServerNameLength);
                    assert routingServerName != null;
                }
                finally {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.finer(this.toString() + " Received routing ENVCHANGE with the following values. routingDataValueLength:" + routingDataValueLength + " protocol:" + routingProtocol + " portNumber:" + routingPortNumber + " serverNameLength:" + routingServerNameLength + " serverName:" + ((routingServerName != null) ? routingServerName : "null"));
                    }
                }
                final String currentHostName = this.activeConnectionProperties.getProperty("hostNameInCertificate");
                if (null != currentHostName && currentHostName.startsWith("*") && null != routingServerName && routingServerName.indexOf(46) != -1) {
                    final char[] currentHostNameCharArray = currentHostName.toCharArray();
                    final char[] routingServerNameCharArray = routingServerName.toCharArray();
                    boolean hostNameNeedsUpdate = true;
                    for (int i = currentHostName.length() - 1, j = routingServerName.length() - 1; i > 0 && j > 0; --i, --j) {
                        if (routingServerNameCharArray[j] != currentHostNameCharArray[i]) {
                            hostNameNeedsUpdate = false;
                            break;
                        }
                    }
                    if (hostNameNeedsUpdate) {
                        final String newHostName = "*" + routingServerName.substring(routingServerName.indexOf(46));
                        this.activeConnectionProperties.setProperty("hostNameInCertificate", newHostName);
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                            SQLServerConnection.connectionlogger.finer(this.toString() + "Using new host to validate the SSL certificate");
                        }
                    }
                }
                this.isRoutedInCurrentAttempt = true;
                this.routingInfo = new ServerPortPlaceHolder(routingServerName, routingPortNumber, null, this.integratedSecurity);
                break;
            }
            default: {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.WARNING)) {
                    SQLServerConnection.connectionlogger.warning(this.toString() + " Unknown environment change: " + envchange);
                }
                this.throwInvalidTDS();
                break;
            }
        }
        tdsReader.reset(mark);
        tdsReader.readBytes(new byte[envValueLength], 0, envValueLength);
    }
    
    final void processFedAuthInfo(final TDSReader tdsReader, final TDSTokenHandler tdsTokenHandler) throws SQLServerException {
        final SqlFedAuthInfo sqlFedAuthInfo = new SqlFedAuthInfo();
        tdsReader.readUnsignedByte();
        int tokenLen = tdsReader.readInt();
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
            SQLServerConnection.connectionlogger.fine(this.toString() + " FEDAUTHINFO token stream length = " + tokenLen);
        }
        if (tokenLen < 4) {
            if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                SQLServerConnection.connectionlogger.severe(this.toString() + "FEDAUTHINFO token stream length too short for CountOfInfoIDs.");
            }
            throw new SQLServerException(SQLServerException.getErrString("R_FedAuthInfoLengthTooShortForCountOfInfoIds"), (Throwable)null);
        }
        final int optionsCount = tdsReader.readInt();
        tokenLen -= 4;
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
            SQLServerConnection.connectionlogger.fine(this.toString() + " CountOfInfoIDs = " + optionsCount);
        }
        if (tokenLen <= 0) {
            if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                SQLServerConnection.connectionlogger.severe(this.toString() + "FEDAUTHINFO token stream is not long enough to contain the data it claims to.");
            }
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_FedAuthInfoLengthTooShortForData"));
            throw new SQLServerException(form.format(new Object[] { tokenLen }), (Throwable)null);
        }
        final byte[] tokenData = new byte[tokenLen];
        tdsReader.readBytes(tokenData, 0, tokenLen);
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
            SQLServerConnection.connectionlogger.fine(this.toString() + " Read rest of FEDAUTHINFO token stream: " + Arrays.toString(tokenData));
        }
        final int optionSize = 9;
        final int totalOptionsSize = optionsCount * 9;
        for (int i = 0; i < optionsCount; ++i) {
            final int currentOptionOffset = i * 9;
            final byte id = tokenData[currentOptionOffset];
            byte[] buffer = { tokenData[currentOptionOffset + 4], tokenData[currentOptionOffset + 3], tokenData[currentOptionOffset + 2], tokenData[currentOptionOffset + 1] };
            ByteBuffer wrapped = ByteBuffer.wrap(buffer);
            final int dataLen = wrapped.getInt();
            buffer = new byte[] { tokenData[currentOptionOffset + 8], tokenData[currentOptionOffset + 7], tokenData[currentOptionOffset + 6], tokenData[currentOptionOffset + 5] };
            wrapped = ByteBuffer.wrap(buffer);
            int dataOffset = wrapped.getInt();
            if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                SQLServerConnection.connectionlogger.fine(this.toString() + " FedAuthInfoOpt: ID=" + id + ", DataLen=" + dataLen + ", Offset=" + dataOffset);
            }
            dataOffset -= 4;
            if (dataOffset < totalOptionsSize || dataOffset >= tokenLen) {
                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                    SQLServerConnection.connectionlogger.severe(this.toString() + "FedAuthInfoDataOffset points to an invalid location.");
                }
                final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_FedAuthInfoInvalidOffset"));
                throw new SQLServerException(form2.format(new Object[] { dataOffset }), (Throwable)null);
            }
            String data = null;
            try {
                final byte[] dataArray = new byte[dataLen];
                System.arraycopy(tokenData, dataOffset, dataArray, 0, dataLen);
                data = new String(dataArray, StandardCharsets.UTF_16LE);
            }
            catch (final Exception e) {
                SQLServerConnection.connectionlogger.severe(this.toString() + "Failed to read FedAuthInfoData.");
                throw new SQLServerException(SQLServerException.getErrString("R_FedAuthInfoFailedToReadData"), e);
            }
            if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                SQLServerConnection.connectionlogger.fine(this.toString() + " FedAuthInfoData: " + data);
            }
            switch (id) {
                case 2: {
                    sqlFedAuthInfo.spn = data;
                    break;
                }
                case 1: {
                    sqlFedAuthInfo.stsurl = data;
                    break;
                }
                default: {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.fine(this.toString() + " Ignoring unknown federated authentication info option: " + id);
                        break;
                    }
                    break;
                }
            }
        }
        if (null == sqlFedAuthInfo.spn || null == sqlFedAuthInfo.stsurl || sqlFedAuthInfo.spn.trim().isEmpty() || sqlFedAuthInfo.stsurl.trim().isEmpty()) {
            if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                SQLServerConnection.connectionlogger.severe(this.toString() + "FEDAUTHINFO token stream does not contain both STSURL and SPN.");
            }
            throw new SQLServerException(SQLServerException.getErrString("R_FedAuthInfoDoesNotContainStsurlAndSpn"), (Throwable)null);
        }
        this.onFedAuthInfo(sqlFedAuthInfo, tdsTokenHandler);
    }
    
    void onFedAuthInfo(final SqlFedAuthInfo fedAuthInfo, final TDSTokenHandler tdsTokenHandler) throws SQLServerException {
        assert this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryMSI.toString()) && this.fedAuthRequiredPreLoginResponse;
        assert null != fedAuthInfo;
        this.attemptRefreshTokenLocked = true;
        this.fedAuthToken = this.getFedAuthToken(fedAuthInfo);
        this.attemptRefreshTokenLocked = false;
        assert null != this.fedAuthToken;
        final TDSCommand fedAuthCommand = new FedAuthTokenCommand(this.fedAuthToken, tdsTokenHandler);
        fedAuthCommand.execute(this.tdsChannel.getWriter(), this.tdsChannel.getReader(fedAuthCommand));
    }
    
    private SqlFedAuthToken getFedAuthToken(final SqlFedAuthInfo fedAuthInfo) throws SQLServerException {
        SqlFedAuthToken fedAuthToken = null;
        assert null != fedAuthInfo;
        final String user = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString());
        int sleepInterval = 100;
        while (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryPassword.toString())) {
            if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryMSI.toString())) {
                fedAuthToken = this.getMSIAuthToken(fedAuthInfo.spn, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.MSI_CLIENT_ID.toString()));
            }
            else {
                if (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.ActiveDirectoryIntegrated.toString())) {
                    continue;
                }
                if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows") && AuthenticationJNI.isDllLoaded()) {
                    try {
                        final FedAuthDllInfo dllInfo = AuthenticationJNI.getAccessTokenForWindowsIntegrated(fedAuthInfo.stsurl, fedAuthInfo.spn, this.clientConnectionId.toString(), "7f98cb04-cd1e-40df-9140-3bf7e2cea4db", 0L);
                        assert null != dllInfo.accessTokenBytes;
                        final byte[] accessTokenFromDLL = dllInfo.accessTokenBytes;
                        final String accessToken = new String(accessTokenFromDLL, StandardCharsets.UTF_16LE);
                        fedAuthToken = new SqlFedAuthToken(accessToken, dllInfo.expiresIn);
                    }
                    catch (final DLLException adalException) {
                        final int errorCategory = adalException.GetCategory();
                        if (-1 == errorCategory) {
                            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnableLoadADALSqlDll"));
                            final Object[] msgArgs = { Integer.toHexString(adalException.GetState()) };
                            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
                        }
                        final int millisecondsRemaining = timerRemaining(this.timerExpire);
                        if (2 != errorCategory || timerHasExpired(this.timerExpire) || sleepInterval >= millisecondsRemaining) {
                            final String errorStatus = Integer.toHexString(adalException.GetStatus());
                            if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                                SQLServerConnection.connectionlogger.fine(this.toString() + " SQLServerConnection.getFedAuthToken.AdalException category:" + errorCategory + " error: " + errorStatus);
                            }
                            MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_ADALAuthenticationMiddleErrorMessage"));
                            final String errorCode = Integer.toHexString(adalException.GetStatus()).toUpperCase();
                            final Object[] msgArgs2 = { errorCode, adalException.GetState() };
                            final SQLServerException middleException = new SQLServerException(form2.format(msgArgs2), adalException);
                            form2 = new MessageFormat(SQLServerException.getErrString("R_ADALExecution"));
                            final Object[] msgArgs3 = { user, this.authenticationString };
                            throw new SQLServerException(form2.format(msgArgs3), null, 0, middleException);
                        }
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                            SQLServerConnection.connectionlogger.fine(this.toString() + " SQLServerConnection.getFedAuthToken sleeping: " + sleepInterval + " milliseconds.");
                            SQLServerConnection.connectionlogger.fine(this.toString() + " SQLServerConnection.getFedAuthToken remaining: " + millisecondsRemaining + " milliseconds.");
                        }
                        try {
                            Thread.sleep(sleepInterval);
                        }
                        catch (final InterruptedException e1) {
                            Thread.currentThread().interrupt();
                        }
                        sleepInterval *= 2;
                    }
                }
                else {
                    if (!this.adalContextExists()) {
                        final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_DLLandADALMissing"));
                        final Object[] msgArgs4 = { SQLServerDriver.AUTH_DLL_NAME, this.authenticationString };
                        throw new SQLServerException(form3.format(msgArgs4), null, 0, null);
                    }
                    fedAuthToken = SQLServerADAL4JUtils.getSqlFedAuthTokenIntegrated(fedAuthInfo, this.authenticationString);
                }
            }
            return fedAuthToken;
        }
        if (!this.adalContextExists()) {
            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_ADALMissing"));
            throw new SQLServerException(form3.format(new Object[] { this.authenticationString }), null, 0, null);
        }
        fedAuthToken = SQLServerADAL4JUtils.getSqlFedAuthToken(fedAuthInfo, user, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()), this.authenticationString);
        return fedAuthToken;
    }
    
    private boolean adalContextExists() {
        try {
            Class.forName("com.microsoft.aad.adal4j.AuthenticationContext");
        }
        catch (final ClassNotFoundException e) {
            return false;
        }
        return true;
    }
    
    private SqlFedAuthToken getMSIAuthToken(final String resource, final String msiClientId) throws SQLServerException {
        final int imdsUpgradeTimeInMs = 70000;
        final List<Integer> retrySlots = new ArrayList<Integer>();
        final String msiEndpoint = System.getenv("MSI_ENDPOINT");
        final String msiSecret = System.getenv("MSI_SECRET");
        final StringBuilder urlString = new StringBuilder();
        int retry = 1;
        int maxRetry = 1;
        final boolean isAzureFunction = null != msiEndpoint && !msiEndpoint.isEmpty() && null != msiSecret && !msiSecret.isEmpty();
        if (isAzureFunction) {
            urlString.append(msiEndpoint).append("?api-version=2017-09-01&resource=").append(resource);
        }
        else {
            urlString.append("http://169.254.169.254/metadata/identity/oauth2/token?api-version=2018-02-01").append("&resource=").append(resource);
            maxRetry = 20;
            for (int x = 0; x < maxRetry; ++x) {
                retrySlots.add(1);
            }
        }
        if (null != msiClientId && !msiClientId.isEmpty()) {
            if (isAzureFunction) {
                urlString.append("&clientid=").append(msiClientId);
            }
            else {
                urlString.append("&client_id=").append(msiClientId);
            }
        }
        while (retry <= maxRetry) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection)new URL(urlString.toString()).openConnection();
                connection.setRequestMethod("GET");
                if (isAzureFunction) {
                    connection.setRequestProperty("Secret", msiSecret);
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.finer(this.toString() + " Using Azure Function/App Service MSI auth: " + (Object)urlString);
                    }
                }
                else {
                    connection.setRequestProperty("Metadata", "true");
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.finer(this.toString() + " Using Azure MSI auth: " + (Object)urlString);
                    }
                }
                connection.connect();
                try (final InputStream stream = connection.getInputStream()) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8), 100);
                    final String result = reader.readLine();
                    final int startIndex_AT = result.indexOf("\"access_token\":\"") + "\"access_token\":\"".length();
                    final String accessToken = result.substring(startIndex_AT, result.indexOf("\"", startIndex_AT + 1));
                    Calendar cal = new Calendar.Builder().setInstant(new Date()).build();
                    if (isAzureFunction) {
                        final int startIndex_ATX = result.indexOf("\"expires_on\":\"") + "\"expires_on\":\"".length();
                        final String accessTokenExpiry = result.substring(startIndex_ATX, result.indexOf("\"", startIndex_ATX + 1));
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                            SQLServerConnection.connectionlogger.finer(this.toString() + " MSI auth token expires on: " + accessTokenExpiry);
                        }
                        final DateFormat df = new SimpleDateFormat("M/d/yyyy h:mm:ss a X");
                        cal = new Calendar.Builder().setInstant(df.parse(accessTokenExpiry)).build();
                    }
                    else {
                        final int startIndex_ATX = result.indexOf("\"expires_in\":\"") + "\"expires_in\":\"".length();
                        final String accessTokenExpiry = result.substring(startIndex_ATX, result.indexOf("\"", startIndex_ATX + 1));
                        cal.add(13, Integer.parseInt(accessTokenExpiry));
                    }
                    return new SqlFedAuthToken(accessToken, cal.getTime());
                }
            }
            catch (final Exception e) {
                if (++retry > maxRetry) {
                    break;
                }
                try {
                    final int responseCode = connection.getResponseCode();
                    Label_0849: {
                        if (410 != responseCode && 429 != responseCode && 404 != responseCode) {
                            if (500 > responseCode || 599 < responseCode) {
                                break Label_0849;
                            }
                        }
                        try {
                            int retryTimeoutInMs = retrySlots.get(ThreadLocalRandom.current().nextInt(retry - 1));
                            retryTimeoutInMs = ((responseCode == 410 && retryTimeoutInMs < 70000) ? 70000 : retryTimeoutInMs);
                            Thread.sleep(retryTimeoutInMs);
                            continue;
                        }
                        catch (final InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    if (null != msiClientId && !msiClientId.isEmpty()) {
                        SQLServerException.makeFromDriverError(this, null, SQLServerException.getErrString("R_MSITokenFailureClientId"), null, true);
                    }
                    else {
                        SQLServerException.makeFromDriverError(this, null, SQLServerException.getErrString("R_MSITokenFailureImds"), null, true);
                    }
                }
                catch (final IOException io) {
                    SQLServerException.makeFromDriverError(this, null, SQLServerException.getErrString("R_MSITokenFailureUnexpected"), null, true);
                }
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        if (retry > maxRetry) {
            SQLServerException.makeFromDriverError(this, null, SQLServerException.getErrString(isAzureFunction ? "R_MSITokenFailureEndpoint" : "R_MSITokenFailureImds"), null, true);
        }
        return null;
    }
    
    private void sendFedAuthToken(final FedAuthTokenCommand fedAuthCommand, final SqlFedAuthToken fedAuthToken, final TDSTokenHandler tdsTokenHandler) throws SQLServerException {
        assert null != fedAuthToken;
        assert null != fedAuthToken.accessToken;
        if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
            SQLServerConnection.connectionlogger.fine(this.toString() + " Sending federated authentication token.");
        }
        final TDSWriter tdsWriter = fedAuthCommand.startRequest((byte)8);
        final byte[] accessToken = fedAuthToken.accessToken.getBytes(StandardCharsets.UTF_16LE);
        tdsWriter.writeInt(accessToken.length + 4);
        tdsWriter.writeInt(accessToken.length);
        tdsWriter.writeBytes(accessToken, 0, accessToken.length);
        final TDSReader tdsReader = fedAuthCommand.startResponse();
        this.federatedAuthenticationRequested = true;
        TDSParser.parse(tdsReader, tdsTokenHandler);
    }
    
    final void processFeatureExtAck(final TDSReader tdsReader) throws SQLServerException {
        tdsReader.readUnsignedByte();
        byte featureId;
        do {
            featureId = (byte)tdsReader.readUnsignedByte();
            if (featureId != -1) {
                final int dataLen = tdsReader.readInt();
                final byte[] data = new byte[dataLen];
                if (dataLen > 0) {
                    tdsReader.readBytes(data, 0, dataLen);
                }
                this.onFeatureExtAck(featureId, data);
            }
        } while (featureId != -1);
    }
    
    private void onFeatureExtAck(final byte featureId, final byte[] data) throws SQLServerException {
        if (null != this.routingInfo) {
            return;
        }
        Label_0826: {
            switch (featureId) {
                case 2: {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for federated authentication.");
                    }
                    if (!this.federatedAuthenticationRequested) {
                        if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                            SQLServerConnection.connectionlogger.severe(this.toString() + " Did not request federated authentication.");
                        }
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnrequestedFeatureAckReceived"));
                        final Object[] msgArgs = { featureId };
                        throw new SQLServerException(form.format(msgArgs), (Throwable)null);
                    }
                    assert null != this.fedAuthFeatureExtensionData;
                    switch (this.fedAuthFeatureExtensionData.libraryType) {
                        case 1:
                        case 2: {
                            if (0 != data.length) {
                                if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                                    SQLServerConnection.connectionlogger.severe(this.toString() + " Federated authentication feature extension ack for ADAL and Security Token includes extra data.");
                                }
                                throw new SQLServerException(SQLServerException.getErrString("R_FedAuthFeatureAckContainsExtraData"), (Throwable)null);
                            }
                            break Label_0826;
                        }
                        default: {
                            assert false;
                            if (SQLServerConnection.connectionlogger.isLoggable(Level.SEVERE)) {
                                SQLServerConnection.connectionlogger.severe(this.toString() + " Attempting to use unknown federated authentication library.");
                            }
                            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_FedAuthFeatureAckUnknownLibraryType"));
                            final Object[] msgArgs = { this.fedAuthFeatureExtensionData.libraryType };
                            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
                        }
                    }
                    break;
                }
                case 4: {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for AE.");
                    }
                    if (1 > data.length) {
                        throw new SQLServerException(SQLServerException.getErrString("R_InvalidAEVersionNumber"), (Throwable)null);
                    }
                    this.aeVersion = data[0];
                    if (0 == this.aeVersion || this.aeVersion > 2) {
                        throw new SQLServerException(SQLServerException.getErrString("R_InvalidAEVersionNumber"), (Throwable)null);
                    }
                    this.serverColumnEncryptionVersion = ColumnEncryptionVersion.AE_v1;
                    if (null == this.enclaveAttestationUrl) {
                        break;
                    }
                    if (this.aeVersion < 2) {
                        throw new SQLServerException(SQLServerException.getErrString("R_enclaveNotSupported"), (Throwable)null);
                    }
                    this.serverColumnEncryptionVersion = ColumnEncryptionVersion.AE_v2;
                    this.enclaveType = new String(data, 2, data.length - 2, StandardCharsets.UTF_16LE);
                    if (!EnclaveType.isValidEnclaveType(this.enclaveType)) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_enclaveTypeInvalid"));
                        final Object[] msgArgs = { this.enclaveType };
                        throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                    }
                    break;
                }
                case 9: {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for Data Classification.");
                    }
                    if (2 != data.length) {
                        throw new SQLServerException(SQLServerException.getErrString("R_UnknownDataClsTokenNumber"), (Throwable)null);
                    }
                    final byte supportedDataClassificationVersion = data[0];
                    if (0 == supportedDataClassificationVersion || supportedDataClassificationVersion > 1) {
                        throw new SQLServerException(SQLServerException.getErrString("R_InvalidDataClsVersionNumber"), (Throwable)null);
                    }
                    final byte enabled = data[1];
                    this.serverSupportsDataClassification = (enabled != 0);
                    break;
                }
                case 10: {
                    if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                        SQLServerConnection.connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for UTF8 support.");
                    }
                    if (1 > data.length) {
                        throw new SQLServerException(SQLServerException.getErrString("R_unknownUTF8SupportValue"), (Throwable)null);
                    }
                    break;
                }
                default: {
                    throw new SQLServerException(SQLServerException.getErrString("R_UnknownFeatureAck"), (Throwable)null);
                }
            }
        }
    }
    
    private void executeDTCCommand(final int requestType, final byte[] payload, final String logContext) throws SQLServerException {
        final class DTCCommand extends UninterruptableTDSCommand
        {
            private static final long serialVersionUID = 1L;
            private final int requestType = requestType;
            private final byte[] payload = payload;
            
            DTCCommand(final byte[] payload, final String logContext) {
                super(logContext);
            }
            
            @Override
            final boolean doExecute() throws SQLServerException {
                final TDSWriter tdsWriter = this.startRequest((byte)14);
                tdsWriter.sendEnclavePackage(null, null);
                tdsWriter.writeShort((short)this.requestType);
                if (null == this.payload) {
                    tdsWriter.writeShort((short)0);
                }
                else {
                    assert this.payload.length <= 32767;
                    tdsWriter.writeShort((short)this.payload.length);
                    tdsWriter.writeBytes(this.payload);
                }
                TDSParser.parse(this.startResponse(), this.getLogContext());
                return true;
            }
        }
        this.executeCommand(new DTCCommand(payload));
    }
    
    final void JTAUnenlistConnection() throws SQLServerException {
        this.executeDTCCommand(1, null, "MS_DTC delist connection");
        this.inXATransaction = false;
    }
    
    final void JTAEnlistConnection(final byte[] cookie) throws SQLServerException {
        this.executeDTCCommand(1, cookie, "MS_DTC enlist connection");
        this.connectionCommand(this.sqlStatementToSetTransactionIsolationLevel(), "JTAEnlistConnection");
        this.inXATransaction = true;
    }
    
    private byte[] toUCS16(final String s) {
        if (s == null) {
            return new byte[0];
        }
        final int l = s.length();
        final byte[] data = new byte[l * 2];
        int offset = 0;
        for (int i = 0; i < l; ++i) {
            final int c = s.charAt(i);
            final byte b1 = (byte)(c & 0xFF);
            data[offset++] = b1;
            data[offset++] = (byte)(c >> 8 & 0xFF);
        }
        return data;
    }
    
    private byte[] encryptPassword(String pwd) {
        if (pwd == null) {
            pwd = "";
        }
        final int len = pwd.length();
        final byte[] data = new byte[len * 2];
        for (int i1 = 0; i1 < len; ++i1) {
            int j1 = pwd.charAt(i1) ^ '\u5a5a';
            j1 = ((j1 & 0xF) << 4 | (j1 & 0xF0) >> 4 | (j1 & 0xF00) << 4 | (j1 & 0xF000) >> 4);
            final byte b1 = (byte)((j1 & 0xFF00) >> 8);
            data[i1 * 2 + 1] = b1;
            final byte b2 = (byte)(j1 & 0xFF);
            data[i1 * 2 + 0] = b2;
        }
        return data;
    }
    
    private void sendLogon(final LogonCommand logonCommand, final SSPIAuthentication authentication, final FederatedAuthenticationFeatureExtensionData fedAuthFeatureExtensionData) throws SQLServerException {
        assert !this.fedAuthRequiredPreLoginResponse;
        assert !this.federatedAuthenticationInfoRequested && !this.federatedAuthenticationRequested;
        assert !(!this.federatedAuthenticationRequested);
        assert !this.federatedAuthenticationInfoRequested && !this.federatedAuthenticationRequested;
        String sUser = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString());
        String sPwd = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString());
        final String appName = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.APPLICATION_NAME.toString());
        final String interfaceLibName = "Microsoft JDBC Driver 8.2";
        final String databaseName = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.DATABASE_NAME.toString());
        String serverName = (null != this.currentConnectPlaceHolder) ? this.currentConnectPlaceHolder.getServerName() : this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_NAME.toString());
        if (null != serverName && serverName.length() > 128) {
            serverName = serverName.substring(0, 128);
        }
        byte[] secBlob = new byte[0];
        final boolean[] done = { false };
        if (null != authentication) {
            secBlob = authentication.generateClientContext(secBlob, done);
            sUser = null;
            sPwd = null;
        }
        final byte[] hostnameBytes = this.toUCS16(this.hostName);
        final byte[] userBytes = this.toUCS16(sUser);
        final byte[] passwordBytes = this.encryptPassword(sPwd);
        final int passwordLen = (null != passwordBytes) ? passwordBytes.length : 0;
        final byte[] appNameBytes = this.toUCS16(appName);
        final byte[] serverNameBytes = this.toUCS16(serverName);
        final byte[] interfaceLibNameBytes = this.toUCS16(interfaceLibName);
        final byte[] interfaceLibVersionBytes = { 0, 2, 2, 8 };
        final byte[] databaseNameBytes = this.toUCS16(databaseName);
        final byte[] netAddress = new byte[6];
        int dataLen = 0;
        if (this.serverMajorVersion >= 11) {
            this.tdsVersion = 1946157060;
        }
        else if (this.serverMajorVersion >= 10) {
            this.tdsVersion = 1930100739;
        }
        else if (this.serverMajorVersion >= 9) {
            this.tdsVersion = 1913192450;
        }
        else {
            assert false : "prelogin did not disconnect for the old version: " + this.serverMajorVersion;
        }
        final int tdsLoginRequestBaseLength = 94;
        final TDSWriter tdsWriter = logonCommand.startRequest((byte)16);
        int len = 94 + hostnameBytes.length + appNameBytes.length + serverNameBytes.length + interfaceLibNameBytes.length + databaseNameBytes.length + ((secBlob != null) ? secBlob.length : 0) + 4;
        if (!this.integratedSecurity && !this.federatedAuthenticationInfoRequested && !this.federatedAuthenticationRequested) {
            len = len + passwordLen + userBytes.length;
        }
        final int aeOffset = len;
        len += this.writeAEFeatureRequest(false, tdsWriter);
        if (this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested) {
            len += this.writeFedAuthFeatureRequest(false, tdsWriter, fedAuthFeatureExtensionData);
        }
        len += this.writeDataClassificationFeatureRequest(false, tdsWriter);
        len += this.writeUTF8SupportFeatureRequest(false, tdsWriter);
        ++len;
        tdsWriter.writeInt(len);
        tdsWriter.writeInt(this.tdsVersion);
        tdsWriter.writeInt(this.requestedPacketSize);
        tdsWriter.writeBytes(interfaceLibVersionBytes);
        tdsWriter.writeInt(0);
        tdsWriter.writeInt(0);
        tdsWriter.writeByte((byte)(-32));
        tdsWriter.writeByte((byte)(0x3 | (this.integratedSecurity ? -128 : 0)));
        tdsWriter.writeByte((byte)(0x0 | ((this.applicationIntent != null && this.applicationIntent.equals(ApplicationIntent.READ_ONLY)) ? 32 : 0)));
        final byte colEncSetting = 16;
        tdsWriter.writeByte((byte)(0x0 | colEncSetting | ((this.serverMajorVersion >= 10) ? 8 : 0)));
        tdsWriter.writeInt(0);
        tdsWriter.writeInt(0);
        tdsWriter.writeShort((short)94);
        tdsWriter.writeShort((short)((this.hostName != null && !this.hostName.isEmpty()) ? this.hostName.length() : 0));
        dataLen += hostnameBytes.length;
        if (this.ntlmAuthentication) {
            tdsWriter.writeShort((short)(94 + dataLen));
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)(94 + dataLen));
            tdsWriter.writeShort((short)0);
        }
        else if (!this.integratedSecurity && !this.federatedAuthenticationInfoRequested && !this.federatedAuthenticationRequested) {
            tdsWriter.writeShort((short)(94 + dataLen));
            tdsWriter.writeShort((short)((sUser == null) ? 0 : sUser.length()));
            dataLen += userBytes.length;
            tdsWriter.writeShort((short)(94 + dataLen));
            tdsWriter.writeShort((short)((sPwd == null) ? 0 : sPwd.length()));
            dataLen += passwordLen;
        }
        else {
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
        }
        tdsWriter.writeShort((short)(94 + dataLen));
        tdsWriter.writeShort((short)((appName == null) ? 0 : appName.length()));
        dataLen += appNameBytes.length;
        tdsWriter.writeShort((short)(94 + dataLen));
        tdsWriter.writeShort((short)((serverName == null) ? 0 : serverName.length()));
        dataLen += serverNameBytes.length;
        tdsWriter.writeShort((short)(94 + dataLen));
        tdsWriter.writeShort((short)4);
        dataLen += 4;
        assert null != interfaceLibName;
        tdsWriter.writeShort((short)(94 + dataLen));
        tdsWriter.writeShort((short)interfaceLibName.length());
        dataLen += interfaceLibNameBytes.length;
        tdsWriter.writeShort((short)0);
        tdsWriter.writeShort((short)0);
        tdsWriter.writeShort((short)(94 + dataLen));
        tdsWriter.writeShort((short)((databaseName == null) ? 0 : databaseName.length()));
        dataLen += databaseNameBytes.length;
        tdsWriter.writeBytes(netAddress);
        final int uShortMax = 65535;
        if (!this.integratedSecurity) {
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
        }
        else {
            tdsWriter.writeShort((short)(94 + dataLen));
            if (65535 <= secBlob.length) {
                tdsWriter.writeShort((short)(-1));
            }
            else {
                tdsWriter.writeShort((short)secBlob.length);
            }
        }
        tdsWriter.writeShort((short)0);
        tdsWriter.writeShort((short)0);
        if (this.tdsVersion >= 1913192450) {
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
            if (null != secBlob && 65535 <= secBlob.length) {
                tdsWriter.writeInt(secBlob.length);
            }
            else {
                tdsWriter.writeInt(0);
            }
        }
        tdsWriter.writeBytes(hostnameBytes);
        tdsWriter.setDataLoggable(false);
        if (!this.integratedSecurity && !this.federatedAuthenticationInfoRequested && !this.federatedAuthenticationRequested) {
            tdsWriter.writeBytes(userBytes);
            tdsWriter.writeBytes(passwordBytes);
        }
        tdsWriter.setDataLoggable(true);
        tdsWriter.writeBytes(appNameBytes);
        tdsWriter.writeBytes(serverNameBytes);
        tdsWriter.writeInt(aeOffset);
        tdsWriter.writeBytes(interfaceLibNameBytes);
        tdsWriter.writeBytes(databaseNameBytes);
        tdsWriter.setDataLoggable(false);
        if (this.integratedSecurity) {
            tdsWriter.writeBytes(secBlob, 0, secBlob.length);
        }
        this.writeAEFeatureRequest(true, tdsWriter);
        if (this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested) {
            this.writeFedAuthFeatureRequest(true, tdsWriter, fedAuthFeatureExtensionData);
        }
        this.writeDataClassificationFeatureRequest(true, tdsWriter);
        this.writeUTF8SupportFeatureRequest(true, tdsWriter);
        tdsWriter.writeByte((byte)(-1));
        tdsWriter.setDataLoggable(true);
        final class LogonProcessor extends TDSTokenHandler
        {
            private final SSPIAuthentication auth = authentication;
            private byte[] secBlobOut;
            StreamLoginAck loginAckToken;
            
            LogonProcessor() {
                super("logon");
                this.secBlobOut = null;
                this.loginAckToken = null;
            }
            
            @Override
            boolean onSSPI(final TDSReader tdsReader) throws SQLServerException {
                final StreamSSPI ack = new StreamSSPI();
                ack.setFromTDS(tdsReader);
                final boolean[] done = { false };
                this.secBlobOut = this.auth.generateClientContext(ack.sspiBlob, done);
                return true;
            }
            
            @Override
            boolean onLoginAck(final TDSReader tdsReader) throws SQLServerException {
                (this.loginAckToken = new StreamLoginAck()).setFromTDS(tdsReader);
                SQLServerConnection.this.sqlServerVersion = this.loginAckToken.sSQLServerVersion;
                SQLServerConnection.this.tdsVersion = this.loginAckToken.tdsVersion;
                return true;
            }
            
            final boolean complete(final LogonCommand logonCommand, final TDSReader tdsReader) throws SQLServerException {
                if (null != this.loginAckToken) {
                    return true;
                }
                if (null != this.secBlobOut && 0 != this.secBlobOut.length) {
                    logonCommand.startRequest((byte)17).writeBytes(this.secBlobOut, 0, this.secBlobOut.length);
                    return false;
                }
                logonCommand.startRequest((byte)17);
                logonCommand.onRequestComplete();
                final TDSChannel access$300 = SQLServerConnection.this.tdsChannel;
                ++access$300.numMsgsSent;
                TDSParser.parse(tdsReader, this);
                return true;
            }
        }
        final LogonProcessor logonProcessor = new LogonProcessor();
        TDSReader tdsReader;
        do {
            tdsReader = logonCommand.startResponse();
            TDSParser.parse(tdsReader, logonProcessor);
        } while (!logonProcessor.complete(logonCommand, tdsReader));
    }
    
    private void checkValidHoldability(final int holdability) throws SQLServerException {
        if (holdability != 1 && holdability != 2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidHoldability"));
            SQLServerException.makeFromDriverError(this, this, form.format(new Object[] { holdability }), null, true);
        }
    }
    
    private void checkMatchesCurrentHoldability(final int resultSetHoldability) throws SQLServerException {
        if (resultSetHoldability != this.holdability) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_sqlServerHoldability"), null, false);
        }
    }
    
    @Override
    public Statement createStatement(final int nType, final int nConcur, final int resultSetHoldability) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "createStatement", new Object[] { nType, nConcur, resultSetHoldability });
        final Statement st = this.createStatement(nType, nConcur, resultSetHoldability, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "createStatement", st);
        return st;
    }
    
    @Override
    public Statement createStatement(final int nType, final int nConcur, final int resultSetHoldability, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "createStatement", new Object[] { nType, nConcur, resultSetHoldability, stmtColEncSetting });
        this.checkClosed();
        this.checkValidHoldability(resultSetHoldability);
        this.checkMatchesCurrentHoldability(resultSetHoldability);
        final Statement st = new SQLServerStatement(this, nType, nConcur, stmtColEncSetting);
        if (this.requestStarted) {
            this.addOpenStatement((ISQLServerStatement)st);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "createStatement", st);
        return st;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int nType, final int nConcur, final int resultSetHoldability) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { nType, nConcur, resultSetHoldability });
        final PreparedStatement st = this.prepareStatement(sql, nType, nConcur, resultSetHoldability, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", st);
        return st;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int nType, final int nConcur, final int resultSetHoldability, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { nType, nConcur, resultSetHoldability, stmtColEncSetting });
        this.checkClosed();
        this.checkValidHoldability(resultSetHoldability);
        this.checkMatchesCurrentHoldability(resultSetHoldability);
        final PreparedStatement st = new SQLServerPreparedStatement(this, sql, nType, nConcur, stmtColEncSetting);
        if (this.requestStarted) {
            this.addOpenStatement((ISQLServerStatement)st);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", st);
        return st;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int nType, final int nConcur, final int resultSetHoldability) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { nType, nConcur, resultSetHoldability });
        final CallableStatement st = this.prepareCall(sql, nType, nConcur, resultSetHoldability, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareCall", st);
        return st;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int nType, final int nConcur, final int resultSetHoldability, final SQLServerStatementColumnEncryptionSetting stmtColEncSetiing) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { nType, nConcur, resultSetHoldability, stmtColEncSetiing });
        this.checkClosed();
        this.checkValidHoldability(resultSetHoldability);
        this.checkMatchesCurrentHoldability(resultSetHoldability);
        final CallableStatement st = new SQLServerCallableStatement(this, sql, nType, nConcur, stmtColEncSetiing);
        if (this.requestStarted) {
            this.addOpenStatement((ISQLServerStatement)st);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareCall", st);
        return st;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int flag) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, flag });
        }
        final SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, flag, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", ps);
        return ps;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int flag, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, flag, stmtColEncSetting });
        }
        this.checkClosed();
        final SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, 1003, 1007, stmtColEncSetting);
        ps.bRequestedGeneratedKeys = (flag == 1);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", ps);
        return ps;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, columnIndexes });
        }
        final SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, columnIndexes, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", ps);
        return ps;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, columnIndexes, stmtColEncSetting });
        this.checkClosed();
        if (columnIndexes == null || columnIndexes.length != 1) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_invalidColumnArrayLength"), null, false);
        }
        final SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, 1003, 1007, stmtColEncSetting);
        ps.bRequestedGeneratedKeys = true;
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", ps);
        return ps;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLServerException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, columnNames });
        }
        final SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, columnNames, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", ps);
        return ps;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "prepareStatement", new Object[] { sql, columnNames, stmtColEncSetting });
        this.checkClosed();
        if (columnNames == null || columnNames.length != 1) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_invalidColumnArrayLength"), null, false);
        }
        final SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, 1003, 1007, stmtColEncSetting);
        ps.bRequestedGeneratedKeys = true;
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "prepareStatement", ps);
        return ps;
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "releaseSavepoint", savepoint);
        SQLServerException.throwNotSupportedException(this, null);
    }
    
    private final Savepoint setNamedSavepoint(final String sName) throws SQLServerException {
        if (this.databaseAutoCommitMode) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_cantSetSavepoint"), null, false);
        }
        final SQLServerSavepoint s = new SQLServerSavepoint(this, sName);
        this.connectionCommand("IF @@TRANCOUNT = 0 BEGIN BEGIN TRAN IF @@TRANCOUNT = 2 COMMIT TRAN END SAVE TRAN " + Util.escapeSQLId(s.getLabel()), "setSavepoint");
        return s;
    }
    
    @Override
    public Savepoint setSavepoint(final String sName) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setSavepoint", sName);
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final Savepoint pt = this.setNamedSavepoint(sName);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setSavepoint", pt);
        return pt;
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setSavepoint");
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final Savepoint pt = this.setNamedSavepoint(null);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setSavepoint", pt);
        return pt;
    }
    
    @Override
    public void rollback(final Savepoint s) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "rollback", s);
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        if (this.databaseAutoCommitMode) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_cantInvokeRollback"), null, false);
        }
        this.connectionCommand("IF @@TRANCOUNT > 0 ROLLBACK TRAN " + Util.escapeSQLId(((SQLServerSavepoint)s).getLabel()), "rollbackSavepoint");
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "rollback");
    }
    
    @Override
    public int getHoldability() throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getHoldability");
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getHoldability", this.holdability);
        }
        return this.holdability;
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLServerException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setHoldability", holdability);
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerConnection.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkValidHoldability(holdability);
        this.checkClosed();
        if (this.holdability != holdability) {
            assert 2 == holdability : "invalid holdability " + holdability;
            this.connectionCommand((holdability == 2) ? "SET CURSOR_CLOSE_ON_COMMIT ON" : "SET CURSOR_CLOSE_ON_COMMIT OFF", "setHoldability");
            this.holdability = holdability;
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setHoldability");
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getNetworkTimeout");
        this.checkClosed();
        int timeout = 0;
        try {
            timeout = this.tdsChannel.getNetworkTimeout();
        }
        catch (final IOException ioe) {
            this.terminate(3, ioe.getMessage(), ioe);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getNetworkTimeout");
        return timeout;
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int timeout) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setNetworkTimeout", timeout);
        if (timeout < 0) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSocketTimeout"));
            final Object[] msgArgs = { timeout };
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
        }
        this.checkClosed();
        final SecurityManager secMgr = System.getSecurityManager();
        if (secMgr != null) {
            try {
                final SQLPermission perm = new SQLPermission("setNetworkTimeout");
                secMgr.checkPermission(perm);
            }
            catch (final SecurityException ex) {
                final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_permissionDenied"));
                final Object[] msgArgs2 = { "setNetworkTimeout" };
                SQLServerException.makeFromDriverError(this, this, form2.format(msgArgs2), null, true);
            }
        }
        try {
            this.tdsChannel.setNetworkTimeout(timeout);
        }
        catch (final IOException ioe) {
            this.terminate(3, ioe.getMessage(), ioe);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setNetworkTimeout");
    }
    
    @Override
    public String getSchema() throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getSchema");
        this.checkClosed();
        try (final SQLServerStatement stmt = (SQLServerStatement)this.createStatement();
             final SQLServerResultSet resultSet = stmt.executeQueryInternal("SELECT SCHEMA_NAME()")) {
            if (resultSet != null) {
                resultSet.next();
                final String string = resultSet.getString(1);
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                return string;
            }
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_getSchemaError"), null, true);
        }
        catch (final SQLException e) {
            if (this.isSessionUnAvailable()) {
                throw e;
            }
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_getSchemaError"), null, true);
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getSchema");
        return null;
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setSchema", schema);
        this.checkClosed();
        this.addWarning(SQLServerException.getErrString("R_setSchemaWarning"));
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setSchema");
    }
    
    @Override
    public void setSendTimeAsDatetime(final boolean sendTimeAsDateTimeValue) {
        this.sendTimeAsDatetime = sendTimeAsDateTimeValue;
    }
    
    @Override
    public void setUseFmtOnly(final boolean useFmtOnly) {
        this.useFmtOnly = useFmtOnly;
    }
    
    @Override
    public final boolean getUseFmtOnly() {
        return this.useFmtOnly;
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        SQLServerException.throwNotSupportedException(this, null);
        return null;
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        this.checkClosed();
        return new SQLServerBlob(this);
    }
    
    @Override
    public Clob createClob() throws SQLException {
        this.checkClosed();
        return new SQLServerClob(this);
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        this.checkClosed();
        return new SQLServerNClob(this);
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "createSQLXML");
        final SQLXML sqlxml = new SQLServerSQLXML(this);
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "createSQLXML", sqlxml);
        }
        return sqlxml;
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        SQLServerException.throwNotSupportedException(this, null);
        return null;
    }
    
    String getTrustedServerNameAE() throws SQLServerException {
        return this.trustedServerNameAE.toUpperCase();
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getClientInfo");
        this.checkClosed();
        final Properties p = new Properties();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getClientInfo", p);
        return p;
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "getClientInfo", name);
        this.checkClosed();
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "getClientInfo", null);
        return null;
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setClientInfo", properties);
        try {
            this.checkClosed();
        }
        catch (final SQLServerException ex) {
            final SQLClientInfoException info = new SQLClientInfoException();
            info.initCause(ex);
            throw info;
        }
        if (!properties.isEmpty()) {
            final Enumeration<?> e = ((Hashtable<?, V>)properties).keys();
            while (e.hasMoreElements()) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProperty"));
                final Object[] msgArgs = { e.nextElement() };
                this.addWarning(form.format(msgArgs));
            }
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setClientInfo");
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "setClientInfo", new Object[] { name, value });
        }
        try {
            this.checkClosed();
        }
        catch (final SQLServerException ex) {
            final SQLClientInfoException info = new SQLClientInfoException();
            info.initCause(ex);
            throw info;
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProperty"));
        final Object[] msgArgs = { name };
        this.addWarning(form.format(msgArgs));
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "setClientInfo");
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "isValid", timeout);
        if (timeout < 0) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidQueryTimeOutValue"));
            final Object[] msgArgs = { timeout };
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, true);
        }
        if (this.isSessionUnAvailable()) {
            return false;
        }
        boolean isValid = true;
        try (final SQLServerStatement stmt = new SQLServerStatement(this, 1003, 1007, SQLServerStatementColumnEncryptionSetting.UseConnectionSetting)) {
            if (0 != timeout) {
                stmt.setQueryTimeout(timeout);
            }
            stmt.executeQueryInternal("SELECT 1");
        }
        catch (final SQLException e) {
            isValid = false;
            SQLServerConnection.connectionlogger.fine(this.toString() + " Exception checking connection validity: " + e.getMessage());
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "isValid", isValid);
        return isValid;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "isWrapperFor", iface);
        final boolean f = iface.isInstance(this);
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "isWrapperFor", f);
        return f;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "unwrap", iface);
        T t;
        try {
            t = iface.cast(this);
        }
        catch (final ClassCastException e) {
            final SQLServerException newe = new SQLServerException(e.getMessage(), e);
            throw newe;
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "unwrap", t);
        return t;
    }
    
    protected void beginRequestInternal() throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "beginRequest", this);
        synchronized (this) {
            if (!this.requestStarted) {
                this.originalDatabaseAutoCommitMode = this.databaseAutoCommitMode;
                this.originalTransactionIsolationLevel = this.transactionIsolationLevel;
                this.originalNetworkTimeout = this.getNetworkTimeout();
                this.originalHoldability = this.holdability;
                this.originalSendTimeAsDatetime = this.sendTimeAsDatetime;
                this.originalStatementPoolingCacheSize = this.statementPoolingCacheSize;
                this.originalDisableStatementPooling = this.disableStatementPooling;
                this.originalServerPreparedStatementDiscardThreshold = this.getServerPreparedStatementDiscardThreshold();
                this.originalEnablePrepareOnFirstPreparedStatementCall = this.getEnablePrepareOnFirstPreparedStatementCall();
                this.originalSCatalog = this.sCatalog;
                this.originalUseBulkCopyForBatchInsert = this.getUseBulkCopyForBatchInsert();
                this.originalSqlWarnings = this.sqlWarnings;
                this.openStatements = new LinkedList<ISQLServerStatement>();
                this.originalUseFmtOnly = this.useFmtOnly;
                this.requestStarted = true;
            }
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "beginRequest", this);
    }
    
    protected void endRequestInternal() throws SQLException {
        SQLServerConnection.loggerExternal.entering(this.getClassNameLogging(), "endRequest", this);
        synchronized (this) {
            if (this.requestStarted) {
                if (!this.databaseAutoCommitMode) {
                    this.rollback();
                }
                if (this.databaseAutoCommitMode != this.originalDatabaseAutoCommitMode) {
                    this.setAutoCommit(this.originalDatabaseAutoCommitMode);
                }
                if (this.transactionIsolationLevel != this.originalTransactionIsolationLevel) {
                    this.setTransactionIsolation(this.originalTransactionIsolationLevel);
                }
                if (this.getNetworkTimeout() != this.originalNetworkTimeout) {
                    this.setNetworkTimeout(null, this.originalNetworkTimeout);
                }
                if (this.holdability != this.originalHoldability) {
                    this.setHoldability(this.originalHoldability);
                }
                if (this.sendTimeAsDatetime != this.originalSendTimeAsDatetime) {
                    this.setSendTimeAsDatetime(this.originalSendTimeAsDatetime);
                }
                if (this.useFmtOnly != this.originalUseFmtOnly) {
                    this.setUseFmtOnly(this.originalUseFmtOnly);
                }
                if (this.statementPoolingCacheSize != this.originalStatementPoolingCacheSize) {
                    this.setStatementPoolingCacheSize(this.originalStatementPoolingCacheSize);
                }
                if (this.disableStatementPooling != this.originalDisableStatementPooling) {
                    this.setDisableStatementPooling(this.originalDisableStatementPooling);
                }
                if (this.getServerPreparedStatementDiscardThreshold() != this.originalServerPreparedStatementDiscardThreshold) {
                    this.setServerPreparedStatementDiscardThreshold(this.originalServerPreparedStatementDiscardThreshold);
                }
                if (this.getEnablePrepareOnFirstPreparedStatementCall() != this.originalEnablePrepareOnFirstPreparedStatementCall) {
                    this.setEnablePrepareOnFirstPreparedStatementCall(this.originalEnablePrepareOnFirstPreparedStatementCall);
                }
                if (!this.sCatalog.equals(this.originalSCatalog)) {
                    this.setCatalog(this.originalSCatalog);
                }
                if (this.getUseBulkCopyForBatchInsert() != this.originalUseBulkCopyForBatchInsert) {
                    this.setUseBulkCopyForBatchInsert(this.originalUseBulkCopyForBatchInsert);
                }
                this.sqlWarnings = this.originalSqlWarnings;
                if (null != this.openStatements) {
                    while (!this.openStatements.isEmpty()) {
                        try (final Statement st = this.openStatements.get(0)) {}
                    }
                    this.openStatements.clear();
                }
                this.requestStarted = false;
            }
        }
        SQLServerConnection.loggerExternal.exiting(this.getClassNameLogging(), "endRequest", this);
    }
    
    String replaceParameterMarkers(final String sqlSrc, final int[] paramPositions, final Parameter[] params, final boolean isReturnValueSyntax) throws SQLServerException {
        final int MAX_PARAM_NAME_LEN = 6;
        final char[] sqlDst = new char[sqlSrc.length() + params.length * (6 + SQLServerConnection.OUT.length)];
        int dstBegin = 0;
        int srcBegin = 0;
        int nParam = 0;
        int paramIndex = 0;
        while (true) {
            final int srcEnd = (paramIndex >= paramPositions.length) ? sqlSrc.length() : paramPositions[paramIndex];
            sqlSrc.getChars(srcBegin, srcEnd, sqlDst, dstBegin);
            dstBegin += srcEnd - srcBegin;
            if (sqlSrc.length() == srcEnd) {
                break;
            }
            dstBegin += makeParamName(nParam++, sqlDst, dstBegin);
            srcBegin = srcEnd + 1;
            if (!params[paramIndex++].isOutput() || (isReturnValueSyntax && paramIndex <= 1)) {
                continue;
            }
            System.arraycopy(SQLServerConnection.OUT, 0, sqlDst, dstBegin, SQLServerConnection.OUT.length);
            dstBegin += SQLServerConnection.OUT.length;
        }
        return new String(sqlDst, 0, dstBegin);
    }
    
    static int makeParamName(final int nParam, final char[] name, final int offset) {
        name[offset + 0] = '@';
        name[offset + 1] = 'P';
        if (nParam < 10) {
            name[offset + 2] = (char)(48 + nParam);
            return 3;
        }
        if (nParam < 100) {
            int nBase;
            for (nBase = 2; nParam >= nBase * 10; ++nBase) {}
            name[offset + 2] = (char)(48 + (nBase - 1));
            name[offset + 3] = (char)(48 + (nParam - (nBase - 1) * 10));
            return 4;
        }
        final String sParam = "" + nParam;
        sParam.getChars(0, sParam.length(), name, offset + 2);
        return 2 + sParam.length();
    }
    
    void notifyPooledConnection(final SQLServerException e) {
        synchronized (this) {
            if (null != this.pooledConnectionParent) {
                this.pooledConnectionParent.notifyEvent(e);
            }
        }
    }
    
    void DetachFromPool() {
        synchronized (this) {
            this.pooledConnectionParent = null;
        }
    }
    
    String getInstancePort(final String server, final String instanceName) throws SQLServerException {
        String browserResult = null;
        DatagramSocket datagramSocket = null;
        String lastErrorMessage = null;
        try {
            lastErrorMessage = "Failed to determine instance for the : " + server + " instance:" + instanceName;
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.setSoTimeout(1000);
            }
            catch (final SocketException socketException) {
                lastErrorMessage = "Unable to create local datagram socket";
                throw socketException;
            }
            assert null != datagramSocket;
            try {
                if (this.multiSubnetFailover) {
                    final InetAddress[] inetAddrs = InetAddress.getAllByName(server);
                    assert null != inetAddrs;
                    for (final InetAddress inetAddr : inetAddrs) {
                        try {
                            final byte[] sendBuffer = (" " + instanceName).getBytes();
                            sendBuffer[0] = 4;
                            final DatagramPacket udpRequest = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddr, 1434);
                            datagramSocket.send(udpRequest);
                        }
                        catch (final IOException ioException) {
                            lastErrorMessage = "Error sending SQL Server Browser Service UDP request to address: " + inetAddr + ", port: " + 1434;
                            throw ioException;
                        }
                    }
                }
                else {
                    final InetAddress inetAddr2 = InetAddress.getByName(server);
                    assert null != inetAddr2;
                    try {
                        final byte[] sendBuffer2 = (" " + instanceName).getBytes();
                        sendBuffer2[0] = 4;
                        final DatagramPacket udpRequest2 = new DatagramPacket(sendBuffer2, sendBuffer2.length, inetAddr2, 1434);
                        datagramSocket.send(udpRequest2);
                    }
                    catch (final IOException ioException2) {
                        lastErrorMessage = "Error sending SQL Server Browser Service UDP request to address: " + inetAddr2 + ", port: " + 1434;
                        throw ioException2;
                    }
                }
            }
            catch (final UnknownHostException unknownHostException) {
                lastErrorMessage = "Unable to determine IP address of host: " + server;
                throw unknownHostException;
            }
            try {
                final byte[] receiveBuffer = new byte[4096];
                final DatagramPacket udpResponse = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                datagramSocket.receive(udpResponse);
                browserResult = new String(receiveBuffer, 3, receiveBuffer.length - 3);
                if (SQLServerConnection.connectionlogger.isLoggable(Level.FINER)) {
                    SQLServerConnection.connectionlogger.fine(this.toString() + " Received SSRP UDP response from IP address: " + udpResponse.getAddress().getHostAddress());
                }
            }
            catch (final IOException ioException3) {
                lastErrorMessage = "Error receiving SQL Server Browser Service UDP response from server: " + server;
                throw ioException3;
            }
        }
        catch (final IOException ioException3) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_sqlBrowserFailed"));
            final Object[] msgArgs = { server, instanceName, ioException3.toString() };
            SQLServerConnection.connectionlogger.log(Level.FINE, this.toString() + " " + lastErrorMessage, ioException3);
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), "08001", false);
        }
        finally {
            if (null != datagramSocket) {
                datagramSocket.close();
            }
        }
        assert null != browserResult;
        final int p = browserResult.indexOf("tcp;");
        if (-1 == p) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_notConfiguredToListentcpip"));
            final Object[] msgArgs = { instanceName };
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), "08001", false);
        }
        final int p2 = p + 4;
        final int p3 = browserResult.indexOf(59, p2);
        return browserResult.substring(p2, p3);
    }
    
    int getNextSavepointId() {
        return ++this.nNextSavePointId;
    }
    
    void doSecurityCheck() {
        assert null != this.currentConnectPlaceHolder;
        this.currentConnectPlaceHolder.doSecurityCheck();
    }
    
    public static synchronized void setColumnEncryptionKeyCacheTtl(final int columnEncryptionKeyCacheTTL, final TimeUnit unit) throws SQLServerException {
        if (columnEncryptionKeyCacheTTL < 0 || unit.equals(TimeUnit.MILLISECONDS) || unit.equals(TimeUnit.MICROSECONDS) || unit.equals(TimeUnit.NANOSECONDS)) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_invalidCEKCacheTtl"), null, 0, false);
        }
        SQLServerConnection.columnEncryptionKeyCacheTtl = TimeUnit.SECONDS.convert(columnEncryptionKeyCacheTTL, unit);
    }
    
    static synchronized long getColumnEncryptionKeyCacheTtl() {
        return SQLServerConnection.columnEncryptionKeyCacheTtl;
    }
    
    final void enqueueUnprepareStatementHandle(final PreparedStatementHandle statementHandle) {
        if (null == statementHandle) {
            return;
        }
        if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnection.loggerExternal.finer(this + ": Adding PreparedHandle to queue for un-prepare:" + statementHandle.getHandle());
        }
        this.discardedPreparedStatementHandles.add(statementHandle);
        this.discardedPreparedStatementHandleCount.incrementAndGet();
    }
    
    @Override
    public int getDiscardedServerPreparedStatementCount() {
        return this.discardedPreparedStatementHandleCount.get();
    }
    
    @Override
    public void closeUnreferencedPreparedStatementHandles() {
        this.unprepareUnreferencedPreparedStatementHandles(true);
    }
    
    private final void cleanupPreparedStatementDiscardActions() {
        this.discardedPreparedStatementHandles.clear();
        this.discardedPreparedStatementHandleCount.set(0);
    }
    
    @Override
    public boolean getEnablePrepareOnFirstPreparedStatementCall() {
        return null != this.enablePrepareOnFirstPreparedStatementCall && this.enablePrepareOnFirstPreparedStatementCall;
    }
    
    @Override
    public void setEnablePrepareOnFirstPreparedStatementCall(final boolean value) {
        this.enablePrepareOnFirstPreparedStatementCall = value;
    }
    
    @Override
    public int getServerPreparedStatementDiscardThreshold() {
        if (0 > this.serverPreparedStatementDiscardThreshold) {
            return 10;
        }
        return this.serverPreparedStatementDiscardThreshold;
    }
    
    @Override
    public void setServerPreparedStatementDiscardThreshold(final int value) {
        this.serverPreparedStatementDiscardThreshold = Math.max(0, value);
    }
    
    final boolean isPreparedStatementUnprepareBatchingEnabled() {
        return 1 < this.getServerPreparedStatementDiscardThreshold();
    }
    
    final void unprepareUnreferencedPreparedStatementHandles(final boolean force) {
        if (this.isSessionUnAvailable()) {
            return;
        }
        final int threshold = this.getServerPreparedStatementDiscardThreshold();
        if (force || threshold < this.getDiscardedServerPreparedStatementCount()) {
            final StringBuilder sql = new StringBuilder(threshold * 32);
            int handlesRemoved = 0;
            PreparedStatementHandle statementHandle = null;
            while (null != (statementHandle = this.discardedPreparedStatementHandles.poll())) {
                ++handlesRemoved;
                sql.append(statementHandle.isDirectSql() ? "EXEC sp_unprepare " : "EXEC sp_cursorunprepare ").append(statementHandle.getHandle()).append(';');
            }
            try {
                try (final SQLServerStatement stmt = (SQLServerStatement)this.createStatement()) {
                    stmt.isInternalEncryptionQuery = true;
                    stmt.execute(sql.toString());
                }
                if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
                    SQLServerConnection.loggerExternal.finer(this + ": Finished un-preparing handle count:" + handlesRemoved);
                }
            }
            catch (final SQLException e) {
                if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
                    SQLServerConnection.loggerExternal.log(Level.FINER, this + ": Error batch-closing at least one prepared handle", e);
                }
            }
            this.discardedPreparedStatementHandleCount.addAndGet(-handlesRemoved);
        }
    }
    
    @Override
    public boolean getDisableStatementPooling() {
        return this.disableStatementPooling;
    }
    
    @Override
    public void setDisableStatementPooling(final boolean value) {
        this.disableStatementPooling = value;
        if (!value && 0 < this.getStatementPoolingCacheSize()) {
            this.prepareCache();
        }
    }
    
    @Override
    public int getStatementPoolingCacheSize() {
        return this.statementPoolingCacheSize;
    }
    
    @Override
    public int getStatementHandleCacheEntryCount() {
        if (!this.isStatementPoolingEnabled()) {
            return 0;
        }
        return this.preparedStatementHandleCache.size();
    }
    
    @Override
    public boolean isStatementPoolingEnabled() {
        return null != this.preparedStatementHandleCache && 0 < this.getStatementPoolingCacheSize() && !this.getDisableStatementPooling();
    }
    
    @Override
    public void setStatementPoolingCacheSize(int value) {
        value = Math.max(0, value);
        this.statementPoolingCacheSize = value;
        if (!this.disableStatementPooling && value > 0) {
            this.prepareCache();
        }
        if (null != this.preparedStatementHandleCache) {
            this.preparedStatementHandleCache.setCapacity(value);
        }
        if (null != this.parameterMetadataCache) {
            this.parameterMetadataCache.setCapacity(value);
        }
    }
    
    private void prepareCache() {
        this.preparedStatementHandleCache = new ConcurrentLinkedHashMap.Builder<CityHash128Key, PreparedStatementHandle>().maximumWeightedCapacity(this.getStatementPoolingCacheSize()).listener(new PreparedStatementCacheEvictionListener()).build();
        this.parameterMetadataCache = new ConcurrentLinkedHashMap.Builder<CityHash128Key, SQLServerParameterMetaData>().maximumWeightedCapacity(this.getStatementPoolingCacheSize()).build();
    }
    
    final SQLServerParameterMetaData getCachedParameterMetadata(final CityHash128Key key) {
        if (!this.isStatementPoolingEnabled()) {
            return null;
        }
        return this.parameterMetadataCache.get(key);
    }
    
    final void registerCachedParameterMetadata(final CityHash128Key key, final SQLServerParameterMetaData pmd) {
        if (!this.isStatementPoolingEnabled() || null == pmd) {
            return;
        }
        this.parameterMetadataCache.put(key, pmd);
    }
    
    final PreparedStatementHandle getCachedPreparedStatementHandle(final CityHash128Key key) {
        if (!this.isStatementPoolingEnabled()) {
            return null;
        }
        return this.preparedStatementHandleCache.get(key);
    }
    
    final PreparedStatementHandle registerCachedPreparedStatementHandle(final CityHash128Key key, final int handle, final boolean isDirectSql) {
        if (!this.isStatementPoolingEnabled() || null == key) {
            return null;
        }
        final PreparedStatementHandle cacheItem = new PreparedStatementHandle(key, handle, isDirectSql, false);
        this.preparedStatementHandleCache.putIfAbsent(key, cacheItem);
        return cacheItem;
    }
    
    final void returnCachedPreparedStatementHandle(final PreparedStatementHandle handle) {
        handle.removeReference();
        if (handle.isEvictedFromCache() && handle.tryDiscardHandle()) {
            this.enqueueUnprepareStatementHandle(handle);
        }
    }
    
    final void evictCachedPreparedStatementHandle(final PreparedStatementHandle handle) {
        if (null == handle || null == handle.getKey()) {
            return;
        }
        this.preparedStatementHandleCache.remove(handle.getKey());
    }
    
    boolean isAzure() {
        if (null == this.isAzure) {
            try (final Statement stmt = this.createStatement();
                 final ResultSet rs = stmt.executeQuery("SELECT CAST(SERVERPROPERTY('EngineEdition') as INT)")) {
                rs.next();
                final int engineEdition = rs.getInt(1);
                this.isAzure = (engineEdition == 5 || engineEdition == 6 || engineEdition == 8);
                this.isAzureDW = (engineEdition == 6);
                this.isAzureMI = (engineEdition == 8);
            }
            catch (final SQLException e) {
                if (SQLServerConnection.loggerExternal.isLoggable(Level.FINER)) {
                    SQLServerConnection.loggerExternal.log(Level.FINER, this + ": Error retrieving server type", e);
                }
                this.isAzure = false;
                this.isAzureDW = false;
                this.isAzureMI = false;
            }
            return this.isAzure;
        }
        return this.isAzure;
    }
    
    boolean isAzureDW() {
        this.isAzure();
        return this.isAzureDW;
    }
    
    boolean isAzureMI() {
        this.isAzure();
        return this.isAzureMI;
    }
    
    final synchronized void addOpenStatement(final ISQLServerStatement st) {
        if (null != this.openStatements) {
            this.openStatements.add(st);
        }
    }
    
    final synchronized void removeOpenStatement(final ISQLServerStatement st) {
        if (null != this.openStatements) {
            this.openStatements.remove(st);
        }
    }
    
    boolean isAEv2() {
        return this.aeVersion >= 2;
    }
    
    ArrayList<byte[]> initEnclaveParameters(final String userSql, final String preparedTypeDefinitions, final Parameter[] params, final ArrayList<String> parameterNames) throws SQLServerException {
        if (!this.enclaveEstablished()) {
            this.enclaveProvider.getAttestationParameters(this.enclaveAttestationUrl);
        }
        return this.enclaveProvider.createEnclaveSession(this, userSql, preparedTypeDefinitions, params, parameterNames);
    }
    
    boolean enclaveEstablished() {
        return null != this.enclaveProvider.getEnclaveSession();
    }
    
    byte[] generateEnclavePackage(final String userSQL, final ArrayList<byte[]> enclaveCEKs) throws SQLServerException {
        return (byte[])((enclaveCEKs.size() > 0) ? this.enclaveProvider.getEnclavePackage(userSQL, enclaveCEKs) : null);
    }
    
    String getServerName() {
        return this.trustedServerNameAE;
    }
    
    static {
        SQLServerConnection.parsedSQLCache = new ConcurrentLinkedHashMap.Builder<CityHash128Key, ParsedSQLCacheItem>().maximumWeightedCapacity(100L).build();
        SQLServerConnection.globalSystemColumnEncryptionKeyStoreProviders = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows")) {
            final SQLServerColumnEncryptionCertificateStoreProvider provider = new SQLServerColumnEncryptionCertificateStoreProvider();
            SQLServerConnection.globalSystemColumnEncryptionKeyStoreProviders.put(provider.getName(), provider);
        }
        SQLServerConnection.globalCustomColumnEncryptionKeyStoreProviders = null;
        SQLServerConnection.columnEncryptionTrustedMasterKeyPaths = new HashMap<String, List<String>>();
        baseConnectionID = new AtomicInteger(0);
        connectionlogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerConnection");
        loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.Connection");
        DEFAULTPORT = SQLServerDriverIntProperty.PORT_NUMBER.getDefaultValue();
        OUT = new char[] { ' ', 'O', 'U', 'T' };
        SQLServerConnection.columnEncryptionKeyCacheTtl = TimeUnit.SECONDS.convert(2L, TimeUnit.HOURS);
    }
    
    static class CityHash128Key implements Serializable
    {
        private static final long serialVersionUID = 166788428640603097L;
        String unhashedString;
        private long[] segments;
        private int hashCode;
        
        CityHash128Key(final String sql, final String parametersDefinition) {
            this(sql + parametersDefinition);
        }
        
        CityHash128Key(final String s) {
            this.unhashedString = s;
            final byte[] bytes = new byte[s.length()];
            s.getBytes(0, s.length(), bytes, 0);
            this.segments = CityHash.cityHash128(bytes, 0, bytes.length);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof CityHash128Key && Arrays.equals(this.segments, ((CityHash128Key)obj).segments) && this.unhashedString.equals(((CityHash128Key)obj).unhashedString);
        }
        
        @Override
        public int hashCode() {
            if (0 == this.hashCode) {
                this.hashCode = Arrays.hashCode(this.segments);
            }
            return this.hashCode;
        }
    }
    
    class PreparedStatementHandle
    {
        private int handle;
        private final AtomicInteger handleRefCount;
        private boolean isDirectSql;
        private volatile boolean evictedFromCache;
        private volatile boolean explicitlyDiscarded;
        private CityHash128Key key;
        
        PreparedStatementHandle(final CityHash128Key key, final int handle, final boolean isDirectSql, final boolean isEvictedFromCache) {
            this.handle = 0;
            this.handleRefCount = new AtomicInteger();
            this.key = key;
            this.handle = handle;
            this.isDirectSql = isDirectSql;
            this.setIsEvictedFromCache(isEvictedFromCache);
            this.handleRefCount.set(1);
        }
        
        private boolean isEvictedFromCache() {
            return this.evictedFromCache;
        }
        
        private void setIsEvictedFromCache(final boolean isEvictedFromCache) {
            this.evictedFromCache = isEvictedFromCache;
        }
        
        void setIsExplicitlyDiscarded() {
            this.explicitlyDiscarded = true;
            SQLServerConnection.this.evictCachedPreparedStatementHandle(this);
        }
        
        private boolean isExplicitlyDiscarded() {
            return this.explicitlyDiscarded;
        }
        
        int getHandle() {
            return this.handle;
        }
        
        CityHash128Key getKey() {
            return this.key;
        }
        
        boolean isDirectSql() {
            return this.isDirectSql;
        }
        
        private boolean tryDiscardHandle() {
            return this.handleRefCount.compareAndSet(0, -999);
        }
        
        private boolean isDiscarded() {
            return 0 > this.handleRefCount.intValue();
        }
        
        boolean tryAddReference() {
            return !this.isDiscarded() && !this.isExplicitlyDiscarded() && this.handleRefCount.incrementAndGet() > 0;
        }
        
        void removeReference() {
            this.handleRefCount.decrementAndGet();
        }
    }
    
    class FederatedAuthenticationFeatureExtensionData implements Serializable
    {
        private static final long serialVersionUID = -6709861741957202475L;
        boolean fedAuthRequiredPreLoginResponse;
        int libraryType;
        byte[] accessToken;
        SqlAuthentication authentication;
        
        FederatedAuthenticationFeatureExtensionData(final int libraryType, final String authenticationString, final boolean fedAuthRequiredPreLoginResponse) throws SQLServerException {
            this.libraryType = -1;
            this.accessToken = null;
            this.authentication = null;
            this.libraryType = libraryType;
            this.fedAuthRequiredPreLoginResponse = fedAuthRequiredPreLoginResponse;
            final String upperCase = authenticationString.toUpperCase(Locale.ENGLISH);
            switch (upperCase) {
                case "ACTIVEDIRECTORYPASSWORD": {
                    this.authentication = SqlAuthentication.ActiveDirectoryPassword;
                    break;
                }
                case "ACTIVEDIRECTORYINTEGRATED": {
                    this.authentication = SqlAuthentication.ActiveDirectoryIntegrated;
                    break;
                }
                case "ACTIVEDIRECTORYMSI": {
                    this.authentication = SqlAuthentication.ActiveDirectoryMSI;
                    break;
                }
                default: {
                    assert false;
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
                    final Object[] msgArgs = { "authentication", authenticationString };
                    throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                }
            }
        }
        
        FederatedAuthenticationFeatureExtensionData(final int libraryType, final boolean fedAuthRequiredPreLoginResponse, final byte[] accessToken) {
            this.libraryType = -1;
            this.accessToken = null;
            this.authentication = null;
            this.libraryType = libraryType;
            this.fedAuthRequiredPreLoginResponse = fedAuthRequiredPreLoginResponse;
            this.accessToken = accessToken;
        }
    }
    
    class SqlFedAuthInfo
    {
        String spn;
        String stsurl;
        
        @Override
        public String toString() {
            return "STSURL: " + this.stsurl + ", SPN: " + this.spn;
        }
    }
    
    class ActiveDirectoryAuthentication
    {
        static final String JDBC_FEDAUTH_CLIENT_ID = "7f98cb04-cd1e-40df-9140-3bf7e2cea4db";
        static final String AZURE_REST_MSI_URL = "http://169.254.169.254/metadata/identity/oauth2/token?api-version=2018-02-01";
        static final String ADAL_GET_ACCESS_TOKEN_FUNCTION_NAME = "ADALGetAccessToken";
        static final String ACCESS_TOKEN_IDENTIFIER = "\"access_token\":\"";
        static final String ACCESS_TOKEN_EXPIRES_IN_IDENTIFIER = "\"expires_in\":\"";
        static final String ACCESS_TOKEN_EXPIRES_ON_IDENTIFIER = "\"expires_on\":\"";
        static final String ACCESS_TOKEN_EXPIRES_ON_DATE_FORMAT = "M/d/yyyy h:mm:ss a X";
        static final int GET_ACCESS_TOKEN_SUCCESS = 0;
        static final int GET_ACCESS_TOKEN_INVALID_GRANT = 1;
        static final int GET_ACCESS_TOKEN_TANSISENT_ERROR = 2;
        static final int GET_ACCESS_TOKEN_OTHER_ERROR = 3;
    }
    
    private enum State
    {
        Initialized, 
        Connected, 
        Opened, 
        Closed;
    }
    
    private final class LogonCommand extends UninterruptableTDSCommand
    {
        private static final long serialVersionUID = 1L;
        
        LogonCommand() {
            super("logon");
        }
        
        @Override
        final boolean doExecute() throws SQLServerException {
            SQLServerConnection.this.logon(this);
            return true;
        }
    }
    
    final class FedAuthTokenCommand extends UninterruptableTDSCommand
    {
        private static final long serialVersionUID = 1L;
        TDSTokenHandler tdsTokenHandler;
        SqlFedAuthToken sqlFedAuthToken;
        
        FedAuthTokenCommand(final SqlFedAuthToken sqlFedAuthToken, final TDSTokenHandler tdsTokenHandler) {
            super("FedAuth");
            this.tdsTokenHandler = null;
            this.sqlFedAuthToken = null;
            this.tdsTokenHandler = tdsTokenHandler;
            this.sqlFedAuthToken = sqlFedAuthToken;
        }
        
        @Override
        final boolean doExecute() throws SQLServerException {
            SQLServerConnection.this.sendFedAuthToken(this, this.sqlFedAuthToken, this.tdsTokenHandler);
            return true;
        }
    }
    
    final class PreparedStatementCacheEvictionListener implements EvictionListener<CityHash128Key, PreparedStatementHandle>
    {
        @Override
        public void onEviction(final CityHash128Key key, final PreparedStatementHandle handle) {
            if (null != handle) {
                handle.setIsEvictedFromCache(true);
                if (handle.tryDiscardHandle()) {
                    SQLServerConnection.this.enqueueUnprepareStatementHandle(handle);
                }
            }
        }
    }
}
