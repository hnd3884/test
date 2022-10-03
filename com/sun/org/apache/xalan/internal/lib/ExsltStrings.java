package com.sun.org.apache.xalan.internal.lib;

import java.util.StringTokenizer;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xpath.internal.NodeSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExsltStrings extends ExsltBase
{
    public static String align(final String targetStr, final String paddingStr, final String type) {
        if (targetStr.length() >= paddingStr.length()) {
            return targetStr.substring(0, paddingStr.length());
        }
        if (type.equals("right")) {
            return paddingStr.substring(0, paddingStr.length() - targetStr.length()) + targetStr;
        }
        if (type.equals("center")) {
            final int startIndex = (paddingStr.length() - targetStr.length()) / 2;
            return paddingStr.substring(0, startIndex) + targetStr + paddingStr.substring(startIndex + targetStr.length());
        }
        return targetStr + paddingStr.substring(targetStr.length());
    }
    
    public static String align(final String targetStr, final String paddingStr) {
        return align(targetStr, paddingStr, "left");
    }
    
    public static String concat(final NodeList nl) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            final String value = ExsltBase.toString(node);
            if (value != null && value.length() > 0) {
                sb.append(value);
            }
        }
        return sb.toString();
    }
    
    public static String padding(final double length, final String pattern) {
        if (pattern == null || pattern.length() == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        final int len = (int)length;
        int numAdded = 0;
        int index = 0;
        while (numAdded < len) {
            if (index == pattern.length()) {
                index = 0;
            }
            sb.append(pattern.charAt(index));
            ++index;
            ++numAdded;
        }
        return sb.toString();
    }
    
    public static String padding(final double length) {
        return padding(length, " ");
    }
    
    public static NodeList split(final String str, final String pattern) {
        final NodeSet resultSet = new NodeSet();
        resultSet.setShouldCacheNodes(true);
        boolean done = false;
        int fromIndex = 0;
        int matchIndex = 0;
        String token = null;
        while (!done && fromIndex < str.length()) {
            matchIndex = str.indexOf(pattern, fromIndex);
            if (matchIndex >= 0) {
                token = str.substring(fromIndex, matchIndex);
                fromIndex = matchIndex + pattern.length();
            }
            else {
                done = true;
                token = str.substring(fromIndex);
            }
            final Document doc = JdkXmlUtils.getDOMDocument();
            synchronized (doc) {
                final Element element = doc.createElement("token");
                final Text text = doc.createTextNode(token);
                element.appendChild(text);
                resultSet.addNode(element);
            }
        }
        return resultSet;
    }
    
    public static NodeList split(final String str) {
        return split(str, " ");
    }
    
    public static NodeList tokenize(final String toTokenize, final String delims) {
        final NodeSet resultSet = new NodeSet();
        if (delims != null && delims.length() > 0) {
            final StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
            final Document doc = JdkXmlUtils.getDOMDocument();
            synchronized (doc) {
                while (lTokenizer.hasMoreTokens()) {
                    final Element element = doc.createElement("token");
                    element.appendChild(doc.createTextNode(lTokenizer.nextToken()));
                    resultSet.addNode(element);
                }
            }
        }
        else {
            final Document doc2 = JdkXmlUtils.getDOMDocument();
            synchronized (doc2) {
                for (int i = 0; i < toTokenize.length(); ++i) {
                    final Element element = doc2.createElement("token");
                    element.appendChild(doc2.createTextNode(toTokenize.substring(i, i + 1)));
                    resultSet.addNode(element);
                }
            }
        }
        return resultSet;
    }
    
    public static NodeList tokenize(final String toTokenize) {
        return tokenize(toTokenize, " \t\n\r");
    }
}
