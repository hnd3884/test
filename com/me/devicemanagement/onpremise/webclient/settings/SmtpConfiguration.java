package com.me.devicemanagement.onpremise.webclient.settings;

import java.util.Hashtable;
import javax.mail.Transport;
import com.me.devicemanagement.framework.server.mailmanager.MailProcessor;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.mailmanager.MailerUtils;
import org.json.JSONException;
import java.util.Properties;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SmtpConfiguration
{
    private Logger logger;
    private static final String MAIL_AUTH_ERROR_URL = "mailAuthErrorUrl";
    public JSONObject mailFailureDetails;
    
    public SmtpConfiguration() {
        this.logger = Logger.getLogger(SmtpConfiguration.class.getName());
        this.mailFailureDetails = new JSONObject();
    }
    
    public boolean checkMailServer(final Properties props) throws JSONException {
        final String smtpServer = ((Hashtable<K, String>)props).get("smtpHost");
        final String smtpPort = ((Hashtable<K, Object>)props).get("smtpPort") + "";
        final String userName = ((Hashtable<K, String>)props).get("smtpUserName");
        final String password = ((Hashtable<K, String>)props).get("smtpPassword");
        final boolean isTLSEnabled = Boolean.valueOf(((Hashtable<K, Object>)props).get("tlsEnabled").toString());
        final boolean isSmtpsEnabled = Boolean.valueOf(((Hashtable<K, Object>)props).get("smtpsEnabled").toString());
        final boolean isValidateUser = Boolean.valueOf(((Hashtable<K, Object>)props).get("needAuthentication").toString());
        final String senderAddress = ((Hashtable<K, String>)props).get("senderAddress");
        String toAddress = ((Hashtable<K, String>)props).get("toAddress");
        if (toAddress == null || toAddress.equals("")) {
            toAddress = senderAddress;
        }
        final boolean checkMailServer = this.checkMailServer(smtpServer, smtpPort, userName, password, isTLSEnabled, isSmtpsEnabled, isValidateUser, senderAddress, toAddress);
        return checkMailServer;
    }
    
    private boolean checkMailServer(final String smtpServer, final String smtpPort, String smtpUserName, String smtpPassword, final boolean isTlsEnabled, final boolean isSmtpsEnabled, final boolean isValidateUser, final String senderAddress, final String toAddress) throws JSONException {
        boolean returnCode = false;
        final Transport transport = null;
        try {
            final int port = 25;
            if (smtpServer != null) {
                final Properties mailProps = MailerUtils.getInstance().gerMailServerPorperties(smtpServer, smtpPort, smtpUserName, smtpPassword, isTlsEnabled, isSmtpsEnabled);
                if (!isValidateUser) {
                    smtpUserName = null;
                    smtpPassword = null;
                    ((Hashtable<String, String>)mailProps).put("mail.smtp.auth", "false");
                    mailProps.remove("mail.smtp.user");
                    mailProps.remove("mail.smtp.user");
                    mailProps.remove("mail.smtp.password");
                }
                this.logger.log(Level.INFO, "Checking the mail server configuration...");
                MailerUtils.getInstance().validateSMTPSSSLCertificate(isSmtpsEnabled, smtpServer, smtpPort);
                final String description = getMailDescription(smtpServer, smtpPort, isTlsEnabled, isSmtpsEnabled);
                final MailDetails mailDetails = new MailDetails(senderAddress, toAddress);
                mailDetails.senderDisplayName = senderAddress;
                mailDetails.subject = MailerUtils.getInstance().getTestMailSub();
                mailDetails.bodyContent = description;
                mailDetails.attachment = null;
                this.mailFailureDetails = MailProcessor.getInstance().sendMail(mailDetails, mailProps);
                final boolean status = this.mailFailureDetails.getBoolean("Status");
                if (status) {
                    returnCode = true;
                }
            }
            else {
                this.logger.log(Level.INFO, "Mail Server Name : " + smtpServer + " Port :" + port);
                returnCode = false;
            }
        }
        finally {
            if (transport != null) {
                try {
                    transport.close();
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "Exception in Closing Connection:", e);
                }
            }
        }
        return returnCode;
    }
    
    private static String getMailDescription(final String smtpServer, final String smtpPort, final boolean isTlsEnabled, final boolean isSmtpsEnabled) {
        final StringBuffer stf = new StringBuffer();
        stf.append(" &nbsp;<div>\t");
        stf.append("<table border=\"0\" width=\"60%\" cellpadding=\"0\" cellspacing=\"0\" style=\"color: rgb(0, 0, 0); font-size: 13px; border: 1px solid rgb(245, 245, 245); font-family: lato,Roboto; background-color: rgb(250, 250, 250);\">");
        stf.append("<tbody>");
        stf.append("<tr>");
        stf.append("<td style=\"padding: 10px 30px; color: rgb(255, 255, 255); background-color: rgb(153, 153, 153);\"><span style=\"font-size: 16px; font-weight: bold;\">" + MailerUtils.getInstance().getTestMailSub() + "</span>&nbsp;</td>");
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td>");
        stf.append("<div style=\"font-size: 13px; padding: 20px;\">");
        stf.append("<table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" width=\"100%\" style=\"color: rgb(0, 0, 0); font-size: 13px; border: 1px solid rgb(245, 245, 245); font-family: lato,Roboto;\">");
        stf.append("<tbody>");
        stf.append("<tr>");
        stf.append("<td width=\"50%\" style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Server Name</td>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">" + smtpServer + "</td>");
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Server Port</td>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">" + smtpPort + "</td>");
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Email Type</td>");
        if (isSmtpsEnabled) {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">SMTPS</td>");
        }
        else {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">SMTP</td>");
        }
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Enable TLS </td>");
        if (isTlsEnabled) {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">Yes</td>");
        }
        else {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">No</td>");
        }
        stf.append("</tr>");
        stf.append("</tbody>");
        stf.append("</table>");
        stf.append("</div>");
        stf.append("</td>");
        stf.append("</tr>");
        stf.append("</tbody>");
        stf.append("</table>");
        stf.append("</div>");
        return stf.toString();
    }
}
