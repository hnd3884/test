package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.Collection;
import com.unboundid.asn1.ASN1OctetString;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class NotificationSubscriptionDetails implements Serializable
{
    private static final long serialVersionUID = 7883889980556267057L;
    private final List<ASN1OctetString> details;
    private final String id;
    
    public NotificationSubscriptionDetails(final String id, final Collection<ASN1OctetString> details) {
        Validator.ensureNotNull(id);
        Validator.ensureNotNull(details);
        Validator.ensureFalse(details.isEmpty());
        this.id = id;
        this.details = Collections.unmodifiableList((List<? extends ASN1OctetString>)new ArrayList<ASN1OctetString>(details));
    }
    
    public String getID() {
        return this.id;
    }
    
    public List<ASN1OctetString> getDetails() {
        return this.details;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("NotificationSubscription(id='");
        buffer.append(this.id);
        buffer.append("')");
    }
}
