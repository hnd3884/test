package com.adventnet.sym.server.fos;

import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.adventnet.persistence.fos.FOS;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class FailoverHealthTracker implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    public void executeTask(final Properties props) {
        Boolean failoverEnabled = Boolean.FALSE;
        ArrayList<String> masterFailed = new ArrayList<String>();
        ArrayList<String> slaveFailed = new ArrayList<String>();
        try {
            failoverEnabled = FOS.isEnabled();
        }
        catch (final Exception ex) {
            FailoverHealthTracker.logger.log(Level.SEVERE, "Exception while checking if failover is enabled", ex);
        }
        if (failoverEnabled) {
            try {
                final FOS fos = new FOS();
                fos.initialize();
                final String masterIP = fos.getFOSConfig().ipaddr();
                final String slaveIP = fos.getOtherNode();
                if (slaveIP != null) {
                    masterFailed = this.getFalseValues(masterIP);
                    slaveFailed = this.getFalseValues(slaveIP);
                    final DataObject notificationDo = SyMUtil.getPersistence().get("FailoverNotificationDetails", (Criteria)null);
                    final int notificationCount = (int)notificationDo.getFirstValue("FailoverNotificationDetails", "NOTIFICATION_COUNT");
                    final Long last_notified_at = (Long)notificationDo.getFirstValue("FailoverNotificationDetails", "LAST_NOTIFIED_TIME");
                    if (masterFailed.isEmpty() && slaveFailed.isEmpty()) {
                        DBUtil.setColumnValue(notificationDo, "FailoverNotificationDetails", "NOTIFICATION_COUNT", (Object)"0");
                        SyMUtil.getPersistence().update(notificationDo);
                        return;
                    }
                    Boolean sendMail = Boolean.FALSE;
                    if (notificationCount == 0 || last_notified_at == 0L) {
                        sendMail = Boolean.TRUE;
                    }
                    else {
                        final long time = System.currentTimeMillis() - last_notified_at;
                        final long hours = time / 3600000L;
                        if (notificationCount < 5 && hours >= Math.pow(notificationCount, 2.0)) {
                            sendMail = Boolean.TRUE;
                        }
                    }
                    if (sendMail) {
                        final StringBuilder errorMsg = new StringBuilder();
                        final String MailSubject = I18N.getMsg("dc.admin.fos.issue_mail_subject", new Object[] { null });
                        errorMsg.append("<div style='margin: 5%; width: 80%; padding: 3%;border: 1px solid #c2bbbb;border-left: 5px solid #F44336;'>");
                        errorMsg.append(I18N.getMsg("dc.admin.fos.mail.admin", new Object[0]));
                        errorMsg.append("<p>");
                        errorMsg.append(I18N.getMsg("dc.admin.fos.mail_issue_header", new Object[0]));
                        errorMsg.append("</p>");
                        if (!masterFailed.isEmpty()) {
                            errorMsg.append("<p style='margin-top: 4%;font: 17px Lato;color: red;'>");
                            errorMsg.append(I18N.getMsg("dc.admin.fos.issue_main_server_title", new Object[] { null }));
                            errorMsg.append("</p>");
                            errorMsg.append((CharSequence)this.getErrorMsg(masterIP, masterFailed));
                            errorMsg.append("</p>");
                        }
                        if (!slaveFailed.isEmpty()) {
                            errorMsg.append("<p style='margin-top: 4%;font: 17px Lato;color: red;'>");
                            errorMsg.append(I18N.getMsg("dc.admin.fos.issue_fos_server_title", new Object[] { null }));
                            errorMsg.append("</p>");
                            errorMsg.append((CharSequence)this.getErrorMsg(slaveIP, slaveFailed));
                            errorMsg.append("</p>");
                        }
                        errorMsg.append("  <p style='margin-top: 5%;font: 14px Lato;'>");
                        errorMsg.append(I18N.getMsg("dc.admin.fos.sign", new Object[0]));
                        errorMsg.append("</p>\n");
                        errorMsg.append("<p style='margin-top: -1%;font: 11px Lato;color: #8a6e6e;'>");
                        errorMsg.append(I18N.getMsg("dc.admin.fos.automated_mail_warning", new Object[0]));
                        errorMsg.append("</p></div>");
                        SYMClientUtil.sendMailForFOS(slaveIP, errorMsg.toString(), MailSubject);
                    }
                    DBUtil.setColumnValue(notificationDo, "FailoverNotificationDetails", "NOTIFICATION_COUNT", (Object)(notificationCount + 1));
                    DBUtil.setColumnValue(notificationDo, "FailoverNotificationDetails", "LAST_NOTIFIED_TIME", (Object)System.currentTimeMillis());
                    SyMUtil.getPersistence().update(notificationDo);
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(FailoverHealthTracker.class.getName()).log(Level.SEVERE, "Exception while getting failover nodes details", ex);
            }
        }
    }
    
    public ArrayList getFalseValues(final String ipAddress) {
        final ArrayList<String> trueValues = new ArrayList<String>();
        try {
            final DataObject healthDo = DBUtil.getDataObjectFromDB("FosHealthDetails", "SERVER_IP", (Object)ipAddress);
            if (healthDo != null && healthDo.isEmpty()) {
                final Row detailRow = healthDo.getRow("FosHealthDetails");
                final List columnsList = detailRow.getColumns();
                final Properties props = new Properties();
                for (int i = 0; i < columnsList.size(); ++i) {
                    final String columnName = columnsList.get(i);
                    final Object value = detailRow.get(columnName);
                    if (value == Boolean.FALSE) {
                        trueValues.add(columnName);
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(FailoverHealthTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return trueValues;
    }
    
    public StringBuilder getErrorMsg(final String ipAddress, final ArrayList<String> failedDetails) {
        final StringBuilder errorMsg = new StringBuilder();
        if (!failedDetails.isEmpty()) {
            try {
                errorMsg.append("<ul style=\"line-height:2;\">");
                for (final String s : failedDetails) {
                    switch (s) {
                        case "IS_DISKSPACE_ENOUGH": {
                            errorMsg.append("<li>");
                            errorMsg.append(I18N.getMsg("dc.admin.fos.mail_insufficient_space", new Object[] { ipAddress }));
                            errorMsg.append("</li>");
                            continue;
                        }
                        case "IS_STATIC": {
                            errorMsg.append("<li>");
                            errorMsg.append(I18N.getMsg("dc.admin.fos.mail_dynamic_ip", new Object[] { ipAddress }));
                            errorMsg.append("</li>");
                            continue;
                        }
                        case "IS_PEER_REACHABLE": {
                            errorMsg.append("<li>");
                            errorMsg.append(I18N.getMsg("dc.admin.fos.mail_peer_reachable", new Object[] { ipAddress }));
                            errorMsg.append("</li>");
                            continue;
                        }
                        case "IS_TIME_SAME": {
                            errorMsg.append("<li>");
                            errorMsg.append(I18N.getMsg("dc.admin.fos.mail_time_diff", new Object[] { ipAddress }));
                            errorMsg.append("</li>");
                            continue;
                        }
                    }
                }
                errorMsg.append("</ul>");
            }
            catch (final Exception e) {
                FailoverHealthTracker.logger.log(Level.SEVERE, "Exception while getting error msg", e);
            }
        }
        return errorMsg;
    }
    
    static {
        FailoverHealthTracker.logger = Logger.getLogger(FailoverHealthTracker.class.getName());
    }
}
