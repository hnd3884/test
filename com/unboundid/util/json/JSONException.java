package com.unboundid.util.json;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONException extends LDAPSDKException
{
    private static final long serialVersionUID = -769276547486681889L;
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
