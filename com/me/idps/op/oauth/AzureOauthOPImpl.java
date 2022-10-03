package com.me.idps.op.oauth;

import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DeleteQuery;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.idps.core.api.IdpsAPIException;
import com.me.idps.core.oauth.OauthDataHandler;
import java.util.Properties;
import com.me.idps.core.service.azure.AzureOauthImpl;

public class AzureOauthOPImpl extends AzureOauthImpl
{
    private static final String OLD_DEFAULT_AZURE_APP_CLIENT_ID = "494acfde-c273-42fd-b737-ba33852d06f0";
    
    @Override
    public Properties fetchMetadata(final Long customerID, final Long userID) {
        final Properties existingMetadata = OauthDataHandler.getInstance().getMetadataFromDomainType(customerID, 201, userID);
        if (existingMetadata != null && existingMetadata.containsKey("OAUTH_CLIENT_ID") && existingMetadata.containsKey("OAUTH_CLIENT_SECRET")) {
            return existingMetadata;
        }
        throw new IdpsAPIException("COM0032", "Azure OAuth App details");
    }
    
    @Override
    public void handleAzureOAuth() {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("OauthMetadata");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("OauthMetadata", "OAUTH_CLIENT_ID"), (Object)"494acfde-c273-42fd-b737-ba33852d06f0", 0, false));
            final int numOfRowsDelete = DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
            IDPSlogger.AUDIT.log(Level.INFO, "deleted {0} rows from {1}", new Object[] { numOfRowsDelete, "OauthMetadata" });
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public SelectQuery configureSelectQuery(final SelectQuery selectQuery, final Long customerID, final Long userID) throws Exception {
        Criteria userCri = null;
        Criteria queryCri = selectQuery.getCriteria();
        final Criteria customerCri = new Criteria(Column.getColumn("OAuthMetaPurposeRel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.addJoin(new Join("OauthMetadata", "OAuthMetaPurposeRel", new String[] { "OAUTH_METADATA_ID" }, new String[] { "OAUTH_METADATA_ID" }, 2));
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP() && userID != null) {
            userCri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0);
            selectQuery.addJoin(new Join("OAuthMetaPurposeRel", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        }
        queryCri = IdpsUtil.andCriteria(queryCri, customerCri);
        queryCri = IdpsUtil.andCriteria(userCri, queryCri);
        selectQuery.setCriteria(queryCri);
        return selectQuery;
    }
}
