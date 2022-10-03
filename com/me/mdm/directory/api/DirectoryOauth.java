package com.me.mdm.directory.api;

import java.util.Hashtable;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.me.idps.core.api.IdpsAPIException;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.me.idps.core.oauth.OauthDataHandler;
import com.me.idps.core.service.azure.AzureOauthHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DirectoryOauth extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long customerID = apiRequest.getParameterList().get("customer_id");
            if (customerID == null) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            final Integer domainType = Integer.parseInt((String)apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("resource_identifier").get("dirclien_id"));
            if (domainType != 3) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            final JSONObject request = apiRequest.toJSONObject();
            final Long userID = APIUtil.getUserID(request);
            final Long oauthId = AzureOauthHandler.getInstance().getAccessToken(customerID, userID, request.getJSONObject("msg_body"));
            if (oauthId == null) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final Properties oauthProps = OauthDataHandler.getInstance().getOauthTokensById(oauthId, (Criteria)null, false);
            final JSONObject body = new JSONObject();
            body.put("OAUTH_TOKEN_ID", (Object)oauthId);
            body.put("DOMAIN_ID", ((Hashtable<K, Object>)oauthProps).get("DOMAIN_ID"));
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)body);
            response.put("status", 201);
            return response;
        }
        catch (final IdpsAPIException e) {
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
        catch (final APIHTTPException e2) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in doGet /directory/oauth/:id", e2);
            throw e2;
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doGet /directory/oauth/:id", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            final JSONObject request = apiRequest.toJSONObject();
            final Long customerID = APIUtil.getCustomerID(request);
            final Integer domain_typeL = Integer.parseInt((String)request.getJSONObject("msg_header").getJSONObject("resource_identifier").get("dirclien_id"));
            final String state = APIUtil.getStringFilter(request, "state");
            final String spaceSeperatedScopes = APIUtil.getStringFilter(request, "scope");
            final org.json.simple.JSONObject params = new org.json.simple.JSONObject();
            if (!SyMUtil.isStringEmpty(spaceSeperatedScopes)) {
                params.put((Object)"scope", (Object)spaceSeperatedScopes);
            }
            if (!SyMUtil.isStringEmpty(state)) {
                params.put((Object)"state", (Object)state);
            }
            params.put((Object)"CUSTOMER_ID", (Object)customerID);
            params.put((Object)"USER_ID", (Object)APIUtil.getUserID(request));
            final JSONObject urlObject = IdpsFactoryProvider.getIdpsAccessAPI((int)domain_typeL).getCustomParams(params);
            response.put("RESPONSE", (Object)urlObject);
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException ", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
        catch (final APIHTTPException e2) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in doGet /directory/oauth/:id", e2);
            throw e2;
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doGet /directory/oauth/:id", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
