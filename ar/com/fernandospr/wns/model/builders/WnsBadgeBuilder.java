package ar.com.fernandospr.wns.model.builders;

import ar.com.fernandospr.wns.model.WnsBadge;

public class WnsBadgeBuilder
{
    private WnsBadge badge;
    
    public WnsBadgeBuilder() {
        this.badge = new WnsBadge();
    }
    
    public WnsBadgeBuilder value(final Integer value) {
        this.badge.value = String.valueOf(value);
        return this;
    }
    
    public WnsBadgeBuilder value(final String value) {
        this.badge.value = value;
        return this;
    }
    
    public WnsBadgeBuilder version(final Integer version) {
        this.badge.version = version;
        return this;
    }
    
    public WnsBadge build() {
        return this.badge;
    }
}
