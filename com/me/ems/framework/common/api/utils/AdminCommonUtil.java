package com.me.ems.framework.common.api.utils;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.ArrayList;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.PersistenceInitializer;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Iterator;
import org.json.simple.JSONObject;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import org.json.simple.parser.JSONParser;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.LinkedList;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.List;
import java.util.logging.Logger;

public class AdminCommonUtil
{
    private static Logger logger;
    
    public List<String> getModuleFiles() throws APIException {
        final String productFolder = ProductUrlLoader.getInstance().getValue("admin_homepage_conf");
        if (productFolder == null || productFolder.isEmpty()) {
            throw new APIException("GENERIC0003", "ems.rest.api.param.missing", new String[] { "admin_homepage_conf" });
        }
        final List<String> moduleFiles = new LinkedList<String>();
        try {
            final String outFileName = SyMUtil.getInstallationDir() + File.separator + productFolder;
            final JSONParser jsonParser = new JSONParser();
            final JSONObject modulesJSON = FileAccessUtil.secureReadJSON(outFileName + "admin-homepage-conf.json");
            if (modulesJSON != null && modulesJSON.containsKey((Object)"adminHomePageFiles")) {
                final JSONArray parse = (JSONArray)jsonParser.parse(modulesJSON.get((Object)"adminHomePageFiles").toString());
                for (final Object file : parse) {
                    if (file.toString().startsWith("adminhome-")) {
                        moduleFiles.add(outFileName + file.toString());
                    }
                }
            }
        }
        catch (final FileNotFoundException ex) {
            AdminCommonUtil.logger.log(Level.SEVERE, "Exception caught in getting module files", ex);
            throw new APIException("GENERIC0003", "ems.rest.api.param.missing", new String[] { "admin_homepage_conf" });
        }
        catch (final Exception ex2) {
            AdminCommonUtil.logger.log(Level.SEVERE, "Exception caught in getting module files", ex2);
        }
        return moduleFiles;
    }
    
    public boolean isDBPostgres() {
        CustomerInfoUtil.getInstance();
        if (!CustomerInfoUtil.isSAS()) {
            final String dbName = PersistenceInitializer.getConfigurationValue("DBName");
            return dbName != null && dbName.equalsIgnoreCase("postgres");
        }
        return false;
    }
    
    public boolean isDBBundled() {
        CustomerInfoUtil.getInstance();
        if (!CustomerInfoUtil.isSAS()) {
            final String dbName = PersistenceInitializer.getConfigurationValue("DBName");
            final boolean isPostgresDB = dbName != null && dbName.equalsIgnoreCase("postgres");
            if (isPostgresDB) {
                try {
                    final String startDBServer = PersistenceInitializer.getConfigurationValue("StartDBServer");
                    if (Boolean.valueOf(startDBServer)) {
                        return true;
                    }
                }
                catch (final Exception e) {
                    AdminCommonUtil.logger.log(Level.SEVERE, "Caught exception while reading the properties from dbsettings", e);
                }
            }
        }
        return false;
    }
    
    public boolean isSpiceCheckEnabled() {
        return SyMUtil.isStandaloneServer() && SyMUtil.getSyMParameter("isSpiceworksEnabled") != null && SyMUtil.getSyMParameter("isSpiceworksEnabled").equalsIgnoreCase("enabled");
    }
    
    public boolean isPatchEditionDisabled() {
        try {
            if (!ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("Patch_Edition_Role")) {
                return true;
            }
        }
        catch (final Exception ex) {
            AdminCommonUtil.logger.log(Level.SEVERE, "Exception caught in customCheck", ex);
        }
        return false;
    }
    
    public static void modifyReadOnlyModeInSecurityXML(final Boolean isEnable) {
        try {
            final String securityPropertiesFilePath = System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "WEB-INF" + File.separator + "security-properties.xml";
            final DocumentBuilder documentBuilder = XMLUtils.getDocumentBuilderInstance();
            final Document document = documentBuilder.parse(securityPropertiesFilePath);
            final Element root = document.getDocumentElement();
            final Element properties = (Element)root.getElementsByTagName("properties").item(0);
            final NodeList property1 = properties.getElementsByTagName("property");
            for (int i = 0; i < property1.getLength(); ++i) {
                final Element item = (Element)property1.item(i);
                if (item.getAttribute("name").equalsIgnoreCase("readonly.mode")) {
                    item.setAttribute("value", isEnable.toString());
                }
            }
            final DOMSource source = new DOMSource(document);
            final Transformer transformer = XMLUtils.getTransformerInstance();
            final StreamResult result = new StreamResult(securityPropertiesFilePath);
            transformer.transform(source, result);
        }
        catch (final Exception ex) {
            AdminCommonUtil.logger.log(Level.SEVERE, "Exception caught in modifyReadOnlyModeInSecurityXML method", ex);
        }
    }
    
    public static Map<String, Object> toMap(final org.json.JSONObject jsonobj) throws JSONException {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Iterator<String> keys = jsonobj.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            Object value = jsonobj.get(key);
            if (value instanceof org.json.JSONArray) {
                value = toList((org.json.JSONArray)value);
            }
            else if (value instanceof org.json.JSONObject) {
                value = toMap((org.json.JSONObject)value);
            }
            map.put(key, value);
        }
        return map;
    }
    
    public static List<Object> toList(final org.json.JSONArray array) throws JSONException {
        final List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); ++i) {
            Object value = array.get(i);
            if (value instanceof org.json.JSONArray) {
                value = toList((org.json.JSONArray)value);
            }
            else if (value instanceof org.json.JSONObject) {
                value = toMap((org.json.JSONObject)value);
            }
            list.add(value);
        }
        return list;
    }
    
    public boolean isValidUserAdministrator() {
        try {
            if (SyMUtil.isSummaryServer()) {
                final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                return DMUserHandler.isAllProbeUser(loginId);
            }
        }
        catch (final Exception ex) {
            AdminCommonUtil.logger.log(Level.SEVERE, "Exception caught in customCheck for Summary Server Admin", ex);
        }
        return true;
    }
    
    static {
        AdminCommonUtil.logger = Logger.getLogger(AdminCommonUtil.class.getName());
    }
}
