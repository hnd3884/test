package com.me.devicemanagement.onpremise.server.mail;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.mailmanager.MailContentGeneratorUtil;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.io.File;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.mailmanager.MailerUtils;
import com.me.ems.onpremise.common.oauth.OauthUtil;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.mailmanager.MailSettingsAPI;
import com.me.devicemanagement.framework.server.mailmanager.MailProcessor;

public class MailSettingsImpl extends MailProcessor implements MailSettingsAPI
{
    private static final Logger LOGGER;
    
    public boolean isMailServerConfigured() {
        try {
            final Properties mailProps = this.getMailServerDetailsProps();
            if (mailProps.get("mail.smtp.host") != null && mailProps.get("mail.smtp.host") != "") {
                return true;
            }
        }
        catch (final Exception e) {
            MailSettingsImpl.LOGGER.log(Level.SEVERE, "Exception occurred in MailProcessor.isMailServerConfigured() ", e);
        }
        return false;
    }
    
    public Properties getMailServerDetailsProps() throws Exception {
        Properties mailProps = null;
        try {
            String host = "";
            String userName = "";
            String password = "";
            String fromName = "";
            String fromAddress = "";
            Long oauthCredentialId = null;
            Integer authType = null;
            int port = 25;
            final SelectQuery smtpquery = (SelectQuery)new SelectQueryImpl(Table.getTable("SmtpConfiguration"));
            smtpquery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject smtpDO = SyMUtil.getPersistence().get(smtpquery);
            boolean isTLSEnabled = false;
            boolean isSMTPSSEnabled = false;
            boolean useProxy = false;
            if (!smtpDO.isEmpty()) {
                host = (String)smtpDO.getFirstValue("SmtpConfiguration", "SERVERNAME");
                final Integer portInt = (Integer)smtpDO.getFirstValue("SmtpConfiguration", "PORT");
                port = portInt;
                userName = (String)smtpDO.getFirstValue("SmtpConfiguration", "USERNAME");
                isTLSEnabled = (boolean)smtpDO.getFirstValue("SmtpConfiguration", "IS_TLS_ENABLED");
                MailSettingsImpl.LOGGER.log(Level.INFO, "ServerName :   " + host + "\n Port    " + portInt);
                isSMTPSSEnabled = (boolean)smtpDO.getFirstValue("SmtpConfiguration", "IS_SMTPS_ENABLED");
                MailSettingsImpl.LOGGER.log(Level.INFO, "ServerName :   " + host + "\n Port    " + portInt);
                fromName = (String)smtpDO.getFirstValue("SmtpConfiguration", "SENDER_NAME");
                fromAddress = (String)smtpDO.getFirstValue("SmtpConfiguration", "SENDER_ADDRESS");
                authType = (Integer)smtpDO.getFirstValue("SmtpConfiguration", "AUTH_TYPE");
                if (authType == 0) {
                    password = Encoder.convertFromBase((String)smtpDO.getFirstValue("SmtpConfiguration", "PASSWORD"));
                }
                else {
                    oauthCredentialId = (Long)smtpDO.getFirstValue("SmtpConfiguration", "CREDENTIAL_ID");
                    useProxy = (boolean)smtpDO.getFirstValue("SmtpConfiguration", "USE_PROXY");
                    password = OauthUtil.getInstance().getAccessTokenFromDb(oauthCredentialId, useProxy);
                }
            }
            mailProps = MailerUtils.getInstance().gerMailServerPorperties(host, new Integer(port).toString(), userName, password, isTLSEnabled, isSMTPSSEnabled);
            if (authType != null && authType == 1) {
                ((Hashtable<String, String>)mailProps).put("mail.smtp.auth.mechanisms", "XOAUTH2");
            }
            if (fromName != null && !fromName.isEmpty() && !fromName.equals("--")) {
                ((Hashtable<String, String>)mailProps).put("mail.fromName", fromName);
            }
            else {
                ((Hashtable<String, String>)mailProps).put("mail.fromName", "noreply");
            }
            if (fromAddress != null && !fromAddress.isEmpty() && !fromAddress.equals("--")) {
                ((Hashtable<String, String>)mailProps).put("mail.fromAddress", fromAddress);
            }
            else {
                ((Hashtable<String, String>)mailProps).put("mail.fromAddress", "noreply@zohocorp.com");
            }
            return mailProps;
        }
        catch (final Exception ex) {
            MailSettingsImpl.LOGGER.log(Level.SEVERE, "Exception while getting mail details in MailSettingsImpl class... " + ex);
            return mailProps;
        }
    }
    
    public void addToMailQueue(final MailDetails mailDetails, final int priority) {
        MailHandler.getInstance().addToMailQueue(mailDetails, priority);
    }
    
    public String appendFooterNote(String description, final Properties props) {
        if (props.containsKey("$productName$") && props.containsKey("$baseUrl$")) {
            final String prodName = props.getProperty("$productName$");
            final String hostName = props.getProperty("$baseUrl$");
            try {
                final String xslfile = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "xsl" + File.separator + "ServerInfo.xsl";
                final String disclaimer = I18N.getMsg("dc.admin.backup_failed.mail.footer", new Object[] { hostName, prodName });
                final String root = "server";
                final HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("disclaimer", disclaimer);
                final String rootElement = "systemparams";
                final String xmlData = new AlertMailGeneratorUtil().generateXMLFromHashMap((HashMap)hashMap, root, rootElement);
                final String footer = new MailContentGeneratorUtil().getHTMLContentFromXML(xslfile, xmlData);
                description += footer;
            }
            catch (final Exception e) {
                MailSettingsImpl.LOGGER.log(Level.INFO, "Exception while appending on-premise email template: ", e);
            }
        }
        return description;
    }
    
    static {
        LOGGER = Logger.getLogger(MailSettingsImpl.class.getName());
    }
}
