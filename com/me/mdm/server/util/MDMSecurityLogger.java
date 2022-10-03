package com.me.mdm.server.util;

import java.util.Properties;
import java.util.Collection;
import java.util.Arrays;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import org.json.JSONException;
import org.json.JSONArray;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.w3c.dom.Node;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import java.util.logging.Level;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPathFactory;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import com.adventnet.iam.security.SecurityUtil;
import com.me.mdm.framework.syncml.xml.XML2SyncMLMessageConverter;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.NSDictionary;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.logging.Logger;

public class MDMSecurityLogger
{
    private static final Logger LOGGER;
    private static HashSet plistsensitiveKeyHash;
    private static String sensitiveDataFilePath;
    private static JSONObject syncMLSensitiveKeyJSON;
    private static String syncMLSensitiveDataFilePath;
    
    public static void info(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, String object) {
        try {
            if (object != null && object.contains("<plist")) {
                info(logger, sourceClass, sourceMethod, msg, (NSDictionary)DMSecurityUtil.parsePropertyList(object.getBytes("UTF-8")));
            }
            else if (object != null && object.contains("<SyncML")) {
                object = XML2SyncMLMessageConverter.replaceInvalidCharacters(object);
                final Document doc = SecurityUtil.getDocumentBuilder(false, false).parse(new InputSource(new StringReader(object)));
                final XPathFactory xPathFactory = XPathFactory.newInstance();
                final XPath xPath = xPathFactory.newXPath();
                final NodeList nodes = (NodeList)xPath.compile("//SyncML/SyncBody/Sequence/Atomic/Add/Item/Target/LocURI/text() | //SyncML/SyncBody/Sequence/Atomic/Replace/Item/Target/LocURI/text() | //SyncML/SyncBody/Sequence/Atomic/Delete/Item/Target/LocURI/text()").evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); ++i) {
                    final Node node = nodes.item(i);
                    final String key = matchKeyString(MDMSecurityLogger.syncMLSensitiveKeyJSON, node.getTextContent());
                    if (key != null) {
                        for (Node dataNode = node.getParentNode().getParentNode().getParentNode().getFirstChild(); dataNode != null; dataNode = dataNode.getNextSibling()) {
                            if (dataNode.getNodeName().equals("Data")) {
                                obfusticateNodeElements(dataNode, key);
                                break;
                            }
                        }
                    }
                }
                logger.logp(Level.INFO, sourceClass, sourceMethod, msg, documentToString(doc));
            }
            else {
                DMSecurityLogger.info(logger, sourceClass, sourceMethod, msg, (Object)object);
            }
        }
        catch (final Exception ex) {
            MDMSecurityLogger.LOGGER.log(Level.WARNING, "Exception while printing data...", ex);
        }
    }
    
    public static void info(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final JSONObject object) {
        DMSecurityLogger.info(logger, sourceClass, sourceMethod, msg, (Object)object);
    }
    
    public static void info(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final NSDictionary object) {
        final NSDictionary logData = (NSDictionary)PlistWrapper.getInstance().replaceDictionaryData((NSObject)object, MDMSecurityLogger.plistsensitiveKeyHash, "********");
        logger.logp(Level.INFO, sourceClass, sourceMethod, msg, logData.toXMLPropertyList());
    }
    
    private static void obfusticateNodeElements(final Node dataNode, final String locURIRegex) throws JSONException {
        Object obj;
        try {
            obj = MDMSecurityLogger.syncMLSensitiveKeyJSON.getJSONArray(locURIRegex);
        }
        catch (final Exception e) {
            obj = String.valueOf(MDMSecurityLogger.syncMLSensitiveKeyJSON.get(locURIRegex));
        }
        if (obj instanceof JSONArray) {
            final JSONArray regexArray = (JSONArray)obj;
            for (int i = 0; i < regexArray.length(); ++i) {
                final String regex = (String)regexArray.get(i);
                dataNode.setTextContent(dataNode.getTextContent().replaceAll(regex, "********"));
            }
        }
        else {
            dataNode.setTextContent("********");
        }
    }
    
    private static String documentToString(final Document document) throws TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
        final Transformer tf = transformerFactory.newTransformer();
        final StringWriter writer = new StringWriter();
        tf.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
    
    private static String matchKeyString(final JSONObject jsonObject, final String stringToBeMatched) {
        final Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            if (key.equals(stringToBeMatched) || Pattern.matches(key, stringToBeMatched)) {
                return key;
            }
        }
        return null;
    }
    
    public static HashSet getPlistsensitiveKeyHash() {
        return MDMSecurityLogger.plistsensitiveKeyHash;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        MDMSecurityLogger.plistsensitiveKeyHash = new HashSet();
        MDMSecurityLogger.sensitiveDataFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "sensitiveLogKeyStorage.properties";
        MDMSecurityLogger.syncMLSensitiveKeyJSON = new JSONObject();
        MDMSecurityLogger.syncMLSensitiveDataFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "sensitiveSyncMLKeys.json";
        try {
            if (new File(MDMSecurityLogger.sensitiveDataFilePath).exists()) {
                final Properties sensitiveDataList = FileUtils.readPropertyFile(new File(MDMSecurityLogger.sensitiveDataFilePath));
                final String sensitiveData = sensitiveDataList.getProperty("plist_sensitive_key");
                if (!"".equals(sensitiveData)) {
                    (MDMSecurityLogger.plistsensitiveKeyHash = new HashSet((Collection<? extends E>)Arrays.asList(sensitiveData.replaceAll(" ", "").split(",")))).remove("");
                }
            }
            final File file = new File(MDMSecurityLogger.syncMLSensitiveDataFilePath);
            if (file.exists()) {
                final String content = org.apache.commons.io.FileUtils.readFileToString(file, "utf-8");
                MDMSecurityLogger.syncMLSensitiveKeyJSON = new JSONObject(content);
            }
        }
        catch (final Exception ex) {
            MDMSecurityLogger.LOGGER.log(Level.WARNING, "Exception get the sensitive data from property file", ex);
        }
    }
}
