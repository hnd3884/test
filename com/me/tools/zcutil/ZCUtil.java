package com.me.tools.zcutil;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.TimeZone;
import java.util.Calendar;
import java.io.FileInputStream;
import java.util.Date;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.adventnet.tools.update.UpdateManagerUtil;
import com.adventnet.tools.update.installer.UpdateManager;
import java.util.Map;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.io.DataOutputStream;
import java.net.Authenticator;
import javax.net.ssl.HttpsURLConnection;
import sun.misc.BASE64Encoder;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.tools.prevalent.Wield;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class ZCUtil
{
    private static Logger logger;
    private String zFile;
    private SimpleDateFormat dateFormat;
    private Properties confProp;
    private ProductConf prodConf;
    private Wield wield;
    private LicenseReader lr;
    private String oldInstalaltionID;
    private String oldProduct;
    
    public ZCUtil() {
        this.zFile = null;
        this.dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        this.confProp = null;
        this.prodConf = null;
        this.wield = null;
        this.lr = null;
        this.oldInstalaltionID = null;
        this.oldProduct = null;
        this.zFile = METrack.getConfDir() + File.separator + "ZohoCreator.properties";
        final ConfFileReader cfr = new ConfFileReader();
        String productName = null;
        if (METrack.getLicenseDir() != null) {
            this.lr = new LicenseReader(METrack.getLicenseDir() + File.separator + "AdventNetLicense.xml");
            productName = this.lr.getProductNode().getAttribute("Name");
        }
        else {
            this.wield = Wield.getInstance();
            if (this.wield.getProductName() == null) {
                ZCUtil.logger.log(Level.INFO, "License not validated , Going to validate now - ");
                this.wield.validateInvoke("Validation", false);
            }
            productName = this.wield.getProductName();
        }
        if (METrack.getProductName() != null) {
            productName = METrack.getProductName();
        }
        this.prodConf = cfr.getFormConfiguaration();
        this.confProp = cfr.getConfProps(productName, "zcdata.xml", this.prodConf);
    }
    
    public ZCUtil(final String productName) {
        this.zFile = null;
        this.dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        this.confProp = null;
        this.prodConf = null;
        this.wield = null;
        this.lr = null;
        this.oldInstalaltionID = null;
        this.oldProduct = null;
        final ConfFileReader cfr = new ConfFileReader();
        this.prodConf = cfr.getFormConfiguaration();
        this.confProp = cfr.getConfProps(productName, "zcdata.xml", this.prodConf);
    }
    
    public String getAppName() {
        return this.confProp.getProperty("appname");
    }
    
    public String getDataForm() {
        return this.confProp.getProperty("dataform");
    }
    
    public Properties getConfValue() {
        return this.confProp;
    }
    
    public ProductConf getProductConf() {
        return this.prodConf;
    }
    
    public boolean getConnectStatus() {
        try {
            if (METrack.getIsOd()) {
                return true;
            }
            final Properties cProp = this.loadPropertiesFile(this.zFile);
            final File f = new File(this.zFile);
            return !f.exists() || cProp.getProperty("enabled").equalsIgnoreCase("true");
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public void updateConnectionStatus(final String staus) {
        final Properties cProp = this.loadPropertiesFile(this.zFile);
        cProp.setProperty("enabled", staus);
        this.storeProperties(cProp, this.zFile);
    }
    
    public String addRecord(final String appName, final String formName, final Properties viewEntries, final Properties proxyDetails) {
        try {
            final ApplicationData appData = new ApplicationData();
            appData.addRecord(formName, viewEntries);
            return METrack.updateMultiFormdData(appData, proxyDetails);
        }
        catch (final Exception e) {
            e.printStackTrace();
            ZCUtil.logger.log(Level.INFO, "Exception in METrack addRecord : ", e.toString());
            return null;
        }
    }
    
    public Document viewRecord(final String viewName, final String criteria, final Properties proxyDetails) {
        Document doc = null;
        try {
            String viewRecordsURLStr = null;
            if (viewRecordsURLStr == null) {
                viewRecordsURLStr = this.confProp.getProperty("url") + "api/xml/" + this.confProp.getProperty("appname") + "/view/" + viewName + "?apikey=" + this.confProp.getProperty("key");
            }
            else {
                viewRecordsURLStr = this.confProp.getProperty("url") + "api/xml/" + this.confProp.getProperty("appname") + "/view/" + viewName + "/matchall/" + criteria + "?apikey=" + this.confProp.getProperty("key");
            }
            final Properties qstrProp = new Properties();
            qstrProp.setProperty("zc_ownername", this.confProp.getProperty("zowner"));
            final String response = this.connect(viewRecordsURLStr, qstrProp, proxyDetails).toString();
            if (response != null) {
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(response.getBytes()));
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception in METrack viewRecord : ", e.toString());
        }
        return doc;
    }
    
    public StringBuffer connect(final String url, final Properties quertString, final Properties proxyDetails) {
        return this.connect(url, quertString, proxyDetails, null);
    }
    
    public StringBuffer connect(final String url, final Properties quertString, final Properties proxyDetails, final String formate) {
        DataOutputStream wr = null;
        HttpsURLConnection httpsConn = null;
        try {
            String qStr = "";
            if (quertString != null) {
                final Enumeration en = quertString.keys();
                while (en.hasMoreElements()) {
                    String propsKey = en.nextElement();
                    String propsVal = quertString.getProperty(propsKey);
                    propsKey = propsKey.replaceAll("&", "_").replaceAll(" ", "_").replaceAll("-", "_");
                    if (formate != null && !formate.equals("XML")) {
                        propsVal = propsVal.replaceAll("/", "\\\\");
                    }
                    propsVal = propsVal.replaceAll("#", "");
                    qStr = qStr + "&" + URLEncoder.encode(propsKey, "UTF-8") + "=" + URLEncoder.encode(propsVal, "UTF-8");
                }
            }
            final URL httpUrl = new URL(url);
            if (this.isValidProxy(proxyDetails)) {
                final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyDetails.getProperty("host"), Integer.parseInt(proxyDetails.getProperty("port"))));
                final BASE64Encoder encoder = new BASE64Encoder();
                final String encodedUserPwd = encoder.encode((proxyDetails.getProperty("username") + ":" + proxyDetails.getProperty("password")).getBytes());
                httpsConn = (HttpsURLConnection)httpUrl.openConnection(proxy);
                httpsConn.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
                Authenticator.setDefault(new ProxyAuthenticator(proxyDetails.getProperty("username"), proxyDetails.getProperty("password")));
            }
            else {
                httpsConn = (HttpsURLConnection)httpUrl.openConnection();
            }
            httpsConn.setConnectTimeout(30000);
            httpsConn.setRequestProperty("Cookie", "metrack=true");
            httpsConn.setDoInput(true);
            httpsConn.setDoOutput(true);
            httpsConn.setUseCaches(false);
            httpsConn.setRequestMethod("POST");
            httpsConn.connect();
            wr = new DataOutputStream(httpsConn.getOutputStream());
            wr.writeBytes(qStr);
            if (url.contains("/baseform/add?") || url.contains("/inputform/add?")) {
                this.updateLastPosted(System.currentTimeMillis());
            }
            return this.readFromStream(httpsConn.getInputStream());
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while invoking METrack : " + e.toString());
            return null;
        }
        finally {
            try {
                if (wr != null) {
                    wr.flush();
                    wr.close();
                }
                if (httpsConn != null) {
                    httpsConn.disconnect();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    public StringBuffer readFromStream(final InputStream ins) {
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
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception fe) {
                ZCUtil.logger.log(Level.INFO, "Exception while readFromStream: " + fe.toString());
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception fe2) {
                ZCUtil.logger.log(Level.INFO, "Exception while readFromStream: " + fe2.toString());
            }
        }
        return sb;
    }
    
    public String getZFile() {
        return this.zFile;
    }
    
    public Properties getCustomerDetails() {
        Object ram = null;
        final Properties cusDet = new Properties();
        if (this.lr != null && METrack.getLicenseDir() != null) {
            cusDet.putAll(this.getLicenseDetailFromLicenseReader());
        }
        else {
            cusDet.putAll(this.getLicenseDetailFromWield());
        }
        if (System.getProperty("os.name").startsWith("Win") || System.getProperty("os.name").startsWith("win")) {
            ram = this.getRamSize("WIN");
            if (ram != null) {
                cusDet.setProperty("Ram", Double.toString(Double.parseDouble(ram.toString()) / 1.073741824E9));
            }
        }
        else {
            ram = this.getRamSize("LIN");
            if (ram != null) {
                cusDet.setProperty("Ram", Double.toString(Double.parseDouble(ram.toString()) / 1.073741824E9));
            }
        }
        final String osDetail = System.getProperty("os.name") + " - " + System.getProperty("os.version") + " - " + System.getProperty("os.arch") + " Architecture";
        cusDet.setProperty("OS", osDetail.replaceAll(",", " "));
        if (System.getProperty("sun.os.patch.level") != null) {
            cusDet.setProperty("OS_SP", System.getProperty("sun.os.patch.level").replaceAll(",", " "));
        }
        if (METrack.loadUpdateManager() && UpdateManager.getAllServicePackVersions(METrack.getHomeDir()) != null) {
            final String[] arr = UpdateManager.getAllServicePackVersions(METrack.getHomeDir());
            cusDet.setProperty("Patch_Version", arr[arr.length - 1]);
            if (arr.length > 1) {
                cusDet.setProperty("Migration_Path", this.getMigPath(arr));
            }
        }
        UpdateManagerUtil.setHomeDirectory(METrack.getHomeDir());
        if (new File("conf" + File.separator + "update_conf.xml").exists() && UpdateManager.getSubProductName("conf") != null) {
            cusDet.setProperty("Context", UpdateManager.getSubProductName("conf"));
        }
        else if (new File(METrack.getConfDir() + File.separator + "update_conf.xml").exists() && UpdateManager.getSubProductName(new File(METrack.getHomeDir()).toURI().relativize(new File(METrack.getConfDir()).toURI()).getPath()) != null) {
            cusDet.setProperty("Context", UpdateManager.getSubProductName(new File(METrack.getHomeDir()).toURI().relativize(new File(METrack.getConfDir()).toURI()).getPath()));
        }
        if (this.getTimeZone() != null) {
            cusDet.setProperty("timezone_val", this.getTimeZone().replaceAll("/", "_").replaceAll("-", "_"));
        }
        cusDet.setProperty("Database", this.getInstallationDB());
        final String buildNum = this.getBuildNumber();
        if (buildNum != null) {
            cusDet.setProperty("buildnumber", buildNum);
        }
        final String[] excludeArr = this.prodConf.getBaseFormExcludeFileds();
        if (excludeArr != null) {
            for (int i = 0; i < excludeArr.length; ++i) {
                cusDet.remove(excludeArr[i].toString());
            }
        }
        cusDet.setProperty("Metrack", this.confProp.getProperty("Metrack"));
        if (METrack.getAdditionalBaseFormDetails() != null && METrack.getAdditionalBaseFormDetails().size() > 0) {
            cusDet.putAll(METrack.getAdditionalBaseFormDetails());
        }
        return cusDet;
    }
    
    private String getBuildNumber() {
        try {
            if (this.confProp.getProperty("build_file_type") != null) {
                if (this.confProp.getProperty("build_file_type").equals("properties")) {
                    final File prdConf = new File(METrack.getConfDir() + File.separator + this.confProp.getProperty("buildnumber"));
                    if (prdConf.exists()) {
                        final Properties prdConfProp = this.loadPropertiesFile(prdConf.getAbsolutePath());
                        return prdConfProp.getProperty(this.confProp.getProperty("buildnumber_name"));
                    }
                }
                else if (this.confProp.getProperty("build_file_type").equals("xml")) {
                    final Element buildElement = this.loadBuildXml(METrack.getConfDir() + File.separator + this.confProp.getProperty("buildnumber"), this.confProp.getProperty("build_tag"));
                    return buildElement.getAttribute(this.confProp.getProperty("build_attr"));
                }
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while getBuildNumber: " + e.toString());
        }
        return null;
    }
    
    private Element loadBuildXml(final String fileName, final String tagName) {
        final InputStream ins = null;
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = dbFactory.newDocumentBuilder();
            final Document xmlDoc = builder.parse(new File(fileName));
            return (Element)xmlDoc.getElementsByTagName(tagName).item(0);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (ins != null) {
                    ins.close();
                }
            }
            catch (final Exception ex) {}
        }
        return null;
    }
    
    private String getMigPath(final String[] arr) {
        String ret = "";
        for (int i = 0; i < arr.length; ++i) {
            ret += arr[i].toString().substring(arr[i].toString().indexOf("-") + 1, arr[i].toString().length());
            if (i < arr.length - 1) {
                ret += " &gt;&gt; ";
            }
        }
        return ret;
    }
    
    public Object getRamSize(final String osType) {
        try {
            final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
            final Class[] c = new Class[0];
            Method method = null;
            if (osType.equals("WIN")) {
                method = operatingSystemMXBean.getClass().getDeclaredMethod("getTotalSwapSpaceSize", (Class<?>[])c);
            }
            else {
                method = operatingSystemMXBean.getClass().getDeclaredMethod("getTotalPhysicalMemorySize", (Class<?>[])c);
            }
            method.setAccessible(true);
            final Object value = method.invoke(operatingSystemMXBean, new Object[0]);
            return value;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getInstallationMail() {
        return null;
    }
    
    private String getUserType(final String uType) {
        if (uType.equals("T")) {
            return "Trial";
        }
        if (uType.equals("R")) {
            return "Registered";
        }
        if (uType.equals("F")) {
            return "Free";
        }
        return "";
    }
    
    public String getCriteria(final Properties customerDetails) {
        String criteriaStr = null;
        try {
            criteriaStr = "";
            final Enumeration en = customerDetails.propertyNames();
            while (en.hasMoreElements()) {
                final String propertyKey = en.nextElement();
                final String propertyValue = customerDetails.getProperty(propertyKey);
                criteriaStr = criteriaStr + URLEncoder.encode(propertyKey, "UTF-8") + "=" + URLEncoder.encode(propertyValue, "UTF-8") + ",";
            }
            criteriaStr = criteriaStr.substring(0, criteriaStr.length() - 1);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return criteriaStr;
    }
    
    public void storeProperties(final Properties prop, final String file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(file));
            prop.store(fos, "");
        }
        catch (final Exception e) {
            e.printStackTrace();
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception e) {
                ZCUtil.logger.log(Level.INFO, "Exception while storeProperties: " + e.toString());
            }
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception e2) {
                ZCUtil.logger.log(Level.INFO, "Exception while storeProperties: " + e2.toString());
            }
        }
    }
    
    public void storeCreatorRowId(final String cId) {
        Properties creatorProps = null;
        if (new File(this.zFile).exists()) {
            creatorProps = this.loadPropertiesFile(this.zFile);
        }
        else {
            creatorProps = new Properties();
        }
        creatorProps.setProperty("installedtime", this.getInstallationTime());
        if (cId != null) {
            creatorProps.setProperty("ID", cId);
            creatorProps.setProperty("lastupdated", Long.toString(System.currentTimeMillis()));
        }
        if (creatorProps.get("enabled") == null) {
            creatorProps.setProperty("enabled", "true");
        }
        this.storeProperties(creatorProps, this.zFile);
    }
    
    public Properties getActionLogDetails(final String action) {
        final Properties actionProps = new Properties();
        actionProps.setProperty("customerid", this.getCreatorId());
        if (action.equals("start")) {
            actionProps.setProperty("action_field", "startup");
        }
        else if (action.equals("stop")) {
            actionProps.setProperty("action_field", "shutdown");
        }
        actionProps.setProperty("timestamp", this.dateFormat.format(new Date()));
        return actionProps;
    }
    
    public Properties loadPropertiesFile(final String fileLocation) {
        FileInputStream fis = null;
        try {
            final File f = new File(fileLocation);
            if (f.exists()) {
                fis = new FileInputStream(f);
                final Properties cProp = new Properties();
                cProp.load(fis);
                return cProp;
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while loadPropertiesFile : " + e.toString() + " , " + fileLocation);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                ZCUtil.logger.log(Level.INFO, "Exception while loadPropertiesFile finally : " + e.toString());
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                ZCUtil.logger.log(Level.INFO, "Exception while loadPropertiesFile finally : " + e2.toString());
            }
        }
        return null;
    }
    
    public String getCreatorId() {
        try {
            final Properties cProp = this.loadPropertiesFile(this.zFile);
            return cProp.getProperty("ID");
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public boolean isNewCustomer() {
        final Properties cProp = this.loadPropertiesFile(this.zFile);
        if (cProp != null && cProp.getProperty("old_installation_id") != null) {
            this.oldInstalaltionID = cProp.getProperty("old_installation_id");
            this.oldProduct = cProp.getProperty("old_product");
        }
        return cProp == null || cProp.getProperty("ID") == null;
    }
    
    public String getTimeZone() {
        final Calendar cal = Calendar.getInstance();
        final TimeZone tz = cal.getTimeZone();
        return tz.getID() + " - " + tz.getDisplayName();
    }
    
    public Document getDocument(final String xmlData) {
        ByteArrayInputStream bais = null;
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            bais = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
            final Document doc = db.parse(new InputSource(bais));
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while invoking getDocument : " + e.toString());
            try {
                if (bais != null) {
                    bais.close();
                }
            }
            catch (final Exception ee) {
                ZCUtil.logger.log(Level.INFO, "Exception while invoking getDocument : " + ee.toString());
            }
        }
        finally {
            try {
                if (bais != null) {
                    bais.close();
                }
            }
            catch (final Exception ee2) {
                ZCUtil.logger.log(Level.INFO, "Exception while invoking getDocument : " + ee2.toString());
            }
        }
        return null;
    }
    
    public String addNewCustomer(final Properties prop, final String appName, final String formName, final Properties proxyProp) {
        String customerid = null;
        try {
            prop.setProperty("installedtime", this.getInstallationTime());
            final Properties finalProp = new AddorUpdateInstallation().getBaseData(this.getNewCustomerDetails(prop));
            final String response = this.addRecord(appName, formName, finalProp, proxyProp);
            final String addResult = this.getTagTextNode(response, "status");
            if (addResult != null && addResult.equalsIgnoreCase("Success")) {
                final Properties result = this.getDataProp(this.getDocument(response));
                this.storeCreatorRowId(result.getProperty("ID"));
                customerid = result.getProperty("ID");
                this.storeProperties(prop, METrack.getConfDir() + File.separator + "cdet.properties");
            }
            else {
                this.storeCreatorRowId(null);
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while adding new entry in METrack : ", e.toString());
            this.storeCreatorRowId(null);
        }
        return customerid;
    }
    
    private String createInstallationID(final String appName, final String formName, final Properties viewEntries, final Properties proxyDetails) {
        final ApplicationData appData = new ApplicationData(null);
        appData.addInstallation(formName, viewEntries);
        appData.addRecord(formName, viewEntries);
        return METrack.updateMultiFormdData(appData, proxyDetails);
    }
    
    public String addNewUser(final AddUser add, final String appName, final String formName, final Properties proxyProp) {
        String customerid = null;
        String response = null;
        try {
            response = this.createInstallationID(appName, formName, add.getUserProp(), proxyProp);
            final String addResult = this.getTagTextNode(response, "status");
            if (addResult != null && addResult.equalsIgnoreCase("Success")) {
                final Properties result = this.getDataProp(this.getDocument(response));
                customerid = result.getProperty("ID");
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.SEVERE, "Exception while adding new entry in METrack : " + e.toString(), response);
        }
        return customerid;
    }
    
    public Properties updateUserDetails(final Properties newDet, final Properties oldDet) {
        final Properties updateProp = new Properties();
        final Enumeration en = newDet.keys();
        while (en.hasMoreElements()) {
            final String propertyKey = en.nextElement();
            if (oldDet.getProperty(propertyKey) == null) {
                updateProp.setProperty(propertyKey, newDet.getProperty(propertyKey));
            }
            else {
                if (oldDet.getProperty(propertyKey).equals(newDet.getProperty(propertyKey))) {
                    continue;
                }
                updateProp.setProperty(propertyKey, newDet.getProperty(propertyKey));
            }
        }
        return updateProp;
    }
    
    public Properties getDataProp(final Document doc) {
        final Properties resultProp = new Properties();
        try {
            final Element el1 = doc.getDocumentElement();
            final NodeList nList0 = el1.getElementsByTagName("values");
            for (int i1 = 0; i1 < nList0.getLength(); ++i1) {
                final Properties dataProps = new Properties();
                final Node cnoTemp = nList0.item(i1);
                final NodeList nList2 = cnoTemp.getChildNodes();
                for (int j = 0; j < nList2.getLength(); ++j) {
                    final Node cno = nList2.item(j);
                    final NamedNodeMap nnm0 = cno.getAttributes();
                    final String propKey = nnm0.getNamedItem("name").getNodeValue();
                    String propValue = "";
                    final NodeList nl1 = cno.getChildNodes();
                    for (int k = 0; k < nl1.getLength(); ++k) {
                        final Node cno2 = nl1.item(k);
                        if (cno2.getNodeType() == 1) {
                            final NamedNodeMap nnm2 = cno2.getAttributes();
                            final NodeList nl2 = cno2.getChildNodes();
                            for (int j2 = 0; j2 < nl2.getLength(); ++j2) {
                                final Node cno3 = nl2.item(j2);
                                propValue = cno3.getNodeValue();
                            }
                        }
                    }
                    resultProp.setProperty(propKey, propValue);
                }
            }
        }
        catch (final Exception e2) {
            e2.printStackTrace();
        }
        return resultProp;
    }
    
    public String getTagTextNode(final String xmlData, final String tagName) {
        try {
            if (xmlData != null) {
                return this.getDocument(xmlData).getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getLicenseValue(final String query) {
        try {
            final String[] arr = query.split("\\+");
            if (arr.length <= 0) {
                return null;
            }
            Properties lProp = null;
            if (this.lr != null && METrack.getLicenseDir() != null) {
                lProp = this.lr.getModuleProperties(arr[0].toString());
            }
            else {
                lProp = this.wield.getModuleProperties(arr[0].toString());
            }
            if (arr.length == 1) {
                return "true";
            }
            return lProp.getProperty(arr[1].toString());
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public Properties parseDoc(final Document doc, final String criteria) {
        final LoadQuery lqry = new LoadQuery(doc.getDocumentElement());
        return lqry.getLoadQuery(criteria);
    }
    
    public Properties getNewCustomerDetails(final Properties prop) {
        final Properties retProp = new Properties();
        retProp.putAll(prop);
        final Properties installProp = this.getInstallProp();
        if (installProp != null && installProp.size() > 0) {
            retProp.putAll(installProp);
        }
        return retProp;
    }
    
    public Properties getInstallProp() {
        final Properties installProp = this.loadPropertiesFile(new File(METrack.getHomeDir() + File.separator + this.confProp.getProperty("installdatadirectory") + File.separator + this.confProp.getProperty("installdatafilename")).getAbsolutePath());
        Properties retProp = null;
        if (installProp != null && installProp.size() > 0) {
            retProp = new Properties();
            final Properties keyProp = new Properties();
            keyProp.setProperty("Country", "Installation_Country");
            keyProp.setProperty("did", "did");
            final Enumeration en = keyProp.keys();
            while (en.hasMoreElements()) {
                final String key = en.nextElement();
                if (installProp.getProperty(key) != null) {
                    retProp.setProperty(keyProp.getProperty(key), installProp.getProperty(key));
                }
            }
            try {
                if (installProp.getProperty("INSTALLATION_DATE") != null) {
                    final String installDate = this.changeDateFormat(this.getFormatedDate(installProp.getProperty("INSTALLATION_DATE")), "dd-MM-yyyy", "dd-MMM-yyyy");
                    if (installDate != null && installProp.getProperty("INSTALLATION_STARTED_TIME") != null && installProp.getProperty("INSTALLATION_ENDED_TIME") != null) {
                        final String startTimeStamp = installProp.getProperty("INSTALLATION_STARTED_TIME").trim();
                        final String endTimeStamp = installProp.getProperty("INSTALLATION_ENDED_TIME").trim();
                        final int startHour = Integer.parseInt(startTimeStamp.substring(0, startTimeStamp.indexOf(":")));
                        final int endHour = Integer.parseInt(endTimeStamp.substring(0, endTimeStamp.indexOf(":")));
                        retProp.setProperty("INSTALLATION_START", installDate + " " + startTimeStamp);
                        if (endHour < startHour) {
                            retProp.setProperty("INSTALLATION_END", increment_Decrement_Dates(installDate, 1L, "dd-MMM-yyyy") + " " + endTimeStamp);
                        }
                        else {
                            retProp.setProperty("INSTALLATION_END", installDate + " " + endTimeStamp);
                        }
                    }
                }
            }
            catch (final Exception e) {
                ZCUtil.logger.log(Level.INFO, "Exception while METrack getting installation hours : " + e.toString());
            }
            final String[] excludeArr = this.prodConf.getBaseFormExcludeFileds();
            if (excludeArr != null) {
                for (int i = 0; i < excludeArr.length; ++i) {
                    retProp.remove(excludeArr[i].toString());
                }
            }
        }
        return retProp;
    }
    
    private String getFormatedDate(final String date) {
        final String[] arr = date.split("-");
        return this.appendZero(Integer.parseInt(arr[1])) + "-" + this.appendZero(Integer.parseInt(arr[0])) + "-" + this.appendZero(Integer.parseInt(arr[2]));
    }
    
    private String appendZero(final int num) {
        if (num < 10) {
            return "0" + num;
        }
        return Integer.toString(num);
    }
    
    public static String increment_Decrement_Dates(final String dateString, final long no_of_days, final String formate) {
        final SimpleDateFormat f1 = new SimpleDateFormat(formate);
        Date dte = null;
        try {
            dte = f1.parse(dateString);
        }
        catch (final Exception exx) {
            exx.printStackTrace();
            ZCUtil.logger.log(Level.INFO, "Exception while METrack getting increment_Decrement_Dates : " + exx.toString());
        }
        long timeInMills = dte.getTime();
        timeInMills += 86400000L * no_of_days;
        final Date dxt = new Date(timeInMills);
        return f1.format(dxt);
    }
    
    private String changeDateFormat(final String date, final String fromFormate, final String toFormate) {
        try {
            final Date dueDate = this.getDateFromString(date, fromFormate);
            final SimpleDateFormat sdf = new SimpleDateFormat(toFormate);
            return sdf.format(dueDate).toString();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private Date getDateFromString(final String dateStr, final String formatStr) {
        Date dt = null;
        try {
            final SimpleDateFormat simpledateformat = new SimpleDateFormat(formatStr);
            dt = simpledateformat.parse(dateStr);
        }
        catch (final Exception exception) {
            ZCUtil.logger.log(Level.INFO, "Exception while METrack getDateFromString : " + exception.toString());
        }
        return dt;
    }
    
    public String getCurrentDateTime(final String timeZone) {
        try {
            final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timeZone));
            return dateFormatGmt.format(new Date());
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public void logStartTime() {
        try {
            final Properties cProp = this.loadPropertiesFile(this.zFile);
            final String cTime = this.getCurrentDateTime("GMT");
            if (cTime != null) {
                cProp.setProperty("starttime", cTime);
            }
            else {
                cProp.setProperty("starttime", "-NA-");
            }
            this.storeProperties(cProp, this.zFile);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getStartTime() {
        try {
            final Properties cProp = this.loadPropertiesFile(this.zFile);
            if (cProp.getProperty("starttime") != null) {
                return cProp.getProperty("starttime");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return "-NA-";
    }
    
    public String getInstallationDB() {
        int db = -1;
        String dbVendorName = null;
        try {
            if (METrack.getDB() == -1) {
                dbVendorName = new RunSelectQuery().getDBName();
                if (dbVendorName.toLowerCase().contains("microsoft")) {
                    db = 2;
                }
                else if (dbVendorName.toLowerCase().contains("mysql")) {
                    db = 1;
                }
                else if (dbVendorName.toLowerCase().contains("postgres")) {
                    db = 3;
                }
            }
            else {
                db = METrack.getDB();
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while METrack getInstallationDB : " + e.toString());
        }
        if (db >= 1) {
            return this.getDBVendor(db);
        }
        return dbVendorName;
    }
    
    public boolean isValidProxy(final Properties proxyProp) {
        try {
            return proxyProp != null && proxyProp.getProperty("host") != null && !((Hashtable<K, Object>)proxyProp).get("host").equals("") && !((Hashtable<K, Object>)proxyProp).get("host").equals(" ") && Integer.parseInt(proxyProp.getProperty("port")) >= 0;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public HashMap getQueries(final ArrayList<String> keyList, final Properties proxyProp) {
        HashMap qMap = null;
        try {
            qMap = new HashMap();
            final Document selectQueryDoc = this.viewRecord(this.confProp.getProperty("queryview"), null, proxyProp);
            for (final String str : keyList) {
                qMap.put(str, this.getQueryForKey(str, selectQueryDoc, this.confProp.getProperty("queryresult")));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return qMap;
    }
    
    private Properties getQueryForKey(final String key, final Document doc, final String criteria) {
        Properties qProp = null;
        try {
            if (doc != null) {
                qProp = new Properties();
                final Element el1 = doc.getDocumentElement();
                final NodeList nList0 = el1.getElementsByTagName("record");
                for (int i1 = 0; i1 < nList0.getLength(); ++i1) {
                    final Properties dataProps = new Properties();
                    final Node cnoTemp = nList0.item(i1);
                    final NodeList nList2 = cnoTemp.getChildNodes();
                    for (int j = 0; j < nList2.getLength(); ++j) {
                        final Node cno = nList2.item(j);
                        final NamedNodeMap nnm0 = cno.getAttributes();
                        final String propKey = nnm0.getNamedItem("name").getNodeValue();
                        String propValue = "";
                        final NodeList nl1 = cno.getChildNodes();
                        for (int k = 0; k < nl1.getLength(); ++k) {
                            final Node cno2 = nl1.item(k);
                            if (cno2.getNodeType() == 1) {
                                final NodeList nl2 = cno2.getChildNodes();
                                for (int j2 = 0; j2 < nl2.getLength(); ++j2) {
                                    final Node cno3 = nl2.item(j2);
                                    propValue = cno3.getNodeValue();
                                }
                            }
                        }
                        dataProps.setProperty(propKey, propValue);
                    }
                    if (dataProps.getProperty("Form_Name").equals(criteria) && dataProps.getProperty("Query_For").equals(key)) {
                        qProp.setProperty(dataProps.getProperty("columnname"), dataProps.getProperty("query"));
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return qProp;
    }
    
    public Properties getLicenseDetailFromLicenseReader() {
        final Properties cusDet = new Properties();
        final String userType = this.lr.getUserType();
        if (userType != null) {
            cusDet.setProperty("User_Type", userType);
        }
        if (this.lr.getProductNode().getAttribute("Type") != null) {
            cusDet.setProperty("License_Type", this.lr.getProductNode().getAttribute("Type"));
        }
        if (this.lr.getProductNode().getAttribute("Category") != null) {
            cusDet.setProperty("License_Category", this.lr.getProductNode().getAttribute("Category"));
        }
        if (this.lr.getProductNode().getAttribute("Name") != null) {
            cusDet.setProperty("Product", this.lr.getProductNode().getAttribute("Name"));
        }
        if (!userType.equals("Free") && this.lr.getEvaluationExpiryDate() != null) {
            cusDet.setProperty("Expiry_Date", this.lr.getEvaluationExpiryDate());
            if (this.isParsableDate(userType, this.lr)) {
                cusDet.setProperty("expirytime", String.valueOf(this.dateInLong(this.lr.getEvaluationExpiryDate(), "yyyy-MM-dd")));
            }
        }
        if (userType.equals("Registered")) {
            final Properties licenseModuleProps = this.lr.getModuleProperties("LicenseDetails");
            if (licenseModuleProps != null && licenseModuleProps.size() > 0) {
                if (licenseModuleProps.getProperty("LicenseID") != null) {
                    cusDet.setProperty("License_ID", "true");
                }
                if (licenseModuleProps.getProperty("CustomerID") != null) {
                    cusDet.setProperty("Customer_ID", "true");
                }
                if (licenseModuleProps.getProperty("licenseusertype") != null) {
                    cusDet.setProperty("User_Type", licenseModuleProps.getProperty("licenseusertype"));
                }
            }
        }
        return cusDet;
    }
    
    private boolean isParsableDate(final String userType, final LicenseReader lr) {
        return !userType.equals("Free") && !userType.equals("Evaluation") && !userType.equals("Trial") && lr.getEvaluationExpiryDate() != null && !"".equals(lr.getEvaluationExpiryDate().trim()) && !"never".equals(lr.getEvaluationExpiryDate().trim()) && !"unknown".equals(lr.getEvaluationExpiryDate().trim());
    }
    
    public Properties getLicenseDetailFromWield() {
        final Properties cusDet = new Properties();
        final String userType = this.getUserType(this.wield.getUserType());
        if (userType != null) {
            cusDet.setProperty("User_Type", userType);
        }
        if (this.wield.getLicenseTypeString() != null) {
            cusDet.setProperty("License_Type", this.wield.getLicenseTypeString());
        }
        if (this.wield.getProductCategoryString() != null) {
            cusDet.setProperty("License_Category", this.wield.getProductCategoryString());
        }
        if (this.wield.getProductName() != null) {
            cusDet.setProperty("Product", this.wield.getProductName());
        }
        if (!userType.equals("Free") && this.wield.getEvaluationExpiryDate() != null) {
            if (!"never".equalsIgnoreCase(this.wield.getEvaluationExpiryDate())) {
                final String[] expiryArr = this.wield.getEvaluationExpiryDate().split(" ");
                final String eDate = expiryArr[0] + "-" + this.appendZero(Integer.parseInt(expiryArr[1])) + "-" + this.appendZero(Integer.parseInt(expiryArr[2])) + " 00:00:00";
                cusDet.setProperty("expirytime", String.valueOf(this.dateInLong(eDate, "yyyy-MM-dd HH:mm:ss")));
            }
            cusDet.setProperty("Expiry_Date", this.wield.getEvaluationExpiryDate());
        }
        if (userType.equals("Registered")) {
            final Properties licenseModuleProps = this.wield.getModuleProperties("LicenseDetails");
            if (licenseModuleProps != null) {
                if (licenseModuleProps.getProperty("LicenseID") != null) {
                    cusDet.setProperty("License_ID", "true");
                }
                if (licenseModuleProps.getProperty("CustomerID") != null) {
                    cusDet.setProperty("Customer_ID", "true");
                }
                if (licenseModuleProps.getProperty("licenseusertype") != null) {
                    cusDet.setProperty("User_Type", licenseModuleProps.getProperty("licenseusertype"));
                }
                if (licenseModuleProps.getProperty("licenseusertype") != null) {
                    cusDet.setProperty("User_Type", licenseModuleProps.getProperty("licenseusertype"));
                }
            }
        }
        return cusDet;
    }
    
    private String getDBVendor(final int db) {
        if (db == 1) {
            return "MySQL";
        }
        if (db == 2) {
            return "MSSQL";
        }
        if (db == 3) {
            return "POSTGRESQL";
        }
        return "UNKNOWN";
    }
    
    public String updateBaseForm(final Properties proxyDetails, final boolean pushAllBaseDetails) {
        String result = "failure";
        if (this.getCreatorId() != null) {
            try {
                final Properties oldProp = this.loadPropertiesFile(METrack.getConfDir() + File.separator + "cdet.properties");
                final Properties newProp = new AddorUpdateInstallation().getBaseData(this.getCustomerDetails());
                Properties updateProp = null;
                if (pushAllBaseDetails) {
                    updateProp = newProp;
                }
                else {
                    updateProp = this.updateUserDetails(newProp, oldProp);
                }
                if (updateProp != null && updateProp.size() > 0) {
                    updateProp.setProperty("customerid", this.getCreatorId());
                    result = this.addRecord(this.confProp.getProperty("appname"), this.confProp.getProperty("datainputform"), updateProp, proxyDetails);
                    if (result != null && this.getTagTextNode(result, "status").equalsIgnoreCase("Success")) {
                        this.storeProperties(newProp, METrack.getConfDir() + File.separator + "cdet.properties");
                    }
                }
            }
            catch (final Exception e) {
                ZCUtil.logger.log(Level.INFO, "Exception while update baseform : " + e.toString());
                result = "failure";
            }
        }
        return result;
    }
    
    public String increment_Decrement_Dates(final long no_of_hours, final String formate) {
        final SimpleDateFormat f1 = new SimpleDateFormat(formate);
        long timeInMills = System.currentTimeMillis();
        timeInMills += 3600000L * no_of_hours;
        final Date dxt = new Date(timeInMills);
        return f1.format(dxt);
    }
    
    public Vector<Properties> viewRecordAsVector(final String view, final String criteria, final Properties proxyProp) {
        Vector retVec = null;
        try {
            retVec = new Vector();
            final Document document = this.viewRecordRestAPI(view, criteria, proxyProp);
            final Element el1 = document.getDocumentElement();
            final NodeList nList0 = el1.getElementsByTagName("record");
            for (int i1 = 0; i1 < nList0.getLength(); ++i1) {
                final Properties dataProps = new Properties();
                final Node cnoTemp = nList0.item(i1);
                final NodeList nList2 = cnoTemp.getChildNodes();
                for (int j = 0; j < nList2.getLength(); ++j) {
                    final Node cno = nList2.item(j);
                    final NamedNodeMap nnm0 = cno.getAttributes();
                    final String propKey = nnm0.getNamedItem("name").getNodeValue();
                    String propValue = "";
                    final NodeList nl1 = cno.getChildNodes();
                    for (int k = 0; k < nl1.getLength(); ++k) {
                        final Node cno2 = nl1.item(k);
                        if (cno2.getNodeType() == 1) {
                            final NodeList nl2 = cno2.getChildNodes();
                            for (int j2 = 0; j2 < nl2.getLength(); ++j2) {
                                final Node cno3 = nl2.item(j2);
                                propValue = cno3.getNodeValue();
                            }
                        }
                    }
                    dataProps.setProperty(propKey, propValue);
                }
                retVec.add(dataProps);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return retVec;
    }
    
    public Document viewRecordRestAPI(final String viewName, final String criteria, final Properties proxyDetails) {
        final BufferedReader in = null;
        Document doc = null;
        try {
            String viewRecordsURLStr = null;
            viewRecordsURLStr = "https://creator.zoho.com/api/xml/" + this.confProp.getProperty("appname") + "/view/" + viewName + "?apikey=" + this.confProp.getProperty("key");
            final Properties qstrProp = new Properties();
            qstrProp.setProperty("zc_ownername", this.confProp.getProperty("zowner"));
            if (criteria != null) {
                qstrProp.setProperty("criteria", criteria);
            }
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(this.connect(viewRecordsURLStr, qstrProp, proxyDetails).toString().getBytes()));
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while parsing document : " + e.toString());
            return null;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception fe) {
                ZCUtil.logger.log(Level.INFO, "Exception while parsing document : " + fe.toString());
            }
        }
        return doc;
    }
    
    public String getOldInstallationID() {
        return this.oldInstalaltionID;
    }
    
    public String getOldProductNae() {
        return this.oldProduct;
    }
    
    public String getInstallationTime() {
        String installationTime = String.valueOf(System.currentTimeMillis());
        try {
            final Properties cProp = this.loadPropertiesFile(this.zFile);
            final File f = new File(this.zFile);
            if (f.exists() && cProp != null && cProp.getProperty("installedtime") != null && !cProp.getProperty("installedtime").trim().equals("")) {
                installationTime = cProp.getProperty("installedtime");
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while getInstallationTime : " + e.toString());
        }
        return installationTime;
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
    
    private void updateLastPosted(final long milliSeconds) {
        try {
            if (new File(this.zFile).exists()) {
                final Properties ulProp = this.loadPropertiesFile(this.zFile);
                if (ulProp != null) {
                    ulProp.setProperty("lastupdated", String.valueOf(milliSeconds));
                    this.storeProperties(ulProp, this.zFile);
                }
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while updateLastPosted : " + e.toString());
        }
    }
    
    public Long getLasteUpdatedTime() {
        try {
            final Properties cProp = this.loadPropertiesFile(this.zFile);
            if (cProp.getProperty("lastupdated") != null) {
                return Long.parseLong(cProp.getProperty("lastupdated"));
            }
        }
        catch (final Exception e) {
            ZCUtil.logger.log(Level.INFO, "Exception while getCreatorLasteUpdatedTime : " + e.toString());
            return 0L;
        }
        return -1L;
    }
    
    static {
        ZCUtil.logger = Logger.getLogger(ZCUtil.class.getName());
    }
}
