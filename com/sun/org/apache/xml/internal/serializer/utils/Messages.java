package com.sun.org.apache.xml.internal.serializer.utils;

import java.text.MessageFormat;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.util.ListResourceBundle;
import java.util.Locale;

public final class Messages
{
    private final Locale m_locale;
    private ListResourceBundle m_resourceBundle;
    private String m_resourceBundleName;
    
    Messages(final String resourceBundle) {
        this.m_locale = Locale.getDefault();
        this.m_resourceBundleName = resourceBundle;
    }
    
    private Locale getLocale() {
        return this.m_locale;
    }
    
    public final String createMessage(final String msgKey, final Object[] args) {
        if (this.m_resourceBundle == null) {
            this.m_resourceBundle = SecuritySupport.getResourceBundle(this.m_resourceBundleName);
        }
        if (this.m_resourceBundle != null) {
            return this.createMsg(this.m_resourceBundle, msgKey, args);
        }
        return "Could not load the resource bundles: " + this.m_resourceBundleName;
    }
    
    private final String createMsg(final ListResourceBundle fResourceBundle, String msgKey, final Object[] args) {
        String fmsg = null;
        boolean throwex = false;
        String msg = null;
        if (msgKey != null) {
            msg = fResourceBundle.getString(msgKey);
        }
        else {
            msgKey = "";
        }
        if (msg == null) {
            throwex = true;
            try {
                msg = MessageFormat.format("BAD_MSGKEY", msgKey, this.m_resourceBundleName);
            }
            catch (final Exception e) {
                msg = "The message key '" + msgKey + "' is not in the message class '" + this.m_resourceBundleName + "'";
            }
        }
        else if (args != null) {
            try {
                for (int n = args.length, i = 0; i < n; ++i) {
                    if (null == args[i]) {
                        args[i] = "";
                    }
                }
                fmsg = MessageFormat.format(msg, args);
            }
            catch (final Exception e) {
                throwex = true;
                try {
                    fmsg = MessageFormat.format("BAD_MSGFORMAT", msgKey, this.m_resourceBundleName);
                    fmsg = fmsg + " " + msg;
                }
                catch (final Exception formatfailed) {
                    fmsg = "The format of message '" + msgKey + "' in message class '" + this.m_resourceBundleName + "' failed.";
                }
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
}
