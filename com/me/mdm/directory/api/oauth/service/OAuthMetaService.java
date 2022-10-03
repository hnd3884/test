package com.me.mdm.directory.api.oauth.service;

import com.adventnet.ds.query.DeleteQuery;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.crud.DomainDataProvider;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.idps.core.service.azure.AzureOauthImpl;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.mdm.directory.api.oauth.model.OAuthMetaInputModel;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.IdpsUtil;
import com.me.mdm.api.paging.SearchUtil;
import com.me.idps.core.oauth.OauthDataHandler;
import com.me.mdm.directory.api.oauth.model.SearchOAuth;
import com.me.mdm.directory.api.oauth.model.OAuthMetaListModel;
import com.me.mdm.directory.api.oauth.model.OAuthMetaInfoListModel;
import com.me.mdm.directory.api.oauth.model.OAuthMetaInfoModel;
import com.me.mdm.directory.api.oauth.model.OAuthTokenListModel;
import com.me.mdm.directory.api.oauth.model.OAuthMetaModel;
import com.me.idps.core.util.DirectoryUtil;
import com.me.mdm.directory.api.oauth.model.OAuthTokenMetaModel;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.directory.api.oauth.model.OAuthTokenModel;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.me.mdm.directory.api.oauth.model.OAuthScopeModel;

public class OAuthMetaService
{
    private String getAccessTokenType(final int tokenType) {
        switch (tokenType) {
            case 2: {
                return "Access Token";
            }
            case 1: {
                return "Refresh Token";
            }
            default: {
                return "NA";
            }
        }
    }
    
    private void extractScopeValues(final OAuthScopeModel oAuthScopeModel, final DataObject oAuthDobj) throws DataAccessException {
        final Long oAuthScopeID = oAuthScopeModel.getoAuthScopeId();
        final Row oAuthScopeRow = oAuthDobj.getRow("OauthScopes", new Criteria(Column.getColumn("OauthScopes", "OAUTH_SCOPES_ID"), (Object)oAuthScopeID, 0));
        oAuthScopeModel.setScope((String)oAuthScopeRow.get("VALUE"));
    }
    
    private void extractTokenScopeDetails(final OAuthTokenModel oAuthTokenModel, final DataObject oAuthDobj) throws DataAccessException {
        final Long oAuthTokenID = oAuthTokenModel.getOAuthTokenId();
        final List<OAuthScopeModel> oAuthScopesList = new ArrayList<OAuthScopeModel>();
        final Iterator scopesMappingItr = oAuthDobj.getRows("OauthScopesMapping", new Criteria(Column.getColumn("OauthScopesMapping", "OAUTH_TOKEN_ID"), (Object)oAuthTokenID, 0));
        while (scopesMappingItr != null && scopesMappingItr.hasNext()) {
            final OAuthScopeModel oAuthScopeModel = new OAuthScopeModel();
            final Row scopesMappingRow = scopesMappingItr.next();
            oAuthScopeModel.setoAuthScopeId((Long)scopesMappingRow.get("OAUTH_SCOPES_ID"));
            this.extractScopeValues(oAuthScopeModel, oAuthDobj);
            oAuthScopesList.add(oAuthScopeModel);
        }
        oAuthTokenModel.setoauthScopes(oAuthScopesList);
    }
    
