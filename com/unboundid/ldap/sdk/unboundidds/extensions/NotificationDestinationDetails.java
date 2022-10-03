package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
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
public final class NotificationDestinationDetails implements Serializable
{
    private static final long serialVersionUID = -6596207374277234834L;
    private final List<ASN1OctetString> details;
    private final List<NotificationSubscriptionDetails> subscriptions;
    private final String id;
    
    public NotificationDestinationDetails(final String id, final Collection<ASN1OctetString> details, final Collection<NotificationSubscriptionDetails> subscriptions) {
        Validator.ensureNotNull(id);
        Validator.ensureNotNull(details);
        Validator.ensureFalse(details.isEmpty());
        this.id = id;
        this.details = Collections.unmodifiableList((List<? extends ASN1OctetString>)new ArrayList<ASN1OctetString>(details));
        if (subscriptions == null) {
            this.subscriptions = Collections.emptyList();
        }
        else {
            this.subscriptions = Collections.unmodifiableList((List<? extends NotificationSubscriptionDetails>)new ArrayList<NotificationSubscriptionDetails>(subscriptions));
        }
    }
    
    public String getID() {
        return this.id;
    }
    
    public List<ASN1OctetString> getDetails() {
        return this.details;
    }
    
    public List<NotificationSubscriptionDetails> getSubscriptions() {
        return this.subscriptions;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("NotificationDestination(id='");
        buffer.append(this.id);
        buffer.append("', subscriptionIDs={");
        final Iterator<NotificationSubscriptionDetails> subscriptionIterator = this.subscriptions.iterator();
        while (subscriptionIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(subscriptionIterator.next().getID());
            buffer.append('\'');
            if (subscriptionIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
