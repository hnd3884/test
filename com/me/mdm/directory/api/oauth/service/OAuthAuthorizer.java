package com.me.mdm.directory.api.oauth.service;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.api.paging.annotations.AllCustomerSearchParam;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.paging.SearchUtil;
import com.me.mdm.api.controller.JerseyUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import com.me.mdm.api.controller.IDauthorizer;

public class OAuthAuthorizer implements IDauthorizer
{
    @Override
    public void authorize(final String customerIDstr, final Long userID, final String pathParam, final List<Object> idList) throws Exception {
        if (userID != null && !SyMUtil.isStringEmpty(pathParam) && idList != null && !idList.isEmpty()) {
            final int idCount = idList.size();
            final boolean isMsp = ApiFactoryProvider.getUtilAccessAPI().isMSP();
            boolean authorized = false;
            final AllCustomerSearchParam allCustomerSearchParam = JerseyUtil.getInstance("OAuthMetaPurposeRel", "CUSTOMER_ID");
            final Criteria authCri = SearchUtil.setCustomerFilter(userID, null, customerIDstr, allCustomerSearchParam);
            switch (pathParam) {
                case "oauth_meta_id": {
                    final Long[] idAr = this.convertStringListToLongAr(idList);
                    final Column idCol = Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID");
                    authorized = this.authorizeIDsAgainstUser(idCol, null, idAr, isMsp, authCri, idCount);
                    break;
                }
                case "oauth_token_id": {
                    final Long[] idAr = this.convertStringListToLongAr(idList);
                    final Column idCol = Column.getColumn("OauthTokens", "OAUTH_TOKEN_ID");
                    final List<Join> joins = new ArrayList<Join>(Arrays.asList(new Join("OauthMetadata", "OauthTokens", new String[] { "OAUTH_METADATA_ID" }, new String[] { "OAUTH_METADATA_ID" }, 2)));
                    authorized = this.authorizeIDsAgainstUser(idCol, joins, idAr, isMsp, authCri, idCount);
                    break;
                }
                case "client_id": {
                    final Column idCol2 = Column.getColumn("OauthMetadata", "OAUTH_CLIENT_ID");
                    authorized = this.authorizeIDsAgainstUser(idCol2, null, idList.toArray(new String[idList.size()]), isMsp, authCri, idCount);
                    break;
                }
            }
            if (!authorized) {
                throw new APIHTTPException("COM0008", new Object[] { Arrays.toString(idList.toArray()) });
            }
        }
    }
    
    private boolean authorizeIDsAgainstUser(final Column idCol, final List<Join> joins, final Object[] arObj, final boolean isMsp, final Criteria authCri, final int idCount) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthMetadata"));
        selectQuery.addJoin(new Join("OauthMetadata", "OAuthMetaPurposeRel", new String[] { "OAUTH_METADATA_ID" }, new String[] { "OAUTH_METADATA_ID" }, 2));
        if (joins != null) {
            for (final Join join : joins) {
                selectQuery.addJoin(join);
            }
        }
        selectQuery.setCriteria(new Criteria(idCol, (Object)arObj, 8));
        selectQuery.addSelectColumn(MDMUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn(idCol));
        if (isMsp) {
            selectQuery.addJoin(new Join("OAuthMetaPurposeRel", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            selectQuery.setCriteria(selectQuery.getCriteria().and(authCri));
        }
        final int countFromDB = DBUtil.getRecordCount(selectQuery);
        return countFromDB == idCount;
    }
}
