package com.azul.crs.client;

import java.nio.charset.StandardCharsets;
import com.azul.crs.shared.models.VMArtifactChunk;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import com.azul.crs.shared.models.VMEvent;
import java.util.Collection;
import com.azul.crs.shared.Utils;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManager;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStoreException;
import javax.net.ssl.TrustManager;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.io.IOException;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import com.azul.crs.shared.models.AuthToken;
import com.azul.crs.util.logging.Logger;
import com.azul.crs.shared.models.Payload;

public class ConnectionManager
{
    private static final String UTF_8;
    private static final Class<Payload> VOID;
    private static final String AUTH_TOKEN_RESOURCE = "/crs/auth/rt/token";
    private static final String EVENT_RESOURCE = "/crs/instance/{vmId}";
    private static final String ARTIFACT_CHUNK_RESOURCE = "/crs/artifact/chunk";
    private static final String MEDIA_TYPE_TEXT_PLAIN = "text/plain";
    private static final String MEDIA_TYPE_JSON = "application/json";
    private static final String MEDIA_TYPE_BINARY = "application/octet-stream";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_STREAM_LENGTH = "Stream-Length";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_PATCH = "PATCH";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_SLEEP = 100L;
    private static final String YEK_IPA = "r1fhe2lVGN1EgDHH0Eg8d94tjv12e0F7a78RNysB";
    private final Logger logger;
    private final String restAPI;
    private final String mailbox;
    private final ConnectionListener listener;
    private static final long TOKEN_REFRESH_TIMEOUT_MS = 300000L;
    private final Client client;
    private final String keystore;
    private AuthToken runtimeToken;
    private long nextRuntimeTokenRefreshTimeCount;
    private String vmId;
    private boolean unrecoverableError;
    private SSLSocketFactory sslSocketFactoryOne;
    private SSLSocketFactory sslSocketFactoryTwo;
    private static final ConnectionConsumer NONE;
    
    ConnectionManager(final Map<Client.ClientProp, Object> props, final Client client, final ConnectionListener listener) {
        this.logger = Logger.getLogger(ConnectionManager.class);
        this.client = client;
        this.listener = listener;
        this.restAPI = props.get(Client.ClientProp.API_URL);
        this.mailbox = props.get(Client.ClientProp.API_MAILBOX);
        this.keystore = props.get(Client.ClientProp.KS);
        this.logger.info("Using CRS endpoint configuration\n   API url = %s\n   mailbox = %s", this.restAPI, this.mailbox);
        if (this.keystore != null) {
            this.logger.info("   auth override keystore = %s", this.keystore);
        }
    }
    
    void start() throws IOException {
        this.createCustomTrustManagers();
        this.saveRuntimeToken(this.getRuntimeToken(this.client.getClientVersion(), this.mailbox));
    }
    
    private void saveRuntimeToken(final AuthToken token) {
        this.runtimeToken = token;
        this.vmId = this.runtimeToken.getVmId();
        this.listener.authenticated();
    }
    
