package sun.security.ssl;

import java.util.List;
import sun.security.provider.certpath.ResponderId;
import sun.security.provider.certpath.OCSPResponse;
import java.util.Date;
import java.security.cert.Extension;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.Collection;
import java.io.IOException;
import sun.security.x509.SerialNumber;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import sun.security.provider.certpath.OCSP;
import sun.security.x509.PKIXExtensions;
import java.util.Objects;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import sun.security.action.GetBooleanAction;
import java.net.URISyntaxException;
import sun.security.action.GetPropertyAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.net.URI;
import sun.security.provider.certpath.CertId;
import sun.security.util.Cache;
import java.util.concurrent.ScheduledThreadPoolExecutor;

final class StatusResponseManager
{
    private static final int DEFAULT_CORE_THREADS = 8;
    private static final int DEFAULT_CACHE_SIZE = 256;
    private static final int DEFAULT_CACHE_LIFETIME = 3600;
    private final ScheduledThreadPoolExecutor threadMgr;
    private final Cache<CertId, ResponseCacheEntry> responseCache;
    private final URI defaultResponder;
    private final boolean respOverride;
    private final int cacheCapacity;
    private final int cacheLifetime;
    private final boolean ignoreExtensions;
    
    StatusResponseManager() {
        final int intValue = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.stapling.cacheSize", 256));
        this.cacheCapacity = ((intValue > 0) ? intValue : false);
        final int intValue2 = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.stapling.cacheLifetime", 3600));
        this.cacheLifetime = ((intValue2 > 0) ? intValue2 : false);
        final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty("jdk.tls.stapling.responderURI");
        URI defaultResponder;
        try {
            defaultResponder = ((privilegedGetProperty != null && !privilegedGetProperty.isEmpty()) ? new URI(privilegedGetProperty) : null);
        }
        catch (final URISyntaxException ex) {
            defaultResponder = null;
        }
        this.defaultResponder = defaultResponder;
        this.respOverride = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("jdk.tls.stapling.responderOverride"));
        this.ignoreExtensions = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("jdk.tls.stapling.ignoreExtensions"));
        (this.threadMgr = new ScheduledThreadPoolExecutor(8, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        }, new ThreadPoolExecutor.DiscardPolicy())).setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.threadMgr.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        this.threadMgr.setKeepAliveTime(5000L, TimeUnit.MILLISECONDS);
        this.threadMgr.allowCoreThreadTimeOut(true);
        this.responseCache = Cache.newSoftMemoryCache(this.cacheCapacity, this.cacheLifetime);
    }
    
    int getCacheLifetime() {
        return this.cacheLifetime;
    }
    
    int getCacheCapacity() {
        return this.cacheCapacity;
    }
    
    URI getDefaultResponder() {
        return this.defaultResponder;
    }
    
    boolean getURIOverride() {
        return this.respOverride;
    }
    
    boolean getIgnoreExtensions() {
        return this.ignoreExtensions;
    }
    
    void clear() {
        if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Clearing response cache", new Object[0]);
        }
        this.responseCache.clear();
    }
    
    int size() {
        return this.responseCache.size();
    }
    
    URI getURI(final X509Certificate x509Certificate) {
        Objects.requireNonNull(x509Certificate);
        if (x509Certificate.getExtensionValue(PKIXExtensions.OCSPNoCheck_Id.toString()) != null) {
            if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                SSLLogger.fine("OCSP NoCheck extension found.  OCSP will be skipped", new Object[0]);
            }
            return null;
        }
        if (this.defaultResponder != null && this.respOverride) {
            if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                SSLLogger.fine("Responder override: URI is " + this.defaultResponder, new Object[0]);
            }
            return this.defaultResponder;
        }
        final URI responderURI = OCSP.getResponderURI(x509Certificate);
        return (responderURI != null) ? responderURI : this.defaultResponder;
    }
    
    void shutdown() {
        if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Shutting down " + this.threadMgr.getActiveCount() + " active threads", new Object[0]);
        }
        this.threadMgr.shutdown();
    }
    
    Map<X509Certificate, byte[]> get(final CertStatusExtension.CertStatusRequestType certStatusRequestType, final CertStatusExtension.CertStatusRequest certStatusRequest, final X509Certificate[] array, final long n, final TimeUnit timeUnit) {
        final HashMap hashMap = new HashMap();
        final ArrayList list = new ArrayList();
        if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Beginning check: Type = " + certStatusRequestType + ", Chain length = " + array.length, new Object[0]);
        }
        if (array.length < 2) {
            return Collections.emptyMap();
        }
        if (certStatusRequestType == CertStatusExtension.CertStatusRequestType.OCSP) {
            try {
                final CertStatusExtension.OCSPStatusRequest ocspStatusRequest = (CertStatusExtension.OCSPStatusRequest)certStatusRequest;
                final CertId certId = new CertId(array[1], new SerialNumber(array[0].getSerialNumber()));
                final ResponseCacheEntry fromCache = this.getFromCache(certId, ocspStatusRequest);
                if (fromCache != null) {
                    hashMap.put(array[0], fromCache.ocspBytes);
                }
                else {
                    list.add(new OCSPFetchCall(new StatusInfo(array[0], certId), ocspStatusRequest));
                }
            }
            catch (final IOException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Exception during CertId creation: ", ex);
                }
            }
        }
        else if (certStatusRequestType == CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
            final CertStatusExtension.OCSPStatusRequest ocspStatusRequest2 = (CertStatusExtension.OCSPStatusRequest)certStatusRequest;
            for (int i = 0; i < array.length - 1; ++i) {
                try {
                    final CertId certId2 = new CertId(array[i + 1], new SerialNumber(array[i].getSerialNumber()));
                    final ResponseCacheEntry fromCache2 = this.getFromCache(certId2, ocspStatusRequest2);
                    if (fromCache2 != null) {
                        hashMap.put(array[i], fromCache2.ocspBytes);
                    }
                    else {
                        list.add(new OCSPFetchCall(new StatusInfo(array[i], certId2), ocspStatusRequest2));
                    }
                }
                catch (final IOException ex2) {
                    if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                        SSLLogger.fine("Exception during CertId creation: ", ex2);
                    }
                }
            }
        }
        else if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Unsupported status request type: " + certStatusRequestType, new Object[0]);
        }
        if (!list.isEmpty()) {
            try {
                for (final Future future : this.threadMgr.invokeAll((Collection<? extends Callable<Object>>)list, n, timeUnit)) {
                    if (!future.isDone()) {
                        continue;
                    }
                    if (!future.isCancelled()) {
                        final StatusInfo statusInfo = (StatusInfo)future.get();
                        if (statusInfo != null && statusInfo.responseData != null) {
                            hashMap.put(statusInfo.cert, statusInfo.responseData.ocspBytes);
                        }
                        else {
                            if (!SSLLogger.isOn || !SSLLogger.isOn("respmgr")) {
                                continue;
                            }
                            SSLLogger.fine("Completed task had no response data", new Object[0]);
                        }
                    }
                    else {
                        if (!SSLLogger.isOn || !SSLLogger.isOn("respmgr")) {
                            continue;
                        }
                        SSLLogger.fine("Found cancelled task", new Object[0]);
                    }
                }
            }
            catch (final InterruptedException | ExecutionException ex3) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Exception when getting data: ", ex3);
                }
            }
        }
        return (Map<X509Certificate, byte[]>)Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
    
    private ResponseCacheEntry getFromCache(final CertId certId, final CertStatusExtension.OCSPStatusRequest ocspStatusRequest) {
        final Iterator<Extension> iterator = ocspStatusRequest.extensions.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Nonce extension found, skipping cache check", new Object[0]);
                }
                return null;
            }
        }
        ResponseCacheEntry responseCacheEntry = this.responseCache.get(certId);
        if (responseCacheEntry != null && responseCacheEntry.nextUpdate != null && responseCacheEntry.nextUpdate.before(new Date())) {
            if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                SSLLogger.fine("nextUpdate threshold exceeded, purging from cache", new Object[0]);
            }
            responseCacheEntry = null;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Check cache for SN" + certId.getSerialNumber() + ": " + ((responseCacheEntry != null) ? "HIT" : "MISS"), new Object[0]);
        }
        return responseCacheEntry;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatusResponseManager: ");
        sb.append("Core threads: ").append(this.threadMgr.getCorePoolSize());
        sb.append(", Cache timeout: ");
        if (this.cacheLifetime > 0) {
            sb.append(this.cacheLifetime).append(" seconds");
        }
        else {
            sb.append(" indefinite");
        }
        sb.append(", Cache MaxSize: ");
        if (this.cacheCapacity > 0) {
            sb.append(this.cacheCapacity).append(" items");
        }
        else {
            sb.append(" unbounded");
        }
        sb.append(", Default URI: ");
        if (this.defaultResponder != null) {
            sb.append(this.defaultResponder);
        }
        else {
            sb.append("NONE");
        }
        return sb.toString();
    }
    
    static final StaplingParameters processStapling(final ServerHandshakeContext serverHandshakeContext) {
        StaplingParameters staplingParameters = null;
        SSLExtension sslExtension = null;
        CertStatusExtension.CertStatusRequestType certStatusRequestType = null;
        CertStatusExtension.CertStatusRequest certStatusRequest = null;
        if (!serverHandshakeContext.sslContext.isStaplingEnabled(false) || serverHandshakeContext.isResumption) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Staping disabled or is a resumed session", new Object[0]);
            }
            return null;
        }
        final Map<SSLExtension, SSLExtension.SSLExtensionSpec> handshakeExtensions = serverHandshakeContext.handshakeExtensions;
        final CertStatusExtension.CertStatusRequestSpec certStatusRequestSpec = handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST);
        final CertStatusExtension.CertStatusRequestV2Spec certStatusRequestV2Spec = handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST_V2);
        if (certStatusRequestV2Spec != null && !serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.fine("SH Processing status_request_v2 extension", new Object[0]);
            }
            sslExtension = SSLExtension.CH_STATUS_REQUEST_V2;
            int n = -1;
            int n2 = -1;
            final CertStatusExtension.CertStatusRequest[] certStatusRequests = certStatusRequestV2Spec.certStatusRequests;
            for (int n3 = 0; n3 < certStatusRequests.length && (n == -1 || n2 == -1); ++n3) {
                final CertStatusExtension.CertStatusRequest certStatusRequest2 = certStatusRequests[n3];
                final CertStatusExtension.CertStatusRequestType value = CertStatusExtension.CertStatusRequestType.valueOf(certStatusRequest2.statusType);
                if (n < 0 && value == CertStatusExtension.CertStatusRequestType.OCSP) {
                    if (((CertStatusExtension.OCSPStatusRequest)certStatusRequest2).responderIds.isEmpty()) {
                        n = n3;
                    }
                }
                else if (n2 < 0 && value == CertStatusExtension.CertStatusRequestType.OCSP_MULTI && ((CertStatusExtension.OCSPStatusRequest)certStatusRequest2).responderIds.isEmpty()) {
                    n2 = n3;
                }
            }
            if (n2 >= 0) {
                certStatusRequest = certStatusRequests[n2];
                certStatusRequestType = CertStatusExtension.CertStatusRequestType.valueOf(certStatusRequest.statusType);
            }
            else if (n >= 0) {
                certStatusRequest = certStatusRequests[n];
                certStatusRequestType = CertStatusExtension.CertStatusRequestType.valueOf(certStatusRequest.statusType);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: No suitable request found in the status_request_v2 extension.", new Object[0]);
            }
        }
        if (certStatusRequestSpec != null && (sslExtension == null || certStatusRequestType == null || certStatusRequest == null)) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.fine("SH Processing status_request extension", new Object[0]);
            }
            sslExtension = SSLExtension.CH_STATUS_REQUEST;
            certStatusRequestType = CertStatusExtension.CertStatusRequestType.valueOf(certStatusRequestSpec.statusRequest.statusType);
            if (certStatusRequestType == CertStatusExtension.CertStatusRequestType.OCSP) {
                final CertStatusExtension.OCSPStatusRequest ocspStatusRequest = (CertStatusExtension.OCSPStatusRequest)certStatusRequestSpec.statusRequest;
                if (ocspStatusRequest.responderIds.isEmpty()) {
                    certStatusRequest = ocspStatusRequest;
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Warning: No suitable request found in the status_request extension.", new Object[0]);
                }
            }
        }
        if (certStatusRequestType == null || certStatusRequest == null || sslExtension == null) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("No suitable status_request or status_request_v2, stapling is disabled", new Object[0]);
            }
            return null;
        }
        X509Authentication.X509Possession x509Possession = null;
        for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
            if (sslPossession instanceof X509Authentication.X509Possession) {
                x509Possession = (X509Authentication.X509Possession)sslPossession;
                break;
            }
        }
        if (x509Possession == null) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: no X.509 certificates found.  Stapling is disabled.", new Object[0]);
            }
            return null;
        }
        final X509Certificate[] popCerts = x509Possession.popCerts;
        final StatusResponseManager statusResponseManager = serverHandshakeContext.sslContext.getStatusResponseManager();
        if (statusResponseManager != null) {
            final Map<X509Certificate, byte[]> value2 = statusResponseManager.get(serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec() ? CertStatusExtension.CertStatusRequestType.OCSP_MULTI : certStatusRequestType, certStatusRequest, popCerts, serverHandshakeContext.statusRespTimeout, TimeUnit.MILLISECONDS);
            if (!value2.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Response manager returned " + value2.size() + " entries.", new Object[0]);
                }
                if (certStatusRequestType == CertStatusExtension.CertStatusRequestType.OCSP) {
                    final byte[] array = value2.get(popCerts[0]);
                    if (array == null || array.length <= 0) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.finest("Warning: Null or zero-length response found for leaf certificate. Stapling is disabled.", new Object[0]);
                        }
                        return null;
                    }
                }
                staplingParameters = new StaplingParameters(sslExtension, certStatusRequestType, certStatusRequest, value2);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: no OCSP responses obtained.  Stapling is disabled.", new Object[0]);
            }
        }
        else {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: lazy initialization of the StatusResponseManager failed.  Stapling is disabled.", new Object[0]);
            }
            staplingParameters = null;
        }
        return staplingParameters;
    }
    
    class StatusInfo
    {
        final X509Certificate cert;
        final CertId cid;
        final URI responder;
        ResponseCacheEntry responseData;
        
        StatusInfo(final StatusResponseManager statusResponseManager, final X509Certificate x509Certificate, final X509Certificate x509Certificate2) throws IOException {
            this(statusResponseManager, x509Certificate, new CertId(x509Certificate2, new SerialNumber(x509Certificate.getSerialNumber())));
        }
        
        StatusInfo(final X509Certificate cert, final CertId cid) {
            this.cert = cert;
            this.cid = cid;
            this.responder = StatusResponseManager.this.getURI(this.cert);
            this.responseData = null;
        }
        
        StatusInfo(final StatusInfo statusInfo) {
            this.cert = statusInfo.cert;
            this.cid = statusInfo.cid;
            this.responder = statusInfo.responder;
            this.responseData = null;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StatusInfo:");
            sb.append("\n\tCert: ").append(this.cert.getSubjectX500Principal());
            sb.append("\n\tSerial: ").append(this.cert.getSerialNumber());
            sb.append("\n\tResponder: ").append(this.responder);
            sb.append("\n\tResponse data: ").append((this.responseData != null) ? (this.responseData.ocspBytes.length + " bytes") : "<NULL>");
            return sb.toString();
        }
    }
    
    class ResponseCacheEntry
    {
        final OCSPResponse.ResponseStatus status;
        final byte[] ocspBytes;
        final Date nextUpdate;
        final OCSPResponse.SingleResponse singleResp;
        final ResponderId respId;
        
        ResponseCacheEntry(final byte[] array, final CertId certId) throws IOException {
            Objects.requireNonNull(array, "Non-null responseBytes required");
            Objects.requireNonNull(certId, "Non-null Cert ID required");
            this.ocspBytes = array.clone();
            final OCSPResponse ocspResponse = new OCSPResponse(this.ocspBytes);
            this.status = ocspResponse.getResponseStatus();
            this.respId = ocspResponse.getResponderId();
            this.singleResp = ocspResponse.getSingleResponse(certId);
            if (this.status == OCSPResponse.ResponseStatus.SUCCESSFUL) {
                if (this.singleResp == null) {
                    throw new IOException("Unable to find SingleResponse for SN " + certId.getSerialNumber());
                }
                this.nextUpdate = this.singleResp.getNextUpdate();
            }
            else {
                this.nextUpdate = null;
            }
        }
    }
    
    class OCSPFetchCall implements Callable<StatusInfo>
    {
        StatusInfo statInfo;
        CertStatusExtension.OCSPStatusRequest ocspRequest;
        List<Extension> extensions;
        List<ResponderId> responderIds;
        
        public OCSPFetchCall(final StatusInfo statusInfo, final CertStatusExtension.OCSPStatusRequest ocspStatusRequest) {
            this.statInfo = Objects.requireNonNull(statusInfo, "Null StatusInfo not allowed");
            this.ocspRequest = Objects.requireNonNull(ocspStatusRequest, "Null OCSPStatusRequest not allowed");
            this.extensions = this.ocspRequest.extensions;
            this.responderIds = this.ocspRequest.responderIds;
        }
        
        @Override
        public StatusInfo call() {
            if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                SSLLogger.fine("Starting fetch for SN " + this.statInfo.cid.getSerialNumber(), new Object[0]);
            }
            try {
                if (this.statInfo.responder == null) {
                    if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                        SSLLogger.fine("Null URI detected, OCSP fetch aborted", new Object[0]);
                    }
                    return this.statInfo;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Attempting fetch from " + this.statInfo.responder, new Object[0]);
                }
                final byte[] ocspBytes = OCSP.getOCSPBytes((List)Collections.singletonList(this.statInfo.cid), this.statInfo.responder, (List)((StatusResponseManager.this.ignoreExtensions || !this.responderIds.isEmpty()) ? Collections.emptyList() : this.extensions));
                if (ocspBytes != null) {
                    final ResponseCacheEntry responseData = new ResponseCacheEntry(ocspBytes, this.statInfo.cid);
                    if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                        SSLLogger.fine("OCSP Status: " + responseData.status + " (" + ocspBytes.length + " bytes)", new Object[0]);
                    }
                    if (responseData.status == OCSPResponse.ResponseStatus.SUCCESSFUL) {
                        this.statInfo.responseData = responseData;
                        this.addToCache(this.statInfo.cid, responseData);
                    }
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("No data returned from OCSP Responder", new Object[0]);
                }
            }
            catch (final IOException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Caught exception: ", ex);
                }
            }
            return this.statInfo;
        }
        
        private void addToCache(final CertId certId, final ResponseCacheEntry responseCacheEntry) {
            if (responseCacheEntry.nextUpdate == null && StatusResponseManager.this.cacheLifetime == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Not caching this OCSP response", new Object[0]);
                }
            }
            else {
                StatusResponseManager.this.responseCache.put(certId, responseCacheEntry);
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Added response for SN " + certId.getSerialNumber() + " to cache", new Object[0]);
                }
            }
        }
        
        private long getNextTaskDelay(final Date date) {
            final int cacheLifetime = StatusResponseManager.this.getCacheLifetime();
            long n2;
            if (date != null) {
                final long n = (date.getTime() - System.currentTimeMillis()) / 1000L;
                n2 = ((cacheLifetime > 0) ? Long.min(n, cacheLifetime) : n);
            }
            else {
                n2 = ((cacheLifetime > 0) ? cacheLifetime : -1L);
            }
            return n2;
        }
    }
    
    static final class StaplingParameters
    {
        final SSLExtension statusRespExt;
        final CertStatusExtension.CertStatusRequestType statReqType;
        final CertStatusExtension.CertStatusRequest statReqData;
        final Map<X509Certificate, byte[]> responseMap;
        
        StaplingParameters(final SSLExtension statusRespExt, final CertStatusExtension.CertStatusRequestType statReqType, final CertStatusExtension.CertStatusRequest statReqData, final Map<X509Certificate, byte[]> responseMap) {
            this.statusRespExt = statusRespExt;
            this.statReqType = statReqType;
            this.statReqData = statReqData;
            this.responseMap = responseMap;
        }
    }
}
