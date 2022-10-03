package io.netty.resolver.dns;

final class UnixResolverOptions
{
    private final int ndots;
    private final int timeout;
    private final int attempts;
    
    UnixResolverOptions(final int ndots, final int timeout, final int attempts) {
        this.ndots = ndots;
        this.timeout = timeout;
        this.attempts = attempts;
    }
    
    static Builder newBuilder() {
        return new Builder();
    }
    
    int ndots() {
        return this.ndots;
    }
    
    int timeout() {
        return this.timeout;
    }
    
    int attempts() {
        return this.attempts;
    }
    
    static final class Builder
    {
        private int ndots;
        private int timeout;
        private int attempts;
        
        private Builder() {
            this.ndots = 1;
            this.timeout = 5;
            this.attempts = 16;
        }
        
        void setNdots(final int ndots) {
            this.ndots = ndots;
        }
        
        void setTimeout(final int timeout) {
            this.timeout = timeout;
        }
        
        void setAttempts(final int attempts) {
            this.attempts = attempts;
        }
        
        UnixResolverOptions build() {
            return new UnixResolverOptions(this.ndots, this.timeout, this.attempts);
        }
    }
}
