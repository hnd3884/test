package com.unboundid.ldap.sdk;

import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.LDAPSDKException;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class EntrySourceException extends LDAPSDKException
{
    private static final long serialVersionUID = -9221149707074845318L;
    private final boolean mayContinueReading;
    
    public EntrySourceException(final boolean mayContinueReading, final Throwable cause) {
        super(StaticUtils.getExceptionMessage(cause), cause);
        Validator.ensureNotNull(cause);
        this.mayContinueReading = mayContinueReading;
    }
    
    public EntrySourceException(final boolean mayContinueReading, final String message, final Throwable cause) {
        super(message, cause);
        Validator.ensureNotNull(message, cause);
        this.mayContinueReading = mayContinueReading;
    }
    
    public final boolean mayContinueReading() {
        return this.mayContinueReading;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EntrySourceException(message='");
        buffer.append(this.getMessage());
        buffer.append("', mayContinueReading=");
        buffer.append(this.mayContinueReading);
        buffer.append(", cause='");
        buffer.append(StaticUtils.getExceptionMessage(this.getCause()));
        buffer.append("')");
    }
}
