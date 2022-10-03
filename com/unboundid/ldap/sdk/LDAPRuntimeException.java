package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKRuntimeException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPRuntimeException extends LDAPSDKRuntimeException
{
    private static final long serialVersionUID = 6201514484547092642L;
    private final LDAPException ldapException;
    
    public LDAPRuntimeException(final LDAPException ldapException) {
        super(ldapException.getMessage(), ldapException.getCause());
        this.ldapException = ldapException;
    }
    
    public LDAPException getLDAPException() {
        return this.ldapException;
    }
    
    public void throwLDAPException() throws LDAPException {
        throw this.ldapException;
    }
    
    public ResultCode getResultCode() {
        return this.ldapException.getResultCode();
    }
    
    public String getMatchedDN() {
        return this.ldapException.getMatchedDN();
    }
    
    public String getDiagnosticMessage() {
        return this.ldapException.getDiagnosticMessage();
    }
    
    public String[] getReferralURLs() {
        return this.ldapException.getReferralURLs();
    }
    
    public boolean hasResponseControl() {
        return this.ldapException.hasResponseControl();
    }
    
    public boolean hasResponseControl(final String oid) {
        return this.ldapException.hasResponseControl(oid);
    }
    
    public Control[] getResponseControls() {
        return this.ldapException.getResponseControls();
    }
    
    public Control getResponseControl(final String oid) {
        return this.ldapException.getResponseControl(oid);
    }
    
    public LDAPResult toLDAPResult() {
        return this.ldapException.toLDAPResult();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        this.ldapException.toString(buffer);
    }
    
    @Override
    public String getExceptionMessage() {
        return this.ldapException.getExceptionMessage();
    }
    
    @Override
    public String getExceptionMessage(final boolean includeStackTrace, final boolean includeCause) {
        return this.ldapException.getExceptionMessage(includeStackTrace, includeCause);
    }
}
