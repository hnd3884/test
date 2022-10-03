package org.openjsse.sun.security.ssl;

import sun.security.provider.certpath.ResponderId;
import sun.security.provider.certpath.OCSPResponse;
import java.util.Date;
import java.security.cert.Extension;
import java.util.Iterator;
import java.util.List;
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
        final int cap = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.stapling.cacheSize", 256));
        this.cacheCapacity = ((cap > 0) ? cap : 0);
        final int life = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.stapling.cacheLifetime", 3600));
        this.cacheLifetime = ((life > 0) ? life : 0);
        final String uriStr = GetPropertyAction.privilegedGetProperty("jdk.tls.stapling.responderURI");
        URI tmpURI;
        try {
            tmpURI = ((uriStr != null && !uriStr.isEmpty()) ? new URI(uriStr) : null);
        }
        catch (final URISyntaxException urise) {
            tmpURI = null;
        }
        this.defaultResponder = tmpURI;
        this.respOverride = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("jdk.tls.stapling.responderOverride"));
        this.ignoreExtensions = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("jdk.tls.stapling.ignoreExtensions"));
        (this.threadMgr = new ScheduledThreadPoolExecutor(8, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
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
    
    URI getURI(final X509Certificate cert) {
        Objects.requireNonNull(cert);
        if (cert.getExtensionValue(PKIXExtensions.OCSPNoCheck_Id.toString()) != null) {
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
        final URI certURI = OCSP.getResponderURI(cert);
        return (certURI != null) ? certURI : this.defaultResponder;
    }
    
    void shutdown() {
        if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Shutting down " + this.threadMgr.getActiveCount() + " active threads", new Object[0]);
        }
        this.threadMgr.shutdown();
    }
    
    Map<X509Certificate, byte[]> get(final CertStatusExtension.CertStatusRequestType type, final CertStatusExtension.CertStatusRequest request, final X509Certificate[] chain, final long delay, final TimeUnit unit) {
        final Map<X509Certificate, byte[]> responseMap = new HashMap<X509Certificate, byte[]>();
        final List<OCSPFetchCall> requestList = new ArrayList<OCSPFetchCall>();
        if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Beginning check: Type = " + type + ", Chain length = " + chain.length, new Object[0]);
        }
        if (chain.length < 2) {
            return Collections.emptyMap();
        }
        if (type == CertStatusExtension.CertStatusRequestType.OCSP) {
            try {
                final CertStatusExtension.OCSPStatusRequest ocspReq = (CertStatusExtension.OCSPStatusRequest)request;
                final CertId cid = new CertId(chain[1], new SerialNumber(chain[0].getSerialNumber()));
                final ResponseCacheEntry cacheEntry = this.getFromCache(cid, ocspReq);
                if (cacheEntry != null) {
                    responseMap.put(chain[0], cacheEntry.ocspBytes);
                }
                else {
                    final StatusInfo sInfo = new StatusInfo(chain[0], cid);
                    requestList.add(new OCSPFetchCall(sInfo, ocspReq));
                }
            }
            catch (final IOException exc) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Exception during CertId creation: ", exc);
                }
            }
        }
        else if (type == CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
            final CertStatusExtension.OCSPStatusRequest ocspReq = (CertStatusExtension.OCSPStatusRequest)request;
            for (int ctr = 0; ctr < chain.length - 1; ++ctr) {
                try {
                    final CertId cid2 = new CertId(chain[ctr + 1], new SerialNumber(chain[ctr].getSerialNumber()));
                    final ResponseCacheEntry cacheEntry2 = this.getFromCache(cid2, ocspReq);
                    if (cacheEntry2 != null) {
                        responseMap.put(chain[ctr], cacheEntry2.ocspBytes);
                    }
                    else {
                        final StatusInfo sInfo2 = new StatusInfo(chain[ctr], cid2);
                        requestList.add(new OCSPFetchCall(sInfo2, ocspReq));
                    }
                }
                catch (final IOException exc2) {
                    if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                        SSLLogger.fine("Exception during CertId creation: ", exc2);
                    }
                }
            }
        }
        else if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Unsupported status request type: " + type, new Object[0]);
        }
        if (!requestList.isEmpty()) {
            try {
                final List<Future<StatusInfo>> resultList = this.threadMgr.invokeAll((Collection<? extends Callable<StatusInfo>>)requestList, delay, unit);
                for (final Future<StatusInfo> task : resultList) {
                    if (!task.isDone()) {
                        continue;
                    }
                    if (!task.isCancelled()) {
                        final StatusInfo info = task.get();
                        if (info != null && info.responseData != null) {
                            responseMap.put(info.cert, info.responseData.ocspBytes);
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
            catch (final InterruptedException | ExecutionException exc3) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Exception when getting data: ", exc3);
                }
            }
        }
        return Collections.unmodifiableMap((Map<? extends X509Certificate, ? extends byte[]>)responseMap);
    }
    
    private ResponseCacheEntry getFromCache(final CertId cid, final CertStatusExtension.OCSPStatusRequest ocspRequest) {
        for (final Extension ext : ocspRequest.extensions) {
            if (ext.getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Nonce extension found, skipping cache check", new Object[0]);
                }
                return null;
            }
        }
        ResponseCacheEntry respEntry = this.responseCache.get(cid);
        if (respEntry != null && respEntry.nextUpdate != null && respEntry.nextUpdate.before(new Date())) {
            if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                SSLLogger.fine("nextUpdate threshold exceeded, purging from cache", new Object[0]);
            }
            respEntry = null;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
            SSLLogger.fine("Check cache for SN" + cid.getSerialNumber() + ": " + ((respEntry != null) ? "HIT" : "MISS"), new Object[0]);
        }
        return respEntry;
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
    
    static final StaplingParameters processStapling(final ServerHandshakeContext shc) {
        StaplingParameters params = null;
        SSLExtension ext = null;
        CertStatusExtension.CertStatusRequestType type = null;
        CertStatusExtension.CertStatusRequest req = null;
        if (!shc.sslContext.isStaplingEnabled(false) || shc.isResumption) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Staping disabled or is a resumed session", new Object[0]);
            }
            return null;
        }
        final Map<SSLExtension, SSLExtension.SSLExtensionSpec> exts = shc.handshakeExtensions;
        final CertStatusExtension.CertStatusRequestSpec statReq = exts.get(SSLExtension.CH_STATUS_REQUEST);
        final CertStatusExtension.CertStatusRequestV2Spec statReqV2 = exts.get(SSLExtension.CH_STATUS_REQUEST_V2);
        if (statReqV2 != null && !shc.negotiatedProtocol.useTLS13PlusSpec()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.fine("SH Processing status_request_v2 extension", new Object[0]);
            }
            ext = SSLExtension.CH_STATUS_REQUEST_V2;
            int ocspIdx = -1;
            int ocspMultiIdx = -1;
            final CertStatusExtension.CertStatusRequest[] reqItems = statReqV2.certStatusRequests;
            for (int pos = 0; pos < reqItems.length && (ocspIdx == -1 || ocspMultiIdx == -1); ++pos) {
                final CertStatusExtension.CertStatusRequest item = reqItems[pos];
                final CertStatusExtension.CertStatusRequestType curType = CertStatusExtension.CertStatusRequestType.valueOf(item.statusType);
                if (ocspIdx < 0 && curType == CertStatusExtension.CertStatusRequestType.OCSP) {
                    final CertStatusExtension.OCSPStatusRequest ocspReq = (CertStatusExtension.OCSPStatusRequest)item;
                    if (ocspReq.responderIds.isEmpty()) {
                        ocspIdx = pos;
                    }
                }
                else if (ocspMultiIdx < 0 && curType == CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    final CertStatusExtension.OCSPStatusRequest ocspReq = (CertStatusExtension.OCSPStatusRequest)item;
                    if (ocspReq.responderIds.isEmpty()) {
                        ocspMultiIdx = pos;
                    }
                }
            }
            if (ocspMultiIdx >= 0) {
                req = reqItems[ocspMultiIdx];
                type = CertStatusExtension.CertStatusRequestType.valueOf(req.statusType);
            }
            else if (ocspIdx >= 0) {
                req = reqItems[ocspIdx];
                type = CertStatusExtension.CertStatusRequestType.valueOf(req.statusType);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: No suitable request found in the status_request_v2 extension.", new Object[0]);
            }
        }
        if (statReq != null && (ext == null || type == null || req == null)) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.fine("SH Processing status_request extension", new Object[0]);
            }
            ext = SSLExtension.CH_STATUS_REQUEST;
            type = CertStatusExtension.CertStatusRequestType.valueOf(statReq.statusRequest.statusType);
            if (type == CertStatusExtension.CertStatusRequestType.OCSP) {
                final CertStatusExtension.OCSPStatusRequest ocspReq2 = (CertStatusExtension.OCSPStatusRequest)statReq.statusRequest;
                if (ocspReq2.responderIds.isEmpty()) {
                    req = ocspReq2;
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Warning: No suitable request found in the status_request extension.", new Object[0]);
                }
            }
        }
        if (type == null || req == null || ext == null) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("No suitable status_request or status_request_v2, stapling is disabled", new Object[0]);
            }
            return null;
        }
        X509Authentication.X509Possession x509Possession = null;
        for (final SSLPossession possession : shc.handshakePossessions) {
            if (possession instanceof X509Authentication.X509Possession) {
                x509Possession = (X509Authentication.X509Possession)possession;
                break;
            }
        }
        if (x509Possession == null) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: no X.509 certificates found.  Stapling is disabled.", new Object[0]);
            }
            return null;
        }
        final X509Certificate[] certs = x509Possession.popCerts;
        final StatusResponseManager statRespMgr = shc.sslContext.getStatusResponseManager();
        if (statRespMgr != null) {
            final CertStatusExtension.CertStatusRequestType fetchType = shc.negotiatedProtocol.useTLS13PlusSpec() ? CertStatusExtension.CertStatusRequestType.OCSP_MULTI : type;
            final Map<X509Certificate, byte[]> responses = statRespMgr.get(fetchType, req, certs, shc.statusRespTimeout, TimeUnit.MILLISECONDS);
            if (!responses.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Response manager returned " + responses.size() + " entries.", new Object[0]);
                }
                if (type == CertStatusExtension.CertStatusRequestType.OCSP) {
                    final byte[] respDER = responses.get(certs[0]);
                    if (respDER == null || respDER.length <= 0) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.finest("Warning: Null or zero-length response found for leaf certificate. Stapling is disabled.", new Object[0]);
                        }
                        return null;
                    }
                }
                params = new StaplingParameters(ext, type, req, responses);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: no OCSP responses obtained.  Stapling is disabled.", new Object[0]);
            }
        }
        else {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.finest("Warning: lazy initialization of the StatusResponseManager failed.  Stapling is disabled.", new Object[0]);
            }
            params = null;
        }
        return params;
    }
    
    class StatusInfo
    {
        final X509Certificate cert;
        final CertId cid;
        final URI responder;
        ResponseCacheEntry responseData;
        
        StatusInfo(final StatusResponseManager this$0, final X509Certificate subjectCert, final X509Certificate issuerCert) throws IOException {
            this(this$0, subjectCert, new CertId(issuerCert, new SerialNumber(subjectCert.getSerialNumber())));
        }
        
        StatusInfo(final X509Certificate subjectCert, final CertId certId) {
            this.cert = subjectCert;
            this.cid = certId;
            this.responder = StatusResponseManager.this.getURI(this.cert);
            this.responseData = null;
        }
        
        StatusInfo(final StatusInfo orig) {
            this.cert = orig.cert;
            this.cid = orig.cid;
            this.responder = orig.responder;
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
        
        ResponseCacheEntry(final byte[] responseBytes, final CertId cid) throws IOException {
            Objects.requireNonNull(responseBytes, "Non-null responseBytes required");
            Objects.requireNonNull(cid, "Non-null Cert ID required");
            this.ocspBytes = responseBytes.clone();
            final OCSPResponse oResp = new OCSPResponse(this.ocspBytes);
            this.status = oResp.getResponseStatus();
            this.respId = oResp.getResponderId();
            this.singleResp = oResp.getSingleResponse(cid);
            if (this.status == OCSPResponse.ResponseStatus.SUCCESSFUL) {
                if (this.singleResp == null) {
                    throw new IOException("Unable to find SingleResponse for SN " + cid.getSerialNumber());
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
        
        public OCSPFetchCall(final StatusInfo info, final CertStatusExtension.OCSPStatusRequest request) {
            this.statInfo = Objects.requireNonNull(info, "Null StatusInfo not allowed");
            this.ocspRequest = Objects.requireNonNull(request, "Null OCSPStatusRequest not allowed");
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
                final List<Extension> extsToSend = (StatusResponseManager.this.ignoreExtensions || !this.responderIds.isEmpty()) ? Collections.emptyList() : this.extensions;
                final byte[] respBytes = OCSP.getOCSPBytes((List)Collections.singletonList(this.statInfo.cid), this.statInfo.responder, (List)extsToSend);
                if (respBytes != null) {
                    final ResponseCacheEntry cacheEntry = new ResponseCacheEntry(respBytes, this.statInfo.cid);
                    if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                        SSLLogger.fine("OCSP Status: " + cacheEntry.status + " (" + respBytes.length + " bytes)", new Object[0]);
                    }
                    if (cacheEntry.status == OCSPResponse.ResponseStatus.SUCCESSFUL) {
                        this.statInfo.responseData = cacheEntry;
                        this.addToCache(this.statInfo.cid, cacheEntry);
                    }
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("No data returned from OCSP Responder", new Object[0]);
                }
            }
            catch (final IOException ioe) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Caught exception: ", ioe);
                }
            }
            return this.statInfo;
        }
        
        private void addToCache(final CertId certId, final ResponseCacheEntry entry) {
            if (entry.nextUpdate == null && StatusResponseManager.this.cacheLifetime == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Not caching this OCSP response", new Object[0]);
                }
            }
            else {
                StatusResponseManager.this.responseCache.put(certId, entry);
                if (SSLLogger.isOn && SSLLogger.isOn("respmgr")) {
                    SSLLogger.fine("Added response for SN " + certId.getSerialNumber() + " to cache", new Object[0]);
                }
            }
        }
        
        private long getNextTaskDelay(final Date nextUpdate) {
            final int lifetime = StatusResponseManager.this.getCacheLifetime();
            long delaySec;
            if (nextUpdate != null) {
                final long nuDiffSec = (nextUpdate.getTime() - System.currentTimeMillis()) / 1000L;
                delaySec = ((lifetime > 0) ? Long.min(nuDiffSec, lifetime) : nuDiffSec);
            }
            else {
                delaySec = ((lifetime > 0) ? lifetime : -1L);
            }
            return delaySec;
        }
    }
    
    static final class StaplingParameters
    {
        final SSLExtension statusRespExt;
        final CertStatusExtension.CertStatusRequestType statReqType;
        final CertStatusExtension.CertStatusRequest statReqData;
        final Map<X509Certificate, byte[]> responseMap;
        
        StaplingParameters(final SSLExtension ext, final CertStatusExtension.CertStatusRequestType type, final CertStatusExtension.CertStatusRequest req, final Map<X509Certificate, byte[]> responses) {
            this.statusRespExt = ext;
            this.statReqType = type;
            this.statReqData = req;
            this.responseMap = responses;
        }
    }
}
