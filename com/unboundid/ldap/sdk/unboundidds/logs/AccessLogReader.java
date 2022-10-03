package com.unboundid.ldap.sdk.unboundidds.logs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Closeable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AccessLogReader implements Closeable
{
    private final BufferedReader reader;
    
    public AccessLogReader(final String path) throws IOException {
        this.reader = new BufferedReader(new FileReader(path));
    }
    
    public AccessLogReader(final File file) throws IOException {
        this.reader = new BufferedReader(new FileReader(file));
    }
    
    public AccessLogReader(final Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader)reader;
        }
        else {
            this.reader = new BufferedReader(reader);
        }
    }
    
    public AccessLogMessage read() throws IOException, LogException {
        while (true) {
            final String line = this.reader.readLine();
            if (line == null) {
                return null;
            }
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == '#') {
                continue;
            }
            return parse(line);
        }
    }
    
    public static AccessLogMessage parse(final String s) throws LogException {
        final LogMessage m = new LogMessage(s);
        if (m.hasUnnamedValue(AccessLogMessageType.CONNECT.getLogIdentifier())) {
            return new ConnectAccessLogMessage(m);
        }
        if (m.hasUnnamedValue(AccessLogMessageType.DISCONNECT.getLogIdentifier())) {
            return new DisconnectAccessLogMessage(m);
        }
        if (m.hasUnnamedValue(AccessLogMessageType.CLIENT_CERTIFICATE.getLogIdentifier())) {
            return new ClientCertificateAccessLogMessage(m);
        }
        if (m.hasUnnamedValue(AccessLogMessageType.SECURITY_NEGOTIATION.getLogIdentifier())) {
            return new SecurityNegotiationAccessLogMessage(m);
        }
        if (m.hasUnnamedValue(AccessLogMessageType.ENTRY_REBALANCING_REQUEST.getLogIdentifier())) {
            return new EntryRebalancingRequestAccessLogMessage(m);
        }
        if (m.hasUnnamedValue(AccessLogMessageType.ENTRY_REBALANCING_RESULT.getLogIdentifier())) {
            return new EntryRebalancingResultAccessLogMessage(m);
        }
        if (m.hasUnnamedValue(AccessLogMessageType.REQUEST.getLogIdentifier())) {
            if (m.hasUnnamedValue(AccessLogOperationType.ABANDON.getLogIdentifier())) {
                return new AbandonRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.ADD.getLogIdentifier())) {
                return new AddRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.BIND.getLogIdentifier())) {
                return new BindRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.COMPARE.getLogIdentifier())) {
                return new CompareRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.DELETE.getLogIdentifier())) {
                return new DeleteRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.EXTENDED.getLogIdentifier())) {
                return new ExtendedRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODIFY.getLogIdentifier())) {
                return new ModifyRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODDN.getLogIdentifier())) {
                return new ModifyDNRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.SEARCH.getLogIdentifier())) {
                return new SearchRequestAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.UNBIND.getLogIdentifier())) {
                return new UnbindRequestAccessLogMessage(m);
            }
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_REQUEST_OPERATION_TYPE.get());
        }
        else if (m.hasUnnamedValue(AccessLogMessageType.RESULT.getLogIdentifier())) {
            if (m.hasUnnamedValue(AccessLogOperationType.ABANDON.getLogIdentifier())) {
                return new AbandonResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.ADD.getLogIdentifier())) {
                return new AddResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.BIND.getLogIdentifier())) {
                return new BindResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.COMPARE.getLogIdentifier())) {
                return new CompareResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.DELETE.getLogIdentifier())) {
                return new DeleteResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.EXTENDED.getLogIdentifier())) {
                return new ExtendedResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODIFY.getLogIdentifier())) {
                return new ModifyResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODDN.getLogIdentifier())) {
                return new ModifyDNResultAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.SEARCH.getLogIdentifier())) {
                return new SearchResultAccessLogMessage(m);
            }
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_RESULT_OPERATION_TYPE.get());
        }
        else if (m.hasUnnamedValue(AccessLogMessageType.FORWARD.getLogIdentifier())) {
            if (m.hasUnnamedValue(AccessLogOperationType.ABANDON.getLogIdentifier())) {
                return new AbandonForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.ADD.getLogIdentifier())) {
                return new AddForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.BIND.getLogIdentifier())) {
                return new BindForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.COMPARE.getLogIdentifier())) {
                return new CompareForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.DELETE.getLogIdentifier())) {
                return new DeleteForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.EXTENDED.getLogIdentifier())) {
                return new ExtendedForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODIFY.getLogIdentifier())) {
                return new ModifyForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODDN.getLogIdentifier())) {
                return new ModifyDNForwardAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.SEARCH.getLogIdentifier())) {
                return new SearchForwardAccessLogMessage(m);
            }
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_FORWARD_OPERATION_TYPE.get());
        }
        else if (m.hasUnnamedValue(AccessLogMessageType.FORWARD_FAILED.getLogIdentifier())) {
            if (m.hasUnnamedValue(AccessLogOperationType.ADD.getLogIdentifier())) {
                return new AddForwardFailedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.BIND.getLogIdentifier())) {
                return new BindForwardFailedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.COMPARE.getLogIdentifier())) {
                return new CompareForwardFailedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.DELETE.getLogIdentifier())) {
                return new DeleteForwardFailedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.EXTENDED.getLogIdentifier())) {
                return new ExtendedForwardFailedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODIFY.getLogIdentifier())) {
                return new ModifyForwardFailedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODDN.getLogIdentifier())) {
                return new ModifyDNForwardFailedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.SEARCH.getLogIdentifier())) {
                return new SearchForwardFailedAccessLogMessage(m);
            }
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_FORWARD_FAILED_OPERATION_TYPE.get());
        }
        else if (m.hasUnnamedValue(AccessLogMessageType.ASSURANCE_COMPLETE.getLogIdentifier())) {
            if (m.hasUnnamedValue(AccessLogOperationType.ADD.getLogIdentifier())) {
                return new AddAssuranceCompletedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.DELETE.getLogIdentifier())) {
                return new DeleteAssuranceCompletedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODIFY.getLogIdentifier())) {
                return new ModifyAssuranceCompletedAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogOperationType.MODDN.getLogIdentifier())) {
                return new ModifyDNAssuranceCompletedAccessLogMessage(m);
            }
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_ASSURANCE_COMPLETE_OPERATION_TYPE.get());
        }
        else {
            if (m.hasUnnamedValue(AccessLogMessageType.ENTRY.getLogIdentifier())) {
                return new SearchEntryAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogMessageType.REFERENCE.getLogIdentifier())) {
                return new SearchReferenceAccessLogMessage(m);
            }
            if (m.hasUnnamedValue(AccessLogMessageType.INTERMEDIATE_RESPONSE.getLogIdentifier())) {
                return new IntermediateResponseAccessLogMessage(m);
            }
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_ACCESS_MESSAGE_TYPE.get());
        }
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
