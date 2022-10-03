package com.unboundid.util;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPSDKUsageException extends LDAPSDKRuntimeException
{
    private static final long serialVersionUID = 4488711069492709961L;
    
    public LDAPSDKUsageException(final String message) {
        super(message);
    }
    
    public LDAPSDKUsageException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPSDKUsageException(message='");
        buffer.append(this.getMessage());
        buffer.append('\'');
        final Throwable cause = this.getCause();
        if (cause != null) {
            buffer.append(", cause=");
            buffer.append(StaticUtils.getExceptionMessage(cause));
        }
        buffer.append(')');
    }
}
