package com.me.mdm.onpremise.util;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Level;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Logger;
import com.me.mdm.server.util.CloudCSRAuthAPIInterface;

public class MDMCSROPAuthProvider implements CloudCSRAuthAPIInterface
{
    private static final Logger LOGGER;
    
    public String getAuthKey() {
        try {
            final String authKeyFromDB = (String)DBUtil.getValueFromDB("MDMCSRSignAPIAuthInfo", "KEY", (Object)"1", "CLIENT_SECRET");
            final String authKey = CryptoUtil.decrypt(authKeyFromDB, "1602569284318");
            return authKey;
        }
        catch (final Exception ex) {
            MDMCSROPAuthProvider.LOGGER.log(Level.SEVERE, "Exception in getting the purpose key for Cloud APIS", ex);
            return null;
        }
    }
    
    public JSONObject getPublicKeyLatestVersion() {
        try {
            final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMCSRSignAPIAuthInfo"));
            selQuery.addSelectColumn(Column.getColumn("MDMCSRSignAPIAuthInfo", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("MDMCSRSignAPIAuthInfo", "KEY"), (Object)"2", 0);
            selQuery.setCriteria(criteria);
            selQuery.addSortColumn(new SortColumn(Column.getColumn("MDMCSRSignAPIAuthInfo", "KEY_VERSION"), false));
            final DataObject dobj = SyMUtil.getPersistence().get(selQuery);
            final Row publicKey = dobj.getFirstRow("MDMCSRSignAPIAuthInfo");
            String returnKey = (String)publicKey.get("CLIENT_SECRET");
            returnKey = CryptoUtil.decrypt(returnKey, "1602569284318");
            final Integer version = (Integer)publicKey.get("KEY_VERSION");
            final JSONObject json = new JSONObject();
            json.put("PUBLIC_KEY", (Object)returnKey);
            json.put("PUBLIC_KEY_VERSION", (Object)version);
            return json;
        }
        catch (final Exception ex) {
            MDMCSROPAuthProvider.LOGGER.log(Level.SEVERE, "Exception in getting getPublicKeyLatestVersion", ex);
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
