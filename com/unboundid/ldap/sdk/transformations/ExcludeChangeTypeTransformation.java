package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldif.LDIFChangeRecord;
import java.util.EnumSet;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ChangeType;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExcludeChangeTypeTransformation implements LDIFChangeRecordTransformation, Serializable
{
    private static final long serialVersionUID = -6927917616913251572L;
    private final Set<ChangeType> excludedChangeTypes;
    
    public ExcludeChangeTypeTransformation(final ChangeType... changeTypes) {
        this(StaticUtils.toList(changeTypes));
    }
    
    public ExcludeChangeTypeTransformation(final Collection<ChangeType> changeTypes) {
        if (changeTypes == null) {
            this.excludedChangeTypes = Collections.emptySet();
        }
        else {
            final EnumSet<ChangeType> ctSet = EnumSet.noneOf(ChangeType.class);
            ctSet.addAll((Collection<?>)changeTypes);
            this.excludedChangeTypes = Collections.unmodifiableSet((Set<? extends ChangeType>)ctSet);
        }
    }
    
    @Override
    public LDIFChangeRecord transformChangeRecord(final LDIFChangeRecord changeRecord) {
        if (this.excludedChangeTypes.contains(changeRecord.getChangeType())) {
            return null;
        }
        return changeRecord;
    }
    
    @Override
    public LDIFChangeRecord translate(final LDIFChangeRecord original, final long firstLineNumber) {
        if (this.excludedChangeTypes.contains(original.getChangeType())) {
            return null;
        }
        return original;
    }
    
    @Override
    public LDIFChangeRecord translateChangeRecordToWrite(final LDIFChangeRecord original) {
        if (this.excludedChangeTypes.contains(original.getChangeType())) {
            return null;
        }
        return original;
    }
}
