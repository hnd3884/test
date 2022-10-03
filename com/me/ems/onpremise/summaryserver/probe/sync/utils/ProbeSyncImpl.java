package com.me.ems.onpremise.summaryserver.probe.sync.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import java.util.Iterator;
import org.apache.http.impl.client.CloseableHttpClient;
import java.util.logging.Level;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import com.adventnet.iam.security.UploadedFileItem;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.net.HttpURLConnection;
import com.me.ems.summaryserver.probe.sync.factory.ProbeSyncAPI;
import com.me.ems.summaryserver.probe.sync.utils.SyncUtil;

public class ProbeSyncImpl extends SyncUtil implements ProbeSyncAPI
{
    private String sourceClass;
    
    public ProbeSyncImpl() {
        this.sourceClass = "ProbeSyncImpl";
    }
    
    public HttpURLConnection processRequest(final String url, final String content_type, final String accept) {
        HttpURLConnection conn = HttpsHandlerUtil.getServerUrlConnection(url);
        try {
            final Map<String, Object> headerParams = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerAPIKeyDetails();
            final String summaryApiKey = headerParams.get("summaryServerAuthKey");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("SummaryAuthorization", summaryApiKey);
            conn.setRequestProperty("probeId", headerParams.get("probeId"));
            conn.setRequestProperty("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
            conn.setRequestProperty("summaryServerRequest", "true");
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("userDomain", this.encryptUserDomain(summaryApiKey));
            if (conn.getURL().toString().contains("https")) {
                conn = HttpsHandlerUtil.skipCertificateCheck(conn);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    public Map<String, String> getUserDomainDetails(final boolean needDefaultAdmin) {
        final Map<String, String> userProb = new HashMap<String, String>();
        String domainName = null;
        String userName = null;
        if (!needDefaultAdmin) {
            try {
                userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                domainName = ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName();
                userProb.put("domainName", domainName);
                userProb.put("userName", userName);
                return userProb;
            }
            catch (final Exception ex) {}
        }
        if (domainName == null && userName == null) {
            final Long loginID = DMUserHandler.getDefaultAdministratorRoleUserList().get(0).get("LOGIN_ID");
            final Map dcUser = DMUserHandler.getLoginDetails(loginID);
            domainName = ((dcUser.get("DOMAINNAME") == null) ? "-" : dcUser.get("DOMAINNAME"));
            userName = dcUser.get("NAME");
            userProb.put("domainName", domainName);
            userProb.put("userName", userName);
        }
        return userProb;
    }
    
    public URLConnection createSummaryServerConnection(final Properties apiProperties) throws Exception {
        URLConnection urlConnection = null;
        if (apiProperties != null) {
            final String apiUrl = apiProperties.getProperty("url");
            final String contentType = apiProperties.getProperty("content-type");
            final String accept = apiProperties.getProperty("accept");
            final String methodType = apiProperties.getProperty("requestMethod");
            final String baseUrl = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerBaseURL();
            if (baseUrl != null) {
                final URL url = new URL(baseUrl + apiUrl);
                urlConnection = this.createSummaryServerConnection(url, methodType, contentType, accept, true, true);
            }
        }
        return urlConnection;
    }
    
    public URLConnection createSummaryServerConnection(final URL url, final String methodType, final String contentType, final String accept, final boolean doOutput, final boolean doInput) throws Exception {
        HttpURLConnection conn = null;
        if (url != null) {
            conn = HttpsHandlerUtil.getServerUrlConnection(url.toString());
            if (conn.getURL().toString().contains("https")) {
                conn = HttpsHandlerUtil.skipCertificateCheck(conn);
            }
            final Map headerParams = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerAPIKeyDetails();
            final String summaryApiKey = headerParams.get("summaryServerAuthKey");
            final String probeID = headerParams.get("probeId");
            conn.setRequestProperty("SummaryAuthorization", summaryApiKey);
            conn.setRequestProperty("probeId", probeID);
            conn.setRequestProperty("probeName", new ProbeDetailsUtil().getProbeName(Long.valueOf(probeID)));
            conn.setRequestProperty("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
            conn.setRequestProperty("summaryServerRequest", "true");
            conn.setRequestProperty("userDomain", this.encryptUserDomain(summaryApiKey));
            conn.setDoOutput(doOutput);
            conn.setDoInput(doInput);
            if (methodType != null) {
                conn.setRequestMethod(methodType);
            }
            if (contentType != null) {
                conn.setRequestProperty("content-type", contentType);
            }
            if (accept != null) {
                conn.setRequestProperty("accept", accept);
            }
        }
        return conn;
    }
    
    private String encryptUserDomain(final String summaryApiKey) {
        final Map<String, String> userProb = this.getUserDomainDetails(false);
        final String domainName = userProb.get("domainName");
        final String userName = userProb.get("userName");
        final String encryptedStr = ApiFactoryProvider.getCryptoAPI().encrypt(userName + "::" + domainName, summaryApiKey, (String)null);
        return encryptedStr;
    }
    
    public String pushMultiPartToSummaryServer(String apiUrl, final String methodType, final Map<String, Object> headersMap, final Map<String, Object> parametersMap, final Map<String, UploadedFileItem> multiFileObj) {
        String response = null;
        try {
            if (apiUrl != null && methodType != null && headersMap != null) {
                final String baseUrl = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerBaseURL();
                apiUrl = (apiUrl.startsWith("/") ? apiUrl.replaceFirst("/", "") : apiUrl);
                final URL postUrl = new URL(baseUrl + apiUrl);
                final CloseableHttpClient httpclient = HttpClients.createDefault();
                final String contentType = headersMap.get("content-type");
                final MultipartEntityBuilder mb = MultipartEntityBuilder.create();
                mb.setBoundary(contentType.substring(contentType.indexOf("=") + 1));
                for (final String paramName : parametersMap.keySet()) {
                    mb.addTextBody(paramName, String.valueOf(parametersMap.get(paramName)));
                }
                for (final UploadedFileItem file : multiFileObj.values()) {
                    mb.addBinaryBody(file.getFieldName(), file.getUploadedFile(), ContentType.DEFAULT_BINARY, file.getFileName());
                }
                final HttpEntity e = mb.build();
                RequestBuilder requestBuilder = null;
                if (methodType != null && methodType.equalsIgnoreCase("POST")) {
                    requestBuilder = RequestBuilder.post(String.valueOf(postUrl));
                }
                else if (methodType != null && methodType.equalsIgnoreCase("PUT")) {
                    requestBuilder = RequestBuilder.put(String.valueOf(postUrl));
                }
                final Map headerParams = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerAPIKeyDetails();
                final String summaryApiKey = headerParams.get("summaryServerAuthKey");
                requestBuilder.setHeader("SummaryAuthorization", summaryApiKey);
                requestBuilder.setHeader("probeId", (String)headerParams.get("probeId"));
                requestBuilder.setHeader("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
                requestBuilder.setHeader("summaryServerRequest", "true");
                requestBuilder.setHeader("userDomain", this.encryptUserDomain(summaryApiKey));
                final String accept = headersMap.get("accept");
                if (accept != null) {
                    requestBuilder.setHeader("accept", accept);
                }
                requestBuilder.setEntity(e);
                final HttpUriRequest multipartRequest = requestBuilder.build();
                final HttpResponse httpresponse = (HttpResponse)httpclient.execute(multipartRequest);
                response = EntityUtils.toString(httpresponse.getEntity());
                ProbeSyncImpl.logger.log(Level.INFO, response);
                ProbeSyncImpl.logger.log(Level.INFO, httpresponse.getStatusLine().toString());
            }
        }
        catch (final Exception e2) {
            ProbeSyncImpl.logger.log(Level.SEVERE, "Exception in pushMultiPartToSummaryServer: ", e2);
        }
        return response;
    }
}
