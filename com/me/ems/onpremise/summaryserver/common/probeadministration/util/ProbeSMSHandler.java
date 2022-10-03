package com.me.ems.onpremise.summaryserver.common.probeadministration.util;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.alerts.sms.SMSAPI;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import java.util.Properties;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.util.ProbeNotificationUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.logging.Logger;

public class ProbeSMSHandler
{
    public static Logger logger;
    
    public static void triggerSMSForProbeEvent(final Long probeId, final String event) {
        try {
            final SMSAPI smsApiImpl = ProbeMgmtFactoryProvider.getSMSAPI();
            if (smsApiImpl != null && smsApiImpl.isSMSSettingsConfigured() && ProbeNotificationUtil.isSMSEnabledForProbe(probeId)) {
                final String users = getUsersConfiguredForProbe(probeId);
                final String[] userArr = users.split(",");
                final Long[] userId = new Long[userArr.length];
                for (int i = 0; i < userArr.length; ++i) {
                    userId[i] = Long.parseLong(userArr[i]);
                }
                final Properties smsProps = new Properties();
                final String phoneNumbers = getPhoneNumberOfUserID(userId);
                final String message = "ems.ss.probemgmt." + event + "_message";
                final String probeName = new ProbeDetailsUtil().getProbeName(probeId);
                ((Hashtable<String, String>)smsProps).put("recipient", phoneNumbers);
                ((Hashtable<String, String>)smsProps).put("message", I18N.getMsg(message, new Object[] { probeName }));
                smsApiImpl.sendHTTPToSMS(smsProps);
            }
        }
        catch (final Exception e) {
            ProbeSMSHandler.logger.log(Level.SEVERE, "Exception while getting triggering sms  for probe " + probeId, e);
        }
    }
    
    public static String getUsersConfiguredForProbe(final Long probeId) {
        String users = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeNotificationSmsUser"));
            selectQuery.addSelectColumn(Column.getColumn("ProbeNotificationSmsUser", "*"));
            selectQuery.setCriteria(new Criteria(new Column("ProbeNotificationSmsUser", "PROBE_ID"), (Object)probeId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator itr = dataObject.getRows("ProbeNotificationSmsUser");
                while (itr.hasNext()) {
                    final Row emailAddrRow = itr.next();
                    if (users.isEmpty()) {
                        users = emailAddrRow.get("USER_ID").toString();
                    }
                    else {
                        users = users + "," + emailAddrRow.get("USER_ID").toString();
                    }
                }
            }
        }
        catch (final Exception e) {
            ProbeSMSHandler.logger.log(Level.SEVERE, "Exception while getting  users configured for  for probe " + probeId, e);
        }
        return users;
    }
    
    public static String getPhoneNumberOfUserID(final Long[] userID) throws DataAccessException {
        String phoneNumber = "";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaContactInfo"));
        final Join userContactInfoJoin = new Join("AaaContactInfo", "AaaUserContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2);
        final Criteria userIDCrit = new Criteria(Column.getColumn("AaaUserContactInfo", "USER_ID"), (Object)userID, 8);
        selectQuery.addJoin(userContactInfoJoin);
        selectQuery.setCriteria(userIDCrit);
        selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "LANDLINE"));
        final DataObject contactDO = SyMUtil.getPersistence().get(selectQuery);
        final Iterator contactItr = contactDO.getRows("AaaContactInfo");
        while (contactItr.hasNext()) {
            final Row contactRow = contactItr.next();
            if (phoneNumber.isEmpty()) {
                phoneNumber = (String)contactRow.get("LANDLINE");
            }
            else {
                phoneNumber = phoneNumber + "," + (String)contactRow.get("LANDLINE");
            }
        }
        return phoneNumber;
    }
    
    static {
        ProbeSMSHandler.logger = Logger.getLogger("probeActionsLogger");
    }
}
