package io.opencensus.trace;

import java.util.Arrays;
import io.opencensus.internal.Utils;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.annotation.Nullable;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Status
{
    private static final List<Status> STATUS_LIST;
    public static final Status OK;
    public static final Status CANCELLED;
    public static final Status UNKNOWN;
    public static final Status INVALID_ARGUMENT;
    public static final Status DEADLINE_EXCEEDED;
    public static final Status NOT_FOUND;
    public static final Status ALREADY_EXISTS;
    public static final Status PERMISSION_DENIED;
    public static final Status UNAUTHENTICATED;
    public static final Status RESOURCE_EXHAUSTED;
    public static final Status FAILED_PRECONDITION;
    public static final Status ABORTED;
    public static final Status OUT_OF_RANGE;
    public static final Status UNIMPLEMENTED;
    public static final Status INTERNAL;
    public static final Status UNAVAILABLE;
    public static final Status DATA_LOSS;
    private final CanonicalCode canonicalCode;
    @Nullable
    private final String description;
    
    private static List<Status> buildStatusList() {
        final TreeMap<Integer, Status> canonicalizer = new TreeMap<Integer, Status>();
        for (final CanonicalCode code : CanonicalCode.values()) {
            final Status replaced = canonicalizer.put(code.value(), new Status(code, null));
            if (replaced != null) {
                throw new IllegalStateException("Code value duplication between " + replaced.getCanonicalCode().name() + " & " + code.name());
            }
        }
        return Collections.unmodifiableList((List<? extends Status>)new ArrayList<Status>(canonicalizer.values()));
    }
    
    private Status(final CanonicalCode canonicalCode, @Nullable final String description) {
        this.canonicalCode = Utils.checkNotNull(canonicalCode, "canonicalCode");
        this.description = description;
    }
    
    public Status withDescription(@Nullable final String description) {
        if (Utils.equalsObjects(this.description, description)) {
            return this;
        }
        return new Status(this.canonicalCode, description);
    }
    
    public CanonicalCode getCanonicalCode() {
        return this.canonicalCode;
    }
    
    @Nullable
    public String getDescription() {
        return this.description;
    }
    
    public boolean isOk() {
        return CanonicalCode.OK == this.canonicalCode;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Status)) {
            return false;
        }
        final Status that = (Status)obj;
        return this.canonicalCode == that.canonicalCode && Utils.equalsObjects(this.description, that.description);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { this.canonicalCode, this.description });
    }
    
    @Override
    public String toString() {
        return "Status{canonicalCode=" + this.canonicalCode + ", description=" + this.description + "}";
    }
    
    static {
        STATUS_LIST = buildStatusList();
        OK = CanonicalCode.OK.toStatus();
        CANCELLED = CanonicalCode.CANCELLED.toStatus();
        UNKNOWN = CanonicalCode.UNKNOWN.toStatus();
        INVALID_ARGUMENT = CanonicalCode.INVALID_ARGUMENT.toStatus();
        DEADLINE_EXCEEDED = CanonicalCode.DEADLINE_EXCEEDED.toStatus();
        NOT_FOUND = CanonicalCode.NOT_FOUND.toStatus();
        ALREADY_EXISTS = CanonicalCode.ALREADY_EXISTS.toStatus();
        PERMISSION_DENIED = CanonicalCode.PERMISSION_DENIED.toStatus();
        UNAUTHENTICATED = CanonicalCode.UNAUTHENTICATED.toStatus();
        RESOURCE_EXHAUSTED = CanonicalCode.RESOURCE_EXHAUSTED.toStatus();
        FAILED_PRECONDITION = CanonicalCode.FAILED_PRECONDITION.toStatus();
        ABORTED = CanonicalCode.ABORTED.toStatus();
        OUT_OF_RANGE = CanonicalCode.OUT_OF_RANGE.toStatus();
        UNIMPLEMENTED = CanonicalCode.UNIMPLEMENTED.toStatus();
        INTERNAL = CanonicalCode.INTERNAL.toStatus();
        UNAVAILABLE = CanonicalCode.UNAVAILABLE.toStatus();
        DATA_LOSS = CanonicalCode.DATA_LOSS.toStatus();
    }
    
    public enum CanonicalCode
    {
        OK(0), 
        CANCELLED(1), 
        UNKNOWN(2), 
        INVALID_ARGUMENT(3), 
        DEADLINE_EXCEEDED(4), 
        NOT_FOUND(5), 
        ALREADY_EXISTS(6), 
        PERMISSION_DENIED(7), 
        RESOURCE_EXHAUSTED(8), 
        FAILED_PRECONDITION(9), 
        ABORTED(10), 
        OUT_OF_RANGE(11), 
        UNIMPLEMENTED(12), 
        INTERNAL(13), 
        UNAVAILABLE(14), 
        DATA_LOSS(15), 
        UNAUTHENTICATED(16);
        
        private final int value;
        
        private CanonicalCode(final int value) {
            this.value = value;
        }
        
        public int value() {
            return this.value;
        }
        
        public Status toStatus() {
            return Status.STATUS_LIST.get(this.value);
        }
    }
}
