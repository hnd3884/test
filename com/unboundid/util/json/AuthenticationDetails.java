package com.unboundid.util.json;

import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.SASLQualityOfProtection;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.unboundid.ldap.sdk.PLAINBindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequestProperties;
import com.unboundid.ldap.sdk.EXTERNALBindRequest;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequestProperties;
import com.unboundid.ldap.sdk.CRAMMD5BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class AuthenticationDetails implements Serializable
{
    private static final String FIELD_AUTHENTICATION_ID = "authentication-id";
    private static final String FIELD_AUTHENTICATION_TYPE = "authentication-type";
    private static final String FIELD_AUTHORIZATION_ID = "authorization-id";
    private static final String FIELD_CONFIG_FILE_PATH = "config-file-path";
    private static final String FIELD_DN = "dn";
    private static final String FIELD_KDC_ADDRESS = "kdc-address";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_PASSWORD_FILE = "password-file";
    private static final String FIELD_QOP = "qop";
    private static final String FIELD_REALM = "realm";
    private static final String FIELD_RENEW_TGT = "renew-tgt";
    private static final String FIELD_REQUIRE_CACHED_CREDENTIALS = "require-cached-credentials";
    private static final String FIELD_TICKET_CACHE_PATH = "ticket-cache-path";
    private static final String FIELD_USE_SUBJECT_CREDS_ONLY = "use-subject-credentials-only";
    private static final String FIELD_USE_TICKET_CACHE = "use-ticket-cache";
    private static final long serialVersionUID = 2798778432389082274L;
    private final BindRequest bindRequest;
    
    AuthenticationDetails(final JSONObject connectionDetailsObject) throws LDAPException {
        final JSONObject o = LDAPConnectionDetailsJSONSpecification.getObject(connectionDetailsObject, "authentication-details");
        if (o == null) {
            this.bindRequest = null;
            return;
        }
        final String authType = LDAPConnectionDetailsJSONSpecification.getString(o, "authentication-type", null);
        final String loweAuthType = StaticUtils.toLowerCase(authType);
        if (loweAuthType == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_MISSING_REQUIRED_FIELD.get("authentication-details", "authentication-type"));
        }
        if (loweAuthType.equals("none")) {
            LDAPConnectionDetailsJSONSpecification.validateAllowedFields(o, "authentication-details", "authentication-type");
            this.bindRequest = null;
        }
        else if (loweAuthType.equals("simple")) {
            validateAllowedFields(o, authType, "dn", "password", "password-file");
            final String dn = LDAPConnectionDetailsJSONSpecification.getString(o, "dn", null);
            if (dn == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_MISSING_REQUIRED_FIELD_FOR_AUTH_TYPE.get("dn", authType));
            }
            final String password = getPassword(o, authType, false);
            this.bindRequest = new SimpleBindRequest(dn, password);
        }
        else if (loweAuthType.equals("cram-md5") || loweAuthType.equals("crammd5")) {
            validateAllowedFields(o, authType, "authentication-id", "password", "password-file");
            final String authID = LDAPConnectionDetailsJSONSpecification.getString(o, "authentication-id", null);
            if (authID == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_MISSING_REQUIRED_FIELD_FOR_AUTH_TYPE.get("authentication-id", authType));
            }
            final String password = getPassword(o, authType, false);
            this.bindRequest = new CRAMMD5BindRequest(authID, password);
        }
        else if (loweAuthType.equals("digest-md5") || loweAuthType.equals("digestmd5")) {
            validateAllowedFields(o, authType, "authentication-id", "authorization-id", "password", "password-file", "qop", "realm");
            final String authID = LDAPConnectionDetailsJSONSpecification.getString(o, "authentication-id", null);
            if (authID == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_MISSING_REQUIRED_FIELD_FOR_AUTH_TYPE.get("authentication-id", authType));
            }
            final String password = getPassword(o, authType, false);
            final DIGESTMD5BindRequestProperties properties = new DIGESTMD5BindRequestProperties(authID, password);
            properties.setAuthorizationID(LDAPConnectionDetailsJSONSpecification.getString(o, "authorization-id", null));
            properties.setRealm(LDAPConnectionDetailsJSONSpecification.getString(o, "realm", null));
            properties.setAllowedQoP(getAllowedQoP(o));
            this.bindRequest = new DIGESTMD5BindRequest(properties, new Control[0]);
        }
        else if (loweAuthType.equals("external")) {
            validateAllowedFields(o, authType, "authorization-id");
            final String authzID = LDAPConnectionDetailsJSONSpecification.getString(o, "authorization-id", null);
            this.bindRequest = new EXTERNALBindRequest(authzID);
        }
        else if (loweAuthType.equals("gssapi") || loweAuthType.equals("gss-api")) {
            validateAllowedFields(o, authType, "authentication-id", "authorization-id", "password", "password-file", "config-file-path", "kdc-address", "qop", "realm", "renew-tgt", "require-cached-credentials", "ticket-cache-path", "use-subject-credentials-only", "use-ticket-cache");
            final String authID = LDAPConnectionDetailsJSONSpecification.getString(o, "authentication-id", null);
            if (authID == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_MISSING_REQUIRED_FIELD_FOR_AUTH_TYPE.get("authentication-id", authType));
            }
            final String password = getPassword(o, authType, true);
            final GSSAPIBindRequestProperties properties2 = new GSSAPIBindRequestProperties(authID, password);
            properties2.setAuthorizationID(LDAPConnectionDetailsJSONSpecification.getString(o, "authorization-id", null));
            properties2.setRealm(LDAPConnectionDetailsJSONSpecification.getString(o, "realm", null));
            properties2.setAllowedQoP(getAllowedQoP(o));
            properties2.setConfigFilePath(LDAPConnectionDetailsJSONSpecification.getString(o, "config-file-path", null));
            properties2.setKDCAddress(LDAPConnectionDetailsJSONSpecification.getString(o, "kdc-address", null));
            properties2.setRenewTGT(LDAPConnectionDetailsJSONSpecification.getBoolean(o, "renew-tgt", false));
            properties2.setRequireCachedCredentials(LDAPConnectionDetailsJSONSpecification.getBoolean(o, "require-cached-credentials", false));
            properties2.setTicketCachePath(LDAPConnectionDetailsJSONSpecification.getString(o, "ticket-cache-path", null));
            properties2.setUseSubjectCredentialsOnly(LDAPConnectionDetailsJSONSpecification.getBoolean(o, "use-subject-credentials-only", true));
            properties2.setUseTicketCache(LDAPConnectionDetailsJSONSpecification.getBoolean(o, "use-ticket-cache", true));
            if (password == null && !properties2.requireCachedCredentials()) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_MISSING_GSSAPI_PASSWORD.get("password", "password-file", authType, "require-cached-credentials"));
            }
            this.bindRequest = new GSSAPIBindRequest(properties2, new Control[0]);
        }
        else {
            if (!loweAuthType.equals("plain")) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_UNRECOGNIZED_TYPE.get(authType));
            }
            validateAllowedFields(o, authType, "authentication-id", "authorization-id", "password", "password-file");
            final String authID = LDAPConnectionDetailsJSONSpecification.getString(o, "authentication-id", null);
            if (authID == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_MISSING_REQUIRED_FIELD_FOR_AUTH_TYPE.get("authentication-id", authType));
            }
            final String authzID2 = LDAPConnectionDetailsJSONSpecification.getString(o, "authorization-id", null);
            final String password2 = getPassword(o, authType, false);
            this.bindRequest = new PLAINBindRequest(authID, authzID2, password2);
        }
    }
    
    BindRequest getBindRequest() {
        return this.bindRequest;
    }
    
    private static void validateAllowedFields(final JSONObject o, final String authType, final String... allowedFields) throws LDAPException {
        final HashSet<String> s = new HashSet<String>(Arrays.asList(allowedFields));
        for (final String fieldName : o.getFields().keySet()) {
            if (fieldName.equals("authentication-type")) {
                continue;
            }
            if (!s.contains(fieldName)) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_FIELD_NOT_PERMITTED_FOR_AUTH_TYPE.get(fieldName, authType));
            }
        }
    }
    
    private static String getPassword(final JSONObject o, final String authType, final boolean optional) throws LDAPException {
        final String password = LDAPConnectionDetailsJSONSpecification.getString(o, "password", null);
        if (password != null) {
            LDAPConnectionDetailsJSONSpecification.rejectConflictingFields(o, "password", "password-file");
            return password;
        }
        final String path = LDAPConnectionDetailsJSONSpecification.getString(o, "password-file", null);
        if (path != null) {
            return LDAPConnectionDetailsJSONSpecification.getStringFromFile(path, "password-file");
        }
        if (optional) {
            return null;
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_NO_PASSWORD.get("password", "password-file", authType));
    }
    
    private static List<SASLQualityOfProtection> getAllowedQoP(final JSONObject o) throws LDAPException {
        final JSONValue v = o.getField("qop");
        if (v == null) {
            return Collections.singletonList(SASLQualityOfProtection.AUTH);
        }
        if (v instanceof JSONString) {
            return SASLQualityOfProtection.decodeQoPList(((JSONString)v).stringValue());
        }
        if (v instanceof JSONArray) {
            final JSONArray a = (JSONArray)v;
            final ArrayList<SASLQualityOfProtection> qopList = new ArrayList<SASLQualityOfProtection>(a.size());
            for (final JSONValue av : a.getValues()) {
                if (!(av instanceof JSONString)) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_INVALID_QOP.get("qop"));
                }
                final SASLQualityOfProtection qop = SASLQualityOfProtection.forName(((JSONString)av).stringValue());
                if (qop == null) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_INVALID_QOP.get("qop"));
                }
                qopList.add(qop);
            }
            return qopList;
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_AUTH_DETAILS_INVALID_QOP.get("qop"));
    }
}
