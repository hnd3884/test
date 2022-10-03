package com.me.devicemanagement.framework.server.mailmanager;

import java.util.Properties;
import org.json.JSONObject;
import java.util.Hashtable;

public interface MailSettingsAPI
{
    boolean isMailServerConfigured();
    
    Hashtable<String, String> getMailSenderDetails() throws Exception;
    
    JSONObject sendMail(final MailDetails p0) throws Exception;
    
    void addToMailQueue(final MailDetails p0, final int p1);
    
    String appendFooterNote(final String p0, final Properties p1);
    
    Properties getMailServerDetailsProps() throws Exception;
}
