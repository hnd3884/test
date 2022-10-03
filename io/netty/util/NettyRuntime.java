package io.netty.util;

import io.netty.util.internal.SystemPropertyUtil;
import java.util.Locale;
import io.netty.util.internal.ObjectUtil;

public final class NettyRuntime
{
    private static final AvailableProcessorsHolder holder;
    
    public static void setAvailableProcessors(final int availableProcessors) {
        NettyRuntime.holder.setAvailableProcessors(availableProcessors);
    }
    
    public static int availableProcessors() {
        return NettyRuntime.holder.availableProcessors();
    }
    
    private NettyRuntime() {
    }
    
    static {
        holder = new AvailableProcessorsHolder();
    }
    
    static class AvailableProcessorsHolder
    {
        private int availableProcessors;
        
        synchronized void setAvailableProcessors(final int availableProcessors) {
            ObjectUtil.checkPositive(availableProcessors, "availableProcessors");
            if (this.availableProcessors != 0) {
                final String message = String.format(Locale.ROOT, "availableProcessors is already set to [%d], rejecting [%d]", this.availableProcessors, availableProcessors);
                throw new IllegalStateException(message);
            }
            this.availableProcessors = availableProcessors;
        }
        
        @SuppressForbidden(reason = "to obtain default number of available processors")
        synchronized int availableProcessors() {
            if (this.availableProcessors == 0) {
                final int availableProcessors = SystemPropertyUtil.getInt("io.netty.availableProcessors", Runtime.getRuntime().availableProcessors());
                this.setAvailableProcessors(availableProcessors);
            }
            return this.availableProcessors;
        }
    }
}
