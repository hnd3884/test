package com.adventnet.client.util.web;

import com.adventnet.i18n.I18N;
import java.io.IOException;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.persistence.Row;
import java.util.Iterator;

public class JSUtil implements JavaScriptConstants, WebConstants
{
    private JSUtil() {
    }
    
    public static void appendProperties(final StringBuffer strBuf, final Iterator ite, final int propNameColIdx, final int propValColIdx) throws Exception {
        while (ite.hasNext()) {
            final Row r = ite.next();
            strBuf.append(r.get(propNameColIdx)).append(':').append('\"').append(r.get(propValColIdx)).append("\",");
        }
        strBuf.deleteCharAt(strBuf.length() - 1);
    }
    
    public static void genArg(final Appendable strBuf, Object arg, final int argPos, final boolean escapeForJs, final boolean addQuote) {
        try {
            if (arg instanceof String) {
                arg = i18nConvertValues(arg);
            }
            if (argPos != 0) {
                strBuf.append(',');
            }
            if (arg == null) {
                strBuf.append("null");
            }
            else {
                if (escapeForJs) {
                    arg = IAMEncoder.encodeJavaScript(arg.toString());
                }
                if (addQuote) {
                    strBuf.append("\"");
                }
                strBuf.append(String.valueOf(arg));
                if (addQuote) {
                    strBuf.append("\"");
                }
            }
            if (argPos == 1 || argPos == 3) {
                strBuf.append(");");
            }
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        catch (final Exception e2) {
            throw new RuntimeException(e2);
        }
    }
    
    public static String getEscapedString(final Object textArg) {
        if (textArg == null) {
            return null;
        }
        final String text = textArg.toString();
        final StringBuffer charBuffer = new StringBuffer();
        for (int length = 0; length < text.length(); ++length) {
            final char ch = text.charAt(length);
            if (ch == '\r' || ch == '\n') {
                charBuffer.append('\\').append('n');
            }
            else if (ch == '\"' || ch == '\'' || ch == '/' || ch == '\\') {
                charBuffer.append('\\');
                charBuffer.append(ch);
            }
            else {
                charBuffer.append(ch);
            }
        }
        return charBuffer.toString();
    }
    
    public static String i18nConvertValues(final Object arg) throws Exception {
        String returnArgs = "";
        final String arguments = (String)arg;
        final String[] args = arguments.split(",");
        for (int i = 0; i < args.length; ++i) {
            if (args[i].indexOf("=") != -1) {
                final int index = args[i].indexOf("=");
                final String key = args[i].substring(0, index);
                final String value = I18N.getMsg(args[i].substring(index + 1), new Object[0]);
                returnArgs = returnArgs + key + "=" + value;
            }
            else {
                returnArgs += args[i];
            }
            if (i < args.length - 1) {
                returnArgs += ",";
            }
        }
        return returnArgs;
    }
}
