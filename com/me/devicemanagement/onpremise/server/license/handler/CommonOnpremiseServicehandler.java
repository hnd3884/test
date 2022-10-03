package com.me.devicemanagement.onpremise.server.license.handler;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.license.ServiceHandler;
import com.me.devicemanagement.framework.server.license.LicenseDiffChecker;
import com.me.devicemanagement.framework.server.license.LicenseFactoryImpl;
import com.me.devicemanagement.onpremise.server.util.CustomColumnUtil;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.json.JSONArray;
import java.util.List;
import java.util.Map;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.FileNotFoundException;
import java.io.File;
import com.me.devicemanagement.framework.server.common.DMModuleHandler;
import com.me.devicemanagement.onpremise.server.util.EMSProductUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.License;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.CommonServiceHandler;

public class CommonOnpremiseServicehandler extends CommonServiceHandler
{
    private static Logger logger;
    private static CommonOnpremiseServicehandler commonOnpremiseServicehandler;
    private static final HashMap<String, String> PRODUCT_CODE_MAP;
    private static final HashMap<String, String> SS_PRODUCT_CODE_MAP;
    
    private CommonOnpremiseServicehandler() {
    }
    
    public static CommonOnpremiseServicehandler getInstance() {
        if (CommonOnpremiseServicehandler.commonOnpremiseServicehandler == null) {
            CommonOnpremiseServicehandler.commonOnpremiseServicehandler = new CommonOnpremiseServicehandler();
        }
        return CommonOnpremiseServicehandler.commonOnpremiseServicehandler;
    }
    
    public void migrate(final JSONObject licenseDiffChecker, final License oldLicenseobject, final License newLicenseObject) {
        super.migrate(licenseDiffChecker, oldLicenseobject, newLicenseObject);
    }
    
