package com.sun.org.apache.xpath.internal.res;

import java.text.MessageFormat;
import com.sun.org.apache.bcel.internal.util.SecuritySupport;
import java.util.ListResourceBundle;
import com.sun.org.apache.xml.internal.res.XMLMessages;

public class XPATHMessages extends XMLMessages
{
    private static ListResourceBundle XPATHBundle;
    private static final String XPATH_ERROR_RESOURCES = "com.sun.org.apache.xpath.internal.res.XPATHErrorResources";
    
    public static final String createXPATHMessage(final String msgKey, final Object[] args) {
        if (XPATHMessages.XPATHBundle == null) {
            XPATHMessages.XPATHBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xpath.internal.res.XPATHErrorResources");
        }
        if (XPATHMessages.XPATHBundle != null) {
            return createXPATHMsg(XPATHMessages.XPATHBundle, msgKey, args);
        }
        return "Could not load any resource bundles.";
    }
    
    public static final String createXPATHWarning(final String msgKey, final Object[] args) {
        if (XPATHMessages.XPATHBundle == null) {
            XPATHMessages.XPATHBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xpath.internal.res.XPATHErrorResources");
        }
        if (XPATHMessages.XPATHBundle != null) {
            return createXPATHMsg(XPATHMessages.XPATHBundle, msgKey, args);
        }
        return "Could not load any resource bundles.";
    }
    
    public static final String createXPATHMsg(final ListResourceBundle fResourceBundle, final String msgKey, final Object[] args) {
        String fmsg = null;
        boolean throwex = false;
        String msg = null;
        if (msgKey != null) {
            msg = fResourceBundle.getString(msgKey);
        }
        if (msg == null) {
            msg = fResourceBundle.getString("BAD_CODE");
            throwex = true;
        }
        if (args != null) {
            try {
                for (int n = args.length, i = 0; i < n; ++i) {
                    if (null == args[i]) {
                        args[i] = "";
                    }
                }
                fmsg = MessageFormat.format(msg, args);
            }
            catch (final Exception e) {
                fmsg = fResourceBundle.getString("FORMAT_FAILED");
                fmsg = fmsg + " " + msg;
            }
        }
        else {
            fmsg = msg;
        }
        if (throwex) {
            throw new RuntimeException(fmsg);
        }
        return fmsg;
    }
    
    static {
        XPATHMessages.XPATHBundle = null;
    }
}
