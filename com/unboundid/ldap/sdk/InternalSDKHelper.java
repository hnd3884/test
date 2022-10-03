package com.unboundid.ldap.sdk;

import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.extensions.CancelExtendedRequest;
import javax.net.ssl.SSLSocketFactory;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class InternalSDKHelper
{
    private InternalSDKHelper() {
    }
    
    public static int getSoTimeout(final LDAPConnection connection) throws LDAPException {
        try {
            return connection.getConnectionInternals(true).getSocket().getSoTimeout();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_INTERNAL_SDK_HELPER_CANNOT_GET_SO_TIMEOUT.get(String.valueOf(connection), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @InternalUseOnly
    public static void setSoTimeout(final LDAPConnection connection, final int soTimeout) throws LDAPException {
        if (Debug.debugEnabled()) {
            Debug.debug(Level.INFO, DebugType.CONNECT, "Setting the SO_TIMEOUT value for connection " + connection + " to " + soTimeout + "ms.");
        }
        try {
            if (connection != null) {
                final LDAPConnectionInternals internals = connection.getConnectionInternals(false);
                if (internals != null) {
                    internals.getSocket().setSoTimeout(soTimeout);
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_INTERNAL_SDK_HELPER_CANNOT_SET_SO_TIMEOUT.get(String.valueOf(connection), soTimeout, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @InternalUseOnly
    public static void convertToTLS(final LDAPConnection connection, final SSLSocketFactory sslSocketFactory) throws LDAPException {
        connection.convertToTLS(sslSocketFactory);
    }
    
    @InternalUseOnly
    public static AsyncRequestID createAsyncRequestID(final int targetMessageID, final LDAPConnection connection) {
        return new AsyncRequestID(targetMessageID, connection);
    }
    
    @InternalUseOnly
    public static void cancel(final LDAPConnection connection, final int targetMessageID, final Control... controls) throws LDAPException {
        final int messageID = connection.nextMessageID();
        final CancelExtendedRequest cancelRequest = new CancelExtendedRequest(targetMessageID);
        Debug.debugLDAPRequest(Level.INFO, cancelRequest, messageID, connection);
        connection.sendMessage(new LDAPMessage(messageID, new ExtendedRequest(cancelRequest), controls), connection.getConnectionOptions().getExtendedOperationResponseTimeoutMillis("1.3.6.1.1.8"));
    }
    
    @InternalUseOnly
    public static LDAPResult readLDAPResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        return LDAPResult.readLDAPResultFrom(messageID, messageSequence, reader);
    }
    
    @InternalUseOnly
    public static BindResult readBindResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        return BindResult.readBindResultFrom(messageID, messageSequence, reader);
    }
    
    @InternalUseOnly
    public static CompareResult readCompareResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        return CompareResult.readCompareResultFrom(messageID, messageSequence, reader);
    }
    
    @InternalUseOnly
    public static ExtendedResult readExtendedResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        return ExtendedResult.readExtendedResultFrom(messageID, messageSequence, reader);
    }
    
    @InternalUseOnly
    public static SearchResultEntry readSearchResultEntryFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader, final Schema schema) throws LDAPException {
        return SearchResultEntry.readSearchEntryFrom(messageID, messageSequence, reader, schema);
    }
    
    @InternalUseOnly
    public static SearchResultReference readSearchResultReferenceFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        return SearchResultReference.readSearchReferenceFrom(messageID, messageSequence, reader);
    }
    
    @InternalUseOnly
    public static SearchResult readSearchResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        return SearchResult.readSearchResultFrom(messageID, messageSequence, reader);
    }
    
    @InternalUseOnly
    public static IntermediateResponse readIntermediateResponseFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        return IntermediateResponse.readFrom(messageID, messageSequence, reader);
    }
    
    @InternalUseOnly
    public static Boolean followReferralsInternal(final LDAPRequest request) {
        return request.followReferralsInternal();
    }
    
    @InternalUseOnly
    public static ReferralConnector getReferralConnectorInternal(final LDAPRequest request) {
        return request.getReferralConnectorInternal();
    }
    
    @InternalUseOnly
    public static int nextMessageID(final LDAPConnection connection) {
        return connection.nextMessageID();
    }
    
    @InternalUseOnly
    public static BindRequest getLastBindRequest(final LDAPConnection connection) {
        return connection.getLastBindRequest();
    }
    
    @InternalUseOnly
    public static Schema getEntrySchema(final Entry entry) {
        return entry.getSchema();
    }
}