    public void StartUp() {
        try {
            this.resetProductConf(true);
            this.resetGeneralProperties();
            super.StartUp();
            this.resetProductConfig();
            this.resetupdateConf();
        }
        catch (final Exception e) {
            CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e);
        }
    }
    
    public void reset(final JSONObject licenseDiffChecker, final License oldLicenseobject, final License newLicenseObject) {
        try {
            this.resetProductConf(false);
            this.resetGeneralProperties();
            EMSProductUtil.regenerateProductCode();
            this.resetModuleState();
            super.reset(licenseDiffChecker, oldLicenseobject, newLicenseObject);
            this.resetWebSettings();
            this.resetCustomColumnUtil();
            this.resetProductConfig();
            this.resetupdateConf();
        }
        catch (final Exception e) {
            CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e);
        }
    }
    
    private void resetModuleState() {
        DMModuleHandler.setIsOSDEnabled((Boolean)null);
    }
    
    private void resetupdateConf() throws Exception {
        final String fileURL = System.getProperty("server.home") + "/conf/update_conf.xml";
        final File file = new File(fileURL);
        if (!file.exists()) {
            throw new FileNotFoundException("File: " + fileURL + "doesn't exist");
        }
        final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
        final Document doc = docBuilder.parse(file);
        final Element root = doc.getDocumentElement();
        final NodeList list = root.getElementsByTagName("property");
        final int size = list.getLength();
        if (size != 0) {
            for (int i = 0; i < size; ++i) {
                final Node node = list.item(i);
                if (node.getNodeType() == 1) {
                    final Element propertyElement = (Element)node;
                    final String key = propertyElement.getAttribute("name");
                    if ("ProductName".equals(key)) {
                        String productCode = com.me.devicemanagement.framework.server.util.EMSProductUtil.getEMSProductCode().get(0).toString();
                        if (productCode.equals("VMP")) {
                            productCode = "PMP";
                        }
                        if (SyMUtil.isProbeServer() || SyMUtil.isSummaryServer()) {
                            propertyElement.setAttribute("value", CommonOnpremiseServicehandler.SS_PRODUCT_CODE_MAP.get(productCode));
                        }
                        else {
                            propertyElement.setAttribute("value", CommonOnpremiseServicehandler.PRODUCT_CODE_MAP.get(productCode));
                        }
                    }
                }
            }
            this.writeXmlToFile(file, root);
        }
    }
    
    private void resetGeneralProperties() {
        GeneralPropertiesLoader.allpropsOfActiveProducts = null;
        GeneralPropertiesLoader.productGenProps = null;
        GeneralPropertiesLoader.getInstance().getProperties();
    }
    
    private void resetProductConf(final boolean isStartup) throws Exception {
        String installDir = null;
        try {
            installDir = GeneralPropertiesLoader.getInstallationDir();
        }
        catch (final Exception e) {
            CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e);
        }
        final String productConfFile = installDir + File.separator + "conf" + File.separator + "product.conf";
        final Properties props = new Properties();
        final ArrayList arrayList = EMSProductUtil.getEMSProductCodeFromMap();
        final Iterator iterator = arrayList.iterator();
        String products = "";
        CommonOnpremiseServicehandler.newProducts = new String[arrayList.size()];
        int i = 0;
        while (iterator.hasNext()) {
            if (!products.isEmpty()) {
                products += ",";
            }
            final String str = iterator.next();
            products += str;
            CommonOnpremiseServicehandler.newProducts[i] = str;
            ++i;
        }
        props.setProperty("activeproductcodes", products);
        if (!isStartup) {
            CommonOnpremiseServicehandler.oldProducts = new String[] { License.getOldLicenseObject().getProductCode() };
        }
        final String productCode = arrayList.get(0);
        String productName;
        if (SyMUtil.isProbeServer() || SyMUtil.isSummaryServer()) {
            productName = CommonOnpremiseServicehandler.SS_PRODUCT_CODE_MAP.get(productCode);
        }
        else {
            productName = CommonOnpremiseServicehandler.PRODUCT_CODE_MAP.get(productCode);
        }
        ((Hashtable<String, String>)props).put("productname", productName);
        props.setProperty("activeproductcodes", products);
        GeneralPropertiesLoader.storeProperties(props, productConfFile);
        CommonOnpremiseServicehandler.newProducts = this.setProducts(CommonOnpremiseServicehandler.newProducts);
    }
    
    private static Properties readProperties(final String confFileName) throws Exception {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            CommonOnpremiseServicehandler.logger.log(Level.SEVERE, "Caught exception while reading properties from file: " + confFileName, ex);
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    private String[] setProducts(String[] products) throws Exception {
        String[] productCodes = new String[0];
        final String fname = GeneralPropertiesLoader.getInstallationDir() + File.separator + "conf" + File.separator + "product.conf";
        final Properties prodStrGen = readProperties(fname);
        final String prodStr = prodStrGen.getProperty("activeproductcodes");
        if (prodStr != null && !prodStr.isEmpty()) {
            productCodes = prodStr.split(",");
            products = new String[productCodes.length];
            int i = 0;
            for (final String code : productCodes) {
                products[i] = code;
                ++i;
            }
        }
        return productCodes;
    }
    
    private void resetWebSettings() throws Exception {
        final String path = System.getProperty("server.home") + File.separator + "conf" + File.separator + "UEMS_Server" + File.separator + "configurations" + File.separator + "default_configurations.json";
        final JSONObject jsonObject = JsonUtils.loadJsonFile(new File(path));
        final String path2 = System.getProperty("server.home") + File.separator + "conf" + File.separator + "websettings.conf";
        final HashMap hashMap = new HashMap();
        final Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Iterator<String> keys2 = jsonObject.keys();
            while (keys2.hasNext()) {
                final JSONObject entries = jsonObject.getJSONObject((String)keys2.next());
                hashMap.put(key, entries);
            }
        }
        final Iterator map = hashMap.entrySet().iterator();
        final HashMap productOldMap = new HashMap();
        final HashMap productNewMap = new HashMap();
        while (map.hasNext()) {
            final Map.Entry entry = map.next();
            final JSONObject key2 = entry.getValue();
            for (final String str : CommonOnpremiseServicehandler.oldProducts) {
                if (key2.has(str)) {
                    final JSONObject json = (JSONObject)key2.get(str);
                    final List list = new ArrayList();
                    final JSONArray jArray = json.names();
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); ++i) {
                            list.add(jArray.getString(i));
                        }
                    }
                    final Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        final String newKey = String.valueOf(iterator.next());
                        final String value = (String)json.get(newKey);
                        productOldMap.put(newKey, value);
                    }
                }
            }
            for (final String str : CommonOnpremiseServicehandler.newProducts) {
                if (key2.has(str)) {
                    final JSONObject json = (JSONObject)key2.get(str);
                    final List list = new ArrayList();
                    final JSONArray jArray = json.names();
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); ++i) {
                            list.add(jArray.getString(i));
                        }
                    }
                    final Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        final String newKey = String.valueOf(iterator.next());
                        final String value = (String)json.get(newKey);
                        productNewMap.put(newKey, value);
                    }
                }
            }
            final Iterator iterator2 = productOldMap.entrySet().iterator();
            final Iterator iterator3 = productNewMap.entrySet().iterator();
            final Properties properties = new Properties();
            while (iterator2.hasNext()) {
                final Map.Entry mapEntry = iterator2.next();
                final String key3 = mapEntry.getKey();
                if (!productNewMap.containsKey(key3)) {
                    ((Hashtable<String, Object>)properties).put(key3, mapEntry.getValue());
                }
            }
            while (iterator3.hasNext()) {
                final Map.Entry mapEntry = iterator3.next();
                final String key3 = mapEntry.getKey();
                if (!productOldMap.containsKey(key3)) {
                    ((Hashtable<String, Object>)properties).put(key3, mapEntry.getValue());
                }
            }
            storeProperties(properties, path2, null);
        }
    }
    
    private static void storeProperties(final Properties newprops, final String confFileName, final String comments) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
            final Enumeration keys = newprops.propertyNames();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                props.setProperty(key, newprops.getProperty(key));
            }
            fos = new FileOutputStream(confFileName);
            props.store(fos, comments);
            fos.close();
        }
        catch (final Exception ex) {
            CommonOnpremiseServicehandler.logger.log(Level.SEVERE, "Caught Exception" + ex);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    private void resetProductConfig() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        final String fileURL = System.getProperty("server.home") + "/conf/product-config.xml";
        final File file = new File(fileURL);
        if (!file.exists()) {
            throw new FileNotFoundException("File: " + fileURL + "doesnt exists");
        }
        final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
        final Document doc = docBuilder.parse(file);
        final Element root = doc.getDocumentElement();
        final NodeList connList = root.getElementsByTagName("configuration");
        for (int length = connList.getLength(), i = 0; i < length; ++i) {
            final Element connectorEl = (Element)connList.item(i);
            final String name = connectorEl.getAttribute("name");
            if (name != null && name.equals("DMProductCode")) {
                connectorEl.setAttribute("value", License.getNewLicenseObject().getProductCode());
            }
        }
        this.writeXmlToFile(file, root);
    }
    
    private void writeXmlToFile(final File file, final Element root) throws IOException, TransformerException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        final String encoding = "ISO-8859-1";
        final Transformer transformer = XMLUtils.getTransformerInstance();
        final DOMSource source = new DOMSource(root);
        final StreamResult result = new StreamResult(writer);
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("indent", "yes");
        ((Hashtable<String, String>)prop).put("encoding", encoding);
        ((Hashtable<String, String>)prop).put("method", "xml");
        transformer.setOutputProperties(prop);
        transformer.transform(source, result);
    }
    
    private void resetCustomColumnUtil() throws Exception {
        CustomColumnUtil.readFromFile();
    }
    
    public void migrationHandling() {
        this.reset(null, null, null);
        this.migrate(null, null, null);
        this.callPreviousProductReset(CommonOnpremiseServicehandler.oldProducts);
        this.callMigratedProductMigrate(CommonOnpremiseServicehandler.newProducts);
    }
    
    private void callMigratedProductMigrate(final String[] newProducts) {
        final LicenseFactoryImpl licenseFactory = new LicenseFactoryImpl();
        final JSONObject licenseDiff = LicenseDiffChecker.getInstance().getLicenseDiff();
        final License oldLicenseObject = License.getOldLicenseObject();
        final License newLicenseObject = License.getNewLicenseObject();
        for (final String prods : newProducts) {
            try {
                final ServiceHandler license = licenseFactory.getLicenseObject(prods);
                if (license != null) {
                    license.migrate(licenseDiff, oldLicenseObject, newLicenseObject);
                }
            }
            catch (final ClassNotFoundException e) {
                CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e);
            }
            catch (final IllegalAccessException e2) {
                CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e2);
            }
            catch (final InstantiationException e3) {
                CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e3);
            }
        }
    }
    
    private void callPreviousProductReset(final String[] oldProducts) {
        final LicenseFactoryImpl licenseFactory = new LicenseFactoryImpl();
        final JSONObject licenseDiff = LicenseDiffChecker.getInstance().getLicenseDiff();
        final License oldLicenseObject = License.getOldLicenseObject();
        final License newLicenseObject = License.getNewLicenseObject();
        for (final String prods : oldProducts) {
            try {
                final ServiceHandler license = licenseFactory.getLicenseObject(prods);
                if (license != null) {
                    license.reset(licenseDiff, oldLicenseObject, newLicenseObject);
                }
            }
            catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e);
            }
        }
    }
    
    public void startUpHandling() {
        try {
            this.StartUp();
            this.CallLicensedProductStartup(CommonOnpremiseServicehandler.newProducts);
        }
        catch (final Exception ex) {
            CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void CallLicensedProductStartup(final String[] newProducts) {
        final LicenseFactoryImpl licenseFactory = new LicenseFactoryImpl();
        for (final String prods : newProducts) {
            try {
                final ServiceHandler license = licenseFactory.getLicenseObject(prods);
                if (license != null) {
                    license.StartUp();
                }
            }
            catch (final ClassNotFoundException e) {
                CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e);
            }
            catch (final IllegalAccessException e2) {
                CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e2);
            }
            catch (final InstantiationException e3) {
                CommonOnpremiseServicehandler.logger.log(Level.SEVERE, null, e3);
            }
        }
    }
    
    static {
        CommonOnpremiseServicehandler.logger = Logger.getLogger(CommonOnpremiseServicehandler.class.getName());
        CommonOnpremiseServicehandler.commonOnpremiseServicehandler = null;
        PRODUCT_CODE_MAP = new HashMap() {
            {
                this.put("DCEE", "ManageEngine Endpoint Central");
                this.put("DCMSP", "ManageEngine Endpoint Central MSP");
                this.put("PMP", "ManageEngine Patch Manager Plus");
                this.put("VMP", "ManageEngine Vulnerability Manager Plus");
                this.put("ACP", "ManageEngine Application Control Plus");
                this.put("DCP", "ManageEngine Device Control Plus");
                this.put("RAP", "ManageEngine Remote Access Plus");
                this.put("OSD", "ManageEngine OS Deployer");
                this.put("UES", "ManageEngine Unified Endpoint Security");
            }
        };
        SS_PRODUCT_CODE_MAP = new HashMap() {
            {
                this.put("DCEE", "ManageEngine UEMS Summary Server");
            }
        };
    }
}
