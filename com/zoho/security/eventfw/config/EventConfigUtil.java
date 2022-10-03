package com.zoho.security.eventfw.config;

import java.util.regex.Matcher;
import java.util.concurrent.TimeUnit;
import com.zoho.security.eventfw.exceptions.EventConfigurationException;
import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class EventConfigUtil
{
    public static final Logger LOGGER;
    private static final Pattern SINGLE_TIME_PATTERN;
    
    public static boolean isValid(final Object value) {
        return value != null && !value.equals("null") && !value.equals("");
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
    
    public static DocumentBuilder createDocumentBuilder() throws Exception {
        try {
            final DocumentBuilderFactory secureDbf = DocumentBuilderFactory.newInstance();
            secureDbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            secureDbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            secureDbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            secureDbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            secureDbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            secureDbf.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
            secureDbf.setXIncludeAware(false);
            secureDbf.setExpandEntityReferences(false);
            return secureDbf.newDocumentBuilder();
        }
        catch (final Exception e) {
            EventConfigUtil.LOGGER.log(Level.SEVERE, "EventConfigurationReader : error : exception  : {0} while creating document builder", new Object[] { e.getMessage() });
            throw e;
        }
    }
    
    public static List<String> getStringAsList(final String str, String delim) {
        if (str == null) {
            return null;
        }
        delim = ((delim == null) ? " " : delim);
        final String[] rs = str.split(delim);
        return new ArrayList<String>(Arrays.asList(rs));
    }
    
    public static long getTimeInMilliSeconds(final String timeStr) {
        TIME_UNIT unit = TIME_UNIT.ms;
        int val = 0;
        try {
            final Matcher matcher = EventConfigUtil.SINGLE_TIME_PATTERN.matcher(timeStr);
            if (!matcher.matches()) {
                EventConfigUtil.LOGGER.log(Level.SEVERE, " The given time unit  : {0} is invalid", new Object[] { timeStr });
                throw new EventConfigurationException("PARSER_NOT_INITIALISED");
            }
            val = Integer.parseInt(matcher.group(1));
            final String timeUnit = matcher.group(2);
            if (timeUnit != null) {
                unit = TIME_UNIT.valueOf(timeUnit);
            }
            switch (unit) {
                case ms: {
                    return val;
                }
                case ns: {
                    return TimeUnit.NANOSECONDS.toMillis(val);
                }
                case m: {
                    return TimeUnit.MINUTES.toMillis(val);
                }
            }
        }
        catch (final IllegalArgumentException e) {
            EventConfigUtil.LOGGER.log(Level.SEVERE, " The given time unit  : {0} is invalid , exception {1} ", new Object[] { timeStr, e.getMessage() });
            throw new EventConfigurationException("PARSER_NOT_INITIALISED");
        }
        return -1L;
    }
    
    static {
        LOGGER = Logger.getLogger(EventConfigUtil.class.getName());
        SINGLE_TIME_PATTERN = Pattern.compile("([0-9]+)(m|ms|ns)?");
    }
    
    private enum TIME_UNIT
    {
        m, 
        ms, 
        ns;
    }
}
