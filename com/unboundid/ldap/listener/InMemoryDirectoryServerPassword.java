package com.unboundid.ldap.listener;

import com.unboundid.ldap.matchingrules.OctetStringMatchingRule;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Iterator;
import java.util.List;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InMemoryDirectoryServerPassword
{
    private final ASN1OctetString storedPassword;
    private final InMemoryPasswordEncoder passwordEncoder;
    private final ReadOnlyEntry userEntry;
    private final String attributeName;
    
    InMemoryDirectoryServerPassword(final ASN1OctetString storedPassword, final ReadOnlyEntry userEntry, final String attributeName, final List<InMemoryPasswordEncoder> passwordEncoders) {
        this.storedPassword = storedPassword;
        this.userEntry = userEntry;
        this.attributeName = attributeName;
        InMemoryPasswordEncoder encoder = null;
        for (final InMemoryPasswordEncoder e : passwordEncoders) {
            if (e.passwordStartsWithPrefix(storedPassword)) {
                encoder = e;
                break;
            }
        }
        this.passwordEncoder = encoder;
    }
    
    public ASN1OctetString getStoredPassword() {
        return this.storedPassword;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public boolean isEncoded() {
        return this.passwordEncoder != null;
    }
    
    public InMemoryPasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }
    
    public ASN1OctetString getClearPassword() throws LDAPException {
        if (this.passwordEncoder == null) {
            return this.storedPassword;
        }
        return this.passwordEncoder.extractClearPasswordFromEncodedPassword(this.storedPassword, this.userEntry);
    }
    
    public boolean matchesClearPassword(final ASN1OctetString clearPassword) throws LDAPException {
        if (this.passwordEncoder == null) {
            return OctetStringMatchingRule.getInstance().valuesMatch(clearPassword, this.storedPassword);
        }
        return this.passwordEncoder.clearPasswordMatchesEncodedPassword(clearPassword, this.storedPassword, this.userEntry);
    }
}
