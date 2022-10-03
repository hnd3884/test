package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.SSOPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class Do2SSOPayload implements DO2Payload
{
    private static Logger logger;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        SSOPayload payload = null;
        final SSOPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("SSOAccountPolicy");
            while (iterator.hasNext()) {
                payload = new SSOPayload(1, "MDM", "com.mdm.mobiledevice.sso", "SingleSignON");
                final Row row = iterator.next();
                final Long configDataItemId = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final Iterator appIterator = this.getPolicyAppRows(dataObject, configDataItemId);
                final Iterator urlIterator = this.getPolicyUrlDetails(dataObject, configDataItemId);
                List appList = this.getContentListFromIterator(appIterator, "APP_IDENTIFIER");
                final List urlList = this.getContentListFromIterator(urlIterator, "URL");
                final String accountName = (String)row.get("ACCOUNT_DISPLAY_NAME");
                final Row accountRow = dataObject.getRow("SSOKerberosAccount", new Criteria(new Column("SSOKerberosAccount", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                final String principalName = (String)accountRow.get("KERBEROS_PRINICPAL_NAME");
                final String realm = (String)accountRow.get("KERBEROS_REALM");
                if (!MDMStringUtils.isEmpty(accountName)) {
                    payload.setAccountName(accountName);
                }
                if (!MDMStringUtils.isEmpty(principalName)) {
                    payload.setPrincipalName(principalName);
                }
                if (!MDMStringUtils.isEmpty(realm)) {
                    payload.setRelam(realm);
                }
                if (urlList.size() != 0) {
                    payload.setURL(urlList);
                }
                if (appList.size() != 0) {
                    if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
                        appList = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDList(appList);
                    }
                    payload.setApp(appList);
                }
            }
        }
        catch (final Exception e) {
            Do2SSOPayload.logger.log(Level.SEVERE, "Exception in Creating SSO Payload", e);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
    
    private Iterator<Row> getPolicyAppRows(final DataObject dataObject, final Long configDataItemId) throws DataAccessException {
        final Criteria criteria = new Criteria(new Column("SSOApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
        return dataObject.getRows("SSOApps", criteria);
    }
    
    private Iterator<Row> getPolicyUrlDetails(final DataObject dataObject, final Long configDataItemId) throws DataAccessException {
        final Criteria criteria = new Criteria(new Column("SSODomains", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
        final Join urlJoin = new Join("SSODomains", "ManagedWebDomainURLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 2);
        return dataObject.getRows("ManagedWebDomainURLDetails", criteria, urlJoin);
    }
    
    private List<String> getContentListFromIterator(final Iterator iterator, final String content) {
        final List<String> urlList = new ArrayList<String>();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String url = (String)row.get(content);
            urlList.add(url);
        }
        return urlList;
    }
    
    static {
        Do2SSOPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
