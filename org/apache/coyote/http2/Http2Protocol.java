package org.apache.coyote.http2;

import java.nio.charset.StandardCharsets;
import org.apache.juli.logging.LogFactory;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.Response;
import java.util.regex.Pattern;
import java.util.List;
import org.apache.tomcat.util.buf.StringUtils;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Collection;
import java.util.HashSet;
import java.util.Enumeration;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeGroupInfo;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.ContextBind;
import javax.servlet.http.HttpUpgradeHandler;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.Request;
import org.apache.coyote.Processor;
import org.apache.coyote.Adapter;
import org.apache.tomcat.util.net.SocketWrapperBase;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.CompressionConfig;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.coyote.UpgradeProtocol;

public class Http2Protocol implements UpgradeProtocol
{
    private static final Log log;
    private static final StringManager sm;
    static final long DEFAULT_READ_TIMEOUT = 5000L;
    static final long DEFAULT_WRITE_TIMEOUT = 5000L;
    static final long DEFAULT_KEEP_ALIVE_TIMEOUT = 20000L;
    static final long DEFAULT_STREAM_READ_TIMEOUT = 20000L;
    static final long DEFAULT_STREAM_WRITE_TIMEOUT = 20000L;
    static final long DEFAULT_MAX_CONCURRENT_STREAMS = 100L;
    static final int DEFAULT_MAX_CONCURRENT_STREAM_EXECUTION = 20;
    static final int DEFAULT_OVERHEAD_COUNT_FACTOR = 10;
    static final int DEFAULT_OVERHEAD_REDUCTION_FACTOR = -20;
    static final int DEFAULT_OVERHEAD_CONTINUATION_THRESHOLD = 1024;
    static final int DEFAULT_OVERHEAD_DATA_THRESHOLD = 1024;
    static final int DEFAULT_OVERHEAD_WINDOW_UPDATE_THRESHOLD = 1024;
    private static final String HTTP_UPGRADE_NAME = "h2c";
    private static final String ALPN_NAME = "h2";
    private static final byte[] ALPN_IDENTIFIER;
    private long readTimeout;
    private long writeTimeout;
    private long keepAliveTimeout;
    private long streamReadTimeout;
    private long streamWriteTimeout;
    private long maxConcurrentStreams;
    private int maxConcurrentStreamExecution;
    private int initialWindowSize;
    private Set<String> allowedTrailerHeaders;
    private int maxHeaderCount;
    private int maxHeaderSize;
    private int maxTrailerCount;
    private int maxTrailerSize;
    private int overheadCountFactor;
    private int overheadContinuationThreshold;
    private int overheadDataThreshold;
    private int overheadWindowUpdateThreshold;
    private boolean initiatePingDisabled;
    private final CompressionConfig compressionConfig;
    private AbstractHttp11Protocol<?> http11Protocol;
    private RequestGroupInfo global;
    
    public Http2Protocol() {
        this.readTimeout = 5000L;
        this.writeTimeout = 5000L;
        this.keepAliveTimeout = 20000L;
        this.streamReadTimeout = 20000L;
        this.streamWriteTimeout = 20000L;
        this.maxConcurrentStreams = 100L;
        this.maxConcurrentStreamExecution = 20;
        this.initialWindowSize = 65535;
        this.allowedTrailerHeaders = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        this.maxHeaderCount = 100;
        this.maxHeaderSize = 8192;
        this.maxTrailerCount = 100;
        this.maxTrailerSize = 8192;
        this.overheadCountFactor = 10;
        this.overheadContinuationThreshold = 1024;
        this.overheadDataThreshold = 1024;
        this.overheadWindowUpdateThreshold = 1024;
        this.initiatePingDisabled = false;
        this.compressionConfig = new CompressionConfig();
        this.http11Protocol = null;
        this.global = new RequestGroupInfo();
    }
    
    @Override
    public String getHttpUpgradeName(final boolean isSSLEnabled) {
        if (isSSLEnabled) {
            return null;
        }
        return "h2c";
    }
    
