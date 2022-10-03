package com.me.devicemanagement.framework.server.downloadmgr;

import java.util.Hashtable;
import java.util.Iterator;
import javax.net.ssl.SSLSocketFactory;
import java.util.Enumeration;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.FileOutputStream;
import HTTPClient.CookieModule;
import HTTPClient.NVPair;
import java.util.List;
import HTTPClient.HTTPConnection;
import javax.net.ssl.SSLHandshakeException;
import java.net.URL;
import java.util.Arrays;
import HTTPClient.HTTPResponse;
import java.io.InputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import HTTPClient.ProtocolNotSuppException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

public class DownloadManager
{
    private static final Logger logger;
    public static DownloadManager dwnmgr;
    private Properties proxydetails;
    private int connectionTimeOut;
    private boolean isClosedNetwork;
    public static int proxyType;
    private ArrayList<String> enforceHttpsList;
    private ArrayList<String> defaultSSLValidationDomainList;
    public static final int DOMAIN_ACCESS_FAILED = 2;
    public static final int DOMAIN_ACCESS_SUCCESS = 1;
    public static final int DOMAIN_ACCESS_PROGRESS = 3;
    public static final int DOMAIN_ACCESS_READY = 4;
    
    public DownloadManager() {
        this.proxydetails = null;
        this.connectionTimeOut = 180000;
        this.enforceHttpsList = new ArrayList<String>();
        this.defaultSSLValidationDomainList = new ArrayList<String>();
    }
    
    public static DownloadManager getInstance() {
        if (DownloadManager.dwnmgr == null) {
            try {
                (DownloadManager.dwnmgr = new DownloadManager()).initiateProxy();
                DownloadManager.dwnmgr.setDownloadTimeout();
                DownloadManager.dwnmgr.setNetworkType();
                setProxyType();
            }
            catch (final Exception ex) {
                DownloadManager.logger.log(Level.SEVERE, null, ex);
            }
        }
        return DownloadManager.dwnmgr;
    }
    
    public void setProxyConfiguration(final Properties proxyProp) {
        this.proxydetails = proxyProp;
    }
    
    private void initiateProxy() throws Exception {
        this.proxydetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
    }
    
    private void setDownloadTimeout() {
        final String timeout = SyMUtil.getSyMParameter("PATCH_DLOAD_TIME_OUT");
        DownloadManager.logger.log(Level.INFO, "In setDownloadTimeout method {0}", this.connectionTimeOut);
        if (timeout != null && !timeout.equalsIgnoreCase("")) {
            this.connectionTimeOut = Integer.parseInt(timeout) * 60000;
        }
        DownloadManager.logger.log(Level.INFO, "Out setDownloadTimeout method {0}", this.connectionTimeOut);
    }
    
    public DownloadStatus downloadFile(final String sourceFile, final String destinationFile, final SSLValidationType... validationTypes) {
        return this.downloadFile(sourceFile, destinationFile, null, null, false, validationTypes);
    }
    
    public DownloadStatus downloadFile(final String sourceFile, final String destinationFile, final Properties formdata, final SSLValidationType... validationTypes) {
        return this.downloadFile(sourceFile, destinationFile, formdata, null, false, validationTypes);
    }
    
    public DownloadStatus downloadFile(final String sourceFile, final String destinationFile, final Properties formdata, final boolean isLocal, final SSLValidationType... validationTypes) {
        return this.downloadFile(sourceFile, destinationFile, formdata, null, false, isLocal, validationTypes);
    }
    
    public DownloadStatus downloadFile(final String sourceFile, final String destinationFile, final Properties formdata, final Properties headers, final SSLValidationType... validationTypes) {
        return this.downloadFile(sourceFile, destinationFile, formdata, headers, false, validationTypes);
    }
    
    public DownloadStatus downloadFile(final String sourceFile, final String destinationFile, final Properties postformdata, final boolean isLocal, final Properties headers, final SSLValidationType... validationTypes) {
        return this.downloadFile(sourceFile, destinationFile, postformdata, headers, false, isLocal, validationTypes);
    }
    
    private DownloadStatus downloadFile(final String sourceFile, final String destinationFile, final Properties postformdata, final Properties headers, final boolean isBinary, final SSLValidationType... validationTypes) {
        return this.downloadFile(sourceFile, destinationFile, postformdata, headers, isBinary, false, validationTypes);
    }
    
