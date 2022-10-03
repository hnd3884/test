package com.me.mdm.onpremise.server.authentication;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Properties;
import com.me.mdm.core.auth.APIKey;
import org.json.JSONObject;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;

public class MDMOnPremiseUserAPIKeyGenerator extends MDMUserAPIKeyGenerator
{
    public APIKey createAPIKey(final JSONObject json) throws Exception {
        final Properties apiProps = new Properties();
        apiProps.setProperty("loginID", String.valueOf(json.getLong("LOGIN_ID")));
        apiProps.setProperty("SCOPE", "MDM Admin App");
        apiProps.setProperty("TEMPLATE_TYPE", String.valueOf(json.get("TEMPLATE_TYPE")));
        final String apiKey = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getOrAddAuthToken(apiProps);
        return new APIKey("zapikey", apiKey, APIKey.VERSION_2_0);
    }
    
    public boolean validateAPIKey(final JSONObject json) {
        final String zAPIKey = json.getString("zapikey");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMUserPurposeToken"));
        selectQuery.addJoin(new Join("MDMUserPurposeToken", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addJoin(new Join("MDMUserPurposeToken", "EnrollmentTemplate", new String[] { "PURPOSE_KEY" }, new String[] { "TEMPLATE_TYPE" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MDMUserPurposeToken", "PURPOSE_TOKEN"), (Object)Base64.encodeBase64String(zAPIKey.getBytes()), 0));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final Row row = dataObject.getFirstRow("AaaLogin");
            final Long userID = (Long)row.get("USER_ID");
            json.put("USER_ID", (Object)userID);
        }
        catch (final DataAccessException e) {
            MDMOnPremiseUserAPIKeyGenerator.LOGGER.log(Level.WARNING, "Exception While validating EncAPI key", (Throwable)e);
        }
        return super.validateAPIKey(json);
    }
}
