package org.apache.coyote.http11;

import javax.servlet.http.HttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeProcessorExternal;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.coyote.Processor;
import org.apache.tomcat.util.modeler.Util;
import org.apache.tomcat.util.buf.StringUtils;
import java.util.Locale;
import java.util.Collection;
import java.util.HashSet;
import org.apache.coyote.Response;
import org.apache.coyote.Request;
import javax.management.ObjectInstance;
import javax.management.QueryExp;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;
import java.util.Iterator;
import org.apache.coyote.http2.Http2Protocol;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.coyote.http11.upgrade.UpgradeGroupInfo;
import java.util.Map;
import org.apache.coyote.UpgradeProtocol;
import java.util.List;
import java.util.Set;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.CompressionConfig;
import org.apache.tomcat.util.res.StringManager;
import org.apache.coyote.AbstractProtocol;

public abstract class AbstractHttp11Protocol<S> extends AbstractProtocol<S>
{
    protected static final StringManager sm;
    private final CompressionConfig compressionConfig;
    private ContinueResponseTiming continueResponseTiming;
    private boolean useKeepAliveResponseHeader;
    private String relaxedPathChars;
    private String relaxedQueryChars;
    private boolean allowHostHeaderMismatch;
    private boolean rejectIllegalHeader;
    private int maxSavePostSize;
    private int maxHttpHeaderSize;
    private int connectionUploadTimeout;
    private boolean disableUploadTimeout;
    private String restrictedUserAgents;
    private String server;
    private boolean serverRemoveAppProvidedValues;
    private int maxTrailerSize;
    private int maxExtensionSize;
    private int maxSwallowSize;
    private boolean secure;
    private Set<String> allowedTrailerHeaders;
    private final List<UpgradeProtocol> upgradeProtocols;
    private final Map<String, UpgradeProtocol> httpUpgradeProtocols;
    private final Map<String, UpgradeProtocol> negotiatedProtocols;
    private final Map<String, UpgradeGroupInfo> upgradeProtocolGroupInfos;
    private SSLHostConfig defaultSSLHostConfig;
    
    public AbstractHttp11Protocol(final AbstractEndpoint<S> endpoint) {
        super(endpoint);
        this.compressionConfig = new CompressionConfig();
        this.continueResponseTiming = ContinueResponseTiming.IMMEDIATELY;
        this.useKeepAliveResponseHeader = true;
        this.relaxedPathChars = null;
        this.relaxedQueryChars = null;
        this.allowHostHeaderMismatch = true;
        this.rejectIllegalHeader = false;
        this.maxSavePostSize = 4096;
        this.maxHttpHeaderSize = 8192;
        this.connectionUploadTimeout = 300000;
        this.disableUploadTimeout = true;
        this.restrictedUserAgents = null;
        this.serverRemoveAppProvidedValues = false;
        this.maxTrailerSize = 8192;
        this.maxExtensionSize = 8192;
        this.maxSwallowSize = 2097152;
        this.allowedTrailerHeaders = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        this.upgradeProtocols = new ArrayList<UpgradeProtocol>();
        this.httpUpgradeProtocols = new HashMap<String, UpgradeProtocol>();
        this.negotiatedProtocols = new HashMap<String, UpgradeProtocol>();
        this.upgradeProtocolGroupInfos = new ConcurrentHashMap<String, UpgradeGroupInfo>();
        this.defaultSSLHostConfig = null;
        this.setConnectionTimeout(60000);
        final ConnectionHandler<S> cHandler = new ConnectionHandler<S>(this);
        this.setHandler(cHandler);
        this.getEndpoint().setHandler(cHandler);
    }
    
    @Override
    public void init() throws Exception {
        for (final UpgradeProtocol upgradeProtocol : this.upgradeProtocols) {
            this.configureUpgradeProtocol(upgradeProtocol);
        }
        super.init();
        for (final UpgradeProtocol upgradeProtocol : this.upgradeProtocols) {
            if (upgradeProtocol instanceof Http2Protocol) {
                ((Http2Protocol)upgradeProtocol).setHttp11Protocol(this);
            }
        }
    }
    
    @Override
    public void destroy() throws Exception {
        final ObjectName rgOname = this.getGlobalRequestProcessorMBeanName();
        if (rgOname != null) {
            final Registry registry = Registry.getRegistry(null, null);
            final ObjectName query = new ObjectName(rgOname.getCanonicalName() + ",Upgrade=*");
            final Set<ObjectInstance> upgrades = registry.getMBeanServer().queryMBeans(query, null);
            for (final ObjectInstance upgrade : upgrades) {
                registry.unregisterComponent(upgrade.getObjectName());
            }
        }
        super.destroy();
    }
    
