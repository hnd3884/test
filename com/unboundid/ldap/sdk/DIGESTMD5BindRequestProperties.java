package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DIGESTMD5BindRequestProperties implements Serializable
{
    private static final long serialVersionUID = -2000440962628192477L;
    private ASN1OctetString password;
    private List<SASLQualityOfProtection> allowedQoP;
    private String authenticationID;
    private String authorizationID;
    private String realm;
    
    public DIGESTMD5BindRequestProperties(final String authenticationID, final String password) {
        this(authenticationID, new ASN1OctetString(password));
    }
    
    public DIGESTMD5BindRequestProperties(final String authenticationID, final byte[] password) {
        this(authenticationID, new ASN1OctetString(password));
    }
    
    public DIGESTMD5BindRequestProperties(final String authenticationID, final ASN1OctetString password) {
        Validator.ensureNotNull(authenticationID);
        this.authenticationID = authenticationID;
        if (password == null) {
            this.password = new ASN1OctetString();
        }
        else {
            this.password = password;
        }
        this.authorizationID = null;
        this.realm = null;
        this.allowedQoP = Collections.singletonList(SASLQualityOfProtection.AUTH);
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public void setAuthenticationID(final String authenticationID) {
        Validator.ensureNotNull(authenticationID);
        this.authenticationID = authenticationID;
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public void setAuthorizationID(final String authorizationID) {
        this.authorizationID = authorizationID;
    }
    
    public ASN1OctetString getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.setPassword(new ASN1OctetString(password));
    }
    
    public void setPassword(final byte[] password) {
        this.setPassword(new ASN1OctetString(password));
    }
    
    public void setPassword(final ASN1OctetString password) {
        if (password == null) {
            this.password = new ASN1OctetString();
        }
        else {
            this.password = password;
        }
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    public void setRealm(final String realm) {
        this.realm = realm;
    }
    
    public List<SASLQualityOfProtection> getAllowedQoP() {
        return this.allowedQoP;
    }
    
    public void setAllowedQoP(final List<SASLQualityOfProtection> allowedQoP) {
        if (allowedQoP == null || allowedQoP.isEmpty()) {
            this.allowedQoP = Collections.singletonList(SASLQualityOfProtection.AUTH);
        }
        else {
            this.allowedQoP = Collections.unmodifiableList((List<? extends SASLQualityOfProtection>)new ArrayList<SASLQualityOfProtection>(allowedQoP));
        }
    }
    
    public void setAllowedQoP(final SASLQualityOfProtection... allowedQoP) {
        this.setAllowedQoP(StaticUtils.toList(allowedQoP));
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("DIGESTMD5BindRequestProperties(authenticationID='");
        buffer.append(this.authenticationID);
        buffer.append('\'');
        if (this.authorizationID != null) {
            buffer.append(", authorizationID='");
            buffer.append(this.authorizationID);
            buffer.append('\'');
        }
        if (this.realm != null) {
            buffer.append(", realm='");
            buffer.append(this.realm);
            buffer.append('\'');
        }
        buffer.append(", qop='");
        buffer.append(SASLQualityOfProtection.toString(this.allowedQoP));
        buffer.append("')");
    }
}
