package com.adventnet.tools.prevalent;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.io.OutputStreamWriter;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.net.ConnectException;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class GetSalesLink
{
    private static Logger logger;
    private Properties licProperties;
    private int errorCode;
    
    public GetSalesLink() {
        this.licProperties = null;
        this.errorCode = 0;
        this.loadProperties();
    }
    
    private void loadProperties() {
        try {
            final File fileName = new File(LUtil.getDir() + File.separator + "lib" + File.separator + "lic.properties");
            if (fileName.exists()) {
                (this.licProperties = new Properties()).load(new FileInputStream(fileName));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getSalesLink() {
        try {
            if (this.allowRenewalLinkGeneration()) {
                final StringBuffer queryParams = new StringBuffer();
                queryParams.append("ic=false&");
                queryParams.append("reqstr=" + DatatypeConverter.printBase64Binary(this.getInputData().getBytes("UTF-8")));
                final String url = this.licProperties.getProperty("quote_store_link") + "?" + queryParams.toString();
                GetSalesLink.logger.info("url -> " + url);
                String response = this.secureConnect(url, new Properties(), "GET", null).trim();
                GetSalesLink.logger.info("url -> " + url);
                if (response.contains("\"result\":\"success\"")) {
                    response = response.substring(0, response.lastIndexOf("}"));
                    response = response + ",\"subscriptiontype\":\"" + this.getSubscriptionType() + "\"}";
                    return response;
                }
            }
        }
        catch (final ConnectException ce) {
            this.errorCode = 2004;
            ce.printStackTrace();
        }
        catch (final Exception e) {
            this.errorCode = 2005;
            e.printStackTrace();
        }
        return this.getErrorResponse();
    }
    
    private String getErrorResponse() {
        return "{\"result\":\"failed\",\"subscriptiontype\":\"" + this.getSubscriptionType() + "\",\"errorcode\":" + this.errorCode + "}";
    }
    
    private String getSubscriptionType() {
        if (Wield.getInstance().getEvaluationExpiryDate().equals("never")) {
            return "AMS";
        }
        return "AS";
    }
    
    private String secureConnect(final String reqUrl, final Properties headers, final String method, final Object payLoad) throws Exception {
        final URL url = new URL(reqUrl);
        final HttpsURLConnection urlConn = (HttpsURLConnection)url.openConnection();
        urlConn.setRequestMethod(method);
        urlConn.setAllowUserInteraction(false);
        urlConn.setDoOutput(true);
        for (final String headerName : headers.stringPropertyNames()) {
            urlConn.setRequestProperty(headerName, headers.getProperty(headerName));
        }
        if (payLoad != null) {
            final OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
            try {
                wr.write(payLoad.toString());
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            finally {
                wr.flush();
            }
        }
        if (urlConn.getResponseCode() == 200) {
            return this.readFromStream(urlConn.getInputStream()).toString();
        }
        GetSalesLink.logger.info("Error while making request -- " + (Object)this.readFromStream(urlConn.getErrorStream()));
        return null;
    }
    
    private StringBuffer readFromStream(final InputStream ins) {
        BufferedReader in = null;
        StringBuffer sb = null;
        try {
            if (ins != null) {
                sb = new StringBuffer();
                in = new BufferedReader(new InputStreamReader(ins));
                String line = null;
                while ((line = in.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex) {}
        }
        return sb;
    }
    
    private String getInputData() {
        final StringBuffer retData = new StringBuffer();
        retData.append("{'id':" + this.getLicenseID() + ",");
        final String[] arr$;
        final String[] moduleArr = arr$ = this.licProperties.getProperty("module_to_pass").split(",");
        for (final String module : arr$) {
            final Properties moduleProp = Wield.getInstance().getModuleProperties(module);
            for (final String propertie : moduleProp.stringPropertyNames()) {
                retData.append("'" + propertie + "':'" + moduleProp.getProperty(propertie) + "',");
            }
        }
        retData.append("'email':'" + Wield.getInstance().getModuleProperties("LicenseDetails").getProperty("PrimaryContact") + "',");
        retData.append("'product':'" + Wield.getInstance().getProductName() + "',");
        retData.append("'expiry':'" + Wield.getInstance().getUserType() + "'}");
        return retData.toString();
    }
    
    public boolean allowRenewalLinkGeneration() throws Exception {
        final long daysDiffToAllowLinkGeneration = Integer.parseInt(this.licProperties.getProperty("days_before_quote_generation_allowed"));
        long numDaysToLicenseExpiry = 0L;
        if (!Wield.getInstance().getEvaluationExpiryDate().equals("never")) {
            numDaysToLicenseExpiry = Wield.getInstance().getEvaluationDays();
        }
        else {
            final String amsExpiry = Wield.getInstance().getAMSExpiry();
            if (amsExpiry != null) {
                numDaysToLicenseExpiry = this.getDifferenceInDate(amsExpiry);
            }
        }
        if (Wield.getInstance().getUserType().equals("T") || Wield.getInstance().getUserType().equals("F")) {
            this.errorCode = 2000;
            return false;
        }
        if (this.getLicenseID() <= 0L) {
            this.errorCode = 2001;
            return false;
        }
        if (!this.isAllowedCustomUserType(Wield.getInstance().getCustomUserType())) {
            this.errorCode = 2002;
            return false;
        }
        if (numDaysToLicenseExpiry > daysDiffToAllowLinkGeneration) {
            this.errorCode = 2003;
            return false;
        }
        return true;
    }
    
    private long getLicenseID() {
        try {
            if (Wield.getInstance().getModuleProperties("LicenseDetails") != null) {
                return Long.parseLong(Wield.getInstance().getModuleProperties("LicenseDetails").getProperty("LicenseID"));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }
    
    private boolean isAllowedCustomUserType(final String userType) {
        final String[] arr$;
        final String[] exludeUserTypes = arr$ = this.licProperties.getProperty("exlude_user_type").toString().split(",");
        for (final String excludeUserType : arr$) {
            if (excludeUserType.equalsIgnoreCase(userType)) {
                return false;
            }
        }
        return true;
    }
    
    public long getDifferenceInDate(final String expiryDate) throws Exception {
        long difference = 0L;
        try {
            final long reminder = this.dateInLong(expiryDate, "yyyy-MM-dd");
            difference = reminder - System.currentTimeMillis();
            return difference / 86400000L;
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new Exception("not able to find diffin days");
        }
    }
    
    public long dateInLong(final String st, final String format) {
        long dateInLong = 0L;
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            final Date mailDate = formatter.parse(st);
            dateInLong = mailDate.getTime();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateInLong;
    }
    
    static {
        GetSalesLink.logger = Logger.getLogger(GetSalesLink.class.getName());
    }
}
