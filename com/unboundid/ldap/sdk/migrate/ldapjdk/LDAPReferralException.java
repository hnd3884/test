package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPReferralException extends LDAPException
{
    private static final long serialVersionUID = 7867903105944011998L;
    private final String[] referralURLs;
    
    public LDAPReferralException() {
        super(null, 10);
        this.referralURLs = new String[0];
    }
    
    public LDAPReferralException(final String message, final int resultCode, final String serverErrorMessage) {
        super(message, resultCode, serverErrorMessage, null);
        this.referralURLs = new String[0];
    }
    
    public LDAPReferralException(final String message, final int resultCode, final String[] referrals) {
        super(message, resultCode, null, null);
        this.referralURLs = referrals;
    }
    
    public LDAPReferralException(final com.unboundid.ldap.sdk.LDAPException ldapException) {
        super(ldapException);
        this.referralURLs = ldapException.getReferralURLs();
    }
    
    public LDAPReferralException(final SearchResultReference reference) {
        super(null, 10);
        this.referralURLs = reference.getReferralURLs();
    }
    
    public String[] getURLs() {
        return this.referralURLs;
    }
}
