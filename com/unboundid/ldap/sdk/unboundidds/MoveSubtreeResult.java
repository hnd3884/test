package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MoveSubtreeResult implements Serializable
{
    private static final long serialVersionUID = 2881207705643180021L;
    private final boolean sourceServerAltered;
    private final boolean targetServerAltered;
    private final int entriesAddedToTarget;
    private final int entriesDeletedFromSource;
    private final int entriesReadFromSource;
    private final ResultCode resultCode;
    private final String adminActionRequired;
    private final String errorMessage;
    
    MoveSubtreeResult(final ResultCode resultCode, final String errorMessage, final String adminActionRequired, final boolean sourceServerAltered, final boolean targetServerAltered, final int entriesReadFromSource, final int entriesAddedToTarget, final int entriesDeletedFromSource) {
        this.resultCode = resultCode;
        this.errorMessage = errorMessage;
        this.adminActionRequired = adminActionRequired;
        this.sourceServerAltered = sourceServerAltered;
        this.targetServerAltered = targetServerAltered;
        this.entriesReadFromSource = entriesReadFromSource;
        this.entriesAddedToTarget = entriesAddedToTarget;
        this.entriesDeletedFromSource = entriesDeletedFromSource;
    }
    
    public ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public String getAdminActionRequired() {
        return this.adminActionRequired;
    }
    
    public boolean sourceServerAltered() {
        return this.sourceServerAltered;
    }
    
    public boolean targetServerAltered() {
        return this.targetServerAltered;
    }
    
    public int getEntriesReadFromSource() {
        return this.entriesReadFromSource;
    }
    
    public int getEntriesAddedToTarget() {
        return this.entriesAddedToTarget;
    }
    
    public int getEntriesDeletedFromSource() {
        return this.entriesDeletedFromSource;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("MoveSubtreeResult(resultCode=");
        buffer.append(this.resultCode.getName());
        if (this.errorMessage != null) {
            buffer.append(", errorMessage='");
            buffer.append(this.errorMessage);
            buffer.append('\'');
        }
        if (this.adminActionRequired != null) {
            buffer.append(", adminActionRequired='");
            buffer.append(this.adminActionRequired);
            buffer.append('\'');
        }
        buffer.append(", sourceServerAltered=");
        buffer.append(this.sourceServerAltered);
        buffer.append(", targetServerAltered=");
        buffer.append(this.targetServerAltered);
        buffer.append(", entriesReadFromSource=");
        buffer.append(this.entriesReadFromSource);
        buffer.append(", entriesAddedToTarget=");
        buffer.append(this.entriesAddedToTarget);
        buffer.append(", entriesDeletedFromSource=");
        buffer.append(this.entriesDeletedFromSource);
        buffer.append(')');
    }
}
