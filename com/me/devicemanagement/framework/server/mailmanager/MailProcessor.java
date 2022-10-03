package com.me.devicemanagement.framework.server.mailmanager;

import java.util.Iterator;
import java.net.UnknownHostException;
import javax.mail.internet.AddressException;
import com.sun.mail.smtp.SMTPAddressFailedException;
import javax.mail.AuthenticationFailedException;
import com.sun.mail.smtp.SMTPSendFailedException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLException;
import java.net.SocketException;
import java.net.ConnectException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONException;
import javax.mail.Multipart;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import javax.mail.Transport;
import javax.mail.internet.MimeUtility;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.io.FileInputStream;
import java.io.File;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import org.json.JSONObject;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class MailProcessor
{
    private static MailProcessor processObj;
    private static Logger logger;
    private String mailExceptionMessage;
    private String mailExceptionClass;
    
    public MailProcessor() {
        this.mailExceptionMessage = "notfound";
        this.mailExceptionClass = "";
    }
    
    public static MailProcessor getInstance() {
        if (MailProcessor.processObj == null) {
            MailProcessor.processObj = new MailProcessor();
        }
        return MailProcessor.processObj;
    }
    
    private MailCallBackHandler getMailCallBackHandler(final MailDetails mailData) throws Exception {
        MailCallBackHandler mailCallBackHandler = null;
        if (mailData.callBackHandler == null) {
            mailCallBackHandler = ApiFactoryProvider.getMailCallBackHandler();
        }
        else {
            try {
                mailCallBackHandler = (MailCallBackHandler)Class.forName(mailData.callBackHandler).newInstance();
            }
            catch (final ClassNotFoundException e) {
                MailProcessor.logger.log(Level.SEVERE, " Custom MailCallBackHandler not found ... : {0} Using DefaultMailCallBackHandler", e);
                mailCallBackHandler = ApiFactoryProvider.getMailCallBackHandler();
            }
        }
        return mailCallBackHandler;
    }
    
    protected Properties getMailServerDetailsProps() throws Exception {
        return null;
    }
    
    public Hashtable<String, String> getMailSenderDetails() {
        Hashtable<String, String> sender = null;
        try {
            sender = new Hashtable<String, String>();
            final Properties smtpProps = this.getMailServerDetailsProps();
            sender.put("mail.fromAddress", smtpProps.getProperty("mail.fromAddress"));
            sender.put("mail.fromName", smtpProps.getProperty("mail.fromName"));
        }
        catch (final Exception e) {
            MailProcessor.logger.log(Level.SEVERE, "Exception occurred in MailProcessor.getMailSenderDetails() ", e);
        }
        return sender;
    }
    
    public JSONObject sendMail(final MailDetails mailobj) throws Exception {
        final MailCallBackHandler mailCallBackHandler = this.getMailCallBackHandler(mailobj);
        final Properties mailProps = this.getMailServerDetailsProps();
        final JSONObject mailStatusDetails = this.sendMail(mailobj, mailProps);
        if (mailCallBackHandler != null) {
            final boolean status = mailStatusDetails.getBoolean("Status");
            if (status) {
                mailCallBackHandler.handleSuccessfulCompletion(mailStatusDetails);
            }
            else if (mailStatusDetails.has("mailErrorCode")) {
                mailCallBackHandler.handleMailSendingFailure(mailStatusDetails);
            }
        }
        return mailStatusDetails;
    }
    
    public JSONObject sendMail(final MailDetails mailobj, final Properties mailProps) throws JSONException {
        int filelen = 0;
        if (mailobj.attachment != null) {
            filelen = mailobj.attachment.length;
        }
        final String[] attach = new String[filelen];
        MailProcessor.logger.log(Level.INFO, "Entering into sendMail Method .... ");
        String fromAddr = mailobj.fromAddress;
        final String toAddr = mailobj.toAddress;
        final String ccAddr = mailobj.ccAddress;
        final String subj = mailobj.subject;
        final String bodycont = mailobj.bodyContent;
        for (int i = 0; i < filelen; ++i) {
            attach[i] = mailobj.attachment[i];
        }
        final Properties props = new Properties();
        if (fromAddr == null || fromAddr.equals("")) {
            fromAddr = "admin@manageengine.com";
        }
        try {
            final String userName = mailProps.getProperty("mail.smtp.user");
            final String password = mailProps.getProperty("mail.smtp.password");
            final MailServerAuthenticator authenticator = new MailServerAuthenticator(userName, password);
            final Session session = Session.getInstance(mailProps, (Authenticator)authenticator);
            session.setDebug(false);
            final MimeMessage message = new MimeMessage(session);
            InternetAddress ia = null;
            try {
                ia = new InternetAddress(fromAddr);
                final String fromName = mailobj.senderDisplayName;
                if (fromName == null || fromName.equals("")) {
                    ia.setPersonal(fromAddr);
                }
                else {
                    ia.setPersonal(fromName, "UTF-8");
                }
            }
            catch (final Exception ue) {
                MailProcessor.logger.log(Level.INFO, "Caught exception in setting from Address", ue);
            }
            InputStream attStream = null;
            try {
                if (ia != null && toAddr != null) {
                    message.setFrom((Address)ia);
                    final InternetAddress[] tos = InternetAddress.parse(toAddr);
                    for (final InternetAddress too : tos) {
                        too.setPersonal(too.getPersonal(), "UTF-8");
                    }
                    message.addRecipients(Message.RecipientType.TO, (Address[])tos);
                    if (ccAddr != null) {
                        final InternetAddress[] ccs = InternetAddress.parse(ccAddr);
                        for (final InternetAddress cc : ccs) {
                            cc.setPersonal(cc.getPersonal(), "UTF-8");
                        }
                        message.addRecipients(Message.RecipientType.CC, (Address[])ccs);
                    }
                    if (subj != null) {
                        message.setSubject(subj, "UTF-8");
                    }
                    final MimeBodyPart msgBodyPart = new MimeBodyPart();
                    final Multipart mp = (Multipart)new MimeMultipart();
                    if (bodycont != null) {
                        msgBodyPart.setContent((Object)bodycont, "text/html; charset=utf-8");
                        mp.addBodyPart((BodyPart)msgBodyPart);
                        message.setContent(mp);
                        MailProcessor.logger.log(Level.INFO, "Body content is added successfully");
                    }
                    final MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();
                    boolean isFileExists = false;
                    boolean isDirectory = false;
                    for (int j = 0; j < attach.length; ++j) {
                        final String att = attach[j];
                        final File file = new File(att);
                        if (file.exists()) {
                            isFileExists = true;
                        }
                        isDirectory = file.isDirectory();
                        if (att != null && !att.equals("") && isFileExists && !isDirectory) {
                            final MimeBodyPart mbp2 = new MimeBodyPart();
                            final String attFileName = file.getName();
                            final String i18nFileName = new String(attFileName.getBytes(), "UTF-8");
                            final String mimeType = mimeMap.getContentType(attFileName);
                            attStream = new FileInputStream(att);
                            final ByteArrayDataSource bas = new ByteArrayDataSource(attStream, mimeType);
                            mbp2.setContentID("<" + attFileName.replaceAll("\\s+", "") + ">");
                            mbp2.setDataHandler(new DataHandler((DataSource)bas));
                            mbp2.setFileName(MimeUtility.encodeText(i18nFileName));
                            mp.addBodyPart((BodyPart)mbp2);
                            if (attStream != null) {
                                attStream.close();
                            }
                        }
                    }
                    this.addInlineImageAttachments(mailobj, mp);
                    MailProcessor.logger.log(Level.INFO, "SMTP Info: Starting to send mail to the corresponding address ... ");
                    Transport.send((Message)message, message.getAllRecipients());
                    MailProcessor.logger.log(Level.INFO, "SMTP Info: Mail Send Successfully to the Address " + this.getMaskedMailAddress(toAddr) + "by Sender" + this.getMaskedMailAddress(fromAddr));
                    SyMUtil.deleteSyMParameter("MAIL_CONFIG_ERROR");
                }
            }
            catch (final Exception ex) {
                MailProcessor.logger.log(Level.SEVERE, "SMTP Exception: Unable to Send Mail to the Address " + toAddr + " by Sender " + fromAddr);
                MailProcessor.logger.log(Level.INFO, "SMTP Exception: Caught exception in setting mail ", ex);
                JSONObject mailFailureDetails = mailobj.additionalParams;
                mailFailureDetails.put("senderEmailAddress", (Object)fromAddr);
                mailFailureDetails.put("recepientEmailAddress", (Object)toAddr);
                mailFailureDetails = this.handleMailServerException(ex, mailFailureDetails, mailProps);
                mailFailureDetails.put("mailExceptionMessage", (Object)this.mailExceptionMessage);
                mailFailureDetails.put("mailExceptionClass", (Object)this.mailExceptionClass);
                mailFailureDetails.put("Status", false);
                return mailFailureDetails;
            }
            finally {
                try {
                    if (attStream != null) {
                        attStream.close();
                    }
                }
                catch (final Exception e) {
                    MailProcessor.logger.log(Level.WARNING, "Exception while closing stream", e);
                }
            }
        }
        catch (final Exception ee) {
            MailProcessor.logger.log(Level.SEVERE, "Unable to Send Mail to the Address " + toAddr + " by Sender " + fromAddr);
            MailProcessor.logger.log(Level.INFO, "Caught exception in setting from Address", ee);
            final JSONObject mailFailureDetails2 = mailobj.additionalParams;
            mailFailureDetails2.put("senderEmailAddress", (Object)fromAddr);
            mailFailureDetails2.put("recepientEmailAddress", (Object)toAddr);
            mailFailureDetails2.put("Status", false);
            return mailFailureDetails2;
        }
        final JSONObject mailFailureDetails3 = mailobj.additionalParams;
        mailFailureDetails3.put("senderEmailAddress", (Object)fromAddr);
        mailFailureDetails3.put("recepientEmailAddress", (Object)toAddr);
        mailFailureDetails3.put("Status", true);
        SyMUtil.deleteSyMParameter("MAIL_CONFIG_ERROR");
        return mailFailureDetails3;
    }
    
    private String getMaskedMailAddress(final String emailAddress) {
        try {
            final String[] emailComponents = emailAddress.split("@");
            final String username = emailComponents[0].replaceAll("(?<=.{3}).(?=.{3})", "*");
            String domain = emailComponents[1].substring(0, emailComponents[1].lastIndexOf(46));
            final String tld = emailComponents[1].substring(emailComponents[1].lastIndexOf(46));
            domain = domain.replaceAll("(?<=.{2}).(?=.{2})", "*");
            return username + "@" + domain + tld;
        }
        catch (final Exception e) {
            return emailAddress;
        }
    }
    
    private String getMessageForThrowable(final Throwable[] stackTraceElements, final Exception ex, final Class<?> clazz) {
        final int found = ExceptionUtils.indexOfThrowable((Throwable)ex, (Class)clazz);
        if (found != -1) {
            final Throwable element = stackTraceElements[found];
            this.mailExceptionMessage = element.getMessage();
            this.mailExceptionClass = clazz.getSimpleName();
            return element.getMessage() + "";
        }
        return "notfound";
    }
    
    protected JSONObject handleMailServerException(final Exception ex, final JSONObject mailFailureDetails, final Properties mailProps) throws Exception {
        int errorCode = 40013;
        final Throwable[] stackTraceElements = ExceptionUtils.getThrowables((Throwable)ex);
        String elementMessage = this.getMessageForThrowable(stackTraceElements, ex, ConnectException.class);
        if (!elementMessage.equalsIgnoreCase("notfound")) {
            errorCode = 40000;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, SocketException.class);
        if (elementMessage.toLowerCase().contains("permission denied")) {
            errorCode = 40001;
        }
        else if (!elementMessage.equalsIgnoreCase("notfound")) {
            errorCode = 40000;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, SSLException.class);
        if (elementMessage.toLowerCase().contains("unrecognized ssl message")) {
            errorCode = 40006;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, IllegalArgumentException.class);
        if (elementMessage.toLowerCase().contains("port")) {
            errorCode = 40000;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, SocketTimeoutException.class);
        if (elementMessage.toLowerCase().contains("timed out")) {
            errorCode = 40003;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, SMTPSendFailedException.class);
        if (elementMessage.toLowerCase().contains("authenticat")) {
            if (mailProps.getProperty("mail.smtp.auth").equalsIgnoreCase("true")) {
                errorCode = 40002;
            }
            else {
                errorCode = 40009;
            }
        }
        else if (elementMessage.toLowerCase().contains("permission")) {
            errorCode = 40004;
        }
        else if (elementMessage.toLowerCase().contains("access denied")) {
            errorCode = 40005;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, AuthenticationFailedException.class);
        if (!elementMessage.equalsIgnoreCase("notfound")) {
            if (mailProps.getProperty("mail.smtp.auth").equalsIgnoreCase("true")) {
                if (mailProps.getProperty("mail.smtp.host").equalsIgnoreCase("smtp.gmail.com")) {
                    errorCode = 40008;
                }
                else if (mailProps.getProperty("mail.smtp.host").equalsIgnoreCase("smtp.office365.com")) {
                    errorCode = 40015;
                }
                else {
                    errorCode = 40002;
                }
            }
            else if (elementMessage.toLowerCase().contains("no authentication mechansims")) {
                errorCode = 40014;
            }
            else {
                errorCode = 40009;
            }
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, SMTPAddressFailedException.class);
        if (elementMessage.toLowerCase().contains("relay")) {
            errorCode = 40005;
        }
        else if (elementMessage.toLowerCase().contains("access denied")) {
            if (mailProps.getProperty("mail.smtp.auth").equalsIgnoreCase("true")) {
                errorCode = 40002;
            }
            else {
                errorCode = 40009;
            }
        }
        else if (elementMessage.toLowerCase().contains("sender address rejected")) {
            errorCode = 40007;
        }
        else if (elementMessage.toLowerCase().contains("recipient address rejected")) {
            errorCode = 40010;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, AddressException.class);
        if (elementMessage.toLowerCase().contains("illegal whitespace")) {
            errorCode = 40011;
        }
        elementMessage = this.getMessageForThrowable(stackTraceElements, ex, UnknownHostException.class);
        if (!elementMessage.equalsIgnoreCase("notfound")) {
            errorCode = 40012;
        }
        mailFailureDetails.put("mailErrorCode", errorCode);
        SyMUtil.updateSyMParameter("MAIL_CONFIG_ERROR", String.valueOf(errorCode));
        return mailFailureDetails;
    }
    
    public String getErrorKeyForErrorCode(final Integer errorCode) {
        if (errorCode == null || errorCode == -1) {
            return null;
        }
        switch (errorCode) {
            case 40000: {
                return "desktopcentral.admin.mail.server.telnet_mail_server";
            }
            case 40012: {
                return "desktopcentral.admin.mail.server.unknown_host";
            }
            case 40001: {
                return "desktopcentral.admin.mail.server.verify_outgoing_port";
            }
            case 40002: {
                return "desktopcentral.admin.mail.server.authentication_failed";
            }
            case 40003: {
                return "desktopcentral.admin.mail.server.connection_timed_out";
            }
            case 40004: {
                return "desktopcentral.admin.mail.server.verify_sender_access";
            }
            case 40005: {
                return "desktopcentral.admin.mail.server.access_across_network";
            }
            case 40006: {
                return "desktopcentral.admin.mail.server.ssl_issue";
            }
            case 40007: {
                return "desktopcentral.admin.mail.server.sender_address_rejected";
            }
            case 40008: {
                return "desktopcentral.admin.mail.server.authentication_failed";
            }
            case 40009: {
                return "desktopcentral.admin.mail.server.authentication_required";
            }
            case 40014: {
                return "desktopcentral.admin.mail.server.authentication_not_required";
            }
            case 40010: {
                return "desktopcentral.admin.mail.server.recepient_address_rejected";
            }
            case 40011: {
                return "desktopcentral.admin.mail.server.illegal_whitespace_in_address";
            }
            case 40013: {
                return "desktopcentral.admin.mail.server.unknown_cause";
            }
            default: {
                return "desktopcentral.admin.mail.server.unknown_cause";
            }
        }
    }
    
    public void addInlineImageAttachments(final MailDetails mailobj, final Multipart mailMultiPart) {
        final MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();
        InputStream attStream = null;
        try {
            if (mailobj.additionalParams != null && mailobj.additionalParams.has("InlineImages")) {
                final JSONObject inlineImagesJSON = (JSONObject)mailobj.additionalParams.get("InlineImages");
                if (inlineImagesJSON != null) {
                    final Iterator<String> inlineImagerIterator = inlineImagesJSON.keys();
                    while (inlineImagerIterator.hasNext()) {
                        final String inlineImageIdentifier = inlineImagerIterator.next();
                        final String inlineImagePath = (String)inlineImagesJSON.get(inlineImageIdentifier);
                        final File imageFile = new File(inlineImagePath);
                        if (inlineImagePath != null && !inlineImagePath.equals("") && imageFile.exists() && !imageFile.isDirectory()) {
                            final MimeBodyPart inlimeImageBodyPart = new MimeBodyPart();
                            final String imageFileName = imageFile.getName();
                            final String mimeType = mimeMap.getContentType(imageFileName);
                            attStream = new FileInputStream(inlineImagePath);
                            final ByteArrayDataSource bas = new ByteArrayDataSource(attStream, mimeType);
                            inlimeImageBodyPart.setDataHandler(new DataHandler((DataSource)bas));
                            inlimeImageBodyPart.setHeader("Content-ID", "<" + inlineImageIdentifier + ">");
                            inlimeImageBodyPart.setDisposition("inline");
                            inlimeImageBodyPart.attachFile(imageFile);
                            mailMultiPart.addBodyPart((BodyPart)inlimeImageBodyPart);
                            if (attStream == null) {
                                continue;
                            }
                            attStream.close();
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            MailProcessor.logger.log(Level.SEVERE, "Exception in SendingMail with inline images");
            try {
                if (attStream != null) {
                    attStream.close();
                }
            }
            catch (final Exception e) {
                MailProcessor.logger.log(Level.WARNING, "Exception while closing stream", e);
            }
        }
        finally {
            try {
                if (attStream != null) {
                    attStream.close();
                }
            }
            catch (final Exception e2) {
                MailProcessor.logger.log(Level.WARNING, "Exception while closing stream", e2);
            }
        }
    }
    
    static {
        MailProcessor.processObj = null;
        MailProcessor.logger = Logger.getLogger(MailProcessor.class.getName());
    }
}
