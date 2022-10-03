package com.me.idps.core.oauth;

import java.util.Hashtable;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import org.json.JSONException;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;
import com.me.idps.core.api.IdpsAPIException;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.persistence.Row;
import com.me.idps.core.util.IdpsUtil;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Properties;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;

public class OauthDataHandler
{
    private static OauthDataHandler oauthDataHandler;
    
    public static OauthDataHandler getInstance() {
        if (OauthDataHandler.oauthDataHandler == null) {
            OauthDataHandler.oauthDataHandler = new OauthDataHandler();
        }
        return OauthDataHandler.oauthDataHandler;
    }
    
    Long getOauthIdFromDomain(final Long domainID, final Long customerID, final String[] scopes, final int oauthType) throws OauthException {
        Long oauthTokenId = null;
        final Criteria domainIdCriteria = new Criteria(Column.getColumn("OauthTokens", "DOMAIN_ID"), (Object)domainID, 0);
        final Criteria oauthTypeCriteria = new Criteria(Column.getColumn("OauthTokens", "OAUTH_TYPE"), (Object)oauthType, 0);
        Criteria cri = domainIdCriteria.and(oauthTypeCriteria);
        Properties oauth;
        if (scopes != null && scopes.length > 0) {
            final Criteria scopeCriteria = new Criteria(Column.getColumn("OauthScopes", "VALUE"), (Object)scopes, 8, false);
            cri = cri.and(scopeCriteria);
            oauth = this.getAllOauthTables(cri, true);
        }
        else {
            oauth = this.getAllOauthTables(cri, false);
        }
        if (oauth != null) {
            oauthTokenId = ((Hashtable<K, Long>)oauth).get("OAUTH_TOKEN_ID");
        }
        return oauthTokenId;
    }
    
    public Properties getOauthTokensById(final Long oauthTokensId, final Criteria filter, final boolean isScope) {
        final Criteria oauthTokenCriteria = new Criteria(Column.getColumn("OauthTokens", "OAUTH_TOKEN_ID"), (Object)oauthTokensId, 0);
        Criteria combine;
        if (filter != null) {
            combine = oauthTokenCriteria.and(filter);
        }
        else {
            combine = oauthTokenCriteria;
        }
        return this.getAllOauthTables(combine, isScope);
    }
    
