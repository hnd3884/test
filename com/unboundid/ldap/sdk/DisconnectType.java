package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum DisconnectType
{
    UNBIND(LDAPMessages.INFO_DISCONNECT_TYPE_UNBIND.get(), ResultCode.LOCAL_ERROR), 
    CLOSED_WITHOUT_UNBIND(LDAPMessages.INFO_DISCONNECT_TYPE_CLOSED_WITHOUT_UNBIND.get(), ResultCode.LOCAL_ERROR), 
    BIND_FAILED(LDAPMessages.INFO_DISCONNECT_TYPE_BIND_FAILED.get(), ResultCode.CONNECT_ERROR), 
    RECONNECT(LDAPMessages.INFO_DISCONNECT_TYPE_RECONNECT.get(), ResultCode.SERVER_DOWN), 
    REFERRAL(LDAPMessages.INFO_DISCONNECT_TYPE_REFERRAL.get(), ResultCode.LOCAL_ERROR), 
    SERVER_CLOSED_WITH_NOTICE(LDAPMessages.INFO_DISCONNECT_TYPE_SERVER_CLOSED_WITH_NOTICE.get(), ResultCode.SERVER_DOWN), 
    SERVER_CLOSED_WITHOUT_NOTICE(LDAPMessages.INFO_DISCONNECT_TYPE_SERVER_CLOSED_WITHOUT_NOTICE.get(), ResultCode.SERVER_DOWN), 
    IO_ERROR(LDAPMessages.INFO_DISCONNECT_TYPE_IO_ERROR.get(), ResultCode.SERVER_DOWN), 
    DECODE_ERROR(LDAPMessages.INFO_DISCONNECT_TYPE_DECODE_ERROR.get(), ResultCode.DECODING_ERROR), 
    LOCAL_ERROR(LDAPMessages.INFO_DISCONNECT_TYPE_LOCAL_ERROR.get(), ResultCode.LOCAL_ERROR), 
    SECURITY_PROBLEM(LDAPMessages.INFO_DISCONNECT_TYPE_SECURITY_PROBLEM.get(), ResultCode.LOCAL_ERROR), 
    POOL_CLOSED(LDAPMessages.INFO_DISCONNECT_TYPE_POOL_CLOSED.get(), ResultCode.LOCAL_ERROR), 
    POOL_CREATION_FAILURE(LDAPMessages.INFO_DISCONNECT_TYPE_POOL_CREATION_FAILURE.get(), ResultCode.CONNECT_ERROR), 
    POOLED_CONNECTION_DEFUNCT(LDAPMessages.INFO_DISCONNECT_TYPE_POOLED_CONNECTION_DEFUNCT.get(), ResultCode.SERVER_DOWN), 
    POOLED_CONNECTION_EXPIRED(LDAPMessages.INFO_DISCONNECT_TYPE_POOLED_CONNECTION_EXPIRED.get(), ResultCode.LOCAL_ERROR), 
    POOLED_CONNECTION_UNNEEDED(LDAPMessages.INFO_DISCONNECT_TYPE_POOLED_CONNECTION_UNNEEDED.get(), ResultCode.LOCAL_ERROR), 
    UNKNOWN(LDAPMessages.INFO_DISCONNECT_TYPE_UNKNOWN.get(), ResultCode.LOCAL_ERROR), 
    CLOSED_BY_FINALIZER(LDAPMessages.INFO_DISCONNECT_TYPE_CLOSED_BY_FINALIZER.get(), ResultCode.LOCAL_ERROR), 
    OTHER(LDAPMessages.INFO_DISCONNECT_TYPE_OTHER.get(), ResultCode.LOCAL_ERROR);
    
    private final ResultCode resultCode;
    private final String description;
    
    private DisconnectType(final String description, final ResultCode resultCode) {
        this.description = description;
        this.resultCode = resultCode;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public static DisconnectType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "unbind": {
                return DisconnectType.UNBIND;
            }
            case "closedwithoutunbind":
            case "closed-without-unbind":
            case "closed_without_unbind": {
                return DisconnectType.CLOSED_WITHOUT_UNBIND;
            }
            case "bindfailed":
            case "bind-failed":
            case "bind_failed": {
                return DisconnectType.BIND_FAILED;
            }
            case "reconnect": {
                return DisconnectType.RECONNECT;
            }
            case "referral": {
                return DisconnectType.REFERRAL;
            }
            case "serverclosedwithnotice":
            case "server-closed-with-notice":
            case "server_closed_with_notice": {
                return DisconnectType.SERVER_CLOSED_WITH_NOTICE;
            }
            case "serverclosedwithoutnotice":
            case "server-closed-without-notice":
            case "server_closed_without_notice": {
                return DisconnectType.SERVER_CLOSED_WITHOUT_NOTICE;
            }
            case "ioerror":
            case "io-error":
            case "io_error": {
                return DisconnectType.IO_ERROR;
            }
            case "decodeerror":
            case "decode-error":
            case "decode_error": {
                return DisconnectType.DECODE_ERROR;
            }
            case "localerror":
            case "local-error":
            case "local_error": {
                return DisconnectType.LOCAL_ERROR;
            }
            case "securityproblem":
            case "security-problem":
            case "security_problem": {
                return DisconnectType.SECURITY_PROBLEM;
            }
            case "poolclosed":
            case "pool-closed":
            case "pool_closed": {
                return DisconnectType.POOL_CLOSED;
            }
            case "poolcreationfailure":
            case "pool-creation-failure":
            case "pool_creation_failure": {
                return DisconnectType.POOL_CREATION_FAILURE;
            }
            case "pooledconnectiondefunct":
            case "pooled-connection-defunct":
            case "pooled_connection_defunct": {
                return DisconnectType.POOLED_CONNECTION_DEFUNCT;
            }
            case "pooledconnectionexpired":
            case "pooled-connection-expired":
            case "pooled_connection_expired": {
                return DisconnectType.POOLED_CONNECTION_EXPIRED;
            }
            case "pooledconnectionunneeded":
            case "pooled-connection-unneeded":
            case "pooled_connection_unneeded": {
                return DisconnectType.POOLED_CONNECTION_UNNEEDED;
            }
            case "unknown": {
                return DisconnectType.UNKNOWN;
            }
            case "closedbyfinalizer":
            case "closed-by-finalizer":
            case "closed_by_finalizer": {
                return DisconnectType.CLOSED_BY_FINALIZER;
            }
            case "other": {
                return DisconnectType.OTHER;
            }
            default: {
                return null;
            }
        }
    }
    
    public static boolean isExpected(final DisconnectType disconnectType) {
        switch (disconnectType) {
            case UNBIND:
            case CLOSED_WITHOUT_UNBIND:
            case RECONNECT:
            case REFERRAL:
            case POOL_CLOSED:
            case POOLED_CONNECTION_DEFUNCT:
            case POOLED_CONNECTION_EXPIRED:
            case POOLED_CONNECTION_UNNEEDED:
            case CLOSED_BY_FINALIZER: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("DisconnectType(name='");
        buffer.append(this.name());
        buffer.append("', resultCode='");
        buffer.append(this.resultCode);
        buffer.append("', description='");
        buffer.append(this.description);
        buffer.append("')");
    }
}
