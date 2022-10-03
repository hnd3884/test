package com.me.idps.core.oauth;

import com.adventnet.ds.query.SelectQuery;
import java.util.Properties;
import org.json.JSONObject;
import java.util.List;

public interface OauthServiceAPI
{
    List generateAccessTokenFromRefreshToken(final Long p0, final String p1, final String p2, final String p3, final String p4) throws OauthException;
    
    JSONObject generateTokens(final Long p0, final String p1, final String p2, final String p3, final String p4) throws OauthException;
    
    Properties fetchMetadata(final Long p0, final Long p1);
    
    boolean revokeTokens(final String p0, final String p1, final String p2);
    
    String getAuthorizeUrl(final Long p0, final Long p1, final String[] p2, final String p3);
    
    SelectQuery configureSelectQuery(final SelectQuery p0, final Long p1, final Long p2) throws Exception;
    
    int getDomainType();
}
