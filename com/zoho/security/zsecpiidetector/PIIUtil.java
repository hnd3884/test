package com.zoho.security.zsecpiidetector;

import java.util.Hashtable;
import com.zlabs.pii.utils.NLPUtils;
import org.json.JSONObject;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.apache.xerces.util.SecurityManager;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import java.util.logging.Level;
import opennlp.tools.tokenize.TokenizerME;
import com.zlabs.pii.models.ZTokenizerModel;
import java.util.logging.Logger;

public final class PIIUtil
{
    private static final Logger LOGGER;
    
    public static String[] tokenize(final String data) {
        try {
            final TokenizerME tokenizer = new TokenizerME(ZTokenizerModel.getInstance(PIIDetectorFactory.isRegexResourceOnly()).getTokenizerModel());
            return tokenizer.tokenize(data);
        }
        catch (final Exception e) {
            PIIUtil.LOGGER.log(Level.SEVERE, "Failed Tokenizing data", e.getMessage());
            return null;
        }
    }
    
    public static DocumentBuilder getDocumentBuilder() {
        try {
            Properties dbfFeatures = null;
            dbfFeatures = new Properties();
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://xml.org/sax/features/external-general-entities", false);
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://xml.org/sax/features/external-parameter-entities", false);
            final DocumentBuilder builder = createDocumentBuilder(false, false, dbfFeatures);
            return builder;
        }
        catch (final Exception e) {
            PIIUtil.LOGGER.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
    
    public static DocumentBuilder createDocumentBuilder(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity, final Properties dbfFeatures) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            int inlineEntityExpansionCount = 0;
            if (allowInlineEntityExpansion) {
                inlineEntityExpansionCount = 1000;
            }
            final SecurityManager manager = new SecurityManager();
            manager.setEntityExpansionLimit(inlineEntityExpansionCount);
            factory.setAttribute("http://apache.org/xml/properties/security-manager", manager);
            if (dbfFeatures != null) {
                for (final Object featureName : ((Hashtable<Object, V>)dbfFeatures).keySet()) {
                    final String name = featureName.toString();
                    final String val = ((Hashtable<K, Object>)dbfFeatures).get(name).toString();
                    final boolean value = Boolean.parseBoolean(val);
                    factory.setFeature(name, value);
                }
            }
            final DocumentBuilder builder = factory.newDocumentBuilder();
            if (!allowExternalEntity) {
                builder.setEntityResolver(new EntityResolver() {
                    @Override
                    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
                        return new InputSource(new StringReader(""));
                    }
                });
            }
            return builder;
        }
        catch (final Exception e) {
            PIIUtil.LOGGER.log(Level.WARNING, null, e);
            return null;
        }
    }
    
    public static List<Element> getChildNodesByTagName(final Element element, final String tagName) {
        final List<Element> nodeList = new ArrayList<Element>();
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (childNode.getNodeType() == 1 && childNode.getNodeName().equalsIgnoreCase(tagName)) {
                final Element childElement = (Element)childNode;
                nodeList.add(childElement);
            }
        }
        return nodeList;
    }
    
    public static Element getFirstChildNodeByTagName(final Element element, final String tagName) {
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (childNode.getNodeType() == 1 && childNode.getNodeName().equalsIgnoreCase(tagName)) {
                return (Element)childNode;
            }
        }
        return null;
    }
    
    public static String createUniqueKeyFromStrings(final String... varags) {
        final StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < varags.length; ++i) {
            sb.append(varags[i]);
            if (i != varags.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
    
    public static JSONObject convertPiiDetectorResultAsJSON(final String piiData, final float confidenceLevel, final int startIndex, final int endIndex, final PIIEnum.Category piiCategory, final PIIEnum.Sensitivity sensitivityLevel, final String piiType, final PIIEnum.DetectionType detectionType, final PIIResult.InfoFormat infoFormat) {
        final JSONObject piiJson = new JSONObject();
        piiJson.put(PIIEnum.JsonKeys.PII_DATA.value(), (Object)piiData);
        piiJson.put(PIIEnum.JsonKeys.CONFIDENCE_LEVEL.value(), (Object)NLPUtils.decimalFormat.format(confidenceLevel));
        if (infoFormat == PIIResult.InfoFormat.INDEX) {
            piiJson.put(PIIEnum.JsonKeys.START_INDEX.value(), startIndex);
            piiJson.put(PIIEnum.JsonKeys.END_INDEX.value(), endIndex);
        }
        piiJson.put(PIIEnum.JsonKeys.PII_CATEGORY.value(), (Object)piiCategory.value());
        piiJson.put(PIIEnum.JsonKeys.SENSITIVITY_LEVEL.value(), (Object)sensitivityLevel);
        piiJson.put(PIIEnum.JsonKeys.PII_TYPE.value(), (Object)piiType);
        piiJson.put(PIIEnum.JsonKeys.DETECTION_TYPE.value(), (Object)detectionType.toString());
        return piiJson;
    }
    
    public static boolean isValid(final Object value) {
        return value != null && !value.equals("null") && !value.equals("");
    }
    
    static {
        LOGGER = Logger.getLogger(PIIUtil.class.getName());
    }
}
