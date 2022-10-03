package io.netty.channel.kqueue;

import io.netty.util.internal.ObjectUtil;

public final class AcceptFilter
{
    static final AcceptFilter PLATFORM_UNSUPPORTED;
    private final String filterName;
    private final String filterArgs;
    
    public AcceptFilter(final String filterName, final String filterArgs) {
        this.filterName = ObjectUtil.checkNotNull(filterName, "filterName");
        this.filterArgs = ObjectUtil.checkNotNull(filterArgs, "filterArgs");
    }
    
    public String filterName() {
        return this.filterName;
    }
    
    public String filterArgs() {
        return this.filterArgs;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AcceptFilter)) {
            return false;
        }
        final AcceptFilter rhs = (AcceptFilter)o;
        return this.filterName.equals(rhs.filterName) && this.filterArgs.equals(rhs.filterArgs);
    }
    
    @Override
    public int hashCode() {
        return 31 * (31 + this.filterName.hashCode()) + this.filterArgs.hashCode();
    }
    
    @Override
    public String toString() {
        return this.filterName + ", " + this.filterArgs;
    }
    
    static {
        PLATFORM_UNSUPPORTED = new AcceptFilter("", "");
    }
}
