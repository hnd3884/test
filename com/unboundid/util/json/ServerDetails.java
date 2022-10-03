package com.unboundid.util.json;

import com.unboundid.util.ObjectPair;
import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import javax.net.SocketFactory;
import com.unboundid.ldap.sdk.SingleServerSet;
import com.unboundid.ldap.sdk.RoundRobinServerSet;
import com.unboundid.ldap.sdk.FewestConnectionsServerSet;
import com.unboundid.ldap.sdk.FastestConnectServerSet;
import java.util.List;
import com.unboundid.ldap.sdk.FailoverServerSet;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ServerSet;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class ServerDetails
{
    private static final String FIELD_ADDRESS = "address";
    private static final String FIELD_FAILOVER_ORDER = "failover-order";
    private static final String FIELD_FAILOVER_SET = "failover-set";
    private static final String FIELD_FASTEST_CONNECT_SET = "fastest-connect-set";
    private static final String FIELD_FEWEST_CONNECTIONS_SET = "fewest-connections-set";
    private static final String FIELD_MAX_FAILOVER_CONN_AGE_MILLIS = "maximum-failover-connection-age-millis";
    private static final String FIELD_PORT = "port";
    private static final String FIELD_ROUND_ROBIN_SET = "round-robin-set";
    private static final String FIELD_SERVERS = "servers";
    private static final String FIELD_SINGLE_SERVER = "single-server";
    private final ServerSet serverSet;
    
    ServerDetails(final JSONObject connectionDetailsObject, final SecurityOptions securityOptions, final ConnectionOptions connectionOptions) throws LDAPException {
        final JSONObject o = LDAPConnectionDetailsJSONSpecification.getObject(connectionDetailsObject, "server-details");
        this.serverSet = createServerSet(o, "server-details", securityOptions, connectionOptions);
    }
    
    private static ServerSet createServerSet(final JSONObject o, final String fieldName, final SecurityOptions securityOptions, final ConnectionOptions connectionOptions) throws LDAPException {
        LDAPConnectionDetailsJSONSpecification.validateAllowedFields(o, fieldName, "failover-set", "fastest-connect-set", "fewest-connections-set", "round-robin-set", "single-server");
        final SocketFactory socketFactory = securityOptions.getSocketFactory();
        final LDAPConnectionOptions ldapConnectionOptions = connectionOptions.createConnectionOptions(securityOptions);
        if (o.getFields().size() != 1) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_INVALID_FIELD_SET.get(fieldName));
        }
        final JSONObject failoverSetObject = LDAPConnectionDetailsJSONSpecification.getObject(o, "failover-set");
        if (failoverSetObject != null) {
            LDAPConnectionDetailsJSONSpecification.validateAllowedFields(failoverSetObject, "failover-set", "failover-order", "maximum-failover-connection-age-millis");
            final Long maxFailoverConnectionAgeMillis = LDAPConnectionDetailsJSONSpecification.getLong(failoverSetObject, "maximum-failover-connection-age-millis", null, 0L, null);
            final JSONValue orderValue = failoverSetObject.getField("failover-order");
            if (orderValue == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_MISSING_FIELD.get("failover-set", "failover-order"));
            }
            if (!(orderValue instanceof JSONArray)) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_FIELD_NOT_ARRAY.get("failover-set", "failover-order"));
            }
            final JSONArray orderArray = (JSONArray)orderValue;
            final List<JSONValue> orderArrayValues = orderArray.getValues();
            if (orderArrayValues.isEmpty()) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_EMPTY_ARRAY.get("failover-set", "failover-order"));
            }
            final ArrayList<ServerSet> failoverSets = new ArrayList<ServerSet>(orderArrayValues.size());
            for (final JSONValue v : orderArrayValues) {
                if (!(v instanceof JSONObject)) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_SERVERS_VALUE_NOT_OBJECT.get("failover-order", "failover-set"));
                }
                failoverSets.add(createServerSet((JSONObject)v, "failover-order", securityOptions, connectionOptions));
            }
            final FailoverServerSet failoverSet = new FailoverServerSet(failoverSets);
            failoverSet.setMaxFailoverConnectionAgeMillis(maxFailoverConnectionAgeMillis);
            return failoverSet;
        }
        else {
            final JSONObject fastestConnectSetObject = LDAPConnectionDetailsJSONSpecification.getObject(o, "fastest-connect-set");
            if (fastestConnectSetObject != null) {
                final ObjectPair<String[], int[]> servers = parseServers(fastestConnectSetObject, "servers");
                return new FastestConnectServerSet(servers.getFirst(), servers.getSecond(), socketFactory, ldapConnectionOptions);
            }
            final JSONObject fewestConnectionsSetObject = LDAPConnectionDetailsJSONSpecification.getObject(o, "fewest-connections-set");
            if (fewestConnectionsSetObject != null) {
                final ObjectPair<String[], int[]> servers2 = parseServers(fewestConnectionsSetObject, "servers");
                return new FewestConnectionsServerSet(servers2.getFirst(), servers2.getSecond(), socketFactory, ldapConnectionOptions);
            }
            final JSONObject roundRobinSetObject = LDAPConnectionDetailsJSONSpecification.getObject(o, "round-robin-set");
            if (roundRobinSetObject != null) {
                final ObjectPair<String[], int[]> servers3 = parseServers(roundRobinSetObject, "servers");
                return new RoundRobinServerSet(servers3.getFirst(), servers3.getSecond(), socketFactory, ldapConnectionOptions);
            }
            final JSONObject singleServerObject = LDAPConnectionDetailsJSONSpecification.getObject(o, "single-server");
            final ObjectPair<String, Integer> addressAndPort = parseServer(singleServerObject, "single-server");
            return new SingleServerSet(addressAndPort.getFirst(), addressAndPort.getSecond(), socketFactory, ldapConnectionOptions);
        }
    }
    
    private static ObjectPair<String[], int[]> parseServers(final JSONObject o, final String f) throws LDAPException {
        LDAPConnectionDetailsJSONSpecification.validateAllowedFields(o, f, "servers");
        final JSONValue serversValue = o.getField("servers");
        if (serversValue == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_MISSING_FIELD.get(f, "servers"));
        }
        if (!(serversValue instanceof JSONArray)) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_FIELD_NOT_ARRAY.get(f, "servers"));
        }
        final List<JSONValue> serverArrayValues = ((JSONArray)serversValue).getValues();
        if (serverArrayValues.isEmpty()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_EMPTY_ARRAY.get(f, "servers"));
        }
        int i = 0;
        final String[] addresses = new String[serverArrayValues.size()];
        final int[] ports = new int[addresses.length];
        for (final JSONValue v : serverArrayValues) {
            if (!(v instanceof JSONObject)) {
                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_SERVERS_VALUE_NOT_OBJECT.get("servers", f));
            }
            final ObjectPair<String, Integer> p = parseServer((JSONObject)v, "servers");
            addresses[i] = p.getFirst();
            ports[i] = p.getSecond();
            ++i;
        }
        return new ObjectPair<String[], int[]>(addresses, ports);
    }
    
    private static ObjectPair<String, Integer> parseServer(final JSONObject o, final String f) throws LDAPException {
        LDAPConnectionDetailsJSONSpecification.validateAllowedFields(o, f, "address", "port");
        final String address = LDAPConnectionDetailsJSONSpecification.getString(o, "address", null);
        if (address == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_MISSING_FIELD.get(f, "address"));
        }
        final Integer port = LDAPConnectionDetailsJSONSpecification.getInt(o, "port", null, 1, 65535);
        if (port == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_SERVER_DETAILS_MISSING_FIELD.get(f, "port"));
        }
        return new ObjectPair<String, Integer>(address, port);
    }
    
    ServerSet getServerSet() {
        return this.serverSet;
    }
}