    public SelectQuery getOAuthBaseQuery() {
        final SelectQuery oauthMetaQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthMetadata"));
        oauthMetaQuery.addJoin(new Join("OauthMetadata", "OauthTokens", new String[] { "OAUTH_METADATA_ID" }, new String[] { "OAUTH_METADATA_ID" }, 1));
        oauthMetaQuery.addJoin(new Join("OauthTokens", "OauthScopesMapping", new String[] { "OAUTH_TOKEN_ID" }, new String[] { "OAUTH_TOKEN_ID" }, 1));
        oauthMetaQuery.addJoin(new Join("OauthScopesMapping", "OauthScopes", new String[] { "OAUTH_SCOPES_ID" }, new String[] { "OAUTH_SCOPES_ID" }, 1));
        oauthMetaQuery.addJoin(new Join("OauthMetadata", "OAuthMetaPurposeRel", new String[] { "OAUTH_METADATA_ID" }, new String[] { "OAUTH_METADATA_ID" }, 2));
        oauthMetaQuery.addJoin(new Join("OauthTokens", "AuthenticationTokenDetails", new String[] { "OAUTH_TOKEN_ID" }, new String[] { "OAUTH_TOKEN_ID" }, 1));
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
            oauthMetaQuery.addJoin(new Join("OAuthMetaPurposeRel", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        }
        oauthMetaQuery.addSelectColumns((List)new ArrayList(Arrays.asList(Column.getColumn("OauthScopes", "VALUE"), Column.getColumn("OauthScopes", "OAUTH_SCOPES_ID"), Column.getColumn("OauthScopesMapping", "OAUTH_SCOPES_ID"), Column.getColumn("OauthScopesMapping", "OAUTH_TOKEN_ID"), Column.getColumn("AuthenticationTokenDetails", "TOKEN_TYPE"), Column.getColumn("AuthenticationTokenDetails", "EXPIRES_AT"), Column.getColumn("AuthenticationTokenDetails", "OAUTH_TOKEN_ID"), Column.getColumn("OauthTokens", "ADDED_AT"), Column.getColumn("OauthTokens", "OAUTH_TOKEN_ID"), Column.getColumn("OauthTokens", "REFERENCE_USER"), Column.getColumn("OauthTokens", "OAUTH_METADATA_ID"), Column.getColumn("OauthMetadata", "DOMAIN_TYPE"), Column.getColumn("OauthMetadata", "OAUTH_CLIENT_ID"), Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID"), Column.getColumn("OAuthMetaPurposeRel", "ADDED_BY"), Column.getColumn("OAuthMetaPurposeRel", "ADDED_AT"), Column.getColumn("OAuthMetaPurposeRel", "MODIFIED_BY"), Column.getColumn("OAuthMetaPurposeRel", "MODIFIED_AT"), Column.getColumn("OAuthMetaPurposeRel", "CUSTOMER_ID"), Column.getColumn("OAuthMetaPurposeRel", "OAUTH_META_MAP_ID"), Column.getColumn("OAuthMetaPurposeRel", "OAUTH_METADATA_ID"))));
        return oauthMetaQuery;
    }
    
    List getAuthTokensByOauthId(final Long oauthTokensId, final Integer tokenType) {
        List<Properties> tokens = null;
        try {
            final Criteria oauthTokensIdCriteria = new Criteria(Column.getColumn("AuthenticationTokenDetails", "OAUTH_TOKEN_ID"), (Object)oauthTokensId, 0);
            Criteria combine;
            if (tokenType != null) {
                final Criteria tokenTypeCriteria = new Criteria(Column.getColumn("AuthenticationTokenDetails", "TOKEN_TYPE"), (Object)tokenType, 0);
                combine = oauthTokensIdCriteria.and(tokenTypeCriteria);
            }
            else {
                combine = oauthTokensIdCriteria;
            }
            final DataObject dObj = IdpsUtil.getPersistenceLite().get("AuthenticationTokenDetails", oauthTokensIdCriteria.and(combine));
            if (!dObj.isEmpty()) {
                tokens = new ArrayList<Properties>();
                final Iterator rows = dObj.getRows("AuthenticationTokenDetails");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final Properties token = new Properties();
                    ((Hashtable<String, Object>)token).put("TOKEN_TYPE", row.get("TOKEN_TYPE"));
                    ((Hashtable<String, Object>)token).put("TOKEN_VALUE", row.get("TOKEN_VALUE"));
                    ((Hashtable<String, Object>)token).put("EXPIRES_AT", row.get("EXPIRES_AT"));
                    tokens.add(token);
                }
            }
        }
        catch (final DataAccessException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "data getting error in getAuthTokensById", (Throwable)e);
        }
        return tokens;
    }
    
    private Properties getAllOauthTables(final Criteria common, final boolean isScopes) {
        Properties oauthProps = null;
        try {
            final Join scopes_join = new Join("OauthTokens", "OauthScopesMapping", new String[] { "OAUTH_TOKEN_ID" }, new String[] { "OAUTH_TOKEN_ID" }, 1);
            final Join mapping_join = new Join("OauthScopesMapping", "OauthScopes", new String[] { "OAUTH_SCOPES_ID" }, new String[] { "OAUTH_SCOPES_ID" }, 1);
            final Join domain_mapping_join = new Join("OauthTokens", "DMDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1);
            final SelectQuery oauthQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthTokens"));
            oauthQuery.setCriteria(common);
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "OAUTH_TOKEN_ID"));
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "OAUTH_TYPE"));
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "OAUTH_METADATA_ID"));
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "CREATED_BY"));
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "ADDED_AT"));
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "STATUS"));
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "DOMAIN_ID"));
            oauthQuery.addSelectColumn(Column.getColumn("OauthTokens", "REFERENCE_USER"));
            oauthQuery.addSelectColumn(Column.getColumn("DMDomain", "DOMAIN_ID"));
            oauthQuery.addSelectColumn(Column.getColumn("DMDomain", "CLIENT_ID"));
            oauthQuery.addJoin(domain_mapping_join);
            if (isScopes) {
                oauthQuery.addSelectColumn(Column.getColumn("OauthScopesMapping", "OAUTH_TOKEN_ID"));
                oauthQuery.addSelectColumn(Column.getColumn("OauthScopes", "OAUTH_SCOPES_ID"));
                oauthQuery.addSelectColumn(Column.getColumn("OauthScopes", "VALUE"));
                oauthQuery.addJoin(scopes_join);
                oauthQuery.addJoin(mapping_join);
            }
            final DataObject dObj = IdpsUtil.getPersistenceLite().get(oauthQuery);
            if (!dObj.isEmpty()) {
                oauthProps = new Properties();
                final Row r = dObj.getFirstRow("OauthTokens");
                ((Hashtable<String, Object>)oauthProps).put("OAUTH_TOKEN_ID", r.get("OAUTH_TOKEN_ID"));
                ((Hashtable<String, Object>)oauthProps).put("OAUTH_TYPE", r.get("OAUTH_TYPE"));
                if (r.get("OAUTH_METADATA_ID") != null) {
                    ((Hashtable<String, Object>)oauthProps).put("OAUTH_METADATA_ID", r.get("OAUTH_METADATA_ID"));
                }
                else {
                    ((Hashtable<String, String>)oauthProps).put("OAUTH_METADATA_ID", "--");
                }
                ((Hashtable<String, Object>)oauthProps).put("CREATED_BY", r.get("CREATED_BY"));
                ((Hashtable<String, Object>)oauthProps).put("ADDED_AT", r.get("ADDED_AT"));
                ((Hashtable<String, Object>)oauthProps).put("STATUS", r.get("STATUS"));
                if (r.get("DOMAIN_ID") != null) {
                    ((Hashtable<String, Object>)oauthProps).put("DOMAIN_ID", r.get("DOMAIN_ID"));
                }
                else {
                    ((Hashtable<String, String>)oauthProps).put("DOMAIN_ID", "--");
                }
                if (r.get("REFERENCE_USER") != null) {
                    ((Hashtable<String, Object>)oauthProps).put("REFERENCE_USER", r.get("REFERENCE_USER"));
                }
                else {
                    ((Hashtable<String, String>)oauthProps).put("REFERENCE_USER", "--");
                }
                final Row domainRow = dObj.getRow("DMDomain");
                if (domainRow != null) {
                    ((Hashtable<String, Object>)oauthProps).put("DOMAIN_TYPE", domainRow.get("CLIENT_ID"));
                }
                if (isScopes) {
                    final List<Properties> scopesList = new ArrayList<Properties>();
                    final Iterator scopeRows = dObj.getRows("OauthScopes");
                    while (scopeRows.hasNext()) {
                        final Properties p = new Properties();
                        final Row srow = scopeRows.next();
                        ((Hashtable<String, Object>)p).put("OAUTH_SCOPES_ID", srow.get("OAUTH_SCOPES_ID"));
                        ((Hashtable<String, Object>)p).put("VALUE", srow.get("VALUE"));
                        scopesList.add(p);
                    }
                    ((Hashtable<String, List<Properties>>)oauthProps).put("scope", scopesList);
                }
            }
        }
        catch (final DataAccessException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "data getting error in getAllOauthTables", (Throwable)e);
        }
        return oauthProps;
    }
    
    private Properties getMetadataProps(final Criteria criteria, final Long customerID, final int oauthType, final Long userID) {
        Properties metadata = null;
        try {
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthMetadata"));
            selectQuery.setCriteria(criteria);
            selectQuery = IdpsFactoryProvider.getOauthImpl(oauthType).configureSelectQuery(selectQuery, customerID, userID);
            selectQuery.addSelectColumn(Column.getColumn("OauthMetadata", "OAUTH_CLIENT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID"));
            selectQuery.addSelectColumn(Column.getColumn("OauthMetadata", "OAUTH_CLIENT_SECRET"));
            final DataObject dObj = IdpsUtil.getPersistenceLite().get(selectQuery);
            if (dObj != null && !dObj.isEmpty()) {
                metadata = new Properties();
                final Row row = dObj.getFirstRow("OauthMetadata");
                ((Hashtable<String, Object>)metadata).put("OAUTH_CLIENT_ID", row.get("OAUTH_CLIENT_ID"));
                ((Hashtable<String, Object>)metadata).put("OAUTH_METADATA_ID", row.get("OAUTH_METADATA_ID"));
                ((Hashtable<String, Object>)metadata).put("OAUTH_CLIENT_SECRET", row.get("OAUTH_CLIENT_SECRET"));
                return metadata;
            }
        }
        catch (final Exception e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "data getting error in getMetadataFromId", e);
            throw new IdpsAPIException("COM0004");
        }
        throw new IdpsAPIException("COM0032", "OAuth App details");
    }
    
    public Properties getMetadataFromDomainType(final Long customerID, final int domainType, final Long userID) {
        return this.getMetadataFromMetaID(customerID, domainType, userID, null);
    }
    
    public Properties getMetadataFromMetaID(final Long customerID, final int oauthType, final Long userID, final Long metadataID) {
        Criteria metaIDcri = null;
        final int domainType = IdpsFactoryProvider.getOauthImpl(oauthType).getDomainType();
        final Criteria domainTypeCri = new Criteria(Column.getColumn("OauthMetadata", "DOMAIN_TYPE"), (Object)domainType, 0);
        if (metadataID != null) {
            metaIDcri = new Criteria(Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID"), (Object)metadataID, 0);
        }
        final Criteria criteria = IdpsUtil.andCriteria(metaIDcri, domainTypeCri);
        return this.getMetadataProps(criteria, customerID, oauthType, userID);
    }
    
    private Row getOAuthMetaMapRow(final Row oAuthMetaRow, Row oAuthMetaMapRow, final Long customerID, final Long userID) {
        final Long timeStamp = System.currentTimeMillis();
        final boolean add = oAuthMetaMapRow == null;
        final Object metaID = oAuthMetaRow.get("OAUTH_METADATA_ID");
        if (add) {
            oAuthMetaMapRow = new Row("OAuthMetaPurposeRel");
            oAuthMetaMapRow.set("ADDED_BY", (Object)userID);
            oAuthMetaMapRow.set("ADDED_AT", (Object)timeStamp);
            oAuthMetaMapRow.set("CUSTOMER_ID", (Object)customerID);
            oAuthMetaMapRow.set("OAUTH_METADATA_ID", metaID);
        }
        oAuthMetaMapRow.set("MODIFIED_BY", (Object)userID);
        oAuthMetaMapRow.set("MODIFIED_AT", (Object)timeStamp);
        return oAuthMetaMapRow;
    }
    
    private Row getOAuthMetaRow(Row row, final Long customerID, final String client_id, final String client_secret, final int domainType, final boolean add) {
        if (row == null) {
            row = new Row("OauthMetadata");
        }
        if (add) {
            row.set("DOMAIN_TYPE", (Object)domainType);
        }
        if (customerID == null || add) {
            row.set("OAUTH_CLIENT_ID", (Object)client_id);
        }
        row.set("OAUTH_CLIENT_SECRET", (Object)client_secret);
        return row;
    }
    
    public Long addOrUpdateOauthMetadata(final Long customerID, final Long userID, Long metadataId, final String client_id, final String client_secret, final int oauthType) throws DataAccessException, IdpsAPIException {
        int domainType = -1;
        Criteria criteria = null;
        if (oauthType != -1) {
            domainType = IdpsFactoryProvider.getOauthImpl(oauthType).getDomainType();
            criteria = new Criteria(Column.getColumn("OauthMetadata", "DOMAIN_TYPE"), (Object)domainType, 0);
        }
        if (metadataId != null) {
            final Criteria metaIdCriteria = new Criteria(Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID"), (Object)metadataId, 0);
            criteria = IdpsUtil.andCriteria(criteria, metaIdCriteria);
        }
        if (customerID != null) {
            final Criteria customerIDcri = new Criteria(Column.getColumn("OAuthMetaPurposeRel", "CUSTOMER_ID"), (Object)customerID, 0);
            criteria = IdpsUtil.andCriteria(criteria, customerIDcri);
        }
        if (oauthType == -1 && metadataId == null) {
            throw new IdpsAPIException("COM0004");
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthMetadata"));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("OauthMetadata", "OAUTH_CLIENT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID"));
        if (customerID != null) {
            selectQuery.addJoin(new Join("OauthMetadata", "OAuthMetaPurposeRel", new String[] { "OAUTH_METADATA_ID" }, new String[] { "OAUTH_METADATA_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("OAuthMetaPurposeRel", "MODIFIED_BY"));
            selectQuery.addSelectColumn(Column.getColumn("OAuthMetaPurposeRel", "MODIFIED_AT"));
            selectQuery.addSelectColumn(Column.getColumn("OAuthMetaPurposeRel", "OAUTH_METADATA_ID"));
            selectQuery.addSelectColumn(Column.getColumn("OAuthMetaPurposeRel", "OAUTH_META_MAP_ID"));
        }
        final DataObject dObj = IdpsUtil.getPersistenceLite().get(selectQuery);
        if (dObj.isEmpty()) {
            final Row oAuthMetaRow = this.getOAuthMetaRow(null, customerID, client_id, client_secret, domainType, true);
            dObj.addRow(oAuthMetaRow);
            if (customerID != null && userID != null) {
                final Row oAuthMetaMapRow = this.getOAuthMetaMapRow(oAuthMetaRow, null, customerID, userID);
                dObj.addRow(oAuthMetaMapRow);
            }
            final DataObject resultDo = IdpsUtil.getPersistenceLite().add(dObj);
            if (resultDo != null && !resultDo.isEmpty()) {
                metadataId = (Long)resultDo.getFirstRow("OauthMetadata").get("OAUTH_METADATA_ID");
            }
        }
        else {
            Row oAuthMetaRow = dObj.getRow("OauthMetadata");
            metadataId = (Long)oAuthMetaRow.get("OAUTH_METADATA_ID");
            oAuthMetaRow = this.getOAuthMetaRow(oAuthMetaRow, customerID, client_id, client_secret, domainType, false);
            dObj.updateRow(oAuthMetaRow);
            if (customerID != null) {
                final Iterator oauthMetaMapItr = dObj.getRows("OAuthMetaPurposeRel", new Criteria(Column.getColumn("OAuthMetaPurposeRel", "OAUTH_METADATA_ID"), (Object)metadataId, 0));
                while (oauthMetaMapItr != null && oauthMetaMapItr.hasNext()) {
                    Row oAuthMetaMapRow2 = oauthMetaMapItr.next();
                    oAuthMetaMapRow2 = this.getOAuthMetaMapRow(oAuthMetaRow, oAuthMetaMapRow2, customerID, userID);
                    dObj.updateRow(oAuthMetaMapRow2);
                }
            }
            IdpsUtil.getPersistenceLite().update(dObj);
        }
        return metadataId;
    }
    
    Long addOrUpdateOauthTokens(final JSONObject oauthTokensJson) {
        Long oauthTokensId = null;
        try {
            oauthTokensId = oauthTokensJson.optLong("OAUTH_TOKEN_ID", -1L);
            if (oauthTokensId != -1L) {
                final Criteria tokensId = new Criteria(Column.getColumn("OauthTokens", "OAUTH_TOKEN_ID"), (Object)oauthTokensId, 0);
                final DataObject dObj = IdpsUtil.getPersistenceLite().get("OauthTokens", tokensId);
                if (dObj.isEmpty()) {
                    throw new DataAccessException("Id does not exist");
                }
                final Row row = dObj.getFirstRow("OauthTokens");
                if (oauthTokensJson.has("STATUS")) {
                    row.set("STATUS", (Object)oauthTokensJson.getInt("STATUS"));
                }
                if (oauthTokensJson.has("REFERENCE_USER")) {
                    row.set("REFERENCE_USER", (Object)oauthTokensJson.getString("REFERENCE_USER"));
                }
                if (oauthTokensJson.has("DOMAIN_ID")) {
                    row.set("DOMAIN_ID", (Object)oauthTokensJson.getLong("DOMAIN_ID"));
                }
                if (oauthTokensJson.has("OAUTH_METADATA_ID")) {
                    row.set("OAUTH_METADATA_ID", (Object)oauthTokensJson.getLong("OAUTH_METADATA_ID"));
                }
                dObj.updateRow(row);
                final DataObject resultDo = IdpsUtil.getPersistenceLite().update(dObj);
                if (resultDo != null && !resultDo.isEmpty()) {
                    oauthTokensId = (Long)resultDo.getFirstRow("OauthTokens").get("OAUTH_TOKEN_ID");
                    return oauthTokensId;
                }
            }
            else {
                final Row row2 = new Row("OauthTokens");
                row2.set("OAUTH_TYPE", (Object)oauthTokensJson.optInt("OAUTH_TYPE", -1));
                if (oauthTokensJson.has("OAUTH_METADATA_ID")) {
                    row2.set("OAUTH_METADATA_ID", (Object)oauthTokensJson.getLong("OAUTH_METADATA_ID"));
                }
                if (oauthTokensJson.has("CREATED_BY")) {
                    row2.set("CREATED_BY", oauthTokensJson.get("CREATED_BY"));
                }
                row2.set("ADDED_AT", (Object)System.currentTimeMillis());
                if (oauthTokensJson.has("STATUS")) {
                    row2.set("STATUS", (Object)oauthTokensJson.getInt("STATUS"));
                }
                if (oauthTokensJson.has("DOMAIN_ID")) {
                    row2.set("DOMAIN_ID", (Object)oauthTokensJson.getLong("DOMAIN_ID"));
                }
                if (oauthTokensJson.has("REFERENCE_USER")) {
                    row2.set("REFERENCE_USER", (Object)oauthTokensJson.getString("REFERENCE_USER"));
                }
                final DataObject dObj = (DataObject)new WritableDataObject();
                dObj.addRow(row2);
                final DataObject resultDo2 = IdpsUtil.getPersistenceLite().add(dObj);
                if (resultDo2 != null && !resultDo2.isEmpty()) {
                    oauthTokensId = (Long)resultDo2.getFirstRow("OauthTokens").get("OAUTH_TOKEN_ID");
                    return oauthTokensId;
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "data Insert/update error in addOrUpdateOauthTokens", e);
        }
        return oauthTokensId;
    }
    
    boolean addOrUpdateAuthToken(final JSONObject tokenJson) {
        try {
            if (tokenJson.has("OAUTH_TOKEN_ID") && tokenJson.has("TOKEN_TYPE")) {
                final Long oauthTokensId = tokenJson.getLong("OAUTH_TOKEN_ID");
                final int tokenType = tokenJson.getInt("TOKEN_TYPE");
                if (oauthTokensId != -1L && tokenType > 0) {
                    final Criteria oauthTokenCriteria = new Criteria(Column.getColumn("AuthenticationTokenDetails", "OAUTH_TOKEN_ID"), (Object)oauthTokensId, 0);
                    final Criteria tokenTypeCriteria = new Criteria(Column.getColumn("AuthenticationTokenDetails", "TOKEN_TYPE"), (Object)tokenType, 0);
                    final DataObject dObj = IdpsUtil.getPersistenceLite().get("AuthenticationTokenDetails", oauthTokenCriteria.and(tokenTypeCriteria));
                    if (dObj.isEmpty()) {
                        final Row row = new Row("AuthenticationTokenDetails");
                        row.set("OAUTH_TOKEN_ID", (Object)tokenJson.getLong("OAUTH_TOKEN_ID"));
                        row.set("TOKEN_TYPE", (Object)tokenJson.getInt("TOKEN_TYPE"));
                        row.set("TOKEN_VALUE", (Object)tokenJson.getString("TOKEN_VALUE"));
                        row.set("EXPIRES_AT", (Object)tokenJson.getLong("EXPIRES_AT"));
                        dObj.addRow(row);
                        final DataObject resultDO = IdpsUtil.getPersistenceLite().add(dObj);
                        if (resultDO != null && !resultDO.isEmpty()) {
                            return true;
                        }
                    }
                    else {
                        final Row validRow = dObj.getFirstRow("AuthenticationTokenDetails");
                        validRow.set("TOKEN_VALUE", (Object)tokenJson.getString("TOKEN_VALUE"));
                        validRow.set("EXPIRES_AT", tokenJson.get("EXPIRES_AT"));
                        dObj.updateRow(validRow);
                        final DataObject resultDO = IdpsUtil.getPersistenceLite().update(dObj);
                        if (resultDO != null && !resultDO.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        catch (final JSONException | DataAccessException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "data Insert/update error in addOrUpdateAuthToken", e);
        }
        return false;
    }
    
    List addOrUpdateScopes(final List values) {
        List<Properties> scopesId = null;
        try {
            final Criteria scopesCriteria = new Criteria(Column.getColumn("OauthScopes", "VALUE"), (Object)values.toArray(), 8);
            DataObject dObj = IdpsUtil.getPersistenceLite().get("OauthScopes", scopesCriteria);
            scopesId = new ArrayList<Properties>();
            if (dObj.isEmpty()) {
                for (final Object value : values) {
                    final String s = (String)value;
                    final Row r = new Row("OauthScopes");
                    r.set("VALUE", (Object)s);
                    dObj.addRow(r);
                }
                final DataObject resultDo = IdpsUtil.getPersistenceLite().add(dObj);
                if (resultDo != null && !resultDo.isEmpty()) {
                    final Iterator i = resultDo.getRows("OauthScopes");
                    while (i.hasNext()) {
                        final Row r2 = i.next();
                        final Properties properties = new Properties();
                        ((Hashtable<String, Object>)properties).put("OAUTH_SCOPES_ID", r2.get("OAUTH_SCOPES_ID"));
                        ((Hashtable<String, Object>)properties).put("VALUE", r2.get("VALUE"));
                        scopesId.add(properties);
                    }
                    return scopesId;
                }
            }
            else {
                final Iterator rows = dObj.getRows("OauthScopes");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final Properties p = new Properties();
                    ((Hashtable<String, Object>)p).put("OAUTH_SCOPES_ID", row.get("OAUTH_SCOPES_ID"));
                    ((Hashtable<String, Object>)p).put("VALUE", row.get("VALUE"));
                    scopesId.add(p);
                    values.remove(row.get("VALUE"));
                }
                if (values.size() > 0) {
                    dObj = (DataObject)new WritableDataObject();
                    for (final Object value2 : values) {
                        final String s2 = (String)value2;
                        final Row r3 = new Row("OauthScopes");
                        r3.set("VALUE", (Object)s2);
                        dObj.addRow(r3);
                    }
                    final DataObject resultDo2 = IdpsUtil.getPersistenceLite().add(dObj);
                    if (resultDo2 != null && !resultDo2.isEmpty()) {
                        final Iterator j = resultDo2.getRows("OauthScopes");
                        while (j.hasNext()) {
                            final Row r = j.next();
                            final Properties properties2 = new Properties();
                            ((Hashtable<String, Object>)properties2).put("OAUTH_SCOPES_ID", r.get("OAUTH_SCOPES_ID"));
                            ((Hashtable<String, Object>)properties2).put("VALUE", r.get("VALUE"));
                            scopesId.add(properties2);
                        }
                        return scopesId;
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "data Insert/update error in addOrUpdateScopes", (Throwable)e);
        }
        return scopesId;
    }
    
    boolean addOrUpdateOauthScopesMapping(final Long oauthTokensId, final List scopesId) {
        try {
            final Criteria oauthIdCriteria = new Criteria(Column.getColumn("OauthScopesMapping", "OAUTH_TOKEN_ID"), (Object)oauthTokensId, 0);
            final Criteria scopesCriteria = new Criteria(Column.getColumn("OauthScopesMapping", "OAUTH_SCOPES_ID"), (Object)scopesId.toArray(), 8);
            DataObject dObj = IdpsUtil.getPersistenceLite().get("OauthScopesMapping", oauthIdCriteria.and(scopesCriteria));
            if (dObj.isEmpty()) {
                for (final Object value : scopesId) {
                    final Long s = (Long)value;
                    final Row r = new Row("OauthScopesMapping");
                    r.set("OAUTH_TOKEN_ID", (Object)oauthTokensId);
                    r.set("OAUTH_SCOPES_ID", (Object)s);
                    dObj.addRow(r);
                }
                final DataObject resultDo = IdpsUtil.getPersistenceLite().add(dObj);
                if (resultDo != null && !resultDo.isEmpty()) {
                    return true;
                }
            }
            else {
                final Iterator rows = dObj.getRows("OauthScopesMapping");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final Properties p = new Properties();
                    scopesId.remove(row.get("OAUTH_SCOPES_ID"));
                }
                if (scopesId.size() > 0) {
                    dObj = (DataObject)new WritableDataObject();
                    for (final Object value2 : scopesId) {
                        final Long s2 = (Long)value2;
                        final Row r2 = new Row("OauthScopesMapping");
                        r2.set("OAUTH_TOKEN_ID", (Object)oauthTokensId);
                        r2.set("OAUTH_SCOPES_ID", (Object)s2);
                        dObj.addRow(r2);
                    }
                    final DataObject resultDo2 = IdpsUtil.getPersistenceLite().add(dObj);
                    if (resultDo2 != null && !resultDo2.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "data Insert/update error in addOrUpdateOauthScopesMapping", (Throwable)e);
        }
        return false;
    }
    
    void deleteOauthTokenFromOauthTokenId(final Long oauthTokenID) throws Exception {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("OauthTokens");
        final Criteria domainDeleteCriteria = new Criteria(Column.getColumn("OauthTokens", "OAUTH_TOKEN_ID"), (Object)oauthTokenID, 0);
        deleteQuery.setCriteria(domainDeleteCriteria);
        IdpsUtil.getPersistenceLite().delete(deleteQuery);
    }
    
    public void deleteOAuthMetadata(final Long oauthMetaID, final Long customerID, final Long userID) throws Exception {
        IDPSlogger.OAUTH.log(Level.WARNING, "deleteing oauth id {0} for custId:{1} by userId:{2}", new Object[] { String.valueOf(oauthMetaID), String.valueOf(customerID), String.valueOf(userID) });
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("OauthMetadata");
        deleteQuery.addJoin(new Join("OauthMetadata", "OAuthMetaPurposeRel", new String[] { "OAUTH_METADATA_ID" }, new String[] { "OAUTH_METADATA_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID"), (Object)oauthMetaID, 0).and(new Criteria(Column.getColumn("OAuthMetaPurposeRel", "CUSTOMER_ID"), (Object)customerID, 0));
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
            deleteQuery.addJoin(new Join("OAuthMetaPurposeRel", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            criteria = criteria.and(new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0));
        }
        deleteQuery.setCriteria(criteria);
        final int deletedRows = DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
        IDPSlogger.OAUTH.log(Level.WARNING, "number of rows deleted for oauth id {0} for custId:{1} by userId:{2} => {3}", new Object[] { String.valueOf(oauthMetaID), String.valueOf(customerID), String.valueOf(userID), deletedRows });
    }
    
    static {
        OauthDataHandler.oauthDataHandler = null;
    }
}
