package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.adcs;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordRequestStatus;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.io.IOException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.util.logging.Logger;

public class AdcsResponseHandler
{
    private static final Logger LOGGER;
    
    private AdcsResponseHandler() {
    }
    
    public static PasswordResponse getPasswordResponse(final long resource, final CloseableHttpResponse response) throws IOException {
        final int responseStatusCode = response.getStatusLine().getStatusCode();
        AdcsResponseHandler.LOGGER.log(Level.INFO, "AdcsPasswordGetter: Password request status code is {0} for resource {1}", new Object[] { responseStatusCode, resource });
        PasswordResponse passwordResponse;
        if (responseStatusCode == 200) {
            passwordResponse = handleSuccessResponse(resource, response);
        }
        else if (responseStatusCode == 401) {
            passwordResponse = handleUnauthorizedResponse(resource);
        }
        else {
            passwordResponse = handleUnknownResponse(resource);
        }
        return passwordResponse;
    }
    
    public static PasswordResponse handleSuccessResponse(final long resource, final CloseableHttpResponse response) throws IOException {
        final byte[] httpResponseAsBytes = getHttpResponseAsBytes(response);
        final String adcsWebPage = decodeAdcsWebPage(httpResponseAsBytes);
        if (!MDMStringUtils.isEmpty(adcsWebPage)) {
            return getPasswordResponseFromWebPage(resource, adcsWebPage);
        }
        AdcsResponseHandler.LOGGER.log(Level.INFO, "AdcsPasswordGetter: Http Web page returned by Dynamic SCEP server is either empty or unable to decode using the standard charsets. {0}", new Object[] { resource });
        return new PasswordResponse(PasswordRequestStatus.UNKNOWN_ERROR, null);
    }
    
    private static String decodeAdcsWebPage(final byte[] httpResponseAsBytes) {
        final Charset[] adcsResponseCharsets = { StandardCharsets.UTF_16, StandardCharsets.UTF_8, StandardCharsets.US_ASCII, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.ISO_8859_1 };
        String httpWebPage = null;
        for (final Charset charset : adcsResponseCharsets) {
            httpWebPage = new String(httpResponseAsBytes, charset);
            if (httpWebPage.contains("The enrollment challenge password is")) {
                break;
            }
            httpWebPage = null;
        }
        return httpWebPage;
    }
    
    private static PasswordResponse getPasswordResponseFromWebPage(final long resource, final String adcsWebPage) {
        final String password = parsePasswordFromHtmlResponse(adcsWebPage);
        if (!MDMStringUtils.isEmpty(password)) {
            AdcsResponseHandler.LOGGER.log(Level.INFO, "AdcsPasswordGetter: Password received for resource - {0}", new Object[] { resource });
            return new PasswordResponse(PasswordRequestStatus.SUCCESS, password);
        }
        final PasswordRequestStatus passwordRequestStatus = getReasonForFailure(adcsWebPage);
        AdcsResponseHandler.LOGGER.log(Level.INFO, "AdcsPasswordGetter: No password received for resource - {0}, Reason: {1}", new Object[] { resource, passwordRequestStatus.getRemarkString() });
        return new PasswordResponse(passwordRequestStatus, null);
    }
    
    private static byte[] getHttpResponseAsBytes(final CloseableHttpResponse response) throws IOException {
        final InputStream inputStream = response.getEntity().getContent();
        return IOUtils.toByteArray(inputStream);
    }
    
    public static PasswordResponse handleUnauthorizedResponse(final long resource) {
        AdcsResponseHandler.LOGGER.log(Level.INFO, "AdcsPasswordGetter: Unauthorized code received for resource - {0}", new Object[] { resource });
        final PasswordRequestStatus passwordRequestStatus = PasswordRequestStatus.ADCS_UNAUTHORIZED;
        return new PasswordResponse(passwordRequestStatus, null);
    }
    
    public static PasswordResponse handleUnknownResponse(final long resource) {
        AdcsResponseHandler.LOGGER.log(Level.INFO, "AdcsPasswordGetter: Unknown error for resource - {0}", new Object[] { resource });
        final PasswordRequestStatus passwordRequestStatus = PasswordRequestStatus.UNKNOWN_ERROR;
        return new PasswordResponse(passwordRequestStatus, null);
    }
    
    public static PasswordRequestStatus getReasonForFailure(final String htmlResponse) {
        if (htmlResponse.contains("The password cache is full")) {
            return PasswordRequestStatus.PASSWORD_CACHE_LIMIT_REACHED;
        }
        return PasswordRequestStatus.UNKNOWN_ERROR;
    }
    
    public static String parsePasswordFromHtmlResponse(final String htmlResponse) {
        if (htmlResponse.contains("The enrollment challenge password is:")) {
            int end = -1;
            if (htmlResponse.contains("This password can be used only once")) {
                end = htmlResponse.indexOf(" </B> <P> This password can be used only once");
            }
            else if (htmlResponse.contains("This password can be used multiple times")) {
                end = htmlResponse.indexOf(" </B> <P> This password can be used multiple times and will not expire.");
            }
            return parsePassword(htmlResponse, end);
        }
        return null;
    }
    
    private static String parsePassword(final String htmlResponse, final int end) {
        final String searchString = "The enrollment challenge password is: <B> ";
        final int start = htmlResponse.indexOf(searchString);
        if (end != -1) {
            return htmlResponse.substring(start + searchString.length(), end);
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
