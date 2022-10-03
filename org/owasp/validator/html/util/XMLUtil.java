package org.owasp.validator.html.util;

import java.util.regex.Matcher;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.regex.Pattern;

public class XMLUtil
{
    private static final Pattern encgt;
    private static final Pattern enclt;
    private static final Pattern encQuot;
    private static final Pattern encAmp;
    private static final Pattern gt;
    private static final Pattern lt;
    private static final Pattern quot;
    private static final Pattern amp;
    
    public static String getAttributeValue(final Element ele, final String attrName) {
        return decode(ele.getAttribute(attrName));
    }
    
    public static int getIntValue(final Element ele, final String tagName, final int defaultValue) {
        int toReturn = defaultValue;
        try {
            toReturn = Integer.parseInt(getTextValue(ele, tagName));
        }
        catch (final Throwable t) {}
        return toReturn;
    }
    
    public static String getTextValue(final Element ele, final String tagName) {
        String textVal = null;
        final NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            final Element el = (Element)nl.item(0);
            if (el.getFirstChild() != null) {
                textVal = el.getFirstChild().getNodeValue();
            }
            else {
                textVal = "";
            }
        }
        return decode(textVal);
    }
    
    public static boolean getBooleanValue(final Element ele, final String tagName) {
        boolean boolVal = false;
        final NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            final Element el = (Element)nl.item(0);
            boolVal = el.getFirstChild().getNodeValue().equals("true");
        }
        return boolVal;
    }
    
    public static boolean getBooleanValue(final Element ele, final String tagName, final boolean defaultValue) {
        boolean boolVal = defaultValue;
        final NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            final Element el = (Element)nl.item(0);
            if (el.getFirstChild().getNodeValue() != null) {
                boolVal = "true".equals(el.getFirstChild().getNodeValue());
            }
            else {
                boolVal = defaultValue;
            }
        }
        return boolVal;
    }
    
    public static String decode(String str) {
        if (str == null) {
            return null;
        }
        final Matcher gtmatcher = XMLUtil.encgt.matcher(str);
        if (gtmatcher.matches()) {
            str = gtmatcher.replaceAll(">");
        }
        final Matcher ltmatcher = XMLUtil.enclt.matcher(str);
        if (ltmatcher.matches()) {
            str = ltmatcher.replaceAll("<");
        }
        final Matcher quotMatcher = XMLUtil.encQuot.matcher(str);
        if (quotMatcher.matches()) {
            str = quotMatcher.replaceAll("\"");
        }
        final Matcher ampMatcher = XMLUtil.encAmp.matcher(str);
        if (ampMatcher.matches()) {
            str = ampMatcher.replaceAll("&");
        }
        return str;
    }
    
    public static String encode(String str) {
        if (str == null) {
            return null;
        }
        final Matcher gtMatcher = XMLUtil.gt.matcher(str);
        if (gtMatcher.matches()) {
            str = gtMatcher.replaceAll("&gt;");
        }
        final Matcher ltMatcher = XMLUtil.lt.matcher(str);
        if (ltMatcher.matches()) {
            str = ltMatcher.replaceAll("&lt;");
        }
        final Matcher quotMatcher = XMLUtil.quot.matcher(str);
        if (quotMatcher.matches()) {
            str = quotMatcher.replaceAll("&quot;");
        }
        final Matcher ampMatcher = XMLUtil.amp.matcher(str);
        if (ampMatcher.matches()) {
            str = ampMatcher.replaceAll("&amp;");
        }
        return str;
    }
    
    static {
        encgt = Pattern.compile("&gt;");
        enclt = Pattern.compile("&lt;");
        encQuot = Pattern.compile("&quot;");
        encAmp = Pattern.compile("&amp;");
        gt = Pattern.compile(">");
        lt = Pattern.compile("<");
        quot = Pattern.compile("\"");
        amp = Pattern.compile("&");
    }
}
