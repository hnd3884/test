package com.me.devicemanagement.framework.server.util;

import javax.net.ssl.HttpsURLConnection;
import java.util.Properties;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.KeyStoreException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import java.util.logging.Logger;

public class CreatorDataPost
{
    private static String sourceClass;
    private static Logger logger;
    private static CreatorDataPost creatorDataPost;
    private static SSLSocketFactory sslSocketFactory;
    private static final char[] CERTIFICATE_DEFAULT_PASSWORD;
    private static final String COMM_SW_KEYSTORE_FILE = "cacerts";
    public static String creator_post_url;
    public static String creator_auth_token;
    public static String creator_owner_name;
    public static String creator_scope;
    public static String creator_application_name;
    public static String creator_form_name;
    private static String resultData;
    public static List<String> xml_field_list;
    public static List<List> xml_filed_values;
    private static int returnCode;
    private static int invalidAuthToken;
    private static int invalidFormName;
    private static int emptyFieldList;
    private static int emptyFieldValues;
    private static int fieldValuesCountMismatch;
    private static int createException;
    private static int postException;
    private static String errorCode;
    private static String fieldID;
    
    public static String getResultData() {
        return CreatorDataPost.resultData;
    }
    
    public static CreatorDataPost getInstance() {
        if (CreatorDataPost.creatorDataPost == null) {
            CreatorDataPost.creatorDataPost = new CreatorDataPost();
        }
        return CreatorDataPost.creatorDataPost;
    }
    
    public static void setXmlFieldList(final ArrayList<String> xmlFieldList) {
        CreatorDataPost.xml_field_list = xmlFieldList;
    }
    
    public static void xmlFiledValues(final List<List> xmlFieldValues) {
        CreatorDataPost.xml_filed_values = xmlFieldValues;
    }
    