    private X509TrustManager getX509TrustManager(final KeyStore ks) throws NoSuchAlgorithmException, KeyStoreException {
        final TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmFactory.init(ks);
        for (final TrustManager tm : tmFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager)tm;
            }
        }
        throw new NoSuchAlgorithmException();
    }
    
    private void createCustomTrustManagers() throws CRSException {
        try {
            final char[] password = "crscrs".toCharArray();
            final KeyStore ks = KeyStore.getInstance("JKS");
            try (final InputStream keystoreStream = (this.keystore == null) ? this.getClass().getResourceAsStream("crs.jks") : new FileInputStream(this.keystore)) {
                ks.load(keystoreStream, password);
            }
            final KeyManagerFactory kmFactory = KeyManagerFactory.getInstance("NewSunX509");
            kmFactory.init(ks, password);
            final X509TrustManager tmOne = this.getX509TrustManager(ks);
            final X509TrustManager tmDefault = this.getX509TrustManager(null);
            final int icount1 = tmOne.getAcceptedIssuers().length;
            final int icount2 = tmDefault.getAcceptedIssuers().length;
            final X509Certificate[] allIssuers = new X509Certificate[icount1 + icount2];
            System.arraycopy(tmOne.getAcceptedIssuers(), 0, allIssuers, 0, icount1);
            System.arraycopy(tmDefault.getAcceptedIssuers(), 0, allIssuers, icount1, icount2);
            final X509TrustManager tmTwo = new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                    throw new CertificateException("unsupported operation");
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                    try {
                        tmOne.checkServerTrusted(chain, authType);
                    }
                    catch (final CertificateException ignored) {
                        tmDefault.checkServerTrusted(chain, authType);
                    }
                }
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return allIssuers;
                }
            };
            final KeyManager[] keyManagers = kmFactory.getKeyManagers();
            this.sslSocketFactoryOne = this.createSocketFactory(tmOne, keyManagers);
            this.sslSocketFactoryTwo = this.createSocketFactory(tmTwo, keyManagers);
        }
        catch (final IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyManagementException ex) {
            this.unrecoverableError = true;
            throw new CRSException(-4, "Unrecoverable internal error: ", ex);
        }
    }
    
    private SSLSocketFactory createSocketFactory(final X509TrustManager tm, final KeyManager[] keyManagers) throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, new TrustManager[] { tm }, null);
        return sslContext.getSocketFactory();
    }
    
    private HttpsURLConnection createConnection(final String url) throws IOException {
        if (this.unrecoverableError) {
            throw new IOException("Unrecoverable error");
        }
        final URL endpoint = new URL(url);
        final HttpsURLConnection con = (HttpsURLConnection)endpoint.openConnection();
        con.setConnectTimeout(30000);
        con.setReadTimeout(20000);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        return con;
    }
    
    private <T extends Payload> Response<T> requestAny(final String resource, String method, final ConnectionConsumer headerWriter, final ConnectionConsumer requestWriter, final Class<T> responseType) throws IOException {
        final long startTime = Utils.currentTimeCount();
        final Response<T> response = new Response<T>();
        final HttpsURLConnection con = this.createConnection(resource);
        con.setSSLSocketFactory(this.sslSocketFactoryOne);
        if (method.equals("PATCH")) {
            con.setRequestProperty("X-HTTP-Method-Override", method);
            method = "POST";
        }
        con.setRequestProperty("Authorization", "Bearer " + this.runtimeToken.getToken());
        con.setRequestMethod(method);
        headerWriter.consume(con);
        con.connect();
        PerformanceMetrics.logHandshakeTime(Utils.elapsedTimeMillis(startTime));
        try {
            requestWriter.consume(con);
            final int code = con.getResponseCode();
            final String message = con.getResponseMessage();
            response.code(code);
            response.message(message);
            if (code < 200 || code >= 300) {
                if (code == 401 && Utils.currentTimeCount() > this.nextRuntimeTokenRefreshTimeCount) {
                    this.saveRuntimeToken(this.refreshRuntimeToken(this.runtimeToken));
                    return (Response<T>)this.requestAny(resource, method, headerWriter, requestWriter, (Class<Payload>)responseType);
                }
                if (con.getErrorStream() != null) {
                    response.error(this.readStream(con.getErrorStream()));
                }
            }
            else if (responseType != null) {
                response.payload(Payload.fromJson(con.getInputStream(), responseType));
            }
        }
        finally {
            PerformanceMetrics.logNetworkTime(Utils.elapsedTimeMillis(startTime));
            con.disconnect();
        }
        return response;
    }
    
    public Response<Payload> sendVMEventBatch(final Collection<VMEvent> events) throws IOException {
        return this.requestAnyJson(this.restAPI + "/crs/instance/{vmId}".replace("{vmId}", this.vmId), "POST", Payload.toJsonArray(events), ConnectionManager.VOID);
    }
    
    public void requestWithRetries(final ResponseSupplier<Payload> request, final String requestName, final int maxRetries, final long retrySleep) {
        final Result<Payload> result = this.requestWithRetriesImpl(request, requestName, maxRetries, retrySleep);
        if (!result.successful()) {
            this.listener.syncFailed(result);
        }
    }
    
    private <T extends Payload> Result<T> requestWithRetriesImpl(final ResponseSupplier<T> request, final String requestName, final int maxRetries, final long retrySleep) {
        int attempt = 1;
        final long startTime = Utils.currentTimeCount();
        Result<T> result;
        while (true) {
            try {
                result = new Result<T>(request.get());
                if (result.successful()) {
                    this.logger.debug("Request %s succeeded on attempt %d, elapsed%s", requestName, attempt, Utils.elapsedTimeString(startTime));
                    return result;
                }
            }
            catch (final IOException ioe) {
                this.logger.debug("Request %s failed on attempt %s of %s, elapsed%s: %s", requestName, attempt, maxRetries, Utils.elapsedTimeString(startTime), ioe.toString());
                result = new Result<T>(ioe);
            }
            if (!result.canRetry() || ++attempt > maxRetries) {
                break;
            }
            Utils.sleep(retrySleep);
        }
        this.logger.debug("Request %s aborted after %d attempt, elapsed%s", requestName, attempt, Utils.elapsedTimeString(startTime));
        return result;
    }
    
     <T extends Payload> Response<T> requestAnyJson(final String resource, final String method, final String payload, final Class<T> responseType) throws IOException {
        return this.requestAny(resource, method, ConnectionManager.NONE, new ConnectionConsumer() {
            @Override
            public void consume(final HttpsURLConnection con) throws IOException {
                ConnectionManager.this.logger.debug("%s %s\n", method, resource);
                ConnectionManager.this.logger.trace("%s\n\n", payload);
                ConnectionManager.this.writeData(con, payload.getBytes(ConnectionManager.UTF_8));
            }
        }, responseType);
    }
    
    private String readStream(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        long totalLength = 0L;
        final byte[] readBuffer = new byte[1024];
        try {
            int length;
            while ((length = inputStream.read(readBuffer)) != -1) {
                outputStream.write(readBuffer, 0, length);
                totalLength += length;
            }
        }
        finally {
            PerformanceMetrics.logBytes(totalLength, 0L);
        }
        return outputStream.toString(ConnectionManager.UTF_8);
    }
    
    private void writeData(final URLConnection con, final byte[] data, final int size) throws IOException {
        try (final OutputStream out = con.getOutputStream()) {
            out.write(data, 0, size);
            PerformanceMetrics.logBytes(0L, size);
        }
    }
    
    private void writeData(final URLConnection con, final byte[] data) throws IOException {
        this.writeData(con, data, data.length);
    }
    
    private long writeData(final URLConnection con, final InputStream in) throws IOException {
        long written = 0L;
        try (final OutputStream out = con.getOutputStream()) {
            final byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
                written += length;
            }
        }
        finally {
            PerformanceMetrics.logBytes(0L, written);
        }
        return written;
    }
    
    private Response putBinaryData(final String location, final ConnectionConsumer requestWriter) throws IOException {
        final long startTime = Utils.currentTimeCount();
        final Response response = new Response();
        final HttpsURLConnection con = this.createConnection(location);
        con.setSSLSocketFactory(this.sslSocketFactoryTwo);
        con.setRequestProperty("Content-Type", "application/octet-stream");
        con.setRequestMethod("PUT");
        con.connect();
        PerformanceMetrics.logHandshakeTime(Utils.elapsedTimeMillis(startTime));
        try {
            requestWriter.consume(con);
            response.code(con.getResponseCode());
            response.message(con.getResponseMessage());
            if (!response.successful()) {
                response.error(this.readStream(con.getErrorStream()));
            }
        }
        finally {
            PerformanceMetrics.logNetworkTime(Utils.elapsedTimeMillis(startTime));
            con.disconnect();
        }
        return response;
    }
    
    private Response putBinaryData(final String location, final InputStream is) throws IOException {
        return this.putBinaryData(location, new ConnectionConsumer() {
            @Override
            public void consume(final HttpsURLConnection con) throws IOException {
                ConnectionManager.this.writeData(con, is);
            }
        });
    }
    
    public Response<VMArtifactChunk> sendVMArtifactChunk(final VMArtifactChunk chunk, final InputStream is) throws IOException {
        final Response<VMArtifactChunk> createResponse = this.requestAnyJson(this.restAPI + "/crs/artifact/chunk", "POST", chunk.toJson(), VMArtifactChunk.class);
        if (createResponse.successful() && is != null) {
            final VMArtifactChunk created = createResponse.getPayload();
            final String location = created.getLocation();
            final Response uploadResponse = this.putBinaryData(location, is);
            if (!uploadResponse.successful()) {
                throw new IOException("Created VM artifact chunk failed to upload data: chunkId=" + created.getChunkId() + ", error=" + uploadResponse.getError());
            }
        }
        return createResponse;
    }
    
    private AuthToken readRuntimeToken(final HttpsURLConnection con) throws IOException {
        final AuthToken token = Payload.fromJson(con.getInputStream(), AuthToken.class);
        final long expiresAt = Utils.currentTimeMillis() + token.getExpirationTime();
        token.setExpirationTime(expiresAt);
        return token;
    }
    
    private Response<AuthToken> retrieveRuntimeToken(final HttpsURLConnection con) throws IOException {
        final Response<AuthToken> response = new Response<AuthToken>().code(con.getResponseCode()).message(con.getResponseMessage());
        if (!response.successful()) {
            if (con.getErrorStream() != null) {
                response.error(this.readStream(con.getErrorStream()));
            }
            return response;
        }
        return response.payload(this.readRuntimeToken(con));
    }
    
    private AuthToken getRuntimeToken(final String clientVersion, final String mailbox) throws CRSException {
        this.logger.info("Get runtime token: clientVersion=%s, mailbox=%s", clientVersion, mailbox);
        final long startTime = Utils.currentTimeCount();
        this.nextRuntimeTokenRefreshTimeCount = Utils.nextTimeCount(300000L);
        final Result<AuthToken> result = this.requestWithRetriesImpl((ResponseSupplier<AuthToken>)new ResponseSupplier<AuthToken>() {
            @Override
            public Response<AuthToken> get() throws IOException {
                final long attemptStartTime = Utils.currentTimeCount();
                final HttpsURLConnection con = ConnectionManager.this.createConnection(ConnectionManager.this.restAPI + "/crs/auth/rt/token" + "?clientVersion=" + clientVersion + "&mailbox=" + mailbox);
                con.setSSLSocketFactory(ConnectionManager.this.sslSocketFactoryOne);
                con.setRequestProperty("x-api-key", "r1fhe2lVGN1EgDHH0Eg8d94tjv12e0F7a78RNysB");
                con.setRequestMethod("GET");
                con.connect();
                PerformanceMetrics.logHandshakeTime(Utils.elapsedTimeMillis(attemptStartTime));
                try {
                    return ConnectionManager.this.retrieveRuntimeToken(con);
                }
                finally {
                    PerformanceMetrics.logNetworkTime(Utils.elapsedTimeMillis(startTime));
                    con.disconnect();
                }
            }
        }, "getRuntimeToken", 3, 100L);
        if (!result.successful()) {
            throw new CRSException(this.client, -2, "Cannot get runtime token: ", result);
        }
        return result.getResponse().getPayload();
    }
    
    private AuthToken refreshRuntimeToken(final AuthToken runtimeToken) throws IOException {
        final long startTime = Utils.currentTimeCount();
        this.nextRuntimeTokenRefreshTimeCount = Utils.nextTimeCount(300000L);
        this.logger.info("Refresh runtime token", new Object[0]);
        final Result<AuthToken> result = this.requestWithRetriesImpl((ResponseSupplier<AuthToken>)new ResponseSupplier<AuthToken>() {
            @Override
            public Response<AuthToken> get() throws IOException {
                final long attemptStartTime = Utils.currentTimeCount();
                final HttpsURLConnection con = ConnectionManager.this.createConnection(ConnectionManager.this.restAPI + "/crs/auth/rt/token");
                con.setSSLSocketFactory(ConnectionManager.this.sslSocketFactoryOne);
                con.setRequestProperty("x-api-key", "r1fhe2lVGN1EgDHH0Eg8d94tjv12e0F7a78RNysB");
                con.setRequestProperty("Content-Type", "text/plain");
                con.setRequestMethod("POST");
                con.connect();
                PerformanceMetrics.logHandshakeTime(Utils.elapsedTimeMillis(attemptStartTime));
                try {
                    con.getOutputStream().write(runtimeToken.getToken().getBytes());
                    return ConnectionManager.this.retrieveRuntimeToken(con);
                }
                finally {
                    PerformanceMetrics.logNetworkTime(Utils.elapsedTimeMillis(startTime));
                    con.disconnect();
                }
            }
        }, "refreshRuntimeToken", 3, 100L);
        if (!result.successful()) {
            throw new CRSException(this.client, -2, "Cannot refresh runtime token: ", result);
        }
        return result.getResponse().getPayload();
    }
    
    public String getVmId() {
        return this.vmId;
    }
    
    public String getMailbox() {
        return this.mailbox;
    }
    
    public String getRestAPI() {
        return this.restAPI;
    }
    
    static {
        UTF_8 = StandardCharsets.UTF_8.name();
        VOID = null;
        NONE = new ConnectionConsumer() {
            @Override
            public void consume(final HttpsURLConnection con) throws IOException {
            }
        };
    }
    
    @FunctionalInterface
    public interface ResponseSupplier<T extends Payload>
    {
        Response<T> get() throws IOException;
    }
    
    private interface ConnectionConsumer
    {
        void consume(final HttpsURLConnection p0) throws IOException;
    }
    
    interface ConnectionListener
    {
        void authenticated();
        
        void syncFailed(final Result<Payload> p0);
    }
}
