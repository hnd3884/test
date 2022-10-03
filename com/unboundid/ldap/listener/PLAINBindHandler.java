package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.Entry;
import java.util.Map;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityResponseControl;
import java.util.Arrays;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PLAINBindHandler extends InMemorySASLBindHandler
{
    @Override
    public String getSASLMechanismName() {
        return "PLAIN";
    }
    
    @Override
    public BindResult processSASLBind(final InMemoryRequestHandler handler, final int messageID, final DN bindDN, final ASN1OctetString credentials, final List<Control> controls) {
        Map<String, Control> controlMap;
        try {
            controlMap = RequestControlPreProcessor.processControls((byte)96, controls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new BindResult(messageID, le.getResultCode(), le.getMessage(), le.getMatchedDN(), le.getReferralURLs(), le.getResponseControls());
        }
        if (credentials == null) {
            return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS, ListenerMessages.ERR_PLAIN_BIND_NO_CREDENTIALS.get(), null, null, (Control[])null);
        }
        int firstNullPos = -1;
        int secondNullPos = -1;
        final byte[] credBytes = credentials.getValue();
        for (int i = 0; i < credBytes.length; ++i) {
            if (credBytes[i] == 0) {
                if (firstNullPos >= 0) {
                    secondNullPos = i;
                    break;
                }
                firstNullPos = i;
            }
        }
        if (secondNullPos < 0) {
            return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS, ListenerMessages.ERR_PLAIN_BIND_MALFORMED_CREDENTIALS.get(), null, null, (Control[])null);
        }
        final String authcID = StaticUtils.toUTF8String(credBytes, firstNullPos + 1, secondNullPos - firstNullPos - 1);
        String authzID;
        if (firstNullPos == 0) {
            authzID = null;
        }
        else {
            authzID = StaticUtils.toUTF8String(credBytes, 0, firstNullPos);
        }
        DN authDN;
        try {
            authDN = handler.getDNForAuthzID(authcID);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS, le2.getMessage(), le2.getMatchedDN(), le2.getReferralURLs(), le2.getResponseControls());
        }
        final byte[] bindPWBytes = new byte[credBytes.length - secondNullPos - 1];
        System.arraycopy(credBytes, secondNullPos + 1, bindPWBytes, 0, bindPWBytes.length);
        boolean passwordValid;
        if (authDN.isNullDN()) {
            passwordValid = (bindPWBytes.length == 0 && authzID == null);
        }
        else {
            final Entry authEntry = handler.getEntry(authDN);
            if (authEntry == null) {
                final byte[] userPWBytes = handler.getAdditionalBindCredentials(authDN);
                passwordValid = Arrays.equals(bindPWBytes, userPWBytes);
            }
            else {
                final List<InMemoryDirectoryServerPassword> passwordList = handler.getPasswordsInEntry(authEntry, new ASN1OctetString(bindPWBytes));
                passwordValid = !passwordList.isEmpty();
            }
        }
        if (!passwordValid) {
            return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS, null, null, null, (Control[])null);
        }
        if (authzID != null) {
            try {
                authDN = handler.getDNForAuthzID(authzID);
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS, le3.getMessage(), le3.getMatchedDN(), le3.getReferralURLs(), le3.getResponseControls());
            }
        }
        handler.setAuthenticatedDN(authDN);
        Control[] responseControls;
        if (controlMap.containsKey("2.16.840.1.113730.3.4.16")) {
            if (authDN == null) {
                responseControls = new Control[] { new AuthorizationIdentityResponseControl("") };
            }
            else {
                responseControls = new Control[] { new AuthorizationIdentityResponseControl("dn:" + authDN.toString()) };
            }
        }
        else {
            responseControls = null;
        }
        return new BindResult(messageID, ResultCode.SUCCESS, null, null, null, responseControls);
    }
}