    public DownloadStatus downloadBinaryFile(final String sourceFile, final String destinationFile, final String checkSum, final SSLValidationType... validationTypes) {
        final DownloadStatus downloadStatus = this.downloadFile(sourceFile, destinationFile, null, null, false, validationTypes);
        try {
            if (downloadStatus.getStatus() == 0 && checkSum != null) {
                final boolean checkSumSuccess = ChecksumProvider.getInstance().ValidateFileCheckSum(destinationFile, checkSum);
                if (!checkSumSuccess) {
                    downloadStatus.setStatus(10009);
                    downloadStatus.setErrorMessage(I18N.getMsg("dc.server.dloadmanager.Check_sumvalidationfailed", new Object[0]));
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(destinationFile);
                }
            }
        }
        catch (final Exception e) {
            DownloadManager.logger.log(Level.INFO, "Exception while downloading " + this.trimURLForLog(sourceFile), e);
        }
        return downloadStatus;
    }
    
    public DownloadStatus downloadBinaryFile(final String sourceFile, final String destinationFile, final String checkSum, final String checksumType, final SSLValidationType... validationTypes) {
        final DownloadStatus downloadStatus = this.downloadFile(sourceFile, destinationFile, null, null, false, validationTypes);
        try {
            if (downloadStatus.getStatus() == 0 && checkSum != null) {
                final boolean checkSumSuccess = ChecksumProvider.getInstance().ValidateFileCheckSum(destinationFile, checkSum, checksumType);
                if (!checkSumSuccess) {
                    downloadStatus.setStatus(10009);
                    downloadStatus.setErrorMessage(I18N.getMsg("dc.server.dloadmanager.Check_sumvalidationfailed", new Object[0]));
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(destinationFile);
                }
            }
        }
        catch (final Exception e) {
            DownloadManager.logger.log(Level.INFO, "Exception while downloading " + this.trimURLForLog(sourceFile), e);
        }
        return downloadStatus;
    }
    
    public DownloadStatus downloadFileWithCheckSumValidation(final String sourceFile, final String destinationFile, final String checkSum, final String checksumType, final Properties formData, final Properties headers, final SSLValidationType... validationTypes) {
        final DownloadStatus downloadStatus = this.downloadFile(sourceFile, destinationFile, formData, (boolean)Boolean.TRUE, headers, validationTypes);
        try {
            if (downloadStatus.getStatus() == 0 && checkSum != null) {
                final boolean checkSumSuccess = ChecksumProvider.getInstance().ValidateFileCheckSum(destinationFile, checkSum, checksumType, Boolean.TRUE);
                if (!checkSumSuccess) {
                    downloadStatus.setStatus(10009);
                    downloadStatus.setErrorMessage(I18N.getMsg("dc.server.dloadmanager.Check_sumvalidationfailed", new Object[0]));
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(destinationFile, Boolean.TRUE);
                }
            }
        }
        catch (final Exception e) {
            DownloadManager.logger.log(Level.INFO, "Exception while downloading " + this.trimURLForLog(sourceFile), e);
        }
        return downloadStatus;
    }
    
    public DownloadStatus DownloadBinaryFile(final String sourceFile, final String destinationFile, final SSLValidationType... validationTypes) {
        return this.downloadFile(sourceFile, destinationFile, null, null, true, validationTypes);
    }
    
    public DownloadStatus GetFileDataBuffer(final String sourceFile, final Properties postformdata, final SSLValidationType... validationTypes) {
        return this.downloadData(sourceFile, postformdata, validationTypes);
    }
    
