package com.adventnet.client.view.web;

import javax.servlet.http.Cookie;
import java.util.Comparator;
import com.adventnet.client.util.web.JSUtil;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.iam.xss.IAMEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.util.web.JavaScriptConstants;

public class StateUtils implements JavaScriptConstants, WebConstants
{
    public static List parseAsList(final String value) {
        final StringTokenizer strTok = new StringTokenizer(value, ",");
        final List toReturn = new ArrayList(strTok.countTokens());
        while (strTok.hasMoreTokens()) {
            toReturn.add(strTok.nextElement());
        }
        return toReturn;
    }
    
    public static String encodeAsJS(final List values) {
        final StringBuffer strBuf = new StringBuffer("[");
        for (int i = 0, j = values.size(); i < j; ++i) {
            final Object value = values.get(i);
            if (value != null) {
                strBuf.append('\'');
                strBuf.append(IAMEncoder.encodeJavaScript(value.toString()));
                strBuf.append('\'');
            }
            strBuf.append(",");
        }
        if (values.size() > 0) {
            strBuf.deleteCharAt(strBuf.length() - 1);
        }
        strBuf.append("]");
        return strBuf.toString();
    }
    
    public static Map parseAsMap(final String value) {
        final StringTokenizer strTok = new StringTokenizer(value, ",");
        if (strTok.countTokens() % 2 != 0) {
            throw new RuntimeException("Map value has not been properly encoded. Passed value is " + value);
        }
        final HashMap toRet = new HashMap();
        while (strTok.hasMoreTokens()) {
            toRet.put(strTok.nextElement(), strTok.nextElement());
        }
        return toRet;
    }
    
    public static String encodeAsJS(final Map values) {
        final Iterator valIte = values.entrySet().iterator();
        final StringBuffer strBuf = new StringBuffer("{");
        while (valIte.hasNext()) {
            final Map.Entry valEntry = valIte.next();
            final String key = valEntry.getKey();
            final Object value = valEntry.getValue();
            if (value != null) {
                strBuf.append(key);
                strBuf.append(":\"");
                strBuf.append(IAMEncoder.encodeJavaScript(value.toString()));
                strBuf.append("\"");
            }
            strBuf.append(",");
        }
        strBuf.deleteCharAt(strBuf.length() - 1);
        strBuf.append("};");
        return strBuf.toString();
    }
    
    public static void generateAssignment(final StringBuffer strBuf, final String lhs, final String rhs, final boolean isString) {
        strBuf.append(lhs);
        strBuf.append("=");
        if (rhs != null) {
            if (isString) {
                strBuf.append("\"");
            }
            strBuf.append(rhs);
            if (isString) {
                strBuf.append("\"");
            }
        }
        else {
            strBuf.append("null");
        }
        strBuf.append(";");
    }
    
    public static void generateArgument(final StringBuffer strBuf, final String arg, final int argPos) {
        JSUtil.genArg(strBuf, arg, argPos, true, true);
    }
    
    public static class CookieComparator implements Comparator
    {
        @Override
        public int compare(final Object o1, final Object o2) {
            if (!(o1 instanceof Cookie)) {
                throw new ClassCastException();
            }
            if (!(o2 instanceof Cookie)) {
                throw new ClassCastException();
            }
            final String val1 = ((Cookie)o1).getName();
            final String val2 = ((Cookie)o2).getName();
            return val1.compareTo(val2);
        }
        
        @Override
        public boolean equals(final Object o1) {
            return true;
        }
    }
}
