package com.adventnet.authentication.rememberme;

import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.authentication.Credential;
import com.adventnet.authentication.internal.WritableCredential;
import java.util.List;
import com.adventnet.authentication.UserPrincipal;
import com.adventnet.authentication.RolePrincipal;
import com.adventnet.authentication.util.AuthDBUtil;
import javax.security.auth.Subject;
import org.apache.commons.codec.digest.DigestUtils;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.util.UUID;
import javax.servlet.http.Cookie;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import javax.servlet.http.HttpSession;
import com.adventnet.persistence.DataAccessException;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.authentication.util.AuthUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.persistence.Persistence;
import com.zoho.framework.utils.crypto.EnDecrypt;
import java.util.logging.Logger;

public class RememberMeServiceImpl implements RememberMeService
{
    private static final Logger LOGGER;
    private static final EnDecrypt AES_UTIL;
    private static final Persistence PERSISTENCE;
    private static final ReadOnlyPersistence READONLYPERSISTENCE;
    private static String rememberMeCookieName;
    
    public RememberMeServiceImpl() {
        RememberMeServiceImpl.rememberMeCookieName = PersistenceInitializer.getConfigurationValue("rememberMeCookieName");
        RememberMeServiceImpl.rememberMeCookieName = ((RememberMeServiceImpl.rememberMeCookieName != null) ? RememberMeServiceImpl.rememberMeCookieName : "nks85rfb9");
    }
    