    private void extractAuthTokenDetails(final OAuthTokenModel oAuthTokenModel, final DataObject oAuthDobj) throws DataAccessException {
        final Long oAuthTokenID = oAuthTokenModel.getOAuthTokenId();
        final List<OAuthTokenMetaModel> oAuthTokenMetaModelList = new ArrayList<OAuthTokenMetaModel>();
        final Iterator authTokenDetailsTtr = oAuthDobj.getRows("AuthenticationTokenDetails", new Criteria(Column.getColumn("AuthenticationTokenDetails", "OAUTH_TOKEN_ID"), (Object)oAuthTokenID, 0));
        while (authTokenDetailsTtr != null && authTokenDetailsTtr.hasNext()) {
            final OAuthTokenMetaModel oAuthTokenMetaModel = new OAuthTokenMetaModel();
            final Row authTokenDetailsRow = authTokenDetailsTtr.next();
            final int tokenType = (int)authTokenDetailsRow.get("TOKEN_TYPE");
            oAuthTokenMetaModel.setTokenType(this.getAccessTokenType(tokenType));
            if (tokenType == 2) {
                oAuthTokenMetaModel.setExpiresAt(DirectoryUtil.getInstance().longdateToString((Long)authTokenDetailsRow.get("EXPIRES_AT")));
            }
            else {
                oAuthTokenMetaModel.setExpiresAt("NA");
            }
            oAuthTokenMetaModelList.add(oAuthTokenMetaModel);
        }
        oAuthTokenModel.setoAuthTokenMetaModel(oAuthTokenMetaModelList);
    }
    
    private void extractOAuthTokenData(final OAuthMetaModel oAuthMetaModel, final DataObject oAuthDobj) throws DataAccessException {
        final Long oAuthMetaID = oAuthMetaModel.getoAuthMetaId();
        final List<OAuthTokenModel> oAuthTokenModelList = new ArrayList<OAuthTokenModel>();
        final OAuthTokenListModel oAuthTokenListModel = new OAuthTokenListModel();
        final Iterator oAuthTokenItr = oAuthDobj.getRows("OauthTokens", new Criteria(Column.getColumn("OauthTokens", "OAUTH_METADATA_ID"), (Object)oAuthMetaID, 0));
        while (oAuthTokenItr != null && oAuthTokenItr.hasNext()) {
            final OAuthTokenModel oAuthTokenModel = new OAuthTokenModel();
            final Row oAuthTokenRow = oAuthTokenItr.next();
            oAuthTokenModel.setOAuthTokenId((Long)oAuthTokenRow.get("OAUTH_TOKEN_ID"));
            oAuthTokenModel.setReferenceUser((String)oAuthTokenRow.get("REFERENCE_USER"));
            oAuthTokenModel.setAddedAt(DirectoryUtil.getInstance().longdateToString((Long)oAuthTokenRow.get("ADDED_AT")));
            this.extractAuthTokenDetails(oAuthTokenModel, oAuthDobj);
            this.extractTokenScopeDetails(oAuthTokenModel, oAuthDobj);
            oAuthTokenModelList.add(oAuthTokenModel);
        }
        oAuthTokenListModel.setOauthTokens(oAuthTokenModelList);
        oAuthMetaModel.setTokens(oAuthTokenListModel);
    }
    
    private void extractOAuthMetaPurposeDetails(final OAuthMetaModel oAuthMetaModel, final DataObject oAuthDobj) throws DataAccessException {
        final List oauthMetInfoList = new ArrayList();
        final Long oauthMetaID = oAuthMetaModel.getoAuthMetaId();
        final Iterator oauthMetaPurposeRelItr = oAuthDobj.getRows("OAuthMetaPurposeRel", new Criteria(Column.getColumn("OAuthMetaPurposeRel", "OAUTH_METADATA_ID"), (Object)oauthMetaID, 0));
        while (oauthMetaPurposeRelItr != null && oauthMetaPurposeRelItr.hasNext()) {
            final Row oauthMetaPurposeRelRow = oauthMetaPurposeRelItr.next();
            final OAuthMetaInfoModel oAuthMetaInfoModel = new OAuthMetaInfoModel();
            oAuthMetaInfoModel.setAddedAt((Long)oauthMetaPurposeRelRow.get("ADDED_AT"));
            oAuthMetaInfoModel.setAddedBy((Long)oauthMetaPurposeRelRow.get("ADDED_BY"));
            oAuthMetaInfoModel.setModifiedAt((Long)oauthMetaPurposeRelRow.get("MODIFIED_AT"));
            oAuthMetaInfoModel.setModifiedBy((Long)oauthMetaPurposeRelRow.get("MODIFIED_BY"));
            oAuthMetaInfoModel.setCustomerId((Long)oauthMetaPurposeRelRow.get("CUSTOMER_ID"));
            oauthMetInfoList.add(oAuthMetaInfoModel);
        }
        final OAuthMetaInfoListModel oAuthMetaInfoListModel = new OAuthMetaInfoListModel();
        oAuthMetaInfoListModel.setOauthMetaInfos(oauthMetInfoList);
        oAuthMetaModel.setMetaInfoListModel(oAuthMetaInfoListModel);
    }
    
