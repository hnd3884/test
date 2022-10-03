package com.unboundid.util;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class LDAPSDKException extends Exception
{
    private static final long serialVersionUID = 8080186918165352228L;
    
    protected LDAPSDKException(final String message) {
        super(message);
    }
    
    protected LDAPSDKException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append(super.toString());
    }
    
    public String getExceptionMessage() {
        final boolean includeCause = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeCauseInExceptionMessages");
        final boolean includeStackTrace = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeStackTraceInExceptionMessages");
        return this.getExceptionMessage(includeCause, includeStackTrace);
    }
    
    public String getExceptionMessage(final boolean includeCause, final boolean includeStackTrace) {
        final StringBuilder buffer = new StringBuilder();
        final String message = this.getMessage();
        if (message == null || message.isEmpty()) {
            this.toString(buffer);
        }
        else {
            buffer.append(message);
        }
        if (includeStackTrace) {
            buffer.append(", trace=");
            StaticUtils.getStackTrace(this, buffer);
        }
        else if (includeCause) {
            final Throwable cause = this.getCause();
            if (cause != null) {
                buffer.append(", cause=");
                buffer.append(StaticUtils.getExceptionMessage(cause));
            }
        }
        final String ldapSDKVersionString = ", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb";
        if (buffer.indexOf(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb") < 0) {
            buffer.append(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        }
        return buffer.toString();
    }
}
