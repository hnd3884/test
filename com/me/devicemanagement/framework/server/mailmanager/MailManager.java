package com.me.devicemanagement.framework.server.mailmanager;

import java.util.Hashtable;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.logging.Level;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.mail.internet.InternetAddress;
import java.util.logging.Logger;

public final class MailManager
{
    private static Logger logger;
    private static final int MAIL_SENT_SUCCESSFULLY = 1;
    private static final int PROBLEM_IN_SENDING_MAIL = 0;
    private static MailManager mailMan;
    private String fileContentType;
    private String bodyText;
    
    private MailManager() {
        this.fileContentType = "text/plain";
        this.bodyText = "";
    }
    
    public static MailManager getInstance() {
        if (MailManager.mailMan == null) {
            MailManager.mailMan = new MailManager();
        }
        return MailManager.mailMan;
    }
    
    private InternetAddress[] getAddressArray(final String mailString) throws Exception {
        if (mailString == null) {
            return null;
        }
        InternetAddress[] iaddress = null;
        try {
            final StringTokenizer st = new StringTokenizer(mailString, ",");
            final int size = st.countTokens();
            iaddress = new InternetAddress[size];
            for (int j = 0; j < size; ++j) {
                final String str = st.nextToken();
                iaddress[j] = new InternetAddress(str);
            }
        }
        catch (final Exception exp) {
            throw exp;
        }
        return iaddress;
    }
    
    public String getMailContent(final int operationType, final String remarks) {
        String taskName = null;
        if (operationType == 5) {
            taskName = "Update Vulnerability Database";
        }
        else if (operationType == 4) {
            taskName = "Patch Scan";
        }
        else if (operationType == 15) {
            taskName = "Anti Virus Scan";
        }
        else if (operationType == 99 || operationType == 97) {
            taskName = "Inventory - Asset Alerts";
        }
        else {
            taskName = "Deploying the missing patches";
        }
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<html>");
        buffer.append("<head>");
        buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
        buffer.append("</head>");
        buffer.append("<body>");
        if (operationType == 88) {
            final int temp = remarks.indexOf("&&<pre>");
            if (temp == -1) {
                return "Error";
            }
            buffer.append(remarks.substring(temp + 2, remarks.length()));
        }
        else {
            if (operationType != 99 && operationType != 97) {
                buffer.append("Hi,");
                buffer.append("<br><p>");
                buffer.append("Task Name : " + taskName + "<br>");
            }
            buffer.append(remarks);
            if (operationType != 99 && operationType != 97) {
                buffer.append("</p>");
                buffer.append("Thanks,");
                buffer.append("<br>");
                buffer.append("Endpoint Central Team");
            }
        }
        buffer.append("</body>");
        buffer.append("</html>");
        final String bodyText = buffer.toString();
        return bodyText;
    }
    
