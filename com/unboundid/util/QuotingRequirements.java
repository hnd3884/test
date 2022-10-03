package com.unboundid.util;

import java.io.Serializable;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class QuotingRequirements implements Serializable
{
    private static final long serialVersionUID = -1430038162579028168L;
    private final boolean requiresSingleQuotesOnUnix;
    private final boolean requiresDoubleQuotesOnUnix;
    private final boolean includesSingleQuote;
    private final boolean includesDoubleQuote;
    
    QuotingRequirements(final boolean requiresSingleQuotesOnUnix, final boolean requiresDoubleQuotesOnUnix, final boolean includesSingleQuote, final boolean includesDoubleQuote) {
        this.requiresSingleQuotesOnUnix = requiresSingleQuotesOnUnix;
        this.requiresDoubleQuotesOnUnix = requiresDoubleQuotesOnUnix;
        this.includesSingleQuote = includesSingleQuote;
        this.includesDoubleQuote = includesDoubleQuote;
    }
    
    public boolean requiresSingleQuotesOnUnix() {
        return this.requiresSingleQuotesOnUnix;
    }
    
    public boolean requiresDoubleQuotesOnUnix() {
        return this.requiresDoubleQuotesOnUnix;
    }
    
    public boolean includesSingleQuote() {
        return this.includesSingleQuote;
    }
    
    public boolean includesDoubleQuote() {
        return this.includesDoubleQuote;
    }
}
