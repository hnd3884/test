package com.me.emsalerts.notifications.core;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertsUtil
{
    Logger alertsLogger;
    
    public AlertsUtil() {
        this.alertsLogger = Logger.getLogger("EMSAlertsLogger");
    }
    
    public StringBuffer replaceContent(StringBuffer content, final String key, final String value) {
        try {
            for (int i = content.indexOf(key); i != -1; i = content.indexOf(key)) {
                content = content.replace(i, i + key.length(), value);
            }
        }
        catch (final Exception ex) {
            this.alertsLogger.log(Level.WARNING, "Exception occured at replaceSubject", ex);
        }
        return content;
    }
    
    public String expandedContent(final Properties prop, final String contentToBeModified) {
        String expDescri = "";
        try {
            StringBuffer expDescriBuff = new StringBuffer(contentToBeModified);
            final Enumeration enumeration = prop.keys();
            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final String value = ((Hashtable<K, Object>)prop).get(key).toString();
                expDescriBuff = this.replaceContent(expDescriBuff, key, value);
            }
            expDescri = expDescriBuff.toString();
        }
        catch (final Exception ex) {
            this.alertsLogger.log(Level.WARNING, "Exception occurred at expandedContent", ex);
        }
        return expDescri;
    }
    
    public String getPhoneNumberOfUserID(final Long[] userID) throws DataAccessException {
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
    
    public String appendNote(String description) {
        final String serverURL = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
        final String productName = ProductUrlLoader.getInstance().getValue("displayname");
        description = description + "<br/> <span style=\"color:#999;\">Note: This is an auto-generated mail from  <a href='" + serverURL + "'>" + productName + "</a></span>";
        return description;
    }
}
