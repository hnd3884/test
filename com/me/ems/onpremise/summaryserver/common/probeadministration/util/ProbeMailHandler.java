package com.me.ems.onpremise.summaryserver.common.probeadministration.util;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class ProbeMailHandler
{
    public static Logger logger;
    
    public static void sendMailForProbeEvent(final Long probeId, final String event) {
        try {
            final Properties smtpProps = ApiFactoryProvider.getMailSettingAPI().getMailServerDetailsProps();
            final String fromName = smtpProps.getProperty("mail.fromName");
            final String fromAddress = smtpProps.getProperty("mail.fromAddress");
            final String toAddr = getMailConfiguredForProbe(probeId);
            final MailDetails mailDetails = new MailDetails(fromAddress, toAddr);
            mailDetails.senderDisplayName = fromName;
            final String contentStr = "ems.ss.probemgmt." + event + "_bodycontent";
            final String subjectStr = "ems.ss.probemgmt." + event + "_subject";
            final String probeName = new ProbeDetailsUtil().getProbeName(probeId);
            mailDetails.subject = I18N.getMsg(subjectStr, new Object[] { probeName });
            mailDetails.bodyContent = I18N.getMsg(contentStr, new Object[] { probeName });
            ApiFactoryProvider.getMailSettingAPI().addToMailQueue(mailDetails, 0);
        }
        catch (final Exception e) {
            ProbeMailHandler.logger.log(Level.SEVERE, "Exception while sending probe mail for " + event + " for probe " + probeId, e);
        }
    }
    
    public static String getMailConfiguredForProbe(final Long probeId) {
        String emailAddr = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeNotificationEmailAddr"));
            selectQuery.addSelectColumn(Column.getColumn("ProbeNotificationEmailAddr", "*"));
            selectQuery.setCriteria(new Criteria(new Column("ProbeNotificationEmailAddr", "PROBE_ID"), (Object)probeId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row emailAddrRow = dataObject.getFirstRow("ProbeNotificationEmailAddr");
                emailAddr = (String)emailAddrRow.get("EMAIL_ADDR");
            }
        }
        catch (final Exception e) {
            ProbeMailHandler.logger.log(Level.SEVERE, "Exception while getting  probe mail  addr for  for probe " + probeId, e);
        }
        return emailAddr;
    }
    
    static {
        ProbeMailHandler.logger = Logger.getLogger("probeActionsLogger");
    }
}