    public String getHtmlMailContent(final Properties mailContentProps) {
        String taskName = ((Hashtable<K, String>)mailContentProps).get("TASKNAME");
        final String startTime = ((Hashtable<K, String>)mailContentProps).get("STARTTIME");
        final String completionTime = ((Hashtable<K, String>)mailContentProps).get("COMPLETIONTIME");
        final String updateRemarks = ((Hashtable<K, String>)mailContentProps).get("UPDATE_REMARKS");
        final String scanRemarks = ((Hashtable<K, String>)mailContentProps).get("SCAN_REMARKS");
        final String patchRemarks = ((Hashtable<K, String>)mailContentProps).get("PATCH_REMARKS");
        final String draftReamrks = ((Hashtable<K, String>)mailContentProps).get("DRAFT_REMARKS");
        final String deployRemarks = ((Hashtable<K, String>)mailContentProps).get("DEPLOY_REMARKS");
        final Integer taskType = ((Hashtable<K, Integer>)mailContentProps).get("TYPE");
        final String status = ((Hashtable<K, String>)mailContentProps).get("STATUS");
        final String patchCleanup = ((Hashtable<K, String>)mailContentProps).get("PatchCleanup");
        String nextUpdateTime = "";
        String baseURLStr = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
        if (!baseURLStr.startsWith("http")) {
            baseURLStr = "http://" + baseURLStr + "/";
        }
        else {
            baseURLStr += "/";
        }
        String taskId = null;
        if (mailContentProps.get("APD_TASK_ID") != null) {
            taskId = ((Hashtable<K, Long>)mailContentProps).get("APD_TASK_ID").toString();
        }
        if (mailContentProps.get("NEXT_DB_UPDATE_TIME") != null) {
            nextUpdateTime = ((Hashtable<K, String>)mailContentProps).get("NEXT_DB_UPDATE_TIME");
        }
        final StringBuffer buffer = new StringBuffer();
        try {
            buffer.append("<html>");
            buffer.append("<head>");
            buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
            buffer.append("<link href=\"https://webfonts.zoho.com/css?family=Lato:400/Roboto:400\">");
            buffer.append("</head>");
            buffer.append("<body>");
            buffer.append("Hi,");
            buffer.append("<br><p>");
            if (taskId != null) {
                final String taskUrl = baseURLStr + "webclient#/uems/patch-mgmt/deployment/automate-patch-deployment/details/" + taskId;
                taskName = "<a href=" + taskUrl + " target=\"_blank\">" + taskName + "</a>";
            }
            if (patchCleanup != null && Integer.parseInt(patchCleanup) == 104) {
                final String cleanupSettingUrl = baseURLStr + "webclient#/uems/patch-mgmt/patches/downloaded-patches?tab=cleanup";
                final String LimitSpecified = ((Hashtable<K, String>)mailContentProps).get("LimitSpecified");
                final String CurrentSize = ((Hashtable<K, String>)mailContentProps).get("CurrentSize");
                final String cleanupPolicyLink = "<a href=\"" + cleanupSettingUrl + "\" target=\"_blank\"> Configure Clean Up Policy </a>";
                String remoteOfficeName = null;
                if (mailContentProps.get("RemoteOffice") != null) {
                    remoteOfficeName = ((Hashtable<K, String>)mailContentProps).get("RemoteOffice");
                    buffer.append(I18N.getMsg("dc.patch.util.roCleanupContent", new Object[] { LimitSpecified, CurrentSize, cleanupPolicyLink, remoteOfficeName }) + "<br>");
                }
                else {
                    buffer.append(I18N.getMsg("dc.patch.util.cleanupContent", new Object[] { LimitSpecified, CurrentSize, cleanupPolicyLink }) + "<br>");
                }
            }
            else {
                buffer.append(" <b> " + I18N.getMsg("dc.conf.schdule.task_name", new Object[0]) + " : </b>" + taskName + "<br><br>");
                buffer.append("<ul style=\"list-style-type:none;padding-left:0px;margin-left:0px;\">");
                buffer.append("<li> <b> " + I18N.getMsg("dc.patch.apd.task_init", new Object[0]) + "   : </b>" + startTime + "</li>" + "<br>");
                buffer.append("<li> <b> " + I18N.getMsg("dc.patch.apd.report_gen_time", new Object[0]) + " : </b>" + completionTime + "</li>" + "<br>");
            }
            if (taskType != null && status.equals("COMPLETED")) {
                final int operationType = taskType;
                if (operationType == 5) {
                    if (nextUpdateTime != null && !nextUpdateTime.equals("")) {
                        buffer.append("<li> <b> " + I18N.getMsg("dc.patch.apd.next_db_time", new Object[0]) + " : </b>" + nextUpdateTime + "</li>" + "<br>");
                    }
                    else {
                        buffer.append("<li> <b> " + I18N.getMsg("dc.patch.apd.next_db_time", new Object[0]) + " : </b> Not Scheduled" + "</li>" + "<br>");
                    }
                    buffer.append("<li> <b> " + I18N.getMsg("dc.patch.apd.db_update_status", new Object[0]) + " : </b>" + updateRemarks + "</li>" + "<br>");
                }
                buffer.append("</ul>");
                if (operationType == 10 || operationType == 16 || operationType == 9 || operationType == 8 || operationType == 4 || operationType == 15) {
                    buffer.append("<br>" + I18N.getMsg("desktopcentral.config.common.addMore.Operation", new Object[0]) + ": <b>" + I18N.getMsg("dc.common.SCAN", new Object[0]) + " </b>" + "<br>");
                    final String[] scanCounts = scanRemarks.split(",");
                    final Integer scansuccess = Integer.parseInt(scanCounts[0]);
                    final Integer scanfailed = Integer.parseInt(scanCounts[1]);
                    final Integer scannotDone = Integer.parseInt(scanCounts[2]);
                    final Integer scanDone = scansuccess + scanfailed + scannotDone;
                    buffer.append("<ul style=\"list-style-type:none;\">");
                    buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_of_mcs_scan", new Object[0]) + " : " + scanDone + "</li>");
                    buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_of_mcs_scan_success", new Object[0]) + " : " + scansuccess + "</li>");
                    buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.common.SCANNING_FAILED", new Object[0]) + " : " + scanfailed + "</li>");
                    buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.common.NOT_SCANNED", new Object[0]) + " : " + scannotDone + "</li>");
                    buffer.append("</ul>");
                    if (scannotDone > 0) {
                        buffer.append("<span style=\"color:#999;font-size:11px;\"><b>" + I18N.getMsg("dc.common.NOTE", new Object[0]) + ":</b> " + I18N.getMsg("dc.patch.util.not_scanned_note", new Object[0]) + "</span><br>");
                    }
                    if (operationType == 8 || operationType == 10 || operationType == 9 || operationType == 16) {
                        buffer.append("<br>" + I18N.getMsg("desktopcentral.config.common.addMore.Operation", new Object[0]) + ": <b>" + I18N.getMsg("dc.patch.apd.download", new Object[0]) + " </b>" + "<br>");
                        final String[] dloadCounts = patchRemarks.split(",");
                        final Integer dloadsuccess = Integer.parseInt(dloadCounts[0]);
                        final Integer dloadavailable = Integer.parseInt(dloadCounts[1]);
                        final Integer dloadfailed = Integer.parseInt(dloadCounts[2]);
                        final Integer dloadDone = dloadsuccess + dloadavailable + dloadfailed;
                        buffer.append("<ul style=\"list-style-type:none;\">");
                        if (dloadDone != 0) {
                            buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_of_miss_patch", new Object[0]) + " : " + dloadDone + "</li>");
                            buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_of_miss_patch_already_dwn", new Object[0]) + " : " + dloadavailable + "</li>");
                            buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_of_miss_patch_dwn_success", new Object[0]) + " : " + dloadsuccess + "</li>");
                            buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.download.Download_Failed", new Object[0]) + " : " + dloadfailed + "</li>");
                        }
                        else {
                            buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_of_miss_patch", new Object[0]) + " : " + dloadDone + " <a href=\"http://www.manageengine.com/products/desktop-central/automated-patch-deployment-no-missing-patches-found.html\">" + I18N.getMsg("dc.common.READ_KB", new Object[0]) + "</a>" + "<br><br><br>");
                        }
                        buffer.append("</ul>");
                        if (operationType == 9) {
                            buffer.append("<br>" + I18N.getMsg("desktopcentral.config.common.addMore.Operation", new Object[0]) + ": <b>" + I18N.getMsg("dc.db.config.status.draft", new Object[0]) + " </b>" + "<br>");
                            buffer.append("<ul style=\"list-style-type:none;\">");
                            if (dloadDone == 0) {
                                buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_draft", new Object[0]) + "</li>");
                            }
                            else {
                                buffer.append("<li style=\"padding-bottom:5px\">" + draftReamrks + "</li>");
                            }
                            buffer.append("</ul>");
                        }
                        if (operationType == 10 || operationType == 16) {
                            buffer.append("<br>" + I18N.getMsg("desktopcentral.config.common.addMore.Operation", new Object[0]) + ": <b>" + I18N.getMsg("dc.common.DEPLOYMENT", new Object[0]) + " </b>" + "<br>");
                            buffer.append("<ul style=\"list-style-type:none;\">");
                            if (dloadDone == 0) {
                                buffer.append("<li style=\"padding-bottom:5px\">" + I18N.getMsg("dc.patch.apd.no_patches_to_deploy", new Object[0]) + "</li>");
                            }
                            else {
                                buffer.append("<li style=\"padding-bottom:5px\">" + deployRemarks + "</li>");
                            }
                            buffer.append("</ul>");
                        }
                    }
                }
            }
            else {
                buffer.append("</ul>");
            }
            buffer.append("</p><br>");
            buffer.append(I18N.getMsg("dc.mdm.email.Thanks", new Object[0]) + ",");
            buffer.append("<br>");
            buffer.append(I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]));
            buffer.append("<br><br><hr style=\"border-top:1px solid #CCC;color:white\" />");
            final String endText = "<span style=\"color:#999;font:13px Lato;\">This is an auto-generated mail from <a href='" + baseURLStr + "'>" + I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]) + "</a>.</span></br>";
            buffer.append(endText);
            buffer.append("</body>");
            buffer.append("</html>");
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return buffer.toString();
    }
    
    public String getMailSubject(final int operationType, final String remarks) {
        String subject = "Endpoint Central : ";
        if (operationType == 5) {
            subject = "Update Vulnerability Database Report";
        }
        else if (operationType == 4) {
            subject = "Patch Scan Report";
        }
        else if (operationType == 15) {
            subject = "Anti Virus Scan Report";
        }
        else if (operationType == 8) {
            subject = "Patch Scan and Patch Download Report";
        }
        else if (operationType == 99 || operationType == 97) {
            subject = "Inventory - Asset Alerts";
        }
        else if (operationType == 88) {
            final int temp = remarks.indexOf("&&<pre>");
            if (temp == -1) {
                return subject;
            }
            subject = remarks.substring(5, temp);
        }
        else {
            subject = "Patch Deployment Report";
        }
        return subject;
    }
    
    public StringBuffer getFileBuffer(final InputStream stream) throws Exception {
        final StringBuffer strBuff = new StringBuffer();
        try {
            final int BUFSIZE = 1024;
            final byte[] buf = new byte[1024];
            int len;
            while ((len = stream.read(buf, 0, buf.length)) != -1) {
                for (int i = 0; i < len; ++i) {
                    strBuff.append((char)buf[i]);
                }
            }
            stream.close();
        }
        catch (final IOException ioe) {
            throw ioe;
        }
        return strBuff;
    }
    
    public static void main(final String[] args) {
        try {
            final File f = new File("Mail.txt");
            MailManager.logger.log(Level.INFO, "File : " + f.getAbsolutePath());
            MailManager.logger.log(Level.INFO, "Exist : " + f.exists());
            final MailManager mManager = getInstance();
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("userName", "svidhya");
            ((Hashtable<String, String>)prop).put("taskType", "Scan");
            ((Hashtable<String, String>)prop).put("taskId", "10");
            ((Hashtable<String, String>)prop).put("serverName", "sd-test2");
            ((Hashtable<String, String>)prop).put("creationTime", "Tue Aug 24 19:01:05 GMT+05:30 2004");
            ((Hashtable<String, String>)prop).put("completionTime", "Tue Aug 25 20:01:05 GMT+05:30 2004");
            ((Hashtable<String, String>)prop).put("remarks", "Scan for all the devices completed successfully.");
            ((Hashtable<String, String>)prop).put("subject", "Deatils for Task Id 1");
            final Properties smtpProp = new Properties();
            ((Hashtable<String, String>)smtpProp).put("smtpServer", "smtp");
            ((Hashtable<String, String>)smtpProp).put("port", "25");
            ((Hashtable<String, String>)smtpProp).put("userName", "svidhya");
            ((Hashtable<String, String>)smtpProp).put("password", "");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Session getSession(final Properties mailProps, final String userName, final String password) {
        final MailServerAuthenticator authenticator = new MailServerAuthenticator(userName, password);
        final Session session = Session.getInstance(mailProps, (Authenticator)authenticator);
        return session;
    }
    
    static {
        MailManager.logger = Logger.getLogger(MailManager.class.getName());
        MailManager.mailMan = null;
    }
}
