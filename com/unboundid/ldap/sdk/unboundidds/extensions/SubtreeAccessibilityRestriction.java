package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SubtreeAccessibilityRestriction implements Serializable
{
    private static final long serialVersionUID = -1893365464740536092L;
    private final Date effectiveTime;
    private final String bypassUserDN;
    private final String subtreeBaseDN;
    private final SubtreeAccessibilityState accessibilityState;
    
    public SubtreeAccessibilityRestriction(final String subtreeBaseDN, final SubtreeAccessibilityState accessibilityState, final String bypassUserDN, final Date effectiveTime) {
        this.subtreeBaseDN = subtreeBaseDN;
        this.accessibilityState = accessibilityState;
        this.bypassUserDN = bypassUserDN;
        this.effectiveTime = effectiveTime;
    }
    
    public String getSubtreeBaseDN() {
        return this.subtreeBaseDN;
    }
    
    public SubtreeAccessibilityState getAccessibilityState() {
        return this.accessibilityState;
    }
    
    public String getBypassUserDN() {
        return this.bypassUserDN;
    }
    
    public Date getEffectiveTime() {
        return this.effectiveTime;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SubtreeAccessibilityRestriction(base='");
        buffer.append(this.subtreeBaseDN.replace("\\\"", "\\22"));
        buffer.append("', state='");
        buffer.append(this.accessibilityState.getStateName());
        buffer.append('\'');
        if (this.bypassUserDN != null) {
            buffer.append(", bypassUser='");
            buffer.append(this.bypassUserDN.replace("\\\"", "\\22"));
            buffer.append('\'');
        }
        buffer.append(", effectiveTime='");
        buffer.append(StaticUtils.encodeGeneralizedTime(this.effectiveTime));
        buffer.append("')");
    }
}
