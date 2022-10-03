package com.unboundid.ldif;

import com.unboundid.util.StaticUtils;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.Arrays;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDIFException extends LDAPSDKException
{
    private static final long serialVersionUID = 1665883395956836732L;
    private final boolean mayContinueReading;
    private final long lineNumber;
    private final List<String> dataLines;
    
    public LDIFException(final String message, final long lineNumber, final boolean mayContinueReading) {
        this(message, lineNumber, mayContinueReading, (List<? extends CharSequence>)null, null);
    }
    
    public LDIFException(final String message, final long lineNumber, final boolean mayContinueReading, final Throwable cause) {
        this(message, lineNumber, mayContinueReading, (List<? extends CharSequence>)null, cause);
    }
    
    public LDIFException(final String message, final long lineNumber, final boolean mayContinueReading, final CharSequence[] dataLines, final Throwable cause) {
        this(message, lineNumber, mayContinueReading, (dataLines == null) ? null : Arrays.asList(dataLines), cause);
    }
    
    public LDIFException(final String message, final long lineNumber, final boolean mayContinueReading, final List<? extends CharSequence> dataLines, final Throwable cause) {
        super(message, cause);
        Validator.ensureNotNull(message);
        this.lineNumber = lineNumber;
        this.mayContinueReading = mayContinueReading;
        if (dataLines == null) {
            this.dataLines = null;
        }
        else {
            final ArrayList<String> lineList = new ArrayList<String>(dataLines.size());
            for (final CharSequence s : dataLines) {
                lineList.add(s.toString());
            }
            this.dataLines = Collections.unmodifiableList((List<? extends String>)lineList);
        }
    }
    
    public long getLineNumber() {
        return this.lineNumber;
    }
    
    public boolean mayContinueReading() {
        return this.mayContinueReading;
    }
    
    public List<String> getDataLines() {
        return this.dataLines;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        final boolean includeCause = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeCauseInExceptionMessages");
        final boolean includeStackTrace = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeStackTraceInExceptionMessages");
        this.toString(buffer, includeCause, includeStackTrace);
    }
    
    public void toString(final StringBuilder buffer, final boolean includeCause, final boolean includeStackTrace) {
        buffer.append("LDIFException(lineNumber=");
        buffer.append(this.lineNumber);
        buffer.append(", mayContinueReading=");
        buffer.append(this.mayContinueReading);
        buffer.append(", message='");
        buffer.append(this.getMessage());
        if (this.dataLines != null) {
            buffer.append("', dataLines='");
            for (final CharSequence s : this.dataLines) {
                buffer.append(s);
                buffer.append("{end-of-line}");
            }
        }
        if (includeStackTrace) {
            buffer.append(", trace='");
            StaticUtils.getStackTrace(this.getStackTrace(), buffer);
            buffer.append('\'');
        }
        if (includeCause || includeStackTrace) {
            final Throwable cause = this.getCause();
            if (cause != null) {
                buffer.append(", cause=");
                buffer.append(StaticUtils.getExceptionMessage(cause, true, includeStackTrace));
            }
        }
        final String ldapSDKVersionString = ", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb";
        if (buffer.indexOf(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb") < 0) {
            buffer.append(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        }
        buffer.append(')');
    }
    
    @Override
    public String getExceptionMessage() {
        return this.toString();
    }
    
    @Override
    public String getExceptionMessage(final boolean includeCause, final boolean includeStackTrace) {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer, includeCause, includeStackTrace);
        return buffer.toString();
    }
}
