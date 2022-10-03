package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.adcs;

import org.apache.http.auth.NTCredentials;
import java.util.regex.Pattern;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import java.net.MalformedURLException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.io.InputStream;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import java.io.IOException;
import java.util.Iterator;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.config.RequestConfig;
import java.util.HashMap;
import javax.net.ssl.SSLContext;
import com.adventnet.sym.server.mdm.MDMProxy;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCAUtil;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordRequestStatus;
import java.net.URL;
import com.adventnet.sym.server.mdm.certificates.scepserver.adcs.AdcsScepServer;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordResponse;
import java.util.Map;
import java.util.List;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.certificates.scep.passwordgetter.ScepPasswordGetter;

public class AdcsPasswordGetter implements ScepPasswordGetter
{
    private final Logger logger;
    
    public AdcsPasswordGetter() {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
    
    @Override
    public Map<Long, PasswordResponse> getPasswordsFromScepServer(final ScepServer scepServer, final List<Long> resourceList) {
        try {
            this.logger.log(Level.INFO, "AdcsPasswordGetter: Create ssl context and proxy for server id: {0}", new Object[] { scepServer.getScepServerId() });
            final URL scepAdminUrl = new URL(((AdcsScepServer)scepServer).getAdminUrl());
            if (!scepAdminUrl.getProtocol().equals("https")) {
                return this.constructMapWithFailureMessage(resourceList, PasswordRequestStatus.HTTP_NOT_ALLOWED);
            }
            final MDMProxy mdmProxy = ThirdPartyCAUtil.getMdmProxy();
            final SSLContext sslContext = this.constructSSLContext(scepServer.getCustomerId(), scepServer.getCertificate());
            if (sslContext != null) {
                return this.requestPasswordForResources(resourceList, (AdcsScepServer)scepServer, sslContext, mdmProxy);
            }
            this.logger.log(Level.SEVERE, "AdcsPasswordGetter: Proxy or ssl context is null. Some problem occurred. Scep Server - {0}", new Object[] { scepServer.getScepServerId() });
        }
        catch (final Exception e) {
            final String eMessage = "AdcsPasswordGetter: Exception while getting passwords from adcs server: " + scepServer.getScepServerId();
            this.logger.log(Level.SEVERE, eMessage, e);
        }
        return null;
    }
    
    private HashMap<Long, PasswordResponse> requestPasswordForResources(final List<Long> resourceList, final AdcsScepServer scepServer, final SSLContext sslContext, final MDMProxy mdmProxy) throws IOException {
        final RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();
        final CredentialsProvider credentialsProvider = this.constructCredentialsProvider(scepServer, mdmProxy);
        final HttpGet httpGet = new HttpGet(scepServer.getAdminUrl());
        final HashMap<Long, PasswordResponse> resourceToPasswordMap = new HashMap<Long, PasswordResponse>();
        try (final CloseableHttpClient httpClient = this.constructHttpClient(sslContext, credentialsProvider, requestConfig)) {
            this.logger.log(Level.INFO, "AdcsPasswordGetter: Getting passwords from Ndes server: {0}", new Object[] { scepServer.getScepServerId() });
            for (final long resource : resourceList) {
                final PasswordResponse passwordResponse = this.executeRequestForResource(httpClient, httpGet, resource);
                resourceToPasswordMap.put(resource, passwordResponse);
            }
        }
        return resourceToPasswordMap;
    }
    
    private PasswordResponse executeRequestForResource(final CloseableHttpClient httpClient, final HttpGet httpGet, final long resource) {
        try (final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)httpGet)) {
            this.logger.log(Level.INFO, "AdcsPasswordGetter: Response received for resource {0}", new Object[] { resource });
            return AdcsResponseHandler.getPasswordResponse(resource, response);
        }
        catch (final Exception e) {
            final String eMessage = "AdcsPasswordGetter: Exception while getting password from Ndes server for resource: " + resource;
            this.logger.log(Level.SEVERE, eMessage, e);
            return new PasswordResponse(PasswordRequestStatus.FAILED, null);
        }
        finally {
            httpGet.releaseConnection();
        }
    }
    
    private CloseableHttpClient constructHttpClient(final SSLContext sslContext, final CredentialsProvider credentialsProvider, final RequestConfig requestConfig) {
        this.logger.log(Level.INFO, "AdcsPasswordGetter: Constructing HttpClient");
        final CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).setDefaultCredentialsProvider(credentialsProvider).setDefaultRequestConfig(requestConfig).build();
        this.logger.log(Level.INFO, "AdcsPasswordGetter: HttpClient constructed");
        return httpClient;
    }
    
    private SSLContext constructSSLContext(final long customerId, final CredentialCertificate certificate) {
        try {
            Certificate[] certificates = null;
            if (certificate != null) {
                certificates = this.getCACertificateChainForScepServer(customerId, certificate.getCertificateId());
            }
            return ThirdPartyCAUtil.createCustomSslContext(certificates, null, null);
        }
        catch (final Exception e) {
            final String eMessage = "Exception while constructing SSL context for ADCS server ";
            this.logger.log(Level.SEVERE, eMessage, e);
            return null;
        }
    }
    
    private Certificate[] getCACertificateChainForScepServer(final long customerId, final long certificateId) throws Exception {
        final CredentialCertificate certificateDetails = ProfileCertificateUtil.getCACertDetails(customerId, certificateId);
        Certificate[] certificates = null;
        if (certificateDetails != null) {
            final String certFilePath = this.getCertFilePath(customerId, certificateDetails);
            final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(certFilePath);
            certificates = CertificateUtil.convertInputStreamToX509CertificateChain(inputStream);
        }
        return certificates;
    }
    
    private String getCertFilePath(final long customerId, final CredentialCertificate certificateDetails) throws Exception {
        final String caCertFileName = certificateDetails.getCertificateFileName();
        final String credentialCertificateFolder = MDMUtil.getCredentialCertificateFolder(customerId);
        return credentialCertificateFolder + File.separator + caCertFileName;
    }
    
    private CredentialsProvider constructCredentialsProvider(final AdcsScepServer scepServer, final MDMProxy mdmProxy) throws MalformedURLException {
        final CredentialsProvider credentialsProvider = (CredentialsProvider)new BasicCredentialsProvider();
        if (mdmProxy != null) {
            this.addProxyCredentialsForRequest(credentialsProvider, mdmProxy);
        }
        this.addScepServerNtlmCredentialsForRequest(credentialsProvider, scepServer);
        return credentialsProvider;
    }
    
    private void addProxyCredentialsForRequest(final CredentialsProvider credentialsProvider, final MDMProxy mdmProxy) {
        this.logger.log(Level.INFO, "AdcsPasswordGetter: Adding proxy scope credentials: {0}", new Object[] { mdmProxy.getProxyServerHost() });
        final AuthScope proxyScope = new AuthScope(mdmProxy.getProxyServerHost(), mdmProxy.getProxyServerPort());
        final Credentials proxyUsernamePasswordCredentials = (Credentials)new UsernamePasswordCredentials(mdmProxy.getProxyUsername(), mdmProxy.getProxyPassword());
        credentialsProvider.setCredentials(proxyScope, proxyUsernamePasswordCredentials);
    }
    
    private void addScepServerNtlmCredentialsForRequest(final CredentialsProvider credentialsProvider, final AdcsScepServer scepServer) throws MalformedURLException {
        this.logger.log(Level.INFO, "AdcsPasswordGetter: Adding NTLM scope credentials: {0}", new Object[] { scepServer.getScepServerId() });
        final String ndesHost = new URL(scepServer.getAdminUrl()).getHost();
        final int ndesPort = this.getScepServerPort(scepServer);
        final AuthScope ntlmAuthScope = new AuthScope(ndesHost, ndesPort);
        String domain = "";
        String username = scepServer.getAdminUsername();
        if (username.contains("\\")) {
            final String[] domainAndUser = username.split(Pattern.quote("\\"));
            domain = domainAndUser[0];
            username = domainAndUser[1];
        }
        final Credentials ntlmUsernamePasswordCredentials = (Credentials)new NTCredentials(username, scepServer.getAdminPassword(), "", domain);
        credentialsProvider.setCredentials(ntlmAuthScope, ntlmUsernamePasswordCredentials);
    }
    
    private int getScepServerPort(final AdcsScepServer scepServer) throws MalformedURLException {
        int ndesPort = new URL(scepServer.getAdminUrl()).getPort();
        ndesPort = ((ndesPort == -1) ? 443 : ndesPort);
        return ndesPort;
    }
    
    private Map<Long, PasswordResponse> constructMapWithFailureMessage(final List<Long> resourceList, final PasswordRequestStatus status) {
        final Map<Long, PasswordResponse> passwordResponseMap = new HashMap<Long, PasswordResponse>();
        for (final Long resource : resourceList) {
            final PasswordResponse passwordResponse = new PasswordResponse(status, null);
            passwordResponseMap.put(resource, passwordResponse);
        }
        return passwordResponseMap;
    }
}