    @Override
    protected String getProtocolName() {
        return "Http";
    }
    
    @Override
    protected AbstractEndpoint<S> getEndpoint() {
        return super.getEndpoint();
    }
    
    public String getContinueResponseTiming() {
        return this.continueResponseTiming.toString();
    }
    
    public void setContinueResponseTiming(final String continueResponseTiming) {
        this.continueResponseTiming = ContinueResponseTiming.fromString(continueResponseTiming);
    }
    
    public ContinueResponseTiming getContinueResponseTimingInternal() {
        return this.continueResponseTiming;
    }
    
    public boolean getUseKeepAliveResponseHeader() {
        return this.useKeepAliveResponseHeader;
    }
    
    public void setUseKeepAliveResponseHeader(final boolean useKeepAliveResponseHeader) {
        this.useKeepAliveResponseHeader = useKeepAliveResponseHeader;
    }
    
    public String getRelaxedPathChars() {
        return this.relaxedPathChars;
    }
    
    public void setRelaxedPathChars(final String relaxedPathChars) {
        this.relaxedPathChars = relaxedPathChars;
    }
    
    public String getRelaxedQueryChars() {
        return this.relaxedQueryChars;
    }
    
    public void setRelaxedQueryChars(final String relaxedQueryChars) {
        this.relaxedQueryChars = relaxedQueryChars;
    }
    
    public boolean getAllowHostHeaderMismatch() {
        return this.allowHostHeaderMismatch;
    }
    
    public void setAllowHostHeaderMismatch(final boolean allowHostHeaderMismatch) {
        this.allowHostHeaderMismatch = allowHostHeaderMismatch;
    }
    
    public boolean getRejectIllegalHeader() {
        return this.rejectIllegalHeader;
    }
    
    public void setRejectIllegalHeader(final boolean rejectIllegalHeader) {
        this.rejectIllegalHeader = rejectIllegalHeader;
    }
    
    @Deprecated
    public boolean getRejectIllegalHeaderName() {
        return this.rejectIllegalHeader;
    }
    
    @Deprecated
    public void setRejectIllegalHeaderName(final boolean rejectIllegalHeaderName) {
        this.rejectIllegalHeader = rejectIllegalHeaderName;
    }
    
    public int getMaxSavePostSize() {
        return this.maxSavePostSize;
    }
    
    public void setMaxSavePostSize(final int maxSavePostSize) {
        this.maxSavePostSize = maxSavePostSize;
    }
    
    public int getMaxHttpHeaderSize() {
        return this.maxHttpHeaderSize;
    }
    
    public void setMaxHttpHeaderSize(final int valueI) {
        this.maxHttpHeaderSize = valueI;
    }
    
    public int getConnectionUploadTimeout() {
        return this.connectionUploadTimeout;
    }
    
    public void setConnectionUploadTimeout(final int timeout) {
        this.connectionUploadTimeout = timeout;
    }
    
    public boolean getDisableUploadTimeout() {
        return this.disableUploadTimeout;
    }
    
    public void setDisableUploadTimeout(final boolean isDisabled) {
        this.disableUploadTimeout = isDisabled;
    }
    
    public void setCompression(final String compression) {
        this.compressionConfig.setCompression(compression);
    }
    
    public String getCompression() {
        return this.compressionConfig.getCompression();
    }
    
    public String getNoCompressionUserAgents() {
        return this.compressionConfig.getNoCompressionUserAgents();
    }
    
    public void setNoCompressionUserAgents(final String noCompressionUserAgents) {
        this.compressionConfig.setNoCompressionUserAgents(noCompressionUserAgents);
    }
    
    @Deprecated
    public String getCompressableMimeType() {
        return this.getCompressibleMimeType();
    }
    
    @Deprecated
    public void setCompressableMimeType(final String valueS) {
        this.setCompressibleMimeType(valueS);
    }
    
    @Deprecated
    public String[] getCompressableMimeTypes() {
        return this.getCompressibleMimeTypes();
    }
    
    public String getCompressibleMimeType() {
        return this.compressionConfig.getCompressibleMimeType();
    }
    
    public void setCompressibleMimeType(final String valueS) {
        this.compressionConfig.setCompressibleMimeType(valueS);
    }
    
    public String[] getCompressibleMimeTypes() {
        return this.compressionConfig.getCompressibleMimeTypes();
    }
    