    public static void createSSLSocketFactory() {
        final String sourceMethod = "createSSLSocketFactory";
        try {
            final String certificatePath = System.getProperty("server.home") + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(certificatePath), CreatorDataPost.CERTIFICATE_DEFAULT_PASSWORD);
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, CreatorDataPost.CERTIFICATE_DEFAULT_PASSWORD);
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            final TrustManager[] tm = tmf.getTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tm, null);
            CreatorDataPost.sslSocketFactory = sslContext.getSocketFactory();
        }
        catch (final KeyStoreException e) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "KeyStoreException Occurred : ", e);
        }
        catch (final IOException e2) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "IOException Occurred : ", e2);
        }
        catch (final NoSuchAlgorithmException e3) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "NoSuchAlgorithmException Occurred : ", e3);
        }
        catch (final CertificateException e4) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "CertificateException Occurred : ", e4);
        }
        catch (final UnrecoverableKeyException e5) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "UnrecoverableKeyException Occurred : ", e5);
        }
        catch (final KeyManagementException e6) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "KeyManagementException Occurred : ", e6);
        }
        catch (final Exception e7) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Exception Occurred : ", e7);
        }
    }
    
    public static SSLSocketFactory getSSLSocketFactory() {
        return CreatorDataPost.sslSocketFactory;
    }
    
    public static int getReturnCode() {
        return CreatorDataPost.returnCode;
    }
    
    public static String getErrorCode() {
        return CreatorDataPost.errorCode;
    }
    
    public static String getFieldID() {
        return CreatorDataPost.fieldID;
    }
    
    public synchronized int submitCreatorData() {
        final String sourceMethod = "submitCreatorData";
        try {
            this.setReturnCode();
            if (CreatorDataPost.returnCode == 0) {
                this.postXMLContent(this.constructXMLContent());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Exception Occurred when submitting creator data : ", e);
        }
        return getReturnCode();
    }
    
    private StringBuffer constructXMLContent() {
        final String sourceMethod = "generateXMLContent";
        StringBuffer xmlContent = null;
        try {
            xmlContent = new StringBuffer();
            xmlContent = xmlContent.append("authtoken=" + CreatorDataPost.creator_auth_token + "&zc_ownername=" + CreatorDataPost.creator_owner_name + "&scope=" + CreatorDataPost.creator_scope + "&XMLString=");
            xmlContent = xmlContent.append("<ZohoCreator><applicationlist><application name='" + CreatorDataPost.creator_application_name + "'><formlist><form name='" + CreatorDataPost.creator_form_name + "'>");
            for (int b = 0; b < CreatorDataPost.xml_filed_values.size(); ++b) {
                final ArrayList values = CreatorDataPost.xml_filed_values.get(b);
                xmlContent = xmlContent.append("<add>");
                for (int a = 0; a < CreatorDataPost.xml_field_list.size(); ++a) {
                    xmlContent = xmlContent.append("<field name='" + CreatorDataPost.xml_field_list.get(a) + "'><value><![CDATA[" + URLEncoder.encode(values.get(a), "UTF-8") + "]]></value></field>");
                }
                xmlContent = xmlContent.append("</add>");
            }
            xmlContent = xmlContent.append("</form></formlist></application></applicationlist></ZohoCreator>");
        }
        catch (final Exception e) {
            CreatorDataPost.returnCode = CreatorDataPost.postException;
            CreatorDataPost.errorCode = "Exception occurred while generating xml contents : " + e.getMessage();
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Exception Occurred : ", e);
        }
        SyMLogger.debug(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "XML Content generated with given data is : " + (Object)xmlContent);
        return xmlContent;
    }
    
    private void setReturnCode() {
        if (CreatorDataPost.creator_auth_token == null || CreatorDataPost.creator_auth_token.equals("")) {
            CreatorDataPost.returnCode = CreatorDataPost.invalidAuthToken;
            CreatorDataPost.errorCode = "The auth code is either null or empty.. Please specify an authcode for posting data..";
        }
        else if (CreatorDataPost.creator_form_name == null || CreatorDataPost.creator_form_name.equals("")) {
            CreatorDataPost.returnCode = CreatorDataPost.invalidFormName;
            CreatorDataPost.errorCode = "The form name is either null or empty.. Please specify a formname for posting data..";
        }
        else if (CreatorDataPost.xml_field_list.isEmpty()) {
            CreatorDataPost.returnCode = CreatorDataPost.emptyFieldList;
            CreatorDataPost.errorCode = "The field list specified is empty.. Please add the fields mentioned in the creator form, for posting data..";
        }
        else if (CreatorDataPost.xml_filed_values.size() == 0) {
            CreatorDataPost.returnCode = CreatorDataPost.emptyFieldValues;
            CreatorDataPost.errorCode = "The field values specified is empty.. Please give the field values for the fields in creator form, for posting data..";
        }
        else if (CreatorDataPost.xml_field_list.size() != CreatorDataPost.xml_filed_values.get(0).size()) {
            CreatorDataPost.returnCode = CreatorDataPost.fieldValuesCountMismatch;
            CreatorDataPost.errorCode = "The field counts and field values count are not same.. Please specify the correct values";
        }
    }
    
    private void postXMLContent(final StringBuffer content) {
        final String sourceMethod = "postXMLContent";
        DataOutputStream wr = null;
        BufferedReader rd = null;
        try {
            final URL httpUrl = new URL(CreatorDataPost.creator_post_url);
            final Properties proxyProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            final String proxyDefined = SyMUtil.getSyMParameter("proxy_defined");
            HttpsURLConnection urlConn = null;
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(CreatorDataPost.creator_post_url, proxyProps);
                urlConn = ApiFactoryProvider.getUtilAccessAPI().getCreatorConnection(httpUrl, null, true, true, 30000, proxyDefined, pacProps);
            }
            else {
                urlConn = ApiFactoryProvider.getUtilAccessAPI().getCreatorConnection(httpUrl, null, true, true, 30000, proxyDefined, proxyProps);
            }
            urlConn.connect();
            wr = new DataOutputStream(urlConn.getOutputStream());
            wr.writeBytes(content.toString());
            wr.flush();
            final int respCode = urlConn.getResponseCode();
            if (respCode != 200) {
                rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String line = null;
                while (rd.readLine() != null) {
                    line = rd.readLine();
                    SyMLogger.info(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Response received : " + line);
                    final String result = line.substring(line.lastIndexOf("<status>") + 8, line.lastIndexOf("</status>"));
                    if ("success".equalsIgnoreCase(result)) {
                        CreatorDataPost.returnCode = respCode;
                    }
                    else {
                        SyMLogger.info(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Data Post failed : " + line);
                    }
                }
            }
            else {
                rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                CreatorDataPost.resultData = rd.readLine();
                SyMLogger.info(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Response Code received : " + respCode);
                CreatorDataPost.returnCode = respCode;
            }
        }
        catch (final IOException e) {
            CreatorDataPost.returnCode = CreatorDataPost.createException;
            CreatorDataPost.errorCode = "IOException occurred while posting xml contents : " + e.getMessage();
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "IOException Occurred : ", e);
        }
        catch (final Exception e2) {
            CreatorDataPost.returnCode = CreatorDataPost.createException;
            CreatorDataPost.errorCode = "Exception occurred while posting xml contents : " + e2.getMessage();
            SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Exception Occurred : ", e2);
        }
        finally {
            try {
                wr.close();
                if (rd != null) {
                    rd.close();
                }
            }
            catch (final IOException e3) {
                SyMLogger.error(CreatorDataPost.logger, CreatorDataPost.sourceClass, sourceMethod, "Exception Occurred while closing streams : ", e3);
            }
        }
    }
    
    public synchronized void resetFields() {
        CreatorDataPost.creator_post_url = "https://creator.zoho.com/api/xml/write";
        CreatorDataPost.creator_auth_token = "";
        CreatorDataPost.creator_owner_name = "desktopcentral1";
        CreatorDataPost.creator_scope = "creatorapi";
        CreatorDataPost.creator_application_name = "desktop-central-request-support-form";
        CreatorDataPost.creator_form_name = "";
        this.resetCodes();
    }
    
    public synchronized void resetCodes() {
        CreatorDataPost.returnCode = 0;
        CreatorDataPost.errorCode = "";
        CreatorDataPost.fieldID = "";
    }
    
    static {
        CreatorDataPost.sourceClass = "CreatorDataPost";
        CreatorDataPost.logger = Logger.getLogger(CreatorDataPost.class.getName());
        CreatorDataPost.creatorDataPost = null;
        CreatorDataPost.sslSocketFactory = null;
        CERTIFICATE_DEFAULT_PASSWORD = "changeit".toCharArray();
        CreatorDataPost.creator_post_url = "https://creator.zoho.com/api/xml/write";
        CreatorDataPost.creator_auth_token = "";
        CreatorDataPost.creator_owner_name = "desktopcentral1";
        CreatorDataPost.creator_scope = "creatorapi";
        CreatorDataPost.creator_application_name = "desktop-central-request-support-form";
        CreatorDataPost.creator_form_name = "";
        CreatorDataPost.resultData = null;
        CreatorDataPost.xml_field_list = new ArrayList<String>();
        CreatorDataPost.xml_filed_values = new ArrayList<List>();
        CreatorDataPost.returnCode = 0;
        CreatorDataPost.invalidAuthToken = 1;
        CreatorDataPost.invalidFormName = 2;
        CreatorDataPost.emptyFieldList = 3;
        CreatorDataPost.emptyFieldValues = 4;
        CreatorDataPost.fieldValuesCountMismatch = 5;
        CreatorDataPost.createException = 6;
        CreatorDataPost.postException = 7;
        CreatorDataPost.errorCode = "";
        CreatorDataPost.fieldID = "";
    }
}
