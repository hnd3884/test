package com.sun.org.apache.xalan.internal.res;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.util.ListResourceBundle;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;

public class XSLMessages extends XPATHMessages
{
    private static ListResourceBundle XSLTBundle;
    private static final String XSLT_ERROR_RESOURCES = "com.sun.org.apache.xalan.internal.res.XSLTErrorResources";
    
    public static String createMessage(final String msgKey, final Object[] args) {
        if (XSLMessages.XSLTBundle == null) {
            XSLMessages.XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
        }
        if (XSLMessages.XSLTBundle != null) {
            return XMLMessages.createMsg(XSLMessages.XSLTBundle, msgKey, args);
        }
        return "Could not load any resource bundles.";
    }
    
    public static String createWarning(final String msgKey, final Object[] args) {
        if (XSLMessages.XSLTBundle == null) {
            XSLMessages.XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
        }
        if (XSLMessages.XSLTBundle != null) {
            return XMLMessages.createMsg(XSLMessages.XSLTBundle, msgKey, args);
        }
        return "Could not load any resource bundles.";
    }
    
    static {
        XSLMessages.XSLTBundle = null;
    }
}
