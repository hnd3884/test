package com.me.devicemanagement.onpremise.start.servertroubleshooter.util;

import java.util.Hashtable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.zoho.framework.utils.crypto.EnDecrypt;
import javax.mail.Transport;
import javax.mail.Message;
import java.io.File;
import java.util.Date;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import java.util.Properties;
import java.util.logging.Logger;

public class ServerTroubleshooterMailerUtil
{
    private static Logger logger;
    private static Properties mailProperties;
    static String mailSettingConf;
    
    public static void sendMail(final String message, final String subject) {
        String mailSentStatus = "";
        if (ServerTroubleshooterMailerUtil.mailProperties != null && !ServerTroubleshooterMailerUtil.mailProperties.isEmpty()) {
            if (ServerTroubleshooterMailerUtil.mailProperties.getProperty("isEmailAlertEnabled").equalsIgnoreCase("true")) {
                try {
                    final EnDecrypt cryptInstance = (EnDecrypt)new EnDecryptAES256Impl();
                    CryptoUtil.setEnDecryptInstance(cryptInstance);
                    final Properties properties = new Properties();
                    String smtpHost = CryptoUtil.decrypt(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpHost"));
                    if (smtpHost.equals(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpHost"))) {
                        smtpHost = CryptoUtil.decrypt(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpHost"), 2);
                    }
                    final boolean istls = Boolean.valueOf(ServerTroubleshooterMailerUtil.mailProperties.getProperty("tlsEnabled"));
                    ((Hashtable<String, String>)properties).put("mail.smtp.host", smtpHost);
                    ((Hashtable<String, String>)properties).put("mail.smtp.starttls.enable", istls + "");
                    String port = CryptoUtil.decrypt(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpPort"));
                    if (port.equals(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpPort"))) {
                        port = CryptoUtil.decrypt(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpPort"), 2);
                    }
                    ((Hashtable<String, String>)properties).put("mail.smtp.port", port);
                    if (istls) {
                        ((Hashtable<String, String>)properties).put("mail.smtp.ssl.trust", smtpHost);
                    }
                    if (ServerTroubleshooterMailerUtil.mailProperties.getProperty("senderAddress") != null && !ServerTroubleshooterMailerUtil.mailProperties.getProperty("senderAddress").equalsIgnoreCase("")) {
                        ((Hashtable<String, String>)properties).put("mail.smtp.mail.sender", ServerTroubleshooterMailerUtil.mailProperties.getProperty("senderAddress"));
                    }
                    else {
                        ((Hashtable<String, String>)properties).put("mail.smtp.mail.sender", "admin@desktopcentral.com");
                    }
                    ((Hashtable<String, String>)properties).put("mail.smtp.user", ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpUserName"));
                    ((Hashtable<String, String>)properties).put("mail.smtp.auth", ServerTroubleshooterMailerUtil.mailProperties.getProperty("needAuthentication"));
                    Session session = null;
                    if (ServerTroubleshooterMailerUtil.mailProperties.getProperty("needAuthentication").equalsIgnoreCase("true")) {
                        session = Session.getDefaultInstance(properties, (Authenticator)new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                try {
                                    String smtpPassword = CryptoUtil.decrypt(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpPassword"));
                                    if (smtpPassword.equals(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpPassword"))) {
                                        smtpPassword = CryptoUtil.decrypt(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpPassword"), 2);
                                    }
                                    return new PasswordAuthentication(ServerTroubleshooterMailerUtil.mailProperties.getProperty("smtpUserName"), smtpPassword);
                                }
                                catch (final Exception e) {
                                    ServerTroubleshooterMailerUtil.logger.info("Password Authentication Exception : " + e);
                                    return null;
                                }
                            }
                        });
                    }
                    else {
                        session = Session.getDefaultInstance(properties, (Authenticator)null);
                    }
                    ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Mail Properties = " + ServerTroubleshooterMailerUtil.mailProperties);
                    ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Properties = " + properties);
                    final String[] emailId = ServerTroubleshooterMailerUtil.mailProperties.getProperty("receiverAddress").split(",");
                    final Message msg = (Message)new MimeMessage(session);
                    if (ServerTroubleshooterMailerUtil.mailProperties.getProperty("senderAddress") != null && !ServerTroubleshooterMailerUtil.mailProperties.getProperty("senderAddress").equalsIgnoreCase("")) {
                        msg.setFrom((Address)new InternetAddress(ServerTroubleshooterMailerUtil.mailProperties.getProperty("senderAddress")));
                    }
                    else {
                        msg.setFrom((Address)new InternetAddress("admin@desktopcentral.com"));
                    }
                    msg.setSubject(subject);
                    msg.setSentDate(new Date());
                    final String previousMailContentFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "user-conf" + File.separator + "serverFailureSolution.html";
                    if (new File(previousMailContentFile).exists()) {
                        final String fileContent = getFileContent(previousMailContentFile);
                        final String msgWithoutWhiteSpace = message.replaceAll("\\s+", "");
                        final String fileContentWithoutWhiteSpace = fileContent.replaceAll("\\s+", "");
                        if (!msgWithoutWhiteSpace.equals(fileContentWithoutWhiteSpace)) {
                            updateFileContent(previousMailContentFile, message);
                            for (final String mailId : emailId) {
                                msg.setContent((Object)message, "text/html");
                                msg.setRecipient(Message.RecipientType.TO, (Address)new InternetAddress(mailId));
                                Transport.send(msg);
                                mailSentStatus = "true";
                                ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Mail successfully sent to " + mailId);
                            }
                        }
                        else if (msgWithoutWhiteSpace.equals(fileContentWithoutWhiteSpace)) {
                            ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "message equals..");
                            if (System.currentTimeMillis() - new File(previousMailContentFile).lastModified() >= 86400000L) {
                                updateFileContent(previousMailContentFile, message);
                                for (final String mailId : emailId) {
                                    msg.setContent((Object)message, "text/html");
                                    msg.setRecipient(Message.RecipientType.TO, (Address)new InternetAddress(mailId));
                                    Transport.send(msg);
                                    mailSentStatus = "true";
                                    ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Mail successfully sent to " + mailId);
                                }
                            }
                            else if (ServerTroubleshooterMailerUtil.mailProperties.containsKey("mailSentStatus")) {
                                if (ServerTroubleshooterMailerUtil.mailProperties.getProperty("mailSentStatus").equalsIgnoreCase("false")) {
                                    ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Previous mail was not sent properly due to some reasons...");
                                    updateFileContent(previousMailContentFile, message);
                                    for (final String mailId : emailId) {
                                        msg.setContent((Object)message, "text/html");
                                        msg.setRecipient(Message.RecipientType.TO, (Address)new InternetAddress(mailId));
                                        Transport.send(msg);
                                        mailSentStatus = "true";
                                        ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Mail successfully sent to " + mailId);
                                    }
                                }
                                mailSentStatus = "true";
                            }
                        }
                    }
                    else {
                        updateFileContent(previousMailContentFile, message);
                        for (final String mailId2 : emailId) {
                            msg.setContent((Object)message, "text/html");
                            msg.setRecipient(Message.RecipientType.TO, (Address)new InternetAddress(mailId2));
                            Transport.send(msg);
                            mailSentStatus = "true";
                            ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Mail successfully sent to " + mailId2);
                        }
                    }
                }
                catch (final Exception e) {
                    ServerTroubleshooterMailerUtil.logger.log(Level.SEVERE, "Exception occurred while sending mail from overloaded sendMail().. " + e);
                    e.printStackTrace();
                    mailSentStatus = "false";
                }
            }
            ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "mail status = " + mailSentStatus);
            ServerTroubleshooterUtil.getInstance().setPropertyValueToFile(ServerTroubleshooterMailerUtil.mailSettingConf, "mailSentStatus", mailSentStatus);
            ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Server startup failure mail sent for the reason : " + subject);
        }
    }
    
    private static void updateFileContent(final String fileName, final String fileContent) throws IOException {
        try {
            ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Creating new File...");
            if (!new File(fileName).exists()) {
                final String dir = fileName.substring(0, fileName.lastIndexOf(File.separator));
                if (!new File(dir).exists()) {
                    new File(dir).mkdir();
                    ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Creating new Dir...");
                }
                new File(fileName).createNewFile();
                ServerTroubleshooterMailerUtil.logger.log(Level.INFO, "Creating new File...");
            }
            final FileWriter fw = new FileWriter(new File(fileName).getAbsoluteFile());
            final BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileContent);
            bw.close();
        }
        catch (final Exception e) {
            ServerTroubleshooterMailerUtil.logger.log(Level.SEVERE, "Exception occurred while updating the file content ..", e);
            throw e;
        }
    }
    
    private static String getFileContent(final String fileName) throws IOException {
        final StringBuffer fileContent = new StringBuffer();
        try {
            BufferedReader br = null;
            String line = "";
            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                fileContent.append(line);
            }
            return fileContent.toString();
        }
        catch (final Exception e) {
            ServerTroubleshooterMailerUtil.logger.log(Level.SEVERE, "Exception while getting file contents..", e);
            throw e;
        }
    }
    
    static {
        ServerTroubleshooterMailerUtil.logger = Logger.getLogger("ServerTroubleshooterLogger");
        ServerTroubleshooterMailerUtil.mailProperties = new Properties();
        ServerTroubleshooterMailerUtil.mailSettingConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "user-conf" + File.separator + "mail-settings.props";
        if (new File(ServerTroubleshooterMailerUtil.mailSettingConf).exists()) {
            ServerTroubleshooterMailerUtil.mailProperties = ServerTroubleshooterUtil.getProperties(ServerTroubleshooterMailerUtil.mailSettingConf);
        }
    }
}
