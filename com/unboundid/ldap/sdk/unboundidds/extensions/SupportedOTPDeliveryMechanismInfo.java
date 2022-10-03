package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SupportedOTPDeliveryMechanismInfo implements Serializable
{
    private static final long serialVersionUID = -6315998976212985213L;
    private final Boolean isSupported;
    private final String deliveryMechanism;
    private final String recipientID;
    
    public SupportedOTPDeliveryMechanismInfo(final String deliveryMechanism, final Boolean isSupported, final String recipientID) {
        Validator.ensureNotNull(deliveryMechanism);
        this.deliveryMechanism = deliveryMechanism;
        this.isSupported = isSupported;
        this.recipientID = recipientID;
    }
    
    public String getDeliveryMechanism() {
        return this.deliveryMechanism;
    }
    
    public Boolean isSupported() {
        return this.isSupported;
    }
    
    public String getRecipientID() {
        return this.recipientID;
    }
    
    @Override
    public int hashCode() {
        int hc = this.deliveryMechanism.hashCode();
        if (this.isSupported == null) {
            hc += 2;
        }
        else if (this.isSupported) {
            ++hc;
        }
        if (this.recipientID != null) {
            hc += this.recipientID.hashCode();
        }
        return hc;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SupportedOTPDeliveryMechanismInfo)) {
            return false;
        }
        final SupportedOTPDeliveryMechanismInfo i = (SupportedOTPDeliveryMechanismInfo)o;
        if (!this.deliveryMechanism.equals(i.deliveryMechanism)) {
            return false;
        }
        if (this.isSupported == null) {
            if (i.isSupported != null) {
                return false;
            }
        }
        else if (!this.isSupported.equals(i.isSupported)) {
            return false;
        }
        if (this.recipientID == null) {
            return i.recipientID == null;
        }
        return this.recipientID.equals(i.recipientID);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SupportedOTPDeliveryMechanismInfo(mechanism='");
        buffer.append(this.deliveryMechanism);
        buffer.append('\'');
        if (this.isSupported != null) {
            buffer.append(", isSupported=");
            buffer.append(this.isSupported);
        }
        if (this.recipientID != null) {
            buffer.append(", recipientID='");
            buffer.append(this.recipientID);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