    @Override
    public byte[] getAlpnIdentifier() {
        return Http2Protocol.ALPN_IDENTIFIER;
    }
    
    @Override
    public String getAlpnName() {
        return "h2";
    }
    
    @Override
    public Processor getProcessor(final SocketWrapperBase<?> socketWrapper, final Adapter adapter) {
        final String upgradeProtocol = this.getUpgradeProtocolName();
        final UpgradeProcessorInternal processor = new UpgradeProcessorInternal(socketWrapper, new UpgradeToken((HttpUpgradeHandler)this.getInternalUpgradeHandler(adapter, null), null, null, upgradeProtocol), null);
        return processor;
    }
    
    @Override
    public InternalHttpUpgradeHandler getInternalUpgradeHandler(final Adapter adapter, final Request coyoteRequest) {
        final Http2UpgradeHandler result = new Http2UpgradeHandler(this, adapter, coyoteRequest);
        result.setReadTimeout(this.getReadTimeout());
        result.setKeepAliveTimeout(this.getKeepAliveTimeout());
        result.setWriteTimeout(this.getWriteTimeout());
        result.setMaxConcurrentStreams(this.getMaxConcurrentStreams());
        result.setMaxConcurrentStreamExecution(this.getMaxConcurrentStreamExecution());
        result.setInitialWindowSize(this.getInitialWindowSize());
        result.setAllowedTrailerHeaders(this.allowedTrailerHeaders);
        result.setMaxHeaderCount(this.getMaxHeaderCount());
        result.setMaxHeaderSize(this.getMaxHeaderSize());
        result.setMaxTrailerCount(this.getMaxTrailerCount());
        result.setMaxTrailerSize(this.getMaxTrailerSize());
        result.setInitiatePingDisabled(this.initiatePingDisabled);
        return result;
    }
    
    @Override
    public boolean accept(final Request request) {
        final Enumeration<String> settings = request.getMimeHeaders().values("HTTP2-Settings");
        int count = 0;
        while (settings.hasMoreElements()) {
            ++count;
            settings.nextElement();
        }
        if (count != 1) {
            return false;
        }
        Enumeration<String> connection;
        boolean found;
        for (connection = request.getMimeHeaders().values("Connection"), found = false; connection.hasMoreElements() && !found; found = connection.nextElement().contains("HTTP2-Settings")) {}
        return found;
    }
    
    public long getReadTimeout() {
        return this.readTimeout;
    }
    
    public void setReadTimeout(final long readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public long getWriteTimeout() {
        return this.writeTimeout;
    }
    
    public void setWriteTimeout(final long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
    
    public long getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }
    
    public void setKeepAliveTimeout(final long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }
    
    public long getStreamReadTimeout() {
        return this.streamReadTimeout;
    }
    
    public void setStreamReadTimeout(final long streamReadTimeout) {
        this.streamReadTimeout = streamReadTimeout;
    }
    
    public long getStreamWriteTimeout() {
        return this.streamWriteTimeout;
    }
    
    public void setStreamWriteTimeout(final long streamWriteTimeout) {
        this.streamWriteTimeout = streamWriteTimeout;
    }
    
    public long getMaxConcurrentStreams() {
        return this.maxConcurrentStreams;
    }
    
    public void setMaxConcurrentStreams(final long maxConcurrentStreams) {
        this.maxConcurrentStreams = maxConcurrentStreams;
    }
    
    public int getMaxConcurrentStreamExecution() {
        return this.maxConcurrentStreamExecution;
    }
    
    public void setMaxConcurrentStreamExecution(final int maxConcurrentStreamExecution) {
        this.maxConcurrentStreamExecution = maxConcurrentStreamExecution;
    }
    
    public int getInitialWindowSize() {
        return this.initialWindowSize;
    }
    
    public void setInitialWindowSize(final int initialWindowSize) {
        this.initialWindowSize = initialWindowSize;
    }
    
    public void setAllowedTrailerHeaders(final String commaSeparatedHeaders) {
        final Set<String> toRemove = new HashSet<String>();
        toRemove.addAll(this.allowedTrailerHeaders);
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
    
    public String getAllowedTrailerHeaders() {
        final List<String> copy = new ArrayList<String>(this.allowedTrailerHeaders.size());
        copy.addAll(this.allowedTrailerHeaders);
        return StringUtils.join((Collection)copy);
    }
    
    public void setMaxHeaderCount(final int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }
    
    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }
    
    public void setMaxHeaderSize(final int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }
    
    public int getMaxHeaderSize() {
        return this.maxHeaderSize;
    }
    
    public void setMaxTrailerCount(final int maxTrailerCount) {
        this.maxTrailerCount = maxTrailerCount;
    }
    
    public int getMaxTrailerCount() {
        return this.maxTrailerCount;
    }
    
    public void setMaxTrailerSize(final int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }
    
    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }
    
