package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import java.util.TreeSet;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedResult;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ListConfigurationsExtendedResult extends ExtendedResult
{
    public static final String LIST_CONFIGS_RESULT_OID = "1.3.6.1.4.1.30221.2.6.27";
    private static final byte TYPE_ACTIVE_CONFIG_FILE_NAME = Byte.MIN_VALUE;
    private static final byte TYPE_BASELINE_CONFIG_FILE_NAMES = -95;
    private static final byte TYPE_ARCHIVED_CONFIG_FILE_NAMES = -94;
    private static final long serialVersionUID = -466738484294922561L;
    private final List<String> archivedFileNames;
    private final List<String> baselineFileNames;
    private final String activeFileName;
    
    public ListConfigurationsExtendedResult(final ExtendedResult result) throws LDAPException {
        super(result);
        final ASN1OctetString value = result.getValue();
        if (value == null) {
            this.activeFileName = null;
            this.baselineFileNames = Collections.emptyList();
            this.archivedFileNames = Collections.emptyList();
            return;
        }
        try {
            String activeName = null;
            List<String> archivedNames = Collections.emptyList();
            List<String> baselineNames = null;
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            for (final ASN1Element e : arr$) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        activeName = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -95: {
                        final ASN1Element[] baselineNameElements = ASN1Sequence.decodeAsSequence(e).elements();
                        baselineNames = new ArrayList<String>(baselineNameElements.length);
                        for (final ASN1Element el : baselineNameElements) {
                            baselineNames.add(ASN1OctetString.decodeAsOctetString(el).stringValue());
                        }
                        archivedNames = Collections.unmodifiableList((List<? extends String>)baselineNames);
                        break;
                    }
                    case -94: {
                        final ASN1Element[] archivedNameElements = ASN1Sequence.decodeAsSequence(e).elements();
                        archivedNames = new ArrayList<String>(archivedNameElements.length);
                        for (final ASN1Element el2 : archivedNameElements) {
                            archivedNames.add(ASN1OctetString.decodeAsOctetString(el2).stringValue());
                        }
                        archivedNames = Collections.unmodifiableList((List<? extends String>)archivedNames);
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_LIST_CONFIGS_RESULT_UNEXPECTED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.activeFileName = activeName;
            this.archivedFileNames = archivedNames;
            this.baselineFileNames = baselineNames;
            if (this.activeFileName == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_LIST_CONFIGS_RESULT_NO_ACTIVE_CONFIG.get());
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_LIST_CONFIGS_RESULT_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public ListConfigurationsExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String activeFileName, final Collection<String> baselineFileNames, final Collection<String> archivedFileNames, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (activeFileName == null) ? null : "1.3.6.1.4.1.30221.2.6.27", encodeValue(activeFileName, baselineFileNames, archivedFileNames), responseControls);
        this.activeFileName = activeFileName;
        if (baselineFileNames == null) {
            this.baselineFileNames = Collections.emptyList();
        }
        else {
            this.baselineFileNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(baselineFileNames));
        }
        if (archivedFileNames == null) {
            this.archivedFileNames = Collections.emptyList();
        }
        else {
            this.archivedFileNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(archivedFileNames));
        }
    }
    
    public static ASN1OctetString encodeValue(final String activeFileName, final Collection<String> baselineFileNames, final Collection<String> archivedFileNames) {
        if (activeFileName == null) {
            Validator.ensureTrue(baselineFileNames == null || baselineFileNames.isEmpty(), "The baseline filename must be null if the active filename is null");
            Validator.ensureTrue(archivedFileNames == null || archivedFileNames.isEmpty(), "The archived filenames must be null or empty if the active filename is null");
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1OctetString((byte)(-128), activeFileName));
        if (baselineFileNames != null && !baselineFileNames.isEmpty()) {
            final TreeSet<String> sortedBaselineNames = new TreeSet<String>(baselineFileNames);
            final ArrayList<ASN1Element> baselineNameElements = new ArrayList<ASN1Element>(sortedBaselineNames.size());
            for (final String s : sortedBaselineNames) {
                baselineNameElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-95), baselineNameElements));
        }
        if (archivedFileNames != null && !archivedFileNames.isEmpty()) {
            final TreeSet<String> sortedArchivedNames = new TreeSet<String>(archivedFileNames);
            final ArrayList<ASN1Element> archivedNameElements = new ArrayList<ASN1Element>(sortedArchivedNames.size());
            for (final String s : sortedArchivedNames) {
                archivedNameElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-94), archivedNameElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getActiveFileName() {
        return this.activeFileName;
    }
    
    public List<String> getBaselineFileNames() {
        return this.baselineFileNames;
    }
    
    public List<String> getArchivedFileNames() {
        return this.archivedFileNames;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_LIST_CONFIGS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ListConfigurationsExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.activeFileName != null) {
            buffer.append(", activeFileName='");
            buffer.append(this.activeFileName);
            buffer.append('\'');
        }
        if (!this.baselineFileNames.isEmpty()) {
            buffer.append(", baselineFileNames={");
            final Iterator<String> iterator = this.baselineFileNames.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.archivedFileNames.isEmpty()) {
            buffer.append(", archivedFileNames={");
            final Iterator<String> iterator = this.archivedFileNames.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
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