    private OAuthMetaListModel extractOAuthMetaData(final OAuthMetaListModel oAuthMetaListModel, final DataObject oAuthDobj) throws DataAccessException {
        final List<OAuthMetaModel> oauthList = new ArrayList<OAuthMetaModel>();
        final Iterator oauthMetaItr = oAuthDobj.getRows("OauthMetadata");
        while (oauthMetaItr != null && oauthMetaItr.hasNext()) {
            final OAuthMetaModel oAuthMetaModel = new OAuthMetaModel();
            final Row oauthMetaRow = oauthMetaItr.next();
            oAuthMetaModel.setoAuthType((Integer)oauthMetaRow.get("DOMAIN_TYPE"));
            oAuthMetaModel.setoAuthMetaId((Long)oauthMetaRow.get("OAUTH_METADATA_ID"));
            oAuthMetaModel.setClientId((String)oauthMetaRow.get("OAUTH_CLIENT_ID"));
            this.extractOAuthMetaPurposeDetails(oAuthMetaModel, oAuthDobj);
            this.extractOAuthTokenData(oAuthMetaModel, oAuthDobj);
            oauthList.add(oAuthMetaModel);
        }
        oAuthMetaListModel.setOAuthMetas(oauthList);
        return oAuthMetaListModel;
    }
    
    public OAuthMetaListModel getOAuthMetas(final SearchOAuth searchOAuthMeta) throws Exception {
        final SelectQuery oauthMetaQuery = OauthDataHandler.getInstance().getOAuthBaseQuery();
        final Criteria criteria = SearchUtil.setSearchCriteria(searchOAuthMeta);
        if (criteria != null) {
            oauthMetaQuery.setCriteria(criteria);
        }
        final DataObject oAuthDobj = IdpsUtil.getPersistenceLite().get(oauthMetaQuery);
        OAuthMetaListModel oAuthMetaListModel = new OAuthMetaListModel();
        if (oAuthDobj != null && !oAuthDobj.isEmpty() && oAuthDobj.containsTable("OauthMetadata")) {
            oAuthMetaListModel = this.extractOAuthMetaData(oAuthMetaListModel, oAuthDobj);
        }
        return oAuthMetaListModel;
    }
    
