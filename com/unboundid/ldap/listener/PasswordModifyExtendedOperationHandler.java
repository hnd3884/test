package com.unboundid.ldap.listener;

import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import java.util.HashSet;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.Modification;
import java.util.ArrayList;
import java.security.SecureRandom;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordModifyExtendedOperationHandler extends InMemoryExtendedOperationHandler
{
    @Override
    public String getExtendedOperationHandlerName() {
        return "Password Modify";
    }
    
    @Override
    public List<String> getSupportedExtendedRequestOIDs() {
        return Collections.singletonList("1.3.6.1.4.1.4203.1.11.1");
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final InMemoryRequestHandler handler, final int messageID, final ExtendedRequest request) {
        for (final Control c : request.getControls()) {
            if (c.isCritical()) {
                return new ExtendedResult(messageID, ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_PW_MOD_EXTOP_UNSUPPORTED_CONTROL.get(c.getOID()), null, null, null, null, null);
            }
        }
        PasswordModifyExtendedRequest pwModRequest;
        try {
            pwModRequest = new PasswordModifyExtendedRequest(request);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new ExtendedResult(messageID, le.getResultCode(), le.getDiagnosticMessage(), le.getMatchedDN(), le.getReferralURLs(), null, null, null);
        }
        final String userIdentity = pwModRequest.getUserIdentity();
        final byte[] oldPWBytes = pwModRequest.getOldPasswordBytes();
        final byte[] newPWBytes = pwModRequest.getNewPasswordBytes();
        DN targetDN = null;
        Label_0296: {
            if (userIdentity == null) {
                targetDN = handler.getAuthenticatedDN();
            }
            else {
                final String lowerUserIdentity = StaticUtils.toLowerCase(userIdentity);
                Label_0244: {
                    if (!lowerUserIdentity.startsWith("dn:")) {
                        if (!lowerUserIdentity.startsWith("u:")) {
                            break Label_0244;
                        }
                    }
                    try {
                        targetDN = handler.getDNForAuthzID(userIdentity);
                        break Label_0296;
                    }
                    catch (final LDAPException le2) {
                        Debug.debugException(le2);
                        return new PasswordModifyExtendedResult(messageID, le2.getResultCode(), le2.getMessage(), le2.getMatchedDN(), le2.getReferralURLs(), null, le2.getResponseControls());
                    }
                    try {
                        targetDN = new DN(userIdentity);
                    }
                    catch (final LDAPException le2) {
                        Debug.debugException(le2);
                        return new PasswordModifyExtendedResult(messageID, ResultCode.INVALID_DN_SYNTAX, ListenerMessages.ERR_PW_MOD_EXTOP_CANNOT_PARSE_USER_IDENTITY.get(userIdentity), null, null, null, null);
                    }
                }
            }
        }
        if (targetDN == null || targetDN.isNullDN()) {
            return new PasswordModifyExtendedResult(messageID, ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_PW_MOD_NO_IDENTITY.get(), null, null, null, null);
        }
        final Entry userEntry = handler.getEntry(targetDN);
        if (userEntry == null) {
            return new PasswordModifyExtendedResult(messageID, ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_PW_MOD_EXTOP_CANNOT_GET_USER_ENTRY.get(targetDN.toString()), null, null, null, null);
        }
        final List<String> passwordAttributes = handler.getPasswordAttributes();
        if (passwordAttributes.isEmpty()) {
            return new PasswordModifyExtendedResult(messageID, ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_PW_MOD_EXTOP_NO_PW_ATTRS.get(), null, null, null, null);
        }
        if (oldPWBytes == null) {
            if (handler.getAuthenticatedDN().isNullDN()) {
                return new PasswordModifyExtendedResult(messageID, ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_PW_MOD_EXTOP_NO_AUTHENTICATION.get(), null, null, null, null);
            }
        }
        else {
            final List<InMemoryDirectoryServerPassword> passwordList = handler.getPasswordsInEntry(userEntry, pwModRequest.getRawOldPassword());
            if (passwordList.isEmpty()) {
                return new PasswordModifyExtendedResult(messageID, ResultCode.INVALID_CREDENTIALS, null, null, null, null, null);
            }
        }
        byte[] pwBytes;
        ASN1OctetString genPW;
        if (newPWBytes == null) {
            final SecureRandom random = new SecureRandom();
            final byte[] pwAlphabet = StaticUtils.getBytes("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            pwBytes = new byte[8];
            for (int i = 0; i < pwBytes.length; ++i) {
                pwBytes[i] = pwAlphabet[random.nextInt(pwAlphabet.length)];
            }
            genPW = new ASN1OctetString(pwBytes);
        }
        else {
            genPW = null;
            pwBytes = newPWBytes;
        }
        final List<InMemoryDirectoryServerPassword> existingPasswords = handler.getPasswordsInEntry(userEntry, null);
        final ArrayList<Modification> mods = new ArrayList<Modification>(existingPasswords.size() + 1);
        if (existingPasswords.isEmpty()) {
            mods.add(new Modification(ModificationType.REPLACE, passwordAttributes.get(0), pwBytes));
        }
        else {
            final HashSet<String> usedPWAttrs = new HashSet<String>(StaticUtils.computeMapCapacity(existingPasswords.size()));
            for (final InMemoryDirectoryServerPassword p : existingPasswords) {
                final String attr = StaticUtils.toLowerCase(p.getAttributeName());
                if (usedPWAttrs.isEmpty()) {
                    usedPWAttrs.add(attr);
                    mods.add(new Modification(ModificationType.REPLACE, p.getAttributeName(), pwBytes));
                }
                else {
                    if (usedPWAttrs.contains(attr)) {
                        continue;
                    }
                    usedPWAttrs.add(attr);
                    mods.add(new Modification(ModificationType.REPLACE, p.getAttributeName()));
                }
            }
        }
        try {
            handler.modifyEntry(userEntry.getDN(), mods);
            return new PasswordModifyExtendedResult(messageID, ResultCode.SUCCESS, null, null, null, genPW, null);
        }
        catch (final LDAPException le3) {
            Debug.debugException(le3);
            return new PasswordModifyExtendedResult(messageID, le3.getResultCode(), ListenerMessages.ERR_PW_MOD_EXTOP_CANNOT_CHANGE_PW.get(userEntry.getDN(), le3.getMessage()), null, null, null, null);
        }
    }
}
