package com.unboundid.util.json;

import com.unboundid.util.StaticUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPConnectionPoolHealthCheck;
import com.unboundid.ldap.sdk.PostConnectProcessor;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ByteStringBuffer;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ServerSet;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPConnectionDetailsJSONSpecification
{
    static final String FIELD_AUTHENTICATION_DETAILS = "authentication-details";
    static final String FIELD_COMMUNICATION_SECURITY = "communication-security";
    static final String FIELD_CONNECTION_OPTIONS = "connection-options";
    static final String FIELD_CONNECTION_POOL_OPTIONS = "connection-pool-options";
    static final String FIELD_SERVER_DETAILS = "server-details";
    private final BindRequest bindRequest;
    private final ConnectionPoolOptions connectionPoolOptionsSpec;
    private final SecurityOptions securityOptionsSpec;
    private final ServerSet serverSet;
    
    public LDAPConnectionDetailsJSONSpecification(final JSONObject connectionDetailsObject) throws LDAPException {
        validateTopLevelFields(connectionDetailsObject);
        try {
            this.securityOptionsSpec = new SecurityOptions(connectionDetailsObject);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(le.getResultCode(), JSONMessages.ERR_LDAP_SPEC_ERROR_PROCESSING_FIELD.get("communication-security", le.getMessage()), le);
        }
        ConnectionOptions connectionOptionsSpec;
        try {
            connectionOptionsSpec = new ConnectionOptions(connectionDetailsObject);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            throw new LDAPException(le2.getResultCode(), JSONMessages.ERR_LDAP_SPEC_ERROR_PROCESSING_FIELD.get("connection-options", le2.getMessage()), le2);
        }
        try {
            final ServerDetails serverDetailsSpec = new ServerDetails(connectionDetailsObject, this.securityOptionsSpec, connectionOptionsSpec);
            this.serverSet = serverDetailsSpec.getServerSet();
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            throw new LDAPException(le2.getResultCode(), JSONMessages.ERR_LDAP_SPEC_ERROR_PROCESSING_FIELD.get("server-details", le2.getMessage()), le2);
        }
        try {
            final AuthenticationDetails authenticationDetailsSpec = new AuthenticationDetails(connectionDetailsObject);
            this.bindRequest = authenticationDetailsSpec.getBindRequest();
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            throw new LDAPException(le2.getResultCode(), JSONMessages.ERR_LDAP_SPEC_ERROR_PROCESSING_FIELD.get("authentication-details", le2.getMessage()), le2);
        }
        try {
            this.connectionPoolOptionsSpec = new ConnectionPoolOptions(connectionDetailsObject);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            throw new LDAPException(le2.getResultCode(), JSONMessages.ERR_LDAP_SPEC_ERROR_PROCESSING_FIELD.get("connection-pool-options", le2.getMessage()), le2);
        }
    }
    
    public static LDAPConnectionDetailsJSONSpecification fromString(final String jsonString) throws JSONException, LDAPException {
        return new LDAPConnectionDetailsJSONSpecification(new JSONObject(jsonString));
    }
    
    public static LDAPConnectionDetailsJSONSpecification fromFile(final String path) throws IOException, JSONException, LDAPException {
        return fromFile(new File(path));
    }
    
    public static LDAPConnectionDetailsJSONSpecification fromFile(final File file) throws IOException, JSONException, LDAPException {
        return fromInputStream(new FileInputStream(file));
    }
    
    public static LDAPConnectionDetailsJSONSpecification fromInputStream(final InputStream inputStream) throws IOException, JSONException, LDAPException {
        try {
            final ByteStringBuffer b = new ByteStringBuffer();
            final byte[] readBuffer = new byte[8192];
            while (true) {
                final int bytesRead = inputStream.read(readBuffer);
                if (bytesRead < 0) {
                    break;
                }
                b.append(readBuffer, 0, bytesRead);
            }
            return new LDAPConnectionDetailsJSONSpecification(new JSONObject(b.toString()));
        }
        finally {
            inputStream.close();
        }
    }
    
    public ServerSet getServerSet() {
        return this.serverSet;
    }
    
    public BindRequest getBindRequest() {
        return this.bindRequest;
    }
    
    public LDAPConnection createConnection() throws LDAPException {
        final LDAPConnection connection = this.createUnauthenticatedConnection();
        if (this.bindRequest != null) {
            try {
                connection.bind(this.bindRequest);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                connection.close();
                throw le;
            }
        }
        return connection;
    }
    
    public LDAPConnection createUnauthenticatedConnection() throws LDAPException {
        return this.serverSet.getConnection();
    }
    
    public LDAPConnectionPool createConnectionPool(final int initialConnections, final int maximumConnections) throws LDAPException {
        final LDAPConnectionPool connectionPool = new LDAPConnectionPool(this.serverSet, this.bindRequest, initialConnections, maximumConnections, this.connectionPoolOptionsSpec.getInitialConnectThreads(), this.securityOptionsSpec.getPostConnectProcessor(), false, this.connectionPoolOptionsSpec.getHealthCheck());
        this.connectionPoolOptionsSpec.applyConnectionPoolSettings(connectionPool);
        return connectionPool;
    }
    
    public LDAPConnectionPool createUnauthenticatedConnectionPool(final int initialConnections, final int maximumConnections) throws LDAPException {
        final LDAPConnectionPool connectionPool = new LDAPConnectionPool(this.serverSet, null, initialConnections, maximumConnections, this.connectionPoolOptionsSpec.getInitialConnectThreads(), this.securityOptionsSpec.getPostConnectProcessor(), false, this.connectionPoolOptionsSpec.getHealthCheck());
        this.connectionPoolOptionsSpec.applyConnectionPoolSettings(connectionPool);
        return connectionPool;
    }
    
    private static void validateTopLevelFields(final JSONObject o) throws LDAPException {
        boolean serverDetailsProvided = false;
        for (final String s : o.getFields().keySet()) {
            if (s.equals("server-details")) {
                serverDetailsProvided = true;
            }
            else {
                if (s.equals("connection-options") || s.equals("communication-security") || s.equals("authentication-details")) {
                    continue;
                }
                if (s.equals("connection-pool-options")) {
                    continue;
                }
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_UNRECOGNIZED_TOP_LEVEL_FIELD.get(s));
            }
        }
        if (!serverDetailsProvided) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_MISSING_SERVER_DETAILS.get("server-details"));
        }
    }
    
    static void validateAllowedFields(final JSONObject o, final String f, final String... a) throws LDAPException {
        final HashSet<String> s = new HashSet<String>(Arrays.asList(a));
        for (final String n : o.getFields().keySet()) {
            if (!s.contains(n)) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_UNRECOGNIZED_FIELD.get(n, f));
            }
        }
    }
    
    static boolean getBoolean(final JSONObject o, final String f, final boolean d) throws LDAPException {
        final JSONValue v = o.getField(f);
        if (v == null) {
            return d;
        }
        if (v instanceof JSONBoolean) {
            return ((JSONBoolean)v).booleanValue();
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_NOT_BOOLEAN.get(f));
    }
    
    static Integer getInt(final JSONObject o, final String f, final Integer d, final Integer n, final Integer x) throws LDAPException {
        final JSONValue v = o.getField(f);
        if (v == null) {
            return d;
        }
        if (v instanceof JSONNumber) {
            try {
                final int i = ((JSONNumber)v).getValue().intValueExact();
                if (n != null && i < n) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_BELOW_MIN.get(f, n));
                }
                if (x != null && i > x) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_ABOVE_MAX.get(f, n));
                }
                return i;
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw le;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_NOT_INTEGER.get(f), e);
            }
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_NOT_INTEGER.get(f));
    }
    
    static Long getLong(final JSONObject o, final String f, final Long d, final Long n, final Long x) throws LDAPException {
        final JSONValue v = o.getField(f);
        if (v == null) {
            return d;
        }
        if (v instanceof JSONNumber) {
            try {
                final long l = ((JSONNumber)v).getValue().longValueExact();
                if (n != null && l < n) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_BELOW_MIN.get(f, n));
                }
                if (x != null && l > x) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_ABOVE_MAX.get(f, n));
                }
                return l;
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw le;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_NOT_INTEGER.get(f), e);
            }
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_NOT_INTEGER.get(f));
    }
    
    static JSONObject getObject(final JSONObject o, final String f) throws LDAPException {
        final JSONValue v = o.getField(f);
        if (v == null) {
            return null;
        }
        if (v instanceof JSONObject) {
            return (JSONObject)v;
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_NOT_OBJECT.get(f));
    }
    
    static String getString(final JSONObject o, final String f, final String d) throws LDAPException {
        final JSONValue v = o.getField(f);
        if (v == null) {
            return d;
        }
        if (v instanceof JSONString) {
            return ((JSONString)v).stringValue();
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_VALUE_NOT_STRING.get(f));
    }
    
    static String getStringFromFile(final String path, final String fieldName) throws LDAPException {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(path));
            final String line = r.readLine();
            if (line == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_READ_FILE_EMPTY.get(path, fieldName));
            }
            if (r.readLine() != null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_READ_FILE_MULTIPLE_LINES.get(path, fieldName));
            }
            if (line.isEmpty()) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_READ_FILE_EMPTY_LINE.get(path, fieldName));
            }
            return line;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_READ_FILE_ERROR.get(path, fieldName, StaticUtils.getExceptionMessage(e)), e);
        }
        finally {
            if (r != null) {
                try {
                    r.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
            }
        }
    }
    
    static void rejectConflictingFields(final JSONObject o, final String existingField, final String... conflictingFields) throws LDAPException {
        for (final String fieldName : conflictingFields) {
            if (o.getField(fieldName) != null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_CONFLICTING_FIELD.get(fieldName, existingField));
            }
        }
    }
    
    static void rejectUnresolvedDependency(final JSONObject o, final String requiredField, final String... dependentFields) throws LDAPException {
        for (final String fieldName : dependentFields) {
            if (o.getField(fieldName) != null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_LDAP_SPEC_MISSING_DEPENDENT_FIELD.get(fieldName, requiredField));
            }
        }
    }
}