    @Override
    public Map<String, Object> hasValidAuthToken(final HttpServletRequest request) {
        final DataObject dobj = this.getRememberMeDetails(AuthUtil.getCookieValue(request, RememberMeServiceImpl.rememberMeCookieName));
        try {
            if (dobj.containsTable("AAARememberMeInfo")) {
                RememberMeServiceImpl.LOGGER.log(Level.FINE, "Auth Token is valid");
                final Row row = dobj.getFirstRow("AAARememberMeInfo");
                final Long userId = (Long)row.get("USER_ID");
                final Long expiryTime = (Long)row.get("EXPIRETIME");
                if (System.currentTimeMillis() > expiryTime) {
                    return null;
                }
                final Map<String, Object> userMap = new HashMap<String, Object>();
                final Row loginRow = dobj.getFirstRow("AaaLogin");
                userMap.put("loginName", loginRow.get("NAME"));
                userMap.put("domainName", loginRow.get("DOMAINNAME"));
                userMap.put("userId", userId);
                return userMap;
            }
        }
        catch (final Exception e) {
            RememberMeServiceImpl.LOGGER.log(Level.FINE, "Exception occurred while validating auth token");
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void updateRememberMeInfo(final HttpServletRequest request, final HttpServletResponse response) {
        final HttpSession session = request.getSession();
        final Long userId = (Long)session.getAttribute("authenticatedUserId");
        final Long currentTime = System.currentTimeMillis();
        String cookieValue = AuthUtil.getCookieValue(request, RememberMeServiceImpl.rememberMeCookieName);
        if ((cookieValue == null && userId != null) || this.getRememberMeDetails(cookieValue).isEmpty()) {
            this.deleteCookie(request, response, RememberMeServiceImpl.rememberMeCookieName);
            final String expirationTime = PersistenceInitializer.getConfigurationValue("rememberMeCookieExpTime");
            final int expiryTime = (int)((expirationTime != null) ? TimeUnit.DAYS.toSeconds(Integer.parseInt(expirationTime)) : TimeUnit.DAYS.toSeconds(7L));
            cookieValue = this.encode(RememberMeServiceImpl.AES_UTIL.encrypt(userId + "-" + currentTime + "-" + generateUniqueID()));
            final Row rowObj = new Row("AAARememberMeInfo");
            rowObj.set("USER_ID", (Object)userId);
            rowObj.set("TOKEN", (Object)this.hash(this.decode(cookieValue)));
            rowObj.set("CREATEDTIME", (Object)currentTime);
            rowObj.set("UPDATEDTIME", (Object)currentTime);
            rowObj.set("EXPIRETIME", (Object)(currentTime + TimeUnit.DAYS.toMillis(45L)));
            this.createCookie(request, response, RememberMeServiceImpl.rememberMeCookieName, cookieValue, expiryTime);
            try {
                final DataObject dobj = RememberMeServiceImpl.PERSISTENCE.constructDataObject();
                dobj.addRow(rowObj);
                RememberMeServiceImpl.PERSISTENCE.add(dobj);
            }
            catch (final DataAccessException e) {
                e.printStackTrace();
            }
        }
        else {
            final DataObject dobj2 = (DataObject)this.getRememberMeDetails(cookieValue).clone();
            try {
                this.deleteCookie(request, response, RememberMeServiceImpl.rememberMeCookieName);
                if (dobj2.containsTable("AAARememberMeInfo")) {
                    final Row row = dobj2.getFirstRow("AAARememberMeInfo");
                    final Long updatedExpireTime = (long)row.get("EXPIRETIME") - currentTime;
                    final String updatedToken = RememberMeServiceImpl.AES_UTIL.encrypt(row.get("USER_ID") + "-" + currentTime + "-" + generateUniqueID());
                    final String encodedToken = this.encode(updatedToken);
                    row.set("TOKEN", (Object)this.hash(updatedToken));
                    row.set("UPDATEDTIME", (Object)currentTime);
                    dobj2.updateRow(row);
                    RememberMeServiceImpl.PERSISTENCE.update(dobj2);
                    this.createCookie(request, response, RememberMeServiceImpl.rememberMeCookieName, encodedToken, (updatedExpireTime / 1000L).intValue());
                }
            }
            catch (final DataAccessException e2) {
                RememberMeServiceImpl.LOGGER.info("Exception occurred while updating the cookie :: " + e2.getMessage() + " :: removing the remember me cookie ");
                e2.printStackTrace();
                this.deleteCookie(request, response, RememberMeServiceImpl.rememberMeCookieName);
            }
        }
    }
    
    @Override
    public void removeRememberMeInfo(final HttpServletRequest request, final HttpServletResponse response) {
        final String cookieValue = AuthUtil.getCookieValue(request, RememberMeServiceImpl.rememberMeCookieName);
        if (cookieValue == null) {
            return;
        }
        final String token = this.hash(this.decode(cookieValue));
        try {
            RememberMeServiceImpl.PERSISTENCE.delete(new Criteria(Column.getColumn("AAARememberMeInfo", "TOKEN"), (Object)token, 0));
        }
        catch (final DataAccessException dae) {
            RememberMeServiceImpl.LOGGER.info("exception occurred while removing remember me token info from database.. it will be removed when the expiry of the token is reached.");
            dae.printStackTrace();
        }
        this.deleteCookie(request, response, RememberMeServiceImpl.rememberMeCookieName);
    }
    
    protected void createCookie(final HttpServletRequest request, final HttpServletResponse response, final String rememberMeCookieName, final String cookieValue, final int expirationTime) {
        final Cookie cookie = new Cookie(rememberMeCookieName, cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(expirationTime);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.getScheme().equals("https"));
        response.addCookie(cookie);
    }
    
    protected void deleteCookie(final HttpServletRequest request, final HttpServletResponse response, final String cookieName) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null && cookieName != null) {
            for (final Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
    
    protected static String generateUniqueID() {
        return "ME_" + UUID.randomUUID();
    }
    
    private String decode(final String encValue) {
        return AuthUtil.base64decode(encValue);
    }
    
    @Override
    public DataObject getRememberMeDetails(final String cookieValue) {
        if (cookieValue == null) {
            return (DataObject)new WritableDataObject();
        }
        try {
            final String encValue = this.decode(cookieValue);
            final String origValue = RememberMeServiceImpl.AES_UTIL.decrypt(encValue);
            final String[] decodedValues = origValue.split("-");
            final Long userIdinCookie = Long.parseLong(decodedValues[0]);
            final Long updatedTime = Long.parseLong(decodedValues[1]);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AAARememberMeInfo"));
            sq.addSelectColumn(Column.getColumn("AAARememberMeInfo", "*"));
            sq.addSelectColumn(Column.getColumn("AaaLogin", "*"));
            Criteria cri = new Criteria(Column.getColumn("AAARememberMeInfo", "TOKEN"), (Object)this.hash(encValue), 0);
            cri = ((userIdinCookie != null) ? cri.and(new Criteria(Column.getColumn("AAARememberMeInfo", "USER_ID"), (Object)userIdinCookie, 0)) : cri);
            cri = ((updatedTime != null) ? cri.and(new Criteria(Column.getColumn("AAARememberMeInfo", "UPDATEDTIME"), (Object)updatedTime, 0)) : cri);
            final Join join = new Join("AAARememberMeInfo", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            sq.setCriteria(cri);
            sq.addJoin(join);
            return RememberMeServiceImpl.READONLYPERSISTENCE.get(sq);
        }
        catch (final DataAccessException e) {
            RememberMeServiceImpl.LOGGER.log(Level.WARNING, "exception occurred while obtaining the details of rememberMeToken :: " + e.getMessage());
            e.printStackTrace();
            return (DataObject)new WritableDataObject();
        }
    }
    
    private String hash(final String value) {
        return DigestUtils.sha256Hex(value);
    }
    
    private String encode(final String value) {
        return AuthUtil.base64encode(value);
    }
    
    @Override
    public Subject constructSubject(Subject subject, final Map<String, Object> userMap, final HttpServletRequest request) {
        final String loginName = userMap.get("loginName");
        final String domainName = userMap.get("domainName");
        final String serviceName = userMap.get("serviceName");
        subject = new Subject();
        try {
            final DataObject accountDO = AuthDBUtil.getAccountDO(loginName, serviceName, domainName);
            final List roleList = AuthDBUtil.getAuthorizedRoles(accountDO);
            final WritableCredential wcr = this.constructWritableCredential(accountDO);
            String roleName = null;
            for (int noOfRoles = roleList.size(), i = 0; i < noOfRoles; ++i) {
                roleName = roleList.get(i);
                subject.getPrincipals().add(new RolePrincipal(roleName));
                wcr.addRole(roleName);
            }
            final Long sessionId = this.createAccSession(accountDO, request);
            wcr.setSessionId(sessionId);
            subject.getPublicCredentials().add(wcr);
            final Credential cr = AuthUtil.transform(wcr);
            if (cr != null) {
                AuthUtil.setUserCredential(cr);
                subject.getPublicCredentials().add(cr);
            }
            subject.getPrincipals().add(new UserPrincipal(loginName));
        }
        catch (final DataAccessException e) {
            RememberMeServiceImpl.LOGGER.log(Level.SEVERE, "exception occurred while construting subject :: " + e.getMessage());
            e.printStackTrace();
        }
        return subject;
    }
    
    private WritableCredential constructWritableCredential(final DataObject accountDO) throws DataAccessException {
        final WritableCredential wcr = new WritableCredential();
        try {
            Row temp = null;
            if (accountDO.containsTable("AaaLogin")) {
                temp = accountDO.getFirstRow("AaaLogin");
                wcr.setUserId((Long)temp.get("USER_ID"));
                wcr.setLoginId((Long)temp.get("LOGIN_ID"));
                wcr.setLoginName((String)temp.get("NAME"));
                wcr.setDomainName((String)temp.get("DOMAINNAME"));
            }
            if (accountDO.containsTable("AaaAccount")) {
                temp = accountDO.getFirstRow("AaaAccount");
                wcr.setAccountId((Long)temp.get("ACCOUNT_ID"));
            }
            if (accountDO.containsTable("AaaService")) {
                temp = accountDO.getFirstRow("AaaService");
                wcr.setServiceName((String)temp.get("NAME"));
            }
            if (accountDO.containsTable("AaaUserProfile")) {
                temp = accountDO.getFirstRow("AaaUserProfile");
                wcr.setTimeZone((String)temp.get("TIMEZONE"));
                wcr.setCountryCode((String)temp.get("COUNTRY_CODE"));
                wcr.setLangCode((String)temp.get("LANGUAGE_CODE"));
            }
        }
        catch (final DataAccessException dae) {
            throw new DataAccessException("DataAccessException occured while constructing Wr.credential", (Throwable)dae);
        }
        return wcr;
    }
    
    private Long createAccSession(final DataObject accountDO, final HttpServletRequest request) throws DataAccessException {
        try {
            final String serverHostName = (request == null) ? AuthUtil.getLocalHostName() : request.getServerName();
            final String hostName = (request == null) ? serverHostName : request.getRemoteHost();
            final Row sessionRow = new Row("AaaAccSession");
            sessionRow.set("ACCOUNT_ID", accountDO.getFirstValue("AaaAccount", "ACCOUNT_ID"));
            sessionRow.set("APPLICATION_HOST", (Object)serverHostName);
            sessionRow.set("OPENTIME", (Object)new Long(System.currentTimeMillis()));
            sessionRow.set("USER_HOST", (Object)hostName);
            sessionRow.set("USER_HOST_NAME", (Object)hostName);
            sessionRow.set("STATUS", (Object)"ACTIVE");
            if ("success".equals(request.getSession().getAttribute("NTLM"))) {
                sessionRow.set("AUTHENTICATOR", (Object)"NTLM");
            }
            DataObject sessionDO = DataAccess.constructDataObject();
            sessionDO.addRow(sessionRow);
            sessionDO = AuthDBUtil.getPersistence("PurePersistence").add(sessionDO);
            return (Long)sessionDO.getFirstValue("AaaAccSession", "SESSION_ID");
        }
        catch (final Exception e) {
            throw new DataAccessException("Exception occured while creating account session", (Throwable)e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(RememberMeServiceImpl.class.getName());
        AES_UTIL = (EnDecrypt)new EnDecryptAES256Impl();
        PERSISTENCE = AuthDBUtil.getPersistence("Persistence");
        READONLYPERSISTENCE = AuthDBUtil.getCachedPersistence("PureCachedPersistence");
        RememberMeServiceImpl.rememberMeCookieName = null;
    }
}