    public int getOverheadCountFactor() {
        return this.overheadCountFactor;
    }
    
    public void setOverheadCountFactor(final int overheadCountFactor) {
        this.overheadCountFactor = overheadCountFactor;
    }
    
    public int getOverheadContinuationThreshold() {
        return this.overheadContinuationThreshold;
    }
    
    public void setOverheadContinuationThreshold(final int overheadContinuationThreshold) {
        this.overheadContinuationThreshold = overheadContinuationThreshold;
    }
    
    public int getOverheadDataThreshold() {
        return this.overheadDataThreshold;
    }
    
    public void setOverheadDataThreshold(final int overheadDataThreshold) {
        this.overheadDataThreshold = overheadDataThreshold;
    }
    
    public int getOverheadWindowUpdateThreshold() {
        return this.overheadWindowUpdateThreshold;
    }
    
    public void setOverheadWindowUpdateThreshold(final int overheadWindowUpdateThreshold) {
        this.overheadWindowUpdateThreshold = overheadWindowUpdateThreshold;
    }
    
    public void setInitiatePingDisabled(final boolean initiatePingDisabled) {
        this.initiatePingDisabled = initiatePingDisabled;
    }
    
    public void setCompression(final String compression) {
        this.compressionConfig.setCompression(compression);
    }
    
    public String getCompression() {
        return this.compressionConfig.getCompression();
    }
    
    protected int getCompressionLevel() {
        return this.compressionConfig.getCompressionLevel();
    }
    
    public String getNoCompressionUserAgents() {
        return this.compressionConfig.getNoCompressionUserAgents();
    }
    
    protected Pattern getNoCompressionUserAgentsPattern() {
        return this.compressionConfig.getNoCompressionUserAgentsPattern();
    }
    
    public void setNoCompressionUserAgents(final String noCompressionUserAgents) {
        this.compressionConfig.setNoCompressionUserAgents(noCompressionUserAgents);
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
    
    public ContinueResponseTiming getContinueResponseTimingInternal() {
        return this.http11Protocol.getContinueResponseTimingInternal();
    }
    
    public AbstractHttp11Protocol<?> getHttp11Protocol() {
        return this.http11Protocol;
    }
    
    public void setHttp11Protocol(final AbstractHttp11Protocol<?> http11Protocol) {
        this.http11Protocol = http11Protocol;
        try {
            final ObjectName oname = this.http11Protocol.getONameForUpgrade(this.getUpgradeProtocolName());
            if (oname != null) {
                Registry.getRegistry(null, null).registerComponent(this.global, oname, null);
            }
        }
        catch (final Exception e) {
            Http2Protocol.log.warn((Object)Http2Protocol.sm.getString("http2Protocol.jmxRegistration.fail"), (Throwable)e);
        }
    }
    
    public String getUpgradeProtocolName() {
        if (this.http11Protocol.isSSLEnabled()) {
            return "h2";
        }
        return "h2c";
    }
    
    public RequestGroupInfo getGlobal() {
        return this.global;
    }
    
    static {
        log = LogFactory.getLog((Class)Http2Protocol.class);
        sm = StringManager.getManager((Class)Http2Protocol.class);
        ALPN_IDENTIFIER = "h2".getBytes(StandardCharsets.UTF_8);
    }
}
