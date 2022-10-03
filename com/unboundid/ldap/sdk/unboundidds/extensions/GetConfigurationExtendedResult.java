package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedResult;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetConfigurationExtendedResult extends ExtendedResult
{
    public static final String GET_CONFIG_RESULT_OID = "1.3.6.1.4.1.30221.2.6.29";
    private static final byte TYPE_CONFIG_TYPE = Byte.MIN_VALUE;
    private static final byte TYPE_FILE_NAME = -127;
    private static final byte TYPE_FILE_DATA = -126;
    private static final long serialVersionUID = 6042324433827773678L;
    private final byte[] fileData;
    private final GetConfigurationType configurationType;
    private final String fileName;
    
    public GetConfigurationExtendedResult(final ExtendedResult result) throws LDAPException {
        super(result);
        final ASN1OctetString value = result.getValue();
        if (value == null) {
            this.configurationType = null;
            this.fileName = null;
            this.fileData = null;
            return;
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            final int configType = ASN1Enumerated.decodeAsEnumerated(elements[0]).intValue();
            this.configurationType = GetConfigurationType.forIntValue(configType);
            if (this.configurationType == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CONFIG_RESULT_INVALID_CONFIG_TYPE.get(configType));
            }
            this.fileName = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            this.fileData = ASN1OctetString.decodeAsOctetString(elements[2]).getValue();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CONFIG_RESULT_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public GetConfigurationExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final GetConfigurationType configurationType, final String fileName, final byte[] fileData, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (configurationType == null) ? null : "1.3.6.1.4.1.30221.2.6.29", encodeValue(configurationType, fileName, fileData), responseControls);
        this.configurationType = configurationType;
        this.fileName = fileName;
        this.fileData = fileData;
    }
    
    public static ASN1OctetString encodeValue(final GetConfigurationType configurationType, final String fileName, final byte[] fileData) {
        if (configurationType == null) {
            Validator.ensureTrue(fileName == null, "The configuration file name must be null if the configuration type is null.");
            Validator.ensureTrue(fileData == null, "The configuration file data must be null if the configuration type is null.");
            return null;
        }
        Validator.ensureTrue(fileName != null, "The configuration file name must not be null if the configuration type is not null.");
        Validator.ensureTrue(fileData != null, "The configuration file data must not be null if the configuration type is not null.");
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1Enumerated((byte)(-128), configurationType.getIntValue()), new ASN1OctetString((byte)(-127), fileName), new ASN1OctetString((byte)(-126), fileData) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public GetConfigurationType getConfigurationType() {
        return this.configurationType;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public byte[] getFileData() {
        return this.fileData;
    }
    
    public InputStream getFileDataInputStream() {
        if (this.fileData == null) {
            return null;
        }
        return new ByteArrayInputStream(this.fileData);
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_GET_CONFIG.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetConfigurationExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.configurationType != null) {
            buffer.append(", configType=");
            buffer.append(this.configurationType.name());
        }
        if (this.fileName != null) {
            buffer.append(", fileName='");
            buffer.append(this.fileName);
            buffer.append('\'');
        }
        if (this.fileData != null) {
            buffer.append(", fileLength=");
            buffer.append(this.fileData.length);
        }
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(diagnosticMessage);
            buffer.append('\'');
        }
        final String matchedDN = this.getMatchedDN();
        if (matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(matchedDN);
            buffer.append('\'');
        }
        final String[] referralURLs = this.getReferralURLs();
        if (referralURLs.length > 0) {
            buffer.append(", referralURLs={");
            for (int i = 0; i < referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        final Control[] responseControls = this.getResponseControls();
        if (responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int j = 0; j < responseControls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(responseControls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
