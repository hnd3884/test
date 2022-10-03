package com.me.ems.onpremise.useraccount.validators;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import java.util.Map;

public class TokenValidation
{
    public Map tokenMap;
    private static Logger logger;
    
    public TokenValidation(final Map tokenMap) {
        this.tokenMap = tokenMap;
    }
    
    private TokenValidation() {
    }
    
    public Map validateToken() throws APIException {
        try {
            final String token = this.tokenMap.get("token");
            final DataObject tokenDO = this.getTokenDetails(token);
            if (tokenDO == null || tokenDO.isEmpty()) {
                throw new APIException(Response.Status.NOT_FOUND, "UAC010", "ems.admin.admin.token_invalid");
            }
            final Row aaaUserToken = tokenDO.getRow("AaaUserLinkDetails");
            final Long expiryTime = aaaUserToken.getLong("EXPIRY_TIME");
            final Integer tokenType = aaaUserToken.getInt("TOKEN_TYPE");
            final Long currentTime = System.currentTimeMillis();
            if (currentTime > expiryTime) {
                final String errorCode = (tokenType == 101) ? "UAC011" : "UAC014";
                throw new APIException(Response.Status.BAD_REQUEST, errorCode, "ems.admin.admin.token_expired");
            }
            final Row aaaLogin = tokenDO.getRow("AaaLogin");
            final Map<String, Object> tokenDetails = (Map<String, Object>)aaaUserToken.getAsJSON().toMap().entrySet().stream().collect(Collectors.toMap(k -> k.getKey().toUpperCase(), l -> l.getValue()));
            final Map<String, Object> loginDetails = (Map<String, Object>)aaaLogin.getAsJSON().toMap().entrySet().stream().collect(Collectors.toMap(k -> k.getKey().toUpperCase(), l -> l.getValue()));
            this.tokenMap.putAll(tokenDetails);
            this.tokenMap.putAll(loginDetails);
            return this.tokenMap;
        }
        catch (final APIException ex) {
            TokenValidation.logger.log(Level.WARNING, "Exception while validating token", (Throwable)ex);
            throw ex;
        }
        catch (final Exception ex2) {
            TokenValidation.logger.log(Level.WARNING, "Exception while validating token", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    private DataObject getTokenDetails(final String token) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUserLinkDetails"));
        selectQuery.addJoin(new Join("AaaUserLinkDetails", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("AaaUserLinkDetails", "TOKEN"), (Object)token, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "TOKEN"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "TOKEN_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "EXPIRY_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "CREATED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "DOMAINNAME"));
        final DataObject tokenDO = SyMUtil.getPersistence().get(selectQuery);
        return tokenDO;
    }
    
    static {
        TokenValidation.logger = Logger.getLogger("UserManagementLogger");
    }
}
