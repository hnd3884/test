package com.me.devicemanagement.onpremise.webclient.message;

import java.util.Hashtable;
import java.io.File;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.onpremise.server.scheduler.SchedulerProviderImpl;
import com.me.devicemanagement.onpremise.server.dbtuning.MssqlTxLogMaintainanceUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.onpremise.server.license.LicenseUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class HomePageMsgHandler implements MsgHandler
{
    public Map modifyMessageContent(final Row msgContentRow, final Long userID, final ArrayList<Long> customerID, final MultivaluedMap<String, String> userDefinedAttributes) throws Exception {
        final Map messageMap = MessageProvider.getInstance().getI18NMessageMap(msgContentRow);
        final String messageName = messageMap.get("name");
        String messageTitle = messageMap.get("title");
        String messageContent = messageMap.get("content");
        Map messageAttributes = messageMap.get("messageAttributes");
        if (messageAttributes == null) {
            messageAttributes = new HashMap();
        }
        final boolean isLicAboutToExpReadMsg = messageName.equalsIgnoreCase("PRODUCT_LICENSE_ABOUT_TO_EXPIRE_READ");
        final boolean isLicAboutToExpWriteMsg = messageName.equalsIgnoreCase("PRODUCT_LICENSE_ABOUT_TO_EXPIRE_WRITE");
        final String expiryDate = LicenseProvider.getInstance().getProductExpiryDate();
        String licenseType = null;
        Object[] msgContentArgument = null;
        if (isLicAboutToExpReadMsg || isLicAboutToExpWriteMsg) {
            final String linkTagID = "LINK_" + messageAttributes.size();
            final String linkOpenTag = "#{" + linkTagID + "}";
            if (!"never".equals(expiryDate)) {
                licenseType = LicenseProvider.getInstance().getLicenseType();
                if (licenseType.equals("T")) {
                    final long noOfDays = LicenseProvider.getInstance().getEvaluationDays();
                    final String url = ProductUrlLoader.getInstance().getValue("prodUrl") + "/request-license.html";
                    messageTitle = I18N.getMsg("dc.common.homePage.trial.about_to_expire.title", new Object[0]);
                    if (noOfDays == 0L) {
                        if (isLicAboutToExpWriteMsg) {
                            messageContent = I18N.getMsg("dm.homePage.trial.expires.today.writecontent", new Object[] { linkOpenTag });
                            messageAttributes = MessageProvider.getInstance().getMessageAttributeMap(messageAttributes, I18N.getMsg("dm.homePage.trial.expires.today.writecontent.2", new Object[0]), "link", url, "_blank");
                        }
                        else {
                            messageContent = I18N.getMsg("dm.homePage.trial.expires.today.read.content", new Object[0]);
                        }
                    }
                    else {
                        if (isLicAboutToExpWriteMsg) {
                            msgContentArgument = new Object[] { noOfDays, linkOpenTag };
                            messageAttributes = MessageProvider.getInstance().getMessageAttributeMap(messageAttributes, I18N.getMsg("dc.common.homePage.about.to.expire.write.content.1", new Object[0]), "link", url, "_blank");
                        }
                        else {
                            msgContentArgument = new Object[] { noOfDays, I18N.getMsg("dc.common.homePage.about.to.expire.read.content.1", new Object[0]) };
                        }
                        messageContent = I18N.getMsg("dc.common.homePage.about.to.expire.content.1", msgContentArgument);
                    }
                }
                else {
                    final String prodExpiryDate = LicenseProvider.getInstance().getProductExpiryDate();
                    final String contactMail = LicenseUtil.getRenewalMailMessageForPayload(true);
                    final String expDate = Utils.getTime(Long.valueOf(LicenseUtil.converDateToLong(prodExpiryDate, " ")));
                    if (isLicAboutToExpWriteMsg) {
                        msgContentArgument = new Object[] { expDate, I18N.getMsg("desktopcentral.common.contact", new Object[0]).concat(" ").concat(linkOpenTag) };
                        messageAttributes = MessageProvider.getInstance().getMessageAttributeMap(messageAttributes, I18N.getMsg("dc.email.sales", new Object[0]), "link", contactMail, "_blank");
                    }
                    else {
                        msgContentArgument = new Object[] { expDate };
                    }
                    messageTitle = I18N.getMsg("dc.common.homePage.reg.about_to_expire.title", new Object[0]);
                    if (isLicAboutToExpReadMsg) {
                        messageContent = I18N.getMsg("dc.common.homePage.reg.about_to_expire.read.content", msgContentArgument);
                    }
                    else {
                        messageContent = I18N.getMsg("dc.common.homePage.reg.about_to_expire.write.content", msgContentArgument);
                    }
                }
            }
            else {
                final String amsExpireDate = LicenseProvider.getInstance().getProductAMSExpiryDate();
                final String contactMail = LicenseUtil.getRenewalMailMessageForPayload(false);
                String expDate = "";
                if (amsExpireDate != null) {
                    expDate = Utils.getTime(Long.valueOf(LicenseUtil.converDateToLong(amsExpireDate, "-")));
                }
                if (isLicAboutToExpWriteMsg) {
                    msgContentArgument = new Object[] { expDate, I18N.getMsg("desktopcentral.common.contact", new Object[0]).concat(" ").concat(linkOpenTag) };
                    messageAttributes = MessageProvider.getInstance().getMessageAttributeMap(messageAttributes, I18N.getMsg("dc.email.sales", new Object[0]), "link", contactMail, "_blank");
                }
                else {
                    msgContentArgument = new Object[] { expDate };
                }
                messageTitle = I18N.getMsg("dc.common.homePage.ams.about_to_expire.title", new Object[0]);
                if (isLicAboutToExpReadMsg) {
                    messageContent = I18N.getMsg("dc.common.home.ams.aboutToExpireReadContent", msgContentArgument);
                }
                else {
                    messageContent = I18N.getMsg("dc.common.home.ams.aboutToExpireWriteContent", msgContentArgument);
                }
            }
        }
        if (messageName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_READ") || messageName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_WRITE")) {
            final String linkTagID = "LINK_" + messageAttributes.size();
            final String linkOpenTag = "#{" + linkTagID + "}";
            if ("never".equals(expiryDate)) {
                final String amsExpireDate = LicenseProvider.getInstance().getProductAMSExpiryDate();
                String expDate2 = "";
                if (amsExpireDate != null) {
                    expDate2 = Utils.getTime(Long.valueOf(LicenseUtil.converDateToLong(amsExpireDate, "-")));
                }
                if (messageName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_READ")) {
                    messageContent = I18N.getMsg("dc.common.homePage.ams.expired.read.content.1", new Object[] { expDate2 });
                }
                if (messageName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_WRITE")) {
                    final String contactMail2 = LicenseUtil.getRenewalMailMessageForPayload(false);
                    messageAttributes = MessageProvider.getInstance().getMessageAttributeMap(messageAttributes, I18N.getMsg("dc.email.sales", new Object[0]), "link", contactMail2, "_blank");
                    messageContent = I18N.getMsg("dc.common.homePage.ams.expired.write.content.1", new Object[] { expDate2, I18N.getMsg("desktopcentral.common.contact", new Object[0]).concat(" ").concat(linkOpenTag) });
                }
            }
        }
        if (messageName.equalsIgnoreCase("MSSQL_TRANSACTOIN_LOG_LIMIT_EXCEED")) {
            final StringBuilder sb = new StringBuilder();
            MssqlTxLogMaintainanceUtil.getInstance();
            final String transactionLogLimit = sb.append(Math.round((float)(MssqlTxLogMaintainanceUtil.txLogSizeToBeSet() / 1024))).append("GB").toString();
            final Long nextExecTime = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
            if (nextExecTime != null && nextExecTime != -1L) {
                final String nextTime = Utils.getEventTime(nextExecTime);
                final String msg = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime });
                final String displayName = ProductUrlLoader.getInstance().getValue("displayname");
                messageContent = messageContent.replace("{0}", displayName).replace("{1}", transactionLogLimit).concat(" [" + msg + "]");
            }
        }
        if (messageName.equalsIgnoreCase("MSSQL_TRANSACTION_LOG_CHANGE")) {
            final Long nextExecTime2 = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
            if (nextExecTime2 != null && nextExecTime2 != -1L) {
                final String nextTime2 = Utils.getEventTime(nextExecTime2);
                final String msg2 = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime2 });
                messageTitle = messageTitle.replace("{0}", "" + Math.round((float)(MssqlTxLogMaintainanceUtil.txLogSizeToBeSet() / 1024)));
                messageContent = messageContent.replace("{0}", SYMClientUtil.getDataBaseName()).concat(" [" + msg2 + "]");
            }
        }
        if (messageName.equalsIgnoreCase("DISK_SPACE_CRITICALLY_LOW")) {
            String diskspacethreshold = "3GB";
            if (System.getProperty("diskcheck.max") != null && System.getProperty("diskcheck.enable").equalsIgnoreCase("true")) {
                String requiredDiskSpace = System.getProperty("diskcheck.max");
                if (System.getProperty("diskcheck.bytetype").equalsIgnoreCase("mb")) {
                    requiredDiskSpace = String.valueOf(Long.valueOf(requiredDiskSpace) / 1000L);
                }
                diskspacethreshold = requiredDiskSpace + I18N.getMsg("dc.common.GB", new Object[0]);
                final String displayName2 = ProductUrlLoader.getInstance().getValue("displayname");
                messageContent = messageContent.replace("{0}", displayName2).replace("{1}", diskspacethreshold);
            }
            else {
                final Long nextExecTime = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
                if (nextExecTime != null && nextExecTime != -1L) {
                    final String nextTime = Utils.getEventTime(nextExecTime);
                    final String msg = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime });
                    final String displayName = ProductUrlLoader.getInstance().getValue("displayname");
                    messageContent = messageContent.replace("{0}", displayName).replace("{1}", diskspacethreshold).concat(" ").concat(msg);
                }
            }
        }
        if (messageName.equalsIgnoreCase("LOW_DISK_SPACE_WARNING")) {
            final String freeDiskSpace = SyMUtil.getSyMParameter("FreeDiskSpace_size");
            final String finalDiskSpaceNeeded = SyMUtil.getSyMParameter("RequiredDiskSpace_size");
            messageContent = messageContent.replace("{0}", freeDiskSpace + "").replace("{1}", finalDiskSpaceNeeded + "");
            final Long nextExecTime3 = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
            if (nextExecTime3 != null && nextExecTime3 != -1L) {
                final String nextTime3 = Utils.getEventTime(nextExecTime3);
                final String msg3 = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime3 });
                messageContent = messageContent + " " + msg3;
            }
        }
        if (messageName.equalsIgnoreCase("PGCONF_RAM_EXCEEDS_SYSRAM") && (CustomerInfoUtil.isDC() || CustomerInfoUtil.isPMP() || CustomerInfoUtil.isVMPProduct())) {
            final String linkTagID = "LINK_0";
            final Map messageAttributeDetails = messageAttributes.get(linkTagID);
            messageAttributeDetails.put("url", "/webclient#/uems/admin/db-optimization");
        }
        if (messageName.equalsIgnoreCase("SQL_DB_FILE_SYSTEM_MISMATCH")) {
            messageContent = SyMUtil.setSQLDBFileSystemMessageContentForBackup(messageContent);
        }
        if (messageName.equalsIgnoreCase("ADMIN_EMAIL_UPDATION_NOTIFY")) {
            final String linkTagID = "LINK_" + messageAttributes.size();
            final String linkOpenTag = "#{" + linkTagID + "}";
            final UserManagementUtil userMgmtUtil = new UserManagementUtil();
            final int noEmailUserCount = userMgmtUtil.getUsersWithoutEmailAddress();
            final int duplicateEmailCount = userMgmtUtil.getDuplicateUserEmailCount();
            boolean isAttrNeeded = true;
            final String url2 = "/webclient#/uems/admin/user-administration/users?invalidEmail=true";
            if (noEmailUserCount > 0 && duplicateEmailCount > 0) {
                messageContent = messageContent.replace("{0}", String.valueOf(noEmailUserCount));
                messageContent = messageContent.replace("{1}", String.valueOf(duplicateEmailCount));
                messageContent = messageContent.replace("{2}", linkOpenTag);
            }
            else if (noEmailUserCount > 0) {
                messageContent = I18N.getMsg("ems.admin.alerts.no_user_email_msg", new Object[] { noEmailUserCount, linkOpenTag });
            }
            else if (duplicateEmailCount > 0) {
                messageContent = I18N.getMsg("ems.admin.alerts.duplicate_email_msg", new Object[] { duplicateEmailCount, linkOpenTag });
            }
            else {
                MessageProvider.getInstance().hideMessage("ADMIN_EMAIL_UPDATION_NOTIFY");
                isAttrNeeded = false;
            }
            if (isAttrNeeded) {
                messageAttributes = MessageProvider.getInstance().getMessageAttributeMap(messageAttributes, I18N.getMsg("ems.admin.alerts.email_updation_needed_link", new Object[0]), "link", url2, "_self");
            }
        }
        messageMap.put("title", messageTitle);
        messageMap.put("content", messageContent);
        messageMap.put("messageAttributes", messageAttributes);
        return messageMap;
    }
    
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        final String msgName = ((Hashtable<K, String>)msgProperties).get("MSG_NAME");
        String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
        String msgTitle = ((Hashtable<K, String>)msgProperties).get("MSG_TITLE");
        final boolean isLicAboutToExpReadMsg = msgName.equalsIgnoreCase("PRODUCT_LICENSE_ABOUT_TO_EXPIRE_READ");
        final boolean isLicAboutToExpWriteMsg = msgName.equalsIgnoreCase("PRODUCT_LICENSE_ABOUT_TO_EXPIRE_WRITE");
        final String expiryDate = LicenseProvider.getInstance().getProductExpiryDate();
        String licenseType = null;
        Object[] msgContentArgument = null;
        if (isLicAboutToExpReadMsg || isLicAboutToExpWriteMsg) {
            if (!"never".equals(expiryDate)) {
                licenseType = LicenseProvider.getInstance().getLicenseType();
                if (licenseType.equals("T")) {
                    final long noOfDays = LicenseProvider.getInstance().getEvaluationDays();
                    final String url = ProductUrlLoader.getInstance().getValue("prodUrl") + "/request-license.html";
                    msgTitle = I18N.getMsg("dc.common.homePage.trial.about_to_expire.title", new Object[0]);
                    if (noOfDays == 0L) {
                        if (isLicAboutToExpWriteMsg) {
                            msgContent = I18N.getMsg("dm.homePage.trial.expires.today.write.content", new Object[] { url });
                        }
                        else {
                            msgContent = I18N.getMsg("dm.homePage.trial.expires.today.read.content", new Object[0]);
                        }
                    }
                    else {
                        if (isLicAboutToExpWriteMsg) {
                            msgContentArgument = new Object[] { noOfDays, url };
                        }
                        else {
                            msgContentArgument = new Object[] { noOfDays };
                        }
                        if (isLicAboutToExpReadMsg) {
                            msgContent = I18N.getMsg("dc.common.homePage.trial.about_to_expire.read.content", msgContentArgument);
                        }
                        else {
                            msgContent = I18N.getMsg("dc.common.homePage.trial.about_to_expire.write.content", msgContentArgument);
                        }
                    }
                }
                else {
                    final String prodExpiryDate = LicenseProvider.getInstance().getProductExpiryDate();
                    final String contactMail = LicenseUtil.getRenewalMailMessage(true);
                    final String expDate = Utils.getTime(Long.valueOf(LicenseUtil.converDateToLong(prodExpiryDate, " ")));
                    if (isLicAboutToExpWriteMsg) {
                        msgContentArgument = new Object[] { expDate, contactMail };
                    }
                    else {
                        msgContentArgument = new Object[] { expDate };
                    }
                    msgTitle = I18N.getMsg("dc.common.homePage.reg.about_to_expire.title", new Object[0]);
                    if (isLicAboutToExpReadMsg) {
                        msgContent = I18N.getMsg("dc.common.homePage.reg.about_to_expire.read.content", msgContentArgument);
                    }
                    else {
                        msgContent = I18N.getMsg("dc.common.homePage.reg.about_to_expire.write.content", msgContentArgument);
                    }
                }
            }
            else {
                final String amsExpireDate = LicenseProvider.getInstance().getProductAMSExpiryDate();
                final String contactMail = LicenseUtil.getRenewalMailMessage(false);
                String expDate = "";
                if (amsExpireDate != null) {
                    expDate = Utils.getTime(Long.valueOf(LicenseUtil.converDateToLong(amsExpireDate, "-")));
                }
                if (isLicAboutToExpWriteMsg) {
                    msgContentArgument = new Object[] { expDate, contactMail };
                }
                else {
                    msgContentArgument = new Object[] { expDate };
                }
                msgTitle = I18N.getMsg("dc.common.homePage.ams.about_to_expire.title", new Object[0]);
                if (isLicAboutToExpReadMsg) {
                    msgContent = I18N.getMsg("dc.common.homePage.ams.about_to_expire.read.content", msgContentArgument);
                }
                else {
                    msgContent = I18N.getMsg("dc.common.homePage.ams.about_to_expire.write.content", msgContentArgument);
                }
            }
        }
        if ((msgName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_READ") || msgName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_WRITE")) && "never".equals(expiryDate)) {
            final String amsExpireDate = LicenseProvider.getInstance().getProductAMSExpiryDate();
            licenseType = "ams";
            String expDate2 = "";
            if (amsExpireDate != null) {
                expDate2 = Utils.getTime(Long.valueOf(LicenseUtil.converDateToLong(amsExpireDate, "-")));
            }
            if (msgName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_READ")) {
                msgContent = I18N.getMsg("dc.common.homePage.ams.expired.read.content", new Object[] { expDate2 });
            }
            if (msgName.equalsIgnoreCase("PRODUCT_LICENSE_EXPIRED_WRITE")) {
                final String contactMail2 = LicenseUtil.getRenewalMailMessage(false);
                msgContent = I18N.getMsg("dc.common.homePage.ams.expired.write.content", new Object[] { expDate2, contactMail2 });
            }
        }
        if (msgName.equalsIgnoreCase("MSSQL_TRANSACTOIN_LOG_LIMIT_EXCEED")) {
            final StringBuilder sb = new StringBuilder();
            MssqlTxLogMaintainanceUtil.getInstance();
            final String transactionLogLimit = sb.append(Math.round((float)(MssqlTxLogMaintainanceUtil.txLogSizeToBeSet() / 1024))).append("GB").toString();
            final Long nextExecTime = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
            if (nextExecTime != null && nextExecTime != -1L) {
                final String nextTime = Utils.getEventTime(nextExecTime);
                final String msg = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime });
                final String displayName = ProductUrlLoader.getInstance().getValue("displayname");
                msgContent = msgContent.replace("{0}", displayName);
                msgContent = msgContent.replace("{1}", transactionLogLimit);
                msgContent = msgContent + " [" + msg + "]";
            }
        }
        if (msgName.equalsIgnoreCase("MSSQL_TRANSACTION_LOG_CHANGE")) {
            final Long nextExecTime2 = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
            if (nextExecTime2 != null && nextExecTime2 != -1L) {
                final String nextTime2 = Utils.getEventTime(nextExecTime2);
                final String msg2 = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime2 });
                final String displayName2 = ProductUrlLoader.getInstance().getValue("displayname");
                final String s = msgTitle;
                final String s2 = "{0}";
                final StringBuilder append = new StringBuilder().append("");
                MssqlTxLogMaintainanceUtil.getInstance();
                msgTitle = s.replace(s2, append.append(Math.round((float)(MssqlTxLogMaintainanceUtil.txLogSizeToBeSet() / 1024))).toString());
                msgContent = msgContent.replace("{0}", SYMClientUtil.getDataBaseName());
                msgContent = msgContent + " [" + msg2 + "]";
            }
        }
        if (msgName.equalsIgnoreCase("DISK_SPACE_CRITICALLY_LOW")) {
            String diskspacethreshold = "3GB";
            if (System.getProperty("diskcheck.max") != null && System.getProperty("diskcheck.enable").equalsIgnoreCase("true")) {
                String requiredDiskSpace = System.getProperty("diskcheck.max");
                if (System.getProperty("diskcheck.bytetype").equalsIgnoreCase("mb")) {
                    requiredDiskSpace = String.valueOf(Long.valueOf(requiredDiskSpace) / 1000L);
                }
                diskspacethreshold = requiredDiskSpace + I18N.getMsg("dc.common.GB", new Object[0]);
                final String displayName3 = ProductUrlLoader.getInstance().getValue("displayname");
                msgContent = msgContent.replace("{0}", displayName3);
                msgContent = msgContent.replace("{1}", diskspacethreshold);
            }
            else {
                final Long nextExecTime = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
                if (nextExecTime != null && nextExecTime != -1L) {
                    final String nextTime = Utils.getEventTime(nextExecTime);
                    final String msg = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime });
                    final String displayName = ProductUrlLoader.getInstance().getValue("displayname");
                    msgContent = msgContent.replace("{0}", displayName);
                    msgContent = msgContent.replace("{1}", diskspacethreshold);
                    msgContent += msg;
                }
            }
        }
        if (msgName.equalsIgnoreCase("LOW_DISK_SPACE_WARNING")) {
            final String freeDiskSpace = SyMUtil.getSyMParameter("FreeDiskSpace_size");
            final String finalDiskSpaceNeeded = SyMUtil.getSyMParameter("RequiredDiskSpace_size");
            msgContent = msgContent.replace("{0}", freeDiskSpace + "");
            msgContent = msgContent.replace("{1}", finalDiskSpaceNeeded + "");
            final Long nextExecTime3 = new SchedulerProviderImpl().getNextExecutionTimeForSchedule("HomePageMessageTaskScheduler");
            if (nextExecTime3 != null && nextExecTime3 != -1L) {
                final String nextTime3 = Utils.getEventTime(nextExecTime3);
                final String msg3 = I18N.getMsg("dc.common.homePage.next_update_time_content", new Object[] { nextTime3 });
                msgContent += msg3;
            }
        }
        if (msgName.equalsIgnoreCase("SQL_DB_FILE_SYSTEM_MISMATCH")) {
            msgContent = SyMUtil.setSQLDBFileSystemMessageContentForBackup(msgContent);
        }
        ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
        ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
        return msgProperties;
    }
    
    public long getFinalDiskSpaceNeeded() {
        final String serverHome = System.getProperty("server.home");
        final String activedb = DBUtil.getActiveDBName();
        long diskSpaceNeeded = 0L;
        long finalDiskSpaceNeeded = 0L;
        try {
            final boolean isDBBackupdefaultLoc = SYMClientUtil.isdbBackUpDirDefaultLoc(serverHome);
            final String[] backupFoldersList = SyMUtil.getBackupFoldersList(serverHome, activedb);
            if (isDBBackupdefaultLoc) {
                diskSpaceNeeded = SYMClientUtil.getDiskSpaceNeeded(backupFoldersList);
                finalDiskSpaceNeeded = diskSpaceNeeded * 2L;
            }
            else {
                final long mySqlSize = SYMClientUtil.getFolderSize(new File(backupFoldersList[0]));
                final long clientDataSize = SYMClientUtil.getFolderSize(new File(backupFoldersList[1]));
                long additionalSpace = 0L;
                for (int i = 2; i < backupFoldersList.length; ++i) {
                    additionalSpace += SYMClientUtil.getFolderSize(new File(backupFoldersList[i]));
                }
                finalDiskSpaceNeeded = mySqlSize * 2L + clientDataSize + additionalSpace;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return finalDiskSpaceNeeded;
    }
}
