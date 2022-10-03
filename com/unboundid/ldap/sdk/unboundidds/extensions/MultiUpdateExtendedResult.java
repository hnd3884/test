package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.sdk.Control;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collections;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MultiUpdateExtendedResult extends ExtendedResult
{
    public static final String MULTI_UPDATE_RESULT_OID = "1.3.6.1.4.1.30221.2.6.18";
    private static final long serialVersionUID = -2529988892013489969L;
    private final List<ObjectPair<OperationType, LDAPResult>> results;
    private final MultiUpdateChangesApplied changesApplied;
    
    public MultiUpdateExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.changesApplied = MultiUpdateChangesApplied.NONE;
            this.results = Collections.emptyList();
            return;
        }
        try {
            final ASN1Element[] outerSequenceElements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            final int cav = ASN1Enumerated.decodeAsEnumerated(outerSequenceElements[0]).intValue();
            this.changesApplied = MultiUpdateChangesApplied.valueOf(cav);
            if (this.changesApplied == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_RESULT_INVALID_CHANGES_APPLIED.get(cav));
            }
            final ASN1Element[] responseSetElements = ASN1Sequence.decodeAsSequence(outerSequenceElements[1]).elements();
            final ArrayList<ObjectPair<OperationType, LDAPResult>> rl = new ArrayList<ObjectPair<OperationType, LDAPResult>>(responseSetElements.length);
            for (final ASN1Element rse : responseSetElements) {
                final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(rse).elements();
                Control[] controls;
                if (elements.length == 2) {
                    controls = Control.decodeControls(ASN1Sequence.decodeAsSequence(elements[1]));
                }
                else {
                    controls = null;
                }
                switch (elements[0].getType()) {
                    case 105: {
                        rl.add(new ObjectPair<OperationType, LDAPResult>(OperationType.ADD, AddResponseProtocolOp.decodeProtocolOp(elements[0]).toLDAPResult(controls)));
                        break;
                    }
                    case 107: {
                        rl.add(new ObjectPair<OperationType, LDAPResult>(OperationType.DELETE, DeleteResponseProtocolOp.decodeProtocolOp(elements[0]).toLDAPResult(controls)));
                        break;
                    }
                    case 120: {
                        rl.add(new ObjectPair<OperationType, LDAPResult>(OperationType.EXTENDED, ExtendedResponseProtocolOp.decodeProtocolOp(elements[0]).toExtendedResult(controls)));
                        break;
                    }
                    case 103: {
                        rl.add(new ObjectPair<OperationType, LDAPResult>(OperationType.MODIFY, ModifyResponseProtocolOp.decodeProtocolOp(elements[0]).toLDAPResult(controls)));
                        break;
                    }
                    case 109: {
                        rl.add(new ObjectPair<OperationType, LDAPResult>(OperationType.MODIFY_DN, ModifyDNResponseProtocolOp.decodeProtocolOp(elements[0]).toLDAPResult(controls)));
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_RESULT_DECODE_INVALID_OP_TYPE.get(StaticUtils.toHex(elements[0].getType())));
                    }
                }
            }
            this.results = Collections.unmodifiableList((List<? extends ObjectPair<OperationType, LDAPResult>>)rl);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_RESULT_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public MultiUpdateExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final MultiUpdateChangesApplied changesApplied, final List<ObjectPair<OperationType, LDAPResult>> results, final Control... controls) throws LDAPException {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, "1.3.6.1.4.1.30221.2.6.18", encodeValue(changesApplied, results), controls);
        this.changesApplied = changesApplied;
        if (results == null) {
            this.results = Collections.emptyList();
        }
        else {
            this.results = Collections.unmodifiableList((List<? extends ObjectPair<OperationType, LDAPResult>>)results);
        }
    }
    
    private static ASN1OctetString encodeValue(final MultiUpdateChangesApplied changesApplied, final List<ObjectPair<OperationType, LDAPResult>> results) throws LDAPException {
        if (results == null || results.isEmpty()) {
            return null;
        }
        final ArrayList<ASN1Element> opElements = new ArrayList<ASN1Element>(results.size());
        for (final ObjectPair<OperationType, LDAPResult> p : results) {
            final OperationType t = p.getFirst();
            final LDAPResult r = p.getSecond();
            ASN1Element protocolOpElement = null;
            switch (t) {
                case ADD: {
                    protocolOpElement = new AddResponseProtocolOp(r).encodeProtocolOp();
                    break;
                }
                case DELETE: {
                    protocolOpElement = new DeleteResponseProtocolOp(r).encodeProtocolOp();
                    break;
                }
                case EXTENDED: {
                    protocolOpElement = new ExtendedResponseProtocolOp(r).encodeProtocolOp();
                    break;
                }
                case MODIFY: {
                    protocolOpElement = new ModifyResponseProtocolOp(r).encodeProtocolOp();
                    break;
                }
                case MODIFY_DN: {
                    protocolOpElement = new ModifyDNResponseProtocolOp(r).encodeProtocolOp();
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.PARAM_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_RESULT_INVALID_OP_TYPE.get(t.name()));
                }
            }
            final Control[] controls = r.getResponseControls();
            if (controls == null || controls.length == 0) {
                opElements.add(new ASN1Sequence(new ASN1Element[] { protocolOpElement }));
            }
            else {
                opElements.add(new ASN1Sequence(new ASN1Element[] { protocolOpElement, Control.encodeControls(controls) }));
            }
        }
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1Enumerated(changesApplied.intValue()), new ASN1Sequence(opElements) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public MultiUpdateChangesApplied getChangesApplied() {
        return this.changesApplied;
    }
    
    public List<ObjectPair<OperationType, LDAPResult>> getResults() {
        return this.results;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_MULTI_UPDATE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("MultiUpdateExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        buffer.append(", changesApplied=");
        buffer.append(this.changesApplied.name());
        buffer.append(", results={");
        final Iterator<ObjectPair<OperationType, LDAPResult>> resultIterator = this.results.iterator();
        while (resultIterator.hasNext()) {
            ((LDAPResult)resultIterator.next().getSecond()).toString(buffer);
            if (resultIterator.hasNext()) {
                buffer.append(", ");
            }
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