    public int getCompressionMinSize() {
        return this.compressionConfig.getCompressionMinSize();
    }
    
    public void setCompressionMinSize(final int compressionMinSize) {
        this.compressionConfig.setCompressionMinSize(compressionMinSize);
    }
    
    @Deprecated
    public boolean getNoCompressionStrongETag() {
        return this.compressionConfig.getNoCompressionStrongETag();
    }
    
    @Deprecated
    public void setNoCompressionStrongETag(final boolean noCompressionStrongETag) {
        this.compressionConfig.setNoCompressionStrongETag(noCompressionStrongETag);
    }
    
    public boolean useCompression(final Request request, final Response response) {
        return this.compressionConfig.useCompression(request, response);
    }
    
    public String getRestrictedUserAgents() {
        return this.restrictedUserAgents;
    }
    
    public void setRestrictedUserAgents(final String valueS) {
        this.restrictedUserAgents = valueS;
    }
    
    public String getServer() {
        return this.server;
    }
    
    public void setServer(final String server) {
        this.server = server;
    }
    
    public boolean getServerRemoveAppProvidedValues() {
        return this.serverRemoveAppProvidedValues;
    }
    
    public void setServerRemoveAppProvidedValues(final boolean serverRemoveAppProvidedValues) {
        this.serverRemoveAppProvidedValues = serverRemoveAppProvidedValues;
    }
    
    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }
    
    public void setMaxTrailerSize(final int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }
    
    public int getMaxExtensionSize() {
        return this.maxExtensionSize;
    }
    
    public void setMaxExtensionSize(final int maxExtensionSize) {
        this.maxExtensionSize = maxExtensionSize;
    }
    
    public int getMaxSwallowSize() {
        return this.maxSwallowSize;
    }
    
    public void setMaxSwallowSize(final int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }
    
    public boolean getSecure() {
        return this.secure;
    }
    
    public void setSecure(final boolean b) {
        this.secure = b;
    }
    
    public void setAllowedTrailerHeaders(final String commaSeparatedHeaders) {
        final Set<String> toRemove = new HashSet<String>(this.allowedTrailerHeaders);
        if (commaSeparatedHeaders != null) {
            final String[] arr$;
            final String[] headers = arr$ = commaSeparatedHeaders.split(",");
            for (final String header : arr$) {
                final String trimmedHeader = header.trim().toLowerCase(Locale.ENGLISH);
                if (toRemove.contains(trimmedHeader)) {
                    toRemove.remove(trimmedHeader);
                }
                else {
                    this.allowedTrailerHeaders.add(trimmedHeader);
                }
            }
            this.allowedTrailerHeaders.removeAll(toRemove);
        }
    }
    
    protected Set<String> getAllowedTrailerHeadersInternal() {
        return this.allowedTrailerHeaders;
    }
    
    public String getAllowedTrailerHeaders() {
        final List<String> copy = new ArrayList<String>(this.allowedTrailerHeaders);
        return StringUtils.join((Collection)copy);
    }
    
    public void addAllowedTrailerHeader(final String header) {
        if (header != null) {
            this.allowedTrailerHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }
    
    public void removeAllowedTrailerHeader(final String header) {
        if (header != null) {
            this.allowedTrailerHeaders.remove(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }
    
    @Override
    public void addUpgradeProtocol(final UpgradeProtocol upgradeProtocol) {
        this.upgradeProtocols.add(upgradeProtocol);
    }
    
    @Override
    public UpgradeProtocol[] findUpgradeProtocols() {
        return this.upgradeProtocols.toArray(new UpgradeProtocol[0]);
    }
    
    private void configureUpgradeProtocol(final UpgradeProtocol upgradeProtocol) {
        final String httpUpgradeName = upgradeProtocol.getHttpUpgradeName(this.getEndpoint().isSSLEnabled());
        boolean httpUpgradeConfigured = false;
        if (httpUpgradeName != null && httpUpgradeName.length() > 0) {
            this.httpUpgradeProtocols.put(httpUpgradeName, upgradeProtocol);
            httpUpgradeConfigured = true;
            this.getLog().info((Object)AbstractHttp11Protocol.sm.getString("abstractHttp11Protocol.httpUpgradeConfigured", new Object[] { this.getName(), httpUpgradeName }));
        }
        final String alpnName = upgradeProtocol.getAlpnName();
        if (alpnName != null && alpnName.length() > 0) {
            if (this.getEndpoint().isAlpnSupported()) {
                this.negotiatedProtocols.put(alpnName, upgradeProtocol);
                this.getEndpoint().addNegotiatedProtocol(alpnName);
                this.getLog().info((Object)AbstractHttp11Protocol.sm.getString("abstractHttp11Protocol.alpnConfigured", new Object[] { this.getName(), alpnName }));
            }
            else if (!httpUpgradeConfigured) {
                this.getLog().error((Object)AbstractHttp11Protocol.sm.getString("abstractHttp11Protocol.alpnWithNoAlpn", new Object[] { upgradeProtocol.getClass().getName(), alpnName, this.getName() }));
            }
        }
    }
    
    public UpgradeProtocol getNegotiatedProtocol(final String negotiatedName) {
        return this.negotiatedProtocols.get(negotiatedName);
    }
    
    public UpgradeProtocol getUpgradeProtocol(final String upgradedName) {
        return this.httpUpgradeProtocols.get(upgradedName);
    }
    
    public UpgradeGroupInfo getUpgradeGroupInfo(final String upgradeProtocol) {
        if (upgradeProtocol == null) {
            return null;
        }
        UpgradeGroupInfo result = this.upgradeProtocolGroupInfos.get(upgradeProtocol);
        if (result == null) {
            synchronized (this.upgradeProtocolGroupInfos) {
                result = this.upgradeProtocolGroupInfos.get(upgradeProtocol);
                if (result == null) {
                    result = new UpgradeGroupInfo();
                    this.upgradeProtocolGroupInfos.put(upgradeProtocol, result);
                    final ObjectName oname = this.getONameForUpgrade(upgradeProtocol);
                    if (oname != null) {
                        try {
                            Registry.getRegistry(null, null).registerComponent(result, oname, null);
                        }
                        catch (final Exception e) {
                            this.getLog().warn((Object)AbstractHttp11Protocol.sm.getString("abstractHttp11Protocol.upgradeJmxRegistrationFail"), (Throwable)e);
                            result = null;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public ObjectName getONameForUpgrade(final String upgradeProtocol) {
        ObjectName oname = null;
        final ObjectName parentRgOname = this.getGlobalRequestProcessorMBeanName();
        if (parentRgOname != null) {
            final StringBuilder name = new StringBuilder(parentRgOname.getCanonicalName());
            name.append(",Upgrade=");
            if (Util.objectNameValueNeedsQuote(upgradeProtocol)) {
                name.append(ObjectName.quote(upgradeProtocol));
            }
            else {
                name.append(upgradeProtocol);
            }
            try {
                oname = new ObjectName(name.toString());
            }
            catch (final Exception e) {
                this.getLog().warn((Object)AbstractHttp11Protocol.sm.getString("abstractHttp11Protocol.upgradeJmxNameFail"), (Throwable)e);
            }
        }
        return oname;
    }
    
    public boolean isSSLEnabled() {
        return this.getEndpoint().isSSLEnabled();
    }
    
    public void setSSLEnabled(final boolean SSLEnabled) {
        this.getEndpoint().setSSLEnabled(SSLEnabled);
    }
    
    public boolean getUseSendfile() {
        return this.getEndpoint().getUseSendfile();
    }
    
    public void setUseSendfile(final boolean useSendfile) {
        this.getEndpoint().setUseSendfile(useSendfile);
    }
    
    public int getMaxKeepAliveRequests() {
        return this.getEndpoint().getMaxKeepAliveRequests();
    }
    
    public void setMaxKeepAliveRequests(final int mkar) {
        this.getEndpoint().setMaxKeepAliveRequests(mkar);
    }
    
    public String getDefaultSSLHostConfigName() {
        return this.getEndpoint().getDefaultSSLHostConfigName();
    }
    
    public void setDefaultSSLHostConfigName(final String defaultSSLHostConfigName) {
        this.getEndpoint().setDefaultSSLHostConfigName(defaultSSLHostConfigName);
        if (this.defaultSSLHostConfig != null) {
            this.defaultSSLHostConfig.setHostName(defaultSSLHostConfigName);
        }
    }
    
    @Override
    public void addSslHostConfig(final SSLHostConfig sslHostConfig) {
        this.getEndpoint().addSslHostConfig(sslHostConfig);
    }
    
    @Override
    public SSLHostConfig[] findSslHostConfigs() {
        return this.getEndpoint().findSslHostConfigs();
    }
    
    public void reloadSslHostConfigs() {
        this.getEndpoint().reloadSslHostConfigs();
    }
    
    public void reloadSslHostConfig(final String hostName) {
        this.getEndpoint().reloadSslHostConfig(hostName);
    }
    
    private void registerDefaultSSLHostConfig() {
        if (this.defaultSSLHostConfig == null) {
            for (final SSLHostConfig sslHostConfig : this.findSslHostConfigs()) {
                if (this.getDefaultSSLHostConfigName().equals(sslHostConfig.getHostName())) {
                    this.defaultSSLHostConfig = sslHostConfig;
                    break;
                }
            }
            if (this.defaultSSLHostConfig == null) {
                (this.defaultSSLHostConfig = new SSLHostConfig()).setHostName(this.getDefaultSSLHostConfigName());
                this.getEndpoint().addSslHostConfig(this.defaultSSLHostConfig);
            }
        }
    }
    
    public String getSslEnabledProtocols() {
        this.registerDefaultSSLHostConfig();
        return StringUtils.join(this.defaultSSLHostConfig.getEnabledProtocols());
    }
    
    public void setSslEnabledProtocols(final String enabledProtocols) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setProtocols(enabledProtocols);
    }
    
    public String getSSLProtocol() {
        this.registerDefaultSSLHostConfig();
        return StringUtils.join(this.defaultSSLHostConfig.getEnabledProtocols());
    }
    
    public void setSSLProtocol(final String sslProtocol) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setProtocols(sslProtocol);
    }
    
    public String getKeystoreFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreFile();
    }
    
    public void setKeystoreFile(final String keystoreFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreFile(keystoreFile);
    }
    
    public String getSSLCertificateChainFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateChainFile();
    }
    
    public void setSSLCertificateChainFile(final String certificateChainFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateChainFile(certificateChainFile);
    }
    
    public String getSSLCertificateFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateFile();
    }
    
    public void setSSLCertificateFile(final String certificateFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateFile(certificateFile);
    }
    
    public String getSSLCertificateKeyFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyFile();
    }
    
    public void setSSLCertificateKeyFile(final String certificateKeyFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyFile(certificateKeyFile);
    }
    
    public String getAlgorithm() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getKeyManagerAlgorithm();
    }
    
    public void setAlgorithm(final String keyManagerAlgorithm) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setKeyManagerAlgorithm(keyManagerAlgorithm);
    }
    
    public String getClientAuth() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationAsString();
    }
    
    public void setClientAuth(final String certificateVerification) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerification(certificateVerification);
    }
    
    public String getSSLVerifyClient() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationAsString();
    }
    
    public void setSSLVerifyClient(final String certificateVerification) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerification(certificateVerification);
    }
    
    public int getTrustMaxCertLength() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationDepth();
    }
    
    public void setTrustMaxCertLength(final int certificateVerificationDepth) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerificationDepth(certificateVerificationDepth);
    }
    
    public int getSSLVerifyDepth() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationDepth();
    }
    
    public void setSSLVerifyDepth(final int certificateVerificationDepth) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerificationDepth(certificateVerificationDepth);
    }
    
    public String getUseServerCipherSuitesOrder() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getHonorCipherOrder();
    }
    
    public void setUseServerCipherSuitesOrder(final String honorCipherOrder) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setHonorCipherOrder(honorCipherOrder);
    }
    
    public String getSSLHonorCipherOrder() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getHonorCipherOrder();
    }
    
    public void setSSLHonorCipherOrder(final String honorCipherOrder) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setHonorCipherOrder(honorCipherOrder);
    }
    
    public String getCiphers() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCiphers();
    }
    
    public void setCiphers(final String ciphers) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCiphers(ciphers);
    }
    
    public String getSSLCipherSuite() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCiphers();
    }
    
    public void setSSLCipherSuite(final String ciphers) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCiphers(ciphers);
    }
    
    public String getKeystorePass() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystorePassword();
    }
    
    public void setKeystorePass(final String certificateKeystorePassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystorePassword(certificateKeystorePassword);
    }
    
    public String getKeyPass() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPassword();
    }
    
    public void setKeyPass(final String certificateKeyPassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPassword(certificateKeyPassword);
    }
    
    public String getSSLPassword() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPassword();
    }
    
    public void setSSLPassword(final String certificateKeyPassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPassword(certificateKeyPassword);
    }
    
    public String getCrlFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListFile();
    }
    
    public void setCrlFile(final String certificateRevocationListFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListFile(certificateRevocationListFile);
    }
    
    public String getSSLCARevocationFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListFile();
    }
    
    public void setSSLCARevocationFile(final String certificateRevocationListFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListFile(certificateRevocationListFile);
    }
    
    public String getSSLCARevocationPath() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListPath();
    }
    
    public void setSSLCARevocationPath(final String certificateRevocationListPath) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListPath(certificateRevocationListPath);
    }
    
    public String getKeystoreType() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreType();
    }
    
    public void setKeystoreType(final String certificateKeystoreType) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreType(certificateKeystoreType);
    }
    
    public String getKeystoreProvider() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreProvider();
    }
    
    public void setKeystoreProvider(final String certificateKeystoreProvider) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreProvider(certificateKeystoreProvider);
    }
    
    public String getKeyAlias() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyAlias();
    }
    
    public void setKeyAlias(final String certificateKeyAlias) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyAlias(certificateKeyAlias);
    }
    
    public String getTruststoreAlgorithm() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreAlgorithm();
    }
    
    public void setTruststoreAlgorithm(final String truststoreAlgorithm) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreAlgorithm(truststoreAlgorithm);
    }
    
    public String getTruststoreFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreFile();
    }
    
    public void setTruststoreFile(final String truststoreFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreFile(truststoreFile);
    }
    
    public String getTruststorePass() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststorePassword();
    }
    
    public void setTruststorePass(final String truststorePassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststorePassword(truststorePassword);
    }
    
    public String getTruststoreType() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreType();
    }
    
    public void setTruststoreType(final String truststoreType) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreType(truststoreType);
    }
    
    public String getTruststoreProvider() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreProvider();
    }
    
    public void setTruststoreProvider(final String truststoreProvider) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreProvider(truststoreProvider);
    }
    
    public String getSslProtocol() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSslProtocol();
    }
    
    public void setSslProtocol(final String sslProtocol) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSslProtocol(sslProtocol);
    }
    
    public int getSessionCacheSize() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSessionCacheSize();
    }
    
    public void setSessionCacheSize(final int sessionCacheSize) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSessionCacheSize(sessionCacheSize);
    }
    
    public int getSessionTimeout() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSessionTimeout();
    }
    
    public void setSessionTimeout(final int sessionTimeout) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSessionTimeout(sessionTimeout);
    }
    
    public String getSSLCACertificatePath() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCaCertificatePath();
    }
    
    public void setSSLCACertificatePath(final String caCertificatePath) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCaCertificatePath(caCertificatePath);
    }
    
    public String getSSLCACertificateFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCaCertificateFile();
    }
    
    public void setSSLCACertificateFile(final String caCertificateFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCaCertificateFile(caCertificateFile);
    }
    
    public boolean getSSLDisableCompression() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getDisableCompression();
    }
    
    public void setSSLDisableCompression(final boolean disableCompression) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setDisableCompression(disableCompression);
    }
    
    public boolean getSSLDisableSessionTickets() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getDisableSessionTickets();
    }
    
    public void setSSLDisableSessionTickets(final boolean disableSessionTickets) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setDisableSessionTickets(disableSessionTickets);
    }
    
    public String getTrustManagerClassName() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTrustManagerClassName();
    }
    
    public void setTrustManagerClassName(final String trustManagerClassName) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTrustManagerClassName(trustManagerClassName);
    }
    
    @Override
    protected Processor createProcessor() {
        final Http11Processor processor = new Http11Processor(this, this.getEndpoint());
        processor.setAdapter(this.getAdapter());
        processor.setMaxKeepAliveRequests(this.getMaxKeepAliveRequests());
        processor.setConnectionUploadTimeout(this.getConnectionUploadTimeout());
        processor.setDisableUploadTimeout(this.getDisableUploadTimeout());
        processor.setRestrictedUserAgents(this.getRestrictedUserAgents());
        processor.setMaxSavePostSize(this.getMaxSavePostSize());
        return processor;
    }
    
    @Override
    protected Processor createUpgradeProcessor(final SocketWrapperBase<?> socket, final UpgradeToken upgradeToken) {
        final HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
        if (httpUpgradeHandler instanceof InternalHttpUpgradeHandler) {
            return new UpgradeProcessorInternal(socket, upgradeToken, this.getUpgradeGroupInfo(upgradeToken.getProtocol()));
        }
        return new UpgradeProcessorExternal(socket, upgradeToken, this.getUpgradeGroupInfo(upgradeToken.getProtocol()));
    }
    
    static {
        sm = StringManager.getManager((Class)AbstractHttp11Protocol.class);
    }
}
