package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PasswordModifyExtendedRequest extends ExtendedRequest
{
    public static final String PASSWORD_MODIFY_REQUEST_OID = "1.3.6.1.4.1.4203.1.11.1";
    private static final byte TYPE_USER_IDENTITY = Byte.MIN_VALUE;
    private static final byte TYPE_OLD_PASSWORD = -127;
    private static final byte TYPE_NEW_PASSWORD = -126;
    private static final long serialVersionUID = 4965048727456933570L;
    private final ASN1OctetString oldPassword;
    private final ASN1OctetString newPassword;
    private final String userIdentity;
    
    public PasswordModifyExtendedRequest(final String newPassword) {
        this(null, null, newPassword, null);
    }
    
    public PasswordModifyExtendedRequest(final byte[] newPassword) {
        this(null, null, newPassword, null);
    }
    
    public PasswordModifyExtendedRequest(final String oldPassword, final String newPassword) {
        this(null, oldPassword, newPassword, null);
    }
    
    public PasswordModifyExtendedRequest(final byte[] oldPassword, final byte[] newPassword) {
        this(null, oldPassword, newPassword, null);
    }
    
    public PasswordModifyExtendedRequest(final String userIdentity, final String oldPassword, final String newPassword) {
        this(userIdentity, oldPassword, newPassword, null);
    }
    
    public PasswordModifyExtendedRequest(final String userIdentity, final byte[] oldPassword, final byte[] newPassword) {
        this(userIdentity, oldPassword, newPassword, null);
    }
    
    public PasswordModifyExtendedRequest(final String userIdentity, final String oldPassword, final String newPassword, final Control[] controls) {
        super("1.3.6.1.4.1.4203.1.11.1", encodeValue(userIdentity, oldPassword, newPassword), controls);
        this.userIdentity = userIdentity;
        if (oldPassword == null) {
            this.oldPassword = null;
        }
        else {
            this.oldPassword = new ASN1OctetString((byte)(-127), oldPassword);
        }
        if (newPassword == null) {
            this.newPassword = null;
        }
        else {
            this.newPassword = new ASN1OctetString((byte)(-126), newPassword);
        }
    }
    
    public PasswordModifyExtendedRequest(final String userIdentity, final byte[] oldPassword, final byte[] newPassword, final Control[] controls) {
        super("1.3.6.1.4.1.4203.1.11.1", encodeValue(userIdentity, oldPassword, newPassword), controls);
        this.userIdentity = userIdentity;
        if (oldPassword == null) {
            this.oldPassword = null;
        }
        else {
            this.oldPassword = new ASN1OctetString((byte)(-127), oldPassword);
        }
        if (newPassword == null) {
            this.newPassword = null;
        }
        else {
            this.newPassword = new ASN1OctetString((byte)(-126), newPassword);
        }
    }
    
    public PasswordModifyExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_MODIFY_REQUEST_NO_VALUE.get());
        }
        try {
            ASN1OctetString oldPW = null;
            ASN1OctetString newPW = null;
            String userID = null;
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Sequence.decodeAsSequence(valueElement).elements();
            for (final ASN1Element e : arr$) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        userID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        oldPW = ASN1OctetString.decodeAsOctetString(e);
                        break;
                    }
                    case -126: {
                        newPW = ASN1OctetString.decodeAsOctetString(e);
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_MODIFY_REQUEST_INVALID_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.userIdentity = userID;
            this.oldPassword = oldPW;
            this.newPassword = newPW;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_MODIFY_REQUEST_CANNOT_DECODE.get(e2), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final String userIdentity, final String oldPassword, final String newPassword) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        if (userIdentity != null) {
            elements.add(new ASN1OctetString((byte)(-128), userIdentity));
        }
        if (oldPassword != null) {
            elements.add(new ASN1OctetString((byte)(-127), oldPassword));
        }
        if (newPassword != null) {
            elements.add(new ASN1OctetString((byte)(-126), newPassword));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    private static ASN1OctetString encodeValue(final String userIdentity, final byte[] oldPassword, final byte[] newPassword) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        if (userIdentity != null) {
            elements.add(new ASN1OctetString((byte)(-128), userIdentity));
        }
        if (oldPassword != null) {
            elements.add(new ASN1OctetString((byte)(-127), oldPassword));
        }
        if (newPassword != null) {
            elements.add(new ASN1OctetString((byte)(-126), newPassword));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getUserIdentity() {
        return this.userIdentity;
    }
    
    public String getOldPassword() {
        if (this.oldPassword == null) {
            return null;
        }
        return this.oldPassword.stringValue();
    }
    
    public byte[] getOldPasswordBytes() {
        if (this.oldPassword == null) {
            return null;
        }
        return this.oldPassword.getValue();
    }
    
    public ASN1OctetString getRawOldPassword() {
        return this.oldPassword;
    }
    
    public String getNewPassword() {
        if (this.newPassword == null) {
            return null;
        }
        return this.newPassword.stringValue();
    }
    
    public byte[] getNewPasswordBytes() {
        if (this.newPassword == null) {
            return null;
        }
        return this.newPassword.getValue();
    }
    
    public ASN1OctetString getRawNewPassword() {
        return this.newPassword;
    }
    
    public PasswordModifyExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new PasswordModifyExtendedResult(extendedResponse);
    }
    
    @Override
    public PasswordModifyExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public PasswordModifyExtendedRequest duplicate(final Control[] controls) {
        final byte[] oldPWBytes = (byte[])((this.oldPassword == null) ? null : this.oldPassword.getValue());
        final byte[] newPWBytes = (byte[])((this.newPassword == null) ? null : this.newPassword.getValue());
        final PasswordModifyExtendedRequest r = new PasswordModifyExtendedRequest(this.userIdentity, oldPWBytes, newPWBytes, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_PASSWORD_MODIFY.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordModifyExtendedRequest(");
        boolean dataAdded = false;
        if (this.userIdentity != null) {
            buffer.append("userIdentity='");
            buffer.append(this.userIdentity);
            buffer.append('\'');
            dataAdded = true;
        }
        if (this.oldPassword != null) {
            if (dataAdded) {
                buffer.append(", ");
            }
            buffer.append("oldPassword='");
            buffer.append(this.oldPassword.stringValue());
            buffer.append('\'');
            dataAdded = true;
        }
        if (this.newPassword != null) {
            if (dataAdded) {
                buffer.append(", ");
            }
            buffer.append("newPassword='");
            buffer.append(this.newPassword.stringValue());
            buffer.append('\'');
            dataAdded = true;
        }
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            if (dataAdded) {
                buffer.append(", ");
            }
            buffer.append("controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