    private DownloadStatus downloadFile(String sourceFile, final String destinationFile, final Properties postformdata, Properties headers, final boolean isBinary, final boolean isLocal, final SSLValidationType... validationTypes) {
        InputStream in = null;
        boolean continueDownload = true;
        HTTPResponse response = null;
        final DownloadStatus status = new DownloadStatus();
        status.dwnloadstatus = 10008;
        try {
            DownloadManager.logger.log(Level.INFO, "Going to establish connecton for " + this.trimURLForLog(sourceFile));
            String userAgent = null;
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS() && ApiFactoryProvider.getEPMPatchUtilAPI().getUserAgent() != null) {
                userAgent = ApiFactoryProvider.getEPMPatchUtilAPI().getUserAgent();
            }
            if (userAgent == null) {
                userAgent = System.getProperty("http.agent");
                if (userAgent == null) {
                    userAgent = "ManageEngine Endpoint Central";
                }
            }
            if (headers == null) {
                headers = new Properties();
            }
            if (!headers.containsKey("User-Agent")) {
                ((Hashtable<String, String>)headers).put("User-Agent", userAgent);
            }
            response = this.getHttpResponse(sourceFile, postformdata, this.proxydetails, headers, status, validationTypes);
            if (response != null) {
                final int httpstatus = response.getStatusCode();
                if (httpstatus == 304) {
                    status.dwnloadstatus = 10010;
                    DownloadManager.logger.log(Level.INFO, "The requested file : {0} was not modified, so skipping the download.", this.trimURLForLog(destinationFile));
                }
                else if (httpstatus >= 200 && httpstatus < 400) {
                    if (isBinary) {
                        final String rspContentType = response.getHeader("Content-Type");
                        if (rspContentType != null && rspContentType.indexOf("text/") != -1) {
                            continueDownload = false;
                        }
                    }
                    if (continueDownload) {
                        in = response.getInputStream();
                        try {
                            if (in != null) {
                                DownloadManager.logger.log(Level.INFO, "Reading the stream of the url ...{0}", this.trimURLForLog(sourceFile));
                                int loopCounter = 0;
                                while (in.available() < 1 && loopCounter < 6) {
                                    if (response.getHeader("Content-Length") != null && response.getHeaderAsInt("Content-Length") > 0) {
                                        break;
                                    }
                                    ++loopCounter;
                                    Thread.sleep(10000L);
                                }
                            }
                        }
                        catch (final InterruptedException iex) {
                            DownloadManager.logger.log(Level.INFO, "The Patch Download thread in sleep exception : {0}", iex.getMessage());
                        }
                        if (in != null && in.available() > 0) {
                            if (destinationFile != null) {
                                this.writeFile(in, destinationFile, isLocal);
                                status.dwnloadstatus = 0;
                                status.lastModifiedTime = response.getHeader("Last-Modified");
                            }
                        }
                        else if (response.getHeaderAsInt("Content-Length") > 0) {
                            if (destinationFile != null) {
                                this.writeFile(in, destinationFile, isLocal);
                                status.dwnloadstatus = 0;
                                status.lastModifiedTime = response.getHeader("Last-Modified");
                            }
                        }
                        else {
                            DownloadManager.logger.log(Level.INFO, "The in stream is empty for the url {0}", sourceFile);
                            status.errMessage = I18N.getMsg("dc.server.dloadmanager.The_in_stream_is_empty", new Object[] { sourceFile });
                            status.errMsgKey = "dc.server.dloadmanager.The_in_stream_is_empty";
                            status.errMsgArgs = sourceFile;
                        }
                    }
                }
                else if (httpstatus == 403) {
                    status.dwnloadstatus = httpstatus;
                    status.errMessage = I18N.getMsg("dc.server.dloadmanager.The_request_is_forbiddenHttp_Status", new Object[0]);
                    status.errMsgKey = "dc.server.dloadmanager.The_request_is_forbiddenHttp_Status";
                }
                else {
                    status.dwnloadstatus = httpstatus;
                    DownloadManager.logger.log(Level.INFO, "The http status code returned from web server : {0}", new Integer(httpstatus));
                    status.errMessage = I18N.getMsg("dc.server.dloadmanager.The_http_status_code_returned", new Object[] { new Integer(httpstatus) });
                    status.errMsgKey = "dc.server.dloadmanager.The_http_status_code_returned";
                    status.errMsgArgs = new Integer(httpstatus);
                }
            }
            else {
                sourceFile = this.trimURLForLog(sourceFile);
                DownloadManager.logger.log(Level.INFO, "The received response is null for the source url : {0}", sourceFile);
                status.errMessage = I18N.getMsg("dc.server.dloadmanager.The_received_response_is_null", new Object[] { sourceFile });
                status.errMsgKey = "dc.server.dloadmanager.The_received_response_is_null";
                status.errMsgArgs = sourceFile;
            }
        }
        catch (final ProtocolNotSuppException pnse) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(sourceFile), pnse });
            status.errMessage = pnse.getMessage();
            status.errMsgKey = pnse.getMessage();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final FileNotFoundException fexp) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while writing to the file : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), fexp });
            DownloadManager.logger.log(Level.SEVERE, null, fexp);
            status.errMessage = fexp.getMessage();
            status.errMsgKey = fexp.getMessage();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final EOFException ex) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), ex });
            status.errMessage = ex.toString();
            status.errMsgKey = ex.toString();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final ConnectException ex2) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), ex2 });
            status.errMessage = ex2.toString();
            status.errMsgKey = ex2.toString();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final SocketTimeoutException ex3) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), ex3 });
            status.errMessage = ex3.toString();
            status.errMsgKey = ex3.toString();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final SocketException ex4) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), ex4 });
            status.errMessage = ex4.toString();
            status.errMsgKey = ex4.toString();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final UnknownHostException ex5) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), ex5 });
            status.errMessage = ex5.toString();
            status.errMsgKey = ex5.toString();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final ProtocolException ex6) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), ex6 });
            status.errMessage = ex6.toString();
            status.errMsgKey = ex6.toString();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final IOException ex7) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading & writing : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), ex7 });
            status.errMessage = ex7.getMessage();
            status.errMsgKey = ex7.getMessage();
            status.errMsgArgs = "NO_I18N";
        }
        catch (final Exception exp) {
            DownloadManager.logger.log(Level.INFO, "Runtime Exception has been occurred while downloading : {0} : {1}", new Object[] { this.trimURLForLog(destinationFile), exp });
            status.errMessage = exp.getMessage();
            status.errMsgKey = exp.getMessage();
            status.errMsgArgs = "NO_I18N";
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex8) {
                DownloadManager.logger.log(Level.INFO, "Error while closing input streams {0}", ex8);
            }
        }
        return status;
    }
    
    private DownloadStatus downloadData(final String sourceFile, final Properties postformdata, final SSLValidationType... validationTypes) {
        InputStream in = null;
        HTTPResponse response = null;
        String responseBuffer = null;
        String errorMessage = "";
        String errorMessageKey = "";
        Object errorMessageArgs = null;
        int returnStatus = 10008;
        final DownloadStatus status = new DownloadStatus();
        try {
            response = this.getHttpResponse(sourceFile, postformdata, this.proxydetails, null, status, validationTypes);
            if (response != null) {
                final int httpstatus = response.getStatusCode();
                if (httpstatus >= 200 && httpstatus < 400) {
                    in = response.getInputStream();
                    try {
                        if (in != null) {
                            DownloadManager.logger.log(Level.INFO, "Reading the stream of the source url ...{0}", this.trimURLForLog(sourceFile));
                            int loopCounter = 0;
                            while (in.available() < 1 && loopCounter < 6) {
                                ++loopCounter;
                                Thread.sleep(10000L);
                            }
                        }
                    }
                    catch (final InterruptedException iex) {
                        DownloadManager.logger.log(Level.INFO, "The Patch Download thread in sleep exception : {0}", iex);
                    }
                    if (in != null && in.available() > 0) {
                        responseBuffer = new String(response.getData());
                        returnStatus = 0;
                    }
                    else {
                        DownloadManager.logger.log(Level.INFO, "The in stream is empty for the url {0}", this.trimURLForLog(sourceFile));
                    }
                }
                else {
                    DownloadManager.logger.log(Level.INFO, "The http status code returned from web server : {0}", new Integer(httpstatus));
                    errorMessage = I18N.getMsg("dc.server.dloadmanager.The_http_status_code_returned", new Object[] { new Integer(httpstatus) });
                    errorMessageKey = "dc.server.dloadmanager.The_http_status_code_returned";
                    errorMessageArgs = new Integer(httpstatus);
                }
            }
        }
        catch (final IOException ex) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while reading source file : {0} : {1}", new Object[] { this.trimURLForLog(sourceFile), ex });
            errorMessage = ex.getMessage();
            errorMessageKey = ex.getMessage();
            errorMessageArgs = "NO_I18N";
        }
        catch (final Exception exp) {
            DownloadManager.logger.log(Level.INFO, "Runtime Exception has been occurred while reading source file : {0} : {1}", new Object[] { this.trimURLForLog(sourceFile), exp });
            errorMessage = exp.getMessage();
            errorMessageKey = exp.getMessage();
            errorMessageArgs = "NO_I18N";
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex2) {
                DownloadManager.logger.log(Level.INFO, "Error occurred while closing input streams {0}", ex2);
            }
        }
        status.dwnloadstatus = returnStatus;
        status.errMessage = errorMessage;
        status.errMsgKey = errorMessageKey;
        status.errMsgArgs = errorMessageArgs;
        status.setUrlDataBuffer(responseBuffer);
        return status;
    }
    
    private HTTPResponse getHttpResponse(final String sourceFile, final Properties postformdata, final Properties proxydetails, final Properties headers, final DownloadStatus status, final SSLValidationType... validationTypes) throws Exception {
        HTTPResponse response = null;
        HTTPConnection connection = null;
        if (sourceFile != null) {
            String theUrl = sourceFile;
            final List<SSLValidationType> sslValidationTypes = (validationTypes == null) ? new ArrayList<SSLValidationType>() : ((validationTypes.length == 0) ? this.getDownloadManagerDefaultSSLValidations(sourceFile) : Arrays.asList(validationTypes));
            if (status != null) {
                status.sslValidationStatus = (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION) ? 0 : 100);
            }
            if (sslValidationTypes.contains(SSLValidationType.ENFORCE_HTTPS)) {
                theUrl = (new URL(theUrl).getProtocol().equalsIgnoreCase("http") ? sourceFile.replaceFirst("http", "https") : sourceFile);
            }
            final URL url = new URL(theUrl);
            connection = this.getConnection(theUrl, proxydetails, sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION));
            connection.setTimeout(this.connectionTimeOut);
            NVPair[] postdataNVPair = null;
            NVPair[] headersNVPair = null;
            if (postformdata != null) {
                postdataNVPair = this.getPostFormData(postformdata);
            }
            if (headers != null) {
                headersNVPair = this.getPostFormData(headers);
            }
            try {
                response = connection.Get(url.getFile(), postdataNVPair, headersNVPair);
                DownloadManager.logger.log(Level.WARNING, "Response for URL " + this.trimURLForLog(url.toString()) + " is : " + response.toString());
                if (status != null) {
                    if (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION)) {
                        DownloadManager.logger.log(Level.INFO, "Connection established with SSL Validation");
                        status.sslValidationStatus = 200;
                    }
                    else {
                        DownloadManager.logger.log(Level.INFO, "Connection established without SSL Validation");
                    }
                }
            }
            catch (final SSLHandshakeException sslHandshakeException) {
                if (status != null && sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION)) {
                    status.sslValidationStatus = 525;
                    status.sslValidationException = sslHandshakeException.getMessage();
                }
                if (!sslValidationTypes.contains(SSLValidationType.RETRY_ACTUAL_WITHOUT_VALIDATION)) {
                    throw sslHandshakeException;
                }
                DownloadManager.logger.log(Level.WARNING, "Download failed, Going to retry again one more time without ssl validation." + this.trimURLForLog(sourceFile));
                connection = this.getConnection(sourceFile, proxydetails, false);
                connection.setTimeout(this.connectionTimeOut);
                response = connection.Get(url.getFile(), postdataNVPair, headersNVPair);
            }
        }
        return response;
    }
    
    public DownloadStatus getURLResponseWithoutCookie(final String connectionUrl, final String data, final SSLValidationType... validationTypes) throws Exception {
        HTTPResponse response = null;
        HTTPConnection connection = null;
        final DownloadStatus downloadStatus = new DownloadStatus();
        int httpstatus = 10008;
        try {
            if (connectionUrl != null) {
                String theUrl = connectionUrl;
                final List<SSLValidationType> sslValidationTypes = (validationTypes == null) ? new ArrayList<SSLValidationType>() : ((validationTypes.length == 0) ? this.getDownloadManagerDefaultSSLValidations(connectionUrl) : Arrays.asList(validationTypes));
                if (sslValidationTypes.contains(SSLValidationType.ENFORCE_HTTPS)) {
                    theUrl = (new URL(theUrl).getProtocol().equalsIgnoreCase("http") ? connectionUrl.replaceFirst("http", "https") : connectionUrl);
                }
                connection = this.getConnection(theUrl, this.proxydetails, sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION));
                connection.setTimeout(this.connectionTimeOut);
                connection.removeModule((Class)CookieModule.class);
                final NVPair[] headersNVPair = { null };
                final NVPair nvJsonHeader = new NVPair("Content-type", "application/json");
                headersNVPair[0] = nvJsonHeader;
                downloadStatus.sslValidationStatus = (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION) ? 0 : 100);
                try {
                    response = connection.Post(theUrl, data, headersNVPair);
                    if (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION)) {
                        DownloadManager.logger.log(Level.INFO, "Connection established with SSL Validation");
                        downloadStatus.sslValidationStatus = 200;
                    }
                    else {
                        DownloadManager.logger.log(Level.INFO, "Connection established without SSL Validation");
                    }
                }
                catch (final SSLHandshakeException sslHandshakeException) {
                    if (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION)) {
                        downloadStatus.sslValidationStatus = 525;
                        downloadStatus.sslValidationException = sslHandshakeException.getMessage();
                    }
                    if (!sslValidationTypes.contains(SSLValidationType.RETRY_ACTUAL_WITHOUT_VALIDATION)) {
                        throw sslHandshakeException;
                    }
                    DownloadManager.logger.log(Level.WARNING, "Download failed, Going to retry again one more time without ssl validation." + this.trimURLForLog(connectionUrl));
                    connection = this.getConnection(connectionUrl, this.proxydetails, false);
                    connection.setTimeout(this.connectionTimeOut);
                    response = connection.Post(theUrl, data, headersNVPair);
                }
                httpstatus = response.getStatusCode();
                if (httpstatus == 200) {
                    final String strOutput = new String(response.getData(), "UTF-8");
                    downloadStatus.setUrlDataBuffer(strOutput);
                    downloadStatus.setStatus(0);
                }
                else if (httpstatus == 503) {
                    final String header = response.getHeader("Retry-After");
                    downloadStatus.setErrorMessage(header);
                    downloadStatus.setStatus(10011);
                }
                else {
                    downloadStatus.setStatus(10008);
                }
            }
        }
        catch (final Exception ex) {
            DownloadManager.logger.log(Level.SEVERE, "Exception caught while sending request to  Server : {0}", ex);
            downloadStatus.setErrorMessage(ex.getMessage());
            downloadStatus.setStatus(10008);
        }
        return downloadStatus;
    }
    
    public DownloadStatus getUrlResponse(final String url, final Properties headers, final Properties parameters, final SSLValidationType... validationTypes) throws Exception {
        HTTPResponse response = null;
        HTTPConnection connection = null;
        final DownloadStatus downloadStatus = new DownloadStatus();
        if (url != null) {
            String theUrl = url;
            final List<SSLValidationType> sslValidationTypes = (validationTypes == null) ? new ArrayList<SSLValidationType>() : ((validationTypes.length == 0) ? this.getDownloadManagerDefaultSSLValidations(url) : Arrays.asList(validationTypes));
            downloadStatus.sslValidationStatus = (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION) ? 0 : 100);
            if (sslValidationTypes.contains(SSLValidationType.ENFORCE_HTTPS)) {
                theUrl = (new URL(theUrl).getProtocol().equalsIgnoreCase("http") ? url.replaceFirst("http", "https") : url);
            }
            connection = this.getConnection(theUrl, this.proxydetails, sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION));
            connection.setTimeout(this.connectionTimeOut);
            connection.removeModule((Class)CookieModule.class);
            try {
                response = connection.Get(theUrl, this.getPostFormData(parameters), this.getPostFormData(headers));
                if (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION)) {
                    DownloadManager.logger.log(Level.INFO, "Connection established with SSL Validation");
                    downloadStatus.sslValidationStatus = 200;
                }
                else {
                    DownloadManager.logger.log(Level.INFO, "Connection established without SSL Validation");
                }
            }
            catch (final SSLHandshakeException sslHandshakeException) {
                if (sslValidationTypes.contains(SSLValidationType.DEFAULT_SSL_VALIDATION)) {
                    downloadStatus.sslValidationStatus = 525;
                    downloadStatus.sslValidationException = sslHandshakeException.getMessage();
                }
                if (!sslValidationTypes.contains(SSLValidationType.RETRY_ACTUAL_WITHOUT_VALIDATION)) {
                    throw sslHandshakeException;
                }
                DownloadManager.logger.log(Level.WARNING, "Download failed, Going to retry again one more time without ssl validation." + this.trimURLForLog(url));
                connection = this.getConnection(url, this.proxydetails, false);
                connection.setTimeout(this.connectionTimeOut);
                response = connection.Get(url, this.getPostFormData(parameters), this.getPostFormData(headers));
            }
            final int httpStatus = response.getStatusCode();
            if (httpStatus == 200) {
                final String responseStr = new String(response.getData(), "UTF-8");
                downloadStatus.setStatus(0);
                downloadStatus.setUrlDataBuffer(responseStr);
            }
            else {
                downloadStatus.setStatus(10008);
                downloadStatus.setErrorMessage("HTTP Status Code : " + httpStatus);
            }
        }
        return downloadStatus;
    }
    
    private void writeFile(final InputStream in, final String destinationFile, final boolean isLocal) throws IOException, FileNotFoundException, Exception {
        FileOutputStream outFile = null;
        try {
            if (destinationFile != null) {
                if (!isLocal) {
                    ApiFactoryProvider.getFileAccessAPI().writeFile(destinationFile, in);
                }
                else {
                    outFile = (FileOutputStream)FileAccessUtil.writeFile(destinationFile);
                    final byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        outFile.write(buf, 0, len);
                    }
                    DownloadManager.logger.log(Level.INFO, "Writing completed for the file : {0}", destinationFile.replace("\\", "\\\\"));
                }
            }
        }
        finally {
            try {
                if (outFile != null) {
                    outFile.close();
                }
            }
            catch (final Exception ex) {
                DownloadManager.logger.log(Level.INFO, "Error occurred while closing output stream  {0}", ex);
            }
        }
    }
    
    private String readDataBufferFromStream(final InputStream in) throws IOException {
        String responseBuffer = "";
        final StringBuilder sbuffer = new StringBuilder();
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            sbuffer.append(buf);
        }
        responseBuffer = sbuffer.toString();
        return responseBuffer;
    }
    
    private NVPair[] getPostFormData(final Properties formdata) {
        NVPair[] post_form_data = null;
        if (formdata != null) {
            final int size = formdata.size();
            if (size > 0) {
                int position = 0;
                post_form_data = new NVPair[size];
                final Enumeration iterate = formdata.keys();
                while (iterate.hasMoreElements()) {
                    final String key = iterate.nextElement();
                    final String value = ((Hashtable<K, String>)formdata).get(key);
                    post_form_data[position] = new NVPair(key, value);
                    ++position;
                }
            }
        }
        return post_form_data;
    }
    
    private void setNetworkType() {
        this.isClosedNetwork = Boolean.parseBoolean(SyMUtil.getSyMParameter("is-closed-netowrk"));
    }
    
    public void setNetworkType(final boolean isClosed) {
        this.isClosedNetwork = isClosed;
    }
    
    public boolean isClosedNetwork() {
        return this.isClosedNetwork;
    }
    
    private static void setProxyType() {
        final String proxy_type = SyMUtil.getSyMParameter("proxyType");
        if (proxy_type != null) {
            try {
                DownloadManager.proxyType = Integer.parseInt(proxy_type);
            }
            catch (final Exception ex) {
                DownloadManager.proxyType = 0;
                DownloadManager.logger.log(Level.INFO, "Exception while setting the proxy Type : " + ex);
            }
        }
    }
    
    public void setProxyType(final int proxytype) {
        DownloadManager.proxyType = proxytype;
    }
    
    public DownloadStatus doPost(final String connectionUrl, final String data) throws Exception {
        HTTPResponse response = null;
        HTTPConnection connection = null;
        final DownloadStatus downloadStatus = new DownloadStatus();
        downloadStatus.dwnloadstatus = 10005;
        if (connectionUrl != null) {
            final SSLUtil sslutil = SSLUtil.getInstance();
            connection = this.getConnection(connectionUrl, this.proxydetails, false);
            connection.setTimeout(this.connectionTimeOut);
            final NVPair[] headersNVPair = { null };
            final NVPair nvJsonHeader = new NVPair("Content-type", "application/json");
            headersNVPair[0] = nvJsonHeader;
            response = connection.Post(connectionUrl, data, headersNVPair);
            downloadStatus.setStatus(0);
            downloadStatus.setUrlDataBuffer(response.getText());
        }
        return downloadStatus;
    }
    
    public List domainURLValidator(final List domainList, final SSLValidationType... validationTypes) {
        final List validatedDomainList = new ArrayList();
        for (int i = 0; i < domainList.size(); ++i) {
            final Properties domainDetails = domainList.get(i);
            final String urlDomain = domainDetails.getProperty("URLDOMAIN");
            final boolean downloadStatus = this.downloadURLValidator(urlDomain, validationTypes);
            Integer status = 2;
            if (downloadStatus) {
                status = 1;
            }
            ((Hashtable<String, String>)domainDetails).put("STATUS", String.valueOf(status));
            validatedDomainList.add(domainDetails);
        }
        return validatedDomainList;
    }
    
    public boolean downloadURLValidator(final String urlDomain, final SSLValidationType... validationTypes) {
        Boolean status = Boolean.FALSE;
        try {
            String userAgent = System.getProperty("http.agent");
            if (userAgent == null) {
                userAgent = "ManageEngine Endpoint Central";
            }
            final Properties headers = new Properties();
            ((Hashtable<String, String>)headers).put("User-Agent", userAgent);
            final HTTPResponse response = this.getHttpResponse(urlDomain, null, this.proxydetails, headers, null, validationTypes);
            if (response != null) {
                final int httpstatus = response.getStatusCode();
                if (httpstatus == 404) {
                    status = Boolean.TRUE;
                    DownloadManager.logger.log(Level.INFO, "Domain validation failed with 404 error, Domain URL : " + this.trimURLForLog(urlDomain));
                }
                else if (httpstatus >= 200 && httpstatus < 400) {
                    status = Boolean.TRUE;
                    DownloadManager.logger.log(Level.INFO, "Domain validation success, Domain URL : " + this.trimURLForLog(urlDomain));
                }
                else {
                    DownloadManager.logger.log(Level.INFO, "Domain validation failed, Domain URL : " + this.trimURLForLog(urlDomain) + ", failed status code : " + httpstatus);
                }
            }
        }
        catch (final Exception pnse) {
            DownloadManager.logger.log(Level.INFO, "Error occurred while validating download URL : " + this.trimURLForLog(urlDomain));
            pnse.printStackTrace();
        }
        return status;
    }
    
    private String trimURLForLog(String url) {
        if (url != null && url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }
    
    private HTTPConnection getConnection(final String connectionUrl, final Properties proxy, final boolean validate) throws Exception {
        return validate ? SSLUtil.getInstance().getConnection(connectionUrl, proxy, (SSLSocketFactory)SSLSocketFactory.getDefault()) : SSLUtil.getInstance().getConnection(connectionUrl, proxy);
    }
    
    private List<SSLValidationType> getDownloadManagerDefaultSSLValidations(final String theUrl) {
        final List<SSLValidationType> sslValidationTypes = new ArrayList<SSLValidationType>();
        try {
            final URL url = new URL(theUrl);
            final String requestDomain = url.getHost();
            final String requestProtocol = url.getProtocol();
            for (final String domainName : this.enforceHttpsList) {
                if (domainName.equalsIgnoreCase(requestDomain) && requestProtocol.equalsIgnoreCase("http")) {
                    sslValidationTypes.add(SSLValidationType.ENFORCE_HTTPS);
                }
            }
            for (final String domainName : this.defaultSSLValidationDomainList) {
                if (domainName.equalsIgnoreCase(requestDomain)) {
                    sslValidationTypes.add(SSLValidationType.DEFAULT_SSL_VALIDATION);
                }
            }
        }
        catch (final Exception e) {
            DownloadManager.logger.log(Level.WARNING, "Exception occurred while determine DownloadManager default SSL validations - " + this.trimURLForLog(theUrl), e);
        }
        return sslValidationTypes;
    }
    
    public void addEnforceHttpsDomainList(final String... domains) throws IllegalArgumentException {
        for (final String domainName : domains) {
            if (domainName == null || domainName.trim().length() <= 0) {
                throw new IllegalArgumentException("Domain name cannot be null/empty.");
            }
            this.enforceHttpsList.add(domainName);
        }
    }
    
    public void addDefaultSSLValidationDomainList(final String... domains) throws IllegalArgumentException {
        for (final String domainName : domains) {
            if (domainName == null || domainName.trim().length() <= 0) {
                throw new IllegalArgumentException("Domain name cannot be null/empty.");
            }
            this.defaultSSLValidationDomainList.add(domainName);
        }
    }
    
    static {
        logger = Logger.getLogger("DownloadManager");
        DownloadManager.dwnmgr = null;
        DownloadManager.proxyType = 0;
    }
}
