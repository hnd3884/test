package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldap.sdk.unboundidds.controls.IntermediateClientRequestValue;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.ldap.sdk.persist.PersistUtils;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import java.io.InputStream;
import com.unboundid.util.json.JSONObjectReader;
import com.unboundid.util.json.JSONObject;
import java.text.ParseException;
import com.unboundid.util.Debug;
import java.io.ByteArrayInputStream;
import com.unboundid.util.ByteStringBuffer;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl;
import java.util.Map;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.controls.IntermediateClientRequestControl;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class AuditLogMessage implements Serializable
{
    private static final Pattern STARTS_WITH_TIMESTAMP_PATTERN;
    private static final String TIMESTAMP_SEC_FORMAT = "dd/MMM/yyyy:HH:mm:ss Z";
    private static final String TIMESTAMP_MS_FORMAT = "dd/MMM/yyyy:HH:mm:ss.SSS Z";
    private static final ThreadLocal<SimpleDateFormat> TIMESTAMP_SEC_FORMAT_PARSERS;
    private static final ThreadLocal<SimpleDateFormat> TIMESTAMP_MS_FORMAT_PARSERS;
    private static final long serialVersionUID = 1817887018590767411L;
    private final Boolean usingAdminSessionWorkerThread;
    private final Date timestamp;
    private final IntermediateClientRequestControl intermediateClientRequestControl;
    private final List<String> logMessageLines;
    private final List<String> requestControlOIDs;
    private final Long connectionID;
    private final Long operationID;
    private final Long threadID;
    private final Long triggeredByConnectionID;
    private final Long triggeredByOperationID;
    private final Map<String, String> namedValues;
    private final OperationPurposeRequestControl operationPurposeRequestControl;
    private final String alternateAuthorizationDN;
    private final String commentedHeaderLine;
    private final String instanceName;
    private final String origin;
    private final String replicationChangeID;
    private final String requesterDN;
    private final String requesterIP;
    private final String productName;
    private final String startupID;
    private final String transactionID;
    private final String uncommentedHeaderLine;
    
    protected AuditLogMessage(final List<String> logMessageLines) throws AuditLogException {
        if (logMessageLines == null) {
            throw new AuditLogException(Collections.emptyList(), LogMessages.ERR_AUDIT_LOG_MESSAGE_LIST_NULL.get());
        }
        if (logMessageLines.isEmpty()) {
            throw new AuditLogException(Collections.emptyList(), LogMessages.ERR_AUDIT_LOG_MESSAGE_LIST_EMPTY.get());
        }
        for (final String line : logMessageLines) {
            if (line == null || line.isEmpty()) {
                throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_LIST_CONTAINS_EMPTY_LINE.get());
            }
        }
        this.logMessageLines = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(logMessageLines));
        String headerLine = null;
        for (final String line2 : logMessageLines) {
            if (AuditLogMessage.STARTS_WITH_TIMESTAMP_PATTERN.matcher(line2).matches()) {
                headerLine = line2;
                break;
            }
        }
        if (headerLine == null) {
            throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_LIST_DOES_NOT_START_WITH_COMMENT.get());
        }
        this.commentedHeaderLine = headerLine;
        this.uncommentedHeaderLine = this.commentedHeaderLine.substring(2);
        final LinkedHashMap<String, String> nameValuePairs = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        this.timestamp = parseHeaderLine(logMessageLines, this.uncommentedHeaderLine, nameValuePairs);
        this.namedValues = Collections.unmodifiableMap((Map<? extends String, ? extends String>)nameValuePairs);
        this.connectionID = getNamedValueAsLong("conn", this.namedValues);
        this.operationID = getNamedValueAsLong("op", this.namedValues);
        this.threadID = getNamedValueAsLong("threadID", this.namedValues);
        this.triggeredByConnectionID = getNamedValueAsLong("triggeredByConn", this.namedValues);
        this.triggeredByOperationID = getNamedValueAsLong("triggeredByOp", this.namedValues);
        this.alternateAuthorizationDN = this.namedValues.get("authzDN");
        this.instanceName = this.namedValues.get("instanceName");
        this.origin = this.namedValues.get("origin");
        this.replicationChangeID = this.namedValues.get("replicationChangeID");
        this.requesterDN = this.namedValues.get("requesterDN");
        this.requesterIP = this.namedValues.get("clientIP");
        this.productName = this.namedValues.get("productName");
        this.startupID = this.namedValues.get("startupID");
        this.transactionID = this.namedValues.get("txnID");
        this.usingAdminSessionWorkerThread = getNamedValueAsBoolean("usingAdminSessionWorkerThread", this.namedValues);
        this.operationPurposeRequestControl = decodeOperationPurposeRequestControl(this.namedValues);
        this.intermediateClientRequestControl = decodeIntermediateClientRequestControl(this.namedValues);
        final String oidsString = this.namedValues.get("requestControlOIDs");
        if (oidsString == null) {
            this.requestControlOIDs = null;
        }
        else {
            final ArrayList<String> oidList = new ArrayList<String>(10);
            final StringTokenizer tokenizer = new StringTokenizer(oidsString, ",");
            while (tokenizer.hasMoreTokens()) {
                oidList.add(tokenizer.nextToken());
            }
            this.requestControlOIDs = Collections.unmodifiableList((List<? extends String>)oidList);
        }
    }
    
    private static Date parseHeaderLine(final List<String> logMessageLines, final String uncommentedHeaderLine, final Map<String, String> nameValuePairs) throws AuditLogException {
        final byte[] uncommentedHeaderBytes = StaticUtils.getBytes(uncommentedHeaderLine);
        final ByteStringBuffer buffer = new ByteStringBuffer(uncommentedHeaderBytes.length);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(uncommentedHeaderBytes);
        final Date timestamp = readTimestamp(logMessageLines, inputStream, buffer);
        while (readNameValuePair(logMessageLines, inputStream, nameValuePairs, buffer)) {}
        return timestamp;
    }
    
    private static Date readTimestamp(final List<String> logMessageLines, final ByteArrayInputStream inputStream, final ByteStringBuffer buffer) throws AuditLogException {
        while (true) {
            final int intRead = inputStream.read();
            if (intRead < 0 || intRead == 59) {
                break;
            }
            buffer.append((byte)(intRead & 0xFF));
        }
        final String timestampString = buffer.toString().trim();
        SimpleDateFormat parser;
        if (timestampString.length() == 30) {
            parser = AuditLogMessage.TIMESTAMP_MS_FORMAT_PARSERS.get();
            if (parser == null) {
                parser = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss.SSS Z");
                parser.setLenient(false);
                AuditLogMessage.TIMESTAMP_MS_FORMAT_PARSERS.set(parser);
            }
        }
        else {
            if (timestampString.length() != 26) {
                throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_HEADER_MALFORMED_TIMESTAMP.get());
            }
            parser = AuditLogMessage.TIMESTAMP_SEC_FORMAT_PARSERS.get();
            if (parser == null) {
                parser = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
                parser.setLenient(false);
                AuditLogMessage.TIMESTAMP_SEC_FORMAT_PARSERS.set(parser);
            }
        }
        try {
            return parser.parse(timestampString);
        }
        catch (final ParseException e) {
            Debug.debugException(e);
            throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_HEADER_MALFORMED_TIMESTAMP.get(), e);
        }
    }
    
    private static boolean readNameValuePair(final List<String> logMessageLines, final ByteArrayInputStream inputStream, final Map<String, String> nameValuePairs, final ByteStringBuffer buffer) throws AuditLogException {
        buffer.clear();
        while (true) {
            final int intRead = inputStream.read();
            if (intRead < 0) {
                if (buffer.isEmpty()) {
                    return false;
                }
                throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_HEADER_ENDS_WITH_PROPERTY_NAME.get(buffer.toString()));
            }
            else if (intRead == 61) {
                final String name = buffer.toString();
                if (name.isEmpty()) {
                    throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_HEADER_EMPTY_PROPERTY_NAME.get());
                }
                String valueString;
                while (true) {
                    inputStream.mark(1);
                    final int intRead2 = inputStream.read();
                    if (intRead2 < 0) {
                        valueString = "";
                        break;
                    }
                    if (intRead2 == 32) {
                        continue;
                    }
                    if (intRead2 == 123) {
                        inputStream.reset();
                        final JSONObject jsonObject = readJSONObject(logMessageLines, name, inputStream);
                        valueString = jsonObject.toString();
                        break;
                    }
                    if (intRead2 == 34) {
                        valueString = readString(logMessageLines, name, true, inputStream, buffer);
                        break;
                    }
                    if (intRead2 == 59) {
                        valueString = "";
                        break;
                    }
                    inputStream.reset();
                    valueString = readString(logMessageLines, name, false, inputStream, buffer);
                    break;
                }
                nameValuePairs.put(name, valueString);
                return true;
            }
            else {
                if (intRead == 32) {
                    continue;
                }
                buffer.append((byte)(intRead & 0xFF));
            }
        }
    }
    
    private static JSONObject readJSONObject(final List<String> logMessageLines, final String propertyName, final ByteArrayInputStream inputStream) throws AuditLogException {
        JSONObject jsonObject;
        try {
            final JSONObjectReader reader = new JSONObjectReader(inputStream, false);
            jsonObject = reader.readObject();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_ERROR_READING_JSON_OBJECT.get(propertyName, StaticUtils.getExceptionMessage(e)), e);
        }
        readSpacesAndSemicolon(logMessageLines, propertyName, inputStream);
        return jsonObject;
    }
    
    private static String readString(final List<String> logMessageLines, final String propertyName, final boolean isQuoted, final ByteArrayInputStream inputStream, final ByteStringBuffer buffer) throws AuditLogException {
        buffer.clear();
    Label_0269:
        while (true) {
            inputStream.mark(1);
            final int intRead = inputStream.read();
            if (intRead < 0) {
                if (isQuoted) {
                    throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_END_BEFORE_CLOSING_QUOTE.get(propertyName));
                }
                return buffer.toString();
            }
            else {
                switch (intRead) {
                    case 92: {
                        final int literalCharacter = inputStream.read();
                        if (literalCharacter < 0) {
                            throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_END_BEFORE_ESCAPED.get(propertyName));
                        }
                        buffer.append((byte)(literalCharacter & 0xFF));
                        continue;
                    }
                    case 35: {
                        int hexByte = readHexDigit(logMessageLines, propertyName, inputStream);
                        hexByte = (hexByte << 4 | readHexDigit(logMessageLines, propertyName, inputStream));
                        buffer.append((byte)(hexByte & 0xFF));
                        continue;
                    }
                    case 34: {
                        if (isQuoted) {
                            break Label_0269;
                        }
                        buffer.append('\"');
                        continue;
                    }
                    case 32: {
                        if (!isQuoted) {
                            break Label_0269;
                        }
                        buffer.append(' ');
                        continue;
                    }
                    case 59: {
                        if (!isQuoted) {
                            inputStream.reset();
                            break Label_0269;
                        }
                        buffer.append(';');
                        continue;
                    }
                    default: {
                        buffer.append((byte)(intRead & 0xFF));
                        continue;
                    }
                }
            }
        }
        readSpacesAndSemicolon(logMessageLines, propertyName, inputStream);
        return buffer.toString();
    }
    
    private static int readHexDigit(final List<String> logMessageLines, final String propertyName, final ByteArrayInputStream inputStream) throws AuditLogException {
        final int byteRead = inputStream.read();
        if (byteRead < 0) {
            throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_END_BEFORE_HEX.get(propertyName));
        }
        switch (byteRead) {
            case 48: {
                return 0;
            }
            case 49: {
                return 1;
            }
            case 50: {
                return 2;
            }
            case 51: {
                return 3;
            }
            case 52: {
                return 4;
            }
            case 53: {
                return 5;
            }
            case 54: {
                return 6;
            }
            case 55: {
                return 7;
            }
            case 56: {
                return 8;
            }
            case 57: {
                return 9;
            }
            case 65:
            case 97: {
                return 10;
            }
            case 66:
            case 98: {
                return 11;
            }
            case 67:
            case 99: {
                return 12;
            }
            case 68:
            case 100: {
                return 13;
            }
            case 69:
            case 101: {
                return 14;
            }
            case 70:
            case 102: {
                return 15;
            }
            default: {
                throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_INVALID_HEX_DIGIT.get(propertyName));
            }
        }
    }
    
    private static void readSpacesAndSemicolon(final List<String> logMessageLines, final String propertyName, final ByteArrayInputStream inputStream) throws AuditLogException {
        while (true) {
            final int intRead = inputStream.read();
            if (intRead < 0 || intRead == 59) {
                return;
            }
            if (intRead != 32) {
                throw new AuditLogException(logMessageLines, LogMessages.ERR_AUDIT_LOG_MESSAGE_UNEXPECTED_CHAR_AFTER_PROPERTY.get(String.valueOf((char)intRead), propertyName));
            }
        }
    }
    
    protected static Boolean getNamedValueAsBoolean(final String name, final Map<String, String> nameValuePairs) {
        final String valueString = nameValuePairs.get(name);
        if (valueString == null) {
            return null;
        }
        final String lowerValueString = StaticUtils.toLowerCase(valueString);
        if (lowerValueString.equals("true") || lowerValueString.equals("t") || lowerValueString.equals("yes") || lowerValueString.equals("y") || lowerValueString.equals("on") || lowerValueString.equals("1")) {
            return Boolean.TRUE;
        }
        if (lowerValueString.equals("false") || lowerValueString.equals("f") || lowerValueString.equals("no") || lowerValueString.equals("n") || lowerValueString.equals("off") || lowerValueString.equals("0")) {
            return Boolean.FALSE;
        }
        return null;
    }
    
    protected static Long getNamedValueAsLong(final String name, final Map<String, String> nameValuePairs) {
        final String valueString = nameValuePairs.get(name);
        if (valueString == null) {
            return null;
        }
        try {
            return Long.parseLong(valueString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    protected static ReadOnlyEntry decodeCommentedEntry(final String header, final List<String> logMessageLines, final String entryDN) {
        List<String> ldifLines = null;
        StringBuilder invalidLDAPNameReason = null;
        for (final String line : logMessageLines) {
            if (!line.startsWith("# ")) {
                break;
            }
            final String uncommentedLine = line.substring(2);
            if (ldifLines == null) {
                if (!uncommentedLine.equalsIgnoreCase(header)) {
                    continue;
                }
                ldifLines = new ArrayList<String>(logMessageLines.size());
                if (entryDN == null) {
                    continue;
                }
                ldifLines.add("dn: " + entryDN);
            }
            else {
                final int colonPos = uncommentedLine.indexOf(58);
                if (colonPos <= 0) {
                    break;
                }
                if (invalidLDAPNameReason == null) {
                    invalidLDAPNameReason = new StringBuilder();
                }
                final String potentialAttributeName = uncommentedLine.substring(0, colonPos);
                if (!PersistUtils.isValidLDAPName(potentialAttributeName, invalidLDAPNameReason)) {
                    break;
                }
                ldifLines.add(uncommentedLine);
            }
        }
        if (ldifLines == null) {
            return null;
        }
        try {
            final String[] ldifLineArray = ldifLines.toArray(StaticUtils.NO_STRINGS);
            final Entry ldifEntry = LDIFReader.decodeEntry(ldifLineArray);
            return new ReadOnlyEntry(ldifEntry);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    private static OperationPurposeRequestControl decodeOperationPurposeRequestControl(final Map<String, String> nameValuePairs) {
        final String valueString = nameValuePairs.get("operationPurpose");
        if (valueString == null) {
            return null;
        }
        try {
            final JSONObject o = new JSONObject(valueString);
            final String applicationName = o.getFieldAsString("applicationName");
            final String applicationVersion = o.getFieldAsString("applicationVersion");
            final String codeLocation = o.getFieldAsString("codeLocation");
            final String requestPurpose = o.getFieldAsString("requestPurpose");
            return new OperationPurposeRequestControl(false, applicationName, applicationVersion, codeLocation, requestPurpose);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    private static IntermediateClientRequestControl decodeIntermediateClientRequestControl(final Map<String, String> nameValuePairs) {
        final String valueString = nameValuePairs.get("intermediateClientRequestControl");
        if (valueString == null) {
            return null;
        }
        try {
            final JSONObject o = new JSONObject(valueString);
            return new IntermediateClientRequestControl(decodeIntermediateClientRequestValue(o));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    private static IntermediateClientRequestValue decodeIntermediateClientRequestValue(final JSONObject o) {
        if (o == null) {
            return null;
        }
        final String clientIdentity = o.getFieldAsString("clientIdentity");
        final String downstreamClientAddress = o.getFieldAsString("downstreamClientAddress");
        final Boolean downstreamClientSecure = o.getFieldAsBoolean("downstreamClientSecure");
        final String clientName = o.getFieldAsString("clientName");
        final String clientSessionID = o.getFieldAsString("clientSessionID");
        final String clientRequestID = o.getFieldAsString("clientRequestID");
        final IntermediateClientRequestValue downstreamRequest = decodeIntermediateClientRequestValue(o.getFieldAsObject("downstreamRequest"));
        return new IntermediateClientRequestValue(downstreamRequest, downstreamClientAddress, downstreamClientSecure, clientIdentity, clientName, clientSessionID, clientRequestID);
    }
    
    public final List<String> getLogMessageLines() {
        return this.logMessageLines;
    }
    
    public final String getCommentedHeaderLine() {
        return this.commentedHeaderLine;
    }
    
    public final String getUncommentedHeaderLine() {
        return this.uncommentedHeaderLine;
    }
    
    public final Date getTimestamp() {
        return this.timestamp;
    }
    
    public final Map<String, String> getHeaderNamedValues() {
        return this.namedValues;
    }
    
    public final String getProductName() {
        return this.productName;
    }
    
    public final String getInstanceName() {
        return this.instanceName;
    }
    
    public final String getStartupID() {
        return this.startupID;
    }
    
    public final Long getThreadID() {
        return this.threadID;
    }
    
    public final String getRequesterDN() {
        return this.requesterDN;
    }
    
    public final String getRequesterIPAddress() {
        return this.requesterIP;
    }
    
    public final Long getConnectionID() {
        return this.connectionID;
    }
    
    public final Long getOperationID() {
        return this.operationID;
    }
    
    public final Long getTriggeredByConnectionID() {
        return this.triggeredByConnectionID;
    }
    
    public final Long getTriggeredByOperationID() {
        return this.triggeredByOperationID;
    }
    
    public final String getReplicationChangeID() {
        return this.replicationChangeID;
    }
    
    public final String getAlternateAuthorizationDN() {
        return this.alternateAuthorizationDN;
    }
    
    public final String getTransactionID() {
        return this.transactionID;
    }
    
    public final String getOrigin() {
        return this.origin;
    }
    
    public final Boolean getUsingAdminSessionWorkerThread() {
        return this.usingAdminSessionWorkerThread;
    }
    
    public final List<String> getRequestControlOIDs() {
        return this.requestControlOIDs;
    }
    
    public final OperationPurposeRequestControl getOperationPurposeRequestControl() {
        return this.operationPurposeRequestControl;
    }
    
    public final IntermediateClientRequestControl getIntermediateClientRequestControl() {
        return this.intermediateClientRequestControl;
    }
    
    public abstract String getDN();
    
    public abstract ChangeType getChangeType();
    
    public abstract LDIFChangeRecord getChangeRecord();
    
    public abstract boolean isRevertible();
    
    public abstract List<LDIFChangeRecord> getRevertChangeRecords() throws AuditLogException;
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
    
    public final String toMultiLineString() {
        return StaticUtils.concatenateStrings(null, null, StaticUtils.EOL, null, null, this.logMessageLines);
    }
    
    static {
        STARTS_WITH_TIMESTAMP_PATTERN = Pattern.compile("^# \\d\\d\\/\\w\\w\\w\\/\\d\\d\\d\\d:\\d\\d:\\d\\d:\\d\\d.*$");
        TIMESTAMP_SEC_FORMAT_PARSERS = new ThreadLocal<SimpleDateFormat>();
        TIMESTAMP_MS_FORMAT_PARSERS = new ThreadLocal<SimpleDateFormat>();
    }
}
