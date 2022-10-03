package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import com.unboundid.ldap.matchingrules.BooleanMatchingRule;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.OctetStringMatchingRule;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UndeleteRequestControl extends Control
{
    public static final String UNDELETE_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.23";
    public static final String ATTR_CHANGES = "ds-undelete-changes";
    public static final String ATTR_DISABLE_ACCOUNT = "ds-undelete-disable-account";
    public static final String ATTR_MUST_CHANGE_PASSWORD = "ds-undelete-must-change-password";
    public static final String ATTR_NEW_PASSWORD = "ds-undelete-new-password";
    public static final String ATTR_OLD_PASSWORD = "ds-undelete-old-password";
    public static final String ATTR_SOFT_DELETED_ENTRY_DN = "ds-undelete-from-dn";
    private static final long serialVersionUID = 5338045977962112876L;
    
    public UndeleteRequestControl() {
        super("1.3.6.1.4.1.30221.2.5.23", true, null);
    }
    
    public UndeleteRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            try {
                final ASN1Sequence valueSequence = ASN1Sequence.decodeAsSequence(control.getValue().getValue());
                final ASN1Element[] elements = valueSequence.elements();
                if (elements.length > 0) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNDELETE_REQUEST_UNSUPPORTED_VALUE_ELEMENT_TYPE.get(StaticUtils.toHex(elements[0].getType())));
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw le;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNDELETE_REQUEST_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
    }
    
    public static AddRequest createUndeleteRequest(final String targetDN, final String softDeletedEntryDN) {
        return createUndeleteRequest(targetDN, softDeletedEntryDN, null, null, null, null, null);
    }
    
    public static AddRequest createUndeleteRequest(final String targetDN, final String softDeletedEntryDN, final List<Modification> changes, final String oldPassword, final String newPassword, final Boolean mustChangePassword, final Boolean disableAccount) {
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>(6);
        attributes.add(new Attribute("ds-undelete-from-dn", softDeletedEntryDN));
        if (changes != null && !changes.isEmpty()) {
            final LDIFModifyChangeRecord changeRecord = new LDIFModifyChangeRecord(targetDN, changes);
            final String[] modLdifLines = changeRecord.toLDIF(0);
            final StringBuilder modLDIFBuffer = new StringBuilder();
            for (int i = 2; i < modLdifLines.length; ++i) {
                modLDIFBuffer.append(modLdifLines[i]);
                modLDIFBuffer.append(StaticUtils.EOL);
            }
            attributes.add(new Attribute("ds-undelete-changes", OctetStringMatchingRule.getInstance(), modLDIFBuffer.toString()));
        }
        if (oldPassword != null) {
            attributes.add(new Attribute("ds-undelete-old-password", OctetStringMatchingRule.getInstance(), oldPassword));
        }
        if (newPassword != null) {
            attributes.add(new Attribute("ds-undelete-new-password", OctetStringMatchingRule.getInstance(), newPassword));
        }
        if (mustChangePassword != null) {
            attributes.add(new Attribute("ds-undelete-must-change-password", BooleanMatchingRule.getInstance(), mustChangePassword ? "true" : "false"));
        }
        if (disableAccount != null) {
            attributes.add(new Attribute("ds-undelete-disable-account", BooleanMatchingRule.getInstance(), disableAccount ? "true" : "false"));
        }
        final Control[] controls = { new UndeleteRequestControl() };
        return new AddRequest(targetDN, attributes, controls);
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_UNDELETE_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UndeleteRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
