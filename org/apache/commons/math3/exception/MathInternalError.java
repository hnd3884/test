package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MathInternalError extends MathIllegalStateException
{
    private static final long serialVersionUID = -6276776513966934846L;
    private static final String REPORT_URL = "https://issues.apache.org/jira/browse/MATH";
    
    public MathInternalError() {
        this.getContext().addMessage(LocalizedFormats.INTERNAL_ERROR, "https://issues.apache.org/jira/browse/MATH");
    }
    
    public MathInternalError(final Throwable cause) {
        super(cause, LocalizedFormats.INTERNAL_ERROR, new Object[] { "https://issues.apache.org/jira/browse/MATH" });
    }
    
    public MathInternalError(final Localizable pattern, final Object... args) {
        super(pattern, args);
    }
}