    private OAuthMetaModel addOrUpdateOAuthDetails(final OAuthMetaInputModel oAuthMetaInputModel, final boolean add) throws Exception {
        int oauthType = -1;
        int domainType = -1;
        final Long userID = oAuthMetaInputModel.getUserId();
        final Long customerID = oAuthMetaInputModel.getCustomerId();
        Long oauthMetaID = oAuthMetaInputModel.getOauthMetaID();
        String clientID = oAuthMetaInputModel.getClientId();
        String clientSecret = oAuthMetaInputModel.getClientSecret();
        if (clientID != null) {
            clientID = clientID.trim();
        }
        if (clientSecret != null) {
            clientSecret = clientSecret.trim();
        }
        if (add) {
            Properties existingMetadata = null;
            oauthType = oAuthMetaInputModel.getoAuthType();
            domainType = IdpsFactoryProvider.getOauthImpl(oauthType).getDomainType();
            if (domainType == 3) {
                ((AzureOauthImpl)IdpsFactoryProvider.getOauthImpl(201)).handleAzureOAuth();
            }
            try {
                existingMetadata = OauthDataHandler.getInstance().getMetadataFromDomainType(customerID, oauthType, (Long)null);
            }
            catch (final Exception ex) {
                Logger.getLogger("OauthLogger").log(Level.FINE, "harmless exception");
            }
            if (existingMetadata != null) {
                throw new APIHTTPException("COM0010", new Object[] { "OAuth App details already exists" });
            }
            if (customerID == null) {
                throw new APIHTTPException("COM0014", new Object[] { "CustomerID is missing" });
            }
        }
        else if (oauthMetaID == null) {
            throw new APIHTTPException("COM0014", new Object[] { "OAUTH_METADATA_ID is missing" });
        }
        oauthMetaID = OauthDataHandler.getInstance().addOrUpdateOauthMetadata(customerID, userID, oauthMetaID, clientID, clientSecret, oauthType);
        if (add && domainType == 3) {
            MessageProvider.getInstance().hideMessage("IDP_AZURE_OAUTH_MSG", customerID);
        }
        if (domainType == -1) {
            domainType = (int)DBUtil.getValueFromDB("OauthMetadata", "OAUTH_METADATA_ID", (Object)oauthMetaID, "DOMAIN_TYPE");
        }
        this.syncAllDomainswithMatchingDomainType(customerID, oauthMetaID, domainType);
        final OAuthMetaModel oAuthMetaModel = new OAuthMetaModel();
        oAuthMetaModel.setoAuthMetaId(oauthMetaID);
        oAuthMetaModel.setClientId(clientID);
        oAuthMetaModel.setoAuthType(oauthType);
        return oAuthMetaModel;
    }
    
    public OAuthMetaModel addOAuthDetails(final OAuthMetaInputModel oAuthMetaInputModel) throws Exception {
        final OAuthMetaModel oAuthMetaModel = this.addOrUpdateOAuthDetails(oAuthMetaInputModel, true);
        return oAuthMetaModel;
    }
    
    public void updateOAuthDetails(final OAuthMetaInputModel oAuthMetaInputModel) throws Exception {
        this.addOrUpdateOAuthDetails(oAuthMetaInputModel, false);
    }
    
    public void deleteOAuthDetails(final SearchOAuth searchOAuth) throws Exception {
        final Long userID = searchOAuth.getUserId();
        final Long customerID = searchOAuth.getCustomerId();
        final Long oauthMetaID = searchOAuth.getOAUthMetaID();
        final int domainType = (int)DBUtil.getValueFromDB("OauthMetadata", "OAUTH_METADATA_ID", (Object)oauthMetaID, "DOMAIN_TYPE");
        OauthDataHandler.getInstance().deleteOAuthMetadata(oauthMetaID, customerID, userID);
        this.syncAllDomainswithMatchingDomainType(customerID, null, domainType);
    }
    
    private void syncAllDomainswithMatchingDomainType(final Long customerID, final Long oauthMetaID, final int domainType) throws Exception {
        if (oauthMetaID != null) {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("OauthTokens");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("OauthTokens", "OAUTH_METADATA_ID"), (Object)oauthMetaID, 0));
            DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
        }
        final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, (String)null, (String)null, Integer.valueOf(domainType));
        final List<Properties> domainProps = DMDomainDataHandler.getInstance().getDomains(domainQuery);
        if (domainProps != null && !domainProps.isEmpty()) {
            for (final Properties dmDomainProp : domainProps) {
                DirectoryUtil.getInstance().syncDomain(dmDomainProp, Boolean.valueOf(true));
            }
        }
        else {
            MessageProvider.getInstance().hideMessage("IDP_AZURE_OAUTH_MSG", customerID);
            MessageProvider.getInstance().hideMessage("IDP_AZURE_INVALID_CLIENT_MSG", customerID);
        }
    }
}
