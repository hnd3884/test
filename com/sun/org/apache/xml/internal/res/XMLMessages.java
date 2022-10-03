package com.sun.org.apache.xml.internal.res;

import java.text.MessageFormat;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.util.ListResourceBundle;
import java.util.Locale;

public class XMLMessages
{
    protected Locale fLocale;
    private static ListResourceBundle XMLBundle;
    private static final String XML_ERROR_RESOURCES = "com.sun.org.apache.xml.internal.res.XMLErrorResources";
    protected static final String BAD_CODE = "BAD_CODE";
    protected static final String FORMAT_FAILED = "FORMAT_FAILED";
    
    public XMLMessages() {
        this.fLocale = Locale.getDefault();
    }
    
    public void setLocale(final Locale locale) {
        this.fLocale = locale;
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    public static final String createXMLMessage(final String msgKey, final Object[] args) {
        if (XMLMessages.XMLBundle == null) {
            XMLMessages.XMLBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xml.internal.res.XMLErrorResources");
        }
        if (XMLMessages.XMLBundle != null) {
            return createMsg(XMLMessages.XMLBundle, msgKey, args);
        }
        return "Could not load any resource bundles.";
    }
    
    public static final String createMsg(final ListResourceBundle fResourceBundle, final String msgKey, final Object[] args) {
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
        XMLMessages.XMLBundle = null;
    }
}
